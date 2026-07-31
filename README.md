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

## Configuration parameters (Sysparam)

`Sysparam` is the schema-first typed configuration module. The **code is the source of truth**
for each parameter's type, default, group and secrecy; the database only holds the current
values. Administrators edit those values through a normal JPM entity, and application code
reads them in a type-safe way.

### Declaring parameters

Declare a `@Component` that implements `SysparamModule` and returns the list of definitions.
Group the definitions in holder classes so the catalog stays readable:

```java
public final class MailKeys {
    public static final SysparamDef<String>  MAIL_SENDER =
            SysparamDef.string("mail-sender-code").def("default").group("mail").build();
    public static final SysparamDef<Integer> RETRIES =
            SysparamDef.integer("mail-retries").def(3).group("mail").range(0, 10).build();
    public static final SysparamDef<String>  API_KEY =
            SysparamDef.secret("mail-api-key").group("mail").build();   // encrypted

    public static List<SysparamDef<?>> defs() {
        return List.of(MAIL_SENDER, RETRIES, API_KEY);
    }
}

@Component
public class MySysparamModule implements SysparamModule {
    @Override public List<SysparamDef<?>> params() { return MailKeys.defs(); }
}
```

`SysparamDef` factories: `string`, `integer`, `longParam`, `decimal`, `doubleParam`, `bool`,
`date`, `datetime`, `duration`, `list`, `json`, `url`, `path`, `enumOf(key, allowed...)`,
`secret`. Builder options: `.def(value)`, `.group(name)`, `.cached(boolean)`, `.required()`,
`.regex(...)`, `.range(min, max)`, `.description(i18nKey)`, then `.build()`.

`SECRET` is a **type**, not a flag: those values are encrypted at rest (AES/GCM via
`SysparamCipher`) and masked in listings and audit. Encryption requires the `sysparam.secret.key`
property; without it, secret parameters cannot be stored or read (fail-closed).

### Reading a value in code

Inject `SysparamService` and read the typed value straight from the definition:

```java
@Autowired private SysparamService sysparamService;

String  sender  = sysparamService.get(MailKeys.MAIL_SENDER);   // typed (String)
int     retries = sysparamService.get(MailKeys.RETRIES);       // typed (Integer)
String  raw     = sysparamService.getRaw("mail-sender-code");  // raw string, by key
```

Resolution order is **cache → DB override → catalog default**. Non-secret values are cached in
the `sysparam` cache region.

### Editing values (admin)

Import the entity definitions in your `spring-jpm.xml` and register the annotated classes:

```xml
<import resource="classpath:entities/sysparam.xml" />
<import resource="classpath:entities/sysparamGroup.xml" />
```

`sysparam` exposes `list`, `show`, `setValue` (a typed editor — radios for booleans, dropdown for
`enumOf`, number/textarea by type, masked input for secrets), `clearCache` / `clearAllCache`,
`sysparamHealth` (missing-required, plaintext-secret, orphan, validation checks…), `sysparamTree`
(group → parameter tree) and `import` / `export`. Writes go through `SysparamService.set(...)`,
which validates, encrypts secrets, evicts the cache and records the change via the standard
detailed audit. `sysparamGroup` holds the purely aesthetic group metadata (label, icon, `style`,
collapsed, order) used by the tree; groups are auto-seeded and are not created or deleted by hand.

Missing parameters (and groups) are auto-seeded from the catalog on boot, so a freshly declared
definition appears with its default without any DDL. A compatibility bridge
(`SysparamConfigBridge`) routes declared keys to Sysparam and leaves undeclared keys on the legacy
store, which lets an application migrate its configuration key by key.

Register the entities on the `sessionFactory` (`spring-hibernate.xml`):

```
jpaoletti.jpm2.core.model.persistent.Sysparam
jpaoletti.jpm2.core.model.persistent.SysparamGroup
```

Tables: `jpm_sysparam` and `jpm_sysparam_group`.

## Debug logging (DebugLog)

`DebugLog` is a lightweight, runtime-controllable debug logging facility. It replaces the old
pattern of gating `logger.info(...)` calls behind a boolean configuration flag. State is pure
in-memory operational state (a debug switch is diagnostic, not application config), so it lives
in the logging layer and never touches the database on the hot path.

Instead of a single on/off flag it uses a numeric **level** per **channel**:

- `0` = OFF, `1` = BASIC (milestones/decisions), `2` = DETAILED (payloads, intermediate
  values), `3` = TRACE (per-iteration, raw dumps).
- A **global** level applies to every channel; a **per-channel** override focuses one area
  (e.g. global `0` but channel `prisma=3`) without flooding the rest.
