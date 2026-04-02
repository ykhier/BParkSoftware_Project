import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import data.Order;
import data.ResponseWrapper;
import data.Subscriber;
import data.SubscriberSession;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Class for controlling showing all subscribers and their details
 */
public class subscriberDetailsControllerManager implements Initializable {

	/** Table column showing subscriber code (ID) */
	@FXML
	private TableColumn<Subscriber, Integer> codeCol;

	/** Table column showing subscriber username */
	@FXML
	private TableColumn<Subscriber, String> usernameCol;

	/** Table column showing subscriber password */
	@FXML
	private TableColumn<Subscriber, String> passwordCol;

	/** Table column showing subscriber phone number */
	@FXML
	private TableColumn<Subscriber, String> phoneNumberCol;

	/** Table column showing subscriber email address */
	@FXML
	private TableColumn<Subscriber, String> emailCol;

	/** Table view holding all subscriber data */
	@FXML
	private TableView<Subscriber> subscriberTable;

	

	/**
	 * Initializes the controller and sets up the subscriber table. Also fetches
	 * subscriber list from the server.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("SubscriberDetailsController initialized.");


		// Bind table columns to Subscriber fields
		codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
		usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
		passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
		phoneNumberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

		// Style table
		subscriberTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		subscriberTable.setEditable(false);
		subscriberTable.setFocusTraversable(false);

		// Listen for response from server
		Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
			@Override
			public void display(Object message) {
				try {
					System.out.println(message);
					if (message instanceof ArrayList<?> list && !list.isEmpty() && list.get(0) instanceof Subscriber) {
						ObservableList<Subscriber> data = FXCollections.observableArrayList();
						@SuppressWarnings("unchecked")
						ArrayList<Subscriber> subscribersList = (ArrayList<Subscriber>) list;
						for (Object o : subscribersList) {
							data.add((Subscriber) o);
						}

						subscriberTable.setItems(data);

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		// Send request to server for all subscribers
		ResponseWrapper subscriberResponse = new ResponseWrapper("subscriberList", null);
		Main.clientConsole.accept(subscriberResponse);
	}

	/**
	 * Navigates to the screen showing parking activity details.
	 * 
	 * @param event the action event
	 */
	@FXML
	void parkingActiveDetailsBtn(ActionEvent event) {
		try {
			Main.switchScene("parkingActiveDetailsOnManager.fxml");
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
	 * added this method method to handle clicking on logout button
	 */
	@FXML
	public void handleLogout() {
		try {
			SubscriberSession.setSubscriber(null);
			Main.switchScene("MainPage.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handle subsriber deatils btn clicked (already opened so we won't do anything)
	 */
	@FXML
	public void subscriberDetailsBtn() {
		
		System.out.println("Already on SubscriberDetails.fxml – not reloading.");
	}
	
	/**
	 * Method to switch to parkingSpotReport page
	 * 
	 * @param event button clicked
	 * @throws Exception exception that might be thrown
	 */

	@FXML
	void reportsBtn(ActionEvent event) throws Exception {
		Main.switchScene("parkingSpotReport.fxml");
	}

	/**
	 * Method to switch to MonthlyReportView page
	 * 
	 * @param event button clicked
	 * @throws Exception exception that might be thrown
	 */
	@FXML
	void MonthlyreportsBtn(ActionEvent event) {
		try {
			Main.switchScene("MonthlyReportView.fxml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
