import java.util.*;

public class SimulateGame {
    private List<Cards.Card> deck;

    private Map<Integer, Cards.Card> removedMap;

    public SimulateGame(List<Cards.Card> deck){
        this.deck = deck;
        removedMap= new HashMap<>();
    }
    public void play() {
        Cards.Card[] pair1 = removeCards(2);
        Cards.Card[] pair2 = removeCards(2);
        burn();
        Cards.Card[] flop = removeCards(3);
        burn();
        Cards.Card[] turn = removeCards(1);
        burn();
        Cards.Card[] river = removeCards(1);

        System.out.printf("pair1: %s%n", Arrays.toString(pair1));
        System.out.printf("pair2: %s%n", Arrays.toString(pair2));

        System.out.printf("flop: %s%n", Arrays.toString(flop));
        System.out.printf("turn: %s%n", Arrays.toString(turn));
        System.out.printf("river: %s%n", Arrays.toString(river));
        System.out.println(Arrays.toString(merge5Cards(flop, turn, river)));


        reset();
    }
    /*
        Resets the game
    */
    public void reset(){
        for(Map.Entry<Integer, Cards.Card> idxAndCard : removedMap.entrySet()){
            deck.set(idxAndCard.getKey(), idxAndCard.getValue());
        }
        removedMap = new HashMap<>();
    }

    public Cards.Card[] merge5Cards(Cards.Card[]... rounds)
    {
        return Arrays.stream(rounds)
                .flatMap(Arrays::stream)
                .toArray(Cards.Card[]::new);
        //return rounds.stream(rounds)
              //  .flatMap(Arrays::stream)
              //  .toArray();
    }

    public void burn(){
        removeCards(1);
    }
/*
Remove cards from deck, setting the idx associated with card to null and then adds removed entry to map
 */
    public Cards.Card [] removeCards(int n){ //n represents num cards to remove
        Cards.Card [] cardsRemoved = new Cards.Card[n];
       // System.out.printf("Removing %d cards from deck%n", n);
        int cardCount = 0;
        int initialSize = removedMap.size();
        while(removedMap.size() < initialSize + n ){
            int cardIdxToRemove = getCardInRange();
            if(deck.get(cardIdxToRemove) != null){
                Cards.Card card = deck.get(cardIdxToRemove);
                removedMap.put(cardIdxToRemove, card);
                deck.set(cardIdxToRemove, null);
                cardsRemoved[cardCount++] = card;
                //System.out.printf("Removed %s from idx %d%n", card, cardIdxToRemove);
            }
        }
        //System.out.println(deck);
        return cardsRemoved;
    }

    public int getCardInRange(){
        return (int)(Math.random()*51);
    }



    class Rules{

    }

}

