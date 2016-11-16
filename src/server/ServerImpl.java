package server;

import common.ClientCredentials;
import common.RemoteSession;
import common.Server;
import server.services.AuthorizationServiceImpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements Server {
    private AuthorizationServiceImpl authorizationService = new AuthorizationServiceImpl();

    protected ServerImpl() throws RemoteException {
    }

    @Override
    public RemoteSession login(ClientCredentials clientCredentials) throws RemoteException {
        boolean isValid = authorizationService.login(clientCredentials);

        System.out.println("login " + clientCredentials.getUsername() + " " + isValid);

        return isValid ? new RemoteSessionImpl(clientCredentials) : null;
    }

    @Override
    public boolean register(ClientCredentials clientCredentials) throws RemoteException {
        boolean response = authorizationService.register(clientCredentials);

        System.out.println("register " + clientCredentials.getUsername() + " " + response);

        return response;
    }
}