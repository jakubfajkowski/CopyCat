package server.services;

public interface AuthorizationService {
    boolean login(ClientCredentials clientCredentials);
    String register(ClientCredentials clientCredentials);
}
