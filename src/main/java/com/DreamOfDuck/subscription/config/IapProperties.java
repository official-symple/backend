package com.DreamOfDuck.subscription.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

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
         * App Store Connect API Key file content (.p8 file as string).
         * Can be stored in GitHub Secrets or application-secret.properties.
         * Include the full PEM content including -----BEGIN PRIVATE KEY----- and -----END PRIVATE KEY-----
         */
        private String keyFileContent;
        
        /**
         * App Store Connect API Key ID (10-character string).
         * Found in App Store Connect > Users and Access > Integrations > Keys
         */
        private String keyId;
        
        /**
         * App Store Connect Issuer ID (UUID format).
         * Found in App Store Connect > Users and Access > Integrations > Keys
         */
        private String issuerId;
        
        /**
         * Bundle ID of your app.
         */
        private String bundleId;
        
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


