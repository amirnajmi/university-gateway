package ir.co.sadad.web;

import ir.co.sadad.metrics.InstrumentedFilter;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.EnumSet;

import static javax.servlet.DispatcherType.*;

/**
 * Configuration of web application
 */
@WebListener
public class WebConfigurer implements ServletContextListener {

    @Inject
    private Logger log;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        initMetrics(sce.getServletContext());
        log.info("Web application fully configured");
    }

    /**
     * Initializes Metrics.
     */
    private void initMetrics(ServletContext servletContext) {
        log.debug("Registering Metrics Filter");
        FilterRegistration.Dynamic metricsFilter = servletContext.addFilter("Instrumented Metrics Filter", InstrumentedFilter.class);
        metricsFilter.addMappingForUrlPatterns(EnumSet.of(REQUEST, FORWARD, ASYNC), true, "/*");
        metricsFilter.setAsyncSupported(true);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
