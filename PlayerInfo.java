import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class PlayerInfo extends JPanel{
	
	/*Displays the info of the currently selected player
	 * Please install the font Linotype Finnegan for optimal results
	 * The font should be included with the rest of the project
	 */
	
	Player player;
	JLabel name;
	JLabel money;
	
	public PlayerInfo() {
		//Constructor
		this.setLayout(new BoxLayout(this, 1));
		name = new JLabel("None");
		money = new JLabel("$0");
		this.add(name);
		this.add(money);
		this.setPreferredSize(new Dimension(200, 110));
		update();
	}
	
	public PlayerInfo(Player p) {
		//Another constructor
		this.setLayout(new BoxLayout(this, 1));
		player = p;
		name = new JLabel(player.getName());
		money = new JLabel("$" + player.getMoney());
		this.add(name);
		this.add(money);
		update();
	}
	
	public void updatePlayer(Player p) {
		//Changes player and updates information
		player = p;
		this.remove(name);
		this.remove(money);
		name = new JLabel(player.getName());
		money = new JLabel("$" + player.getMoney());
		this.add(name);
		this.add(money);
		update();
	}
	
	public void update() {
		//Updates graphical parts
		name.setFont(new Font("LTFinnegan Medium",Font.PLAIN, 24));
		money.setFont(new Font("LTFinnegan Medium",Font.PLAIN, 24));
		if (!money.getFont().getFamily().equals("LTFinnegan Medium"))
			System.out.println("Please install Linotype Finnegan on your computer for maximum viewing pleasure");
		name.setAlignmentX(Component.CENTER_ALIGNMENT);
		money.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.repaint();
		this.revalidate();
	}
	
	/*public static void main(String args[]) {
		JFrame test = new JFrame();
		PlayerPanel p = new PlayerPanel();
		test.add(p);
		test.setVisible(true);
		test.pack();
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//test.setSize(200, 110);
	}*/

}
