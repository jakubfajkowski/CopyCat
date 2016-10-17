package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class TableController extends Controller {
    @FXML private TableView<FileInfo> table;
    @FXML private TableColumn<FileInfo, String> name;
    @FXML private TableColumn<FileInfo, Long> size;
    @FXML private TableColumn<FileInfo, String> extension;
    @FXML private TableColumn<FileInfo, Date> lastModified;
    @FXML private TableColumn<FileInfo, String> path;

    private ObservableList<FileInfo> records = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
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

        loadSerializedRecords();
        table.setItems(records);
    }

    private void loadSerializedRecords(){
        try{
            FileInputStream fileIn = new FileInputStream("fileinforecords.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);

            @SuppressWarnings("unchecked")
            List<FileInfo> list = (List<FileInfo>) in.readObject();

            records = FXCollections.observableList(list);
        } catch (FileNotFoundException fe) {
            return;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    void addRecord(FileInfo fileInfo){
        records.add(fileInfo);
        serializeRecords(new ArrayList<>(records));
    }

    void popSelectedRecord(){
        FileInfo fileInfoToRemove = getSelectedFileRecord();
        records.remove(fileInfoToRemove);
        serializeRecords(new ArrayList<>(records));
    }

    private void serializeRecords(Serializable records){
        try {
            FileOutputStream fileOut = new FileOutputStream("fileinforecords.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(records);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    FileInfo getSelectedFileRecord(){
        return table.getSelectionModel().getSelectedItem();
    }
}
