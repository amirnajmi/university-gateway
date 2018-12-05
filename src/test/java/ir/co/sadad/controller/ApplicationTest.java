package ir.co.sadad.controller;

import ir.co.sadad.config.SecurityConfig;
import ir.co.sadad.controller.vm.ManagedUserVM;
import ir.co.sadad.domain.AbstractAuditingEntity;
import ir.co.sadad.domain.AuditListner;
import ir.co.sadad.domain.Authority;
import ir.co.sadad.domain.User;
import ir.co.sadad.mail.MailNotifier;
import ir.co.sadad.producer.TemplateEngineProducer;
import ir.co.sadad.repository.AuthorityRepository;
import ir.co.sadad.repository.UserRepository;
import ir.co.sadad.security.SecurityHelper;
import ir.co.sadad.service.MailService;
import ir.co.sadad.service.UserService;
import ir.co.sadad.service.dto.LoginDTO;
import ir.co.sadad.service.dto.UserDTO;
import ir.co.sadad.util.RandomUtil;
import junit.framework.AssertionFailedError;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;

import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URL;
import java.util.Map;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

/**
 * Abstract class for application packaging.
 *
 */
public abstract class ApplicationTest extends AbstractTest {

    protected static final String USERNAME = "admin";
    protected static final String PASSWORD = "admin";
    protected static final String INVALID_PASSWORD = "invalid_password";
    protected static final String INCORRECT_PASSWORD = "pw";
    protected static final String NEW_PASSWORD = "newpw";
    protected static final String INVALID_RESET_KEY = "invalid_reset_key";

    protected String tokenId;

    protected AuthenticationControllerClient authClient;

    public static WebArchive buildApplication() {
        return buildArchive()
                .addPackages(true,
                        SecurityConfig.class.getPackage(),
                        MailService.class.getPackage(),
                        MailNotifier.class.getPackage(),
                        UserDTO.class.getPackage(),
                        ManagedUserVM.class.getPackage(),
                        SecurityHelper.class.getPackage(),
                        RandomUtil.class.getPackage())
                .addClasses(
                        TemplateEngineProducer.class,
                        User.class,
                        Authority.class,
                        AbstractAuditingEntity.class,
                        AuditListner.class,
                        UserRepository.class,
                        AuthorityRepository.class,
                        UserService.class,
                        AuthenticationController.class,
                        AuthenticationControllerClient.class,
                        AbstractTest.class,
                        ApplicationTest.class,
                        ApplicationConfig.class)
                .addAsResource("META-INF/sql/insert.sql")
                .addAsResource(new File("src/main/resources/config/application-common.properties"), "META-INF/microprofile-config.properties")
                .addAsResource("i18n/messages.properties")
                .addAsResource("publicKey.pem")
                .addAsResource("privateKey.pem");
    }

    @Before
    public void setUp() throws Exception {
        try {
            authClient = buildClient(AuthenticationControllerClient.class);
        } catch (Exception ex) {
            throw new AssertionFailedError(ex.getMessage());
        }
        login(USERNAME, PASSWORD);
    }

    @After
    public void tearDown() {
        logout();
    }

    protected Response login(String username, String password) {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setPassword(password);

        Response response = authClient.login(loginDTO);
        tokenId = response.getHeaderString(AUTHORIZATION);
        return response;
    }

    protected void logout() {
        tokenId = null;
    }

    @Override
    protected Invocation.Builder target(String path) {
        return super.target(path).header(AUTHORIZATION, tokenId);
    }

    @Override
    protected Invocation.Builder target(String path, Map<String, Object> params) {
        return super.target(path, params).header(AUTHORIZATION, tokenId);
    }

    @Override
    protected <T> T buildClient(Class<? extends T> type) throws Exception {
        RestClientBuilder builder = RestClientBuilder.newBuilder();
        if (tokenId != null) {
            builder.register((ClientRequestFilter) context -> context.getHeaders().add(AUTHORIZATION, tokenId));
        }
        return builder.baseUrl(new URL(deploymentUrl.toURI().toString() + "resources/"))
                .build(type);
    }

}
