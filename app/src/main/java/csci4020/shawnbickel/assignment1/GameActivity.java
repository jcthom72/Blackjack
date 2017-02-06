package csci4020.shawnbickel.assignment1;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

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
    private Vector<ImageView> playerCardImages;
    private Vector<ImageView> dealerCardImages;
    private final int PLAYERWINS = 1;
    private final int DEALERWINS = 2;
    private final int PUSH = 3;

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

        //initialize image view vectors
        playerCardImages = new Vector<ImageView>();
        playerCardImages.add(playerImage1);
        playerCardImages.add(playerImage2);

        dealerCardImages = new Vector<ImageView>();
        dealerCardImages.add(dealerImage1);
        dealerCardImages.add(dealerImage2);


        //set on click listeners


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
        hitButton.setText("HIT");
        //hooking up hitEvent to hitButton's onClickListener
        if(hitButton != null) {
            hitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*hit button will trigger hit event*/
                    hitEvent();
                }
            });
        }

        int betAmount = Integer.parseInt(bet.getSelectedItem().toString());
        player.wager(betAmount);
        game.deal(player);
        game.deal(dealer);
        updateGUI(player);
        updateGUI(dealer);

        if(player.hasBlackJack()){
            game.revealHole();
            updateGUI(dealer);
            if(dealer.hasBlackJack()){
                //push
                GameWinEvent(PUSH);
            }

            else{
                //player1 wins
                game.giftBet();
                GameWinEvent(PLAYERWINS);
            }
        }
    }

    /*the event for the game restarting; should be triggered by a reset button; perhaps
    * we can change the hit button to turn into a reset button whenever the game ends*/
    private void resetGameEvent(){
        //bet.setEnabled(false);   // prevents the user from changing bet after starting game
        game.reset();
        updateGUI(player);
        updateGUI(dealer);

        //update imageview vectors
        //remove views from layout
        RelativeLayout playerLayout = (RelativeLayout) findViewById(R.id.playersHand);
        RelativeLayout dealerLayout = (RelativeLayout) findViewById(R.id.dealersHand);

        for(View image : playerCardImages){
            playerLayout.removeView(image);
        }

        for(View image : dealerCardImages){
            dealerLayout.removeView(image);
        }

        playerCardImages.clear();
        dealerCardImages.clear();
        playerImage1.setImageResource(R.drawable.playerfacedownone);
        playerImage2.setImageResource(R.drawable.playerfacedowntwo);
        dealerImage1.setImageResource(R.drawable.dealerfacedownone);
        dealerImage2.setImageResource(R.drawable.dealerfacedowntwo);
        playerCardImages.add(playerImage1);
        playerCardImages.add(playerImage2);
        dealerCardImages.add(dealerImage1);
        dealerCardImages.add(dealerImage2);
        startGameEvent();
    }

    /*the event for the player standing; triggered by stand button or a special
    * case whenever a natural blackjack is dealt to the player*/
    private void standEvent(){
        player.stand();
        // disable hit button?
        //standButton.setEnabled(false);
        updateGUI(player);
        game.revealHole();
        updateGUI(dealer);

        if(dealer.hasBlackJack()){
            if(player.hasBlackJack()){
                if(player.viewHand().size() > dealer.viewHand().size()){
                    //dealer wins
                    game.deductBet();
                    GameWinEvent(DEALERWINS);
                }
                else if(player.viewHand().size() < dealer.viewHand().size()){
                    //player wins
                    game.giftBet();
                    GameWinEvent(PLAYERWINS);
                }

                else{
                    //push
                    GameWinEvent(PUSH);
                }
            }
            else{
                //dealer wins
                game.deductBet();
                GameWinEvent(DEALERWINS);
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
                game.giftBet();
                GameWinEvent(PLAYERWINS);
            }

            else if(dealer.getScore() < player.getScore()){
                //player1 wins
                game.giftBet();
                GameWinEvent(PLAYERWINS);
            }

            else if(dealer.getScore() == player.getScore()){
                //push
                GameWinEvent(PUSH);
            }

            else{ /*dealer's score is higher than player1's score*/
                //dealer wins
                game.deductBet();
                GameWinEvent(DEALERWINS);
            }
        }
    }

    /*the event for the player hitting; typically triggered by hit button*/
    private void hitEvent(){
        player.hit();
        updateGUI(player);
        if(player.isBusted()){
            //dealer wins


            //disable button?
            game.deductBet();
            updateGUI(player);
            //etc. etc.
            GameWinEvent(DEALERWINS);
            return;
        }

        if(player.getScore() == 21){
            //program will auto-stand for the player once they hit BlackJack
            //trigger event that player1 presses stand
            standEvent();
        }
    }

    private void GameWinEvent(int winIndicator){
        //bet.setEnabled(true);  // allows the user to enter a bet for next game
        String winText = "Player wins";
        if (winIndicator == 1){
            winText = "player 1 wins";
        }

        else if (winIndicator == 2){
            winText = "dealer wins";
        }

        else if (winIndicator == 3){
            winText = "tie";
        }

        Toast t = Toast.makeText(this, winText, Toast.LENGTH_LONG);
        t.show();

        //repurpose hit button to new game button
        hitButton.setText("New Game");
        hitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGameEvent();
            }
        });

    }

    /* updateGUI updates player and dealer's scores as well as the images at different points
        during the BlackJackGame */
    private void updateGUI(BlackJackGame.Player playerToUpdate) {
        float cardPadding = 100;
        Vector<ImageView> imagesToUpdate;
        TextView scoreToUpdate;

        /*determine which views need to be updated based on playerToUpdate*/
        if(playerToUpdate == player) {
            imagesToUpdate = playerCardImages;
            scoreToUpdate = playerScore;
        }
        
        else if(playerToUpdate == dealer){
            imagesToUpdate = dealerCardImages;
            scoreToUpdate = dealerScore;
        }
        
        else { /*error*/
            return;
        }
        
        /*update score*/
        scoreToUpdate.setText("" + playerToUpdate.getScore());

        /*update deck images*/
        Vector<BlackJackGame.Card> hand = playerToUpdate.viewHand();
        BlackJackGame.Card card;
        ImageView cardImage;
        RelativeLayout layout;

        if(playerToUpdate == player) {
            layout = (RelativeLayout) findViewById(R.id.playersHand);
        }

        else if(playerToUpdate == dealer){
            layout = (RelativeLayout) findViewById(R.id.dealersHand);
        }

        else{
            /*error occurred*/
            return;
        }

        if(hand.size() > 0) {
            for (int i = 0; i < hand.size(); i++) {
                if (i < imagesToUpdate.size()) {
                /*ImageView already exists: update image resource for imageview*/
                    cardImage = imagesToUpdate.elementAt(i);
                    cardImage.setImageResource(cardToImage(hand.elementAt(i)));
                }

                else {
                /*ImageView does not exist: add new image view*/
                    cardImage = new ImageView(getApplicationContext());
                    if(imagesToUpdate.size() > 0) {
                        /*set X based on position card is in hand*/
                        cardImage.setX(imagesToUpdate.lastElement().getX() + cardPadding);
                        cardImage.setY(imagesToUpdate.lastElement().getY());
                    }

                    //else{
                    ///*set X / Y to initial card position*/
                    //cardImage.setX(100f); //test value
                    //cardImage.setY(100f); //test value
                    //}
                    cardImage.setImageResource(cardToImage(hand.elementAt(i)));
                    cardImage.setEnabled(true);
                    cardImage.setVisibility(View.VISIBLE);
                    layout.addView(cardImage);
                    imagesToUpdate.add(cardImage);

                }
            }
        }

        else{
            /*hand is empty; add two blank cards*/
            imagesToUpdate.clear();
            cardImage = new ImageView(getApplicationContext());
            //cardImage.setX(??);
            //cardImage.setY(??);
            if(playerToUpdate == player) {
                cardImage.setImageResource(R.drawable.playerfacedownone);
            }
            else if(playerToUpdate == dealer) {
                cardImage.setImageResource(R.drawable.dealerfacedownone);
            }
            cardImage.setEnabled(true);
            imagesToUpdate.add(cardImage);

            cardImage = new ImageView(getApplicationContext());
            //cardImage.setX(??);
            //cardImage.setY(??);
            if(playerToUpdate == player) {
                cardImage.setImageResource(R.drawable.playerfacedowntwo);
            }
            else if(playerToUpdate == dealer) {
                cardImage.setImageResource(R.drawable.dealerfacedowntwo);
            }
            cardImage.setEnabled(true);
            imagesToUpdate.add(cardImage);
        }
    }

    /*maps a Card object to the appropriate resource id for the corresponding card image*/
    int cardToImage(BlackJackGame.Card card){
        switch(card.getSuit()){
            case DIAMONDS:
                switch(card.getRank()) {
                    case MYSTERY:
                        return R.drawable.dealerfacedownone; /*is this correct?*/
                    case ACE:
                        return R.drawable.acecard;
                    case TWO:
                        return R.drawable.twocard;
                    case THREE:
                        return R.drawable.threecard;
                    case FOUR:
                        return R.drawable.fourcard;
                    case FIVE:
                        return R.drawable.fivecard;
                    case SIX:
                        return R.drawable.sixcard;
                    case SEVEN:
                        return R.drawable.sevencard;
                    case EIGHT:
                        return R.drawable.eightcard;
                    case NINE:
                        return R.drawable.ninecard;
                    case JACK:
                        return R.drawable.jackcard;
                    case KING:
                        return R.drawable.kingcard;
                    case QUEEN:
                        return R.drawable.queencard;
                    default:
                        /*Invalid rank: Some error occurred... throw an exception, etc. etc.*/
                        break;
                }
                break;

            default:
                /*Invalid suit: Some error occurred... throw an exception, etc. etc.*/
                break;
        }

        //to bypass missing return statement error
        return 0;
    }
}

