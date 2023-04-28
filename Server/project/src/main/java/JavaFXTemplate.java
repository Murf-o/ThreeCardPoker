import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

//SERVER

public class JavaFXTemplate extends Application {

	GameServer game;
	Menu menu = new Menu("Menu");
	Scene stateOfGameScene;	
	ListView<String> clientList;
	Button serverOnOffB;
	BorderPane bpStart = new BorderPane();
	Scene startScene = new Scene(bpStart);
	
	MenuItem returnItem;
	MenuItem stateItem;
	Text clientsConnectedTxt;
	Button viewClientB = new Button("View");
	TextField promptField = new TextField();
	TextField portField = new TextField();
	//clientData Scene
	BorderPane bpClient = new BorderPane();
	Scene clientScene = new Scene(bpClient);
	int clientViewing = -1;
	Text gamesPlayedTxt = new Text();
	ArrayList<Text> isClientDiscList = new ArrayList<Text>();
	TextField enterGameNum = new TextField();
	Button viewGameB = new Button("View");
	MenuItem returnStateItem;
	
	//gameData Scene
	MenuItem returnClientItem;
	BorderPane bpGame = new BorderPane();
	Scene gameScene = new Scene(bpGame);
	
	
	BorderPane bState = new BorderPane();
	Scene stateScene = new Scene(bState);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}


	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		bpStart.setId("startPane");
		bState.setId("statePane");
		bpClient.setId("clientPane");
		bpGame.setId("gamePane");
		
		
		stateItem = new MenuItem("State of the Game");
		menu.getItems().add(stateItem);
		
		clientsConnectedTxt = new Text("Clients Connected: 0");
		clientsConnectedTxt.setId("clientsConnectedTxt");
		
		clientList = new ListView<String>();
		game = new GameServer(data->{
				Platform.runLater(()->{
				clientList.getItems().add(data.toString());
				clientsConnectedTxt.setText("Clients Connected: " + game.clientsConnected);	
				isClientDiscList.add(new Text("Client Connected"));
				});
		}, disconnect-> {
			Platform.runLater(()->{
				clientList.getItems().set((int) disconnect-1, "Client #" + (int) disconnect + " (Disconnected)");
				clientsConnectedTxt.setText("Clients Connected: " + game.clientsConnected);
				isClientDiscList.get((int) disconnect-1).setText("Client Disconnected");
			});
		}, newHand-> {	//newHand = clientNum
			Platform.runLater(()->{
				clientList.getItems().set((int) newHand-1, "Client #" + (int) newHand + " (Playing another Hand)");
				isClientDiscList.get((int) newHand-1).setText("Client Playing another hand");
				
			}); 
		});
		
		returnItem = new MenuItem("Return");
		returnItem.setOnAction(e->{
			Scene scene = getStartScene();
			scene.getStylesheets().add("/CSS/start.css");
			primaryStage.setScene(scene);
			primaryStage.show();
		});
		
		
		returnStateItem = new MenuItem("Return");
		returnStateItem.setOnAction(e->{
			Scene scene = getStateScene();
			scene.getStylesheets().add("/CSS/state.css");
			primaryStage.setScene(scene);
			primaryStage.show();
		});
		
		returnClientItem = new MenuItem("Return");
		returnClientItem.setOnAction(e->{
			Scene scene = getClientDataScene(clientViewing);
			scene.getStylesheets().add("/CSS/clientData.css");
			primaryStage.setScene(scene);
			primaryStage.show();
		});
		
		primaryStage.setTitle("SERVER");
		serverOnOffB = new Button("Turn on\n Server");
		serverOnOffB.setShape(new Circle(80));
		serverOnOffB.setPrefHeight(120);
		serverOnOffB.setPrefWidth(120);
		//serverOnOffB.setMaxSize(120, 120);
		
		//turn on/off server
		serverOnOffB.setOnAction(e->{
			if(game.isServerOn)	{turnServerOff();}	//if on, turn off
			else				{turnServerOn();}	//if off, turn on
		});
		
		stateItem.setOnAction(e->{
			stateOfGameScene = getStateScene();
			stateOfGameScene.getStylesheets().add("/CSS/state.css");
			primaryStage.setScene(stateOfGameScene);
		});
		
		viewClientB.setOnAction(e->{
			int num;
			try {
				num = Integer.parseInt(promptField.getText());
			}catch(Exception parseE) {promptField.setText(""); return;}
			
			promptField.setText("");
			if(num < 1 || num > game.count) {
				return;
			}
			clientViewing = num;
			//clientData Scene
			Scene scene = getClientDataScene(num);
			scene.getStylesheets().add("/CSS/clientData.css");
			primaryStage.setScene(scene);
			primaryStage.show();
		});
		
		viewGameB.setOnAction(e->{
			int gameNum = Integer.parseInt(enterGameNum.getText());
			enterGameNum.setText("");
			
			if(gameNum > game.clientDataList.get(clientViewing).gamesPlayed || gameNum < 1) {return;}
			Scene scene = getGameDataScene(gameNum-1);
			scene.getStylesheets().add("/CSS/gameData.css");
			primaryStage.setScene(getGameDataScene(gameNum-1));
			primaryStage.show();	
		});
		
		Scene scene = getStartScene();
		scene.getStylesheets().add("/CSS/start.css");
		primaryStage.setScene(scene);
		primaryStage.show();				
		primaryStage.setOnCloseRequest(e->{
			Platform.exit();
			System.exit(0);
        });
	}
	
	public Scene getGameDataScene(int gameNum) {
		//BorderPane bp = new BorderPane();
		bpGame.setPrefSize(600, 600);
		
		menu.getItems().set(0, returnClientItem);
		MenuBar menuBar = new MenuBar(menu);
		bpGame.setTop(menuBar);
		
		
		Text anteWager = new Text("Ante wager: " + game.clientDataList.get(clientViewing).getGame(gameNum).anteWager);
		Text pairWager = new Text("Pair Plus Wager: " + game.clientDataList.get(clientViewing).getGame(gameNum).pairWager);
		Text totalBet = new Text();
		if(game.clientDataList.get(clientViewing).getGame(gameNum).clientPlay) 
			totalBet.setText("Total Bet (with play wager): " + (game.clientDataList.get(clientViewing).getGame(gameNum).anteWager*2 + game.clientDataList.get(clientViewing).getGame(gameNum).pairWager));
		else
			totalBet.setText("Total Bet: " + (game.clientDataList.get(clientViewing).getGame(gameNum).anteWager + game.clientDataList.get(clientViewing).getGame(gameNum).pairWager));
		
		VBox vb = new VBox(anteWager, pairWager, totalBet);
		vb.setAlignment(Pos.CENTER);
		for(String str: game.clientDataList.get(clientViewing).getGame(gameNum).events) {
			vb.getChildren().add(new Text(str));
		}
		vb.setSpacing(15);
		bpGame.setCenter(vb);
		return gameScene;
	}
	
	public Scene getClientDataScene(int clientNum) {
		//BorderPane bpClient = new BorderPane();
		bpClient.setPrefSize(600, 600);
		
		menu.getItems().set(0, returnStateItem);
		MenuBar menuBar = new MenuBar(menu);
		bpClient.setTop(menuBar);
		gamesPlayedTxt.setText("Games Played: " + game.clientDataList.get(clientNum).gamesPlayed);
		
		enterGameNum.setPromptText("Game #");
		Text gameNumTxt = new Text("Enter Game #: ");
		
		HBox hb = new HBox(gameNumTxt, enterGameNum, viewGameB);
		hb.setSpacing(10);
		hb.setAlignment(Pos.CENTER);
		VBox vb;
		if(clientNum-1 < isClientDiscList.size()) {vb = new VBox(gamesPlayedTxt, isClientDiscList.get(clientNum-1),hb);}
		else {vb = new VBox(gamesPlayedTxt, hb);}
		
		vb.setSpacing(10);
		vb.setAlignment(Pos.CENTER);
		
		bpClient.setCenter(vb);
		
		return clientScene;
	}
	
	public Scene getStartScene() {
		
		menu.getItems().set(0, stateItem);
		MenuBar menuBar = new MenuBar(menu);
		bpStart.setTop(menuBar);
		
		portField.setPromptText("PORT #:");
		portField.setPrefSize(100, 20);
		portField.setMaxSize(100, 20);
		VBox vb = new VBox(serverOnOffB, portField);
		vb.setSpacing(50);
		bpStart.setCenter(vb);
		bpStart.setPrefSize(600, 600);
		vb.setAlignment(Pos.CENTER);
		return startScene;
	}
	
	public Scene getStateScene() {
		//BorderPane bState = new BorderPane();
		Text prompt = new Text("Client Info: ");
		
		promptField.setPromptText("Client #");
		
		HBox hb = new HBox(prompt, promptField, viewClientB);
		hb.setAlignment(Pos.CENTER);
		VBox vb = new VBox(clientsConnectedTxt, hb, clientList);
		vb.setSpacing(10);
		vb.setAlignment(Pos.CENTER);
		bState.setCenter(vb);
		clientList.setPrefSize(300, 300);
		clientList.setMaxSize(300, 300);
		menu.getItems().set(0, returnItem);
		MenuBar menuBar = new MenuBar(menu);
		bState.setTop(menuBar);
		bState.setPrefSize(600, 600);
		return stateScene;
	}

	
	public void turnServerOn() {
		int portNum;
		try {
			portNum = Integer.parseInt(portField.getText());
		}catch(Exception e) {portField.clear(); return;}	//return if error parsing
		game.setPort(portNum);
		game.turnOn();
		if(game.isServerOn)
			serverOnOffB.setText("Turn off\n Server");
	}
	public void turnServerOff() {
		game.turnOff();
		if(!game.isServerOn)
			serverOnOffB.setText("Turn on\n Server");
	}
}
