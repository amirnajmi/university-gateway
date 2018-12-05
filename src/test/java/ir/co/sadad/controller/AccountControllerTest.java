package ir.co.sadad.controller;

import ir.co.sadad.controller.vm.KeyAndPasswordVM;
import ir.co.sadad.controller.vm.ManagedUserVM;
import ir.co.sadad.controller.vm.PasswordChangeVM;
import ir.co.sadad.domain.User;
import ir.co.sadad.repository.AuthorityRepository;
import ir.co.sadad.repository.UserRepository;
import ir.co.sadad.security.AuthoritiesConstants;
import ir.co.sadad.service.dto.UserDTO;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static ir.co.sadad.security.AuthoritiesConstants.ADMIN;
import static ir.co.sadad.security.AuthoritiesConstants.USER;
import static java.util.Collections.singleton;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.valid4j.matchers.http.HttpResponseMatchers.hasStatus;

/**
 * Test class for the AccountController REST controller.
 *
 */
@RunWith(Arquillian.class)
public class AccountControllerTest extends ApplicationTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    private AccountControllerClient client;

    @Deployment
    public static WebArchive createDeployment() {
        return buildApplication()
                .addClass(AccountController.class)
                .addClass(AccountControllerClient.class);
    }

    @Before
    public void buildClient() throws Exception {
        client = buildClient(AccountControllerClient.class);
    }

    @Test
    public void testGetExistingAccount() {
        Response response = client.getAccount();
        assertThat(response, hasStatus(OK));
        UserDTO user = response.readEntity(UserDTO.class);
        assertNotNull(user);
        assertThat(user.getLogin(), is(USERNAME));
    }

    @Test
    public void testGetUnknownAccount() throws Exception {
        logout();
        assertWebException(UNAUTHORIZED, () -> client.getAccount());
    }

    @Test
    public void testRegisterValid() throws Exception {
        ManagedUserVM validUser = new ManagedUserVM(
                null, // id
                "joe", // login
                "password", // password
                "Joe", // firstName
                "Shmoe", // lastName
                "joe@example.com", // e-mail
                true, // activated
                "en", // langKey
                null, // createdBy
                null, // createdDate
                null, // lastModifiedBy
                null, // lastModifiedDate
                singleton(USER) // authorities
        );

        Response response = client.registerAccount(validUser);
        assertThat(response, hasStatus(CREATED));

        Optional<User> user = userRepository.findOneByLogin("joe");
        assertTrue(user.isPresent());
    }

    @Test
    public void testRegisterDuplicateLogin() throws Exception {
        // Good
        ManagedUserVM validUser = new ManagedUserVM(
                null, // id
                "alice", // login
                "password", // password
                "Alice", // firstName
                "Something", // lastName
                "alice@example.com", // e-mail
                true, // activated
                "en", // langKey
                null, // createdBy
                null, // createdDate
                null, // lastModifiedBy
                null, // lastModifiedDate
                singleton(USER) // authorities
        );

        // Duplicate login, different e-mail
        ManagedUserVM duplicatedUser = new ManagedUserVM(
                validUser.getId(), validUser.getLogin(), validUser.getPassword(),
                validUser.getFirstName(), validUser.getLastName(),
                "alicejr@example.com", validUser.isActivated(), validUser.getLangKey(),
                validUser.getCreatedBy(), validUser.getCreatedDate(),
                validUser.getLastModifiedBy(), validUser.getLastModifiedDate(),
                validUser.getAuthorities()
        );

        // Good user
        Response response = client.registerAccount(validUser);
        assertThat(response, hasStatus(CREATED));

        // Duplicate login
        assertWebException(BAD_REQUEST, () -> client.registerAccount(duplicatedUser));

        Optional<User> userDup = userRepository.findOneByEmail("alicejr@example.com");
        assertFalse(userDup.isPresent());
    }

    @Test
    public void testRegisterDuplicateEmail() throws Exception {
        // Good
        ManagedUserVM validUser = new ManagedUserVM(
                null, // id
                "john", // login
                "password", // password
                "John", // firstName
                "Doe", // lastName
                "john@example.com", // e-mail
                true, // activated
                "en", // langKey
                null, // createdBy
                null, // createdDate
                null, // lastModifiedBy
                null, // lastModifiedDate
                singleton(USER) // authorities
        );

        // Duplicate e-mail, different login
        ManagedUserVM duplicatedUser = new ManagedUserVM(
                validUser.getId(), "johnjr", validUser.getPassword(),
                validUser.getFirstName(), validUser.getLastName(),
                validUser.getEmail(), true, validUser.getLangKey(),
                validUser.getCreatedBy(), validUser.getCreatedDate(),
                validUser.getLastModifiedBy(), validUser.getLastModifiedDate(),
                validUser.getAuthorities()
        );

        // Good user
        Response response = client.registerAccount(validUser);
        assertThat(response, hasStatus(CREATED));

        // Duplicate  e-mail
        assertWebException(BAD_REQUEST, () -> client.registerAccount(duplicatedUser));

        Optional<User> userDup = userRepository.findOneByLogin("johnjr");
        assertFalse(userDup.isPresent());
    }

    @Test
    public void testRegisterAdminIsIgnored() throws Exception {
        ManagedUserVM validUser = new ManagedUserVM(
                null, // id
                "badguy", // login
                "password", // password
                "Bad", // firstName
                "Guy", // lastName
                "badguy@example.com", // e-mail
                true, // activated
                "en", // langKey
                null, // createdBy
                null, // createdDate
                null, // lastModifiedBy
                null, // lastModifiedDate
                singleton(ADMIN) // authorities
        );

        Response response = client.registerAccount(validUser);
        assertThat(response, hasStatus(CREATED));

        Optional<User> userDup = userRepository.findOneByLogin("badguy");
        assertTrue(userDup.isPresent());
        assertThat(userDup.get().getAuthorities().size(), is(1));
        assertThat(userDup.get().getAuthorities(), hasItems(authorityRepository.find(AuthoritiesConstants.USER)));

    }

    @Test
    public void assertThatOnlyActivatedUserCanRequestPasswordReset() {
        ManagedUserVM user = new ManagedUserVM(
                null, // id
                "gaurav", // login
                "password", // password
                "Gaurav", // firstName
                "Gupta", // lastName
                "gaurav.gupta.jc@example.com", // e-mail
                true, // activated
                "en", // langKey
                null, // createdBy
                null, // createdDate
                null, // lastModifiedBy
                null, // lastModifiedDate
                singleton(USER) // authorities
        );

        Response response = client.registerAccount(user);
        assertThat(response, hasStatus(CREATED));

        assertWebException(BAD_REQUEST, () -> client.requestPasswordReset("gaurav.gupta.jc@example.com"));
    }

    @Test
    public void assertThatUserMustExistToResetPassword() {
        assertWebException(BAD_REQUEST, () -> client.requestPasswordReset("john.doe@example.com"));

        Response response = client.requestPasswordReset("admin@example.com");
        assertThat(response, hasStatus(OK));
    }

    @Test
    public void testfinishPasswordReset() {
        KeyAndPasswordVM keyAndPasswordVM = new KeyAndPasswordVM();
        keyAndPasswordVM.setKey(INVALID_RESET_KEY);
        keyAndPasswordVM.setNewPassword(PASSWORD);
        assertWebException(INTERNAL_SERVER_ERROR, () -> client.finishPasswordReset(keyAndPasswordVM));

        keyAndPasswordVM.setNewPassword(INCORRECT_PASSWORD);
        assertWebException(BAD_REQUEST, () -> client.finishPasswordReset(keyAndPasswordVM));
    }

    @Test
    public void testSaveAccount() throws Exception {
        Response response = client.getAccount();
        assertThat(response, hasStatus(OK));
        ManagedUserVM user = response.readEntity(ManagedUserVM.class);
        user.setLastName("Gupta");

        response = client.saveAccount(user);
        assertThat(response, hasStatus(OK));
    }

    @Test
    public void testSaveUserDuplicateEmail() throws Exception {
        Response response = client.getAccount();
        assertThat(response, hasStatus(OK));
        ManagedUserVM user = response.readEntity(ManagedUserVM.class);
        user.setEmail("user@example.com");
        assertWebException(BAD_REQUEST, () -> client.saveAccount(user));
    }

    @Test
    public void testChangePassword() throws Exception {
        Response response;
        PasswordChangeVM passwordChangeVM = new PasswordChangeVM();

        //Invalid password
        passwordChangeVM.setCurrentPassword(PASSWORD);
        passwordChangeVM.setNewPassword(INCORRECT_PASSWORD);
        assertWebException(BAD_REQUEST, () -> client.changePassword(passwordChangeVM));

        //Valid password
        passwordChangeVM.setNewPassword(NEW_PASSWORD);
        response = client.changePassword(passwordChangeVM);
        assertThat(response, hasStatus(OK));

        //Valid password
        passwordChangeVM.setCurrentPassword(NEW_PASSWORD);
        passwordChangeVM.setNewPassword(PASSWORD);
        response = client.changePassword(passwordChangeVM);
        assertThat(response, hasStatus(OK));
    }

    @Test
    public void testActivateAccountInvalidResetKey() throws Exception {
        //Invalid Reset Key
        assertWebException(INTERNAL_SERVER_ERROR, () -> client.activateAccount(INVALID_RESET_KEY));
    }

}
