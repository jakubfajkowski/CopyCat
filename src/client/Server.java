package client;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    boolean login(ClientCredentials clientCredentials) throws RemoteException;
    String register(ClientCredentials clientCredentials) throws RemoteException;
    void storeFile(File file) throws RemoteException;
    void retrieveFile(String filePath) throws RemoteException;
}
