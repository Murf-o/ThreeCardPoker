
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GameServer extends GameLogic{
	public int clientsConnected;
	public int clientsDropped;
	private TheServer server;
	private Thread serverT;
	private Consumer<Serializable> callback;	//called when clients connect
	private Consumer<Serializable> callbackDisc;//called when clients disconnect
	private Consumer<Serializable> callbackNewHand;
	ArrayList<ClientRunnable> clients = new ArrayList<ClientRunnable>();
	Map<Integer, ClientData> clientDataList = new HashMap<Integer, ClientData>();	//index 0 represents client #1, and so on
	public int count;	//# total clients connected
	public boolean isServerOn;
	private int portNum;
	private ServerSocket mySocket;
	private int maxClients;
	
	public GameServer(Consumer<Serializable> call, Consumer<Serializable> callDisc, Consumer<Serializable> callHand) {
		this.callback = call;
		this.callbackDisc = callDisc;
		this.callbackNewHand = callHand;
		this.server = new TheServer();
		this.count = 0;
		this.isServerOn = false;
		this.clientsConnected = 0;
		this.portNum = 0;
		this.maxClients = 4;
	}
	
	//start up server thread
	public void turnOn() {
		isServerOn = true;
		serverT = new Thread(server);
		serverT.start();
	}
	
	//turn off server, interrupt server thread
	public void turnOff() {
		isServerOn = false;
		try {
			mySocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//close all clients
		for(ClientRunnable client: clients) {
			client.closeConnection();
		}
		
		//serverT.interrupt();
	}
	
	public class TheServer implements Runnable{
		

		@Override
		public void run() {
			
			try{
				mySocket = new ServerSocket(portNum);
				isServerOn = true;
				while(true) {
					Socket clientSock = mySocket.accept();
					ClientRunnable clientR = new ClientRunnable(clientSock, ++count);
					++clientsConnected;
					//if more than 'maxClients' clients already connected, close client connection
					if(clientsConnected > maxClients) {
						clientR.sendServerFull();
						clientSock.close();
						--clientsConnected;
						--count;
					}
					else {
						clients.add(clientR);
						clientDataList.put(count, new ClientData(count));
						callback.accept("Client #" + count);
						Thread t = new Thread(clientR);	//put runnable in a thread
						t.start();
					}
				}
			}
			catch(Exception e){
				System.out.println(e.getMessage());
				isServerOn = false;
				//mySocket.close();
				return;
			}
		}
	}
		
	//receives data sent from client
	public class ClientRunnable implements Runnable{
		
		public int clientNum;
		Socket connection;
		ObjectInputStream in;
		ObjectOutputStream out;
		
		ClientRunnable(Socket s, int clientNum){
			this.connection = s;
			this.clientNum = clientNum;	
		}
		
		private void sendServerFull() {
			try {
				out = new ObjectOutputStream(connection.getOutputStream());
				in = new ObjectInputStream(connection.getInputStream());
				connection.setTcpNoDelay(true);
				PokerInfo serverFull = new PokerInfo(true);
				out.writeObject(serverFull);
				out.close();
				in.close();
			}catch(IOException e) {System.out.println(e.getMessage());}
		}
		
		private void closeConnection() {
			try {
				in.close();
				out.close();
				connection.close();
			}catch(IOException e) {System.out.println(e.getMessage());}
		}

		@Override
		public void run() {
			try {
				out = new ObjectOutputStream(connection.getOutputStream());
				in = new ObjectInputStream(connection.getInputStream());
				connection.setTcpNoDelay(true);
				
				//send clientNum to client, through PokerInfo
				PokerInfo info = new PokerInfo();
				info.clientNum = this.clientNum;
				
				out.writeObject(info);
			}
			catch(Exception e) {
				System.out.println(e.getMessage());
				clientsConnected--;
				return;
			}
			
			//deal cards, etc -- handle pokerInfo objects sent from client
			while(true) {
				try {
					
					PokerInfo info = (PokerInfo)in.readObject();	//blocking call
					if(info.playingAnotherHand) {callbackNewHand.accept(info.clientNum); out.writeObject(info);}
					else {
						handlePokerInfo(info);	
						storeGame(info);
						out.writeObject(info);	//send object
					}
					
				}
				catch(IOException e) {
					//client disconnected
					--clientsConnected;
					callbackDisc.accept(clientNum);
					break;
				}
				catch(Exception e) {
					--clientsConnected;
					callbackDisc.accept(clientNum);
					System.out.println(e.getMessage());
					break;
				}
				
			}
			
		}
	}
	public void storeGame(PokerInfo info) {
		//if game not finished
		if(info.clientFold == false && info.clientPlay == false) {return;}
		clientDataList.get(info.clientNum).addGame(info.anteWager, info.pairPlusWager, info.betWinnings, info.clientFold, info.clientPlay, info.events);
		return;
	}
	public void setPort(int portNum) {
		this.portNum = portNum;
	}
}
