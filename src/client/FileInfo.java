package client;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;

public class FileInfo {
    private String name;
    private Long size;
    private String path;
    private Date lastModified;

    public FileInfo(File file){
        this.name = file.getName();
        this.size = file.length();
        this.path = file.getPath();
        this.lastModified = new Date(file.lastModified());
    }


    public String getName() {
        return name;
    }

    public Long getSize() {
        return size;
    }

    public static String getSizeText(Long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public String getPath() {
        return path;
    }

    public Date getLastModified() {
        return lastModified;
    }
}
