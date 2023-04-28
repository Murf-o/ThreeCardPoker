import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MyTest {

	@Test
	void testDefaultValues() {
		PokerInfo info = new PokerInfo();
		assertEquals(false, info.clientFold);
		assertEquals(false, info.clientPlay);
		assertEquals(false, info.serverFull);
		assertEquals(false, info.lostGame);
		assertEquals(false, info.drawGame);
		assertEquals(0, info.anteWager);
		assertEquals(0, info.pairPlusWager);
		assertEquals(0, info.betWinnings);
		assertEquals(0, info.totalWinnings);
	}
	@Test
	void getValidCards() {
		PokerInfo info = new PokerInfo();
		GameLogic game = new GameLogic();
		game.handlePokerInfo(info);
		for(Integer num: info.playerCardsNum) {
			assertTrue(num < 15);
			assertTrue(num > 1);
		}
		
		for(Integer type: info.playerCardsType) {
			assertTrue(type < 4);
			assertTrue(type >= 0);
		}
		
		for(Integer num: info.dealerCardsNum) {
			assertTrue(num < 15);
			assertTrue(num > 1);
		}
		
		for(Integer type: info.dealerCardsType) {
			assertTrue(type < 4);
			assertTrue(type >= 0);
		}
	}
	
	@Test
	void testHandlePokerInfoOne() {
		PokerInfo info = new PokerInfo();
		GameLogic game = new GameLogic();
		game.handlePokerInfo(info);
		assertEquals(0, info.betWinnings);
		assertEquals(0, info.totalWinnings);
	}
	
	@Test
	void testHandlePokerInfoTwo() {
		PokerInfo info = new PokerInfo();
		info.clientFold = true;
		info.anteWager = 10;
		GameLogic game = new GameLogic();
		game.handlePokerInfo(info);
		assertEquals(-10, info.betWinnings);
		assertEquals(-10, info.totalWinnings);
	}
	
	//tests when client plays. Checks when loses, invalid hand
	@Test
	void testHandlePokerInfoThree() {
		PokerInfo info = new PokerInfo();
		
		info.anteWager = 10;
		GameLogic game = new GameLogic();
		game.handlePokerInfo(info);
		info.clientPlay = true;
		game.handlePokerInfo(info);
		if(info.lostGame) {
			assertEquals(-20, info.betWinnings);
			assertEquals(-20, info.totalWinnings);
		}else if(info.dealerInvalidHand) {
			assertEquals(0, info.betWinnings);
			assertEquals(0, info.totalWinnings);
		}
	}
	
	//tests whether program is evaluating hands as straights, pairs, etc correctly
	@Test
	void testEvaluateHandStraightOne(){
		PokerInfo info = new PokerInfo();
		for(int i = 0; i < 3; i++) {
			info.playerCardsNum.add(i);	//straight
			info.playerCardsType.add(i);
		}
		
		//done in the program, when dealing cards
		Collections.sort(info.playerCardsNum);
		Collections.reverse(info.playerCardsNum);
		
		GameLogic game = new GameLogic();
		int eval = game.evaluateHand(info.playerCardsNum, info.playerCardsType);
		assertEquals(3, eval);
	}
	
	//tests whether program is evaluating hands as straights, pairs, etc correctly
		@Test
		void testEvaluateHandStraightTwo(){
			PokerInfo info = new PokerInfo();
			for(int i = 0; i < 3; i++) {
				
				info.playerCardsType.add(i);
			}
			
			//straight
			info.playerCardsNum.add(4);
			info.playerCardsNum.add(2);
			info.playerCardsNum.add(3);
			
			//done in the program, when dealing cards
			Collections.sort(info.playerCardsNum);
			Collections.reverse(info.playerCardsNum);
			
			GameLogic game = new GameLogic();
			int eval = game.evaluateHand(info.playerCardsNum, info.playerCardsType);
			assertEquals(3, eval);
		}
	
	@Test
	void testEvaluateHandPairOne(){
		PokerInfo info = new PokerInfo();
		for(int i = 0; i < 3; i++) {
			info.playerCardsType.add(i);
		}
		
		//pair
		info.playerCardsNum.add(2);
		info.playerCardsNum.add(2);
		info.playerCardsNum.add(5);
		
		//done in the program, when dealing cards
		Collections.sort(info.playerCardsNum);
		Collections.reverse(info.playerCardsNum);
		
		GameLogic game = new GameLogic();
		int eval = game.evaluateHand(info.playerCardsNum, info.playerCardsType);
		assertEquals(5, eval);
	}
	@Test
	void testEvaluateHandPairTwo(){
		PokerInfo info = new PokerInfo();
		for(int i = 0; i < 3; i++) {
			info.playerCardsType.add(i);
		}
		
		//pair
		info.playerCardsNum.add(2);
		info.playerCardsNum.add(7);
		info.playerCardsNum.add(2);
		
		//done in the program, when dealing cards
		Collections.sort(info.playerCardsNum);
		Collections.reverse(info.playerCardsNum);
		
		GameLogic game = new GameLogic();
		int eval = game.evaluateHand(info.playerCardsNum, info.playerCardsType);
		assertEquals(5, eval);
	}
	
	@Test
	void testEvaluateHandPairThree(){
		PokerInfo info = new PokerInfo();
		for(int i = 0; i < 3; i++) {
			info.playerCardsType.add(i);
		}
		
		//pair
		info.playerCardsNum.add(2);
		info.playerCardsNum.add(7);
		info.playerCardsNum.add(7);
		
		//done in the program, when dealing cards
		Collections.sort(info.playerCardsNum);
		Collections.reverse(info.playerCardsNum);
		
		GameLogic game = new GameLogic();
		int eval = game.evaluateHand(info.playerCardsNum, info.playerCardsType);
		assertEquals(5, eval);
	}
	
	@Test
	void testEvaluateHandFlush(){
		PokerInfo info = new PokerInfo();
		
		//flush
		for(int i = 0; i < 3; i++) {
			info.playerCardsType.add(2);
		}
		
	
		info.playerCardsNum.add(2);
		info.playerCardsNum.add(7);
		info.playerCardsNum.add(4);
		
		//done in the program, when dealing cards
		Collections.sort(info.playerCardsNum);
		Collections.reverse(info.playerCardsNum);
		
		GameLogic game = new GameLogic();
		int eval = game.evaluateHand(info.playerCardsNum, info.playerCardsType);
		assertEquals(4, eval);
	}
	
	@Test
	void testEvaluateHandThreeOfAKind(){
		PokerInfo info = new PokerInfo();
		
		
		for(int i = 0; i < 3; i++) {
			info.playerCardsType.add(i);
		}
		
		info.playerCardsNum.add(2);
		info.playerCardsNum.add(2);
		info.playerCardsNum.add(2);
		
		//done in the program, when dealing cards
		Collections.sort(info.playerCardsNum);
		Collections.reverse(info.playerCardsNum);
		
		GameLogic game = new GameLogic();
		int eval = game.evaluateHand(info.playerCardsNum, info.playerCardsType);
		assertEquals(2, eval);
	}
	
	@Test
	void testEvaluateHandStraightFlush(){
		PokerInfo info = new PokerInfo();
		
		//flush
		for(int i = 0; i < 3; i++) {
			info.playerCardsType.add(2);
		}
		
		//straight
		info.playerCardsNum.add(2);
		info.playerCardsNum.add(4);
		info.playerCardsNum.add(3);
		
		//done in the program, when dealing cards
		Collections.sort(info.playerCardsNum);
		Collections.reverse(info.playerCardsNum);
		
		GameLogic game = new GameLogic();
		int eval = game.evaluateHand(info.playerCardsNum, info.playerCardsType);
		assertEquals(1, eval);
	}
	
	@Test
	void testEvaluateHandStraightFlushTwo(){
		PokerInfo info = new PokerInfo();
		
		//flush
		for(int i = 0; i < 3; i++) {
			info.playerCardsType.add(2);
		}
		
		//straight
		info.playerCardsNum.add(4);
		info.playerCardsNum.add(3);
		info.playerCardsNum.add(2);
		
		//done in the program, when dealing cards
		Collections.sort(info.playerCardsNum);
		Collections.reverse(info.playerCardsNum);
		
		GameLogic game = new GameLogic();
		int eval = game.evaluateHand(info.playerCardsNum, info.playerCardsType);
		assertEquals(1, eval);
	}
	
	@Test
	void testInvalidDealerHandOne() {
		PokerInfo info = new PokerInfo();
		info.dealerCardsNum.add(2);
		info.dealerCardsNum.add(10);
		info.dealerCardsNum.add(8);
		
		info.dealerCardsType.add(1);
		info.dealerCardsType.add(1);
		info.dealerCardsType.add(2);
		
		//done in the program, when dealing cards
		Collections.sort(info.dealerCardsNum);
		Collections.reverse(info.dealerCardsNum);
		
		GameLogic game = new GameLogic();
		assertTrue(game.dealerInvalidHand(info));
	}
	
	@Test
	void testInvalidDealerHandTwo() {
		PokerInfo info = new PokerInfo();
		info.dealerCardsNum.add(2);
		info.dealerCardsNum.add(10);
		info.dealerCardsNum.add(12);
		
		info.dealerCardsType.add(1);
		info.dealerCardsType.add(1);
		info.dealerCardsType.add(2);
		
		//done in the program, when dealing cards
		Collections.sort(info.dealerCardsNum);
		Collections.reverse(info.dealerCardsNum);
		
		GameLogic game = new GameLogic();
		assertFalse(game.dealerInvalidHand(info));
	}
	
	//Testing evaluating game and give winnings to client etc
	@Test
	void evaluateGameOne() {
		PokerInfo info = new PokerInfo();
		
		//player cards -- royal flush
		//flush
		for(int i = 0; i < 3; i++) {
			info.playerCardsType.add(2);
		}
		
		//straight
		info.playerCardsNum.add(4);
		info.playerCardsNum.add(3);
		info.playerCardsNum.add(2);
		
		
		//dealer cards -- pair
		info.dealerCardsNum.add(2);
		info.dealerCardsNum.add(2);
		info.dealerCardsNum.add(12);
		
		info.dealerCardsType.add(3);
		info.dealerCardsType.add(1);
		info.dealerCardsType.add(2);
		
		//done in the program, when dealing cards
		Collections.sort(info.playerCardsNum);
		Collections.reverse(info.playerCardsNum);
	
		Collections.sort(info.dealerCardsNum);
		Collections.reverse(info.dealerCardsNum);
		
		info.anteWager = 5;
		info.pairPlusWager = 5;
		GameLogic game = new GameLogic();
		int clientEval = game.evaluateHand(info.playerCardsNum, info.playerCardsType);
		int dealerEval = game.evaluateHand(info.dealerCardsNum, info.dealerCardsType);
		game.evaluateGame(info,  clientEval, dealerEval);
		assertEquals(5*41 + 5, info.betWinnings);
	}
	
	@Test
	void evaluateGameTwo() {
		PokerInfo info = new PokerInfo();
		
		//player cards -- royal flush
		//flush
		for(int i = 0; i < 3; i++) {
			info.playerCardsType.add(2);
		}
		
		//straight
		info.playerCardsNum.add(4);
		info.playerCardsNum.add(3);
		info.playerCardsNum.add(2);
		
		
		//dealer cards -- pair
		info.dealerCardsNum.add(2);
		info.dealerCardsNum.add(2);
		info.dealerCardsNum.add(12);
		
		info.dealerCardsType.add(3);
		info.dealerCardsType.add(1);
		info.dealerCardsType.add(2);
		
		//done in the program, when dealing cards
		Collections.sort(info.playerCardsNum);
		Collections.reverse(info.playerCardsNum);
	
		Collections.sort(info.dealerCardsNum);
		Collections.reverse(info.dealerCardsNum);
		
		info.anteWager = 5;
		info.pairPlusWager = 0;
		GameLogic game = new GameLogic();
		int clientEval = game.evaluateHand(info.playerCardsNum, info.playerCardsType);
		int dealerEval = game.evaluateHand(info.dealerCardsNum, info.dealerCardsType);
		game.evaluateGame(info,  clientEval, dealerEval);
		assertEquals(5*2, info.betWinnings);
	}
	
	//client flush, dealer pair, client wins
	@Test
	void evaluateGameThree() {
		PokerInfo info = new PokerInfo();
		
		//player cards --  flush
		//flush
		for(int i = 0; i < 3; i++) {
			info.playerCardsType.add(2);
		}
		
		info.playerCardsNum.add(4);
		info.playerCardsNum.add(5);
		info.playerCardsNum.add(2);
		
		
		//dealer cards -- pair
		info.dealerCardsNum.add(2);
		info.dealerCardsNum.add(2);
		info.dealerCardsNum.add(12);
		
		info.dealerCardsType.add(3);
		info.dealerCardsType.add(1);
		info.dealerCardsType.add(2);
		
		//done in the program, when dealing cards
		Collections.sort(info.playerCardsNum);
		Collections.reverse(info.playerCardsNum);
	
		Collections.sort(info.dealerCardsNum);
		Collections.reverse(info.dealerCardsNum);
		
		info.anteWager = 5;
		info.pairPlusWager = 5;
		GameLogic game = new GameLogic();
		int clientEval = game.evaluateHand(info.playerCardsNum, info.playerCardsType);
		int dealerEval = game.evaluateHand(info.dealerCardsNum, info.dealerCardsType);
		game.evaluateGame(info,  clientEval, dealerEval);
		assertEquals((5*4 - 5) + 5*2, info.betWinnings);
	}
	
	//draw, dealer has higher card, dealer wins
	@Test
	void evaluateGameFour() {
		PokerInfo info = new PokerInfo();
		
		//player cards -- pair
		//flush
		for(int i = 0; i < 3; i++) {
			info.playerCardsType.add(i);
		}
		
		//pair
		info.playerCardsNum.add(4);
		info.playerCardsNum.add(3);
		info.playerCardsNum.add(4);
		
		
		//dealer cards -- pair
		info.dealerCardsNum.add(2);
		info.dealerCardsNum.add(2);
		info.dealerCardsNum.add(12);
		
		info.dealerCardsType.add(3);
		info.dealerCardsType.add(1);
		info.dealerCardsType.add(2);
		
		//done in the program, when dealing cards
		Collections.sort(info.playerCardsNum);
		Collections.reverse(info.playerCardsNum);
	
		Collections.sort(info.dealerCardsNum);
		Collections.reverse(info.dealerCardsNum);
		
		info.anteWager = 5;
		info.pairPlusWager = 5;
		GameLogic game = new GameLogic();
		int clientEval = game.evaluateHand(info.playerCardsNum, info.playerCardsType);
		int dealerEval = game.evaluateHand(info.dealerCardsNum, info.dealerCardsType);
		game.evaluateGame(info,  clientEval, dealerEval);
		assertEquals(-10 + 5, info.betWinnings);	//5 pair plus success
		assertTrue(info.lostGame);
	}
	
	//draw, client has higher card, client wins
		@Test
		void evaluateGameFive() {
			PokerInfo info = new PokerInfo();
			
			//player cards -- pair
			//flush
			for(int i = 0; i < 3; i++) {
				info.playerCardsType.add(i);
			}
			
			//pair
			info.playerCardsNum.add(4);
			info.playerCardsNum.add(4);
			info.playerCardsNum.add(13);
			
			
			//dealer cards -- pair
			info.dealerCardsNum.add(2);
			info.dealerCardsNum.add(2);
			info.dealerCardsNum.add(12);
			
			info.dealerCardsType.add(3);
			info.dealerCardsType.add(1);
			info.dealerCardsType.add(2);
			
			//done in the program, when dealing cards
			Collections.sort(info.playerCardsNum);
			Collections.reverse(info.playerCardsNum);
		
			Collections.sort(info.dealerCardsNum);
			Collections.reverse(info.dealerCardsNum);
			
			info.anteWager = 5;
			info.pairPlusWager = 5;
			GameLogic game = new GameLogic();
			int clientEval = game.evaluateHand(info.playerCardsNum, info.playerCardsType);
			int dealerEval = game.evaluateHand(info.dealerCardsNum, info.dealerCardsType);
			game.evaluateGame(info,  clientEval, dealerEval);
			assertEquals(5*2 + 5, info.betWinnings);	
			assertFalse(info.lostGame);
		}
		
		
	//test various addEvent conditions
	@Test
	void testAddEvents() {
		PokerInfo info = new PokerInfo();
		info.clientFold = true;
		
		GameLogic game = new GameLogic();
		game.addEvents(info, -1, -1);
		ArrayList<String> ans = new ArrayList<String>();
		ans.add("Player FOLDED");
		ans.add("Player Lost: " + (info.anteWager + info.pairPlusWager));
		ans.add("Total Winnings: " + info.totalWinnings);
		int i = 0;
		for(String str: ans) {
			assertEquals(str, info.events.get(i++));
		}
		
		ans.clear();
		info.events.clear();
		info.clientFold = false;
		info.clientPlay = true;
		i = 0;
		info.pairPlusWager = 5;
		
		game.addEvents(info, 4, 2);
		
		ans.add("Flush! Player won the Pair Plus Wager! Payout: 3 to 1");
		ans.add("Dealer has a Three of a Kind");
		ans.add("Player Lost to the Dealers Hand!");
		
		for(String str: ans) {
			assertEquals(str, info.events.get(i++));
		}
		
		ans.clear();
		info.events.clear();
		i = 0;
		info.pairPlusWager = 0;
		info.dealerInvalidHand = true;
		
		ans.add("Player Has a straight Flush!");
		ans.add("Dealer does not have at least a Queen High Hand; Ante wager is pushed");
		
		game.addEvents(info, 1, 3);
		
		for(String str: ans) {
			assertEquals(str, info.events.get(i++));
		}
	}
	
	@Test
	void testHandler(){
		PokerInfo info = new PokerInfo();
		GameLogic game = new GameLogic();
		//get cards
		game.handlePokerInfo(info);
		
		//check cards valid
		for(Integer num: info.playerCardsNum) {
			assertTrue(num < 15);
			assertTrue(num > 1);
		}
		
		for(Integer type: info.playerCardsType) {
			assertTrue(type < 4);
			assertTrue(type >= 0);
		}
		
		for(Integer num: info.dealerCardsNum) {
			assertTrue(num < 15);
			assertTrue(num > 1);
		}
		
		for(Integer type: info.dealerCardsType) {
			assertTrue(type < 4);
			assertTrue(type >= 0);
		}
		info.clientPlay = true;
		info.anteWager = 5;
		game.handlePokerInfo(info);
		
		if(info.dealerInvalidHand) {
			assertTrue(game.dealerInvalidHand(info));
			assertEquals(0, info.betWinnings);
			assertEquals(0, info.totalWinnings);
		}
		else if(info.drawGame) {
			assertEquals(0, info.betWinnings);
			assertEquals(0, info.totalWinnings);
		}
		else {
			
			if(!info.lostGame) {	//won game
				assertEquals(10, info.betWinnings);
				assertEquals(10, info.totalWinnings);
			}
			else if(info.lostGame){	//lost game
				assertEquals(-10, info.betWinnings);
				assertEquals(-10, info.totalWinnings);
			}
		}
	}
	
	@Test
	void testAce() {
		PokerInfo info = new PokerInfo();
		GameLogic game = new GameLogic();
		
		for(int i = 0; i < 3; i++) {
			info.playerCardsType.add(i);
		}
		
		//straight
		info.playerCardsNum.add(12);
		info.playerCardsNum.add(13);
		info.playerCardsNum.add(14);
		
		
		//dealer cards -- pair
		info.dealerCardsNum.add(2);
		info.dealerCardsNum.add(2);
		info.dealerCardsNum.add(12);
		
		info.dealerCardsType.add(3);
		info.dealerCardsType.add(1);
		info.dealerCardsType.add(2);
		
		//done in the program, when dealing cards
		Collections.sort(info.playerCardsNum);
		Collections.reverse(info.playerCardsNum);
	
		Collections.sort(info.dealerCardsNum);
		Collections.reverse(info.dealerCardsNum);
		
		assertEquals(3, game.evaluateHand(info.playerCardsNum, info.playerCardsType));
	}
	

}
