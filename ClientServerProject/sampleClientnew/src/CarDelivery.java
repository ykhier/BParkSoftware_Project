import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import data.Car;
import data.Order;
import data.ResponseWrapper;
import data.Subscriber;
import data.SubscriberSession;
import data.SystemStatus;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller class for the Car Delivery window. Handles input for car model,
 * year, and number; submission of delivery request; confirmation feedback; and
 * communication with the server.
 */
public class CarDelivery implements Initializable {

	/** Text field for entering the car model */
	@FXML
	private TextField modelField;

	/** Text field for entering the car year */
	@FXML
	private TextField yearField;

	/** Text field for entering the car number */
	@FXML
	private TextField carNumberField;

	/** Label for showing empty/invalid fields warning */
	@FXML
	private Label emptyLabels;

	/** Label to show success message */
	@FXML
	private Label successLabel;

	/** Spinner for selecting extra parking hours */
	@FXML
	private Spinner<Integer> hourSpinner;

	/** Label next to car number input to indicate error */
	@FXML
	private Label carNumberLabel;

	/** Label next to year input to indicate error */
	@FXML
	private Label yearLabel;

	/** Label next to model input to indicate error */
	@FXML
	private Label modelLabel;

	/** The parking spot code assigned to the order */
	private int parkingCode;

	/** The confirmation code generated after delivery */
	private int confirmationCode;

	/** The Car object representing entered details */
	private Car car;

	/** Number of additional hours requested */
	private int extraHours;

	/** Start time of the parking */
	private LocalDateTime startTime;

	/** End time of the parking */
	private LocalDateTime endTime;

	/** Optional order object passed in by previous screen */
	private Order order1 = null;

	/**
	 * Sets a pre-created Order instance if delivery is being completed using a
	 * code.
	 * 
	 * @param order The order object with car info and timing
	 * @param sub   The subscriber performing the delivery
	 */
	public void setOrderWithCode(Order order, Subscriber sub) {
		this.order1 = order;
	}

	/**
	 * Injects an Order object into the CarDelivery controller. Used when navigating
	 * from the CarDeliveryController.
	 * 
	 * @param order the Order instance
	 */
	public void setOrder(Order order) {
		this.order1 = order;
	}

	/**
	 * Generates a confirmation code from the parking code.
	 * 
	 * @param parkingCode The parking space number
	 * @return A numeric confirmation code
	 */
	public static int generateConfirmationCode(int parkingCode) {
		return parkingCode * 73 + 1234;
	}

