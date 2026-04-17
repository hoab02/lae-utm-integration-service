package com.viettelpost.fms.utm_integration.session.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "integration.utm")
public class UtmSessionProperties {

    private String baseUrl;
    private String dcsId;
    private String username;
    private String password;
    private final Auth auth = new Auth();
    private final Worker worker = new Worker();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDcsId() {
        return dcsId;
    }

    public void setDcsId(String dcsId) {
        this.dcsId = dcsId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Auth getAuth() {
        return auth;
    }

    public Worker getWorker() {
        return worker;
    }

    public String resolveUsername() {
        return StringUtils.hasText(username) ? username : dcsId;
    }

    public static class Auth {

        private String tokenPath = "/utm-qg/oauth2/token";
        private String refreshPath = "/utm-qg/oauth2/refresh_token";
        private long refreshBeforeSeconds = 300;

        public String getTokenPath() {
            return tokenPath;
        }

        public void setTokenPath(String tokenPath) {
            this.tokenPath = tokenPath;
        }

        public String getRefreshPath() {
            return refreshPath;
        }

        public void setRefreshPath(String refreshPath) {
            this.refreshPath = refreshPath;
        }

        public long getRefreshBeforeSeconds() {
            return refreshBeforeSeconds;
        }

        public void setRefreshBeforeSeconds(long refreshBeforeSeconds) {
            this.refreshBeforeSeconds = refreshBeforeSeconds;
        }
    }

    public static class Worker {

        private long refreshIntervalMs = 60000;

        public long getRefreshIntervalMs() {
            return refreshIntervalMs;
        }

        public void setRefreshIntervalMs(long refreshIntervalMs) {
            this.refreshIntervalMs = refreshIntervalMs;
        }
    }
}
