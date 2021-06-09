# jQuery 3.x UPGRADE GUIDE for Java Presentation Manager

## UPGRADE COMPONENTS

[OK v1.5.3]     http://github.com/vitalets/x-editable #Ver Novedades, cosas muy lindas

[OK v4.2.2]     https://github.com/malsup/form

[NO v1.3.0]     https://github.com/sockjs/sockjs-client      FAILS ON SOME ENVIROMENTS

[OK v3.3.4]     https://github.com/nakupanda/bootstrap3-dialog

[OK v9.29.0]    https://github.com/blueimp/jQuery-File-Upload

[OK v1.14.15]   https://github.com/igorescobar/jQuery-Mask-Plugin

https://github.com/eternicode/bootstrap-datepicker

https://www.jstree.com/

https://select2.org/

https://www.flotcharts.org/


## REPLACES 

### Event triggers shortcuts
.focus()                ->      .trigger('focus')
.submit()               ->      .trigger('submit')

### Event binding shortcuts
.click(function         ->      .on('click', function

### Others
jQuery.parseJSON -> JSON.parse

## MIGRATE REMAINING

<script type="text/javascript" src="${cp}static/js/autoNumeric.js?v=${jpm.appversion}"></script>
FOR
<script type="text/javascript" src="${cp}static/js/autoNumeric-4.x.x.js?v=${jpm.appversion}"></script>

Notable changes. 
