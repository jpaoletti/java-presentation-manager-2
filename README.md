# Java Presentation Manager 2

CRUD framework for traditional Java web applications built on Spring MVC, Hibernate, and JSP.
The active line in this repository today is `jpm2-web-bs5`: Bootstrap 5 on the frontend, Spring 5 on the backend, and primarily XML-based configuration.

This repository contains the framework and a sample WAR application.

## Current status

- Java 17
- Multi-module Maven project
- Spring Framework 5.3.39
- Spring Security 5.8.12
- Hibernate 5.6.15.Final
- JSP + JSTL + standard taglibs
- Bootstrap 5.3.8
- jQuery 3.5.x
- MySQL Connector/J 8.0.30
- Log4j 2.24.1
- Jackson 2.20.x
- WebSocket/STOMP for asynchronous operations

This is not a Spring Boot or Thymeleaf stack. Real integration is still based on a classic WAR layout with `web.xml`, `applicationContext.xml`, `spring-security.xml`, `spring-hibernate.xml`, `spring-jpm.xml`, and `jpm-servlet-custom.xml`.

## Modules

- `modules/jpm2-core`: base model, DAO layer, security, validators, converters, operations, and core framework services.
- `modules/jpm2-web-core`: Spring MVC controllers, web rendering, web converters, filters, and shared infrastructure.
- `modules/jpm2-web-bs3`: legacy Bootstrap 3 variant.
- `modules/jpm2-web-bs5`: active web variant based on Bootstrap 5.
- `modules/jpm2-web-bs5-test`: minimal sample application for the `bs5` line.

## What `jpm2-web-bs5` provides

The `bs5` variant packages the reusable web layer of the framework:

- base layout and framework JSPs
- Spring Security integration
- generic CRUD controllers
- Hibernate/Open Session in View filters
- static assets for the bs5 theme
- support for synchronous and asynchronous operations
- support for declarative XML entity configuration

In a real application, you typically do not hand-code a full CRUD for each entity. Instead, you declare the entity in XML, define its DAO/model, and the framework resolves listing, add, edit, show, audit, and custom operations.

## Actual frontend stack in the bs5 version

The `package.json` under `modules/jpm2-web-bs5/src/main/webapp/static` shows the active frontend dependencies:

- `bootstrap` `5.3.8`
- `@fortawesome/fontawesome-free` `6.1.1`
- `jquery` `3.5.x`
- `jquery-blockui`
- `jquery.cookie`
- `jstree`
- `moment`
- `tempusdominus-bootstrap-4`
- `tempusdominus-core`
- `trumbowyg`

Notes:

- The current frontend build is minimal: Maven installs Node/NPM and runs `npm install` + `npm run build`, but the current `build` script is a no-op.
- The UI still uses server-side JSP rendering; there is no modern bundling pipeline or SPA architecture.
- There is still a mix of Bootstrap 5 with some inherited auxiliary libraries.

## How it is actually used in a bs5 project

The base reference inside this repository is `modules/jpm2-web-bs5-test`.

The integration pattern is the following:

### 1. Create your own WAR project

A consuming application is usually another Maven `war` project that depends on:

- `jpm2-web-bs5` as `war`
- `jpm2-web-bs5` with classifier `classes`
- `jpm2-core`

```xml
<dependency>
    <groupId>com.github.jpaoletti</groupId>
    <artifactId>jpm2-web-bs5</artifactId>
    <version>${jpm.version}</version>
    <type>war</type>
</dependency>
<dependency>
    <groupId>com.github.jpaoletti</groupId>
    <artifactId>jpm2-web-bs5</artifactId>
    <version>${jpm.version}</version>
    <type>jar</type>
    <classifier>classes</classifier>
</dependency>
<dependency>
    <groupId>com.github.jpaoletti</groupId>
    <artifactId>jpm2-core</artifactId>
    <version>${jpm.version}</version>
</dependency>
```

This setup allows you to reuse both the base WAR and the framework classes.

### 2. Define Maven profiles and filtered properties

Real-world usage relies on Maven profiles such as `Development` and `Production`/`Prod...` to inject:

- `connection.url`
- `connection.username`
- `connection.password`
- `jpm.cssMode`

- `src/main/filtered/database.properties` resolves the datasource
- `src/main/filtered/extra.properties` exposes `jpm.version` and `jpm.cssMode`

Example:

```properties
jdbc.driverClassName=com.mysql.cj.jdbc.Driver
jdbc.url=${connection.url}
jdbc.username=${connection.username}
jdbc.password=${connection.password}
```

### 3. Keep the standard `WEB-INF` structure

A typical bs5 application ends up with these files:

