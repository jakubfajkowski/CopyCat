package server;

import com.healthmarketscience.rmiio.RemoteInputStream;
import common.FileInfo;
import common.RemoteSession;
import common.Server;
import server.services.AuthorizationServiceImpl;
import common.ClientCredentials;
import server.services.FileServiceImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.concurrent.locks.ReentrantLock;

public class RemoteSessionImpl extends UnicastRemoteObject implements RemoteSession, Unreferenced {
    private ReentrantLock reentrantLock = new ReentrantLock();
    private FileServiceImpl fileService;
    private ClientCredentials clientCredentials;

    public RemoteSessionImpl(ClientCredentials clientCredentials) throws RemoteException {
        super();
        this.clientCredentials = clientCredentials;
        this.fileService = new FileServiceImpl(clientCredentials.getUsername());
    }

    @Override
    public void signOut() throws RemoteException {
        reentrantLock.lock();
        try {
            UnicastRemoteObject.unexportObject(this, true);
            System.out.println("signOut " + clientCredentials.getUsername());
        }
        finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public boolean isModified(FileInfo fileInfo) throws FileNotFoundException, RemoteException {
        reentrantLock.lock();
        try {
            boolean isModified = fileService.isModified(fileInfo);
            System.out.println(clientCredentials.getUsername() + " isModified " + fileInfo.getName() + " " + isModified);
            return isModified;
        }
        finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public boolean sendFile(FileInfo fileInfo, RemoteInputStream remoteInputStream) throws RemoteException {
        reentrantLock.lock();
        try {
            System.out.println(clientCredentials.getUsername() + " sendFile " + fileInfo.getName());
            return fileService.sendFile(fileInfo, remoteInputStream);
        }
        finally {
                reentrantLock.unlock();
        }
    }

    public FileInfo getFileInfo(FileInfo fileInfo) throws RemoteException {
        reentrantLock.lock();
        try {
            System.out.println(clientCredentials.getUsername() + " getFileInfo " + fileInfo.getName());
            return fileService.getFileInfo(fileInfo);
        }
        finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public RemoteInputStream getFile(FileInfo fileInfo) throws IOException  {
        reentrantLock.lock();
        try {
            System.out.println(clientCredentials.getUsername() + " getFile " + fileInfo.getName());
            return fileService.getFile(fileInfo);
        }
        finally {
        reentrantLock.unlock();
        }
    }

    @Override
    public boolean deleteFile(FileInfo fileInfo) throws RemoteException {
        reentrantLock.lock();
        try {
            System.out.println(clientCredentials.getUsername() + " deleteFile " + fileInfo.getName());
            return fileService.deleteFile(fileInfo);
        }
        finally {
        reentrantLock.unlock();
        }
    }

    @Override
    public void unreferenced() {

    }
}