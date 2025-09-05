package cat.itacademy.s05.t02.n01.config;

import cat.itacademy.s05.t02.n01.services.whatsapp.MetaWhatsAppSender;
import cat.itacademy.s05.t02.n01.services.whatsapp.NoopWhatsAppSender;
import cat.itacademy.s05.t02.n01.services.whatsapp.TwilioWhatsAppSender;
import cat.itacademy.s05.t02.n01.services.whatsapp.WhatsAppSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WhatsAppConfig {

    @Bean
    public WhatsAppSender whatsAppSender(
            @Value("${app.whatsapp.provider:none}") String provider,
            @Value("${app.whatsapp.meta.base-url:https://graph.facebook.com/v20.0}") String metaBaseUrl,
            @Value("${app.whatsapp.meta.phone-number-id:}") String metaPhoneNumberId,
            @Value("${app.whatsapp.meta.token:}") String metaToken,
            @Value("${app.whatsapp.twilio.base-url:https://api.twilio.com}") String twilioBaseUrl,
            @Value("${app.whatsapp.twilio.account-sid:}") String twilioAccountSid,
            @Value("${app.whatsapp.twilio.auth-token:}") String twilioAuthToken,
            @Value("${app.whatsapp.twilio.from:}") String twilioFrom
    ) {
        switch (provider.toLowerCase()) {
            case "meta":
                return new MetaWhatsAppSender(WebClient.builder().baseUrl(metaBaseUrl).build(), metaPhoneNumberId, metaToken);
            case "twilio":
                return new TwilioWhatsAppSender(WebClient.builder().baseUrl(twilioBaseUrl).build(), twilioAccountSid, twilioAuthToken, twilioFrom);
            default:
                return new NoopWhatsAppSender();
        }
    }
}

