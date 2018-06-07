package server;

import java.util.ArrayList;


import java.util.HashMap;

import javax.jws.WebMethod;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import server.database.entities.Author;
import server.database.entities.Book;
import server.database.entities.Identity;
import server.database.entities.User;
import server.UserResponseData;
import server.database.entities.UserBuilder;


@WebService
@SOAPBinding(style = Style.DOCUMENT)
public interface BookStoreServer {

	@WebMethod
	ResponseData addNewUser(UserBuilder newUserBuilder);

	@WebMethod
	UserResponseData editUserInformation(UserBuilder modifiedUser);

	@WebMethod
	ResponseData editUserIdentity(Identity identity, String newPassword);

	@WebMethod
	ArrayList<Book> searchBook(String filter, String valueFilter);

	@WebMethod
	boolean addNewBook(Book newBook, Author author, server.database.entities.Publisher publisher);

	@WebMethod
	boolean editBook(Book modifiedBook);

	@WebMethod
	boolean promoteUser(HashMap<String, User> temp);
	
	@WebMethod
	UserResponseData loginUser(Identity identity);

	@WebMethod
	boolean isManager(Identity identity);
	
	@WebMethod
	boolean placeOrder(String isbn, String quantity);
	
	byte[] generateReport(Identity identity, String reportType);

}
