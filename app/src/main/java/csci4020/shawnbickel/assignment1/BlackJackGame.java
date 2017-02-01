package csci4020.shawnbickel.assignment1;

import java.util.Vector;

/**
 * Created by sbickel20 on 1/30/17.
 */

public class BlackJackGame {
    /*data members*/
    private Player player1;
    private Player dealer;

    /*inner classes*/
    public class Player{
        /*data members*/
        private Vector<Card> hand;
        private int bank;
        private int bet;
        private int score;
        private boolean isStanding;

        /*methods*/
        private Player(){
            bank = 0;
            bet = 0;
            score = 0;
            isStanding = false;
            hand = new Vector<Card>();
        }

        public boolean wager(int betAmount){
            if(betAmount > bank /*wagering what you do not owe*/
                    || betAmount == 0 /*betting nothing*/
                    || betAmount < 0 /*betting negative*/
                    || bet != 0 /*already wagered*/){
                return false;
            }

            bet = betAmount;
            return true;
        }

        public boolean hit(){
            if(isStanding){
                return false;
            }

            //give a card to player
            int cardID = randomGen.nextInt;
            hand.add(cardList[cardID]);
            return true;
        }

        public void stand(){
            isStanding = true;
        }

        public boolean hasBlackJack(){
            return score == 21;
        }

        public boolean isBusted(){
            return score > 21;
        }

        public boolean isStanding(){
            return isStanding;
        }

        public final Vector<Card> viewHand(){
            return hand;
        }

        public int getScore(){
            return score;
        }

        public int getBank(){
            return bank;
        }

        public int getBet(){
            return bet;
        }
    }

    /*methods*/
    public BlackJackGame(){
        player1 = new Player();
        dealer = new Player(); /*a dealer is a player; however, the bank, bet
		members should be ignored*/
    }

    public final Player getPlayer(){
        return player1;
    }

    public final Player getDealer(){
        return dealer;
    }

    public void giftBet(){
        player1.bank += player1.bet;
    }

    public void deductBet(){
        player1.bank -= player1.bet;
    }

    public boolean deal(Player player){
        if(player.hand.size() != 0){
            return false;
        }

        //give first card to player
        int cardID = randomGen.nextInt;
        hand.add(cardList[cardID]);

        //give second card to player
        if(player == dealer){
            hand.add(mystery);
        }

        else{
            cardID = randomGen.nextInt;
            hand.add(cardList[cardID]);
        }

        return true;
    }

    public boolean revealHole(){
        if(dealer.hand[1] != mystery){
            return false;
        }

        int cardID = randomGen.nextInt;
        dealer.hand[1] = cardList[cardID];
        return true;
    }

    //resets the game board; does not reset players' bank
    void reset(){
        randomGen = new random etc. etc.

                //reset player
                player1.bet = 0;
        player1.isStanding = false;
        player1.hand = null;

        //reset dealer
        dealer.hand = null;
    }

}
