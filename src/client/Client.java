package client;

import common.ClientCredentials;
import common.Server;

import java.rmi.*;

public class Client {
    private ClientCredentials clientCredentials;

    public Client(ClientCredentials clientCredentials){
        this.clientCredentials = clientCredentials;
        initializeServerConnection();
    }

    private void initializeServerConnection(){
        /*if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }*/

        //System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\FajQa\\IdeaProjects\\CopyCat\\CopyCatClient\\ClientTruststore");
        //System.setProperty("javax.net.ssl.trustStorePassword", "CopyCat");

        /*try {
            Registry registry = LocateRegistry.getRegistry("localhost", Registry.REGISTRY_PORT);//, new SslRMIClientSocketFactory());

            common.Server server = (common.Server) registry.lookup("common.Server");

        } catch (NotBoundException | RemoteException e) {
            e.printStackTrace();
        }*/
        //System.setProperty("java.security.policy", "C:\\Users\\FajQa\\IdeaProjects\\CopyCat\\CopyCatClient\\rsc\\client.policy");
        //System.setSecurityManager(new SecurityManager());
        try {
            Remote remote = Naming.lookup("SERVER");
            Server server = null;
            if (remote instanceof Server)
                server = (Server) remote;
            //String result = server.login("Hello server");
            //System.out.println(result);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void login(){

    }

    public void register(){

    }
}
