package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    RemoteSession login(ClientCredentials clientCredentials) throws RemoteException;
    boolean register(ClientCredentials clientCredentials) throws RemoteException;
}
