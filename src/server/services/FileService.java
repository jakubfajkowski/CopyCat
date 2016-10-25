package server.services;

import java.io.File;

public interface FileService {
    void storeFile(File file);
    void retrieveFile(String filePath);
}
