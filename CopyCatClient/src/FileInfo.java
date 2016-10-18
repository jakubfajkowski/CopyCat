import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;

public class FileInfo implements Serializable {
    private String name;
    private Long size;
    private String extension;
    private Date lastModified;
    private String path;

    public FileInfo(File file){
        this.name = file.getName();
        this.size = file.length();
        this.extension = getExtensionFromFileName(this.name);
        this.lastModified = new Date(file.lastModified());
        this.path = file.getPath();
    }

    private String getExtensionFromFileName(String fileName){
        String[] parts = fileName.split("\\.");
        return parts[parts.length - 1];
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

    public String getExtension() {
        return extension;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public String getPath() {
        return path;
    }
}
