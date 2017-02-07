package csci4020.shawnbickel.assignment1;

import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;
import java.util.Vector;

/**
 * Created by Judson Thomas on 1/29/17.
 */

public class BlackJackGame {
    /*data members*/
    private final Player player1;
    private final Player dealer;
    private Deck deck;

    /*inner classes*/

    /*Card is defined as a static nested class because
    * it does not need access to the instance variables of BlackJackGame.
    * It's only use to serve as a namespace inside BlackJackGame to enclose the methods
    * and members associated with a Card.*/
    public static class Card{
        public enum Suit{MYSTERY, SPADES, CLUBS, HEARTS, DIAMONDS}

        /*here I define rank as the type of card (1, 2, queen, etc.); this might
        * not be the correct term*/
        public enum Rank{MYSTERY, ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, JACK, KING, QUEEN}

        protected Suit suit;
        protected Rank rank;
        protected int value;

        protected Card(Suit suit, Rank rank){
            this.suit = suit;
            this.rank = rank;

            switch(rank){
                case MYSTERY: value = 0; break;
                case ACE: value = 1; break; /*aces are initialized to 1 by default*/
                case TWO: value = 2; break;
                case THREE: value = 3; break;
                case FOUR: value = 4; break;
                case FIVE: value = 5; break;
                case SIX: value = 6; break;
                case SEVEN: value = 7; break;
                case EIGHT: value = 8; break;
                case NINE: value = 9; break;
                case JACK: value = 10; break;
                case KING: value = 10; break;
                case QUEEN: value = 10; break;
            }
        }

        /*value accessor*/
        public int getValue(){return value;}

        /*suit accessor*/
        public Suit getSuit(){return suit;}

        /*rank accessor*/
        public Rank getRank(){return rank;}
    }

    /*See above reason for being static*/
    public static class Ace extends Card{
        public enum AceValue{ONE, ELEVEN}

        private Ace(Suit suit, AceValue aceValue){
            super(suit, Rank.ACE);

            if(aceValue == AceValue.ELEVEN) {
                value = 11;
            }
        }

        public void setEleven(){value = 11;}
        public void setOne(){value = 1;}
    }

