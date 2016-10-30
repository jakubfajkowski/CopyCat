package client;

import com.healthmarketscience.rmiio.GZIPRemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.healthmarketscience.rmiio.RemoteInputStreamServer;
import common.FileInfo;
import common.Server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileTransferController {
    private Client client;
    private Server server;

    public void syncFiles() throws IOException {
        for (FileInfo fileInfo: client.getFileList()) {
            if (server.isModified(fileInfo)) {
                server.sendFile(fileInfo, sendFile(fileInfo));
            }
        }
    }

    private RemoteInputStream sendFile(FileInfo fileInfo) throws IOException {
        RemoteInputStreamServer remoteInputStreamServer = null;
        String path = fileInfo.getPath().toString();

        try {
            remoteInputStreamServer = new GZIPRemoteInputStream(new BufferedInputStream(
                    new FileInputStream(path)));
            RemoteInputStream result = remoteInputStreamServer.export();
            remoteInputStreamServer = null;
            return result;
        } finally {
            if(remoteInputStreamServer != null) remoteInputStreamServer.close();
        }
    }

    public void retrieveBackup(FileInfo fileInfo) throws IOException {
        if (server.isModified(fileInfo)) {
            getFile(fileInfo, server.getFile(fileInfo));
        }
    }

    private void getFile(FileInfo fileInfo, RemoteInputStream remoteInputStream) throws IOException {
        InputStream inputStream= RemoteInputStreamClient.wrap(remoteInputStream);
        Path target = fileInfo.getPath();

        Files.copy(inputStream, target, REPLACE_EXISTING);
        Files.setLastModifiedTime(target, FileTime.fromMillis(server.getFileInfo(fileInfo).getLastModified().getTime()));
        inputStream.close();
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
