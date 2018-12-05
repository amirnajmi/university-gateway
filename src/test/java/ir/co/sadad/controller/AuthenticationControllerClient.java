package ir.co.sadad.controller;

import ir.co.sadad.service.dto.LoginDTO;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@Path("/api")
public interface AuthenticationControllerClient {

    @Path("/authenticate")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public Response login(LoginDTO loginDTO);

}
