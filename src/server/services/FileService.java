package server.services;

import com.healthmarketscience.rmiio.RemoteInputStream;
import common.FileInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public interface FileService {
    boolean isModified(FileInfo fileInfo) throws FileNotFoundException;
    boolean sendFile(FileInfo fileInfo, RemoteInputStream remoteInputStream);
    boolean deleteFile(FileInfo fileInfo);
    FileInfo getFileInfo(FileInfo fileInfo);
    RemoteInputStream getFile(FileInfo fileInfo) throws IOException;
}
