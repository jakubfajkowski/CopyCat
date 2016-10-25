package server;

import common.Server;
import server.services.AuthorizationServiceImpl;
import common.ClientCredentials;
import server.services.FileServiceImpl;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements Server {
    private AuthorizationServiceImpl authorizationService = new AuthorizationServiceImpl();
    private FileServiceImpl fileService = new FileServiceImpl();

    protected ServerImpl() throws RemoteException {
        super();
    }

    public ServerImpl(int port) throws RemoteException {
        super(port);
    }

    public ServerImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public boolean login(ClientCredentials clientCredentials) throws RemoteException {
        return authorizationService.login(clientCredentials);
    }

    @Override
    public String register(ClientCredentials clientCredentials) throws RemoteException {
        return authorizationService.register(clientCredentials);
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