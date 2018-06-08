package server.database.entities;

import java.io.Serializable;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import server.database.entities.book.Book;

@Getter
@Setter
public class Order implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8540223415047834830L;
	private static final String PLACE_ORDER_QUERY = "insert into %s(BOOK_ISBN, QUANTITY) values(?, ?)";
	private static final String SELECT_ALL_ORDERS = "SELECT * FROM %s LIMIT %d,%d";
	private static final String DELETE_BY_ID = "DELETE FROM %s WHERE ORDER_ID = ?";
	private static final String BOOK_ORDER_TABLE = "BOOK_ORDER";
	private static final String QUANTITY_COL = "QUANTITY";
	private static final String BOOK_ISBN_COL = "BOOK_ISBN"; 
	private static final String ORDER_ID_COL = "ORDER_ID";
	
	
	private int quantity;
	private Book book;
	private int orderId;
	
	public Order (int quantity, Book book) {
		this.quantity = quantity;
		this.book = book;
	}
	
	public Order() {
		
	}
	
	public static boolean addNewOrder(Order order, Connection connection) {
		try {
			String query = String.format(PLACE_ORDER_QUERY, BOOK_ORDER_TABLE);
			PreparedStatement st = (PreparedStatement) connection.prepareStatement(query);
			st.setString(1, order.book.getBookISBN());
			st.setLong(2, order.quantity);
			st.executeUpdate();
		} catch(SQLException | NumberFormatException sql) {
			return false;
		}
		return true;
	}
	
	public static List<Order> selectAllOrders(int offset, int limit, Connection connection) {
		try {
			String query = String.format(SELECT_ALL_ORDERS, BOOK_ORDER_TABLE, offset, limit);
			PreparedStatement st = (PreparedStatement) connection.prepareStatement(query);
			ResultSet rs = st.executeQuery();
			return composeList(rs);
		} catch(SQLException e) {
			return new ArrayList<>();
		}
	}
	
	private static List<Order> composeList(ResultSet rs) throws SQLException {
		List<Order> orders = new ArrayList<>();
		while(rs.next()) {
			Order order = new Order();
			order.setQuantity(rs.getInt(QUANTITY_COL));
			Book book = new Book();
			book.setBookISBN(rs.getString(BOOK_ISBN_COL));
			order.setOrderId(rs.getInt(ORDER_ID_COL));
			order.setBook(book);
			orders.add(order);
		}
		return orders;
	}
	
	public static boolean deleteOrderById(int id, Connection connection) {
		try {
			String query = String.format(DELETE_BY_ID, BOOK_ORDER_TABLE);
			PreparedStatement st = (PreparedStatement) connection.prepareStatement(query);
			st.setInt(1, id);
			int rowsAffected = st.executeUpdate();
			return rowsAffected == 1;
		} catch(SQLException e) {
			return false;
		}
	}

	 @Override
     public int hashCode() {
		 return book.getBookISBN().hashCode();
	 }
	 
	 @Override
	 public boolean equals(Object obj) {
		 if (this == obj)
             return true;
         if (obj == null)
             return false;
         if (getClass() != obj.getClass())
             return false;
         Order other = (Order) obj;
         return this.getBook().getBookISBN().equals(other.getBook().getBookISBN());
	 }
}
