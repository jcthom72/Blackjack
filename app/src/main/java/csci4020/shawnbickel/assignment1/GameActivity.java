package csci4020.shawnbickel.assignment1;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import csci4020.shawnbickel.assignment1.blackjack.R;

/**
 * Created by Judson Thomas on 2/1/17.
 */

public class GameActivity extends AppCompatActivity {
    // variables
    private BlackJackGame game;
    private BlackJackGame.Player player;
    private BlackJackGame.Player dealer;
    private TextView dealerScore;
    private TextView playerScore;
    private ImageView playerImage1;
    private ImageView playerImage2;
    private ImageView dealerImage1;
    private ImageView dealerImage2;
    private Button hitButton;
    private Button standButton;
    private Spinner bet;
    private int startGameIndicator = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*initialize views*/
        setContentView(R.layout.activity_blackjack_game);

        // connects variable names to widget ids in the activity layout
        playerImage1 = (ImageView) findViewById(R.id.faceDownPlayer1);
        playerImage2 = (ImageView) findViewById(R.id.faceDownPlayer2);
        dealerImage1 = (ImageView) findViewById(R.id.faceDownDealer1);
        dealerImage2 = (ImageView) findViewById(R.id.faceDownDealer2);
        playerScore = (TextView) findViewById(R.id.PlayerScore);
        dealerScore = (TextView) findViewById(R.id.DealerScore);
        hitButton = (Button) findViewById(R.id.Hit);
        standButton = (Button) findViewById(R.id.Stand);

        // Spinner provided a list of betting choices for the user to choose from
        bet = (Spinner) findViewById(R.id.bettingSpinner);

        // ArrayAdapter populates the spinner with the contents of a string array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.betting_amounts, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assert bet != null;
        bet.setAdapter(adapter);


        //set on click listeners

