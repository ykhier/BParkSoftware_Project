import data.ClientStatus;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for the server GUI that manages the client connection table.
 * Displays IP, host name, and current connection status.
 */
public class ServerController {

    /**
     * Table displaying connected client statuses
     */
    @FXML
    private TableView<ClientStatus> clientTable;

    /**
     * Table columns for IP
     */
    @FXML
    private TableColumn<ClientStatus, String> ipColumn;

    /**
     * Table columns for host name
     */
    @FXML
    private TableColumn<ClientStatus, String> hostNameColumn;

    /**
     * Table columns for connection status
     */
    @FXML
    private TableColumn<ClientStatus, String> statusColumn;

    /**
     * 
    * Observable list to hold client data (auto-updates the TableView when changed)
     */
    private final ObservableList<ClientStatus> clients = FXCollections.observableArrayList();

    /**
     * Initializes the TableView with column bindings and attaches the data list.
     */
    @FXML
    public void initialize() {
        // Link table columns to properties in ClientStatus class
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));
        hostNameColumn.setCellValueFactory(new PropertyValueFactory<>("hostName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Set the observable list as the data source for the table
        clientTable.setItems(clients);
    }

    /**
     * Updates the status of a client in the table or adds it if not present.
     * Runs safely on the JavaFX UI thread.
     *
     * @param ip        The IP address of the client
     * @param hostName  The hostname of the client
     * @param status    The current connection status
     */
    public void updateClientStatus(String ip, String hostName, String status) {
        Platform.runLater(() -> {
            boolean found = false;
            for (int i = 0; i < clients.size(); i++) {
                ClientStatus c = clients.get(i);
                if (c.getIp().equals(ip)) {
                    // Replace the existing item to trigger UI update
                    clients.set(i, new ClientStatus(ip, hostName, status));
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Add new client if it doesn't already exist
                clients.add(new ClientStatus(ip, hostName, status));
            }
        });
    }
}
