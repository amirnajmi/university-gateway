package ir.co.sadad.controller;

import com.ghasemkiani.util.SimplePersianCalendar;
import com.ghasemkiani.util.icu.PersianCalendar;
import ir.co.sadad.controller.vm.StudentVM;
import ir.co.sadad.exception.StudentCreationException;
import ir.co.sadad.repository.UserRepository;
import ir.co.sadad.domain.User;
import ir.co.sadad.restinterfaces.StudentServiceClient;
import ir.co.sadad.restinterfaces.restclientmodels.Student;
import ir.co.sadad.security.SecurityHelper;
import ir.co.sadad.service.MailService;
import ir.co.sadad.service.UserService;
import ir.co.sadad.controller.vm.ManagedUserVM;
import ir.co.sadad.service.dto.UserDTO;
import ir.co.sadad.controller.util.HeaderUtil;
import ir.co.sadad.controller.util.Page;
import ir.co.sadad.controller.util.PaginationUtil;
import static ir.co.sadad.config.Constants.EMAIL_ALREADY_USED_TYPE;
import static ir.co.sadad.config.Constants.LOGIN_ALREADY_USED_TYPE;
import static ir.co.sadad.security.AuthoritiesConstants.ADMIN;
import static ir.co.sadad.security.AuthoritiesConstants.USER;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import static java.util.stream.Collectors.toList;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

/**
 * REST controller for managing users.
 *
 * <p>
 * This class accesses the User entity, and needs to fetch its collection of
 * authorities.</p>
 */
@Path("/api")
public class UserController {

    @Inject
    private Logger log;

    @Inject
    private UserRepository userRepository;

    @Inject
    private MailService mailService;

    @Inject
    private UserService userService;

    @Inject
    @RestClient
    private StudentServiceClient studentServiceClient;


    @Inject
    SecurityHelper securityHelper;


    @Inject
    @Claim(standard = Claims.raw_token)
    private String rawToken;



