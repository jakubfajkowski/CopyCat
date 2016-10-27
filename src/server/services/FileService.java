package server.services;

import com.healthmarketscience.rmiio.RemoteInputStream;
import common.FileInfo;

import java.io.IOException;
import java.rmi.RemoteException;

public interface FileService {
    void sendFile(FileInfo fileInfo, RemoteInputStream remoteInputStream);
    RemoteInputStream getFile(FileInfo fileInfo) throws RemoteException, IOException;
}
