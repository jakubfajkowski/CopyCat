package client.controller;

import client.Client;
import common.ClientCredentials;
import common.FileInfo;
import common.RemoteSession;
import common.Server;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by FajQa on 28.10.2016.
 */
public class FileTransferControllerTest {
    private boolean isServerUp = false;
    @Test
    public void syncFiles() throws Exception {
        prepareServer();

        String username = "test_rsc/client";
        String password = "password";
        ClientCredentials clientCredentials = new ClientCredentials(username, password);

        List<FileInfo> fileInfos = new ArrayList<>();
        fileInfos.add(new FileInfo(new File(getClass().getResource("fileTransferTestFile1.txt").toURI())));
        fileInfos.add(new FileInfo(new File(getClass().getResource("fileTransferTestFile2.txt").toURI())));

        Client client = new Client();
        client.setClientCredentials(clientCredentials);
        client.setFileList(fileInfos);

        Server server = initializeServerConnection("localhost", Registry.REGISTRY_PORT);
        server.register(clientCredentials);
        RemoteSession remoteSession = server.login(clientCredentials);

        FileTransferController fileTransferController = new FileTransferController();
        fileTransferController.setRemoteSession(remoteSession);

        server.register(clientCredentials);
        server.login(clientCredentials);

        fileTransferController.syncFiles(client.getFileList());

        for (FileInfo fileInfo: fileInfos) {
            assertTrue(fileInfo.equals(new FileInfo(new File(getServerFilePath(fileInfo.getPath()).toString()))));
        }

        clean(getServerFilePath(fileInfos.get(0).getPath()).subpath(0,3));
    }

    @Test
    public void retrieveBackup() throws Exception {
        prepareServer();

        String username = "test_rsc/client";
        String password = "password";
        ClientCredentials clientCredentials = new ClientCredentials(username, password);

        List<FileInfo> fileInfos = new ArrayList<>();
        fileInfos.add(new FileInfo(new File(getClass().getResource("fileTransferTestFile1.txt").toURI())));
        fileInfos.add(new FileInfo(new File(getClass().getResource("fileTransferTestFile2.txt").toURI())));

        Client client = new Client();
        client.setClientCredentials(clientCredentials);
        client.setFileList(fileInfos);

        Server server = initializeServerConnection("localhost", Registry.REGISTRY_PORT);
        server.register(clientCredentials);
        RemoteSession remoteSession = server.login(clientCredentials);

        FileTransferController fileTransferController = new FileTransferController();
        fileTransferController.setRemoteSession(remoteSession);

        server.register(clientCredentials);
        server.login(clientCredentials);

        fileTransferController.syncFiles(client.getFileList());

        for (FileInfo fileInfo: fileInfos) {
            fileTransferController.retrieveBackup(fileInfo);
            assertTrue(fileInfo.equals(new FileInfo(getServerFilePath(fileInfo.getPath()).toFile())));
        }

        clean(getServerFilePath(fileInfos.get(0).getPath()).subpath(0,3));
    }

    private void prepareServer() {
        if (!isServerUp)
            server.Main.main(null);
        isServerUp = true;
    }

    private Server initializeServerConnection(String ip, int port) throws RemoteException, NotBoundException {
        //System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\FajQa\\IdeaProjects\\CopyCat\\rsc\\client\\ClientTruststore");
        //System.setProperty("javax.net.ssl.trustStorePassword", "CopyCat");

        Registry registry = LocateRegistry.getRegistry(ip, port);//, new SslRMIClientSocketFactory());

        return (Server) registry.lookup("SERVER");
    }

    private Path getServerFilePath(Path originalPath) {
        return Paths.get("test_rsc/client" + "/"
                + originalPath.getRoot().toString().replace(":","")
                + originalPath.subpath(1, originalPath.getNameCount()));
    }

    private void clean(Path path){
        try {
            delete(path.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                delete(c);
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }
}