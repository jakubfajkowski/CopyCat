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
import java.util.ArrayList;
import java.util.List;

public class ServerImpl extends UnicastRemoteObject implements Server {
    private AuthorizationServiceImpl authorizationService = new AuthorizationServiceImpl();
    private List<RemoteSession> remoteSessions = new ArrayList<>();

    protected ServerImpl() throws RemoteException {
    }

    @Override
    public RemoteSession login(ClientCredentials clientCredentials) throws RemoteException {
        boolean isValid = authorizationService.login(clientCredentials);

        System.out.println("login " + clientCredentials.getUsername() + " " + isValid);
        RemoteSession remoteSession = isValid ? new RemoteSessionImpl(clientCredentials) : null;
        remoteSessions.add(remoteSession);
        return remoteSession;
    }

    @Override
    public boolean register(ClientCredentials clientCredentials) throws RemoteException {
        boolean response = authorizationService.register(clientCredentials);

        System.out.println("register " + clientCredentials.getUsername() + " " + response);

        return response;
    }
}