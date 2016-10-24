import services.ClientCredentials;

import java.io.File;
import java.rmi.RemoteException;

public class ServerImpl implements Server {
    @Override
    public boolean login(ClientCredentials clientCredentials) throws RemoteException {
        return authoriztionService.login(clientCredentials);
    }

    @Override
    public String register(ClientCredentials clientCredentials) throws RemoteException {
        return authoriztionService.register(clientCredentials);
    }

    @Override
    public void storeFile(File file) throws RemoteException {
        fileService.storeFile(file);
    }

    @Override
    public void retrieveFile(String filePath) throws RemoteException {
        fileService.retrieveFile(filePath);
    }
}