package server.services;

import com.healthmarketscience.rmiio.RemoteInputStream;
import common.FileInfo;

import java.io.File;
import java.io.IOException;

public interface FileService {
    boolean isModified(FileInfo fileInfo);
    void sendFile(FileInfo fileInfo, RemoteInputStream remoteInputStream);
    FileInfo getFileInfo(FileInfo fileInfo);
    RemoteInputStream getFile(FileInfo fileInfo) throws IOException;
}
