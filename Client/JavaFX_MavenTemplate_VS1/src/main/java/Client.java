
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;


public class Client implements Runnable{
	Socket socketClient;
	ObjectOutputStream out;
	ObjectInputStream in;
	private String ipAddress;
	private int portNum;
	public int anteWager;
	public int pairPlusWager;
	public int totalWinnings;
	public int clientNum;
	PokerInfo info;
	Consumer<Serializable> callBack; //failure with server, return client back to start
	
	public Client(String ipAddress, int portNum, Consumer<Serializable> call) {
		this.ipAddress = ipAddress;
		this.portNum = portNum;
		this.anteWager = 0;
		this.pairPlusWager = 0;
		this.totalWinnings = 0;
		this.clientNum = -1;
		this.info = new PokerInfo();
		this.callBack = call;
		//set up info with client num
		
	}
	
	//sends wages to server, server returns new pokerInfo object --
	//this pokerInfo object should contain the dealersCards
	public void sendWagers(int anteWage, int pairPlusWage) {
		this.anteWager = anteWage;
		this.pairPlusWager = pairPlusWage;
		this.info.anteWager = anteWage;
		this.info.pairPlusWager = pairPlusWage;
		
		try {
			out.writeObject(info);				//send pokerInfo object
			info = (PokerInfo) in.readObject();	//blocking call, wait to receive, pokerInfo object
			this.totalWinnings = info.totalWinnings;
		}catch(IOException e) {
			System.out.println(e.getStackTrace());
			System.out.println(e.getMessage());
			callBack.accept(0);
		}catch(Exception e) {
			callBack.accept(1);
			System.out.println(e.getMessage());
		}
	}
	
	public void send() {
		try {
			out.writeObject(info);				//send pokerInfo object
			info = (PokerInfo) in.readObject();	//blocking call, wait to receive, pokerInfo object
			this.totalWinnings = info.totalWinnings;
			
		}catch(IOException e) {
			callBack.accept(1);
			System.out.println(e.getMessage());
			System.out.println("failed to SEND PokerInfo object -- from client side");
		}catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("failed to RECEIVE PokerInfo object -- from client side");
		}
	}
	
	public int connect() throws Exception {
		try {
			socketClient = new Socket(this.ipAddress, this.portNum);
			out = new ObjectOutputStream(socketClient.getOutputStream());
		    in = new ObjectInputStream(socketClient.getInputStream());
		    socketClient.setTcpNoDelay(true);
		    
		    //get pokerInfo object from server, with clientNum
		    this.info = (PokerInfo)in.readObject();
		    
		  //server FULL, closed connection
		    if(info.serverFull) {
		    	in.close();
		    	out.close();
		    	socketClient.close();
		    	return 0;
		    }	
		    clientNum = this.info.clientNum;
		}
		catch(Exception e) {	//invalid ip address, etc
			in.close();
			out.close();
			socketClient.close();
			throw new Exception("connectionE");}	
		return 1;	//success
	}
	
	@Override
	public void run(){
		
		try {
			socketClient = new Socket(this.ipAddress, this.portNum);
			out = new ObjectOutputStream(socketClient.getOutputStream());
		    in = new ObjectInputStream(socketClient.getInputStream());
		    socketClient.setTcpNoDelay(true);
		    
		    //get pokerInfo object from server, with clientNum
		    this.info = (PokerInfo)in.readObject();
		    clientNum = this.info.clientNum;
		    
		   // while(true) {	//read pokerInfo here?
		    	
		   // }
		}
		catch(Exception e) {callBack.accept(1);System.out.println("client disconnected");}	
		
		
	}
	
}
