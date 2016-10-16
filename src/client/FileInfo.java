package client;

import java.io.File;
import java.util.Date;

public class FileInfo {
    private String name;
    private String path;
    private Date lastModified;

    public FileInfo(File file){
        this.name = file.getName();
        this.path = file.getPath();
        this.lastModified = new Date(file.lastModified());
    }


    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Date getLastModified() {
        return lastModified;
    }
}
