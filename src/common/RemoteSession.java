package common;

import com.healthmarketscience.rmiio.RemoteInputStream;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteSession extends Remote {
    void signOut() throws RemoteException;
    boolean isModified(FileInfo fileInfo) throws RemoteException;
    void sendFile(FileInfo fileInfo, RemoteInputStream remoteInputStream) throws RemoteException;
    FileInfo getFileInfo(FileInfo fileInfo) throws RemoteException;
    RemoteInputStream getFile(FileInfo fileInfo) throws IOException;
}
