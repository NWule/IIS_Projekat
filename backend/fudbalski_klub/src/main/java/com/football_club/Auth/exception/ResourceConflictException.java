package com.football_club.Auth.exception;

public class ResourceConflictException extends RuntimeException {
    private String resourceId;

    public ResourceConflictException(String resourceId, String message) {
        super(message);
        this.setResourceId(resourceId);
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

}
