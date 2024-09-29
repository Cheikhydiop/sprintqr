package com.dette.listener;

import com.dette.event.FidelityCardEvent;
import com.dette.model.Client;
import com.dette.service.EmailService;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class FidelityCardEventListener {

    @Autowired
    private EmailService emailService;

    @EventListener
    public void handleFidelityCardEvent(FidelityCardEvent event) {
        Client client = event.getClient();
        try {
			emailService.sendFidelityCardWithQRCode(client);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
