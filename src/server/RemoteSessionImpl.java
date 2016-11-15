package server;

import com.healthmarketscience.rmiio.RemoteInputStream;
import common.FileInfo;
import common.RemoteSession;
import common.Server;
import server.services.AuthorizationServiceImpl;
import common.ClientCredentials;
import server.services.FileServiceImpl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;

public class RemoteSessionImpl extends UnicastRemoteObject implements RemoteSession, Unreferenced {
    private FileServiceImpl fileService;
    private ClientCredentials clientCredentials;

    public RemoteSessionImpl(ClientCredentials clientCredentials) throws RemoteException {
        super();
        this.clientCredentials = clientCredentials;
        this.fileService = new FileServiceImpl(clientCredentials.getUsername());
    }

    @Override
    public void signOut() throws RemoteException {
        UnicastRemoteObject.unexportObject(this, true);
        System.out.println("signOut " + clientCredentials.getUsername());
    }

    @Override
    public boolean isModified(FileInfo fileInfo) throws RemoteException {
        boolean isModified = fileService.isModified(fileInfo);
        System.out.println(clientCredentials.getUsername() + " isModified " + fileInfo.getName() + " " + isModified);
        return isModified;
    }

    @Override
    public void sendFile(FileInfo fileInfo, RemoteInputStream remoteInputStream) throws RemoteException {
        System.out.println(clientCredentials.getUsername() + " sendFile " + fileInfo.getName());
        fileService.sendFile(fileInfo, remoteInputStream);
    }

    public FileInfo getFileInfo(FileInfo fileInfo) throws RemoteException {
        System.out.println(clientCredentials.getUsername() + " getFileInfo " + fileInfo.getName());
        return fileService.getFileInfo(fileInfo);
    }

    @Override
    public RemoteInputStream getFile(FileInfo fileInfo) throws IOException  {
        System.out.println(clientCredentials.getUsername() + " getFile " + fileInfo.getName());
        return fileService.getFile(fileInfo);
    }

    @Override
    public boolean deleteFile(FileInfo fileInfo) throws RemoteException {
        System.out.println(clientCredentials.getUsername() + " deleteFile " + fileInfo.getName());
        return fileService.deleteFile(fileInfo);
    }

    @Override
    public void unreferenced() {

    }
}