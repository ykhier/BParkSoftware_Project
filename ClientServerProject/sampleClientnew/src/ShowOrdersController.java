import java.util.ArrayList;

import data.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller to show all orders in a table for workers.
 * Handles table initialization, server connection, and scene navigation.
 */
public class ShowOrdersController {

    // ========== UI Components (linked from FXML) ==========

    /** Table to display order data */
    @FXML private TableView<Order> ordersTable;

    /** Column displaying order number */
    @FXML private TableColumn<Order, Integer> orderNumberCol;

    /** Column displaying parking space */
    @FXML private TableColumn<Order, Integer> parkingSpaceCol;

    /** Column displaying order date */
    @FXML private TableColumn<Order, String> orderDateCol;

    /** Column displaying confirmation code */
    @FXML private TableColumn<Order, Integer> confirmationCodeCol;

    /** Column displaying date of placing the order */
    @FXML private TableColumn<Order, String> dateOfPlacingAnOrderCol;

    /** Column displaying subscriber ID */
    @FXML private TableColumn<Order, Integer> subscriberIdCol;

    /** Reference to the main application (used for scene switching) */
    private Main mainApp;

    /**
     * Sets the main application reference.
     *
     * @param mainApp the main application instance
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Initializes the controller.
     * Sets up table columns and populates order data from the server.
     */
    @FXML
    public void initialize() {
        // Bind each column to the appropriate property in Order
        orderNumberCol.setCellValueFactory(new PropertyValueFactory<>("order_number"));
        parkingSpaceCol.setCellValueFactory(new PropertyValueFactory<>("parking_space"));
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("order_date"));
        confirmationCodeCol.setCellValueFactory(new PropertyValueFactory<>("confirmation_code"));
        dateOfPlacingAnOrderCol.setCellValueFactory(new PropertyValueFactory<>("date_of_placing_an_order"));
        subscriberIdCol.setCellValueFactory(new PropertyValueFactory<>("subscriber_id"));

        // Table styling and behavior
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ordersTable.setEditable(false);
        ordersTable.setFocusTraversable(false);

        // Start listening for server response
        Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
            @Override
            public void display(Object message) {
                try {
                    // Handle list of orders returned from server
                    if (message instanceof ArrayList<?> list && !list.isEmpty() && list.get(0) instanceof Order) {
                        ObservableList<Order> data = FXCollections.observableArrayList();
                        for (Object o : list) {
                            data.add((Order) o);
                        }
                        ordersTable.setItems(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // Send request to server to get orders
        Main.clientConsole.accept("showAllOrders");
    }

    /**
     * Navigates back to the worker home page.
     */
    public void goBack() {
        try {
            Main.switchScene("workerHomePage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the order update screen.
     */
    public void updateOrder() {
        try {
            Main.switchScene("UpdateOrder.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
