package client.controller;

import client.Client;
import client.alert.CopyAlert;
import client.alert.ErrorAlert;
import com.healthmarketscience.rmiio.GZIPRemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.healthmarketscience.rmiio.RemoteInputStreamServer;
import common.FileInfo;
import common.RemoteSession;
import javafx.concurrent.Task;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileTransferController {
    private RemoteSession remoteSession;
    private RemoteInputStreamServer remoteInputStreamServer;
    private InputStream clientInputStream;
    private boolean copying = false;

    public FileTransferController(Client client) {
        client.getRemoteSession();
    }

    public void syncFiles(List<FileInfo> fileInfoList) throws IOException {
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                copying = true;
                for (int i = 0; i < fileInfoList.size(); i++) {
                    if (remoteSession.isModified(fileInfoList.get(i))) {
                        remoteSession.sendFile(fileInfoList.get(i), sendFile(fileInfoList.get(i)));
                        if(this.isCancelled()) break;
                    }
                    this.updateProgress(i + 1, fileInfoList.size());
                }
                return null ;
            }
        };

        CopyAlert copyAlert = new CopyAlert(task);
        task.setOnSucceeded(event -> {
            copying = false;
            copyAlert.setDone();
        });
        task.setOnCancelled(event -> {
            try {
                remoteInputStreamServer.abort();
                copying = false;
            } catch (IOException e) {
                e.printStackTrace();
            }});
        task.setOnFailed(event -> {
            try {
                copying = false;
                throw task.getException();
            } catch (Throwable throwable) {
                new ErrorAlert(throwable.getMessage());
            }
        });
        new Thread(task).start();
    }

    public void checkIfActualFiles(List<FileInfo> fileInfoList) throws IOException {
        for (FileInfo fileInfo: fileInfoList) {
            fileInfo.setActual(!remoteSession.isModified(fileInfo));
        }
    }

    private RemoteInputStream sendFile(FileInfo fileInfo) throws IOException {
        RemoteInputStreamServer server = null;
        String path = fileInfo.getPath().toString();

        try {
            server = new GZIPRemoteInputStream(new BufferedInputStream(
                    new FileInputStream(path)));
            this.remoteInputStreamServer = server;
            RemoteInputStream result = server.export();
            server = null;
            return result;
        } finally {
            if(server != null) server.close();
        }
    }

    public void retrieveBackup(FileInfo fileInfo) throws IOException {
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                fileInfo.updateObject();
                if (remoteSession.isModified(fileInfo)) {
                    copying = true;
                    getFile(fileInfo, remoteSession.getFile(fileInfo));
                } else this.done();
                this.updateProgress(1,1);
                return null ;
            }
        };

        CopyAlert copyAlert = new CopyAlert(task);
        task.setOnSucceeded(event -> {
            copying = false;
            copyAlert.setDone();
        });
        task.setOnCancelled(event -> {
            try {
                clientInputStream.close();
                copying = false;
            } catch (IOException e) {
                e.printStackTrace();
            }});
        task.setOnFailed(event -> {
            try {
                copying = false;
                throw task.getException();
            } catch (Throwable throwable) {
                new ErrorAlert(throwable.getMessage());
            }
        });
    new Thread(task).start();

    }

    private void getFile(FileInfo fileInfo, RemoteInputStream remoteInputStream) throws IOException {
        clientInputStream = RemoteInputStreamClient.wrap(remoteInputStream);
        Path target = fileInfo.getPath();

        Files.copy(clientInputStream, target, REPLACE_EXISTING);
        Files.setLastModifiedTime(target, FileTime.fromMillis(remoteSession.getFileInfo(fileInfo).getLastModified().getTime()));
        clientInputStream.close();
    }

    public void setRemoteSession(RemoteSession remoteSession) {
        this.remoteSession = remoteSession;
    }

    public boolean isCopying() {
        return copying;
    }
}
