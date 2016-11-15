package client.controller;

import client.controller.Controller;
import common.FileInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class TableController extends Controller {
    @FXML private TableView<FileInfo> table;
    @FXML private TableColumn<FileInfo, String> name;
    @FXML private TableColumn<FileInfo, Boolean> actual;
    @FXML private TableColumn<FileInfo, Long> size;
    @FXML private TableColumn<FileInfo, String> extension;
    @FXML private TableColumn<FileInfo, Date> lastModified;
    @FXML private TableColumn<FileInfo, String> path;

    private ObservableList<FileInfo> records = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        actual.setCellValueFactory(new PropertyValueFactory<>("actual"));
        size.setCellValueFactory(new PropertyValueFactory<>("size"));
        size.setCellFactory(c -> new TableCell<FileInfo, Long>() {

            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(FileInfo.getSizeText(item));
                }
            }

        });
        extension.setCellValueFactory(new PropertyValueFactory<>("extension"));
        lastModified.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
        path.setCellValueFactory(new PropertyValueFactory<>("path"));

        table.setItems(records);
    }

    void addRecord(FileInfo fileInfo){
        records.add(fileInfo);
    }

    void popSelectedRecord(){
        FileInfo fileInfoToRemove = getSelectedFileRecord();
        records.remove(fileInfoToRemove);
    }

    FileInfo getSelectedFileRecord(){
        return table.getSelectionModel().getSelectedItem();
    }

    public ObservableList<FileInfo> getRecords() {
        return records;
    }

    public void setRecords(List<FileInfo> records) {
        this.records = FXCollections.observableArrayList(records);
        table.setItems(this.records);
        table.refresh();
    }

}
