package server.services;

import com.healthmarketscience.rmiio.GZIPRemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.healthmarketscience.rmiio.RemoteInputStreamServer;
import common.ClientCredentials;
import common.FileInfo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileServiceImpl implements FileService {
    private String usernameRootFolderName;

    public FileServiceImpl(String username) {
        usernameRootFolderName = username;
    }

    @Override
    public boolean isModified(FileInfo fileInfo) throws FileNotFoundException {
        Path serverFilePath = getServerFilePath(fileInfo.getPath());
        File serverFile = serverFilePath.toFile();
        if (!serverFile.exists())
            throw new FileNotFoundException();
        FileInfo serverFileInfo = new FileInfo(serverFile);

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
    public boolean sendFile(FileInfo fileInfo, RemoteInputStream remoteInputStream) {
        Path target = getServerFilePath(fileInfo.getPath());

        try (InputStream inputStream= RemoteInputStreamClient.wrap(remoteInputStream)){
            createServerFileDirectory(target);
            Files.copy(inputStream, target, REPLACE_EXISTING);
            Files.setLastModifiedTime(target, FileTime.fromMillis(fileInfo.getLastModified().getTime()));
            System.out.println(target.toString() + " DONE");
            return true;
        }
        catch (IOException e) {
            System.out.println(target.toString() + " ABORTED");
            deleteFile(fileInfo);
            return false;
        }
    }

    @Override
    public boolean deleteFile(FileInfo fileInfo) {
        try {
            Files.delete(getServerFilePath(fileInfo.getPath()));
            return true;
        } catch (IOException e) {
            System.out.println("Can't delete: " + e.getMessage());
        }
        return false;
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
}
