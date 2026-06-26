package jpaoletti.jpm2.core.service;

import java.util.ArrayList;
import java.util.List;

public class NotificationBellPayload {

    private long unreadCount;
    private String viewAllUrl;
    private List<NotificationBellPayloadItem> items = new ArrayList<>();

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getViewAllUrl() {
        return viewAllUrl;
    }

    public void setViewAllUrl(String viewAllUrl) {
        this.viewAllUrl = viewAllUrl;
    }

    public List<NotificationBellPayloadItem> getItems() {
        return items;
    }

    public void setItems(List<NotificationBellPayloadItem> items) {
        this.items = items;
    }
}
