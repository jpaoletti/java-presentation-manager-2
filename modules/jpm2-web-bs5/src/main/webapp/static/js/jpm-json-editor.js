/* global jQuery */
/*
 * jpm-json-editor.js
 *
 * Lightweight, dependency-free JSON code editor used by
 * WebEditJSonNiceConverter. Turns a plain <textarea> into a "nice" editor with:
 *   - JSON syntax highlighting (via a <pre> overlay behind a transparent textarea)
 *   - automatic tabulation (Tab inserts spaces, Enter keeps the current indent,
 *     Tab/Shift+Tab indent/outdent multi-line selections)
 *   - a "Format" action that pretty-prints and reports invalid JSON
 *
 * Markup expected (rendered by edit-json-nice-converter.jsp):
 *   <div class="jpm-json-editor">
 *     <div class="jpm-json-toolbar">... [.jpm-json-format] ... [.jpm-json-status]</div>
 *     <div class="jpm-json-body">
 *       <pre class="jpm-json-highlight" aria-hidden="true"><code></code></pre>
 *       <textarea class="jpm-json-input" ...></textarea>
 *     </div>
 *   </div>
 */
(function (window, document) {
    "use strict";

    var INDENT = "  "; // two spaces per level

    function escapeHtml(s) {
        return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
    }

    // Classic JSON tokenizer → HTML spans.
    function highlight(json) {
        var html = escapeHtml(json);
        html = html.replace(
            /("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g,
            function (match) {
                var cls = "jt-num";
                if (/^"/.test(match)) {
                    cls = /:$/.test(match) ? "jt-key" : "jt-str";
                } else if (/true|false/.test(match)) {
                    cls = "jt-bool";
                } else if (/null/.test(match)) {
                    cls = "jt-null";
                }
                return "<span class='" + cls + "'>" + match + "</span>";
            }
        );
        return html;
    }

    function render(editor) {
        var value = editor.textarea.value;
        // Trailing newline so the overlay keeps the last (possibly empty) line.
        editor.code.innerHTML = highlight(value) + "\n";
        syncScroll(editor);
        validate(editor, false);
    }

    function syncScroll(editor) {
        editor.pre.scrollTop = editor.textarea.scrollTop;
        editor.pre.scrollLeft = editor.textarea.scrollLeft;
    }

    function setStatus(editor, text, kind) {
        if (!editor.status) {
            return;
        }
        editor.status.textContent = text || "";
        editor.status.className = "jpm-json-status" + (kind ? " jpm-json-status-" + kind : "");
    }

    // Returns the parsed value or null; updates the status line.
    function validate(editor, verbose) {
        var raw = editor.textarea.value.trim();
        if (raw === "") {
            setStatus(editor, "", "");
            editor.root.classList.remove("is-invalid-json");
            return null;
        }
        try {
            var parsed = JSON.parse(raw);
            setStatus(editor, verbose ? (editor.okText || "OK") : "", "ok");
            editor.root.classList.remove("is-invalid-json");
            return parsed;
        } catch (e) {
            setStatus(editor, (editor.errText || "Invalid JSON") + ": " + e.message, "error");
            editor.root.classList.add("is-invalid-json");
            return null;
        }
    }

    function format(editor) {
        var parsed = validate(editor, true);
        if (parsed === null) {
            return;
        }
        editor.textarea.value = JSON.stringify(parsed, null, 2);
        render(editor);
        setStatus(editor, editor.okText || "OK", "ok");
    }

    function replaceSelection(ta, text, selStart, selEnd) {
        var value = ta.value;
        ta.value = value.slice(0, selStart) + text + value.slice(selEnd);
        var pos = selStart + text.length;
        ta.selectionStart = ta.selectionEnd = pos;
    }

    function currentLineIndent(value, caret) {
        var lineStart = value.lastIndexOf("\n", caret - 1) + 1;
        var m = value.slice(lineStart).match(/^[ \t]*/);
        return m ? m[0] : "";
    }

    function onKeyDown(editor, e) {
        var ta = editor.textarea;
        var start = ta.selectionStart;
        var end = ta.selectionEnd;
        var value = ta.value;

        if (e.key === "Tab") {
            e.preventDefault();
            var hasSelection = start !== end;
            var multiline = hasSelection && value.slice(start, end).indexOf("\n") !== -1;
            if (multiline) {
                // Indent / outdent every line in the selection.
                var lineStart = value.lastIndexOf("\n", start - 1) + 1;
                var block = value.slice(lineStart, end);
                var replaced;
                if (e.shiftKey) {
                    replaced = block.replace(/^( {1,2}|\t)/gm, "");
                } else {
                    replaced = block.replace(/^/gm, INDENT);
                }
                ta.value = value.slice(0, lineStart) + replaced + value.slice(end);
                ta.selectionStart = lineStart;
                ta.selectionEnd = lineStart + replaced.length;
            } else if (e.shiftKey) {
                // Outdent current line.
                var ls = value.lastIndexOf("\n", start - 1) + 1;
                if (value.slice(ls, ls + INDENT.length) === INDENT) {
                    ta.value = value.slice(0, ls) + value.slice(ls + INDENT.length);
                    ta.selectionStart = ta.selectionEnd = Math.max(ls, start - INDENT.length);
                }
            } else {
                replaceSelection(ta, INDENT, start, end);
            }
            render(editor);
            return;
        }

        if (e.key === "Enter") {
            // Keep the current line's indentation; add one level after { or [.
            e.preventDefault();
            var indent = currentLineIndent(value, start);
            var prevChar = value.slice(0, start).replace(/[ \t]*$/, "").slice(-1);
            var extra = (prevChar === "{" || prevChar === "[") ? INDENT : "";
            var nextChar = value.slice(end).charAt(0);
            var insert = "\n" + indent + extra;
            if (extra && (nextChar === "}" || nextChar === "]")) {
                // Opening/closing pair: put the closing brace on its own line.
                insert = "\n" + indent + extra + "\n" + indent;
                replaceSelection(ta, insert, start, end);
                ta.selectionStart = ta.selectionEnd = start + 1 + indent.length + extra.length;
            } else {
                replaceSelection(ta, insert, start, end);
            }
            render(editor);
            return;
        }
    }

    function init(root) {
        if (!root || root.__jpmJsonReady) {
            return;
        }
        var textarea = root.querySelector(".jpm-json-input");
        var pre = root.querySelector(".jpm-json-highlight");
        var code = pre ? pre.querySelector("code") : null;
        if (!textarea || !pre || !code) {
            return;
        }
        var editor = {
            root: root,
            textarea: textarea,
            pre: pre,
            code: code,
            status: root.querySelector(".jpm-json-status"),
            okText: root.getAttribute("data-ok-text"),
            errText: root.getAttribute("data-error-text")
        };
        root.__jpmJsonReady = true;

        textarea.addEventListener("input", function () { render(editor); });
        textarea.addEventListener("scroll", function () { syncScroll(editor); });
        textarea.addEventListener("keydown", function (e) { onKeyDown(editor, e); });

        var formatBtn = root.querySelector(".jpm-json-format");
        if (formatBtn) {
            formatBtn.addEventListener("click", function (e) {
                e.preventDefault();
                format(editor);
            });
        }
        render(editor);
    }

    function initAll(context) {
        var scope = context || document;
        var nodes = scope.querySelectorAll(".jpm-json-editor");
        for (var i = 0; i < nodes.length; i++) {
            init(nodes[i]);
        }
    }

    window.jpmJsonEditor = { init: init, initAll: initAll, format: format };
})(window, document);
