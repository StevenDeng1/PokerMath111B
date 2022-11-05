import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Math111A {

    public static void main(String[] args) {
        Cards cards = new Cards();
        List<Cards.Card> deck = cards.deck;
        SimulateGame game = new SimulateGame(deck);
        int totalSimulations = 1000000;
        for (int i = 0; i <= totalSimulations; i++) {
            game.play();
        }
        System.out.println(Rules.statCount/totalSimulations);
    }

/*
        Cards.Card card1 = new Cards.Card(Cards.CardValue.TWO, '\u2666');
        Cards.Card card2 = new Cards.Card(Cards.CardValue.THREE, '\u2666');
        Cards.Card card3 = new Cards.Card(Cards.CardValue.FIVE, '\u2666');
        Cards.Card card4 = new Cards.Card(Cards.CardValue.FOUR, '\u2666');
        Cards.Card card5 = new Cards.Card(Cards.CardValue.ACE, '\u2666');
        Cards.Card [] straightArr = new Cards.Card[] {card1,card2,card3, card4, card5};
        //System.out.println(Arrays.toString(straightArr));
        System.out.println(Rules.hasStraight(straightArr).getKey());
        System.out.println(Arrays.toString(Rules.hasStraight(straightArr).getValue()));

        Cards.Card card1a = new Cards.Card(Cards.CardValue.KING, '\u2666');
        Cards.Card card2a = new Cards.Card(Cards.CardValue.JACK, '\u2666');
        Cards.Card card3a = new Cards.Card(Cards.CardValue.QUEEN, '\u2666');
        Cards.Card card4a = new Cards.Card(Cards.CardValue.TEN, '\u2666');
        Cards.Card card5a = new Cards.Card(Cards.CardValue.ACE, '\u2666');


        Cards.Card [] straightArr2 = new Cards.Card[] {card1a,card2a,card3a, card4a, card5a};
        //System.out.println(Arrays.toString(straightArr));
        System.out.println(Rules.hasStraight(straightArr2).getKey());
        System.out.println(Arrays.toString(Rules.hasStraight(straightArr2).getValue()));
        //Rules.eval(null, null, null);
*/
}