    /*See above reason for being static*/
    public static class Deck{
        /*CARDS is the list of card objects that exist for this BlackJackGame instance;
         these Card objects will then be put inside of our deck and randomly shuffled
        * to make our game deck.*/
        private static final Card MYSTERY_CARD = new Card(Card.Suit.MYSTERY, Card.Rank.MYSTERY);
        private final Card[] CARDS = new Card[] {
                new Card(Card.Suit.DIAMONDS, Card.Rank.ACE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.TWO),
                new Card(Card.Suit.DIAMONDS, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SIX),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN),
                new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT),
                new Card(Card.Suit.DIAMONDS, Card.Rank.NINE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.JACK),
                new Card(Card.Suit.DIAMONDS, Card.Rank.QUEEN),
                new Card(Card.Suit.DIAMONDS, Card.Rank.KING)};

        private Stack<Card> deck;

        private Deck(){
            deck = new Stack<Card>();
            deck.addAll(Arrays.asList(CARDS));
            Collections.shuffle(deck); /*randomly shuffles our deck*/
        }

        /*pops a card from the top of the stack; returns the card drawn
        * if the deck is not empty; otherwise, returns null.*/
        public Card popCard(){
            if(deck.empty()) {
                return null;
            }

            return deck.pop();
        }
    }

    /*Player is defined as an inner class instead of a static nested class
    * because it IS necessary for the Player class to be tied to an instance of
    * the BlackJackGame and have access to its instance members. Specifically, the Player
    * class needs to be tied to a given BlackJackGame instance to access the specific deck object
    * which it will draw from.*/
    public class Player{
        /*data members*/
        private Vector<Card> hand;
        private int bank;
        private int bet;
        private int score;
        private boolean isStanding;

        /*methods*/

        private Player(){
            bank = 50000;
            bet = 0;
            score = 0;
            isStanding = false;
            hand = new Vector<Card>();
        }

        /*causes the player to place a wager (bet) of amount "betAmount";
        * returns true if the wager was placed successfully; false otherwise*/
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

        /*causes the player to draw a card from the top of the deck;
        returns true if a card was drawn and added to the hand;
        otherwise, returns false (indicating no card was drawn, i.e. the deck is empty)*/
        public boolean drawCard(){
            Card drawnCard = deck.popCard();
            if(drawnCard == null){
                /*nothing was drawn because the deck is empty*/
                return false;
            }

            hand.add(drawnCard);
            score += drawnCard.getValue();
            return true;
        }

        /*causes the player to take a hit; drawing a new card*/
        public boolean hit(){
            if(isStanding){
                return false;
            }

            //give a card to player
            drawCard();
            return true;
        }

        /*causes the player to stand; conceding his turn*/
        public void stand(){
            isStanding = true;
        }

        /*returns whether or not the player has a BlackJack (a score of 21)
        * Note: This function returns true for any hand whose score is 21, not
        * just natural blackjack hands (i.e. 2 card black jack hands)*/
        public boolean hasBlackJack(){
            return score == 21;
        }

        /*returns whether or not the player has busted (score exceeded 21)*/
        public boolean isBusted(){
            return score > 21;
        }

        /*returns whether or not the player is standing*/
        public boolean isStanding(){
            return isStanding;
        }

        /*hand accessor*/
        public final Vector<Card> viewHand(){
            return hand;
        }

        /*score accessor*/
        public int getScore(){
            return score;
        }

        /*bank accessor*/
        public int getBank(){
            return bank;
        }

        /*bet accessor*/
        public int getBet(){
            return bet;
        }
    }

    /*methods*/

    public BlackJackGame(){
        player1 = new Player();

        /*a dealer is a player; however, the bank, bet
		members should be ignored*/
        dealer = new Player();

        deck = new Deck();
    }

    /*returns the player (player1)*/
    public final Player getPlayer(){
        return player1;
    }

    /*returns the dealer*/
    public final Player getDealer(){
        return dealer;
    }

    /*gifts the bet amount to player1's bank; used when
    * player1 wins*/
    public void giftBet(){
        player1.bank += player1.bet;
    }

    /*removes the bet amount from player1's bank; used when
    * player1 loses*/
    public void deductBet(){
        player1.bank -= player1.bet;
    }

    /*deals the first two cards to the Player specified by "player"*/
    public boolean deal(Player player){
        if(player.hand.size() != 0){
            return false;
        }

        //give first card to player
        player.drawCard();

        //give second card to player
        if(player == dealer){
            /*dealer's second card is a "mystery card";
            i.e. a card faced down
             */
            player.hand.add(Deck.MYSTERY_CARD);
        }

        else{
            player.drawCard();
        }

        return true;
    }

    /*reveal's the dealer's hole card (his second card)*/
    public boolean revealHole(){
        if(dealer.hand.elementAt(1) != Deck.MYSTERY_CARD){
            /*if the dealer's second card is not a mystery card
            * then we cannot reveal the hole card; either it has already
            * been revealed (i.e. it is not a mystery card anymore) or there
            * is no second card (i.e. the cards have not been dealt yet*/
            return false;
        }

        /*revealing the hole card is implemented by drawing a new card and replacing
        * the mystery card with the new card*/
        dealer.hand.removeElementAt(1);
        dealer.drawCard();
        return true;
    }

    /*resets the game: bets, hands, isStanding statuses, scores etc.
    * but does NOT reset the player's bank*/
    void reset(){
        //reset deck
        deck = new Deck();

        //reset player
        player1.bet = 0;
        player1.isStanding = false;
        player1.hand.clear();
        player1.score = 0;

        //reset dealer
        //dealer's bet does not need to be reset as it is ignored
        dealer.isStanding = false;
        dealer.hand.clear();
        dealer.score = 0;
    }
}