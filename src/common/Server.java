package common;

import com.healthmarketscience.rmiio.RemoteInputStream;

import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    boolean login(ClientCredentials clientCredentials) throws RemoteException;
    String register(ClientCredentials clientCredentials) throws RemoteException;
    boolean isModified(FileInfo fileInfo) throws RemoteException;
    void sendFile(FileInfo fileInfo, RemoteInputStream remoteInputStream) throws RemoteException;
    FileInfo getFileInfo(FileInfo fileInfo) throws RemoteException;
    RemoteInputStream getFile(FileInfo fileInfo) throws IOException;
}
