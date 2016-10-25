package server;

import common.Server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main{
    public static void main(String args[]) {
        /*if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }*/

        //System.setProperty("javax.net.ssl.keyStore", "C:\\Users\\FajQa\\IdeaProjects\\CopyCat\\CopyCatServer\\ServerKeystore");
        //System.setProperty("javax.net.ssl.keyStorePassword", "CopyCat");

        try{
            Server stub = new ServerImpl(0);//, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            Registry registry = LocateRegistry.getRegistry(Registry.REGISTRY_PORT);
            registry.rebind("SERVER", stub);
            System.out.println("Server bound in registry");

        }catch(Exception e){
            e.printStackTrace();
        }

        /*System.setProperty("java.security.policy", "C:\\Users\\FajQa\\IdeaProjects\\CopyCat\\CopyCatServer\\rsc\\server.policy");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            client.common.Server e = new server.ServerImpl();
            LocateRegistry.createRegistry(1099);
            Naming.rebind("client.common.Server", e);
        } catch (Exception x) {
            System.out.println(x.toString());
        }*/
    }
}
