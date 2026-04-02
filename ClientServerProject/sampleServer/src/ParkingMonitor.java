import java.sql.*;
import java.time.*;
import java.util.concurrent.*;
import data.EmailSender;

/**
 * Monitors parking activity, late orders, sends warnings and reports via email,
 * and schedules monthly reports.
 */
public class ParkingMonitor {

	/**
	 * Starts periodic monitoring of active subscriber parking sessions.
	 * Runs every 10 minutes.
	 */
	public static void startMonitoring() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(() -> {
			try {
				checkParkingDurations();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, 0, 10, TimeUnit.MINUTES);
	}

	/**
	 * Starts monitoring for late orders.
	 * Runs every 1 minute to detect missed check-ins.
	 */
	public static void startMonitoringLateOrders() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(() -> {
			checkLateOrders();
		}, 0, 1, TimeUnit.MINUTES);
	}

	/**
	 * Checks active subscriber parkings and sends warnings or late notifications.
	 */
	public static void checkParkingDurations() {
		String query = """
		    SELECT sp.subscriberCode, sp.time, sp.receivingCarTime, sp.numberOfExtends, sp.status,
		           s.email, s.username, sp.parkingCode
		    FROM subscriberparking sp
		    JOIN subscribers s ON sp.subscriberCode = s.code
		    WHERE sp.status = 'ACTIVE'
		""";

		try (Connection conn = mysqlConnection.getInstance().getConnection();
			 PreparedStatement stmt = conn.prepareStatement(query);
			 ResultSet rs = stmt.executeQuery()) {

			LocalTime now = LocalTime.now();

			while (rs.next()) {
				int subscriberCode = rs.getInt("subscriberCode");
				int parkingCode = rs.getInt("parkingCode");
				LocalTime entryTime = rs.getTime("time").toLocalTime();
				Time receivedTimeObj = rs.getTime("receivingCarTime");
				int extensions = rs.getInt("numberOfExtends");
				String email = rs.getString("email");
				String name = rs.getString("username");

				if (receivedTimeObj != null)
					continue;

				LocalTime allowedUntil = entryTime.plusHours(4 + extensions);
				Duration remaining = Duration.between(now, allowedUntil);

				if (remaining.isNegative()) {
					registerLate(parkingCode, subscriberCode);
					sendEmail(email, name, "Your parking time (parking no. " + parkingCode + ") has expired.");
				} else if (remaining.toMinutes() <= 10) {
					sendEmail(email, name, "Only 10 minutes left in your parking time (parking no. " + parkingCode + ").");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if orders are late (not used within 15 minutes of start time).
	 * Sends email and deletes the order.
	 */
	public static void checkLateOrders() {
		String query = """
			SELECT order_number, order_date, startTime, subscriber_id
			FROM `order` WHERE order_date = CURDATE()
		""";

		try (PreparedStatement ps = mysqlConnection.getInstance().getConnection().prepareStatement(query);
			 ResultSet rs = ps.executeQuery()) {

			LocalTime now = LocalTime.now();

			while (rs.next()) {
				LocalTime startTime = rs.getTime("startTime").toLocalTime();
				int subscriberCode = rs.getInt("subscriber_id");
				int order_number = rs.getInt("order_number");
				LocalTime lateThreshold = startTime.plusMinutes(15);

				if (now.isAfter(lateThreshold)) {
					System.out.println("Order " + order_number + " is LATE!");

					String getSubscriber = "SELECT email,username from `subscribers` WHERE code = ?";
					PreparedStatement ps2 = mysqlConnection.getInstance().getConnection().prepareStatement(getSubscriber);
					ps2.setInt(1, subscriberCode);
					ResultSet rs2 = ps2.executeQuery();
					rs2.next();

					String email = rs2.getString("email");
					String name = rs2.getString("username");

					System.out.println("Email :" + email + " Subscriber: " + subscriberCode);
					sendEmail(email, name, "You're late for the order. The spot has been released and your order is cancelled.");

					String deleteRow = "DELETE FROM `order` WHERE order_number = ?";
					PreparedStatement ps3 = mysqlConnection.getInstance().getConnection().prepareStatement(deleteRow);
					ps3.setInt(1, order_number);
					ps3.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a formatted HTML email to the subscriber.
	 *
	 * @param to      recipient email
	 * @param name    recipient name
	 * @param message message content
	 */
	public static void sendEmail(String to, String name, String message) {
		String htmlContent = String.format("""
			<html>
			<body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
				<div style="max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); padding: 30px;">
					<h2 style="color: #004080; text-align: center; margin-bottom: 20px;">Dear %s,</h2>
					<p style="font-size: 20px; text-align: center;">
						<span style="color: red; font-weight: bold;">%s</span>
					</p>
					<div style="text-align: center; margin-top: 30px;">
						<img src="https://www.keflatwork.com/wp-content/uploads/2019/01/parking-lot-with-trees.jpg" alt="Parking Lot" style="width: 100%%; max-width: 550px; border-radius: 6px;" />
					</div>
					<p style="font-size: 16px; text-align: center; margin-top: 40px;">
						Best regards,<br/>
						<strong>Auto BPark</strong>
					</p>
				</div>
			</body>
			</html>
		""", name, message);

		EmailSender.sendEmailDesigned(to, htmlContent);
	}

	/**
	 * Gets number of lateness warnings for a subscriber in a specific parking spot today.
	 *
	 * @param subscriberCode subscriber ID
	 * @param parkingCode    parking code
	 * @return number of warnings today
	 */
	public static int getNumberOfWarnings(int subscriberCode, int parkingCode) {
		try {
			String query = """
				SELECT numberOfWarnings FROM latemessage
				WHERE subscriberCode = ? AND parkingCode = ? AND date = ?
			""";
			Connection conn = mysqlConnection.getInstance().getConnection();
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, subscriberCode);
			pstmt.setInt(2, parkingCode);
			pstmt.setDate(3, Date.valueOf(LocalDate.now()));
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("numberOfWarnings");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Registers a late warning for a subscriber. Either inserts or updates the warning count.
	 *
	 * @param parkingCode    parking spot code
	 * @param subscriberCode subscriber code
	 */
	public static void registerLate(int parkingCode, int subscriberCode) {
		try {
			Connection conn = mysqlConnection.getInstance().getConnection();
			int numberOfWarnings = getNumberOfWarnings(subscriberCode, parkingCode);
			String query;

			if (numberOfWarnings == 0) {
				query = """
					INSERT INTO latemessage (text, date, numberOfWarnings, subscriberCode, parkingCode)
					VALUES (?, ?, ?, ?, ?)
				""";
				PreparedStatement pstmt2 = conn.prepareStatement(query);
				pstmt2.setString(1, "Late for receiving the car");
				pstmt2.setDate(2, Date.valueOf(LocalDate.now()));
				pstmt2.setInt(3, 1);
				pstmt2.setInt(4, subscriberCode);
				pstmt2.setInt(5, parkingCode);
				pstmt2.executeUpdate();
			} else {
				query = """
					UPDATE latemessage
					SET numberOfWarnings = ?
					WHERE subscriberCode = ? AND parkingCode = ? AND date = ?
				""";
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, numberOfWarnings + 1);
				pstmt.setInt(2, subscriberCode);
				pstmt.setInt(3, parkingCode);
				pstmt.setDate(4, Date.valueOf(LocalDate.now()));
				pstmt.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Schedules daily report generation for monthly summary (runs every 24 hours).
	 * Only generates the report if it’s the last day of the month.
	 *
	 * @param monthlyReportController the controller to generate the report
	 */
	public static void startMonthlyReportScheduler(MonthlyReportController monthlyReportController) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

		Runnable task = () -> {
			LocalDate today = LocalDate.now();
			if (today.getDayOfMonth() == today.lengthOfMonth()) {
				System.out.println("📅 Last day of the month — generating report...");
				monthlyReportController.generateMonthlyParkingReport();
			} else {
				System.out.println("📅 Not the last day of the month — skipping report.");
			}
		};

		scheduler.scheduleAtFixedRate(task, 0, 24, TimeUnit.HOURS);
	}

	/**
	 * Schedules daily generation of parking spot reports.
	 *
	 * @param controller the controller used to generate the report
	 */
	public static void scheduleParkingSpotReport(ParkingSpotReportController controller) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

		Runnable task = () -> {
			LocalDate today = LocalDate.now();
			if (today.getDayOfMonth() == today.lengthOfMonth()) {
				System.out.println("📅 Last day of the month — generating parking spot report...");
				controller.generateMonthlyReport();
			} else {
				System.out.println("📅 Not the last day — skipping parking spot report.");
			}
		};

		scheduler.scheduleAtFixedRate(task, 0, 24, TimeUnit.HOURS);
	}
}
