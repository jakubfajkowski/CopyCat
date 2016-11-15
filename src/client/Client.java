package client;

import common.ClientCredentials;
import common.FileInfo;
import common.RemoteSession;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private RemoteSession remoteSession;
    private ClientCredentials clientCredentials;
    private List<FileInfo> fileList = new ArrayList<>();
    private boolean loggedIn = false;

    public ClientCredentials getClientCredentials() {
        return clientCredentials;
    }

    public void setClientCredentials(ClientCredentials clientCredentials) {
        this.clientCredentials = clientCredentials;
    }

    public List<FileInfo> getFileList() {
        return fileList;
    }

    public void setFileList(List<FileInfo> fileList) {
        this.fileList = fileList;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public RemoteSession getRemoteSession() {
        return remoteSession;
    }

    public void setRemoteSession(RemoteSession remoteSession) {
        this.remoteSession = remoteSession;
    }
}
