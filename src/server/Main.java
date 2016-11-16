package server;

import common.Server;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

public class Main{
    public static void main(String args[]) {
        //System.setProperty("javax.net.ssl.keyStore", "C:\\Users\\FajQa\\IdeaProjects\\CopyCat\\rsc\\server\\server.keystore");
        //System.setProperty("javax.net.ssl.keyStorePassword", "CopyCat");
        int port = (args.length > 0) ? Integer.valueOf(args[0]) : Registry.REGISTRY_PORT;
        try{
            Server stub = new ServerImpl();//, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());

            LocateRegistry.createRegistry(port);
            Registry registry = LocateRegistry.getRegistry(port);
            registry.rebind("SERVER", stub);
            System.out.println("server bound in registry (port: " + port + ")");

        } catch(ExportException e){
            System.out.println("server is already up on port: " + port);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