        //hooking up hitEvent to hitButton's onClickListener
        if(hitButton != null) {
            hitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*hit button will trigger hit event*/
                    //bet.setEnabled(false);
                    hitEvent();
                }
            });
        }

        //hooking up standEvent to standButton's onClickListener
        if(standButton != null) {
            standButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*stand button will trigger stand event*/
                    //hitButton.setEnabled(false);
                    //standButton.setEnabled(false);
                    standEvent();
                }
            });
        }

        /*initialize game*/
        game = new BlackJackGame();
        player = game.getPlayer(); /*grab reference to dealer for convenience*/
        dealer = game.getDealer(); /*grab reference to player1 for convenience*/

        //start game
        startGameEvent();
    }

    /*the event for the game starting; could be triggered by
    * a start button but probably should just be triggered by onCreate; also
    * triggered by resetGameEvent*/
    private void startGameEvent() {
        //hitButton.setEnabled(true);
        //standButton.setEnabled(true);
        //bet.setEnabled(true);
        game.deal(player);
        game.deal(dealer);
        updateGUI(player);
        updateGUI(dealer);

        if(player.hasBlackJack()){
            game.revealHole();
            updateGUI(dealer);
            if(dealer.hasBlackJack()){
                //push
            }

            else{
                //player1 wins
            }
        }
    }

    /*the event for the game restarting; should be triggered by a reset button; perhaps
    * we can change the hit button to turn into a reset button whenever the game ends*/
    private void resetGameEvent(){
        game.reset();
        updateGUI(player);
        updateGUI(dealer);
        playerImage1.setImageResource(R.drawable.playerfacedownone);
        playerImage2.setImageResource(R.drawable.playerfacedowntwo);
        dealerImage1.setImageResource(R.drawable.dealerfacedownone);
        dealerImage2.setImageResource(R.drawable.dealerfacedowntwo);
        startGameEvent();
    }

    /*the event for the player standing; triggered by stand button or a special
    * case whenever a natural blackjack is dealt to the player*/
    private void standEvent(){
        player.stand();
        // disable hit button?
        //standButton.setEnabled(false);
        //updateGUI(player);
        game.revealHole();
        //updateGUI(dealer);

        if(dealer.hasBlackJack()){
            if(player.hasBlackJack()){
                if(player.viewHand().size() > dealer.viewHand().size()){
                    //dealer wins
                }
                else if(player.viewHand().size() < dealer.viewHand().size()){
                    //player wins
                }

                else{
                    //push
                }
            }
            else{
                //dealer wins
            }
        }

        else{
            while(dealer.getScore() < 17){
                //dealer must hit (all aces counted as 11)
                dealer.hit();
                updateGUI(dealer);
            }

            dealer.stand();

            //game over, determine winner
            if(dealer.isBusted()){
                //player1 wins
            }

            else if(dealer.getScore() < player.getScore()){
                //player1 wins
            }

            else if(dealer.getScore() == player.getScore()){
                //push
            }

            else{ /*dealer's score is higher than player1's score*/
                //dealer wins
            }
        }
    }

    /*the event for the player hitting; typically triggered by hit button*/
    private void hitEvent(){
        startGameIndicator++;
        player.hit();
        updateGUI(player);
        if(player.isBusted()){
            //disable button?
            game.deductBet();
            updateGUI(player);
            //etc. etc.
            return;
        }

        if(player.getScore() == 21){
            //program will auto-stand for the player once they hit BlackJack
            //trigger event that player1 presses stand
            standEvent();
        }
    }

    /* updateGUI updates player and dealer's scores as well as the images at different points
        during the BlackJackGame */
    private void updateGUI(BlackJackGame.Player p) {
        int s = p.getScore();
        String score = Integer.toString(s);
        BlackJackGame.Deck randomCardDeck = new BlackJackGame.Deck();
        BlackJackGame.Card image;

        try{
            // following if statements reset the game if either player busts or has a blackjack
            if (player.hasBlackJack() && dealer.isBusted()){
                resetGameEvent();
            }

            else if (dealer.hasBlackJack() && player.isBusted()){
                resetGameEvent();
            }

            else if ((dealer.hasBlackJack() && player.hasBlackJack()) || (dealer.isBusted() && player.isBusted())){
                resetGameEvent();
            }

            /*
            else if (player.isStanding() && dealer.isStanding()){
                resetGameEvent();
            }
            */

        }catch (NullPointerException e){

        }

        // if statement executes if the hit button is pressed; receives random card from popCard()
        if(!(p.isStanding()) && startGameIndicator > 0){
            image = randomCardDeck.popCard();
            chooseImage(image, p);
        }

        // executes when game first starts
        if (p == player && startGameIndicator == 0){
            try{
                playerScore.setText(score);
                image = randomCardDeck.popCard();
                chooseImage(image, p);
                image = randomCardDeck.popCard();
                chooseImage2(image, p);
            }catch (NullPointerException e){

            }

        }
        // generates random cards for the dealer
        if (p == dealer && startGameIndicator == 0){
            try{
                dealerScore.setText(score);
                image = randomCardDeck.popCard();
                chooseImage(image, p);
                image = randomCardDeck.popCard();
                chooseImage2(image, p);
            }catch (NullPointerException e){

            }

        }
    }

    /**********************************************************************************************
     * chooseImage and chooseImage2 methods sets the images in the layout based on the card
     *      randomly chosen by popcard
     **********************************************************************************************/
    private void chooseImage(BlackJackGame.Card image, BlackJackGame.Player p){
        if (image.rank == BlackJackGame.Card.Rank.ACE){
            if (p == player)
                playerImage1.setImageResource(R.drawable.acecard);
            else if (p == dealer)
                dealerImage1.setImageResource(R.drawable.acecard);
        }


        else if (image.rank == BlackJackGame.Card.Rank.KING){
            if (p == player)
                playerImage1.setImageResource(R.drawable.kingcard);
            else if (p == dealer)
                dealerImage1.setImageResource(R.drawable.kingcard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.QUEEN){
            if (p == player)
                playerImage1.setImageResource(R.drawable.queencard);
            else if (p == dealer)
                dealerImage1.setImageResource(R.drawable.queencard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.JACK){
            if (p == player)
                playerImage1.setImageResource(R.drawable.jackcard);
            else if (p == dealer)
                dealerImage1.setImageResource(R.drawable.jackcard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.TWO){
            if (p == player)
                playerImage1.setImageResource(R.drawable.twocard);
            else if (p == dealer)
                dealerImage1.setImageResource(R.drawable.twocard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.THREE){
            if (p == player)
                playerImage1.setImageResource(R.drawable.threecard);
            else if (p == dealer)
                dealerImage1.setImageResource(R.drawable.threecard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.FOUR){
            if (p == player)
                playerImage1.setImageResource(R.drawable.fourcard);
            else if (p == dealer)
                dealerImage1.setImageResource(R.drawable.fourcard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.FIVE){
            if (p == player)
                playerImage1.setImageResource(R.drawable.fivecard);
            else if (p == dealer)
                dealerImage1.setImageResource(R.drawable.fivecard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.SIX){
            if (p == player)
                playerImage1.setImageResource(R.drawable.sixcard);
            else if (p == dealer)
                dealerImage1.setImageResource(R.drawable.sixcard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.SEVEN){
            if (p == player)
                playerImage1.setImageResource(R.drawable.sevencard);
            else if (p == dealer)
                dealerImage1.setImageResource(R.drawable.sevencard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.EIGHT){
            if (p == player)
                playerImage1.setImageResource(R.drawable.eightcard);
            else if (p == dealer)
                dealerImage1.setImageResource(R.drawable.eightcard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.NINE){
            if (p == player)
                playerImage1.setImageResource(R.drawable.ninecard);
            else if (p == dealer)
                dealerImage1.setImageResource(R.drawable.ninecard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.TEN){
            if (p == player)
                playerImage1.setImageResource(R.drawable.tencard);
            else if (p == dealer)
                dealerImage1.setImageResource(R.drawable.tencard);
        }


    }

    private void chooseImage2(BlackJackGame.Card image, BlackJackGame.Player p){
        if (image.rank == BlackJackGame.Card.Rank.ACE){
            if (p == player)
                playerImage2.setImageResource(R.drawable.acecard);
            else if (p == dealer)
                dealerImage2.setImageResource(R.drawable.acecard);
        }


        else if (image.rank == BlackJackGame.Card.Rank.KING){
            if (p == player)
                playerImage2.setImageResource(R.drawable.kingcard);
            else if (p == dealer)
                dealerImage2.setImageResource(R.drawable.kingcard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.QUEEN){
            if (p == player)
                playerImage2.setImageResource(R.drawable.queencard);
            else if (p == dealer)
                dealerImage2.setImageResource(R.drawable.queencard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.JACK){
            if (p == player)
                playerImage2.setImageResource(R.drawable.jackcard);
            else if (p == dealer)
                dealerImage2.setImageResource(R.drawable.jackcard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.TWO){
            if (p == player)
                playerImage2.setImageResource(R.drawable.twocard);
            else if (p == dealer)
                dealerImage2.setImageResource(R.drawable.twocard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.THREE){
            if (p == player)
                playerImage2.setImageResource(R.drawable.threecard);
            else if (p == dealer)
                dealerImage2.setImageResource(R.drawable.threecard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.FOUR){
            if (p == player)
                playerImage2.setImageResource(R.drawable.fourcard);
            else if (p == dealer)
                dealerImage2.setImageResource(R.drawable.fourcard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.FIVE){
            if (p == player)
                playerImage2.setImageResource(R.drawable.fivecard);
            else if (p == dealer)
                dealerImage2.setImageResource(R.drawable.fivecard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.SIX){
            if (p == player)
                playerImage2.setImageResource(R.drawable.sixcard);
            else if (p == dealer)
                dealerImage2.setImageResource(R.drawable.sixcard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.SEVEN){
            if (p == player)
                playerImage2.setImageResource(R.drawable.sevencard);
            else if (p == dealer)
                dealerImage2.setImageResource(R.drawable.sevencard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.EIGHT){
            if (p == player)
                playerImage2.setImageResource(R.drawable.eightcard);
            else if (p == dealer)
                dealerImage2.setImageResource(R.drawable.eightcard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.NINE){
            if (p == player)
                playerImage2.setImageResource(R.drawable.ninecard);
            else if (p == dealer)
                dealerImage2.setImageResource(R.drawable.ninecard);
        }

        else if (image.rank == BlackJackGame.Card.Rank.TEN){
            if (p == player)
                playerImage2.setImageResource(R.drawable.tencard);
            else if (p == dealer)
                dealerImage2.setImageResource(R.drawable.tencard);
        }


    }
}

