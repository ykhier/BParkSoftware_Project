import java.util.regex.Pattern;

import data.ResponseWrapper;
import data.Subscriber;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

/**
 * Controller for handling the registration form. It validates input fields and
 * communicates with the server to register a new subscriber.
 */
public class RegisterController {

	/** Email input field */
	@FXML
	private TextField emailField;

	/** Phone input field */
	@FXML
	private TextField phoneField;

	/** Username input field */
	@FXML
	private TextField usernameField;

	/** Password input field */
	@FXML
	private PasswordField passwordField;

	/** Container for displaying success or error messages */
	@FXML
	private HBox messageContainer;

	/** Singleton instance of this controller */
	private static RegisterController instance;

	/**
	 * Default constructor storing reference to this instance.
	 */
	public RegisterController() {
		instance = this;
	}

	/**
	 * Gets the singleton instance of this controller.
	 * 
	 * @return the current instance of RegisterController
	 */
	public static RegisterController getInstance() {
		return instance;
	}

	/**
	 * Initializes the FXML page and sets up the client listener for server
	 * responses.
	 */
	@FXML
	public void initialize() {
		// client listener - waiting for response after sending registration
		Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
			@Override
			public void display(Object msg) {
				Platform.runLater(() -> {
					try {
						ResponseWrapper rsp = (ResponseWrapper) msg;
						if ("CODE".equals(rsp.getType())) {
							handleServerResponse((String) rsp.getData());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
		};
	}

	/**
	 * Handles the registration button click. Validates the input fields and sends
	 * the data to the server if valid.
	 */
	@FXML
	private void handleRegister() {
		clearMessage();

		String username = usernameField.getText().trim();
		String password = passwordField.getText().trim();
		String phone = phoneField.getText().trim();
		String email = emailField.getText().trim();

		if (username.isEmpty() || password.isEmpty() || phone.isEmpty() || email.isEmpty()) {
			showMessage("all fields must be filled.", "error");
			return;
		}
		if (!isValidUsername(username)) {
			showMessage("username must contain only letters.", "error");
			return;
		}

		if (!isValidPhone(phone)) {
			showMessage("invalid phone number. use 10 digits like: 0501234567", "error");
			return;
		}

		if (!isValidEmail(email)) {
			showMessage("invalid email format.", "error");
			return;
		}

		Subscriber sub = new Subscriber(username, password, phone, email, 0);
		ResponseWrapper rsp = new ResponseWrapper("REGISTER_SUBSCRIBER", sub);
		Main.clientConsole.accept(rsp);
	}

	/**
	 * Handles the response from the server after a registration attempt.
	 * 
	 * @param message the response message from the server
	 */
	public void handleServerResponse(String message) {
		clearMessage();
		if (message.toLowerCase().contains("successful")) {
			showMessage(message, "success");
			clearForm();
		} else {
			showMessage(message, "error");
		}
	}

	/**
	 * Clears all input fields in the form.
	 */
	public void clearForm() {
		emailField.clear();
		phoneField.clear();
		usernameField.clear();
		passwordField.clear();
	}

	/**
	 * Validates the email format.
	 * 
	 * @param email the email to validate
	 * @return true if valid, false otherwise
	 */
	private boolean isValidEmail(String email) {
		return Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email);
	}

	/**
	 * Validates the phone number format (must be 10 digits).
	 * 
	 * @param phone the phone number to validate
	 * @return true if valid, false otherwise
	 */
	private boolean isValidPhone(String phone) {
		return phone.matches("\\d{10}");
	}

	/**
	 * Displays a styled message inside the message container.
	 * 
	 * @param text the message text
	 * @param type "success" or "error" to determine style
	 */
	private void showMessage(String text, String type) {
		messageContainer.getChildren().clear();
		Label msg = new Label(text);
		msg.setStyle(getStyleForType(type));
		messageContainer.getChildren().add(msg);
	}

	/**
	 * Clears the message container.
	 */
	private void clearMessage() {
		messageContainer.getChildren().clear();
	}

	/**
	 * Returns the CSS style string for success or error messages.
	 * 
	 * @param type the message type ("success" or "error")
	 * @return CSS style string
	 */
	private String getStyleForType(String type) {
		if ("success".equals(type)) {
			return "-fx-background-color: rgba(144, 238, 144, 0.4);" + "-fx-text-fill: #006400;"
					+ "-fx-font-weight: bold;" + "-fx-padding: 12;" + "-fx-border-color: #006400;"
					+ "-fx-border-width: 1;" + "-fx-background-radius: 6;" + "-fx-border-radius: 6;";
		} else {
			return "-fx-background-color: rgba(255, 99, 71, 0.3);" + "-fx-text-fill: #990000;"
					+ "-fx-font-weight: bold;" + "-fx-padding: 12;" + "-fx-border-color: #990000;"
					+ "-fx-border-width: 1;" + "-fx-background-radius: 6;" + "-fx-border-radius: 6;";
		}
	}

	/**
	 * Handles the back button click and navigates to the previous scene.
	 */
	@FXML
	private void handleBack() {
		Main.goBackToPreviousScene();
	}

	/**
	 * Validates that the username contains only alphabetic characters.
	 * 
	 * @param username the username to validate
	 * @return true if only alphabetic, false otherwise
	 */
	private boolean isValidUsername(String username) {
		return username.matches("[A-Za-z]+");
	}
}
