import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameLogic {
	
	
	public void handlePokerInfo(PokerInfo info) {
		info.events.clear();
		//deal cards to dealer and player
		if(info.clientFold == false && info.clientPlay == false) {
			getCards(info);
			int clientEval = evaluateHand(info.playerCardsNum, info.playerCardsType);
			addPairPlusEvent(info, clientEval);
		}	
		else if(info.clientFold) {
			//game is over calculations
			info.betWinnings = 0-(info.pairPlusWager + info.anteWager);
			info.totalWinnings += info.betWinnings;
			
			addEvents(info, 0, 0);
		}
		else if(info.clientPlay) {
			
			int clientEval = evaluateHand(info.playerCardsNum, info.playerCardsType);
			if(dealerInvalidHand(info)) {	//invalid dealer hand
				info.dealerInvalidHand = true;
				evaluateGame(info, clientEval, -1);
				addEvents(info, clientEval, 0);
				return;
			}
			//valid dealers hand
			int dealersEval = evaluateHand(info.dealerCardsNum, info.dealerCardsType);
			evaluateGame(info, clientEval, dealersEval);
			addEvents(info, clientEval, dealersEval);
		}
		return;
	}
	
	//just used to notify the player if they have a pair or not
	public void addPairPlusEvent(PokerInfo info, int clientEval) {
		if(info.pairPlusWager > 0) {
			if(clientEval == 6) {info.events.add("Player lost the Pair Plus Wager");}
			else if(clientEval == 5) {info.events.add("Pair!");}
			else if(clientEval == 4) {info.events.add("Flush!");}
			else if(clientEval == 3) {info.events.add("Straight!");}
			else if(clientEval == 2) {info.events.add("Three of a Kind!");}
			else if(clientEval == 1) {info.events.add("Straight Flush!");}
		}
	}
	
	public void evaluateGame(PokerInfo info, int clientEval, int dealersEval) {
		
		//calculate pairPlus Earnings
		if(info.pairPlusWager > 0) {
			if(clientEval == 6) {info.betWinnings = 0-info.pairPlusWager;	info.totalWinnings += info.betWinnings;}
			else if(clientEval == 5) {info.betWinnings = info.pairPlusWager*2 - info.pairPlusWager;	info.totalWinnings += info.betWinnings;}
			else if(clientEval == 4) {info.betWinnings = info.pairPlusWager*4 - info.pairPlusWager;	info.totalWinnings += info.betWinnings;}
			else if(clientEval == 3) {info.betWinnings = info.pairPlusWager*7 - info.pairPlusWager;	info.totalWinnings += info.betWinnings;}
			else if(clientEval == 2) {info.betWinnings = info.pairPlusWager*31 - info.pairPlusWager;	info.totalWinnings += info.betWinnings;}
			else if(clientEval == 1) {info.betWinnings = info.pairPlusWager*41 - info.pairPlusWager;	info.totalWinnings += info.betWinnings;}
		}
		
		//if dealer has a invalid hand, return
		if(info.dealerInvalidHand) 
			return;
		
		//see whether dealer or client won
		if(dealersEval < clientEval) {	//if dealer won
			info.lostGame = true;
			info.betWinnings -= info.anteWager*2;	//anteWager + playWager
		}
		else if(dealersEval == clientEval) {
			int dealHighest = info.dealerCardsNum.get(0);
			int clientHighest = info.playerCardsNum.get(0);
			
			if(dealHighest < clientHighest) {info.betWinnings += info.anteWager*2;}	//client Wins, highest card
			else if(dealHighest > clientHighest) {info.lostGame = true; info.betWinnings -= info.anteWager*2;}	//dealer wins, highest card
			else {info.drawGame = true; return;}	//tie
		}
		else {	//if Client won
			//(anteWager + playWager = 2*(anteWager), 1 to 1 payout: 2*(antewager)
			info.betWinnings += info.anteWager*2;	
		}
		info.totalWinnings += info.betWinnings;
		
		return;
	}
	
	public void addEvents(PokerInfo info, int clientEval, int dealersEval) {
		
		//if client folded
		if(info.clientFold) {
			info.events.add("Player FOLDED");
			info.events.add("Player Lost: " + (info.anteWager + info.pairPlusWager));
			info.events.add("Total Winnings: " + info.totalWinnings);
			return;
		}
		
		//check pair Plus
		if(info.pairPlusWager > 0) {
			if(clientEval == 6) {info.events.add("Player lost the Pair Plus Wager");}
			else if(clientEval == 5) {info.events.add("Pair! Player won the Pair Plus Wager! Payout: 1 to 1");}
			else if(clientEval == 4) {info.events.add("Flush! Player won the Pair Plus Wager! Payout: 3 to 1");}
			else if(clientEval == 3) {info.events.add("Straight! Player won the Pair Plus Wager! Payout: 6 to 1");}
			else if(clientEval == 2) {info.events.add("Three of a Kind! Player won the Pair Plus Wager! Payout: 30 to 1");}
			else if(clientEval == 1) {info.events.add("Straight Flush! Player won the Pair Plus Wager! Payout: 40 to 1");}
		}
		else {	//evaluate clients cards, normally
			//if(clientEval == 6) {info.events.add("You have no Winning Pair");}
			if(clientEval == 5) {info.events.add("Player Has a Pair!");}
			else if(clientEval == 4) {info.events.add("Player Has a Flush!");}
			else if(clientEval == 3) {info.events.add("Player Has a Straight!");}
			else if(clientEval == 2) {info.events.add("Player Has a Three of a Kind!");}
			else if(clientEval == 1) {info.events.add("Player Has a straight Flush!");}
		}
		
		if(!info.clientFold && !info.clientPlay) //dealing part of the game
			return;
		
		
		//check if dealers hand is invalid
		if(info.dealerInvalidHand) {
			info.events.add("Dealer does not have at least a Queen High Hand; Ante wager is pushed");
			return;
		}
		
		//if(dealersEval == 6) {info.events.add("Dealer has no winning hand");}
		if(dealersEval == 5) {info.events.add("Dealer has a Pair");}
		else if(dealersEval == 4) {info.events.add("Dealer has a Flush");}
		else if(dealersEval == 3) {info.events.add("Dealer has a Straight");}
		else if(dealersEval == 2) {info.events.add("Dealer has a Three of a Kind");}
		else if(dealersEval == 1) {info.events.add("Dealer has a Straight Flush!");}
		
		//check if player won/lost 
		if(clientEval < dealersEval) 
			info.events.add("Player Beat the Dealers Hand!");
		else if(clientEval > dealersEval)
			info.events.add("Player Lost to the Dealers Hand!");
		else { 	//draw
			if(info.lostGame) 	//dealer had highest card
				info.events.add("Dealer Wins, Dealer has the Highest Card.");
			else if(info.drawGame == false) 	//client had highest card
				info.events.add("Player Wins, Player has the Highest Card.");
			else 
				info.events.add("Draw, ante and play wagers returned");
		}
		if(info.betWinnings < 0) 
			info.events.add("Player Lost: " + info.betWinnings);
		
		else if(info.betWinnings > 0) 
			info.events.add("Player Won: " + info.betWinnings);
		
		else if(info.betWinnings == 0) 
			info.events.add("DRAW Game");
		
		info.events.add("Total Winnings: " + info.totalWinnings);
		return;
	}
	
	
	/*	Evaluate User Hand. the smaller the number, the greater the hand. 
	*	1 - Straight Flush
	*	2 - Three of a Kind
	*	3 - Straight
	*	4 - Flush
	*	5 - Pair
	*	6 - nothing
	*/	
	public int evaluateHand(ArrayList<Integer> cardNums, ArrayList<Integer> cardTypes) {
		
		if(cardTypes.size() == 0) {return -1;}	//doesnt have cards, return -1
		
		boolean flag = true;	//once a condition is broken, this will turn into false
		int type;
		int num;
		
		//check if hand is a straight flush
		type = cardTypes.get(0);
		num = cardNums.get(0);
		for(int i = 1; i < cardTypes.size(); ++i) {
			if(cardTypes.get(i) != type) {flag = false; break;}
			if(num-1 != cardNums.get(i)) {flag = false; break;}
			num = cardNums.get(i);
		}
		if(flag)
			return 1;
		
		flag = true;
		//check if hand is three of a kind
		num = cardNums.get(0);
		for(int i = 1; i < cardNums.size(); ++i) {
			if(cardNums.get(i) != num) {flag = false; break;}
		}
		if(flag)
			return 2;
		
		flag = true;
		//check if hand is a straight
		num = cardNums.get(0);
		for(int i = 1; i < cardNums.size(); i++) {
			if(num-1 != cardNums.get(i)) {flag = false; break;}
			num = cardNums.get(i);
		}
		if(flag)
			return 3;
		
		flag = true;
		//check if hand is a flush
		type = cardTypes.get(0);
		for(int i = 1; i < cardTypes.size(); ++i) {
			if(cardTypes.get(i) != type) {flag = false; break;}
		}
		if(flag)
			return 4;
		
		
		flag = false;
		
		//check if hand has a pair
		for(int i = 0; i < cardTypes.size(); ++i) {
			num = cardNums.get(i);
			for(int j = i+1; j < cardTypes.size(); ++j) {
				if(cardNums.get(j) == num) {flag = true; break;}
			}
		}
		if(flag)
			return 5;
		
		//hand had nothing
		return 6;
	}
	
	//returns true if the dealer has a hand that is not at least Queen High or better
	public boolean dealerInvalidHand(PokerInfo info) {
		boolean invalid = true;
		for(Integer i: info.dealerCardsNum) {
			if(i >= 12)
				invalid = false;
		}
		return invalid;
		
	}
	
	/* CARDNUMS: (2 - 14)
	*	2 to 10 - number 
	*	11 - Jack
	*	12 - Queen
	*	13 - King
	*	14-  Ace
	*/
	/* CARD TYPES: 
	 * 0 - Clubs
	 * 1 - Spades
	 * 2 - Diamonds
	 * 3 - Hearts
	 */
	public void getCards(PokerInfo info){
		Random rand = new Random();
		
		//player Cards
		for(int i = 0; i < 3; ++i) {
			int num = rand.nextInt(13)+2;
			int type = rand.nextInt(4);
			if(info.deck.get(type).get(num-2) != -1){
				info.playerCardsNum.add(num);
				info.playerCardsType.add(type);
				info.deck.get(type).set(num-2, -1);	//remove card from deck
			}
			else
				--i;	//if already pulled out of deck, get a new num
		}
	
		Collections.sort(info.playerCardsNum);
		Collections.reverse(info.playerCardsNum);	//sort in descending order
	
		//dealer Cards
		for(int i = 0; i < 3; ++i) {
			int num = rand.nextInt(13)+2;
			int type = rand.nextInt(4);
			if(info.deck.get(type).get(num-2) != -1){
				info.dealerCardsNum.add(num); 
				info.dealerCardsType.add(type);	
				info.deck.get(type).set(num-2, -1);	//remove card from deck
			}
			else
				--i;	//if already pulled out of deck, get a new num
		}
		Collections.sort(info.dealerCardsNum);
		Collections.reverse(info.dealerCardsNum);	//sort in descending order
		
		
		return;
	}
}
