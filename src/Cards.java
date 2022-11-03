import java.sql.Array;
import java.util.*;

public class Cards {
    public List<Card> deck;

    private String[] values = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};
    private Character [] suits = {'\u2666','\u2663','\u2665','\u2660'}; //d,c,h,s

    public Cards(){
        deck = new ArrayList<Card>(52);
        for(String value: values){
            for(char suit : suits){
                deck.add(new Card(value, suit));
            }
        }
    }

    class Card{
        public Character suit;
        public String value;

        public Card(String value, Character suit){
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
            return value+suit;
        }
    }




}
