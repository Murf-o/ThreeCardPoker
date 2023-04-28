import javafx.scene.text.Font;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;


public class JavaFXTemplate extends Application {
	
	//cards object used to get cards
	Cards cardsGetter = new Cards();
	
	//intro text
	Text errorIntro = new Text();
	
	Button dealCardsB = new Button("Deal Cards");
	Button foldB = new Button("FOLD");
	Button playB = new Button("PLAY");
	Button exitB = new Button("EXIT");
	Button anotherGameB = new Button("Another Game");
	TextField anteWagerTxt;
	TextField pairPlusWagerTxt;
	Text totalWinnings;
	boolean prevHandInvalid = false;
	Client client;
	
	//Game DEAL
	BorderPane bGameDeal = new BorderPane();
	Scene gameDealS = new Scene(bGameDeal);
	Text errorDealTxt = new Text();
	
	BorderPane bGame = new BorderPane();
	Scene gameS = new Scene(bGame);
	
	BorderPane bpResultsFold = new BorderPane();
	Scene resultsFold = new Scene(bpResultsFold);
	BorderPane bpResultsPlay = new BorderPane();
	Scene resultsPlay = new Scene(bpResultsPlay);
	
	EventHandler<ActionEvent> freshStartHandler;
	EventHandler<ActionEvent> newLookHandler;
	boolean newLook = false;
	EventHandler<ActionEvent> exitHandler;
	
