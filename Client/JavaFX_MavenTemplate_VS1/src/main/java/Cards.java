import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Cards{
	/*
	 * CARD TYPES:
	 * 0 - Clubs
	 * 1 - Spades
	 * 2 - Diamonds
	 * 3 - Hearts
	 * CARD NUMS:
	 *  1 - Ace
	 *	2 to 10 - number 
	 *	11 - Jack
	 *	12 - Queen
	 *	13 - King
	 */
	public ImageView getCard(int type, int num) {
		String path = "/PokerCards/PNG-cards-1.3/"; //represents path to card png -- in main/resources/PNG-cards1.3
		if(num > 10) {
			if(num == 11) {path+= "jack_of_";}
			else if(num == 12) {path += "queen_of_";}
			else if(num == 13) {path += "king_of_";}
			else if(num == 14) {path += "ace_of_";}
		}
	
		else {path += num + "_of_";}
		
		if(type == 0) {path += "clubs";}
		if(type == 1) {path += "spades";}
		if(type == 2) {path += "diamonds";}
		if(type == 3) {path += "hearts";}
		path += ".png";
		
		Image card = new Image(path);
		ImageView cardView = new ImageView(card);
		cardView.setFitHeight(100);
		cardView.setFitWidth(100);
		
		return cardView;
	}
	
	public ImageView getBacksideCard() {
		String path = "/PokerCards/backside_of_card2.jpg";	//2 is smaller than original, faster to load
		Image card = new Image(path);
		ImageView cardView = new ImageView(card);
		cardView.setFitHeight(100);
		cardView.setFitWidth(100);
		return cardView;
	}
}