import java.io.InputStream;

import data.ResponseWrapper;
import data.Subscriber;
import data.SubscriberSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

/**
 * Controller responsible for handling subscriber login via QR code.
 * This simulates QR scanning by entering a subscriber code manually.
 * If the code is valid, the user is logged in and redirected to the main subscriber view.
 */
public class QRCodeLoginController {

    /**
     * TextField where the subscriber enters the code (simulated QR content).
     */
    @FXML
    private TextField codeField;

    /**
     * Label for displaying login status messages (e.g., success, invalid code).
     */
    @FXML
    private Label statusLabel;

    /**
     * Initializes the controller and sets up the client listener.
     * When a valid Subscriber object is received from the server, the session is updated
     * and the scene is switched to the subscriber's main page.
     */
    @FXML
    public void initialize() {
        Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
            @Override
            public void display(Object message) {
                Platform.runLater(() -> {
                    if (message instanceof Subscriber sub) {
                        SubscriberSession.setSubscriber(sub);
                        try {
                            Main.switchScene("SubscriberMain.fxml");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        
        // Clear error when user starts typing
        codeField.textProperty().addListener((obs, oldVal, newVal) -> {
            statusLabel.setVisible(false);
        });
    }

    /**
     * Handles the login button click.
     * Sends a request to the server to validate the entered subscriber code.
     * If valid, the server will respond with a Subscriber object (handled in {@code initialize()}).
     */
    @FXML
    private void handleLogin() {
        String code = codeField.getText().trim();

        if (code.isEmpty()) {
            statusLabel.setText("Please enter a code.");
            statusLabel.setVisible(true); //added
            return;
        }

        try {
            int parsedCode = Integer.parseInt(code);
            ResponseWrapper rsp = new ResponseWrapper("VALIDATE_SUBSCRIBER_CODE", parsedCode);
            Main.clientConsole.accept(rsp);
        } catch (NumberFormatException e) {
            statusLabel.setText("Code must be numeric.");
            statusLabel.setVisible(true); //added
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handles the back button action - navigates back to SubscriberLogin.fxml.
     */
    @FXML
    private void handleBack() {
        try {
            Main.switchScene("SubscriberLogin.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}