package cc.catalysts.boot.javamelody.config;


import net.bull.javamelody.MonitoringFilter;
import net.bull.javamelody.Parameter;
import net.bull.javamelody.SessionListener;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Adds <a href="https://github.com/javamelody/javamelody/wiki">JavaMelody</a> to your application.
 * JavaMelody can be configured via properties with the prefix <code>javamelody</code>.
 *
 * @author Klaus Lehner
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "javamelody")
@ImportResource("classpath:net/bull/javamelody/monitoring-spring.xml")
public class JavaMelodyAutoConfiguration implements ServletContextInitializer {

    private boolean disabled = false;
    private String monitoringPath = "/monitoring";
    private String urlExcludePattern = "(/webjars/.*|/css/.*|/images/.*|/fonts/.*|/ui/.*|/js/.*|/views/.*|/monitoring/.*|/lesscss/.*|/favicon.ico)";
    private String storageDirectory = "/tmp/javamelody";
    private String[] urlPatterns = new String[]{"/*"};

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addListener(new SessionListener());
    }

    @Bean
    public FilterRegistrationBean javaMelody() {
        final FilterRegistrationBean javaMelody = new FilterRegistrationBean();
        javaMelody.setFilter(new MonitoringFilter());
        javaMelody.setAsyncSupported(true);
        javaMelody.setName("javamelody");
        javaMelody.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);

        // see the list of parameters:
        // https://github.com/javamelody/javamelody/wiki/UserGuide#6-optional-parameters
        javaMelody.addInitParameter(Parameter.LOG.getCode(), Boolean.toString(false));
        javaMelody.addInitParameter(Parameter.DISABLED.getCode(), Boolean.toString(disabled));
        javaMelody.addInitParameter(Parameter.MONITORING_PATH.getCode(), monitoringPath);
        javaMelody.addInitParameter(Parameter.STORAGE_DIRECTORY.getCode(), storageDirectory);
        javaMelody.addInitParameter(Parameter.URL_EXCLUDE_PATTERN.getCode(), urlExcludePattern);

        javaMelody.addUrlPatterns(urlPatterns);
        return javaMelody;
    }

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
}
