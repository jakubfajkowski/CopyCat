public class AuthorizationServiceImpl implements AuthorizationService {
    PropertiesManager propertiesManager = PropertiesManager.getInstance();

    @Override
    public boolean login(String login, byte[] hashPassword) {
        return isValidPassword(login, hashPassword);
    }

    private boolean isValidPassword(String login, byte[] hashPassword) {
        if(isAlreadyUsed(login))
            return propertiesManager.getProperty(login).equals(new String(hashPassword));
        else
            return false;
    }

    @Override
    public String register(String login, byte[] hashPassword) {
        String serviceCommunicate;

        if(addNewCredentialsToPropertiesFile(login, hashPassword))
            serviceCommunicate = "User successfully registered";
        else
            serviceCommunicate = "Login is already taken";

        return serviceCommunicate;
    }

    private boolean addNewCredentialsToPropertiesFile(String login, byte[] hashPassword) {
        if(!isAlreadyUsed(login)){
            propertiesManager.setProperty(login, new String(hashPassword));
            return true;
        }else
            return false;
    }

    private boolean isAlreadyUsed(String login) {
        return propertiesManager.getProperty(login) != null;
    }
}
