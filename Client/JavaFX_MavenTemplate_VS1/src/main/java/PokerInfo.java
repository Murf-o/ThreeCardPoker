import java.io.Serializable;
import java.util.ArrayList;

public class PokerInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public int anteWager;
	public int pairPlusWager;
	public int clientNum;
	
	//did the client fold or play
	public boolean clientFold;
	public boolean clientPlay;
	
	//cards of both user and dealer
	public ArrayList<Integer> playerCardsType;
	public ArrayList<Integer> playerCardsNum;
	public ArrayList<Integer> dealerCardsType;
	public ArrayList<Integer> dealerCardsNum;
	
	public ArrayList<String> events;	//events of the game put in here
	
	public int betWinnings;
	public int totalWinnings;
	
	public boolean playingAnotherHand;
	public boolean dealerInvalidHand;
	public boolean lostGame;
	public boolean drawGame;
	public ArrayList<ArrayList<Integer>> deck;
	public boolean serverFull;
	
	public PokerInfo() {
		anteWager = 0;
		pairPlusWager = 0;
		clientFold = false;
		clientPlay = false;
		playerCardsType = new ArrayList<Integer>();
		playerCardsNum = new ArrayList<Integer>();
		dealerCardsNum = new ArrayList<Integer>();
		dealerCardsType = new ArrayList<Integer>();
		events = new ArrayList<String>();
		betWinnings = 0;
		totalWinnings = 0;
		dealerInvalidHand = false;
		playingAnotherHand = false;
		lostGame = false;
		drawGame = false;
		newDeck();
		serverFull = false;
	}
	
	public PokerInfo(boolean serverFull) {
		this.serverFull = serverFull;
	}
	
	public void newDeck() {
		deck = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < 4; ++i) {
			deck.add(new ArrayList<Integer>());
			for(int j = 0; j < 13; ++j) {
				deck.get(i).add(1);
			}
		}
	}
	
	//reset variables, totalWinnings stays the same
	public void newGame() {
		anteWager = 0;
		pairPlusWager = 0;
		clientFold = false;
		clientPlay = false;
		events = new ArrayList<String>();
		betWinnings = 0;
		dealerInvalidHand = false;
		playingAnotherHand = false;
		lostGame = false;
		drawGame = false;
		newDeck();
		playerCardsType = new ArrayList<Integer>();
		playerCardsNum = new ArrayList<Integer>();
		dealerCardsNum = new ArrayList<Integer>();
		dealerCardsType = new ArrayList<Integer>();
		serverFull = false;	//not necessary
	}
	
	public void freshStart() {
		anteWager = 0;
		pairPlusWager = 0;
		clientFold = false;
		clientPlay = false;
		events = new ArrayList<String>();
		betWinnings = 0;
		dealerInvalidHand = false;
		playingAnotherHand = false;
		lostGame = false;
		drawGame = false;
		newDeck();
		playerCardsType = new ArrayList<Integer>();
		playerCardsNum = new ArrayList<Integer>();
		dealerCardsNum = new ArrayList<Integer>();
		dealerCardsType = new ArrayList<Integer>();
		totalWinnings = 0;
		serverFull = false;	//not necessary
	}
	
}
