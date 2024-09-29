package com.dette.event;

import com.dette.model.Client;
import org.springframework.context.ApplicationEvent;

public class FidelityCardEvent extends ApplicationEvent {
    private final Client client;

    public FidelityCardEvent(Object source, Client client) {
        super(source);
        this.client = client;
    }

    public Client getClient() {
        return client;
    }
}