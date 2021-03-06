function jpmDialogConfirm(params) {
    var wrapper = document.createElement('div');
    wrapper.classList.add("modal");
    wrapper.innerHTML = '<div class="modal-dialog"><div class="modal-content"><div class="modal-header"><h5 class="modal-title">' + (params.title || messages["jpm.modal.confirm.title"]) + '</h5>'
            + '<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button></div><div class="modal-body"><p>' + (params.message || messages["jpm.modal.confirm.text"]) + '</p></div>'
            + '<div class="modal-footer"><button type="button" class="btn btn-secondary" data-bs-dismiss="modal">' + (params.cancelBtn || messages["jpm.modal.confirm.cancel"])
            + '</button><button type="button" class="btn btn-primary btn-confirm">' + (params.okBtn || messages["jpm.modal.confirm.submit"]) + '</button></div></div></div>';
    var myModal = new bootstrap.Modal(wrapper);
    $(wrapper).find(".btn-confirm").on("click", function () {
        myModal.hide();
        myModal.dispose();
        params.callback();
    });
    myModal.show();
}

function jpmDialog(params) {
    var wrapper = document.createElement('div');
    wrapper.classList.add("modal");
    wrapper.innerHTML = '<div class="modal-dialog "><div class="modal-content"><div class="modal-header ' + (params.titleBackground || "bg-info") + '"><h5 class="modal-title">' + (params.title || "") + '</h5>'
            + '<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button></div><div class="modal-body"><p>' + (params.message || "") + '</p></div>'
            + '<div class="modal-footer">';
    if (!params.closeTimeout) {
        wrapper.innerHTML += '<button type="button" class="btn btn-primary btn-confirm">' + (params.okBtn || messages["jpm.modal.confirm.submit"]) + '</button>';
    }
    wrapper.innerHTML += '</div></div></div>';
    var myModal = new bootstrap.Modal(wrapper);
    if (!params.closeTimeout) {
        $(wrapper).find(".btn-confirm").on("click", function () {
            myModal.hide();
            myModal.dispose();
            if (params.callback) {
                params.callback();
            }
        });
    } else {
        setTimeout(function () {
            myModal.hide();
            myModal.dispose();
            if (params.callback) {
                params.callback();
            }
        }, params.closeTimeout);
    }
    myModal.show();
}