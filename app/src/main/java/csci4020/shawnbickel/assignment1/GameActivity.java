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
        Spinner bet = (Spinner) findViewById(R.id.bettingSpinner);

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
        startGameEvent();
    }

    /*the event for the player standing; triggered by stand button or a special
    * case whenever a natural blackjack is dealt to the player*/
    private void standEvent(){
        player.stand();
        //disable hit button?
        updateGUI(player);
        game.revealHole();
        updateGUI(dealer);

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

    private void updateGUI(BlackJackGame.Player player) {
        /*
        if (player == dealer){
            dealerScore.setText(player.getScore());

        }else{
            playerScore.setText(player.getScore());
        }

        */
    }
}

