package view;

import java.time.LocalDate;

import java.util.Optional;
import client.alphabit.BookStoreApp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Pair;
import server.database.entities.Order;
import server.database.entities.ShoppingCart;

public class OrdersController implements CustomController {

	private static final String CONFIRM_REMOVE = "Are you sure you want to delete this order?";
	private static final String DELETE_IMG = "../images/delete.png";
	private static final String PAYMENT_IMG = "../images/Creditcard.png";
	private static final String ERROR_IN_CREDIT = "Enter correct credit number";
	private static final String ERROR_IN_DATE = "Enter correct expiry date";
	private static final String INVALID_INPUT = "Invalid Input";
	private static final int MIN_CREDITCARD_DIGITS = 8;
	private static final int MAX_CREDITCARD_DIGITS = 19;

	@FXML
	private Label totalPrices;
	@FXML
	private GridPane ordersPane;
	@FXML
	private Label userName;
	
	private void showItems() {
		ShoppingCart currentCart = BookStoreApp.getShoppingCart();
		int index = 1;
		float totalPrice = 0f;
		for (Order order : currentCart) {
			Text indexTxt = new Text(Integer.toString(index));
			ordersPane.add(indexTxt, 0, index);

			Text titleTxt = new Text(order.getBook().getBookTitle());
			ordersPane.add(titleTxt, 1, index);

			Text priceTxt = new Text(Float.toString(order.getBook().getSellingPrice()));
			ordersPane.add(priceTxt, 2, index);
			totalPrice += order.getBook().getSellingPrice();

			Text quantityTxt = new Text(Integer.toString(order.getQuantity()));
			ordersPane.add(quantityTxt, 3, index);

			Image imageDecline = new Image(getClass().getResourceAsStream(DELETE_IMG), 20, 20, true, true);
			Button removeMe = new Button("", new ImageView(imageDecline));
			ordersPane.add(removeMe, 4, index);

			removeMe.setGraphic(new ImageView(imageDecline));
			removeMe.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					Alert conf = new Alert(AlertType.CONFIRMATION, CONFIRM_REMOVE);
					Optional<ButtonType> result = conf.showAndWait();
					if (result.isPresent() && result.get().equals(ButtonType.OK)) {
						currentCart.removeOrder(order);
						ordersPane.getChildren().clear();
						showItems();
					}
				}
			});

			index++;
		}
		totalPrices.setText(Float.toString(totalPrice));
	}

	@Override
	public void initData(Parameters parameters) {
		showItems();
		//userName.setText(BookStoreApp.getUser().getUserName());

	}
    
	@FXML
	private void goHome() {
		BookStoreApp.showCustomer(BookStoreApp.getUser());
	}
	
	
	@FXML
	private void checkOut() {
		showCreditCardPanel();
	}
	
	
    private void showCreditCardPanel() {
    	Dialog<Pair<String, String>> dialog = new Dialog<>();
    	dialog.setTitle("Payment Step");
    	dialog.setHeaderText("PLease Enter your Payment Method");
        ImageView creditCardView = new ImageView(this.getClass().getResource(PAYMENT_IMG).toString());
        creditCardView.setFitHeight(100);
        creditCardView.setFitWidth(100);
        creditCardView.setPreserveRatio(true);

    	// Set the icon (must be included in the project).
    	dialog.setGraphic(creditCardView);

    	// Set the button types.
    	ButtonType buyButtonType = new ButtonType("Buy", ButtonData.OK_DONE);
    	dialog.getDialogPane().getButtonTypes().addAll(buyButtonType, ButtonType.CANCEL);

    	
    	GridPane grid = new GridPane();
    	grid.setHgap(10);
    	grid.setVgap(10);
    	grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker expiryDatePicker = new DatePicker(LocalDate.now());

    	TextField creditCardNumber = new TextField();
    	creditCardNumber.setPromptText("Enter your Number");

    	grid.add(new Label("Expiry Date:"), 0, 0);
    	grid.add(expiryDatePicker, 1, 0);
    	grid.add(new Label("Credit Card Number:"), 0, 1);
    	grid.add(creditCardNumber, 1, 1);

    	// Enable login button.
    	Node buyButton = dialog.getDialogPane().lookupButton(buyButtonType);
    	
    	buyButton.addEventFilter(
    		    ActionEvent.ACTION, 
    		    event -> {
    		       
    		       if (!validateCreditCard(creditCardNumber.getText())) {
    		            BookStoreApp.displayDialog(AlertType.ERROR, INVALID_INPUT, null, ERROR_IN_CREDIT);
    		            event.consume();
    		        } else if (expiryDatePicker.getValue().isBefore(LocalDate.now())){
    		            BookStoreApp.displayDialog(AlertType.ERROR, INVALID_INPUT, null, ERROR_IN_DATE);
                         
    		        }
    		    }
    		);
  
    	dialog.getDialogPane().setContent(grid);

    	Platform.runLater(() -> expiryDatePicker.requestFocus());
    
    	((ButtonBase) buyButton).setOnAction(
    			 event -> {
    				 BookStoreApp.showCustomer(BookStoreApp.getUser());
    			 }
    			);
 

       dialog.showAndWait();
    }
    
    private static boolean validateCreditCard(String creditCardNum) {
    	
    	String pattern = "[0-9]+";
    	if (creditCardNum.matches(pattern) && creditCardNum.length() >= MIN_CREDITCARD_DIGITS && creditCardNum.length() <= MAX_CREDITCARD_DIGITS) {
    		return performLuhnAlgorthim(creditCardNum);
    	}
    	return false;
    }
    
    private static boolean performLuhnAlgorthim(String creditCardNum) {
    	
    	int sum = 0;
    	int [] intValues = new int [] {0,2,4, 6, 8, 1, 3, 5, 7, 9};
    	for (int i = 0; i < creditCardNum.length(); i++) {
    		if (i % 2 == 1) {
    			sum += intValues[creditCardNum.charAt(i) - '0'];
    		} else {
    			sum += creditCardNum.charAt(i) - '0';
    		}
    	}
    	if (sum % 10 == 0) {
    		return true;
    	}
        return false;
    }
}