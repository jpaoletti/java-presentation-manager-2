package jpaoletti.jpm2.core.ai;

/**
 * SPI that decides whether AI is entitled for a logical purpose — the hook for "a client acquired an AI
 * service, so its modules are enabled". Modules/tenants register {@code AIEntitlementResolver} beans;
 * {@code AIService.isEnabled(purpose)} requires an active connector for the purpose AND every registered
 * resolver to allow it (it fails closed on a resolver error). When no resolver is registered, entitlement
 * reduces to "an active connector exists for the purpose". A multi-tenant application typically implements
 * one resolver that reads the current session/tenant.
 */
public interface AIEntitlementResolver {

    /** @return true if AI is entitled for {@code purpose} (in the current context, if the impl is tenant-aware). */
    boolean isEnabled(String purpose);
}
