package client.controller;

import com.healthmarketscience.rmiio.GZIPRemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.healthmarketscience.rmiio.RemoteInputStreamServer;
import common.FileInfo;
import common.Server;

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
    private Server server;

    public void syncFiles(List<FileInfo> fileInfoList) throws IOException {
        for (FileInfo fileInfo: fileInfoList) {
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

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
