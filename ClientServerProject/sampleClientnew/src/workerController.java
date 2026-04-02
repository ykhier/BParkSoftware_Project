import java.net.URL;
import java.util.ResourceBundle;

import data.Role;
import data.StaffSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * Controller class for the worker's main dashboard screen. Displays a table
 * with all subscribers and enables navigation to register, view orders, and
 * parking activity details.
 */
public class workerController implements Initializable {
//
//	/** Table column showing subscriber code (ID) */
//	@FXML
//	private TableColumn<Subscriber, Integer> codeCol;
//
//	/** Table column showing subscriber username */
//	@FXML
//	private TableColumn<Subscriber, String> usernameCol;
//
//	/** Table column showing subscriber password */
//	@FXML
//	private TableColumn<Subscriber, String> passwordCol;
//
//	/** Table column showing subscriber phone number */
//	@FXML
//	private TableColumn<Subscriber, String> phoneNumberCol;
//
//	/** Table column showing subscriber email address */
//	@FXML
//	private TableColumn<Subscriber, String> emailCol;
//
//	/** Table view holding all subscriber data */
//	@FXML
//	private TableView<Subscriber> subscriberTable;
//
//	/** Reference to the current instance of the controller */
//	private static workerController instance;
//
//	/**
//	 * Getter for the subscriber table (for external access).
//	 * 
//	 * @return the subscriber TableView
//	 */
//	public static TableView<Subscriber> getSubscriberTable() {
//		return instance.subscriberTable;
//	}
//
	/**
	 * Initializes the controller and sets up the subscriber table. Also fetches
	 * subscriber list from the server.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		new Thread(() -> {
			try {
				Thread.sleep(100); // delay to see the flash
				Platform.runLater(() -> {
					try {
						if(StaffSession.getInstance().getRole()==Role.ADMIN)
							Main.switchScene("SubscribersDetailsManager.fxml");
						else 
							Main.switchScene("SubscribersDetails.fxml");
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			} catch (InterruptedException ignored) {
			}
		}).start();
	}

	/**
	 * Navigates to the screen showing parking activity details.
	 * 
	 * @param event the action event
	 */
	@FXML
	void parkingActiveDetailsBtn(ActionEvent event) {
		try {
			Main.switchScene("parkingActiveDetails.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Navigates to the screen for registering a new subscriber.
	 */
	@FXML
	private void handleRegisterNewSubscriber() {
		try {
			Main.switchScene("Register.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Navigates to the screen displaying all existing orders.
	 */
	@FXML
	private void showOrdersBtn() {
		try {
			Main.switchScene("ShowOrder.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns to the main login/home page.
	 */
	@FXML
	private void handleBack() {
		try {
			Main.switchScene("MainPage.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * added this method method to handle clicking on logout button
	 */
	@FXML
	public void handleLogout() {
		try {
			StaffSession.getInstance().logout();
			Main.switchScene("MainPage.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
