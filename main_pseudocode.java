//in onCreate
{
	BlackJackGame game = new BlackJackGame();
	//start game
}
	
//in some startgame procedure
{
	game.deal(game.getPlayer());
	game.deal(game.getDealer());
	updateGUI(player1);
	updateGUI(dealer);

	if(game.getPlayer().hasBlackJack()){
		game.revealHole();
		updateGUI(dealer);
		if(game.getDealer().hasBlackJack()){
			//push
		}
		
		else{
			//player1 wins
		}
	}
}

//player1's turn to make a move

//event that player1 presses stand button
{
	game.getPlayer().stand();
	//disable hit button?
	updateGUI(player1);
	game.revealHole();
	updateGUI(dealer);

	if(game.getDealer().hasBlackJack()){
		//dealer wins
	}

	else{
		while(game.getDealer().getScore() < 17){
			//dealer must hit (all aces counted as 11)
			game.getDealer().hit();
			updateGUI(dealer);
		}
		
		game.getDealer().stand();
		
		//game over, determine winner
		if(game.getDealer().hasBusted()){
			//player1 wins
		}
		
		else if(game.getDealer().getScore() < game.getPlayer().getScore()){
			//player1 wins
		}

		else if(game.getDealer().getScore() == game.getPlayer().getScore()){
			//push
		}
		
		else{ /*dealer's score is higher than player1's score*/
			//dealer wins
		}
	}
}

//event that player1 presses hit
{
	game.getPlayer().hit();
	updateGUI(player1);
	if(game.getPlayer().hasBusted()){
		//disable button?
		game.deductBet();
		//etc. etc.
		return;
	}
	
	if(game.getPlayer().getScore() == 21){
		/*program will auto-stand for the player once they hit BlackJack*/
		//trigger event that player1 presses stand
	}
}