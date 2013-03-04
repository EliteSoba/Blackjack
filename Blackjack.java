import java.io.Serializable;
import java.util.*;


public class Blackjack implements Serializable{
	
	/*All of the game logic for Blackjack.
	 * Does everything for the game based on small input messages.
	 * Most of the commented lines were for the transfer from singular gui to server
	 * The code can get a bit messy at times, but I don't want to delete the comments
	 * because some of them are crucial for the conversion back and understanding the steps a method takes
	 * Some unused methods remain because they were necessary to fulfil HW2 requirements, but unnecessary/deprecated for HW3
	 */

	//ArrayList<Card> playerHand;
	ArrayList<Card> dealerHand;
	Deck deck;
	ArrayList<Player> players;
	int shuffleMark;
	boolean shuffleFlag;
	ArrayList<Integer> curBet;
	int curPlayer;
	public static final int MAX_PLAYERS = 3;
	public static final boolean BUST = false;
	boolean gameEnd;
	boolean dTurn;
	
	public Blackjack() {
		//Constructor
		//playerHand = new ArrayList<Card>();
		dealerHand = new ArrayList<Card>();
		players = new ArrayList<Player>();
		deck = new Deck();
		gameEnd = false;
		curBet = new ArrayList<Integer>();
		//newGame(p);
	}
	
	public void newGame(Player p) {
		//Creates a new game with new players
		players.clear();
		players.add(p);
		curBet.add(0);
		curPlayer = 0;
		for (int i = 0; i < players.size(); i++)
			players.get(i).clearHand();
		dealerHand.clear();
		gameEnd = false;
		dTurn = false;
		shuffle();
	}
	
	public void addPlayer(Player p) {
		//Adds another player to the game.
		if (players.size() <= MAX_PLAYERS) {
			players.add(p);
			curBet.add(0);
		}
	}
	
	public void removePlayer(Player p) {
		//Removes a player from the game. Pretty sure this is unused.
		players.remove(p);
	}
	
	public void shuffle() {
		//Shuffles the deck and sets when the deck should be shuffled again
		deck.shuffle();
		shuffleMark = deck.getCard(51).getVal();
		shuffleFlag = false;
	}
	
