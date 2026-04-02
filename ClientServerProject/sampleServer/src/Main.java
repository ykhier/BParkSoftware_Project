import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class to launch the JavaFX application for the server. It loads the
 * server FXML UI, initializes the EchoServer on port 5555, and binds it to the
 * ServerController to handle connection status updates.
 */
public class Main extends Application {

	/**
	 * Starts the JavaFX application by loading the FXML layout, initializing the
	 * EchoServer, and displaying the primary window.
	 *
	 * @param primaryStage The primary stage provided by the JavaFX runtime.
	 * @throws Exception If FXML loading or server startup fails.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("server.fxml"));
		Parent root = loader.load();

		ServerController controller = loader.getController();

		// Initialize and start the EchoServer on port 5555
		EchoServer server = new EchoServer(5555, controller);
		server.listen();

		primaryStage.setTitle("Server Status");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

	/**
	 * The entry point of the application. Launches the JavaFX application.
	 *
	 * @param args Command-line arguments (not used).
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