- A call at level `L` on a channel logs only when that channel's effective level is `>= L`.
  Everything defaults to `0` (off).

### From application code

Use the facade on `PresentationManager` (`getJpm()` is available almost everywhere):

```java
getJpm().debug("processed order " + id);                 // default channel, level 1
getJpm().debug(2, "gateway payload: " + data);           // default channel, level 2
getJpm().debug("prisma", 3, "raw response: " + xml);     // channel "prisma", level 3
getJpm().debug(3, () -> expensiveDump());                // lazy: supplier runs only if it logs

if (getJpm().isDebug("prisma", 2)) { /* guard a costly block */ }
```

Output flows through log4j2 loggers named `jpm.debug` (no channel) or `jpm.debug.<channel>`, so
it can be routed or filtered by normal appender configuration. **The numeric level is the gate;
messages are currently emitted at log4j `INFO`** (the level does not change the log4j severity —
levels 1/2/3 all come out as `INFO` on the corresponding logger).

### Controlling it at runtime via Sysparam

Declaring an `INTEGER` Sysparam named `debug` turns the sysparam admin into the live control
surface: `SysparamService` pushes any write to `debug` (or `debug.<channel>`) into `DebugLog`
immediately, and re-seeds the levels from the database on boot (so a level left on survives a
restart).

```java
public static final SysparamDef<Integer> DEBUG =
        SysparamDef.integer(SysparamService.DEBUG_KEY).def(0).group("debug").build();
```

Setting `debug = 2` from the `setValue` screen takes effect at once — no restart. Per-channel
keys (`debug.prisma = 3`) drive individual channels.

### Programmatic control

```java
DebugLog.setGlobalLevel(2);                 // or setGlobalLevel(2, 1800) with a TTL in seconds
DebugLog.setChannelLevel("prisma", 3);      // TTL-capable overload as well
DebugLog.reset();                           // everything off
Map<String,Integer> active = DebugLog.channels();
```

TTL-based enables let a channel turned on in production switch itself off again after N seconds.

## AI connectors (AIService)

`AIService` is a provider-neutral facade for large-language-model completions. Application code
talks to a single neutral API; **which** provider actually runs (Claude, OpenAI, Gemini, ...) and
which model, credentials and endpoint are used are decided by an administrable **connector**, not by
the caller. Every call is recorded for cost/audit purposes.

### Architecture

- **`AIProviderImplementation`** — the pluggable contract (same idea as a gateway/converter
  implementation): `complete(AIConnectorConfig, AIRequest)`, plus `code()` and
  `supports(capability)`. Built-in implementations cover Claude (Anthropic Messages API), OpenAI
  (Chat Completions) and Gemini (Generative Language API); each talks raw HTTP through the JDK
  client, so no extra dependency is added to the core.
- **`AIProviderType`** — catalog enum (`CLAUDE`, `OPENAI`, `GEMINI`, with room for more) whose
  `code` selects the implementation.
- **Neutral DTOs** — `AIRequest` (messages, model, `maxTokens`, `temperature`, system prompt, an
  optional `jsonSchema` for structured output, and an `extras` map for provider-specific
  passthrough), `AICompletion` (text, `structuredJson`, model, `AIUsage` token counts,
  `finishReason`, a `refusal` flag and the raw payload), `AIMessage`/`AIRole`, and
  `AIConnectorConfig` (the resolved, decrypted runtime configuration handed to a provider).
- **Entities** — `AIConnector` (one configured connection), `AIConnectorParameter` (name/value
  children, with an `encrypted` flag for secrets) and `AICallLog` (per-call ledger: model, token
  usage, latency, status, truncated request/response).

Keep provider implementations thin: model chat and structured output, and let uncommon
provider-specific knobs travel through `AIRequest.extras` rather than growing the neutral DTO for
every feature.

### Enabling the module

The three entity definitions are self-contained; `aiConnector.xml` also declares the provider beans
and the `aiService` bean, so importing them is all the wiring needed:

```xml
<import resource="classpath:entities/aiConnector.xml" />
<import resource="classpath:entities/aiConnectorParameter.xml" />
<import resource="classpath:entities/aiCallLog.xml" />
```

Then register the entity classes with the session factory (`AIConnector`, `AIConnectorParameter`,
`AICallLog`) and add the three tables to the application DDL. A `SysparamCipher` bean is optional
(see below).

### Configuring a connector (admin)

