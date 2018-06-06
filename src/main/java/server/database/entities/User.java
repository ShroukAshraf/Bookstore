package server.database.entities;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import server.ResponseData;

public class User implements Serializable {

	private static final long serialVersionUID = -6108052518548327564L;

	public static final String USER_TABLE = "USERS";

	private static final String NEW_USER_QUERY = "Insert into %s(USER_NAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD_SHA1, PHONE_NUMBER, ADDRESS)"
			+ "VALUES (?, ?, ?, ?, SHA1(?), ?, ?);";
	private static final int INSERT_USER_NAME_INDEX = 1;
	private static final int INSERT_FIRST_NAME_INDEX = 2;
	private static final int INSERT_LAST_NAME_INDEX = 3;
	private static final int INSERT_EMAIL_INDEX = 4;
	private static final int INSERT_PASSWORD_INDEX = 5;
	private static final int INSERT_PHONE_NUMBER_INDEX = 6;
	private static final int INSERT_ADDRESS_INDEX = 7;

	private static final String GET_USER_BY_EMAIL = "Select * from %s where EMAIL = ?;";
	private static final String GET_USER_BY_USER_NAME = "Select * from %s where USER_NAME = ?";

	private static final String SHORTER_THAN_ALLOWED_ERROR = "Field can't be less than";
	private static final String LONGER_THAN_ALLOWED_ERROR = "Field can't be longer than";

	private static final String DUPLICATE_EMAIL_ERROR = "User with this email already registered";
	private static final String DUPLICATE_USER_NAME_ERROR = "User with this user name already registered";

	private Identity identity;
	private String firstName;
	private String lastName;
	private String email;
	private String address;
	private String phoneNumber;

	public User() {
		this.identity = new Identity();
	}

	public User(String userName, String email, String firstName, String lastName, String password, String phoneNumber,
			String address) {
		this.identity = new Identity();
		this.identity.setUserName(userName);
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.identity.setPassword(password);
		this.address = address;
		this.phoneNumber = phoneNumber;
	}

	public User (ResultSet rs) throws SQLException {
		this.identity = new Identity();
		this.identity.setUserName(rs.getString(INSERT_USER_NAME_INDEX));
		this.identity.setPassword(rs.getString(INSERT_USER_NAME_INDEX));
		this.firstName = rs.getString(INSERT_FIRST_NAME_INDEX);
		this.lastName = (rs.getString(INSERT_LAST_NAME_INDEX));
		this.address = rs.getString(INSERT_ADDRESS_INDEX);
		this.phoneNumber = rs.getString(INSERT_PHONE_NUMBER_INDEX);
		this.email = rs.getString(INSERT_EMAIL_INDEX);
		
	}
	@Override
	public String toString() {

		return "User [firstName=" + firstName + ", lastName=" + lastName + ", userName=" + identity.getUserName()
				+ ", email=" + email + ", password=" + identity.getPassword() + ", address=" + address
				+ ", phoneNumber=" + phoneNumber;
	}

	public void registerUser(Connection connection, String tableName) throws SQLException {
		PreparedStatement registerUserQuery = createInsertUserQuery(connection, tableName);
		registerUserQuery.execute();
	}

	public String validateData() {
		StringBuilder sb = new StringBuilder();
		List<String> attributes = Arrays.asList("User Name", "First Name", "Last Name", "Email", "Password", "Address",
				"Phone Number");
		List<String> values = Arrays.asList(identity.getUserName(), email, firstName, lastName, identity.getPassword(),
				phoneNumber, address);
		List<Integer> maxLength = Arrays.asList(20, 20, 20, 20, 40, 15, 50);
		List<Integer> minLength = Arrays.asList(5, 5, 5, 5, 8, 8, 5);
		for (int i = 0; i < attributes.size(); i++) {
			String value = values.get(i);
			String attribute = attributes.get(i);
			int attributeMinLength = minLength.get(i);
			int attributeMaxLength = maxLength.get(i);
			if (value.length() < attributeMinLength) {
				sb.append(attribute + " " + SHORTER_THAN_ALLOWED_ERROR + " " + attributeMinLength);
			} else if (value.length() > attributeMaxLength) {
				sb.append(attribute + " " + LONGER_THAN_ALLOWED_ERROR + " " + attributeMaxLength);
			}
		}
		return sb.length() != 0 ? sb.toString() : null;
	}

	private PreparedStatement createInsertUserQuery(Connection con, String tableName) throws SQLException {
		String query = String.format(NEW_USER_QUERY, tableName);
		PreparedStatement st = con.prepareStatement(query);
		st.setString(INSERT_USER_NAME_INDEX, identity.getUserName());
		st.setString(INSERT_FIRST_NAME_INDEX, firstName);
		st.setString(INSERT_LAST_NAME_INDEX, lastName);
		st.setString(INSERT_EMAIL_INDEX, email);
		st.setString(INSERT_PASSWORD_INDEX, identity.getPassword());
		st.setString(INSERT_PHONE_NUMBER_INDEX, phoneNumber);
		st.setString(INSERT_ADDRESS_INDEX, address);
		return st;
	}

	public static ResponseData addNewUser(User user, Connection connection) {
		ResponseData rs = new ResponseData();
		String userErrors = user.validateData();
		try {
			if (userErrors != null) {
				rs.setError(userErrors);
			} else if (user.isAlreadyRegisteredUserName(connection)) {
				rs.setError(DUPLICATE_USER_NAME_ERROR);
			} else if (user.isAlreadyRegisteredEmail(connection)) {
				rs.setError(DUPLICATE_EMAIL_ERROR);
			} else {
				user.registerUser(connection, USER_TABLE);
			}
		} catch (SQLException e) {
			rs.setError("Couldn't execute query");
			e.printStackTrace();
		}
		return rs;
	}

	private boolean isAlreadyRegisteredEmail(Connection connection) throws SQLException {
		return isAlreadyRegistered(connection, GET_USER_BY_EMAIL, email);
	}

	private boolean isAlreadyRegisteredUserName(Connection connection) throws SQLException {
		return isAlreadyRegistered(connection, GET_USER_BY_USER_NAME, identity.getUserName());
	}

	private boolean isAlreadyRegistered(Connection connection, String selectionType, String attribute)
			throws SQLException {
		String query = String.format(selectionType, USER_TABLE);
		PreparedStatement st = connection.prepareStatement(query);
		st.setString(1, attribute);
		ResultSet rs = st.executeQuery();
		return rs.next();
	}

	public void setUserName(String userName) {
		identity.setUserName(userName);
	}

	public String getUserName() {
		return identity.getUserName();
	}

	public boolean isManager() {
		return false;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return identity.getPassword();
	}

	public void setPassword(String password) {
		identity.setPassword(password);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
