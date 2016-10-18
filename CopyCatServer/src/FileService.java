import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileService extends Remote {
    void storeFile(File file) throws RemoteException;
    void retrieveFile(String filePath) throws RemoteException;
}
