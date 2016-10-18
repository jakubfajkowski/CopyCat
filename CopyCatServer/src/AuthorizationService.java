import java.rmi.RemoteException;

public interface AuthorizationService {
    boolean login(String login, byte[] hashPassword) throws RemoteException;
    String register(String login, byte[] hashPassword) throws RemoteException;
}
