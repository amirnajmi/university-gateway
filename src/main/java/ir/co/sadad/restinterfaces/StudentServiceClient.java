package ir.co.sadad.restinterfaces;


import ir.co.sadad.controller.util.HeaderUtil;
import ir.co.sadad.exception.StudentCreationException;
import ir.co.sadad.restinterfaces.restclientmodels.Student;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.Dependent;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Dependent
@RegisterRestClient
@RegisterProvider(StudentCreationException.class)
@Path("/api/student")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface StudentServiceClient {



    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createStudent(@HeaderParam("Authorization") String authorization, Student student);

}
