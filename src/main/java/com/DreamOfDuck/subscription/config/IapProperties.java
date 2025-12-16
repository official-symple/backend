package com.DreamOfDuck.subscription.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "iap")
public class IapProperties {
    private Apple apple = new Apple();
    private Google google = new Google();

    @Getter
    @Setter
    public static class Apple {
        /**
         * App Store Connect shared secret (for subscription receipt verification).
         * Keep it in `application-secret.properties`.
         */
        private String sharedSecret;
        private boolean sandbox = true;
    }

    @Getter
    @Setter
    public static class Google {
        /**
         * Google Play Developer API credentials/service-account config.
         * Keep it in `application-secret.properties`.
         */
        private String serviceAccountJson;
        private String packageName;
    }
}


