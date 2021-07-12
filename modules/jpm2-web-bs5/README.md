#For NPM installs

cd src/main/webapp/static
npm install --save bootstrap5
npm install --save bootstrap5-dialog
npm install --save @fortawesome/fontawesome-free

#BS4 Migration

## DROPDOWNS

Replace all data-toggle="dropdown" with data-bs-toggle="dropdown"

Replace div with class="dropdown-menu" for ul with same class

Surround any a.dropdown-item with a <li></li>

## FORMS

Remove al <div class="input-group-prepend"></div> and <div class="input-group-append"></div> surrounds

## Dialogs

bootstrap4-dialog got replaced with custom dialogs.

BootstrapDialog.confirm -> jpmDialogConfirm

BootstrapDialog.alert -> jpmDialog

## MISC

Replace all btn-block with w-100 

Replace all badge-xxcolor with bg-xxcolor

Replace float-right with float-end

Replace float-left with float-start


