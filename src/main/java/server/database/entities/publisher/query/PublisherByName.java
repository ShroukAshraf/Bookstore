package server.database.entities.publisher.query;

import java.sql.Connection;
import java.sql.SQLException;

import lombok.Setter;
import server.database.Query;
import server.database.entities.publisher.PublisherModel;

public class PublisherByName extends Query {
	private static final String SELECT_WITH_COND = "SELECT ID FROM %s WHERE NAME = ?";
	private static final int NAME_INDEX = 1;
	
	@Setter private String name;
	
	@Override
	protected void prepareStatement(Connection connection) throws SQLException {
		String query = String.format(SELECT_WITH_COND, PublisherModel.PUBLISHER_TABLE);
		ps = connection.prepareStatement(query);
		ps.setString(NAME_INDEX, name);
	}
}
