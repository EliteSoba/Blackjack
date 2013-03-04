import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**TODO: Insert betting field into this panel, remove from Game.  - DONE ENOUGH
 * Add game end status to Game window - DONE
 * Reintegrate betting into PlayerPanel or something
 * 		possibly just keep it like this and change "Deal" to "Ready" - DONE
 * Add AI
 * Add seats to game to allow for multiple players: addPlayer function
 * Add server that allows for saving/loading player database
 * Client class that holds its own Application
 * 		Client to Server communication transfers deck data back and forth
 * 		Server handles initial deal?
 * The way my Blackjack class works is, it could be put into the Server easily and requests could be dealt in and out
 * 		Would have to completely change the current frontend, though.
 * Add another thread to each client to constantly read game status from server and update visuals accordingly
 * 		game status gets passed back and forth as a String, following the format:
 * 			n 0 [HAND] 1 [HAND] 2 [HAND]...
 * 				n is the number of players
 * 				0, 1, 2... is the number of the player whose hand succeeds 0 or -1 is dealer; check numbering scheme
 * 				[HAND] is the string data of the player's hand. Value is NUM+'A'. ex: 'B' has value 1.
 * 		either read by blackjack or entirely new class dedicated to providing gui DONE (?)
 * Also turn bj bet into an arraylist instead of an int
 * sitting vs readying and standing to add
 * SOCKETMANAGER DIRECTLY TELL SERVER WHAT TO DO, THEN RECEIVE UPDATED GAME INFO FROM SERVER, THEN SEND BACK TO CLIENT
 * 
 * 
 * NEW TODO:
 * 		CREATE AI CLASS
 * 		EVERYTHING RELATING TO PLAYERS
 */ //This was my todo list. I don't want to delete it because it contains my thought processes, despite it having quite a few useless steps

public class PlayerPanel extends JPanel implements ActionListener{
	
	/*This is the PlayerPanel. 
	 * It has a PlayerInfo for player info and controls creating new players and selecting players and the quit button because it's more aesthetically appealing here
	 */

	PlayerInfo playerInfo;
	Player player;
	JList playerList;
	ArrayList<Player> players;
	JButton start;
	JButton info;
	JButton quit;
	JButton newPlayer;
	JTextField newName;
	JButton newConfirm;
	Application app;
	
	public PlayerPanel(Application a) {
		//Constructor. Requires Application to sync with Game
		app = a;
		this.setPreferredSize(new Dimension(200, 800));
		//this.setLayout(new BoxLayout(this, 1));
		this.setLayout(null);
		playerInfo = new PlayerInfo();
		//players = new ArrayList<Player>();
		loadPlayers();
		player = players.get(0);
		update();
		this.add(playerInfo);
		playerList = new JList(playerNames());
		playerList.setSize(100, 300);
		playerList.setPreferredSize(new Dimension(100, 300));
		start = new JButton("Start");
		this.add(start);
		start.addActionListener(this);
		info = new JButton("Info");
		this.add(info);
		info.addActionListener(this);
		quit = new JButton("Quit");
		this.add(quit);
		quit.addActionListener(this);
		newPlayer = new JButton("New Player");
		this.add(newPlayer);
		newPlayer.addActionListener(this);
		newName = new JTextField(5);
		this.add(newName);
		newName.setMaximumSize(new Dimension(100, 20));
		newConfirm = new JButton("Confirm");
		this.add(newConfirm);
		newConfirm.addActionListener(this);
		playerList.setLayoutOrientation(JList.VERTICAL);
		playerList.setVisibleRowCount(-1);
		
		this.add(playerList);
		newName.setVisible(false);
		newConfirm.setVisible(false);
		
		playerInfo.setBounds(50, 0, 100, 75);
		playerList.setBounds(50, 75, 100, 300);
		start.setBounds(50, 400, 100, 50);
		info.setBounds(50, 460, 100, 50);
		quit.setBounds(50, 520, 100, 50);
		newPlayer.setBounds(50, 580, 100, 50);
		newName.setBounds(50, 640, 100, 20);
		newConfirm.setBounds(50, 670, 100, 50);
		
		/*playerInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
		playerList.setAlignmentX(Component.CENTER_ALIGNMENT);
		start.setAlignmentX(Component.CENTER_ALIGNMENT);
		newPlayer.setAlignmentX(Component.CENTER_ALIGNMENT);
		newName.setAlignmentX(Component.CENTER_ALIGNMENT);
		newConfirm.setAlignmentX(Component.CENTER_ALIGNMENT);*/
	}
	
	public void selectPlayer(Player p) {
		//Selects a player
		player = p;
		playerInfo.updatePlayer(player);
	}
	
	public void update() {
		//Updates playerinfo
		playerInfo.updatePlayer(player);
	}
	
	public Player getPlayer() {
		//Returns the current player
		return player;
	}
	
	public void actionPerformed(ActionEvent arg0) {
		//For dealing with each button pressed
		Object source = arg0.getSource();
		
		if (source == newPlayer) {
			newName.setVisible(true);
			newConfirm.setVisible(true);
		}
		
		else if (source == newConfirm) {
			if (nonDuplicate(newName.getText()) && newName.getText().length() > 0) {
				players.add(new Player(newName.getText()));
				newName.setVisible(false);
				newConfirm.setVisible(false);
				this.remove(playerList);
				playerList = new JList(playerNames());
				this.add(playerList);
				playerList.setBounds(50, 75, 100, 300);
				this.revalidate();
				this.repaint();
				app.newPlayered();
			}
		}
		
		else if (source == quit) {
			app.quit(players);
			System.exit(1);
		}
		
		else if (source == start) {
			int index = playerList.getMinSelectionIndex();
			if (index != -1) {
				selectPlayer(players.get(index));
				app.setPlayer(player);
				app.startGame();
			}
		}
		
		else if (source == info) {
			int index = playerList.getMinSelectionIndex();
			if (index != -1) {
				selectPlayer(players.get(index));
			}
		}
	}
	
	public String[] playerNames() {
		//For the JList
		String s[] = new String[players.size()];
		for (int i = 0; i < players.size(); i++)
			s[i] = players.get(i).getName();
		return s;
	}
	
	public void savePlayers() {
		//Saves players to text file
		try {
			FileOutputStream f = new FileOutputStream("player.txt");
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeObject(players);
			o.close();
		} catch (Exception e) {
			
		}
	}
	
	public void loadPlayers(ArrayList<Player> ps) {
		//Loads player database from another player database
		players = ps;
	}
	
	public void loadPlayers() {
		//Loads players from file
		try {
			FileInputStream f = new FileInputStream("player.txt");
			ObjectInputStream o = new ObjectInputStream(f);
			players = (ArrayList<Player>)o.readObject();
			o.close();
		} catch (Exception e) {
			players = new ArrayList<Player>();
			players.add(new Player("Guest"));
		}
	}
	
	public boolean nonDuplicate(String name) {
		//Ensures no duplication in names
		for (int i = 0; i < players.size(); i++)
			if (name.equals(players.get(i).getName()))
				return false;
		return true;
	}
	
	public ArrayList<Player> getPlayers() {
		//Returns player list
		return players;
	}
}
