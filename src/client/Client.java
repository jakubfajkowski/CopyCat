package client;

import common.ClientCredentials;
import common.Server;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    private ClientCredentials clientCredentials;
    private Server server;

    public Client(ClientCredentials clientCredentials) throws RemoteException, NotBoundException {
        this.clientCredentials = clientCredentials;
        initializeServerConnection();
    }

    private void initializeServerConnection() throws RemoteException, NotBoundException {
        //System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\FajQa\\IdeaProjects\\CopyCat\\rsc\\client\\ClientTruststore");
        //System.setProperty("javax.net.ssl.trustStorePassword", "CopyCat");

        Registry registry = LocateRegistry.getRegistry("localhost", Registry.REGISTRY_PORT);//, new SslRMIClientSocketFactory());

        server = (Server) registry.lookup("SERVER");
    }

    public boolean login() throws RemoteException {
        return server.login(clientCredentials);
    }

    public String register() throws RemoteException {
        return server.register(clientCredentials);
    }

    public String getUsername() {
        return clientCredentials.getUsername();
    }
}
