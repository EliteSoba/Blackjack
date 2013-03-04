//Server.java
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	
	/*Server. Contains the game logic
	 * Receives messages from clients, adjusts game accordingly, and sends back data to be drawn.
	 * Everything has to be run on the same computer because some firewall settings got messed up for me.
	 * If you want, you can try to change the server settings in Game.java or Client.java, but I personally cannot.
	 */
	
	ServerSocket ss = null;
	Socket s = null;
	boolean start;
	ArrayList<HandleAClient> players;
	Blackjack game;
	Drop d;
	ArrayList<Integer> bets;
	ArrayList<Player> ps;
	
	
    public Server() {
    	//Constructor
    	game = new Blackjack();
    	players = new ArrayList<HandleAClient>();
    	start = false;
    	loadPlayers();
    	d = new Drop();
    	bets = new ArrayList<Integer>();
    	try {
    		ss = new ServerSocket( 41079 );
    	} catch( Exception e ) {
    		//e.printStackTrace();
    		System.exit(0);
    	}
    	standby();
    }
    
    public void savePlayers() {
    	//Saves player database to external file
		try {
			FileOutputStream f = new FileOutputStream("player.txt");
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeObject(ps);
			o.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public void loadPlayers() {
    	//Load player database from external file
		try {
			FileInputStream f = new FileInputStream("player.txt");
			ObjectInputStream o = new ObjectInputStream(f);
			ps = (ArrayList<Player>)o.readObject();
			o.close();
		} catch (Exception e) {
			ps = new ArrayList<Player>();
			ps.add(new Player("Guest"));
		}
	}
    
    public Drop getDrop() {
    	//For coordination with HandleAClients
    	return d;
    }
    
    public void loadPlayers(ArrayList<Player> plays) {
    	//Loads player database from arraylist
    	ps = plays;
    	savePlayers();
    }
    
    public void loadDifs(ArrayList<Player> plays) {
    	//Adjusts player database from dif arraylist
    	for (int i = 0; i < plays.size(); i++) {
    		if (i < ps.size())
    			ps.get(i).win(plays.get(i).getMoney());
    		else
    			ps.add(plays.get(i));
    	}
    	savePlayers();
    }
    
    public void standby() {
    	//Waits to accept players
    	players.clear();
    	System.out.println("Current state: standby");
    	
    	while ( players.size() < 3) {
    		try {
    			s = ss.accept();
    			
    			HandleAClient hac = new HandleAClient( s , this, players.size());
    			new Thread(hac).start();
    			
    			players.add(hac);
    			bets.add(0);
    			players.get(players.size()-1).write(players.size()+"");
    			players.get(players.size()-1).write(ps);
    			
    		} catch (Exception e) {
    			System.out.println("Connection failed: " + e.getMessage() );
    		}
	    
	    System.out.println( "Player connected" );
    	}
    	
    	//start();
    }
    
    public boolean getReadys() {
    	//If players are ready to be dealt to
    	for (int i = 0; i < players.size(); i++)
    		if (!players.get(i).getReady())
    			return false;
    	return true;
    }
    
    public void start(int ID, int bet) {
    	//Deals to players
    	System.out.println("Current state: start");
    	bets.set(ID, bet);
    	
    	if (!getReadys()) {
    		game.setCurPlayer(-1);
    		return;
    	}
    	for (int i = 0; i < players.size(); i++) {
    		players.get(i).unready();
    		
    	}
    	game = null;
    	game = new Blackjack();
    	p.l("success");
    	game.newGame(players.get(0).getPlayer());
    	game.bet(0, bets.get(0));
    	for (int i = 1; i < players.size(); i++) {
    		game.addPlayer(players.get(i).getPlayer());
    		game.bet(i, bets.get(i));
    	}
    	game.deal();
    	p.l(game.toString() + "server");
    	for (int i = 0; i < players.size(); i++)
    		d.put(game.toString());
    	//update();
    }
    
    public ArrayList<Player> getPlayers() {
    	//Getter
    	return ps;
    }
    
    public void hit(int player) {
    	//Has the player hit if it's his turn
    	game.hit(player);
    	if (game.playerTotal(player) > 21)
    		stay(player);
    	else
    		//d.put(game.toString());
    	update();
    }
    public void doubleDown(int player) {
    	//Has the player double if it's his turn
    	game.doubleDown(player);
    	//d.put(game.toString());
    	stay(player);
    	update();
    }
    public void stay(int player) {
    	//Has the player stay if it's his turn. Goes to gameend state if game ends
    	game.stay(player);
    	//game.dealerTurn(true);
    	//d.put(game.toString());
    	update();
    	if (game.getGameEnd())
    		end();
    }
    
    public int getCurPlayer() {
    	//Getter
    	return game.curPlayer;
    }
    
    public void update() {
    	//Sends clients information to be drawn
    	for (int i = 0; i < players.size(); i++) {
    		d.put(game.toString());
    		players.get(i).update();
    	}
				//players.get(i).setbj(game);
    }
    
    public void end() {
    	//Could've implemented an accept part here to allow, but didn't
    	System.out.println("Current state: end");
    	boolean ends[] = new boolean[players.size()];
    	for (int i = 0; i < ends.length; i++)
    		ends[i] = false;
    	while (netArray(ends)) {
    		for (int i = 0; i < players.size(); i++)
    			//if (players.get(i).getTurn() == 'r')
    				ends[i] = true;
    	}
    	
    	//start();
    }
    
    public static boolean netArray(boolean[] ends) {
    	//Returns ||'d total of array
    	boolean total = false;
    	for (int i = 0; i < ends.length; i++)
    		total |= ends[i];
    	return total;
    }
    
    synchronized Blackjack getGame() {
    	//Getter
    	return game;
    }
    
    /*
    private void stay(boolean b) {
    	//Deprecated old game logic. Didn't work anyways
		if (!game.isBlackjack(bj.getPlayerHand(bj.getCurPlayer())))
			dTurn |= b;
		if (bj.rotatePlayer()) {
			reveal = true;
			bj.dealerTurn(dTurn);
			update();
			start = false;
			//for (int i = 0; i < bj.getPlayersSize(); i++) {
				score.setText(bj.playerTotal(bj.getCurPlayer()) + " vs " + bj.dealerTotal());
				result.setText(bj.calculateWin(bj.getCurPlayer()));
				score.setVisible(true);
				result.setVisible(true);
				this.revalidate();
				this.repaint();
			//}
			setVisibilities(true);
		}
	}*/
    
    //A few getters
    public ArrayList<Card> getHand(int index) {
    	if (index < 0 || index >= players.size())
    		return null;
    	return players.get(index).getPlayer().getHand();
    }
    
    public int getLastPlayer() {
    	return game.getPlayersSize()-1;
    }

    public static void main( String[] args ) {
    	//Starts up a new server
    	Server server = new Server();
    }
}