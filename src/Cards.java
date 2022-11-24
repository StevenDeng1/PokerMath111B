import java.sql.Array;
import java.util.*;

public class Cards {

    public enum CardValue{
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        JACK(11),
        QUEEN(12),
        KING(13),
        ACE(14);
        private int cardValue ;
        private String symbol;

        CardValue(int cardValue){
            this.cardValue = cardValue;
            this.symbol = switch(cardValue) {
                case 11:
                    yield "J";
                case 12:
                    yield "Q";
                case 13:
                    yield "K";
                case 14:
                    yield "A";
                default:
                    yield Integer.toString(cardValue);
            };
        }
        public int getCardValue(){
            return cardValue;
        }
        public String getSymbol(){
            return symbol;
        }
    }
    public List<Card> deck;

    //private String[] values = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};
    public static final Character [] suits = {'\u2666','\u2663','\u2665','\u2660'}; //d,c,h,s

    public Cards(){
        deck = new ArrayList<Card>(52);
        for(CardValue value : CardValue.values()){
            for(char suit : suits){
                deck.add(new Card(value, suit));
            }
        }
        /*
        for(String value: values){
            for(char suit : suits){
                deck.add(new Card(value, suit));
            }
        }*/
    }

    static class Card{
        public Character suit;
        public CardValue value;

        public Card(CardValue value, Character suit){
            this.suit = suit;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Card card = (Card) o;
            return suit.equals(card.suit) && value.equals(card.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(suit, value);
        }

        @Override
        public String toString() {
            return value.getSymbol()+suit;
        }
    }




}
