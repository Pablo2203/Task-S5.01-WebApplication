package cat.itacademy.s05.t02.n01.services.whatsapp;

import reactor.core.publisher.Mono;

public interface WhatsAppSender {
    Mono<Void> sendText(String toPhoneE164, String text);
}

