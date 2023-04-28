import java.util.ArrayList;

public class ClientData{
	public ArrayList<GameData> games;
	public int clientNum;
	public int gamesPlayed;
	
	public ClientData(int clientNum) {
		this.clientNum = clientNum;
		this.games = new ArrayList<GameData>();
		this.gamesPlayed = 0;
	}
	
	public void addGame(int anteWager, int pairWager, int totalWonLost, boolean clientFolded, boolean clientPlay, ArrayList<String> events) {
		this.games.add(new GameData(anteWager, pairWager, totalWonLost, clientFolded, clientPlay, events));
		++this.gamesPlayed;
	}
	
	//gameNum 1 - n, where n represents number of games played
	public GameData getGame(int gameNum) {
		if(gameNum < 0 || gameNum >= gamesPlayed) {return null;}

		return this.games.get(gameNum);
	}
}