A connector has: `code`, `type` (provider), `defaultModel`, `fallbackModels` (CSV, tried in order),
`baseUrl` (optional; each provider defaults to its public endpoint), `timeoutMs`, `active`, and a
logical `purpose`. Credentials and extra settings are child **parameters**; the API key is stored in
a parameter named `api-key` with `encrypted = true`. When the application declares a
`SysparamCipher` bean, that value is decrypted transparently; otherwise it is read as plaintext (the
cipher contract is a plaintext passthrough either way).

### Calling it from code

Resolve a connector by its logical `purpose` (so callers never hardcode a provider) or by `code`:

```java
@Autowired
private AIService aiService;

final AIRequest request = AIRequest.builder()
        .system("You are a helpful assistant. Answer as strict JSON matching the schema.")
        .user(userText)
        .jsonSchema(schemaJson)   // optional: request structured output
        .build();

final AICompletion completion = aiService.completeForPurpose("my-purpose", request);
// or: aiService.complete("my-connector-code", request);

if (completion.isRefusal()) { /* safety-policy decline: no exception, refusal flag is set */ }
final String out = (completion.getStructuredJson() != null)
        ? completion.getStructuredJson()
        : completion.getText();
```

`AIService` decrypts the credentials, dispatches to the implementation matching the connector
`type`, tries the model fallback chain in order, and writes an `AICallLog` row for every attempt. A
transport failure or non-2xx response throws `AIException`; a provider safety decline comes back as
a normal `AICompletion` with `refusal == true` (not an exception).

### Structured output

Set `jsonSchema` on the request with a standard JSON Schema (object types, `enum`, arrays, nested
objects, `required`, `additionalProperties`). Each provider maps it to its own structured-output
mechanism, and `AICompletion.getStructuredJson()` returns the constrained JSON. The first call with
a new schema can be noticeably slower (one-time schema compilation on the provider side); repeated
calls with the same schema are fast.

### Adding a provider

Implement `AIProviderImplementation` (map the neutral `AIRequest` to the provider's wire format and
its response back to `AICompletion`), register it as a bean in the provider list in
`aiConnector.xml`, and add a value to `AIProviderType`. Connectors of that type then become
selectable in the admin and are resolved automatically — no change to calling code.

### Injecting context (`AIContextProvider`)

Modules can enrich prompts without the caller assembling the context by hand. Implement
`AIContextProvider` as a bean:

```java
@Component
public class MyContext implements AIContextProvider {
    public boolean supports(String purpose) { return "my-purpose".equals(purpose); }
    public List<String> contribute(AIContextRequest req) {
        // req.getPurpose(), req.getInput() (user text), req.getAttribute("...")
        return List.of("Relevant catalog/tenant/retrieved context ...");
    }
}
```

`AIService` auto-discovers every `AIContextProvider` bean. Before dispatching a completion it asks the
ones that `supports(purpose)` (the connector's `purpose`) for snippets and appends them to the system
prompt under a `[Context]` section. A provider that throws is logged and skipped — it never fails the
completion. Domain data a provider needs travels in the request's `attributes` bag (read by context
providers, never sent to the model — that is what `extras` is for):

```java
AIRequest.builder().user(text).attribute("order", order).build();
```

The core ships no provider: retrieval/context is always a module concern, keeping the domain out of the
core. Snippet injection is logged on the `ai-connector` channel (count at level 1, full snippets at 3).

### Entitlements (`AIEntitlementResolver`)

`AIService.isEnabled(purpose)` answers whether AI is available for a purpose: an active connector
exists for it **and** every registered `AIEntitlementResolver` allows it (it fails closed on a
resolver error). With no resolver, entitlement reduces to "a connector for the purpose is active" —
the hook for "a client acquired an AI service, so its modules light up". A multi-tenant application
registers a resolver bean that reads the current session/tenant:

```java
@Component
public class MyEntitlement implements AIEntitlementResolver {
    public boolean isEnabled(String purpose) { return currentTenantHasAI(purpose); }
}
```

Gate an operation on it with the ready-made `AIEnabledCondition` (one bean per purpose), referenced
as the operation's `condition`:

```xml
<bean id="aiEnabled-myPurpose" class="jpaoletti.jpm2.core.service.AIEnabledCondition">
    <property name="purpose" value="my-purpose" />
</bean>
```

The operation (and its menu entry) then shows only when AI is enabled for that purpose.

### Debugging

The module logs richly on the DebugLog channel **`ai-connector`**: connector resolution, model
attempts, HTTP status, token usage and latency at level `1`; endpoint and config metadata at level
`2`; and full request/response dumps at level `3`. API keys are never logged. Enable it like any
other channel (see *Debug logging* above), e.g. set `debug.ai-connector = 3`.

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
