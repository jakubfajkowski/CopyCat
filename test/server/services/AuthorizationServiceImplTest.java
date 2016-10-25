package server.services;

import common.ClientCredentials;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuthorizationServiceImplTest {
    @Test
    public void login() throws Exception {
        AuthorizationServiceImpl authorizationService = new AuthorizationServiceImpl();

        String validUsername = "login";
        String invalidUsername = "invalidUsername";
        String validPassword = "password";
        String invalidPassword = "invalidPassword";

        assertTrue(authorizationService.login(new ClientCredentials(validUsername, validPassword)));
        assertFalse(authorizationService.login(new ClientCredentials(invalidUsername, validPassword)));
        assertFalse(authorizationService.login(new ClientCredentials(validUsername, invalidPassword)));
        assertFalse(authorizationService.login(new ClientCredentials(invalidUsername, invalidPassword)));



    }

    @Test
    public void register() throws Exception {
        AuthorizationServiceImpl authorizationService = new AuthorizationServiceImpl();

        String login = "login";
        String password = "password";

        Assert.assertEquals("User successfully registered", authorizationService.register(new ClientCredentials(login, password)));
        Assert.assertEquals("Login is already taken", authorizationService.register(new ClientCredentials(login, password)));

    }
}