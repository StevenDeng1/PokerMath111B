import java.util.List;
import java.util.Set;

public class Math111A {

    public static void main(String[] args) {
        Cards cards = new Cards();
        List<Cards.Card> deck = cards.deck;
        SimulateGame game = new SimulateGame(deck);
        game.play();

    }
}
