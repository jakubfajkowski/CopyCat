package common;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Objects;

public class FileInfo implements Serializable {
    static final long serialVersionUID = 1L;

    private String name;
    private boolean modified;
    private boolean backuped;
    private Long size;
    private Date lastModified;
    private Path path;

    public FileInfo() {
        name = "";
        modified = false;
        backuped = false;
        size = 0L;
        lastModified = new Date();
        path = Paths.get("C");
    }

    public FileInfo(File file){
        this.name = file.getName();
        this.modified = true;
        this.backuped = false;
        this.size = file.length();
        this.lastModified = new Date(file.lastModified());
        this.path = file.toPath();
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(name);
        out.writeBoolean(backuped);
        out.writeLong(size);
        out.writeObject(lastModified);
        out.writeObject(path.toString());
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        name = (String) in.readObject();
        modified = true;
        backuped = in.readBoolean();
        size = in.readLong();
        lastModified = (Date) in.readObject();
        path = Paths.get((String) in.readObject());
        updateObject();
    }

    public void updateObject() {
        File file = path.toFile();
        if(!file.exists()) {
            this.lastModified = new Date();
        }
        else {
            this.size = file.length();
            this.lastModified = new Date(file.lastModified());
        }
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
        return getExtensionFromFileName(this.name);
    }

    private String getExtensionFromFileName(String fileName){
        String[] parts = fileName.split("\\.");
        return parts[parts.length - 1];
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfo fileInfo = (FileInfo) o;
        return Objects.equals(name, fileInfo.name) &&
                Objects.equals(size, fileInfo.size) &&
                Objects.equals(lastModified, fileInfo.lastModified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, size, lastModified);
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public boolean isBackuped() {
        return backuped;
    }

    public void setBackuped(boolean backuped) {
        this.backuped = backuped;
    }
}
