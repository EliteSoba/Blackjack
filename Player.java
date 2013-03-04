import java.io.Serializable;
import java.util.ArrayList;


public class Player implements Serializable{
	
	/*Player class
	 * holds the player's name, money, and hand, and has mutators for each.
	 */

	String name;
	int money;
	ArrayList<Card> hand;
	
	public Player(String s) {
		//Constructor
		name = s;
		money = 1000;
		hand = new ArrayList<Card>();
	}
	
	//Getters and setters, essentially
	public void win(int p) {
		money += p;
	}
	
	public void lose(int p) {
		money -= p;
	}
	
	public void setMoney(int m) {
		money = m;
	}
	
	public int getMoney() {
		return money;
	}
	
	public void setName(String s) {
		name = s;
	}
	
	public String getName() {
		return name;
	}
	
	public void setHand(ArrayList<Card> c) {
		hand = c;
	}
	
	public ArrayList<Card> getHand() {
		return hand;
	}
	
	public void clearHand() {
		hand.clear();
	}
	
	public void drawCard(Card c) {
		hand.add(c);
	}
}
