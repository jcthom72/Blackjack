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

public class GameImplementation extends AppCompatActivity {
    BlackJackGame game = new BlackJackGame();
    Spinner bet;
    ImageView playerImage1;
    ImageView playerImage2;
    ImageView dealerImage1;
    ImageView dealerImage2;
    TextView playerScore;
    TextView dealerScore;
    Button hit;
    Button stand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackjack_game);
        playerImage1 = (ImageView) findViewById(R.id.faceDownPlayer1);
        playerImage2 = (ImageView) findViewById(R.id.faceDownPlayer2);
        dealerImage1 = (ImageView) findViewById(R.id.faceDownDealer1);
        dealerImage2 = (ImageView) findViewById(R.id.faceDownDealer2);
        playerScore = (TextView) findViewById(R.id.PlayerScore);
        dealerScore = (TextView) findViewById(R.id.DealerScore);
        hit = (Button) findViewById(R.id.Hit);
        stand = (Button) findViewById(R.id.Stand);
        bet = (Spinner) findViewById(R.id.bettingSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.betting_amounts, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assert bet != null;
        bet.setAdapter(adapter);


        //start game
    }

    public void startActivity(){
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
        stand.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                hit.setEnabled(false);
                game.getPlayer().stand();
                //disable hit button?
                updateGUI(player1);
                game.revealHole();
                updateGUI(dealer);

                if(game.getDealer().hasBlackJack()){
                    if(game.getPlayer().hasBlackJack()){
                        if(game.getPlayer().viewHand().size() > game.getDealer().viewHand().size()){
                            //dealer wins
                        }
                        else if(game.getPlayer().viewHand().size() < game.getDealer().viewHand().size()){
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
        });

//event that player1 presses hit
        hit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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

        });

    }

    public void updateGUI(BlackJackGame.Player p){
        if (p == dealer){
            dealerScore.setText(p.getScore());

        }else{
            playerScore.setText(p.getScore());
        }



    }
}
