package server.services;

import com.healthmarketscience.rmiio.GZIPRemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.healthmarketscience.rmiio.RemoteInputStreamServer;
import common.FileInfo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileServiceImpl implements FileService {
    private String usernameRootFolderName = "public";

    @Override
    public boolean isModified(FileInfo fileInfo) {
        Path serverFilePath = getServerFilePath(fileInfo.getPath());
        FileInfo serverFileInfo = new FileInfo(serverFilePath.toFile());

        return !serverFileInfo.equals(fileInfo);
    }

    private Path getServerFilePath(Path originalPath) {
        return Paths.get(usernameRootFolderName + "/"
                + originalPath.getRoot().toString().replace(":","")
                + originalPath.subpath(1, originalPath.getNameCount()));
    }

    private void createServerFileDirectory(Path serverFilePath) throws IOException {
        Files.createDirectories(serverFilePath.getParent());
    }

    @Override
    public void sendFile(FileInfo fileInfo, RemoteInputStream remoteInputStream) {
        try {
            InputStream inputStream= RemoteInputStreamClient.wrap(remoteInputStream);
            Path target = getServerFilePath(fileInfo.getPath());

            createServerFileDirectory(target);
            Files.copy(inputStream, target, REPLACE_EXISTING);
            Files.setLastModifiedTime(target, FileTime.fromMillis(fileInfo.getLastModified().getTime()));
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FileInfo getFileInfo(FileInfo fileInfo) {
        return new FileInfo(getServerFilePath(fileInfo.getPath()).toFile());
    }

    @Override
    public RemoteInputStream getFile(FileInfo fileInfo) throws IOException {
        RemoteInputStreamServer remoteInputStreamServer = null;
        String path = getServerFilePath(fileInfo.getPath()).toString();

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

    public void setUsernameRootFolderName(String usernameRootFolderName) {
        this.usernameRootFolderName = usernameRootFolderName;
    }
}
