import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server /*implements Hello*/ {

    /*public Server() {}

    public synchronized String sayHello(String text) {
        return "Hello " + text + "!";
    }

    public static void main(String args[]) throws UnknownHostException {
        try {
            System.setProperty("javax.net.ssl.keyStore", "C:\\Users\\FajQa\\ServerKeyStore");
            System.setProperty("javax.net.ssl.keyStorePassword", "CopyCat");
            Server obj = new Server();
            Hello stub = (Hello) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
            registry.bind("Hello", stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }*/
}