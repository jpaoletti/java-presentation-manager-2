/*
 * JPM2 Password Converter - native Bootstrap 5 implementation.
 * Replaces the legacy pwdwidget.js. Single input whose type toggles
 * between password/text, plus generate button and strength meter.
 */
(function (root) {
    'use strict';

    function scorePassword(pwd) {
        var s = 0;
        if (pwd.length > 6) s++;
        if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) s++;
        if (/\d/.test(pwd)) s++;
        if (/[^a-zA-Z\d]/.test(pwd)) s++;
        if (pwd.length > 12) s++;
        return s;
    }

    function shuffle(s) {
        var a = s.split('');
        for (var i = a.length - 1; i > 0; i--) {
            var j = Math.floor(Math.random() * (i + 1));
            var t = a[i]; a[i] = a[j]; a[j] = t;
        }
        return a.join('');
    }

    function randChar(from, len) {
        return String.fromCharCode(from.charCodeAt(0) + Math.floor(Math.random() * len));
    }

    function generatePwd() {
        var symbols = "!@#$%&*(){}?=-|][";
        var pwd = '';
        for (var i = 0; i < 3; i++) pwd += randChar('a', 26);
        for (var i = 0; i < 3; i++) pwd += randChar('A', 26);
        for (var i = 0; i < 3; i++) pwd += randChar('0', 10);
        for (var i = 0; i < 4; i++) pwd += symbols.charAt(Math.floor(Math.random() * symbols.length));
        return shuffle(shuffle(shuffle(pwd)));
    }

    function init(opts) {
        var input = document.getElementById(opts.inputId);
        var toggle = document.getElementById(opts.toggleId);
        var generate = document.getElementById(opts.generateId);
        var bar = document.getElementById(opts.strengthBarId);
        var label = document.getElementById(opts.strengthTextId);
        if (!input || input.dataset.pwdConverterReady === '1') {
            return;
        }
        input.dataset.pwdConverterReady = '1';

        var labels = opts.labels || {};

        function updateStrength() {
            if (!bar) return;
            var pwd = input.value;
            if (!pwd) {
                bar.style.width = '0%';
                bar.className = 'progress-bar';
                if (label) label.textContent = '';
                return;
            }
            var score = scorePassword(pwd);
            bar.style.width = Math.min(100, (score + 1) * 20) + '%';
            bar.className = 'progress-bar';
            if (score < 3) {
                bar.classList.add('bg-danger');
                if (label) label.textContent = labels.weak || '';
            } else if (score < 4) {
                bar.classList.add('bg-warning');
                if (label) label.textContent = labels.medium || '';
            } else {
                bar.classList.add('bg-success');
                if (label) label.textContent = labels.good || '';
            }
        }

        function togglePassword(e) {
            if (e) e.preventDefault();
            if (!toggle) return;
            var span = toggle.querySelector('.pwd-toggle-text');
            var icon = toggle.querySelector('i');
            if (input.type === 'password') {
                input.type = 'text';
                if (span) span.textContent = toggle.dataset.maskText;
                if (icon) { icon.classList.remove('fa-eye'); icon.classList.add('fa-eye-slash'); }
            } else {
                input.type = 'password';
                if (span) span.textContent = toggle.dataset.showText;
                if (icon) { icon.classList.remove('fa-eye-slash'); icon.classList.add('fa-eye'); }
            }
            input.focus();
        }

        function doGenerate(e) {
            if (e) e.preventDefault();
            input.value = generatePwd();
            if (input.type === 'password') {
                togglePassword(e);
            }
            updateStrength();
        }

        if (toggle) toggle.addEventListener('click', togglePassword);
        if (generate) generate.addEventListener('click', doGenerate);
        input.addEventListener('input', updateStrength);

        if (opts.disableFormAutocomplete !== false) {
            var form = input.closest('form');
            if (form) form.setAttribute('autocomplete', 'off');
        }
    }

    root.JpmPwdConverter = { init: init, generate: generatePwd };
})(window);
