import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Game extends JPanel implements ActionListener, Runnable {
	
	/*This is the gui client.
	 * It connects to a server, sends requests, receives information, and translates that information into graphics
	 * It used to look a lot prettier before the game logic had to be moved from here over to the server
	 */

	Blackjack bj;
	Player player;
	Insets insets;
	ArrayList<JLabel> pHand, dHand;
	ArrayList<JLabel> pHands;
	JButton hit;
	JButton deal;
	JButton stay;
	JButton doubledown;
	JTextField bet;
	JLabel result;
	JLabel score;
	JLabel Bet;
	JButton quit;
	boolean reveal;
	boolean start;
	boolean dTurn;
	boolean doublable;
	Application app;
	int ID;
	boolean gameEnd;
	volatile Thread t;
	boolean startart;
	JLabel yourTurn;
	int better;
	ArrayList<Player> playold;
	int[] playolds;

	Socket s;
	ObjectOutputStream out;
	ObjectInputStream in;
	
	public Game(Player p, Application a) {
		//Constructor with playing player and app for interaction with playerpanel
		app = a;
		bj = new Blackjack();
		player = p;
		this.setLayout(null);
		insets = this.getInsets();
		this.setPreferredSize(new Dimension(1300, 800)); //1300 x 800
		this.setSize(this.getPreferredSize());
		pHand = new ArrayList<JLabel>();
		dHand = new ArrayList<JLabel>();
		pHands = new ArrayList<JLabel>();
		doublable = true;
		gameEnd = false;
		better = 0;
		startart = true;
		t = new Thread(this);
		this.setBackground(new Color(8, 107, 43));
		this.setButtons();
		start = false;
		setVisibilities(true);
		
		try { //Connection to server and receiving important information
			s = new Socket("localhost", 41079);
			out = new ObjectOutputStream(s.getOutputStream());
			in = new ObjectInputStream(s.getInputStream());
			ID = Integer.parseInt((String)in.readObject());
			playold = (ArrayList<Player>)in.readObject();
			playolds = new int[playold.size()];
			for (int i = 0; i < playold.size(); i++)
				playolds[i] = playold.get(i).getMoney();
			app.loadPlayers(playold);
			
		} catch (UnknownHostException e) {
			System.err.println("Error: Could not resolve host \"aludra.usc.edu:" + 41079 + "\"");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Error: Could not resolve IO");
			System.exit(1);
		} catch (Exception e) {
			System.err.println("Unknown Error. Exiting");
			System.exit(1);
		}
	}
	
	private void setButtons() {
		//Sets buttons and positions
		Bet = new JLabel("Bet: ");
		Bet.setPreferredSize(new Dimension(50, 20));
		this.add(Bet);
		Bet.setBounds(610, 320, 50, 20);
		bet = new JTextField(10);
		bet.setPreferredSize(new Dimension(60, 20));
		this.add(bet);
		bet.setBounds(640, 320, 60, 20);
		deal = new JButton("Ready");
		deal.setPreferredSize(new Dimension(100, 60));
		this.add(deal);
		deal.setBounds(600, 350, 100, 60);
		hit = new JButton("Hit");
		hit.setPreferredSize(new Dimension(100, 60));
		this.add(hit);
		hit.setBounds(600, 280, 100, 60);
		doubledown = new JButton("Double");
		doubledown.setPreferredSize(new Dimension(100, 60));
		this.add(doubledown);
		doubledown.setBounds(600, 360, 100, 60);
		stay = new JButton("Stay");
		stay.setPreferredSize(new Dimension(100, 60));
		this.add(stay);
		stay.setBounds(600, 440, 100, 60);
		
		result = new JLabel("test");
		result.setFont(new Font("LTFinnegan Medium", Font.PLAIN, 24));
		result.setHorizontalAlignment(JLabel.CENTER);
		result.setPreferredSize(new Dimension(200, 30));
		this.add(result);
		result.setBounds(550, 200, 200, 30);
		result.setVisible(false);
		
		yourTurn = new JLabel("Your turn!");
		yourTurn.setFont(new Font("LTFinnegan Medium", Font.PLAIN, 24));
		yourTurn.setHorizontalAlignment(JLabel.CENTER);
		yourTurn.setPreferredSize(new Dimension(200, 30));
		this.add(yourTurn);
		yourTurn.setBounds(550, 200, 200, 30);
		yourTurn.setVisible(false);
		
		score = new JLabel("0 vs 0");
		score.setFont(new Font("LTFinnegan Medium", Font.PLAIN, 24));
		score.setHorizontalAlignment(JLabel.CENTER);
		score.setPreferredSize(new Dimension(100, 60));
		this.add(score);
		score.setBounds(600, 240, 100, 60);
		score.setVisible(false);
		
		deal.addActionListener(this);
		hit.addActionListener(this);
		doubledown.addActionListener(this);
		stay.addActionListener(this);
	}

	private void setVisibilities(boolean b) {
		//Sets which components are visible
		Bet.setVisible(b);
		bet.setVisible(b);
		deal.setVisible(b);
		hit.setVisible(!b);
		doubledown.setVisible(!b);
		stay.setVisible(!b);
		score.setVisible(b && gameEnd);
		result.setVisible(b && gameEnd);
	}
	
	public void newGame(int bets) {
		//Creates a new game. Essentially what happens when "Ready" is pressed
		//bj.newGame(player);
		//write("start");
		try {
			write("start");
			write(bets+"");
			//update((String)in.readObject());
		} catch (Exception e) {
			e.printStackTrace();
		}
		player.setMoney(player.getMoney());
		reveal = false;
		start = true;
		dTurn = false;
		gameEnd = false;
		doublable = true;
		if (startart) {
			startart = false;
			t.start();
		}
		score.setVisible(false);
		result.setVisible(false);
		setVisibilities(false);
		//update();
		
	}
	
	public void write(String s) {
		//Writes a string to the outputstream. Pretty useless method
		try {
			out.writeObject(s);
			//update((String)in.readObject());
		} catch (Exception e) {}
		
	}
	public void update(String s) {
		//Updates the gui based on the raw data string by parsing all the info.
		//You can thank ACM programming contests for giving me the idea on how to parse and write this data
		//p.l(s);
		for (int i = 0; i < pHands.size(); i++) {
			try {
				this.remove(pHands.get(i));
			}
			catch (Exception e) { }
		}
		for (int i = 0; i < dHand.size(); i++) {
			try {
				this.remove(dHand.get(i));
			}
			catch (Exception e) { }
		}
		
		pHand.clear();
		dHand.clear();
		
		this.repaint();
		this.revalidate();
		StringTokenizer tok = new StringTokenizer(s);
		int cur = Integer.parseInt(tok.nextToken());
		if (cur == ID)
			yourTurn.setVisible(true);
		else
			yourTurn.setVisible(false);
		boolean bool = cur == 0;
		int k = Integer.parseInt(tok.nextToken());
		ArrayList<Card> hand = new ArrayList<Card>();
		//String ss;
		for (int i = 0; i < k; i++) {
				hand.clear();
				int j = Integer.parseInt(tok.nextToken());
				//System.out.println("j: " + j);
				//if (i != 0)
					//ss = tok.nextToken();
				for (int l = 0; l < j; l++) {
					if (i == 0) {
						if (l != 0 || bool)
							dHand.add(new JLabel(Card.fromString(tok.nextToken()).getImage()));
						else
							dHand.add(new JLabel(Card.fromString(tok.nextToken()).cardBack));
						dHand.get(l).setBounds(600 + 21*l + insets.left, 50 + insets.top, Card.BASE, Card.HEIGHT);
						this.add(dHand.get(l), 0);
					}
					else {
						pHand.add(new JLabel(Card.fromString(tok.nextToken()).getImage()));
						this.add(pHand.get(l), 0);
						pHand.get(l).setBounds(600 - 500 * ((i)%2) + 1000 * ((i-1)/2) + 21*(l) + insets.left,  720 - 300*((i)%2) - Card.HEIGHT, Card.BASE, Card.HEIGHT);
						pHands.add(pHand.get(l));
					}
				}
				pHand.clear();
			}
		if (bool) {
			ArrayList<String> scores = new ArrayList<String>();
			for (int i = 0; i < k; i++)
				scores.add(tok.nextToken());
			int wins = -1;
			for (int i = 1; i < k; i++) {
				wins = Integer.parseInt(tok.nextToken());
				if (i == ID)
					break;
			}
			player.lose(better);
			switch (wins) {
				case 0: break;
				case 1: player.win((int)(2.5 * better)); break;
				case 2: player.win((int)(2 * better)); break;
				case 3: player.win((int)(better)); break;
				case 4: break;
				default: player.win((int)(2 * better)); break;
			}
			score.setText(scores.get(ID) + " vs " + scores.get(0));
			gameEnd = true;
			result.setText(Blackjack.wintToString(wins));
			System.out.println(bool);
			//t.interrupt();
			setVisibilities(true);
			this.revalidate();
			this.repaint();
		}
	}
	private void update() {
		//Old update method when game logic was in here. Deprecated
		System.out.println("Game: " + bj.toString());
		for (int i = 0; i < pHands.size(); i++) {
			try {
				this.remove(pHands.get(i));
			}
			catch (Exception e) { }
		}
		for (int i = 0; i < dHand.size(); i++) {
			try {
				this.remove(dHand.get(i));
			}
			catch (Exception e) { }
		}
		
		pHand.clear();
		dHand.clear();
		
		//this.rootPane.repaint();
		//this.rootPane.revalidate();
		this.repaint();
		this.revalidate();
		for (int j = 0; j < bj.getPlayersSize(); j++) {
			for (int i = 0; i < bj.getPlayerHand(j).size(); i++) {
				pHand.add(new JLabel(bj.getPlayerHand(j).get(i).getImage()));
				this.add(pHand.get(i), 0);
				pHand.get(i).setBounds(600 - 500 * ((j+1)%2) + 1000 * (j/2) + 21*i + insets.left,  720 - 300*((j+1)%2) - Card.HEIGHT, Card.BASE, Card.HEIGHT);
				pHands.add(pHand.get(i));
			}
			pHand.clear();
		}
		
		for (int i = 0; i < bj.getDealerHand().size(); i++) {
			if (i != 0 || bj.getGameEnd())
				dHand.add(new JLabel(bj.getDealerHand().get(i).getImage()));
			else
				dHand.add(new JLabel(Card.cardBack));
			this.add(dHand.get(i), 0);
			dHand.get(i).setBounds(600 + 21*i + insets.left, 50 + insets.top, Card.BASE, Card.HEIGHT);
		}
		
		if (bj.getGameEnd()) {
			score.setText(bj.playerTotal(bj.getCurPlayer()) + " vs " + bj.dealerTotal());
			result.setText(bj.calculateWin(bj.getCurPlayer()));
			this.revalidate();
			this.repaint();
			
		setVisibilities(true);
		}
		
	}
	
	public void changePlayer(Player p) {
		//Changes the curent player
		player = p;
	}
	
	public void quit(ArrayList<Player> obj) {
		//Quits the game and alerts server of changes to playerlist
		try {
			out.writeObject("quit");
			out.writeObject(genDifs(obj));
		} catch(Exception e) {
			System.err.println("qq");
			System.exit(1);
		}
	}
	
	public ArrayList<Player> genDifs(ArrayList<Player> obj) {
		//For dealing with money synching when multiple clients are connected
		ArrayList<Player> olds = playold;
		
		for (int i = 0; i < obj.size(); i++) {
			if (i < playolds.length)
				olds.get(i).setMoney(obj.get(i).getMoney()-playolds[i]);
			else
				olds.add(obj.get(i));
		//	p.l(playolds[i] + ", " + obj.get(i).getMoney());
		}
		
		return olds;
	}
	
	public void actionPerformed(ActionEvent arg0) {
		//Upon each button press, sends signal to server to perform an action
		Object source = arg0.getSource();
		int bets = 0;
		
		if (source == deal && player != null) {
			try {
				bets = Integer.parseInt(bet.getText());
			} catch (Exception e){bets = 0;}
			if (bets > 0 && bets <= player.getMoney()) {
				this.newGame(bets);
				better = bets;
				//bj.bet(bets);
				//bj.deal();
				//update();
				setVisibilities(false);
			}
		}
		
		if (source == hit && start) {
			//if (!bj.hit(bj.getCurPlayer()));
				//bj.stay(bj.getCurPlayer());
			doublable = false;
			try {
				//out.writeObject("hit");
				//bj = (Blackjack)in.readObject();
				//System.out.println(bj.toString());
				write("hit");
				//update();
			} catch (Exception e) {
			}
			//update();
		}
		
		if (source == doubledown && start && doublable) {
			if (player.getMoney() >= better) {
				doublable = false;
				//better *= 2;
				//if (!bj.doubleDown(bj.getCurPlayer()));
					//bj.stay(bj.getCurPlayer());
				//else
					//bj.stay(bj.getCurPlayer());
				try {
					//out.writeObject("double");
					write("double");
					//bj = (Blackjack)in.readObject();
				} catch (Exception e) {
				}
			}
			//update();
		}
		
		if (source == stay) {
			//bj.stay(bj.getCurPlayer());
			try {
				//out.writeObject("stay");
				write("stay");
				//bj = (Blackjack)in.readObject();
			} catch (Exception e) {
			}
			//update();
		}
		
		app.update();
		
	}
	
	public void sendPlayers(ArrayList<Player> pls) {
		//Sends player database to server when new player is added
		try {
			out.writeObject("np");
			out.writeObject((pls));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.exit(0);
		}
	}
	
	/*
	private void stay(boolean b) {
		if (!bj.isBlackjack(bj.getPlayerHand(bj.getCurPlayer())))
			dTurn |= b;
		if (bj.rotatePlayer()) {
			reveal = true;
			bj.dealerTurn(dTurn);
			update();
			start = false;
			//for (int i = 0; i < bj.getPlayersSize(); i++) {
			if (bj.getGameEnd()) {
				score.setText(bj.playerTotal(bj.getCurPlayer()) + " vs " + bj.dealerTotal());
				result.setText(bj.calculateWin(bj.getCurPlayer()));
				score.setVisible(true);
				result.setVisible(true);
				this.revalidate();
				this.repaint();
			}
			setVisibilities(true);
		}
	}*/
	/*
	public static void main (String args[]) {
		JFrame test = new JFrame();
		Game g = new Game(new Player("Test"));
		g.setVisible(true);
		test.add(g);
		test.setVisible(true);
		test.pack();
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//g.setResizable(false);
	}*/

	public void run() {
		//Constantly reads from server and updates gui
		try {
		while (true) {
			Object o = in.readObject();
			if (o!= null)
				update((String)o);
			Thread.sleep(20);
		}
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
			//run();
		}
		
	}

}
