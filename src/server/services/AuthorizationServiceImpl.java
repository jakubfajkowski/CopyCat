package server.services;

import common.ClientCredentials;
import common.PropertiesManager;

public class AuthorizationServiceImpl implements AuthorizationService {
    private PropertiesManager propertiesManager = PropertiesManager.getInstance();

    public AuthorizationServiceImpl() {
        propertiesManager.setFileName("credentials");
    }

    @Override
    public boolean login(ClientCredentials clientCredentials) {
        return isValidPassword(clientCredentials);
    }

    private boolean isValidPassword(ClientCredentials clientCredentials) {
        String username = clientCredentials.getUsername();
        byte[] hashPassword = clientCredentials.getHashPassword();

        if(isAlreadyUsed(username))
            return propertiesManager.getProperty(username).equals(new String(hashPassword));
        else
            return false;
    }

    @Override
    public String register(ClientCredentials clientCredentials) {
        String serviceCommunicate;

        if(addNewCredentialsToPropertiesFile(clientCredentials))
            serviceCommunicate = "User successfully registered";
        else
            serviceCommunicate = "Login is already taken";

        return serviceCommunicate;
    }

    private boolean addNewCredentialsToPropertiesFile(ClientCredentials clientCredentials) {
        String username = clientCredentials.getUsername();
        byte[] hashPassword = clientCredentials.getHashPassword();

        if(!isAlreadyUsed(username)){
            propertiesManager.setProperty(username, new String(hashPassword));
            return true;
        }else
            return false;
    }

    private boolean isAlreadyUsed(String login) {
        return propertiesManager.getProperty(login) != null;
    }
}
