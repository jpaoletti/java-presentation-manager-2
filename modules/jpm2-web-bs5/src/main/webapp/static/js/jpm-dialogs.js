function jpmDialogConfirm(params) {
    params = params || {};
    var wrapper = document.createElement('div');

    // Clases y atributos necesarios para que Escape funcione
    wrapper.className = "modal fade";
    wrapper.setAttribute("tabindex", "-1");
    wrapper.setAttribute("aria-hidden", "true");
    wrapper.setAttribute("role", "dialog");

    wrapper.innerHTML =
        '<div class="modal-dialog jpm-dialog-confirm ' + (params.addClass || '') + '">' +
            '<div class="modal-content">' +
                '<div class="modal-header">' +
                    '<h5 class="modal-title">' + (params.title || messages["jpm.modal.confirm.title"]) + '</h5>' +
                    '<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>' +
                '</div>' +
                '<div class="modal-body"><p>' + (params.message || messages["jpm.modal.confirm.text"]) + '</p></div>' +
                '<div class="modal-footer">' +
                    '<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">' +
                        (params.cancelBtn || messages["jpm.modal.confirm.cancel"]) +
                    '</button>' +
                    '<button type="button" class="btn btn-primary btn-confirm">' +
                        (params.okBtn || messages["jpm.modal.confirm.submit"]) +
                    '</button>' +
                '</div>' +
            '</div>' +
        '</div>';

    // IMPORTANTE: agregar al DOM antes de inicializar el modal
    document.body.appendChild(wrapper);

    var myModal = new bootstrap.Modal(wrapper, {
        keyboard: true,  // habilita Escape
        backdrop: true,  // clic fuera también cierra (si querés bloquear: 'static')
        focus: true      // asegura focus adentro
    });

    // Confirmar
    $(wrapper).find(".btn-confirm").on("click", function () {
        // Quitar el foco del botón antes de cerrar para evitar warning de aria-hidden
        this.blur();
        if (params.callback) {
            if (params.callback()) {
                myModal.hide();
            }
        } else {
            myModal.hide();
        }
    });

    // Eventos
    if (params.onShown) {
        wrapper.addEventListener('shown.bs.modal', function () {
            params.onShown();
        });
    }
    // Enfocar el botón confirmar al abrir (opcional, ayuda a capturar Escape)
    wrapper.addEventListener('shown.bs.modal', function () {
        var btn = wrapper.querySelector('.btn-confirm');
        if (btn) btn.focus();
    });

    // Quitar foco antes de ocultar para evitar warning de aria-hidden
    wrapper.addEventListener('hide.bs.modal', function () {
        if (document.activeElement && wrapper.contains(document.activeElement)) {
            document.activeElement.blur();
        }
    });

    wrapper.addEventListener('hidden.bs.modal', function () {
        if (params.onHide) params.onHide();
        myModal.dispose();     // limpia listeners internos
        wrapper.remove();      // saca el DOM node
    });

    myModal.show();
}

function jpmDialog(params) {
    params = params || {};

    var wrapper = document.createElement('div');
    wrapper.className = "modal fade";
    wrapper.setAttribute("tabindex", "-1");
    wrapper.setAttribute("aria-hidden", "true");
    wrapper.setAttribute("role", "dialog");

    var html =
        '<div class="modal-dialog ' + (params.addClass || '') + '">' +
          '<div class="modal-content">' +
            '<div class="modal-header ' + (params.titleBackground || "bg-info") + '">' +
              '<h5 class="modal-title">' + (params.title || "") + '</h5>' +
              '<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>' +
            '</div>' +
            '<div class="modal-body">' + (params.message || "") + '</div>' +
            '<div class="modal-footer">';

    if (!params.closeTimeout) {
        html += '<button type="button" class="btn btn-primary btn-confirm">' +
                (params.okBtn || (window.messages && messages["jpm.modal.confirm.submit"]) || "Aceptar") +
                '</button>';
    }

    html +=     '</div>' +
          '</div>' +
        '</div>';

    wrapper.innerHTML = html;

    // *** Agregar al DOM antes de inicializar el Modal ***
    document.body.appendChild(wrapper);

    var myModal = new bootstrap.Modal(wrapper, {
        keyboard: true,                     // permite cerrar con Escape
        backdrop: params.backdrop ?? true,  // true | 'static'
        focus: true                         // fuerza foco dentro del modal
    });

    // Limpieza segura al cerrarse
    let timeoutId = null;

    // Quitar foco antes de ocultar para evitar warning de aria-hidden
    wrapper.addEventListener('hide.bs.modal', function () {
        if (document.activeElement && wrapper.contains(document.activeElement)) {
            document.activeElement.blur();
        }
    });

    wrapper.addEventListener('hidden.bs.modal', function () {
        if (timeoutId) clearTimeout(timeoutId);
        if (typeof params.onhide === 'function') params.onhide(myModal);
        myModal.dispose();
        wrapper.remove();
    });

    // Botón confirmar (si existe)
    if (!params.closeTimeout) {
        $(wrapper).find(".btn-confirm").on("click", function () {
            // Quitar el foco del botón antes de cerrar para evitar warning de aria-hidden
            this.blur();
            if (typeof params.callback === 'function') {
                params.callback();
            }
            myModal.hide();
        });
    } else {
        // Autocierre con timeout
        timeoutId = setTimeout(function () {
            if (typeof params.callback === 'function') {
                params.callback();
            }
            myModal.hide();
        }, params.closeTimeout);
    }

    // Evento al mostrarse
    if (typeof params.onshown === 'function') {
        $(wrapper).on('shown.bs.modal', function () {
            params.onshown(myModal);
        });
    }

    // Enfocar algo al abrir (prioriza el botón confirmar)
    wrapper.addEventListener('shown.bs.modal', function () {
        var btn = wrapper.querySelector('.btn-confirm');
        if (btn) {
            btn.focus();
        } else {
            // fallback: primer elemento focuseable del modal
            var firstFocusable = wrapper.querySelector(
                'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
            );
            if (firstFocusable) firstFocusable.focus();
        }
    });

    myModal.show();
}

/**
 * Muestra un modal de alerta (warning)
 * @param {string|object} message - Mensaje a mostrar o objeto con params
 * @param {function} callback - (opcional) Función a ejecutar al cerrar
 */
function jpmAlert(message, callback) {
    var params = {};

    if (typeof message === 'string') {
        params.message = message;
        params.callback = callback;
    } else if (typeof message === 'object') {
        params = message;
    }

    params.title = params.title || (window.messages && messages["jpm.modal.alert.title"]) || "Warning";
    params.titleBackground = params.titleBackground || "bg-warning text-dark";
    params.addClass = params.addClass || "";

    jpmDialog(params);
}

/**
 * Muestra un modal de éxito
 * @param {string|object} message - Mensaje a mostrar o objeto con params
 * @param {function} callback - (opcional) Función a ejecutar al cerrar
 */
function jpmSuccess(message, callback) {
    var params = {};

    if (typeof message === 'string') {
        params.message = message;
        params.callback = callback;
    } else if (typeof message === 'object') {
        params = message;
    }

    params.title = params.title || (window.messages && messages["jpm.modal.success.title"]) || "Success";
    params.titleBackground = params.titleBackground || "bg-success text-white";
    params.addClass = params.addClass || "";

    jpmDialog(params);
}
