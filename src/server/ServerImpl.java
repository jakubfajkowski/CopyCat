package server;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteOutputStream;
import common.FileInfo;
import common.Server;
import server.services.AuthorizationServiceImpl;
import common.ClientCredentials;
import server.services.FileServiceImpl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements Server {
    private AuthorizationServiceImpl authorizationService = new AuthorizationServiceImpl();
    private FileServiceImpl fileService = new FileServiceImpl();

    public ServerImpl(int port) throws RemoteException {
        super(port);
    }

    @Override
    public boolean login(ClientCredentials clientCredentials) throws RemoteException {
        boolean isValid = authorizationService.login(clientCredentials);

        if (isValid) {
            fileService.setUsernameRootFolderName(clientCredentials.getUsername());
        }

        System.out.println("login" + clientCredentials.getUsername() + isValid);

        return isValid;
    }

    @Override
    public String register(ClientCredentials clientCredentials) throws RemoteException {
        String response = authorizationService.register(clientCredentials);

        System.out.println("register" + clientCredentials.getUsername() + response);

        return response;
    }

    @Override
    public void sendFile(FileInfo fileInfo, RemoteInputStream remoteInputStream) throws RemoteException {
        fileService.sendFile(fileInfo, remoteInputStream);
    }

    @Override
    public void getFile(FileInfo fileInfo, RemoteOutputStream remoteOutputStream) {
        try {
            fileService.getFile(fileInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}