import java.util.ArrayList;

public class GameData{
	public int anteWager;
	public int pairWager;
	public int totalWonLost;
	public boolean clientLost;
	public boolean clientFolded;
	public boolean clientPlay;
	public ArrayList<String> events;
	
	public GameData(int anteWager, int pairWager, int totalWonLost, boolean clientFolded, boolean clientPlay, ArrayList<String> events) {
		this.anteWager = anteWager;
		this.pairWager = pairWager;
		this.totalWonLost = totalWonLost;
		this.clientFolded = clientFolded;
		this.events = events;
		this.clientPlay = clientPlay;
	}
}