    /**
     * POST /users : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends
     * an mail with an activation link. The user needs to be activated on
     * creation.
     * </p>
     *
     * @param managedUserVM the user to create
     * @return the Response with status 201 (Created) and with body the new
     * user, or with status 400 (Bad Request) if the login or email is already
     * in use
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Timed
    @Operation(summary = "create a new user")
    @APIResponse(responseCode = "201", description = "Created")
    @APIResponse(responseCode = "400", description = "Bad Request")
    @Path(value = "/users")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Response createUser(ManagedUserVM managedUserVM) throws URISyntaxException {
        log.debug("REST request to save User : {}", managedUserVM);

        //Lowercase the user login before comparing with database
        if (userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase()).isPresent()) {
            return HeaderUtil.createFailureAlert(Response.status(BAD_REQUEST), "userManagement", "userexists", LOGIN_ALREADY_USED_TYPE).build();
        } else if (userRepository.findOneByEmail(managedUserVM.getEmail()).isPresent()) {
            return HeaderUtil.createFailureAlert(Response.status(BAD_REQUEST), "userManagement", "emailexists", EMAIL_ALREADY_USED_TYPE).build();
        } else {
            User newUser = userService.createUser(managedUserVM);

            return HeaderUtil.createAlert(Response.created(new URI("/resources/api/users/" + newUser.getLogin())),
                    "userManagement.created", newUser.getLogin()).entity(new UserDTO(newUser)).build();
        }
    }


    /**
     * POST /students : Creates a new student.
     * <p>
     * Creates a new student if the login and email are not already used, and sends
     * an mail with an activation link. The student needs to be activated on
     * creation.
     * </p>
     *
     * @param studentVM the student to create
     * @return the Response with status 201 (Created) and with body the new
     * student, or with status 400 (Bad Request) if the login or email is already
     * in use
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Timed
    @Operation(summary = "create a new Student")
    @APIResponse(responseCode = "201", description = "Created")
    @APIResponse(responseCode = "400", description = "Bad Request")
    @Path(value = "/students")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    @Transactional(rollbackOn = StudentCreationException.class)
    public Response createStudent(StudentVM studentVM) throws URISyntaxException {
        log.debug("REST request to save User : {}", studentVM);

        //Lowercase the student login before comparing with database
        if (userRepository.findOneByLogin(studentVM.getLogin().toLowerCase()).isPresent()) {
            return HeaderUtil.createFailureAlert(Response.status(BAD_REQUEST), "userManagement", "userexists", LOGIN_ALREADY_USED_TYPE).build();
        } else if (userRepository.findOneByEmail(studentVM.getEmail()).isPresent()) {
            return HeaderUtil.createFailureAlert(Response.status(BAD_REQUEST), "userManagement", "emailexists", EMAIL_ALREADY_USED_TYPE).build();
        } else {
            User newUser = userService.createUser(studentVM);
            Student student = new Student();
            student.setName(studentVM.getFirstName());
            student.setLastName(studentVM.getLastName());
            student.setAccountId(newUser.getLogin());
            student.setEmail(studentVM.getEmail());
            student.setMobile(studentVM.getMobile());
            student.setNationalCode(studentVM.getNationalCode());
            student.setStudentNo(String.valueOf(Calendar.getInstance().get(Calendar.YEAR))+studentVM.getNationalCode());
            studentServiceClient.createStudent("Bearer "+rawToken,student);
            return HeaderUtil.createAlert(Response.created(new URI("/resources/api/users/" + newUser.getLogin())),
                    "userManagement.created", newUser.getLogin()).entity(new UserDTO(newUser)).build();
        }
    }


    /**
     * PUT /users : Updates an existing User.
     *
     * @param managedUserVM the user to update
     * @return the Response with status 200 (OK) and with body the updated user,
     * or with status 400 (Bad Request) if the login or email is already in use,
     * or with status 500 (Internal Server Error) if the user couldn't be
     * updated
     */
    @Timed
    @Operation(summary = "update user")
    @APIResponse(responseCode = "200", description = "OK")
    @APIResponse(responseCode = "400", description = "Bad Request")
    @APIResponse(responseCode = "500", description = "Internal Server Error")
    @Path(value = "/users")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    public Response updateUser(ManagedUserVM managedUserVM) {
        log.debug("REST request to update User : {}", managedUserVM);
        Optional<User> existingUser = userRepository.findOneByEmail(managedUserVM.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserVM.getId()))) {
            return HeaderUtil.createFailureAlert(Response.status(BAD_REQUEST), "userManagement", "emailexists", EMAIL_ALREADY_USED_TYPE).build();
        }
        existingUser = userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserVM.getId()))) {
            return HeaderUtil.createFailureAlert(Response.status(BAD_REQUEST), "userManagement", "userexists", LOGIN_ALREADY_USED_TYPE).build();
        }
        Optional<UserDTO> updatedUser = userService.updateUser(managedUserVM);

        return updatedUser.map(userDTO -> HeaderUtil.createAlert(Response.ok(userDTO),
                "userManagement.updated", managedUserVM.getLogin()).build())
                .orElseGet(() -> Response.status(NOT_FOUND).build());
    }

    /**
     * GET /users : get all users.
     *
     * @param page the pagination information
     * @param size the pagination size information
     * @return the Response with status 200 (OK) and with body all users
     * @throws URISyntaxException if the pagination headers couldn't be
     * generated
     */
    @Timed
    @Operation(summary = "get all the users")
    @APIResponse(responseCode = "200", description = "OK")
    @Path(value = "/users")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timeout
    @RolesAllowed(USER)
    public Response getAllUsers(@QueryParam("page") int page, @QueryParam("size") int size) throws URISyntaxException {
        List<User> userList = userRepository.getUsersWithAuthorities(page * size, size);
        List<UserDTO> userDTOs = userList.stream()
                .map(UserDTO::new)
                .collect(toList());

        ResponseBuilder builder = Response.ok(userDTOs);
        PaginationUtil.generatePaginationHttpHeaders(builder, new Page(page, size, userRepository.count()), "/resources/api/users");
        return builder.build();
    }

    /**
     * @return a string list of the all of the roles
     */
    @Timed
    @Operation(summary = "get roles")
    @APIResponse(responseCode = "200", description = "OK")
    @Path("/users/authorities")
    @GET
    @RolesAllowed(ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthorities() {
        List<String> authorities = userService.getAuthorities();
        GenericEntity<List<String>> listGenericEntity = new GenericEntity<List<String>>(authorities){};

        return Response.ok(listGenericEntity).build();
    }

    /**
     * GET /users/:login : get the "login" user.
     *
     * @param login the login of the user to find
     * @return the Response with status 200 (OK) and with body the "login" user,
     * or with status 404 (Not Found)
     */
    @Timed
    @Operation(summary = "get the user")
    @APIResponse(responseCode = "200", description = "OK")
    @APIResponse(responseCode = "404", description = "Not Found")
    @Path(value = "/users/{login}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(USER)
    public Response getUser(@PathParam("login") String login) {
        log.debug("REST request to get User : {}", login);
        return userService.getUserWithAuthoritiesByLogin(login)
                .map(UserDTO::new)
                .map(userDTO -> Response.ok(userDTO).build())
                .orElse(Response.status(NOT_FOUND).build());
    }

    /**
     * DELETE /users/:login : delete the "login" User.
     *
     * @param login the login of the user to delete
     * @return the Response with status 200 (OK)
     */
    @Timed
    @Operation(summary = "remove the user")
    @APIResponse(responseCode = "200", description = "OK")
    @APIResponse(responseCode = "404", description = "Not Found")
    @Path(value = "/users/{login}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    public Response deleteUser(@PathParam("login") String login) {
        log.debug("REST request to delete User: {}", login);
        userService.deleteUser(login);
        return HeaderUtil.createAlert(Response.ok(), "userManagement.deleted", login).build();
    }
}
