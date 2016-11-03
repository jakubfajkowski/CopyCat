package server;

import com.healthmarketscience.rmiio.RemoteInputStream;
import common.FileInfo;
import common.Server;
import server.services.AuthorizationServiceImpl;
import common.ClientCredentials;
import server.services.FileServiceImpl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements Server {
    private AuthorizationServiceImpl authorizationService = new AuthorizationServiceImpl();
    private FileServiceImpl fileService = new FileServiceImpl();
    private String clientUsername = "public";

    public ServerImpl(int port) throws RemoteException {
        super(port);
    }

    @Override
    public boolean login(ClientCredentials clientCredentials) throws RemoteException {
        boolean isValid = authorizationService.login(clientCredentials);

        if (isValid) {
            clientUsername = clientCredentials.getUsername();
            fileService.setUsernameRootFolderName(clientUsername);
        }

        System.out.println("login " + clientCredentials.getUsername() + " " + isValid);

        return isValid;
    }

    @Override
    public void signOut() throws RemoteException {
        System.out.println("signOut " + clientUsername);
        clientUsername = "public";
        fileService.setUsernameRootFolderName(clientUsername);
    }

    @Override
    public boolean register(ClientCredentials clientCredentials) throws RemoteException {
        boolean response = authorizationService.register(clientCredentials);

        System.out.println("register " + clientCredentials.getUsername() + " " + response);

        return response;
    }

    @Override
    public boolean isModified(FileInfo fileInfo) throws RemoteException {
        boolean isModified = fileService.isModified(fileInfo);
        System.out.println("isModified " + fileInfo.getName() + " " + isModified);
        return isModified;
    }

    @Override
    public void sendFile(FileInfo fileInfo, RemoteInputStream remoteInputStream) throws RemoteException {
        System.out.println("sendFile " + fileInfo.getName());
        fileService.sendFile(fileInfo, remoteInputStream);
    }

    public FileInfo getFileInfo(FileInfo fileInfo) throws RemoteException {
        System.out.println("getFileInfo " + fileInfo.getName());
        return fileService.getFileInfo(fileInfo);
    }

    @Override
    public RemoteInputStream getFile(FileInfo fileInfo) throws IOException  {
        System.out.println("getFile " + fileInfo.getName());
        return fileService.getFile(fileInfo);
    }
}