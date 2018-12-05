package ir.co.sadad.controller;

import com.orbitz.consul.Consul;
import com.orbitz.consul.model.health.Service;
import ir.co.sadad.controller.vm.RouteVM;
import ir.co.sadad.controller.vm.ServiceInstanceVM;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static javax.ws.rs.core.Response.Status.OK;

/**
 * Controller for view and managing Log Level at runtime.
 */
@Path("/api/gateway")
public class GatewayController {

    @Inject
    @ConfigProperty(name = "registry.url")
    private String registryUrl;

    /**
     * GET /routes : get the active routes.
     *
     * @return the Response with status 200 (OK) and with body the list of
     * routes
     */
    @Path("/routes")
    @GET
    @Timed
    public Response activeRoutes() {
        List<RouteVM> routeVMs = new ArrayList<>();
        Consul consul = Consul.builder().withUrl(registryUrl).build();
        for (Service service : consul.agentClient().getServices().values()) {
            RouteVM routeVM = new RouteVM();
            ServiceInstanceVM serviceInstance = new ServiceInstanceVM();
            routeVM.setServiceId(service.getId());
            routeVM.setPath("/" + service.getService());
            String uri = "http://"+service.getAddress() + ':' + service.getPort();
            serviceInstance.setUri(URI.create(uri));
            serviceInstance.setStatus(getServiceStatus(uri, service.getService()));
            serviceInstance.setTags(service.getTags());
            routeVM.setServiceInstances(asList(serviceInstance));
            routeVMs.add(routeVM);
        }
        return Response.ok(routeVMs).build();
    }

    private String getServiceStatus(String uri, String service) {
        Client client = ClientBuilder.newClient();
        Response response = client.target(uri + "/health").request().get();
        if (response.getStatus() == OK.getStatusCode()) {
            return response.readEntity(JsonObject.class)
                    .getJsonArray("checks")
                    .stream()
                    .map(JsonObject.class::cast)
                    .filter(check -> service.equals(check.getString("name")))
                    .map(check -> check.getString("state"))
                    .findAny()
                    .orElse(null);
        }
        return null;
    }
}
