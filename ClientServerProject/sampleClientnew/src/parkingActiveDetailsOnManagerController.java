import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Controller to handle manager dash board
 */
public class parkingActiveDetailsOnManagerController extends parkingActiveDetailsController {

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

	/**
	 * method to handle clicking on logout button
	 */
	@FXML
	public void handleLogout() {
		try {
			Main.switchScene("MainPage.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * method to handle clicking on subscriber details button
	 */
	@Override
	@FXML
	void subscriberDetailsBtn(ActionEvent event) {
		try {
			Main.switchScene("SubscribersDetails.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * method to handle clicking on back button
	 * 
	 * @param event back button clicked
	 */
	@FXML
	private void handleBack(ActionEvent event) {

		try {
			Main.switchScene("MainPage.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}