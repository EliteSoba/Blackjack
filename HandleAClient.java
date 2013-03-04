import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

class HandleAClient implements Runnable {
	/*Handles each individual client
	 * Reads commands from clients and sends them to server
	 */
	
		Socket mySocket;
		ObjectOutputStream out;
		ObjectInputStream in;
		Player player;
		Blackjack bj;
		Server serv;
		int ID;
		Drop d;
		boolean ready;
	
		public HandleAClient( Socket s, Server se, int i ) {
			//Constructor
		    mySocket = s;
		    player = new Player("test");
		    bj = new Blackjack();
		    bj.deal();
		    serv = se;
		    ID = i;
		    ready = false;
		    d = serv.getDrop();
		    try {
				//Create the 2 streams for talking to the client
				out = new ObjectOutputStream(mySocket.getOutputStream());
				in = new ObjectInputStream(mySocket.getInputStream());
			    } catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
		    }
		}
		//Getters and setters. Some of these are unused
		public boolean getReady() {
			return ready;
		}
		
		public Socket getSocket() {
			return mySocket;
		}
		
		public void setbj(Blackjack b) {
			bj = b;
		}
		
		public void setName(String s) {
			player.setName(s);
		}
		
		public Player getPlayer() {
			return player;
		}
		
		/*public void readMessage(String message) {
		 	//This used to be used for sending/receiving messages but has since been deprecated
		 
			if (message.equals("tart")) {
				serv.start(0, 5);
			}
			
			if (message.equals("hit")) {
				serv.hit(ID);
			}
			
			/*try {
				Thread.sleep(20);
				bj = serv.getGame();
				//out.writeObject(bj);
			} catch (Exception e) {//e.printStackTrace();
			}
		}*/
		
		public void write(Object o) {
			//Write objects 
			try {
				out.writeObject(o);
			} catch (IOException e) {}
		}
		
		/*public Object read() {
			//Reads object from client. Probably unused, actually.
			try {
				Object o = in.readObject();
				System.out.println(o.toString());
				return o;
			} catch (Exception e) {
				return null;
			}
		}*/
		
		public void close() {
			//Closes the socket. Unused, but nice to have
			try {
				mySocket.close();
			} catch (IOException e) {}
		}
		
		public void update() {
			//Sends updated data to client to redraw graphics
			String s = d.take();
			try {
				out.writeObject(s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void unready() {
			//Setter
			ready = false;
		}
		
		public void run() {
		   //Receives information constantly from clients, sends info to Server, and sends info back to client
		    bj = serv.getGame();
	
		    //System.out.println("Player connexed");
	
		    //This thread loops forever to receive a client message and echo it back
		    while( true ) {
				try {

				    //out.writeObject( bj );
				    //Wait for the client to send a String and print it out
					Object o = in.readObject();
				    String message = (String)o;
				    //if (message != null)
				    //System.out.println("Client message: " + message );
				    //if (message == null)
				    	//mySocket.close();
				    //readMessage(message);
				    //this.wait();
				    //out.writeObject(bj);
				    //serv.update();
				    if (message.equals("start")) {
				    	ready = true;
				    	int bet = Integer.parseInt((String)in.readObject());
				    	
				    	serv.start(ID, bet);
				    	String s = d.take();
				    	p.l("started"+s);
				    	out.writeObject(s);
				    }
				    
				    else if (message.equals("hit")) {
				    	serv.hit(ID);
				    	//String s = d.take();
				    	//out.writeObject(s);
				    	//p.l(s);
				    }
				    
				    else if (message.equals("double")) {
				    	serv.doubleDown(ID);
				    	//String s = d.take();
				    	//out.writeObject(d.take());
				    	//p.l(s);
				    }
				    
				    else if (message.equals("stay")) {
				    	serv.stay(ID);
				    	//String s = d.take();
				    	//out.writeObject(d.take());
				    	//p.l(s);
				    }
				    
				    else if (message.equals("quit")) {
				    	serv.loadDifs((ArrayList<Player>)in.readObject());
				    	serv.standby();
				    }
				    
				    else if (message.equals("np")) {
				    	serv.loadPlayers((ArrayList<Player>)in.readObject());
				    }
				    
				    else if (message.equals("quitnochange")) {
				    	
				    }
					
				    
				    //Send back a response
				    //message = "Server echoes: " + message;
				    //System.out.println(bj.toString());
				} catch (Exception e) {
				}
		    }
		}
	}