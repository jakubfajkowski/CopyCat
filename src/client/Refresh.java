package client;

import client.controller.MainController;
import common.FileInfo;

import java.util.ArrayList;

public class Refresh {
    private static Refresh ourInstance = new Refresh();

    public static Refresh getInstance() {
        return ourInstance;
    }

    private Refresh() {
    }

    private MainController mainController;

    public boolean refreshAll() {
        mainController.loadSerializedRecords();
        mainController.getFileTransferController().checkIfActualFiles(mainController.getClient().getFileList());
        mainController.getTableController().paintRecords();
        mainController.serializeRecords((ArrayList)mainController.getClient().getFileList());
        return true;
    }

    public boolean refresh(FileInfo fileInfo) {
        mainController.loadSerializedRecords();
        mainController.getFileTransferController().checkIfActualFile(fileInfo);
        mainController.getTableController().paintRecords();
        mainController.serializeRecords((ArrayList)mainController.getClient().getFileList());
        return true;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
