package server;

import common.Server;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main{
    public static void main(String args[]) {
        //System.setProperty("javax.net.ssl.keyStore", "C:\\Users\\FajQa\\IdeaProjects\\CopyCat\\rsc\\server\\server.keystore");
        //System.setProperty("javax.net.ssl.keyStorePassword", "CopyCat");

        try{
            Server stub = new ServerImpl();//, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            Registry registry = LocateRegistry.getRegistry(Registry.REGISTRY_PORT);
            registry.rebind("SERVER", stub);
            System.out.println("Server bound in registry");

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
