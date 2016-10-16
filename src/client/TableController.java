package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class TableController extends Controller {
    @FXML private TableView<FileInfo> table;
    @FXML private TableColumn<FileInfo, String> name;
    @FXML private TableColumn<FileInfo, Date> lastModified;
    @FXML private TableColumn<FileInfo, String> path;

    private ObservableList<FileInfo> records = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        name.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("name"));
        lastModified.setCellValueFactory(new PropertyValueFactory<FileInfo, Date>("lastModified"));
        path.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("path"));

        table.setItems(records);
    }

    public void addRecord(FileInfo fileInfo){
        records.add(fileInfo);
    }

    public void popSelectedRecord(){
        FileInfo fileInfoToRemove = table.getSelectionModel().getSelectedItem();
        records.remove(fileInfoToRemove);
    }
}
