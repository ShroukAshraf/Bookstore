package client.alphabit;

import javafx.application.Application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import server.database.entities.Book;
import server.database.entities.Identity;
import server.database.entities.Order;
import server.database.entities.ShoppingCart;
import view.CustomController;

public class BookStoreApp extends Application {

	private static final String APP_TITLE = "Alphabet Bookstore";
	private static final String LOGIN_VIEW = "/LoginView.fxml";
	private static final String REGISTER_VIEW = "/RegisterView.fxml";
	private static final String MANAGER_VIEW = "/ManagerView.fxml";
	private static final String CUSTOMER_VIEW = "/CustomerView.fxml";
    private static final String BOOK_VIEW = "/BookView.fxml";
    private static final String ORDERS_VIEW = "/OrdersView.fxml";
	private Stage primaryStage;
	private static ControlForm login, register, manager, customer, bookView, ordersView;
	private static ShoppingCart currentCart;
	private static Identity userIdentity;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(APP_TITLE);
		currentCart = new ShoppingCart();
		login = new Controller(primaryStage, LOGIN_VIEW);
		register = new Controller(primaryStage, REGISTER_VIEW);
		manager = new Controller(primaryStage, MANAGER_VIEW);
		customer = new Controller(primaryStage, CUSTOMER_VIEW);
		bookView = new Controller(primaryStage, BOOK_VIEW);
		ordersView = new Controller(primaryStage, ORDERS_VIEW);
		//showOrdersView();
		//getOrderViewController().initData(null);
		//showLogin();
	}
	
	public static void showLogin() {
		login.show();
	}
	
	public static void showRegister() {
		register.show();
	}

	public static void showBookView() {
		bookView.show();
	}
	
	public static void showOrdersView() {	
		ordersView.show();
		
	}
	
	public static CustomController getBookViewController() {
		return bookView.getController();
	}

	public static CustomController getOrderViewController() {
		return ordersView.getController();
	}
	public static void showCustomer(Identity userIdentity) {
		BookStoreApp.userIdentity = userIdentity;
		customer.show();
	}
	
	public static void showManager() {
		manager.show();
	}
	
	public static Identity getUser() {
		return userIdentity;
	}
	
	public static ShoppingCart getShoppingCart() {
		return currentCart;
		
	}
	public static void displayDialog(AlertType alertType, String dialogTitle, String dialogHeader, String dialogText) {
		Alert alert = new Alert(alertType);
		alert.setTitle(dialogTitle);
		alert.setHeaderText(dialogHeader);
		alert.setContentText(dialogText);
		alert.showAndWait();
	}
	
	private static void pushSomeOrders() {
		float x = 1f;
		for (int i = 1; i <= 40; i++) {
			Book book = new Book(Integer.toString(i), new String("boook" + i),"1960", x, "arts", true);
			currentCart.addOrder(new Order(i, book));
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
