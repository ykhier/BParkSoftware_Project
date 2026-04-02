import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;

import data.EmailSender;
import data.ParkingSpotsSession;
import data.ResponseWrapper;
import data.SubscriberSession;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Pos;

/**
 * Controller class for the "View Available Parking Spots" screen.
 * <p>
 * Displays a visual map of currently available parking spots based on
 * the current time range (now to +4 hours), and handles user requests.
 */
public class ViewAvailableParkingSpotsController implements Initializable {

    /** Label to display the current date and time range */
    @FXML private Label timeRangeLabel;

    /** Scrollable container for the parking grid */
    @FXML private ScrollPane parkingScroll;

    /** Grid pane where parking spots are displayed */
    @FXML private GridPane grid;

    /** Button to trigger the availability check */
    @FXML private Button viewButton;

    /** Label to show feedback or error messages */
    @FXML private Label statusLabel;

    /** Time range for querying parking availability */
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;

    /**
     * Initializes the controller. Sets up the default time range,
     * formats the UI label, and attaches a listener to server responses.
     *
     * @param location  the location of the FXML file
     * @param resources the resources used for localization
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize time range: now to +4 hours
        startDate = LocalDate.now();
        startTime = LocalTime.now().withMinute(0).withSecond(0).withNano(0);
        endTime = startTime.plusHours(4);
        endDate = startTime.isAfter(endTime) ? startDate.plusDays(1) : startDate;

        // Format the display text for the label
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MMMM d");

        String startDateStr = dateFmt.format(startDate);
        String endDateStr = dateFmt.format(endDate);
        String startTimeStr = timeFmt.format(startTime);
        String endTimeStr = timeFmt.format(endTime);

        String displayText = startDate.equals(endDate)
                ? "📅 Today, " + startDateStr + " — 🕒 " + startTimeStr + " to " + endTimeStr
                : "📅 " + startDateStr + " — 🕒 " + startTimeStr + " to " + endDateStr + " " + endTimeStr;

        timeRangeLabel.setText(displayText);

        // Set up a listener for server responses
        Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
            @Override
            public void display(Object msg) {
                Platform.runLater(() -> {
                    if (msg instanceof ResponseWrapper rsp && "PARKING_LIST".equals(rsp.getType())) {
                        grid.getChildren().clear();

                        if (rsp.getData() == null || ((Map<?, ?>) rsp.getData()).isEmpty()) {
                            statusLabel.setText("No available parking spots for this time.");
                            statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        } else {
                            statusLabel.setText("");
                            @SuppressWarnings("unchecked")
                            Map<Integer, Boolean> map = (Map<Integer, Boolean>) rsp.getData();
                            ParkingSpotsSession.setMap(map);
                            renderMap(map);
                        }
                    } else if (msg instanceof ResponseWrapper rsp && "CONFIRMATION_CODE".equals(rsp.getType())) {
                        if (rsp.getData() == null) {
                            statusLabel.setText("Order cannot be created.");
                            statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        } else {
                            int code = Integer.parseInt(rsp.getData().toString());
                            statusLabel.setText("Order created successfully, your code: " + code + " sent to your email too");
                            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

                            String content = "Order created successfully, your code: " + code;
                            EmailSender.sendEmail(SubscriberSession.getSubscriber().getEmail(), content);

                            // Delay before returning to main screen
                            PauseTransition delay = new PauseTransition(Duration.seconds(3));
                            delay.setOnFinished(event -> {
                                try {
                                    Main.switchScene("SubscriberMain.fxml");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                            delay.play();
                        }
                    }
                });
            }
        };
    }

    /**
     * Handles the "View" button action.
     * Sends a request to the server for available parking spots in the time range.
     */
    public void handleView() {
        ResponseWrapper timeRequest = new ResponseWrapper("TIME", startTime, endTime);
        ResponseWrapper request = new ResponseWrapper("REQUEST_ORDER", startDate, timeRequest);
        Main.clientConsole.accept(request);
    }

    /**
     * Renders the visual map of parking spots in the grid.
     * Each spot is shown as a colored circle with a label beneath it.
     *
     * @param spotMap a map of spot IDs to their availability (true = free, false = occupied)
     */
    private void renderMap(Map<Integer, Boolean> spotMap) {
        grid.setHgap(30); // horizontal spacing between columns
        grid.setVgap(30); // vertical spacing between rows
        grid.getChildren().clear();

        int col = 0, row = 0, maxCols = 5;

        for (Map.Entry<Integer, Boolean> entry : spotMap.entrySet()) {
            int spotId = entry.getKey();
            boolean isFree = entry.getValue();

            Circle spotCircle = new Circle(20);
            spotCircle.setFill(isFree ? Color.GREEN : Color.RED);

            Label label = new Label(String.valueOf(spotId));
            label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            VBox cell = new VBox(8, spotCircle, label); // vertical alignment
            cell.setAlignment(Pos.CENTER);
            cell.setSpacing(5);
            cell.setStyle("-fx-padding: 5;");

            grid.add(cell, col, row);

            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Handles the "Back" button action.
     * Navigates the user to the previous scene based on their role.
     *
     * @param event the ActionEvent triggered by the button click
     */
    public void handleBack(ActionEvent event) {
        try {
            String previousScene = "MainPage.fxml";
            if (SubscriberSession.getSubscriber() != null)
                previousScene = "SubscriberMain.fxml";

            Parent previousRoot = FXMLLoader.load(getClass().getResource(previousScene));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(previousRoot));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
