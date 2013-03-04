import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Application extends JFrame implements ActionListener{
	
	/*Top-end application.
	 * Deals with all the interaction between panels.
	 * That's it.
	 */
	
	Game game;
	PlayerPanel pPanel;
	Player player;
	
	public Application() {
		//Constructor
		this.setLayout(null);
		pPanel = new PlayerPanel(this);
		pPanel.setBounds(1320,0,200,800);
		player = pPanel.getPlayer();
		game = new Game(player, this);
		game.setBounds(0, 0, 1300, 800);
		this.add(game);
		this.add(pPanel);
	}
	
	public void setPlayer(Player p) {
		//Sets current player
		player = p;
	}
	
	public void startGame() {
		//Loads game with current player
		game.changePlayer(player);
		//game.newGame();
	}
	
	public void loadPlayers(ArrayList<Player> ps) {
		//Load player database to playerpanel
		pPanel.loadPlayers(ps);
	}
	
	public void newPlayered() {
		//Tells game to update server player list
		game.sendPlayers(pPanel.getPlayers());
	}
	
	public void update() {
		//Updates player panel
		pPanel.update();
	}
	
	public void quit(ArrayList<Player> plays) {
		//Tells game to quit and tell server to save player data
		game.quit(plays);
	}

	public static void main(String args[]) {
		//Gui gets drawn here. Please have at least 1600 x 800, preferably 1920 x 1080.
		Application app = new Application();
		app.setVisible(true);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setSize(1600, 800);
		if (!app.getSize().equals(new Dimension(1600, 800)))
			System.out.println("Please have a monitor resolution of at least 1920 x 1080 to play this game.");
	}
	
	public void actionPerformed(ActionEvent arg0) {
		// Does nothing, actually. Has the potential to do something.
		
	}
	
	
	
}
