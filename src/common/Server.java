package common;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteOutputStream;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    boolean login(ClientCredentials clientCredentials) throws RemoteException;
    String register(ClientCredentials clientCredentials) throws RemoteException;
    void sendFile(FileInfo fileInfo, RemoteInputStream remoteInputStream) throws RemoteException;
    void getFile(FileInfo fileInfo, RemoteOutputStream remoteOutputStream) throws RemoteException;
}
