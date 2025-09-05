package cat.itacademy.s05.t02.n01.services.whatsapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class MetaWhatsAppSender implements WhatsAppSender {

    private static final Logger log = LoggerFactory.getLogger(MetaWhatsAppSender.class);

    private final WebClient client;
    private final String phoneNumberId;
    private final String token;

    public MetaWhatsAppSender(WebClient client, String phoneNumberId, String token) {
        this.client = client;
        this.phoneNumberId = phoneNumberId;
        this.token = token;
    }

    @Override
    public Mono<Void> sendText(String toPhoneE164, String text) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("to", toPhoneE164);
        payload.put("type", "text");

        Map<String, Object> textObj = new HashMap<>();
        textObj.put("preview_url", false);
        textObj.put("body", text);
        payload.put("text", textObj);

        return client.post()
                .uri("/{phoneId}/messages", phoneNumberId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth(token))
                .body(BodyInserters.fromValue(payload))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(body -> log.debug("Meta WA response: {}", body))
                .then();
    }
}

