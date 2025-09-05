package cat.itacademy.s05.t02.n01.services.whatsapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class TwilioWhatsAppSender implements WhatsAppSender {

    private static final Logger log = LoggerFactory.getLogger(TwilioWhatsAppSender.class);

    private final WebClient client;
    private final String accountSid;
    private final String authToken;
    private final String fromNumber; // format: whatsapp:+1415...

    public TwilioWhatsAppSender(WebClient client, String accountSid, String authToken, String fromNumber) {
        this.client = client;
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromNumber = fromNumber;
    }

    @Override
    public Mono<Void> sendText(String toPhoneE164, String text) {
        String to = toPhoneE164.startsWith("whatsapp:") ? toPhoneE164 : "whatsapp:" + toPhoneE164;
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("From", fromNumber);
        form.add("To", to);
        form.add("Body", text);

        return client.post()
                .uri("/2010-04-01/Accounts/{sid}/Messages.json", accountSid)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .headers(h -> h.setBasicAuth(accountSid, authToken))
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(body -> log.debug("Twilio response: {}", body))
                .then();
    }
}

