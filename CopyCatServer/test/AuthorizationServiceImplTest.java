import org.junit.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class AuthorizationServiceImplTest {
    @Test
    public void login() throws Exception {
        AuthorizationServiceImpl authorizationService = new AuthorizationServiceImpl();

        String validLogin = "login";
        String invalidLogin = "invalidLogin";
        byte[] validPassword = hashPassword("password");
        byte[] invalidPassword = hashPassword("invalidPassword");
        assertTrue(authorizationService.login(validLogin, validPassword));
        assertFalse(authorizationService.login(validLogin, invalidPassword));
        assertFalse(authorizationService.login(invalidLogin, invalidPassword));
        assertFalse(authorizationService.login(invalidLogin, validPassword));



    }

    @Test
    public void register() throws Exception {
        AuthorizationServiceImpl authorizationService = new AuthorizationServiceImpl();

        String login = "login";
        byte[] password = hashPassword("password");

        assertEquals("User successfully registered", authorizationService.register(login, password));
        assertEquals("Login is already taken", authorizationService.register(login, password));

    }

    private byte[] hashPassword(String password) throws NoSuchAlgorithmException {
        return  MessageDigest.getInstance("MD5").digest(password.getBytes());
    }
}