package ir.co.sadad.controller;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.inject.Inject;

@Health
public class HealthController implements HealthCheck {

    @Inject
    @ConfigProperty(name = "context.path")
    private String contextPath;

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named(contextPath).up().build();
    }

}
