package server.database.entities;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ShoppingCart implements Iterable<Order>{

	private Set<Order> orders;
	
	public ShoppingCart() {
		orders = new HashSet<>();
	}
	
	public void addOrder (Order newOrder) {
		if (orders.contains(newOrder)) {
			orders.add(newOrder);
		}
	}
	
	public void removeOrder (Order removedOrder) {
		orders.remove(removedOrder);
	}
	
	public void clearCart () {
		orders.clear();
	}

	@Override
	public Iterator<Order> iterator() {
		// TODO Auto-generated method stub
		return orders.iterator();
	}
}
