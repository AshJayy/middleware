package com.swiftlogistics.orchestrator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Environment Configuration
 * Loads configuration from .env file and environment variables
 * Provides centralized access to all environment-specific settings
 */
@Configuration
@PropertySource(value = "file:.env", ignoreResourceNotFound = true)
@EnableConfigurationProperties
public class EnvironmentConfig {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentConfig.class);

    @Autowired
    private Environment env;

    @PostConstruct
    public void logEnvironmentInfo() {
        String appName = getProperty("APP_NAME", "orchestrator");
        String appVersion = getProperty("APP_VERSION", "unknown");
        String appEnvironment = getProperty("APP_ENVIRONMENT", "development");
        
        log.info("=================================================================");
        log.info("Swift Logistics Orchestrator Starting...");
        log.info("Application: {} v{}", appName, appVersion);
        log.info("Environment: {}", appEnvironment);
        log.info("=================================================================");
    }

    // =============================================================================
    // DATABASE CONFIGURATION
    // =============================================================================
    
    public String getMongoHost() {
        return getProperty("MONGODB_HOST", "localhost");
    }

    public int getMongoPort() {
        return Integer.parseInt(getProperty("MONGODB_PORT", "27017"));
    }

    public String getMongoDatabase() {
        return getProperty("MONGODB_DATABASE", "swiftlogistics");
    }

    public String getMongoUsername() {
        return getProperty("MONGODB_USERNAME", "");
    }

    public String getMongoPassword() {
        return getProperty("MONGODB_PASSWORD", "");
    }

    public String getMongoUri() {
        return getProperty("MONGODB_URI", "");
    }

    // =============================================================================
    // RABBITMQ CONFIGURATION
    // =============================================================================
    
    public String getRabbitMQHost() {
        return getProperty("RABBITMQ_HOST", "localhost");
    }

    public int getRabbitMQPort() {
        return Integer.parseInt(getProperty("RABBITMQ_PORT", "5672"));
    }

    public String getRabbitMQUsername() {
        return getProperty("RABBITMQ_USERNAME", "guest");
    }

    public String getRabbitMQPassword() {
        return getProperty("RABBITMQ_PASSWORD", "guest");
    }

    public String getRabbitMQVirtualHost() {
        return getProperty("RABBITMQ_VIRTUAL_HOST", "/");
    }

    // =============================================================================
    // EXTERNAL SERVICES CONFIGURATION
    // =============================================================================
    
    public String getCmsAdapterBaseUrl() {
        return getProperty("CMS_ADAPTER_BASE_URL", "http://localhost:8001");
    }

    public String getCmsAdapterApiKey() {
        return getProperty("CMS_ADAPTER_API_KEY", "");
    }

    public String getWmsAdapterBaseUrl() {
        return getProperty("WMS_ADAPTER_BASE_URL", "http://localhost:8002");
    }

    public String getWmsAdapterApiKey() {
        return getProperty("WMS_ADAPTER_API_KEY", "");
    }

    public String getRosAdapterBaseUrl() {
        return getProperty("ROS_ADAPTER_BASE_URL", "http://localhost:8003");
    }

    public String getRosAdapterApiKey() {
        return getProperty("ROS_ADAPTER_API_KEY", "");
    }

    // =============================================================================
    // SECURITY CONFIGURATION
    // =============================================================================
    
    public String getJwtSecret() {
        return getProperty("JWT_SECRET", "default-jwt-secret-change-in-production");
    }

    public String[] getCorsAllowedOrigins() {
        String origins = getProperty("CORS_ALLOWED_ORIGINS", "http://localhost:3000");
        return origins.split(",");
    }

    // =============================================================================
    // FEATURE FLAGS
    // =============================================================================
    
    public boolean isRealTimeTrackingEnabled() {
        return Boolean.parseBoolean(getProperty("FEATURE_REAL_TIME_TRACKING", "true"));
    }

    public boolean isAdvancedRoutingEnabled() {
        return Boolean.parseBoolean(getProperty("FEATURE_ADVANCED_ROUTING", "true"));
    }

    public boolean isDevModeEnabled() {
        return Boolean.parseBoolean(getProperty("DEV_MODE_ENABLED", "true"));
    }

    // =============================================================================
    // UTILITY METHODS
    // =============================================================================
    
    /**
     * Get property value with fallback to default
     */
    private String getProperty(String key, String defaultValue) {
        return env.getProperty(key, defaultValue);
    }

    /**
     * Get required property (throws exception if not found)
     */
    public String getRequiredProperty(String key) {
        String value = env.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Required property '" + key + "' not found in environment");
        }
        return value;
    }

    /**
     * Check if we're running in development mode
     */
    public boolean isDevelopment() {
        return "development".equalsIgnoreCase(getProperty("APP_ENVIRONMENT", "development"));
    }

    /**
     * Check if we're running in production mode
     */
    public boolean isProduction() {
        return "production".equalsIgnoreCase(getProperty("APP_ENVIRONMENT", "development"));
    }

    /**
     * Get application version
     */
    public String getApplicationVersion() {
        return getProperty("APP_VERSION", "0.0.1-SNAPSHOT");
    }

    /**
     * Get application name
     */
    public String getApplicationName() {
        return getProperty("APP_NAME", "orchestrator");
    }
}