	/**
	 * Shows a simple alert popup with the given message.
	 * 
	 * @param msg The message to show
	 */
	public void alert(String msg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Message");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}

	/**
	 * Called when the user clicks the delivery button. Validates input and sends a
	 * delivery request to the server.
	 * 
	 * @param event The ActionEvent triggered by clicking the button
	 */
	@FXML
	void DeliveryButton(ActionEvent event) {
		String carNumber = carNumberField.getText().trim();
		String model = modelField.getText().trim();
		String yearText = yearField.getText().trim();

		boolean allFieldsFilled = !carNumber.isEmpty() && !model.isEmpty() && !yearText.isEmpty();
		boolean isCarNumberValid = Car.formatIsraeliCarNumber(carNumber) != null;
		boolean isModelValid = Car.isValidCarModel(model);
		boolean isYearValid = false;
		int year = -1;

		try {
			year = Integer.parseInt(yearText);
			isYearValid = Car.isValidCarYear(year);
		} catch (NumberFormatException e) {
			isYearValid = false;
		}

		if (allFieldsFilled && isCarNumberValid && isModelValid && isYearValid) {
			emptyLabels.setVisible(false);
			clearErrorBorders();

			this.car = new Car(carNumber, model, year);
			this.extraHours = hourSpinner.getValue();

			ZonedDateTime zonedNow = ZonedDateTime.now(ZoneId.of("Asia/Jerusalem"));
			this.startTime = zonedNow.toLocalDateTime();
			this.endTime = startTime.plusHours(extraHours);
			int subscriberId;
			if ("internal".equals(SubscriberSession.getconway())) {
				Subscriber sub = SubscriberSession.getSubscriber();
				if (sub == null) {
					emptyLabels.setText("⚠️ Subscriber info missing. Please re-login.");
					emptyLabels.setVisible(true);
					return;
				}
				subscriberId = sub.getCode();
			} else {
				String id = SubscriberSession.getsubscriberid();
				if (id == null || id.isEmpty()) {
					emptyLabels.setText("⚠️ External subscriber ID missing.");
					emptyLabels.setVisible(true);
					return;
				}
				subscriberId = Integer.parseInt(id);
			}

			if (order1 == null) {
				Order fullOrder = new Order(subscriberId, 0, startTime, extraHours, endTime, car, 0);
				Main.clientConsole.accept(new ResponseWrapper("DELIVERYCAR", fullOrder));
			} else {
				order1.setSubscriber_id(subscriberId);
				order1.setNumberofextend(extraHours);
				order1.setRecivingcartime(startTime);
				order1.setDelivery_time(endTime);
				order1.setCar(car);
				Main.clientConsole.accept(new ResponseWrapper("DELIVERYCAR", order1));
			}

		} else {
			emptyLabels.setText("You have empty or invalid fields.");
			emptyLabels.setVisible(true);
			markInvalidFields(isCarNumberValid, isModelValid, isYearValid);
		}
	}

	/**
	 * Removes error-border CSS classes and hides all error labels.
	 */
	private void clearErrorBorders() {
		carNumberField.getStyleClass().remove("error-border");
		modelField.getStyleClass().remove("error-border");
		yearField.getStyleClass().remove("error-border");
		carNumberLabel.setVisible(false);
		modelLabel.setVisible(false);
		yearLabel.setVisible(false);
	}

	/**
	 * Adds red borders and shows relevant error labels depending on field validity.
	 * 
	 * @param carValid   Whether the car number is valid
	 * @param modelValid Whether the model name is valid
	 * @param yearValid  Whether the year is valid
	 */
	private void markInvalidFields(boolean carValid, boolean modelValid, boolean yearValid) {
		if (!carValid) {
			if (!carNumberField.getStyleClass().contains("error-border"))
				carNumberField.getStyleClass().add("error-border");
			carNumberLabel.setVisible(true);
		}

		if (!modelValid) {
			if (!modelField.getStyleClass().contains("error-border"))
				modelField.getStyleClass().add("error-border");
			modelLabel.setVisible(true);
		}

		if (!yearValid) {
			if (!yearField.getStyleClass().contains("error-border"))
				yearField.getStyleClass().add("error-border");
			yearLabel.setVisible(true);
		}
	}

	/**
	 * Handles the back button press to close the current stage and open
	 * CarDelivery2.fxml.
	 * 
	 * @param event The action event from the button press
	 */
	@FXML
	public void handleBackToMainPage(ActionEvent event) {
		try {
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.close();
			Main.switchScene("CarDelivery2.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the view and configures client-server response handling.
	 * 
	 * @param location  Not used
	 * @param resources Not used
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 12, 4);
		hourSpinner.setValueFactory(valueFactory);

		carNumberLabel.setVisible(false);
		modelLabel.setVisible(false);
		yearLabel.setVisible(false);

		Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
			@Override
			public void display(Object message) {
				Platform.runLater(() -> {
					if (message instanceof ResponseWrapper response && response.getType().equals("SUCCESS_DELIVERY")) {
						Order order = (Order) response.getData();
						parkingCode = order.getParking_space();
						confirmationCode = order.getConfirmation_code();

						Stage currentStage = (Stage) successLabel.getScene().getWindow();
						currentStage.close();

						Alert alert = new Alert(Alert.AlertType.INFORMATION);
						alert.setTitle("Delivery Confirmed");
						alert.setHeaderText("Car Delivery Completed");
						alert.setContentText(String.format(
								"✅ The car was delivered successfully.\n📍 Parking spot: %d\n🔐 Confirmation code: %d",
								parkingCode, confirmationCode));

						alert.getDialogPane().getStylesheets()
								.add(getClass().getResource("Alert.css").toExternalForm());
						alert.getDialogPane().getStyleClass().add("custom-alert");

						alert.showAndWait();

						try {
							if ("external".equals(SubscriberSession.getconway())) {
								SubscriberSession.setconway("internal");
								SubscriberSession.setsubscriberid(null);
								Main.switchScene("MainPage.fxml");
							} else {
								Main.switchScene("SubscriberMain.fxml");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					if (message instanceof SystemStatus status) {
						switch (status) {
						case NO_PARKING_SPOT -> emptyLabels.setText("No available parking spot.");
						case ALREADY_DELIVERED ->
							emptyLabels.setText("This subscriber already has an active delivery with this car.");
						}
						emptyLabels.setVisible(true);
					}
				});
			}
		};
	}
}