	int cycleCount = 0;
	
	
	public static void main(String[] args) {
		
		launch(args);
	}

	
	@Override
	public void start(Stage primaryStage) throws Exception {
		bGameDeal.setId("dealPane");
		bGame.setId("gamePane");
		bpResultsFold.setId("fold");
		bpResultsPlay.setId("play");
		
		dealCardsB.setId("dealCardsB");
		anotherGameB.setId("anotherGameB");
		exitB.setId("exitGameB");
		
		foldB.setId("foldB");
		playB.setId("playB");
		
		errorIntro.setId("errorIntroTxt");
		errorDealTxt.setId("errorDealTxt");
		
		primaryStage.setTitle("PLAYER");
		Button connectB = new Button("Connect");
		connectB.setShape(new Circle(80));
		connectB.setPrefHeight(200);
		connectB.setMaxSize(120, 120);
		anotherGameB.setPrefSize(110, 30);
		exitB.setPrefSize(80, 30);
		
		Text welcomeTxt = new Text("WELCOME TO\nTHREE CARD POKER");
		welcomeTxt.setTextAlignment(TextAlignment.CENTER);
		welcomeTxt.setId("welcomeTxt");
		
		TextField ipAddressTxt = new TextField();
		ipAddressTxt.setPrefSize(150, 30);
		ipAddressTxt.setMaxWidth(150);
		ipAddressTxt.setPromptText("IP Address");
		TextField portText = new TextField();
		portText.setPromptText("Port #");
		portText.setPrefSize(100, 30);
		portText.setMaxWidth(100);
		VBox introVB = new VBox(welcomeTxt, connectB, ipAddressTxt, portText, errorIntro);
		VBox.setMargin(errorIntro, new Insets(80,0,0,0));
		introVB.setAlignment(Pos.CENTER);
		introVB.setPadding(new Insets(0, 0, 150, 0));
		introVB.setSpacing(20);
		BorderPane bp = new BorderPane();
		bp.setPrefSize(700, 700);
		bp.setCenter(introVB);
		
		
		//intro scene setup
		Scene intro = new Scene(bp);
		bp.setId("introPane");
		intro.getStylesheets().add("/CSS/introScene.css");
		//primaryStage.setFullScreen(value);
		primaryStage.setScene(intro);
		primaryStage.show();
		
		connectB.setOnAction(e->{
			int portNum;
			try {
				portNum = Integer.parseInt(portText.getText());
			}catch(Exception ParseE) {portText.clear(); return;}
			client = new Client(ipAddressTxt.getText(), portNum, data-> {
				Platform.runLater(()->{
					errorIntro.setText("Connection to Server Failed.\nReturned to Main Menu");
					client.info.freshStart();
					primaryStage.setScene(intro);
					primaryStage.show();
				});
			});
			int handler = 1;
			try{
				handler = client.connect();
			}catch(Exception connectionE) {errorIntro.setText("Connection Unsuccessful\nPlease Check Your Inputs are Correct\nand Try Again.");; return;}
			
			if(handler == 0) {
				errorIntro.setText("The Server is Currently Full, Please Try Again Later");
				return;
			}
			errorIntro.setText("");
			//Thread t = new Thread(client);
			//t.start();	
			Scene scene = getGameDealScene();
			if(newLook) {scene.getStylesheets().add("/CSS/newLook/gameDealNL.css");}
			else {scene.getStylesheets().add("/CSS/gameDeal.css");}
			primaryStage.setScene(scene);
			primaryStage.show();
		});
		
		dealCardsB.setOnAction(e->{
			if(anteWagerTxt.getText().isEmpty()) {}	//do nothing
			else {
				int pairWager = 0;
				int anteWager = 0;
				try {
					anteWager = Integer.parseInt(anteWagerTxt.getText());
					if(!pairPlusWagerTxt.getText().isEmpty()) {pairWager = Integer.parseInt(pairPlusWagerTxt.getText());
					if(pairWager < 5 && pairWager != 0) {errorDealTxt.setText("Your Pair Plus Wager must be\nGreater than, or equal to, $5 -- or $0");return;}
					else if(pairWager > 25) {errorDealTxt.setText("Your Pair Plus Wager must be\nLess than, or equal to, $25");return;}}
				}catch(Exception parsE) {return;}				// 5 <= pairWager <= 25
				if(anteWager < 5) {errorDealTxt.setText("Your Ante Wager must be\nGreater than, or equal to, $5");return;}	// 5 <= anteWager <= 25
				else if(anteWager > 25) {errorDealTxt.setText("Your Ante Wager must be\nLess than, or equal to, $25");return;}
				client.sendWagers(anteWager, pairWager);
				Scene scene = getGameScene();
				scene.getStylesheets().clear();
				if(newLook) {scene.getStylesheets().add("/CSS/newLook/gameSceneNL.css");}
				else{scene.getStylesheets().add("/CSS/gameScene.css");}
				primaryStage.setScene(scene);
				primaryStage.show();
			}
		});
		
		foldB.setOnAction(e->{
			client.info.clientFold = true;
			client.send();
			Scene scene = getResultsFoldScene();
			scene.getStylesheets().clear();
			if(newLook) {scene.getStylesheets().add("/CSS/newLook/resultsSceneNL.css");}
			else{scene.getStylesheets().add("/CSS/resultsScene.css");}
			primaryStage.setScene(scene);
			primaryStage.show();
		});
		
		playB.setOnAction(e->{
			client.info.clientPlay = true;
			client.send();
			Scene scene = getResultsPlayScene();
			scene.getStylesheets().clear();
			if(newLook) {scene.getStylesheets().add("/CSS/newLook/resultsSceneNL.css");}
			else{scene.getStylesheets().add("/CSS/resultsScene.css");}
			
			primaryStage.setScene(scene);
			primaryStage.show();
		});
		
		anotherGameB.setOnAction(e->{
			client.info.playingAnotherHand = true;
			client.send();
			client.info.newGame();
			Scene scene = getGameDealScene();
			scene.getStylesheets().clear();
			if(newLook) {scene.getStylesheets().add("/CSS/newLook/gameDealNL.css");}
			else {scene.getStylesheets().add("/CSS/gameDeal.css");}
			primaryStage.setScene(scene);
			primaryStage.show();
		});
		
		exitB.setOnAction(exitHandler);
		
		
		exitHandler = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Platform.exit();
				System.exit(1);
			}
		};
		
		freshStartHandler = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				client.info.freshStart();
				Scene scene = getGameDealScene();
				scene.getStylesheets().clear();
				if(newLook) {scene.getStylesheets().add("/CSS/newLook/gameDealNL.css");}
				else{scene.getStylesheets().add("/CSS/gameDeal.css");}
				primaryStage.setScene(getGameDealScene());
				primaryStage.show();
			}
		};
		newLookHandler = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Scene scene = primaryStage.getScene();
				if(scene.getStylesheets().size() == 0) {return;}	//no styleSheet
				
				if(newLook) {
	
					if(scene.getStylesheets().get(0).equals("/CSS/newLook/gameDealNL.css")) {
						scene.getStylesheets().clear();
						scene.getStylesheets().add("/CSS/gameDeal.css");
					}
					else if(scene.getStylesheets().get(0).equals("/CSS/newLook/gameSceneNL.css")) {
						scene.getStylesheets().clear();
						scene.getStylesheets().add("/CSS/gameScene.css");
					}
					else if(scene.getStylesheets().get(0).equals("/CSS/newLook/resultsSceneNL.css")) {
						scene.getStylesheets().clear();
						scene.getStylesheets().add("/CSS/resultsScene.css");
					}else {
						scene.getStylesheets().clear();
						scene.getStylesheets().add("/CSS/introScene.css");
					}
					newLook = false;
				}
				else {

					if(scene.getStylesheets().get(0).equals("/CSS/gameDeal.css")) {
						scene.getStylesheets().clear();
						scene.getStylesheets().add("/CSS/newLook/gameDealNL.css");
					}
					else if(scene.getStylesheets().get(0).equals("/CSS/gameScene.css")) {
						scene.getStylesheets().clear();
						scene.getStylesheets().add("/CSS/newLook/gameSceneNL.css");
					}
					else if(scene.getStylesheets().get(0).equals("/CSS/resultsScene.css")) {
						scene.getStylesheets().clear();
						scene.getStylesheets().add("/CSS/newLook/resultsSceneNL.css");
					}else {
						scene.getStylesheets().clear();
						scene.getStylesheets().add("/CSS/introScene.css");
					}
					newLook = true;
				}
				
				primaryStage.setScene(scene);
				primaryStage.show();
			}
		};
		
		
	}
	
	public Scene getResultsPlayScene() {
		//BorderPane bpResults = new BorderPane();
		bpResultsPlay.setRight(null);
		bpResultsPlay.setLeft(null);
		Menu menu = new Menu("Menu");
		
		MenuItem freshStart = new MenuItem("Fresh Start");
		freshStart.setOnAction(freshStartHandler);
		MenuItem newLook = new MenuItem("New Look");
		newLook.setOnAction(newLookHandler);
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(exitHandler);
		
		menu.getItems().add(freshStart);
		menu.getItems().add(newLook);
		menu.getItems().add(exitItem);
		MenuBar menuB = new MenuBar(menu);
		
		VBox eventsVB = new VBox();
		eventsVB.setAlignment(Pos.CENTER);
	
		eventsVB.setId("eventsVB");
		
		//show players cards
		HBox playerCards = new HBox();
		playerCards.setSpacing(10);
		int size = client.info.playerCardsNum.size();
		for(int i = 0; i < size; i++) {
			playerCards.getChildren().add(cardsGetter.getCard(client.info.playerCardsType.get(i), client.info.playerCardsNum.get(i)));
		}
		bpResultsPlay.setBottom(playerCards);
		playerCards.setAlignment(Pos.CENTER);
		
		//show dealers cards
		HBox dealerCards = new HBox();
		dealerCards.setSpacing(10);
		for(int i = 0; i < size; i++) {
			dealerCards.getChildren().add(cardsGetter.getBacksideCard());
		}
		
		cycleCount = 0;
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(.7), 
				event->{
					dealerCards.getChildren().set(cycleCount, cardsGetter.getCard(client.info.dealerCardsType.get(cycleCount), client.info.dealerCardsNum.get(cycleCount)));
					cycleCount++;
				})
			); 
		timeline.setCycleCount(3);
		timeline.play();
		VBox menuCardsVB = new VBox(menuB, dealerCards);
		menuCardsVB.setSpacing(5);
		bpResultsPlay.setTop(menuCardsVB);
		dealerCards.setAlignment(Pos.CENTER);
		
		bpResultsPlay.setCenter(new Text("Flipping Dealer's Hand"));
		
		timeline.setOnFinished(eventsAdd->{
			bpResultsPlay.setCenter(eventsVB);
			for(String str: client.info.events) {
				Text txt = new Text(str);
				eventsVB.getChildren().add(txt);
			}
			bpResultsPlay.setRight(anotherGameB);
			bpResultsPlay.setLeft(exitB);
			BorderPane.setAlignment(exitB, Pos.BOTTOM_LEFT);
			BorderPane.setAlignment(anotherGameB, Pos.BOTTOM_RIGHT);
		});
		if(client.info.dealerInvalidHand) {
			anteWagerTxt.setText(""+client.info.anteWager);
			prevHandInvalid = true;
		}
		
		
		bpResultsPlay.setPrefSize(700, 700);
		return resultsPlay;
	}
	
	public Scene getResultsFoldScene() {
		bpResultsFold.setRight(null);
		bpResultsFold.setLeft(null);
		//BorderPane bpResults = new BorderPane();
		Menu menu = new Menu("Menu");
		
		MenuItem freshStart = new MenuItem("Fresh Start");
		freshStart.setOnAction(freshStartHandler);
		MenuItem newLook = new MenuItem("New Look");
		newLook.setOnAction(newLookHandler);
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(exitHandler);
		
		menu.getItems().add(freshStart);
		menu.getItems().add(newLook);
		menu.getItems().add(exitItem);
		MenuBar menuB = new MenuBar(menu);
		
		
		
		//show playerCards
		HBox playerCards = new HBox();
		playerCards.setSpacing(10);
		int size = client.info.playerCardsNum.size();
		for(int i = 0; i < size; i++) {
			playerCards.getChildren().add(cardsGetter.getCard(client.info.playerCardsType.get(i), client.info.playerCardsNum.get(i)));
		}
		bpResultsFold.setBottom(playerCards);
		playerCards.setAlignment(Pos.CENTER);
		playerCards.setPadding(new Insets(0,0,5,0));
		
		//show dealers Cards
		HBox dealerCards = new HBox();
		dealerCards.setSpacing(10);
		
		
		//show as hidden first
		for(int i = 0; i < size; i++) {
			dealerCards.getChildren().add(cardsGetter.getBacksideCard());
		}
		
		cycleCount = 0;
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(.8), 
				event->{
					dealerCards.getChildren().set(cycleCount, cardsGetter.getCard(client.info.dealerCardsType.get(cycleCount), client.info.dealerCardsNum.get(cycleCount)));
					cycleCount++;
				})
			); 
		timeline.setCycleCount(3);
		timeline.play();
		
		
		VBox menuCardsVB = new VBox(menuB, dealerCards);
		menuCardsVB.setSpacing(5);
		bpResultsFold.setTop(menuCardsVB);
		dealerCards.setAlignment(Pos.CENTER);
		
		bpResultsFold.setCenter(new Text("Flipping Dealer's Hand"));
		
		VBox eventVB = new VBox();
		timeline.setOnFinished(eventsAdd->{
			bpResultsFold.setCenter(eventVB);
			for(String str: client.info.events) {
				Text txt = new Text(str);
				eventVB.getChildren().add(txt);
			}
			bpResultsFold.setRight(anotherGameB);
			bpResultsFold.setLeft(exitB);
			BorderPane.setAlignment(exitB, Pos.BOTTOM_LEFT);
			BorderPane.setAlignment(anotherGameB, Pos.BOTTOM_RIGHT);
		});
		
		
		
		eventVB.setAlignment(Pos.CENTER);
		eventVB.setSpacing(30);
		bpResultsFold.setPrefSize(700, 700);
		return resultsFold;
	}
	
	public Scene getGameScene() {
		//menu
		Menu menu = new Menu("Menu");
		
		MenuItem freshStart = new MenuItem("Fresh Start");
		freshStart.setOnAction(freshStartHandler);
		MenuItem newLook = new MenuItem("New Look");
		newLook.setOnAction(newLookHandler);
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(exitHandler);
		
		menu.getItems().add(freshStart);
		menu.getItems().add(newLook);
		menu.getItems().add(exitItem);
		MenuBar menuB = new MenuBar(menu);
		
		
		foldB.setShape(new Circle(60));
		foldB.setPrefSize(120, 120);
		foldB.setMaxSize(120, 120);
		foldB.setMaxWidth(120);
		playB.setShape(new Circle(60));
		playB.setPrefSize(120, 120);
		playB.setMaxSize(120, 120);
		Text anteTxt = new Text("Ante Wager: " + client.anteWager);
		anteTxt.setId("anteTxt");
		Text pairTxt = new Text("Pair Plus Wager: " + client.pairPlusWager);
		pairTxt.setId("pairTxt");
		totalWinnings= new Text("Total Winnings: " + client.totalWinnings);
		totalWinnings.setId("totalWTxt");
		VBox vb = new VBox(anteTxt, pairTxt, totalWinnings);
		vb.setAlignment(Pos.BOTTOM_LEFT);
		
		
		HBox hb = new HBox(foldB, playB);
		VBox centerVB = new VBox(hb);
		
		//client cards container
		HBox playerCards = new HBox();
		playerCards.setSpacing(10);
		playerCards.setPadding(new Insets(0,0,5,0));
		bGame.setBottom(playerCards);
				
		//display player cards as hidden
		int size = client.info.playerCardsNum.size();
		for(int i = 0; i < size; i++) {
			playerCards.getChildren().add(cardsGetter.getBacksideCard());
		}
		bGame.setCenter(new Text("Flipping Player's Hand"));
		BorderPane.setMargin(bGame.getCenter(), new Insets(0,90,0,0));
		//reveal player cards one by one
		cycleCount = 0;
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(.7), 
					event->{
						playerCards.getChildren().set(cycleCount, cardsGetter.getCard(client.info.playerCardsType.get(cycleCount), client.info.playerCardsNum.get(cycleCount)));
						cycleCount++;
					})
			);
		timeline.setCycleCount(3);
		timeline.play();
		//add fold play buttons, along with events, if there is any
		timeline.setOnFinished(finish->{
			bGame.setCenter(centerVB);
			for(String str: client.info.events) {
				Text txt = new Text(str);
				centerVB.getChildren().add(txt);
			}
		});
		
		centerVB.setAlignment(Pos.CENTER);
		centerVB.setSpacing(15);
		
		hb.setAlignment(Pos.CENTER);
		hb.setSpacing(100);
		BorderPane.setAlignment(centerVB, Pos.CENTER);
		BorderPane.setMargin(centerVB, new Insets(0,50,0,0));
		bGame.setLeft(vb);
		
		bGame.setPrefSize(700, 700);
		
		BorderPane.setMargin(vb, new Insets(300, 0, 0, 0));
		
		
		
		//display hidden dealerCards
		HBox dealerCards = new HBox();
		dealerCards.setSpacing(10);
		for(int i = 0; i < size; i++) {
			dealerCards.getChildren().add(cardsGetter.getBacksideCard());
		}
		VBox menuCardsVB = new VBox(menuB, dealerCards);
		menuCardsVB.setSpacing(5);
		bGame.setTop(menuCardsVB);
		dealerCards.setAlignment(Pos.CENTER);
		
		playerCards.setAlignment(Pos.CENTER);
		
		return gameS;
	}
	
	public Scene getGameDealScene() {
		//BorderPane bGameDeal = new BorderPane();
		Menu menu = new Menu("Menu");
		
		MenuItem freshStart = new MenuItem("Fresh Start");
		freshStart.setOnAction(freshStartHandler);
		MenuItem newLook = new MenuItem("New Look");
		newLook.setOnAction(newLookHandler);
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(exitHandler);
		
		menu.getItems().add(freshStart);
		menu.getItems().add(newLook);
		menu.getItems().add(exitItem);
		MenuBar menuB = new MenuBar(menu);
		bGameDeal.setTop(menuB);
		
		dealCardsB.setShape(new Circle(40));
		dealCardsB.setMaxSize(100, 100);

		if(!prevHandInvalid) {
			anteWagerTxt = new TextField();
			prevHandInvalid = false;
		}
		anteWagerTxt.setPromptText("Ante Wager $5-$25:");
		anteWagerTxt.setPrefSize(150, 30);
		anteWagerTxt.setMaxWidth(150);
		pairPlusWagerTxt = new TextField();
		pairPlusWagerTxt.setPromptText("Pair Plus Wager $0, $5-$25:");
		pairPlusWagerTxt.setPrefSize(150, 30);
		pairPlusWagerTxt.setMaxWidth(150);
		
		totalWinnings= new Text("Total Winnings: " + client.totalWinnings);
		totalWinnings.setId("winningsTxt");
		VBox vb = new VBox(errorDealTxt, anteWagerTxt, pairPlusWagerTxt, totalWinnings);
		vb.setAlignment(Pos.CENTER);
		vb.setSpacing(5);
		anteWagerTxt.setAlignment(Pos.CENTER_LEFT);
		pairPlusWagerTxt.setAlignment(Pos.CENTER_LEFT);
		bGameDeal.setCenter(dealCardsB);
		bGameDeal.setLeft(vb);
		BorderPane.setMargin(vb, new Insets(300, 0, 0, 0));
		bGameDeal.setPrefSize(700, 700);
		
		return gameDealS;
		//return new Scene(bGameDeal);
	}

}
