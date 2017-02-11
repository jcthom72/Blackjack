package csci4020.shawnbickel.assignment1;


import android.content.Context;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
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
    private TextView playerBank;
    private Button hitButton;
    private Button standButton;
    private Spinner bet;
    private Vector<ImageView> playerCardImages;
    private Vector<ImageView> dealerCardImages;
    private final String SERIALIZABLE_OBJECTS = "BlackJackGame.txt";
    private String bankText;
    private final int PLAYERWINS = 1;
    private final int DEALERWINS = 2;
    private final int PUSH = 3;
    private final int PURPOSE_NEW_GAME = 1;
    private final int PURPOSE_HIT = 2;
    private final int PURPOSE_NEXT_GAME = 3;
    private int hitButtonPurpose;
    private int gamePause = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*initialize views*/
        setContentView(R.layout.activity_blackjack_game);
        // connects variable names to widget ids in the activity layout
        playerScore = (TextView) findViewById(R.id.PlayerScore);
        dealerScore = (TextView) findViewById(R.id.DealerScore);
        playerBank = (TextView) findViewById(R.id.bankTextView);
        hitButton = (Button) findViewById(R.id.Hit);
        standButton = (Button) findViewById(R.id.Stand);
        hitButton.setEnabled(true);
        standButton.setEnabled(false);

        // Spinner provided a list of betting choices for the user to choose from
        bet = (Spinner) findViewById(R.id.bettingSpinner);

        // ArrayAdapter populates the spinner with the contents of a string array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.betting_amounts, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assert bet != null;
        bet.setAdapter(adapter);

        //hooking up standEvent to standButton's onClickListener
        if (standButton != null) {
            standButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //standButton.setEnabled(false);
                    //game.setStandButtonState(false);
                    standEvent();
                }
            });
        }

        //initialize image view vectors
        playerCardImages = new Vector<ImageView>();
        dealerCardImages = new Vector<ImageView>();

         /* restore game state*/
        restoreGameState();

        if (savedInstanceState != null) {
            Boolean hitEnabled = savedInstanceState.getBoolean("HIT_BUTTON_ENABLED");
            Boolean standEnabled = savedInstanceState.getBoolean("STAND_BUTTON_ENABLED");
            int hitPurpose = savedInstanceState.getInt("HIT_BUTTON_PURPOSE");
            if (hitPurpose == PURPOSE_HIT){
                game.setGameState(PURPOSE_HIT);
            }else if (hitPurpose == PURPOSE_NEW_GAME){
                game.setGameState(PURPOSE_NEW_GAME);
            }else if (hitPurpose == PURPOSE_NEXT_GAME){
                game.setGameState(PURPOSE_NEXT_GAME);
            }

            // set state of button depending on previous setting
            if (game.getHitButtonState() == true){
                hitButton.setEnabled(true);
                game.setHitButtonState(hitEnabled);
            }else{
                hitButton.setEnabled(false);
                game.setHitButtonState(hitEnabled);

            }

            if (game.getStandButtonState() == true){
                standButton.setEnabled(true);
                game.setStandButtonState(standEnabled);
            }else{
                standButton.setEnabled(false);
                game.setStandButtonState(standEnabled);
            }


            // set game state
            if(game.getGameState() == PURPOSE_HIT){
                hitButton.setText("HIT");
                setHitButtonPurpose(PURPOSE_HIT);
            }else if (game.getGameState() == PURPOSE_NEXT_GAME){
                hitButton.setText("PLAY AGAIN");
                standButton.setEnabled(true);
                setHitButtonPurpose(PURPOSE_NEXT_GAME);
            }else if (game.getGameState() == PURPOSE_NEW_GAME){
                hitButton.setText("RESUME");
                standButton.setEnabled(true);
                setHitButtonPurpose(PURPOSE_NEW_GAME);
            }

        } else {
            setHitButtonPurpose(PURPOSE_NEW_GAME);
            game.setGameState(PURPOSE_NEW_GAME);
        }




    }
    /*the event for the game starting; could be triggered by
    * a start button but probably should just be triggered by onCreate; also
    * triggered by resetGameEvent*/
    private void startGameEvent() {
        //once game starts, set hit button to normal hit event behavior
        hitButton.setEnabled(true);
        setHitButtonPurpose(PURPOSE_HIT);
        game.setGameState(PURPOSE_HIT);
        game.setHitButtonState(true);

        //get player bet
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
                gameEndEvent(PUSH);
            }

            else{
                //player1 wins
                game.giftBet();
                updatePlayerBank();
                gameEndEvent(PLAYERWINS);
            }
        }
    }

    /*the event for the game restarting; should be triggered by a reset button; perhaps
    * we can change the hit button to turn into a reset button whenever the game ends*/
    private void resetGameEvent(){
        game.reset();

        //remove card images from layout
        RelativeLayout playerLayout = (RelativeLayout) findViewById(R.id.playersHand);
        RelativeLayout dealerLayout = (RelativeLayout) findViewById(R.id.dealersHand);

        //remove the added card images from the layout

        for(ImageView image : playerCardImages){
            playerLayout.removeView(image);
        }

        for(View image : dealerCardImages){
            dealerLayout.removeView(image);
        }
        //
        playerCardImages.clear();
        dealerCardImages.clear();

        startGameEvent();
    }

    /*the event for the player standing; triggered by stand button or a special
    * case whenever a natural blackjack is dealt to the player*/
    private void standEvent(){
        player.stand();
        game.revealHole();

        // disable hit button and stand button
        /*
        hitButton.setEnabled(false);
        game.setHitButtonState(false);
        standButton.setEnabled(false);
        game.setStandButtonState(false);
        */
        updateGUI(player);
        updateGUI(dealer);

        if(dealer.hasBlackJack()){
            if(player.hasBlackJack()){
                if(player.viewHand().size() > dealer.viewHand().size()){
                    //dealer wins
                    game.deductBet();
                    updatePlayerBank();
                    gameEndEvent(DEALERWINS);
                }
                else if(player.viewHand().size() < dealer.viewHand().size()){
                    //player wins
                    game.giftBet();
                    updatePlayerBank();
                    gameEndEvent(PLAYERWINS);
                }

                else{
                    //push
                    gameEndEvent(PUSH);
                }
            }
            else{
                //dealer wins
                game.deductBet();
                updatePlayerBank();
                gameEndEvent(DEALERWINS);
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
                updatePlayerBank();
                gameEndEvent(PLAYERWINS);
            }

            else if(dealer.getScore() < player.getScore()){
                //player1 wins
                game.giftBet();
                updatePlayerBank();
                gameEndEvent(PLAYERWINS);
            }

            else if(dealer.getScore() == player.getScore()){
                //push
                gameEndEvent(PUSH);
            }

            else{ /*dealer's score is higher than player1's score*/
                //dealer wins
                game.deductBet();
                updatePlayerBank();
                gameEndEvent(DEALERWINS);
            }
        }
    }

    /*the event for the player hitting; typically triggered by hit button*/
    private void hitEvent(){

        standButton.setEnabled(true);
        game.setStandButtonState(true);
        player.hit();
        updateGUI(player);
        if(player.isBusted()){
            //dealer wins


            //disable button?
            game.deductBet();
            updatePlayerBank();
            updateGUI(player);
            //etc. etc.
            gameEndEvent(DEALERWINS);
            return;
        }

        if(player.getScore() == 21){
            //program will auto-stand for the player once they hit BlackJack
            //trigger event that player1 presses stand
            standEvent();
        }
    }

    /*the event for the game ending; triggered by a win / loss / push*/
    private void gameEndEvent(int winIndicator){
        String winText;

        if (winIndicator == 1){
            winText = "player 1 wins";
        }

        else if (winIndicator == 2){
            winText = "dealer wins";
        }

        else if (winIndicator == 3){
            winText = "tie";
        }

        else {
            /*invalid winIndicator value*/
            return;
        }

        Toast t = Toast.makeText(this, winText, Toast.LENGTH_SHORT);
        t.show();

        //repurpose hit button to next game button
        setHitButtonPurpose(PURPOSE_NEXT_GAME);
        game.setGameState(PURPOSE_NEXT_GAME);
        hitButton.setEnabled(true);
        game.setHitButtonState(true);
        //disable stand button
        standButton.setEnabled(false);
        game.setStandButtonState(false);
        serializeObject(game);  // saves state of game to file in serializeObject method
    }

    // method to update user of view of amount available to bet
    private void updatePlayerBank(){
        int bank = player.getBank();
        bankText = Integer.toString(bank);
        playerBank.setText(bankText);
    }

    /* updateGUI updates player and dealer's scores as well as the images at different points
        during the BlackJackGame */
    private void updateGUI(BlackJackGame.Player playerToUpdate) {
        float cardPadding = 100; //specifies the X coordinate padding value between card images
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

                    else{
                    /*set X / Y to initial card position*/
                        cardImage.setX(300f); //test value
                        cardImage.setY(0); //test value
                    }

                    cardImage.setImageResource(cardToImage(hand.elementAt(i)));
                    cardImage.setEnabled(true);
                    cardImage.setVisibility(View.VISIBLE);
                    layout.addView(cardImage);
                    imagesToUpdate.add(cardImage);
                    cardImage.animate().rotationY(360).start();

                }
            }
        }

        else{
            /*hand is empty; add two blank cards*/
            imagesToUpdate.clear();
            cardImage = new ImageView(getApplicationContext());
            cardImage.setX(300f);
            cardImage.setY(0);
            if(playerToUpdate == player) {
                cardImage.setImageResource(R.drawable.playerfacedownone);
            }
            else if(playerToUpdate == dealer) {
                cardImage.setImageResource(R.drawable.dealerfacedownone);
            }
            cardImage.setEnabled(true);
            cardImage.setVisibility(View.VISIBLE);
            layout.addView(cardImage);
            imagesToUpdate.add(cardImage);

            cardImage = new ImageView(getApplicationContext());
            cardImage.setX(300f + cardPadding);
            cardImage.setY(0);
            if(playerToUpdate == player) {
                cardImage.setImageResource(R.drawable.playerfacedowntwo);
            }
            else if(playerToUpdate == dealer) {
                cardImage.setImageResource(R.drawable.dealerfacedowntwo);
            }
            cardImage.setEnabled(true);
            cardImage.setVisibility(View.VISIBLE);
            layout.addView(cardImage);
            imagesToUpdate.add(cardImage);
        }
    }

    /*maps a Card object to the appropriate resource id for the corresponding card image*/
    int cardToImage(BlackJackGame.Card card){
        //first, check if mystery card
        if(card.getRank() == BlackJackGame.Card.Rank.MYSTERY ||
                card.getSuit() == BlackJackGame.Card.Suit.MYSTERY){
            return R.drawable.dealerfacedownone;
        }

        switch(card.getSuit()){
            case DIAMONDS:
                switch(card.getRank()) {
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
                    case TEN:
                        return R.drawable.tencard;
                    case JACK:
                        return R.drawable.jackcard;
                    case KING:
                        return R.drawable.kingcard;
                    case QUEEN:
                        return R.drawable.queencard;
                    default:
                        /*Invalid rank: Some error occurred... throw an exception, etc. etc.*/
                        return R.drawable.playerfacedownone;
                    //break;
                }
                //break;

            default:
                /*Invalid suit: Some error occurred... throw an exception, etc. etc.*/
                break;
        }

        //to bypass missing return statement error
        return 0;
    }


    /*used to repurpose the hit button to serve as a start button / restart button, etc.*/
    void setHitButtonPurpose(int purpose) {
        if (hitButton == null) {
            return;
        }

        hitButtonPurpose = purpose;

        if (player.isStanding() || dealer.isStanding() || player.isBusted() || dealer.isBusted()){
            hitButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    standButton.setEnabled(true);
                    game.setStandButtonState(true);
                    resetGameEvent();
                }
            });

        }

        else if (purpose == PURPOSE_NEW_GAME) {
            /*make hit button a new game button*/
            hitButton.setText("NEW GAME");
            hitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*hit button will trigger start game event*/
                    standButton.setEnabled(true);
                    game.setStandButtonState(true);
                    //resetGameEvent();
                    //startGameEvent();
                    startGameEvent();
                }
            });

        }

        else if (purpose == PURPOSE_HIT) {
            /*make hit button a normal hit button*/
            hitButton.setText("HIT");
            hitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*hit button will trigger hit event*/
                    standButton.setEnabled(true);
                    game.setStandButtonState(true);
                    hitEvent();
                }
            });
        }

        else if (purpose == PURPOSE_NEXT_GAME) {
            hitButton.setText("PLAY AGAIN");
            hitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*hit button will trigger reset game event*/
                    standButton.setEnabled(true);
                    game.setStandButtonState(true);
                    resetGameEvent();

                }
            });
        }

        else {
            /*invalid purpose*/
            return;
        }
    }


    // saves the state of the variables used in the game
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        serializeObject(game);
        outState.putBoolean("HIT_BUTTON_ENABLED", hitButton.isEnabled());
        outState.putBoolean("STAND_BUTTON_ENABLED", standButton.isEnabled());
        outState.putInt("HIT_BUTTON_PURPOSE", hitButtonPurpose);
    }


    private void serializeObject(BlackJackGame o){
        try{
            //FileOutputStream outputStream = new FileOutputStream(SERIALIZABLE_OBJECTS);
            FileOutputStream outputStream = openFileOutput(SERIALIZABLE_OBJECTS, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(o);
            //objectOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BlackJackGame readSerializable(){
        BlackJackGame g = new BlackJackGame();
        try{
            FileInputStream inputStream = openFileInput(SERIALIZABLE_OBJECTS);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            g = (BlackJackGame) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            serializeObject(game);
            g = readSerializable();
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return g;
    }

    @Override
    protected void onPause() {
        serializeObject(game);
        super.onPause();
    }

    public void restoreGameState(){
        game = readSerializable();
        if (game == null){
            game = new BlackJackGame();
        }

        player = game.getPlayer(); /*grab reference to dealer for convenience*/
        dealer = game.getDealer(); /*grab reference to player1 for convenience*/
        updatePlayerBank();

        updateGUI(player);
        updateGUI(dealer);
    }

}