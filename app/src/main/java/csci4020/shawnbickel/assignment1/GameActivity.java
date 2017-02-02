package csci4020.shawnbickel.assignment1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import csci4020.shawnbickel.assignment1.blackjack.R;

/**
 * Created by Judson Thomas on 2/1/17.
 */

public class GameActivity extends AppCompatActivity {
    private BlackJackGame game;
    private BlackJackGame.Player player;
    private BlackJackGame.Player dealer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*initialize views*/
        setContentView(R.layout.activity_blackjack_game);

        Button hitButton = (Button) findViewById(R.id.Hit);
        Button standButton = (Button) findViewById(R.id.Stand);

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
        updateGUI(player1);
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
        updateGUI(player1 and dealer);
        startGameEvent();
    }

    /*the event for the player standing; triggered by stand button or a special
    * case whenever a natural blackjack is dealt to the player*/
    private void standEvent(){
        player.stand();
        //disable hit button?
        updateGUI(player1);
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
        updateGUI(player1);
        if(player.isBusted()){
            //disable button?
            game.deductBet();
            updateGUI(player1);
            //etc. etc.
            return;
        }

        if(player.getScore() == 21){
            //program will auto-stand for the player once they hit BlackJack
            //trigger event that player1 presses stand
            standEvent();
        }
    }
}

