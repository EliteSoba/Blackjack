import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Deck implements Serializable{
	/*This is the class for the deck of cards.
	 * All it does is contain the cards in an ArrayList and shuffles.
	 * If it weren't for the shuffling, this would be no different from a normal ArrayList.
	 */
	ArrayList<Card> d;
	
	public Deck() {
		//Constructor. Initializes 52 cards.
		
		d = new ArrayList<Card>();
		for (int i = 0; i < 52; i++) {
			d.add(new Card(i));
		}
		shuffle();
	}
	
	public void shuffle() {
		//Shuffles the deck.
		Random gen = new Random();
		for (int i = 0; i < d.size(); i++) {
			d.add(d.remove(gen.nextInt(d.size()-i)));
		}
	}
	
	public Card getCard(int index) {
		//Returns the card at the given index, or the nearest valid card.
		
		if (index > 51)
			return d.get(51);
		if (index < 0)
			return d.get(0);
		return d.get(index);
	}
	
	public Card toBottom() {
		//Returns the top card and sends it to the bottom of the deck.
		
		Card c = d.remove(0);
		d.add(c);
		return c;
	}
	
	public String toString() {
		String s = "";
		for (int i = 0; i < d.size(); i++)
			s += d.get(i).getVal() + 'a';
		return s;
	}
	
	public void fromString(String s) {
		//NOERRORCHECKINGLIKEABOSS
		for (int i= 0; i < s.length(); i++) {
			d.set(i, new Card(s.indexOf(i) - 'a'));
		}
	}

}
