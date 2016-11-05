package server.services;

import common.FileInfo;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;

import static org.junit.Assert.*;

/**
 * Created by FajQa on 28.10.2016.
 */
public class FileServiceImplTest {
    String usernameRootFolderName;

    @Test
    public void isModified_notModifiedFile() throws Exception {
        usernameRootFolderName = "test_rsc/server/services";

        FileServiceImpl fileService = new FileServiceImpl(usernameRootFolderName);

        FileInfo fileInfo = new FileInfo(new File(getClass().getResource("isModifiedTestFile_NOTMODIFIED.txt").toURI()));
        Path path = fileInfo.getPath();
        Path modifiedPath = getServerFilePath(path);

        createServerFileDirectory(modifiedPath);
        Files.copy(fileInfo.getPath(), modifiedPath);

        assertFalse(fileService.isModified(fileInfo));

        clean(modifiedPath.subpath(0,4));
    }

    @Test
    public void isModified_ModifiedFile() throws Exception {
        usernameRootFolderName = "test_rsc/server/services";

        FileServiceImpl fileService = new FileServiceImpl(usernameRootFolderName);

        FileInfo fileInfo = new FileInfo(new File(getClass().getResource("isModifiedTestFile_MODIFIED.txt").toURI()));
        Path path = fileInfo.getPath();
        Path modifiedPath = getServerFilePath(path);

        createServerFileDirectory(modifiedPath);
        Files.copy(fileInfo.getPath(), modifiedPath);

        fileInfo = new FileInfo(new File(getClass().getResource("isModifiedTestFile_NOTMODIFIED.txt").toURI()));

        assertTrue(fileService.isModified(fileInfo));

        clean(modifiedPath.subpath(0,4));
    }

    private Path getServerFilePath(Path originalPath) {
        return Paths.get(usernameRootFolderName + "/"
                + originalPath.getRoot().toString().replace(":","")
                + originalPath.subpath(1, originalPath.getNameCount()));
    }

    private void createServerFileDirectory(Path serverFilePath) throws IOException {
        Files.createDirectories(serverFilePath.getParent());
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