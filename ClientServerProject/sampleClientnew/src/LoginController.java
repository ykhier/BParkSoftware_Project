import java.io.IOException;
import data.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Handles the login logic and scene switching based on user role. Also includes
 * password visibility toggle and user feedback display.
 */
public class LoginController {

	// FXML UI COMPONENTS

	/** Text field for entering the username */
	@FXML
	private TextField usernameField;

	/** Hidden password field for secure input */
	@FXML
	private PasswordField passwordField;

	/** Visible text field for showing password (if eye is open) */
	@FXML
	private TextField visiblePasswordField;

	/** ImageView acting as a toggle (eye icon) for password visibility */
	@FXML
	private ImageView eyeIcon;

	/** Success or error message container */
	@FXML
	private VBox messageBox;

	/** Label to display success or error messages */
	@FXML
	private Label messageLabel;

	/** Label used internally to display unexpected errors */
	@FXML
	private Label errorLabel;

	// =======================
	// INTERNAL STATE
	// =======================

	/** Image for open eye (password visible) */
	private Image eyeOpen;

	/** Image for closed eye (password hidden) */
	private Image eyeClosed;

	/** Indicates whether the password is currently visible */
	private boolean showingPassword = false;

	/** Static reference to errorLabel (for static methods or async access) */
	private static Label errorLabelStatic;

	/**
	 * Initializes the login controller. Loads icons, hides message box, and
	 * connects to server.
	 */
	@FXML
	public void initialize() {
		eyeOpen = new Image(getClass().getResourceAsStream("/images/eye_open.png"));
		eyeClosed = new Image(getClass().getResourceAsStream("/images/eye_closed.png"));
		eyeIcon.setImage(eyeClosed);

		// hide message box at start
		messageBox.setVisible(false);
		messageBox.setManaged(false);

		// static reference for internal methods
		errorLabelStatic = errorLabel;

		// init connection to server
		Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
			@Override
			public void display(Object response) {
				Platform.runLater(() -> handleServerResponse(response));
			}
		};
	}

	/**
	 * Handles the server's response after login. Switches scenes or shows error
	 * based on login result.
	 *
	 * @param response the server's response object
	 */
	private void handleServerResponse(Object response) {
		try {
			if (response instanceof ResponseWrapper) {
				ResponseWrapper rsp = (ResponseWrapper) response;
				if (!rsp.getType().equals("LOGIN_RESPONSE")) {
					showError("invalid response from server.");
					return;
				}

				switch (rsp.getData().toString()) {
				case "subscriber":
					Subscriber sub = (Subscriber) rsp.getExtra();
					SubscriberSession.setSubscriber(sub);
					showSuccessMessage("welcome, " + sub.getUsername() + "!", "SubscriberMain.fxml");
					break;

				case "worker":
					Worker worker = (Worker) rsp.getExtra();
					StaffSession.getInstance().login(worker.getUsername(), Role.WORKER);
					Main.switchScene("workerHomePage.fxml");
					break;

				case "manager":
					Manager manager = (Manager) rsp.getExtra();
					StaffSession.getInstance().login(manager.getUsername(), Role.ADMIN);
					Main.switchScene("managerHomePage.fxml");
					break;

				default:
					showError("invalid username or password.");
				}
			} else {
				showError("something went wrong.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			showError("something went wrong.");
		}
	}

	/**
	 * Toggles the visibility of the password between hidden and visible. Triggered
	 * by clicking the eye icon.
	 */
	@FXML
	private void togglePasswordVisibility() {
		showingPassword = !showingPassword;
		if (showingPassword) {
			visiblePasswordField.setText(passwordField.getText());
			visiblePasswordField.setVisible(true);
			visiblePasswordField.setManaged(true);
			passwordField.setVisible(false);
			passwordField.setManaged(false);
			eyeIcon.setImage(eyeOpen);
		} else {
			passwordField.setText(visiblePasswordField.getText());
			passwordField.setVisible(true);
			passwordField.setManaged(true);
			visiblePasswordField.setVisible(false);
			visiblePasswordField.setManaged(false);
			eyeIcon.setImage(eyeClosed);
		}
	}

	/**
	 * Handles login button click. Sends login data to the server for
	 * authentication.
	 */
	@FXML
	private void handleLogin() {
		String username = usernameField.getText().trim();
		String password = passwordField.isVisible() ? passwordField.getText().trim()
				: visiblePasswordField.getText().trim();

		if (username.isEmpty() || password.isEmpty()) {
			showError("username and password are required.");
			return;
		}

		Login login = new Login(username, password);
		ResponseWrapper wrapper = new ResponseWrapper("LOGIN", login);
		Main.clientConsole.accept(wrapper);
	}

	/**
	 * Navigates to the QR code login scene.
	 *
	 * @throws Exception if FXML loading fails
	 */
	@FXML
	private void handleQRLoginLink() throws Exception {
		try {
			Main.switchScene("QRCode.fxml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Navigates back to the main page when 'Back' button is clicked.
	 *
	 * @param event the action event triggered by the back button
	 */
	@FXML
	private void handleBack(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(root));
			stage.setTitle("AutoBPark");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays an error message in a red message box.
	 *
	 * @param message the error message to display
	 */
	private void showError(String message) {
		messageBox.setVisible(true);
		messageBox.setManaged(true);
		messageBox.getStyleClass().removeAll("success-box");
		if (!messageBox.getStyleClass().contains("error-box")) {
			messageBox.getStyleClass().add("error-box");
		}
		messageLabel.setText(message);
	}

	/**
	 * Displays a success message in a green box, then switches scenes after a
	 * delay.
	 *
	 * @param message   the success message to display
	 * @param nextScene the FXML scene to load next
	 */
	private void showSuccessMessage(String message, String nextScene) {
		messageBox.setVisible(true);
		messageBox.setManaged(true);
		messageBox.getStyleClass().removeAll("error-box");
		if (!messageBox.getStyleClass().contains("success-box")) {
			messageBox.getStyleClass().add("success-box");
		}
		messageLabel.setText(message);

		new Thread(() -> {
			try {
				Thread.sleep(2000);
				Platform.runLater(() -> {
					try {
						Main.switchScene(nextScene);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * Displays a generic login error using the static error label. Used for
	 * fallback in case of unexpected errors.
	 */
	private void showLoginError() {
		if (errorLabelStatic != null) {
			errorLabelStatic.setText("invalid username or password.");
			errorLabelStatic.setStyle("-fx-text-fill: red;");
		}
	}
}
