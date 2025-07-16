function jpmDialogConfirm(params) {
    var wrapper = document.createElement('div');
    wrapper.classList.add("modal");
    wrapper.innerHTML = '<div class="modal-dialog jpm-dialog-confirm ' + params.addClass + '"><div class="modal-content"><div class="modal-header"><h5 class="modal-title">' + (params.title || messages["jpm.modal.confirm.title"]) + '</h5>'
            + '<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button></div><div class="modal-body"><p>' + (params.message || messages["jpm.modal.confirm.text"]) + '</p></div>'
            + '<div class="modal-footer"><button type="button" class="btn btn-secondary" data-bs-dismiss="modal">' + (params.cancelBtn || messages["jpm.modal.confirm.cancel"])
            + '</button><button type="button" class="btn btn-primary btn-confirm">' + (params.okBtn || messages["jpm.modal.confirm.submit"]) + '</button></div></div></div>';
    var myModal = new bootstrap.Modal(wrapper);
    $(wrapper).find(".btn-confirm").on("click", function () {
        if (params.callback) {
            if (params.callback()) {
                myModal.hide();
                myModal.dispose();
            }
        } else {
            myModal.hide();
            myModal.dispose();
        }
    });
    if (params.onShown) {
        wrapper.addEventListener('shown.bs.modal', function () {
            params.onShown();
        });
    }
    if (params.onHide) {
        wrapper.addEventListener('hidden.bs.modal', function () {
            params.onHide();
        });
    }
    myModal.show();
}

function jpmDialog(params) {
    var wrapper = document.createElement('div');
    wrapper.classList.add("modal");
    wrapper.setAttribute("tabindex", "-1");

    var html = '<div class="modal-dialog ' + (params.addClass || '') + '"><div class="modal-content"><div class="modal-header ' + (params.titleBackground || "bg-info") + '">'
            + '<h5 class="modal-title">' + (params.title || "") + '</h5>'
            + '<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button></div><div class="modal-body">' + (params.message || "") + '</div>'
            + '<div class="modal-footer">';

    if (!params.closeTimeout) {
        html += '<button type="button" class="btn btn-primary btn-confirm">' + (params.okBtn || messages["jpm.modal.confirm.submit"] || "Aceptar") + '</button>';
    }

    html += '</div></div></div>';
    wrapper.innerHTML = html;

    var myModal = new bootstrap.Modal(wrapper);
    // Limpieza segura al cerrarse
    wrapper.addEventListener('hidden.bs.modal', () => {
        myModal.dispose();
        wrapper.remove();
    });
    if (!params.closeTimeout) {
        $(wrapper).find(".btn-confirm").on("click", function () {
            if (params.callback) {
                params.callback();
            }
            myModal.hide();
        });
    } else {
        setTimeout(function () {
            if (params.callback) {
                params.callback();
            }
            myModal.hide();
        }, params.closeTimeout);
    }
    // Ejecutar callback cuando se muestra el modal
    if (typeof params.onshown === 'function') {
        $(wrapper).on('shown.bs.modal', function () {
            params.onshown(myModal);
        });
    }
    myModal.show();
}
