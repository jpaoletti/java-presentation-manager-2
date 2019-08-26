# Java Presentation Manager 2

This is the new version of the generic CRUD framework Java Presentation Manager.
We reduced the unused functionalities and updated frameworks.

THIS IS A WORK IN PROGRESS PROJECT.

## Frameworks and libraries

* Spring 4.2
* Spring 4.2 MVC
* Spring 4.0 Security
* Hibernate 4 + JPA + DAO
* Boostrap 3
* jQuery 2
* Less
* <a href='https://select2.org'>Select2</a>
* <a href='http://blueimp.github.io/jQuery-File-Upload/'>jQuery File Upload</a>
* <a href='https://github.com/carhartl/jquery-cookie'>jQuery Cookie</a>
* <a href='http://autonumeric.org/'>AutoNumeric</a>
* <a href='https://github.com/igorescobar/jQuery-Mask-Plugin'>jQuery Mask Plugin v1.7.7</a>
* <a href='https://github.com/NicolasCARPi/jquery_jeditable'>jEditable </a>
* Node js: 

## Functionalities

* Localization (spring, view side)
* Data access (DAO, core side)
* Session management (spring ?)
* Security & Authorization (spring security 3)


## Notes ##


### Operation over entities with multiple contexts

If an entity has multiple contexts, operations should be mapped like this:

@RequestMapping(value = {"/jpm/{entity:someEntity}/{instanceId}/{operationId:someOp}", "/jpm/{entity:someEntity!.*}/{instanceId}/{operationId:someOp}"})
...

This will ensure that all the context has the operation available.

