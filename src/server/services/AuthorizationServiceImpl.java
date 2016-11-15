package server.services;

import common.ClientCredentials;
import common.PropertiesManager;

public class AuthorizationServiceImpl implements AuthorizationService {
    private PropertiesManager propertiesManager = PropertiesManager.getInstance();

    public AuthorizationServiceImpl() {
        propertiesManager.setFileName("credentials");
        propertiesManager.load();
    }

    @Override
    public boolean login(ClientCredentials clientCredentials) {
        return isValidPassword(clientCredentials);
    }

    private boolean isValidPassword(ClientCredentials clientCredentials) {
        String username = clientCredentials.getUsername();
        byte[] hashPassword = clientCredentials.getHashPassword();

        return isUsed(username) && propertiesManager.getProperty(username).equals(new String(hashPassword));
    }

    @Override
    public boolean register(ClientCredentials clientCredentials) {
        return addNewCredentialsToPropertiesFile(clientCredentials);
    }

    private boolean addNewCredentialsToPropertiesFile(ClientCredentials clientCredentials) {
        String username = clientCredentials.getUsername();
        byte[] hashPassword = clientCredentials.getHashPassword();

        if(!isUsed(username)){
            propertiesManager.setProperty(username, new String(hashPassword));
            return true;
        }else
            return false;
    }

    private boolean isUsed(String login) {
        return propertiesManager.getProperty(login) != null;
    }
}
