import java.io.Serializable;

import javax.swing.ImageIcon;

public class Card implements Serializable{
	/*Card class for generating a single card
	 * Has the raw data value and converts that to a suit and number
	 * and, in this case, a number for the blackjack value
	 * 
	 */

	public final static int BASE = 73;
	public final static int HEIGHT = 97;
	private int value;
	private int suit, num;
	private String ID;
	private ImageIcon cardImage;
	public static ImageIcon cardBack = new ImageIcon("Cards/b.gif");
	
	public Card(int v) {
		//Constructor
		value = v;
		suit = v / 13;
		num = v % 13;
		
		switch (num) {
		case 0: ID = "a"; break;
		case 9: ID = "t"; break;
		case 10: ID = "j"; break;
		case 11: ID = "q"; break;
		case 12: ID = "k"; break;
		default: ID = (num + 1) + "";
		}
		switch (suit) {
		case 0: ID += "d"; break;
		case 1: ID += "c"; break;
		case 2: ID += "h"; break;
		case 3: ID += "s"; break;
		}

		//Gets the image using the magic of strings.
		cardImage = new ImageIcon("Cards/" + ID + ".gif");
		
	}
	
	//Getters and Setters
	public int getVal() {
		return value;
	}
	
	public int getBJNum() {
		if (num+1 >= 10)
			return 10;
		return num+1;
	}
	
	public int getSuit() { //0 = Diamond, 1 = Club, 2 = Heart, 3 = Spade
		return suit;
	}
	
	public int getNum() {
		return num+1;
	}
	
	public String getID() {
		return ID;
	}
	
	public ImageIcon getImage() {
		return cardImage;
	}
	
	//For transfer to and from server - I like sending a String message full of weird characters and reparsing that into usable data
	public String toString() {
		return (char)('A' + value) + "";
	}
	
	public static Card fromString(String s) {
		return new Card(s.charAt(0)-'A');
	}
	
}