- `WEB-INF/web.xml`
- `WEB-INF/applicationContext.xml`
- `WEB-INF/spring-datasource.xml`
- `WEB-INF/spring-security.xml`
- `WEB-INF/spring-hibernate.xml`
- `WEB-INF/spring-jpm.xml`
- `WEB-INF/jpm-servlet-custom.xml`

The actual bootstrap starts from `applicationContext.xml`, which imports datasource, security, hibernate, and JPM configuration.

### 4. Configure Spring MVC and custom components

Custom controllers are added in `jpm-servlet-custom.xml` through `component-scan`.

```xml
<context:component-scan base-package="ar.com.myapp.ui">
    <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller" />
</context:component-scan>
```

It is also common to define the `multipartResolver` there.

### 5. Configure the `PresentationManager`

The core of the application is the `jpm` bean in `spring-jpm.xml`.
This is where you define:

- version
- title/subtitle
- contact
- `cssMode`
- audit service
- `JPMService` implementation
- imports for entities and helper beans

Simplified example:

```xml
<bean id="jpm" class="jpaoletti.jpm2.core.PresentationManager">
    <property name="appversion" value="${jpm.version}" />
    <property name="title" value="My application" />
    <property name="subtitle" value="built on JPM2" />
    <property name="contact" value="admin@myapp.com" />
    <property name="cssMode" value="${jpm.cssMode}" />
    <property name="auditService">
        <bean class="jpaoletti.jpm2.core.service.AuditServiceDatabase" />
    </property>
    <property name="service">
        <bean class="jpaoletti.jpm2.core.service.JPMServiceImpl" />
    </property>
</bean>
```

After that, applications usually import:

- `jpm-default-beans.xml`
- `field-configs.xml` if the app defines custom field configurations
- `extra-beans.xml` if the app registers custom beans
- `entities/...xml`

### 6. Declare entities in XML

Real usage of the framework is highly declarative. Each entity is defined as an `Entity` bean in XML.

Each entity file usually contains:

- DAO
- `Entity` bean
- enabled operations
- fields
- converters/configs
- show/edit panels

Conceptually:

```xml
<bean id="dao-customer" class="ar.com.myapp.dao.DefaultDAO">
    <property name="transformer" ref="transformer-strToLong" />
    <property name="className" value="ar.com.myapp.model.Customer" />
</bean>

<bean id="customer" class="jpaoletti.jpm2.core.model.Entity">
    <property name="clazz" value="ar.com.myapp.model.Customer" />
    <property name="dao" ref="dao-customer" />
    <property name="operations">
        <list>
            <ref bean="default-list-operation" />
            <ref bean="default-add-operation" />
            <ref bean="default-show-operation" />
            <ref bean="default-edit-operation" />
            <ref bean="default-delete-operation" />
        </list>
    </property>
</bean>
```

### 7. Add custom code only where it adds value

In a real application, custom code usually appears mostly in:

- custom JPA models
- specialized DAOs
- business services
- controllers for special operations
- custom JSPs for non-standard screens

Typical examples:

- a controller that customizes the home page or dashboard
- controllers for special operations such as printing, reporting, or integrations
- business services that are outside the generic CRUD workflow

## Real URLs and operation patterns

The framework is centered around `/jpm`.

Actual route patterns:

- listing: `/jpm/{entity}/list`
- add: `/jpm/{entity}/add`
- operation execution: `/jpm/{entity}/{id}/{operation}.exec`
- owner-dependent lists: `/jpm/{owner}/{ownerId}/{entity}/list`

Examples:

- `/jpm/customer/list`
- `/jpm/invoice/15/show.exec`
- `/jpm/company/2/show.exec`

You can also declare custom operations in your own Spring controllers:

```java
@RequestMapping(value = {"/jpm/{entity}/{instanceId}/{operationId:printInvoice}"})
```

If an entity has multiple contexts, the mapping should cover both the plain and contextual entity form. The legacy pattern that still applies is:

```java
@RequestMapping(value = {
    "/jpm/{entity:someEntity}/{instanceId}/{operationId:someOp}",
    "/jpm/{entity:someEntity!.*}/{instanceId}/{operationId:someOp}"
})
```

## Custom operations

A custom operation can be implemented in two ways:

- declaring an `Operation` with an `executor`, so the framework handles the `.exec` lifecycle
- implementing a custom Spring controller if the flow does not fit the generic mechanism

Inside JPM2, the most idiomatic option is to start with an `executor`.

### When to use an `executor`

Using an `executor` is usually the right choice when the operation:

- needs its own form or intermediate screen
- executes business logic on one or more instances
- should follow the standard permissions, breadcrumbs, messages, and navigation flow
- may end by redirecting to `show`, `list`, or another operation

### Minimal XML definition

A custom operation is declared inside the entity `operations` list.