	public void deal() {
		//Deals cards out to each player
		gameEnd = false;
		for (int i = 0; i < players.size(); i++)
			players.get(i).clearHand();
		dealerHand.clear();
		
		for (int i = 0; i < 4; i++) {
			if (deck.getCard(i).getVal() == shuffleMark)
				shuffleFlag = true;
		}
		
		if (shuffleFlag) {
			shuffle();
		}
		
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < players.size(); j++)
				players.get(j).drawCard(deck.toBottom());
			dealerHand.add(deck.toBottom());
		}
	}
	
	public boolean hit(int player) {
		//Has the player hit if it is their turn; otherwise, ignores
		if (player != curPlayer && player != -1)
			return true;
		if (deck.getCard(0).getVal() == shuffleMark)
			shuffleFlag = true;
		if (player != -1) {
			players.get(player).drawCard(deck.toBottom());
			if (playerTotal(player) > 21) {
				stay(player);
				return false;
			}
		}
		else {
			dealerHand.add(deck.toBottom());
			if (dealerTotal() > 21)
				return false;
		}
		return true;
	}
	
	public boolean doubleDown(int player) {
		//Same as above, but with double
		if (player != curPlayer)
			return true;
		if (players.get(player).getMoney() < curBet.get(player))
			return true;
		players.get(player).lose(curBet.get(player));
		curBet.set(player, curBet.get(player)*2);
		if (hit(player)) {
			stay(player);
			return true;
		}
		stay(player);
		return false;
	}
	
	public void setCurBet(int index, int betar) {
		//Sets the bet of the indexed player
		curBet.set(index, betar);
	}
	
	public void stay(int player) {
		//Player stays if it is their turn. Handles logic for passing reins over to next player
		if (player != curPlayer)
			return;
		p.l(curPlayer);
		if (rotatePlayer()) {
			p.l("testest");
			dealerTurn(continues());
		}
	}
	
	public ArrayList<Card> getPlayerHand(int index) {
		//Returns the hand of the indexed player
		return players.get(index).getHand();
	}
	
	public int playerTotal(int index) {
		//Returns the indexed player's blackjack total
		int sum = 0;
		for (int i = 0; i < players.get(index).getHand().size(); i++)
			sum += players.get(index).getHand().get(i).getBJNum();
		if (hasAce(players.get(index).getHand()) && sum + 10 <=21)
			sum += 10;
		return sum;
	}
	
	public ArrayList<Card> getDealerHand() {
		//Returns the dealer's hand
		return dealerHand;
	}

	public int dealerTotal() {
		//Returns the dealer's blackjack total
		int sum = 0;
		for (int i = 0; i < dealerHand.size(); i++)
			sum += dealerHand.get(i).getBJNum();
		if (hasAce(dealerHand) && sum + 10 <=21)
			sum += 10;
		return sum;
	}
	
	public void bet(int b) {
		//Sets a bet for the current player. Pretty sure this is unused
		curBet.set(curPlayer, b);
		players.get(curPlayer).lose(curBet.get(curPlayer));
	}
	
	public void bet(int index, int b) {
		//Sets bet for the indexed player replaces bet
		curBet.set(index, b);
		p.l("teste");
		players.get(index).lose(curBet.get(index));
	}
	
	public void dealerTurn(boolean b) {
		//If the dealer's turn matters, he will hit until 17
		if (b)
			while (dealerTotal() < 17 && hit(-1));
		gameEnd = true;
		
	}
	
	public boolean continues() {
		//If the dealer needs to hit
		for (int i = 0; i < players.size(); i++)
			if (!(isBlackjack(players.get(i).getHand()) || playerTotal(i) > 21))
				return true;
		return false;
	}
	
	public int calculateWint(int index) {
		//Calculates the results of the game for the indexed player. For raw data to be sent to/from server and converted with wintToString
		if (playerTotal(index) > 21)
			return 0;
		
		if (isBlackjack(players.get(index).getHand())) {
			players.get(index).win((int)(2.5 * curBet.get(index)));
			return 1;
		}
		
		if (dealerTotal() > 21) {
			players.get(index).win(2 * curBet.get(index));
			return 2;
		}
		
		if (playerTotal(index) == dealerTotal()) {
			players.get(index).win(curBet.get(index));
			return 3;
		}
		
		if (playerTotal(index) < dealerTotal()) {
			return 4;
		}
		
		players.get(index).win(2 * curBet.get(index));
		return 5;
	}
	
	public static String wintToString(int win) {
		//Converts raw data from calculateWint to intelligible string
		switch (win) {
			case 0: return "Player loss";
			case 1: return "Blackjack!";
			case 2: return "Player win";
			case 3: return "Push";
			case 4: return "Player loss";
			default: return "Player win";
		}
	}
	
	public String calculateWin(int index) {
		//Completely deprecated and unused.
		if (playerTotal(index) > 21)
			return "Player loss";
		
		if (isBlackjack(players.get(index).getHand())) {
			players.get(curPlayer).win((int)(2.5 * curBet.get(curPlayer)));
			return "Blackjack!";
		}
		
		if (dealerTotal() > 21) {
			players.get(curPlayer).win(2 * curBet.get(curPlayer));
			return "Player win";
		}
		
		if (playerTotal(index) == dealerTotal()) {
			players.get(curPlayer).win(curBet.get(curPlayer));
			return "Push";
		}
		
		if (playerTotal(index) < dealerTotal()) {
			return "Player loss";
		}
		
		
		players.get(curPlayer).win(2 * curBet.get(curPlayer));
		return "Player win";
		
			
	}
	
	public boolean hasAce(ArrayList<Card> hand) {
		//Returns if the hand has an ace to add to calculating soft totals
		for (int i = 0; i < hand.size(); i++)
			if (hand.get(i).getNum() == 1)
				return true;
		return false;
	}
	
	public boolean isBlackjack(ArrayList<Card> hand) {
		//Returns if the given hand is a blackjack
		if (hand.size() != 2)
			return false;
		if (!hasAce(hand))
			return false;
		for (int i = 0; i < 2; i++)
			if (hand.get(i).getBJNum() == 10)
				return true;
		return false;
	}
	
	/******************************************
	
	public static void main (String args[]) {
		Blackjack bj = new Blackjack();
		bj.newGame(new Player("Hello"));
		while (true) {
		bj.deal();
		
		System.out.println(bj.playerTotal());
		if (bj.isBlackjack(bj.getPlayerHand()))
			System.out.println("Blackjack!");
		System.out.println(bj.dealerTotal());
		
		new Scanner(System.in).next();
		}
	}
	 ****************************************************/
	
	//Getters and Setters. Some are unused, but kept in regardless because they have potential
	public Deck getDeck() {
		return deck;
	}

	public void setDeck(Deck deck) {
		this.deck = deck;
	}

	public void setDealerHand(ArrayList<Card> dealerHand) {
		this.dealerHand = dealerHand;
	}
	
	public int getCurBet() {
		return curBet.get(curPlayer);
	}
	
	public int getBet(int index) {
		return curBet.get(index);
	}
	
	public int getCurPlayer() {
		return curPlayer;
	}
	
	public void setCurPlayer(int cp) {
		curPlayer = cp;
		if (curPlayer < 0)
			curPlayer = 0;
		if (curPlayer >= players.size());
			curPlayer = players.size()-1;
	}
	
	//Rotates the current player. Returns true if it's the dealer's turn
	public boolean rotatePlayer() {
		curPlayer++;
		if (curPlayer >= players.size()) {
			curPlayer = 0;
			return true;
		}
		return false;
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public int getPlayersSize() {
		return players.size();
	}
	
	public String decktoString() {
		return deck.toString();
	}
	
	public void deckfromString(String s) {
		//NO ERROR CHECKING LIKE A BOSS.
		deck.fromString(s);
	}
	
	public boolean getGameEnd() {
		return gameEnd;
	}
	
	//Raw String data to be sent to/from server. Contains most important information for drawing and nothing else.
	public String toString() {
		String s = (gameEnd?0:curPlayer+1) + " ";
		s += (players.size()+1) + " ";
		s += dealerHand.size() + " ";
		for (int i = 0; i < dealerHand.size(); i++)
			s += dealerHand.get(i).toString() + " ";
		for (int i = 0; i < players.size(); i++) {
			s += this.getPlayerHand(i).size() + " ";
			//s += players.get(i).getName() + " ";
			for (int j = 0; j < players.get(i).getHand().size(); j++)
				s += players.get(i).getHand().get(j).toString() + " ";
		}
		
		if (gameEnd) {
			s += dealerTotal() + " ";
			for (int i = 0; i < players.size(); i++)
				s += playerTotal(i) + " ";
			for (int i = 0; i < players.size(); i++)
				s += calculateWint(i) + " ";
		}
		
		return s;
	}
	
}
