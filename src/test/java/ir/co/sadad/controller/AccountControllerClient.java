package ir.co.sadad.controller;

import ir.co.sadad.controller.vm.KeyAndPasswordVM;
import ir.co.sadad.controller.vm.ManagedUserVM;
import ir.co.sadad.controller.vm.PasswordChangeVM;
import ir.co.sadad.service.dto.UserDTO;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RegisterRestClient
@Path("/api")
public interface AccountControllerClient {

    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response registerAccount(ManagedUserVM managedUserVM);

    @Path("/activate")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response activateAccount(@QueryParam("key") String key);

    @Path("/authenticate")
    @GET
    @Produces({MediaType.TEXT_PLAIN})
    public String isAuthenticated();

    @Path("/account")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAccount();

    @Path("/account")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response saveAccount(UserDTO userDTO);

    @Path("/account/change-password")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public Response changePassword(PasswordChangeVM passwordChangeVM);

    @Path("/account/reset-password/init")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public Response requestPasswordReset(String mail);

    @Path("/account/reset-password/finish")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public Response finishPasswordReset(KeyAndPasswordVM keyAndPassword);

}