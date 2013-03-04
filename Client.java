import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class Client implements Runnable{
	
	/*This is the AI
	 * Receives info from Server and, based on that information, sends commands back
	 * Runs for one dealing cycle 
	 */

	Socket s;
	ObjectOutputStream out;
	ObjectInputStream in;
	int ID;
	ArrayList<Player> players;
	ArrayList<Card> dHand;
	ArrayList<Card> pHand;
	Thread t;
	String status;
	
	public Client() {
		//Constructor
		try {
			s = new Socket("localhost", 41079);
			out = new ObjectOutputStream(s.getOutputStream());
			in = new ObjectInputStream(s.getInputStream());
			ID = Integer.parseInt((String)in.readObject());
			players = (ArrayList<Player>)in.readObject();
			out.writeObject("start");
			out.writeObject("0");
			status = " ";
			pHand = new ArrayList<Card>();
			t = new Thread(this);
			t.start();
		} catch (Exception e) {}
	}
	
	public void update(String status) {
		//Determines the next command when it is his turn
		StringTokenizer tok = new StringTokenizer(status);
		int turn = Integer.parseInt(tok.nextToken());
		p.l(turn);
		if (turn != ID)
			return;
		int k = Integer.parseInt(tok.nextToken());
		tok.nextToken();
		tok.nextToken();
		int dh = Card.fromString(tok.nextToken()).getBJNum();
		for (int i = 1; i < k; i++) {
			pHand.clear();
			int j = Integer.parseInt(tok.nextToken());
			for (int l = 0; l < j; l++)
				pHand.add(Card.fromString(tok.nextToken()));
			if (i == ID)
				i = k;
		}
		
		int sum = 0;
		boolean blackjack = false;
		for (int i = 0; i < pHand.size(); i++) {
			sum += (pHand.get(i).getBJNum());
			if (pHand.get(i).getBJNum() == 1)
				blackjack = true;
		}
		if (blackjack) {
			sum += 10;
		}
		try { //credit goes to http://www.blackjackinfo.com/bjbse.php?numdecks=1+deck&soft17=s17&dbl=all&das=yes&surr=ns&peek=yes
			  //simplified for this level of programming.
			if (sum >= 17)
				out.writeObject("stay");
			else if (sum <= 16 && sum >= 13 && dh >= 2 && dh <= 6)
				out.writeObject("stay");
			else if (sum == 12 && dh >= 4 && dh <= 6)
				out.writeObject("stay");
			else if (sum == 11)
				out.writeObject("double");
			else if (sum == 10 && dh > 1 && dh < 10)
				out.writeObject("double");
			else if (sum == 9 && dh > 1 && dh < 7)
				out.writeObject("double");
			else if (sum == 8 && dh > 4 && dh < 7)
				out.writeObject("double");
			else
				out.writeObject("hit");
		} catch (IOException e) {}
		
	}
	
	
	
	public static void main(String args[]) {
		//Makes a client
		Client c = new Client();
	}

	public void run() {
		//Constantly reads from server and writes to it
		try{
			while (status.charAt(0) != '0') {
				status = (String)in.readObject();
				update(status);
			}
			out.writeObject("quit");
			out.writeObject(players);
		} catch(Exception e) {e.printStackTrace();}
		
	}
}
