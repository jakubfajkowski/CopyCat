import services.*;
import services.ClientCredentials;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    AuthorizationServiceImpl authorizationService = new AuthorizationServiceImpl();
    boolean login(ClientCredentials clientCredentials) throws RemoteException;
    String register(ClientCredentials clientCredentials) throws RemoteException;


    FileServiceImpl fileService = new FileServiceImpl();
    void storeFile(File file) throws RemoteException;
    void retrieveFile(String filePath) throws RemoteException;
}
