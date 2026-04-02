import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;

import java.io.IOException;

import data.Order;
import data.ResponseWrapper;
import data.Subscriber;
import data.SubscriberSession;

/**
 * Controller class for the Car Delivery GUI.
 * Handles subscription code input, validation with the server,
 * displays feedback messages to the user, and opens the delivery window.
 */
/**
 * Controller class for the Car Delivery GUI. Handles subscription code input,
 * validation with the server, displays feedback messages to the user, and opens
 * the delivery window.
 */
public class CarDeliveryController {

	@FXML
	private TextField subscriptionCodeField;

	@FXML
	private Label outputLabel;

	@FXML
	private Button deliverCarClicked;

	/**
	 * Initializes the controller after the FXML components are loaded. Sets up
	 * client response handling.
	 */
	@FXML
	public void initialize() {
		outputLabel.setVisible(false);
		deliverCarClicked.setVisible(false);

		Subscriber sub = SubscriberSession.getSubscriber();
		if (sub != null) {
			SubscriberSession.setconway("internal");
		} else {
			SubscriberSession.setconway("external");
		}

		Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
			@Override
			public void display(Object message) {
				Platform.runLater(() -> {
					if (message instanceof ResponseWrapper rsp && "CHECK_USER_CODE_RESULT".equals(rsp.getType())) {

						if (rsp.getData() instanceof String receivedMsg) {
							switch (receivedMsg) {
							case "Invalid Code" ->
								showMessage("❌ Invalid subscription code. Please try again.", "error");
							case "FULL" -> showMessage("❌ The Parking is Full", "error");
							default -> {
								showMessage("✅ Press delivery to deliver the car", "success");
								deliverCarClicked.setVisible(true);
							}
							}
						}
					}
				});
			}
		};
	}

	/**
	 * Method that checks if valid code was entered and sends it to the client
	 */
	@FXML
	private void checkCodeClicked() {
		String inputCode = subscriptionCodeField.getText().trim();
		deliverCarClicked.setVisible(false);
		outputLabel.setVisible(false);

		if (inputCode.isEmpty()) {

			showMessage("⚠️ Please enter a subscription code.", "warning");
			return;
		}

		if ("internal".equals(SubscriberSession.getconway())) {
			Subscriber sub = SubscriberSession.getSubscriber();
			if (sub == null) {
				showMessage("⚠ Session not found. Please login again.", "error");
				return;
			}

			if (!inputCode.equals(String.valueOf(sub.getCode()))) {
				showMessage("❌ This is not your subscription code!", "error");
				return;
			}
		} else {
			// SubscriberSession.getSubscriber().setCode(Integer.parseInt(inputCode));
			SubscriberSession.setsubscriberid(inputCode);
		}

		Main.clientConsole.accept(new ResponseWrapper("CHECK_USER_CODE", inputCode));
	}

	/**
	 * Sends the subscription code to the server for validation.
	 */

	/**
	 * Opens the CarDelivery.fxml scene and passes subscriber ID internally.
	 */
	@FXML
	private void DeliverCarClicked() {
		try {
			int subscriberId;
			if ("internal".equals(SubscriberSession.getconway())) {
				Subscriber sub = SubscriberSession.getSubscriber();
				if (sub == null) {
					showMessage("⚠ Subscriber not found. Please re-login.", "error");
					return;
				}
				subscriberId = sub.getCode();
			} else {
				String externalId = SubscriberSession.getsubscriberid();
				if (externalId == null || externalId.isEmpty()) {
					showMessage("⚠ Missing subscription ID for external user.", "error");
					return;
				}
				subscriberId = Integer.parseInt(externalId);
			}
			Order order1 = new Order(subscriberId);

			FXMLLoader loader = new FXMLLoader(getClass().getResource("CarDelivery.fxml"));
			Parent root = loader.load();
			CarDelivery controller = loader.getController();
			controller.setOrder(order1);

			// Main.switchScene("CarDelivery.fxml");
			Stage stage = new Stage();
			stage.setTitle("AutoBPark");
			stage.setScene(new Scene(root));
			stage.show();

			Stage currentStage = (Stage) deliverCarClicked.getScene().getWindow();
			currentStage.close();

		} catch (Exception e) {
			showMessage("❌ Error: " + e.getMessage(), "error");
		}
	}

	/**
	 * Opens the CarDelivery2.fxml scene.
	 */
	@FXML
	public void carDeliveryBtnClicked() {
		try {
			Main.switchScene("CarDelivery2.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Opens the CarDeliveryWithCode.fxml scene for confirmation code entry.
	 */
	@FXML
	void confCode(MouseEvent event) {
		try {
			Parent previousRoot = FXMLLoader.load(getClass().getResource("CarDeliveryWithCode.fxml"));
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(previousRoot));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Navigates back to the SubscriberMain.fxml scene.
	 * @param event button clicked event
	 */
	@FXML
	public void handleBackToMainPage(ActionEvent event) {
		try {
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            if ("external".equals(SubscriberSession.getconway())) {
            	SubscriberSession.setconway("internal");
                Main.switchScene("MainPage.fxml");
            } else {
                Main.switchScene("SubscriberMain.fxml");
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays a styled message at the bottom of the screen.
	 *
	 * @param message the message text
	 * @param type    one of: "success", "error", "warning"
	 */
	private void showMessage(String message, String type) {
		outputLabel.setText(message);
		outputLabel.setVisible(true);

		String style = switch (type) {
		case "success" -> "-fx-font-size: 14px; -fx-text-fill: green; -fx-border-color: green; "
				+ "-fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; "
				+ "-fx-padding: 10px; -fx-background-color: #e6ffe6;";
		case "error" -> "-fx-font-size: 14px; -fx-text-fill: red; -fx-border-color: red; "
				+ "-fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; "
				+ "-fx-padding: 10px; -fx-background-color: #ffe5e5;";
		case "warning" -> "-fx-font-size: 14px; -fx-text-fill: orange; -fx-border-color: orange; "
				+ "-fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; "
				+ "-fx-padding: 10px; -fx-background-color: #fff8e1;";
		default -> "-fx-font-size: 14px;";
		};

		outputLabel.setStyle(style);
	}
}