```xml
<bean class="jpaoletti.jpm2.core.model.Operation">
    <property name="id" value="approve" />
    <property name="scope" value="ITEM" />
    <property name="display" value="show list" />
    <property name="icon" value="fas fa-check" />
    <property name="confirm" value="true" />
    <property name="executor" ref="operationExecutorApprove" />
</bean>
```

And the executor bean:

```xml
<bean id="operationExecutorApprove" class="ar.com.myapp.core.executor.OperationExecutorApprove" />
```

If the operation has an `executor`, its `pathId` automatically becomes `id.exec`.
For example, the `approve` operation becomes:

- item: `/jpm/order/15/approve.exec`
- general: `/jpm/order/approve.exec`
- selected/grouped: `/jpm/order/10,11,12/approve.exec`

### Scopes

The real available scopes are:

- `GENERAL`: operation on the entity itself, with no specific instance. Usually shown in `list`.
- `ITEM`: operation on a single instance. Usually shown in `show`, `list`, or `edit`.
- `SELECTED`: operation on multiple selected rows, executed individually per instance.
- `GROUPED`: operation on multiple selected rows, handled as a group.

Practical rule of thumb:

- use `ITEM` for actions such as approve, clone, print, recalculate
- use `GENERAL` for wizards, imports, or global processes
- use `SELECTED` or `GROUPED` for bulk actions

### `display`

The `display` property tells the framework where the button or link should appear.

Typical examples:

- `display="list"`: only in lists
- `display="show list"`: in show and list views
- `display="all"`: in any compatible view
- `display="all !add"`: everywhere except `add`
- `display="none"`: do not render it, but keep it callable

### Prepare + Commit lifecycle of a `.exec`

The standard executor lifecycle is:

1. `GET /.../{operationId}.exec`
2. the framework calls `prepare(...)`
3. if `immediateExecute()` returns `true`, it executes without showing a view
4. otherwise it renders the operation JSP
5. the form performs `POST /.../{operationId}.exec`
6. the framework calls `preExecute(...)`
7. then it calls `execute(...)`
8. it returns redirects, messages, or validation errors

By default, the view name is:

```text
op-{operationId}
```

That means operation `approve` uses JSP `op-approve.jsp`, unless the executor overrides `getViewName(...)`.

### Typical executor structure

The simplest base class is `OperationExecutorSimple`.

```java
public class OperationExecutorApprove extends OperationExecutorSimple {

    @Override
    public Map<String, Object> prepare(Entity owner, String ownerId, List<EntityInstance> instances) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("reasons", List.of("OK", "Observed"));
        return model;
    }

    @Override
    public Map preExecute(JPMContext ctx, List<EntityInstance> instances, Map parameters) {
        return parameters;
    }

    @Override
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        String reason = getSimpleParameterValue(parameters, "reason");
        // business logic
        return null;
    }

    @Override
    public String getDefaultNextOperationId() {
        return "show";
    }
}
```

Important behavior:

- if `prepare(...)` returns `null`, there is no intermediate screen
- if `immediateExecute()` returns `true`, it does not even try to render a JSP
- if `execute(...)` returns `null`, the framework redirects according to `getDefaultNextOperationId()`
- if `execute(...)` returns a URL or `redirect:...`, that destination is used

### Operation JSP

If the operation needs its own UI, create a JSP under:

- `src/main/webapp/WEB-INF/jsp/op-{operationId}.jsp`

Example:

```jsp
<%@include file="inc/default-taglibs.jsp" %>
<spring:message var="operationName" code="${operation.title}" arguments="${entityName}" />
<%@include file="inc/default-itemop-header.jsp" %>

<form method="post" action="${cp}jpm/${contextualEntity}/${instance.id}/approve.exec">
    <div class="mb-3">
        <label class="form-label">
            <spring:message code="order.approve.reason" text="Reason" />
        </label>
        <select name="reason" class="form-select">
            <c:forEach items="${reasons}" var="r">
                <option value="${r}">${r}</option>
            </c:forEach>
        </select>
    </div>
    <button type="submit" class="btn btn-primary">
        <spring:message code="jpm.operation.approve" text="Approve" />
    </button>
</form>
```

For `GENERAL` operations, the form action is usually:

- `${cp}jpm/${contextualEntity}/approve.exec`

For `ITEM`:

- `${cp}jpm/${contextualEntity}/${instance.id}/approve.exec`

For `SELECTED` or `GROUPED`, the framework builds the URL using the selected ids.

### Operation i18n

The standard operation title comes from:

```text
jpm.operation.{operationId}
```

So for `approve`:

```properties
jpm.operation.approve=Approve
```

That value is used in headers, breadcrumbs, and buttons.

It is also common to add:

```properties
jpm.approve.success=Operation completed successfully
order.approve.reason=Reason
```

If you want to reuse the standard success flow, the generic executor controller emits by default:

```text
jpm.{operationId}.success
```

For `approve`:

```properties
jpm.approve.success=Order approved successfully
```

### Permissions

Operation authorization is built with this key:

```text
jpm.auth.operation.{entityId}.{operationId}
```

Example:

```text
jpm.auth.operation.order.approve
```

If the operation belongs to a contextual entity, the `entityId` used in permissions includes the resolved contextual entity id.

### Useful `Operation` properties

The most relevant properties for custom operations are:

- `icon`: CSS class for the icon
- `confirm`: asks for confirmation before executing
- `compact`: renders a compact visual representation
- `showTitle`: forces the title to be shown next to the icon
- `repeatable`: adds the repeat checkbox when the view follows the framework form pattern
- `follows`: suggested next operation after success
- `navigable`: whether it affects navigation/history
- `popup`: tries to present it as a popup instead of full navigation
- `useFields`: whether the operation should use the framework field scheme
- `synchronic`: set to `false` for asynchronous execution
- `condition`: decides whether it should be shown based on the current instance and current view
- `properties`: helper map for flags or custom parameters

### Asynchronous operations

If the operation may take a long time, mark it as:

```xml
<property name="synchronic" value="false" />
```

With that, the framework registers an `AsynchronicOperationExecutor` and publishes progress through `/jpm-websocket`.

This is useful for:

- imports
- bulk recalculations
- document generation
- external synchronizations

### When a custom controller is a better fit

It makes sense to move out of the `executor` model and write a Spring controller when:

- you need multiple URLs for the same operation
- the flow combines HTML, JSON, file downloads, or very different views
- you need a non-standard routing convention
- the operation is not naturally tied to an entity `Operation`

In that case, you can still map it under `/jpm/.../{operationId:myOperation}` to preserve the framework semantics.

## Security

Current security is built on Spring Security 5.8 with XML configuration.

The `bs5` configuration already includes:

- form-based login
- remember-me
- `ROLE_USER`-based protection
- `userDetailsService`
- password encoder
- session event publishing
- websocket endpoint `/jpm-websocket`

Base file: `modules/jpm2-web-bs5/src/main/webapp/WEB-INF/spring-security.xml`.

## Persistence

The persistence layer is still based on Hibernate 5 with `LocalSessionFactoryBean` and `HibernateTransactionManager`.

Important points:

- datasource defined in XML
- annotated classes registered in `spring-hibernate.xml`
- `OpenSessionInViewFilter` in `web.xml`
- MySQL dialect by default in the base configuration

In real applications, you add your own domain annotated classes to the `sessionFactory`, in addition to the internal framework entities.

## Build and execution

Build the whole framework:

```bash
mvn clean install
```

Run tests:

```bash
mvn test
```

Build only the bs5 line:

```bash
mvn -pl modules/jpm2-web-bs5 -am package
```

Build the bs5 sample app:

```bash
mvn -pl modules/jpm2-web-bs5-test -am package
```

Manual frontend install if you need to inspect assets:

```bash
cd modules/jpm2-web-bs5/src/main/webapp/static
npm install
npm run build
```

## Recommended minimal flow for a new application

1. Create a `war` project.
2. Add dependencies to `jpm2-web-bs5`, `jpm2-web-bs5:classes`, and `jpm2-core`.
3. Define Maven profiles with DB properties and `jpm.cssMode`.
4. Create `database.properties` and `extra.properties` using Maven filtering.
5. Copy the standard `WEB-INF` structure from `modules/jpm2-web-bs5-test`.
6. Create the `jpm` bean in `spring-jpm.xml`.
7. Register `component-scan` for your own controllers and services.
8. Declare a simple entity in `WEB-INF/entities/...xml`.
9. Add the JPA class and its DAO.
10. Import the entity XML in `spring-jpm.xml`.

## References inside this repository

- Minimal sample app in this repo: `modules/jpm2-web-bs5-test`

Files worth studying:

- `modules/jpm2-web-bs5-test/src/main/webapp/WEB-INF/spring-jpm.xml`
- `modules/jpm2-web-bs5-test/src/main/webapp/WEB-INF/entities/test.xml`
- `modules/jpm2-web-bs5/src/main/webapp/WEB-INF/applicationContext.xml`
- `modules/jpm2-web-bs5/src/main/webapp/WEB-INF/spring-security.xml`
- `modules/jpm2-web-bs5/src/main/webapp/WEB-INF/spring-hibernate.xml`

## Limitations and notes

- Configuration still relies heavily on Spring XML.
- The web base still uses `javax.servlet`, not Spring Boot or a full Jakarta EE stack.
- `bs5` is the active line; legacy modules are still present in the repository for compatibility.
- Some frontend pieces are inherited and not fully modernized to a pure Bootstrap 5 ecosystem.
