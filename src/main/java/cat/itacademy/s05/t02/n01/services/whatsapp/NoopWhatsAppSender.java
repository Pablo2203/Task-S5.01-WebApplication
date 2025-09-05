package cat.itacademy.s05.t02.n01.services.whatsapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class NoopWhatsAppSender implements WhatsAppSender {

    private static final Logger log = LoggerFactory.getLogger(NoopWhatsAppSender.class);

    @Override
    public Mono<Void> sendText(String toPhoneE164, String text) {
        log.info("[WA NOOP] to={} text={}...", toPhoneE164, text != null ? Math.min(text.length(), 32) : 0);
        // No envío real
        return Mono.empty();
    }
}

