package cc.catalysts.boot.javamelody.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Klaus Lehner
 */
@ConfigurationProperties(prefix = "javamelody")
public class JavaMelodyConfigurationProperties {

    /**
     * Set this property to <code>true</code> to entirely disable profiling with JavaMelody
     */
    private boolean disabled = false;

    /**
     * The path under width JavaMelody should be available
     */
    private String monitoringPath = "/monitoring";

    /**
     * A list of URLs that should not be profiled with JavaMelody
     */
    private String urlExcludePattern = "(/webjars/.*|/css/.*|/images/.*|/fonts/.*|/ui/.*|/js/.*|/views/.*|/monitoring/.*|/lesscss/.*|/favicon.ico)";
    private String storageDirectory = "/tmp/javamelody";
    private String[] urlPatterns = new String[]{"/*"};
    private String[] excludedDataSources = new String[0];

    private boolean enableSpringServiceMonitoring = false;
    private boolean enableSpringControllerMonitoring = false;

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getMonitoringPath() {
        return monitoringPath;
    }

    public void setMonitoringPath(String monitoringPath) {
        this.monitoringPath = monitoringPath;
    }

    public String getUrlExcludePattern() {
        return urlExcludePattern;
    }

    public void setUrlExcludePattern(String urlExcludePattern) {
        this.urlExcludePattern = urlExcludePattern;
    }

    public String getStorageDirectory() {
        return storageDirectory;
    }

    public void setStorageDirectory(String storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

    public String[] getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(String[] urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

    public String[] getExcludedDataSources() {
        return excludedDataSources;
    }

    public void setExcludedDataSources(String[] excludedDataSources) {
        this.excludedDataSources = excludedDataSources;
    }

    public boolean isEnableSpringServiceMonitoring() {
        return enableSpringServiceMonitoring;
    }

    public void setEnableSpringServiceMonitoring(boolean enableSpringServiceMonitoring) {
        this.enableSpringServiceMonitoring = enableSpringServiceMonitoring;
    }

    public boolean isEnableSpringControllerMonitoring() {
        return enableSpringControllerMonitoring;
    }

    public void setEnableSpringControllerMonitoring(boolean enableSpringControllerMonitoring) {
        this.enableSpringControllerMonitoring = enableSpringControllerMonitoring;
    }
}
