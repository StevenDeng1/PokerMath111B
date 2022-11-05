
import javafx.util.Pair;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class Rules {

    public static double statCount = 0;

    public enum PokerHand{
        High_Card,
        Pair,
        Two_Pair,
        Three_Of_A_Kind,
        Straight,
        Flush,
        Four_Of_A_Kind,
        Straight_Flush,
        Royal_Flush,
    }
    public Rules(){

    }
    public static List<String> eval(Cards.Card[] pair1, Cards.Card[] pair2, Cards.Card[] fiveCards ){
        List<String> results = new ArrayList<>();
        getHighestHandRank(pair1, fiveCards);
        return results;
    }
    public Cards.Card[] sameHandEval(String handType, Cards.Card[] pair1, Cards.Card[] pair2, Cards.Card[] fiveCards){
        return null;
    }
    /**
     * gets the highest hand possible of a pair and fiveCard as well as the hand
     *
     * @param  pair represents user pair fiveCards represents the fiveCards in hand
     * @return     List of string representing greatest hand produced by pair and fivecard
     * pair[0]: handType, pair[1]: highest rank
     */
    public static List<String> getHighestHandRank(Cards.Card[] pair, Cards.Card[] fiveCards){
        //System.out.println(PokerHand.Flush.compareTo(PokerHand.High_Card));
        //pair.
        Pair<Boolean, Cards.Card[]> results = null;//delete after

        Cards.Card[] hand = ArrayUtils.addAll(pair, fiveCards);
        Arrays.sort(hand, Comparator.comparing(card -> card.value));
        Pair<Boolean, Cards.Card[]> hasRoyalFlushResults = hasRoyalFlush(hand);
        //Pair<Boolean, Cards.Card[]> hasStraightFlushResults = hasStraightFlush(hand);
        //Pair<Boolean, Cards.Card[]> fourOfAKindResults = hasFourOfAKind(hand);
        //Pair<Boolean, Cards.Card[]> straightResults = hasStraight(hand);
        //Pair<Boolean, Cards.Card[]> flushResults = hasFlush(hand);
        //Pair<Boolean, Cards.Card[]> twoPairResults = hasTwoPair(hand);
        //Pair<Boolean, Cards.Card[]> pairResults = hasPair(hand);
        //Pair<Boolean, Cards.Card[]> threeOfAKindResults = hasThreeOfAKind(hand);

        results = hasRoyalFlushResults;
        if(results.getKey()) {
            System.out.println(Arrays.toString(results.getValue()));
            statCount++;
        }
        /*
        if(flushResults.getKey()) {
            System.out.println(Arrays.toString(hand));
            System.out.println(Arrays.toString(flushResults.getValue()));
            flushCount++;
        }*/
        return null;
    }
    public boolean checkHands(){
        return false;
    }
    public static Pair<Boolean, Cards.Card[]> hasRoyalFlush(Cards.Card[] hand){
        Set<Integer> royalFlushValues = new HashSet<>();
        Pair<Boolean, Cards.Card[]> possibleRoyal = hasStraightFlush(hand);
        if(possibleRoyal.getKey()){
            for(Cards.Card card: possibleRoyal.getValue()){
                if(card.value.getCardValue() >= 10 && card.value.getCardValue() <= 14){
                    royalFlushValues.add(card.value.getCardValue());
                }
                else {
                    return new Pair<>(false, null);
                }
            }
        }
        if(royalFlushValues.size()==5){
            return possibleRoyal;
        }
        return new Pair<>(false, null);
    }
    public static Pair<Boolean, Cards.Card[]> hasStraightFlush(Cards.Card[] hand){
        Map<Character, List<Cards.Card>> suitMap = new HashMap<>();
        //
        for(int i=hand.length-1; i>=0; i--) {
            Cards.Card currCard = hand[i];
            suitMap.computeIfAbsent(currCard.suit, k -> new ArrayList<>())
                    .add(currCard);
        }
        for(List<Cards.Card> cardsWithSuit : suitMap.values()){
            if(cardsWithSuit.size() >= 5) { //checks if there is at least 5 elements
                cardsWithSuit.sort(Comparator.comparing(card -> card.value));
                return hasStraight(cardsWithSuit.toArray(Cards.Card[]::new));
            }
        }
        return new Pair<>(false, null);
    }
    public static Pair<Boolean, Cards.Card[]> hasFourOfAKind(Cards.Card[] hand){
        for(int i=hand.length-4; i>=0; i--) {
            Cards.Card leftCard = hand[i];
            Cards.Card lMidCard = hand[i+1];
            Cards.Card rMidCard = hand[i+2];
            Cards.Card rightCard = hand[i+3];
            if(leftCard.value.getCardValue() == lMidCard.value.getCardValue() &&
                    lMidCard.value.getCardValue() == rightCard.value.getCardValue() &&
                    rMidCard.value.getCardValue() == rightCard.value.getCardValue()) {
                return new Pair<>(true, new Cards.Card[]{leftCard, lMidCard, rMidCard, rightCard});
            }
        }
        return new Pair<>(false, null);
    }
    public static Pair<Boolean, Cards.Card[]> hasFlush(Cards.Card[] hand){
        Map<Character, List<Cards.Card>> suitMap = new HashMap<>();
        //
        for(int i=hand.length-1; i>=0; i--) {
            Cards.Card currCard = hand[i];
            suitMap.computeIfAbsent(currCard.suit, k -> new ArrayList<>())
                    .add(currCard);
            if(suitMap.get(currCard.suit).size() == 5){
                return new Pair<>(true, suitMap.get(currCard.suit).toArray(Cards.Card[]::new));
            }
        }
        return new Pair<>(false, null);
    }
    public static Pair<Boolean, Cards.Card[]> hasStraight(Cards.Card[] hand){
        List<Cards.Card> potentialStraight = new ArrayList<Cards.Card>();
        potentialStraight.add(hand[0]);
        int potentialStraightPointer = 1;

        Map<Integer, Cards.Card> AceTwoStraight = new HashMap<>(); //used to deal with case of A,2,3,4,5 lowest straight
        //goes in reverse as to get the highest straight
        for(int i=hand.length-1; i>=0; i--){
            Cards.Card currCard = hand[i];
            if(potentialStraight.size()==5){
                return new Pair<Boolean, Cards.Card[]>(true,
                        potentialStraight.toArray(Cards.Card[]::new));
            }
            //special cond for A,2,3,4,5 straight
            if(currCard.value.getCardValue() == 14 ||
                    (currCard.value.getCardValue() <= 5 && currCard.value.getCardValue()>=2)){
                AceTwoStraight.put(currCard.value.getCardValue(), currCard);
            }
            //skips duplicate cards
            if(currCard.value.getCardValue() ==
                    potentialStraight.get(potentialStraightPointer-1).value.getCardValue()){
                continue;
            }
            //checks if currentCard is sequential with last card
            if(currCard.value.getCardValue() + 1 ==
                    potentialStraight.get(potentialStraightPointer-1)
                            .value.getCardValue()){
                potentialStraight.add(currCard);
                potentialStraightPointer++;
            } else {
                potentialStraight = new ArrayList<>();
                potentialStraightPointer = 1;
                potentialStraight.add(currCard);
            }

        }
        if(potentialStraight.size()==5){
            return new Pair<Boolean, Cards.Card[]>(true,
                    potentialStraight.toArray(Cards.Card[]::new));
        }
        if(AceTwoStraight.size()==5){ //ace two straight is complete
            return new Pair<Boolean, Cards.Card[]>(true,
                    AceTwoStraight.values().toArray(Cards.Card[]::new));
        }
        return new Pair<>(false, null);
    }
    public static Pair<Boolean, Cards.Card[]> hasThreeOfAKind(Cards.Card[] hand) {
        for(int i=hand.length-3; i>=0; i--) {
            Cards.Card leftCard = hand[i];
            Cards.Card midCard = hand[i+1];
            Cards.Card rightCard = hand[i+2];
            if(leftCard.value.getCardValue() == midCard.value.getCardValue() &&
                    midCard.value.getCardValue() == rightCard.value.getCardValue()) {
                return new Pair<>(true, new Cards.Card[]{leftCard, midCard, rightCard});
            }
        }
        return new Pair<>(false, null);
    }
    public static Pair<Boolean, Cards.Card[]> hasTwoPair(Cards.Card[] hand){
        int numPairs = 0;
        int firstLargestPair = -1;
        Map<Integer, List<Cards.Card>> rankCardMap = new HashMap<>();
        for(int i=hand.length-1; i>=0; i--) {
            Cards.Card currCard = hand[i];
            rankCardMap.computeIfAbsent(currCard.value.getCardValue(), k-> new ArrayList<>())
                    .add(currCard);
            if(rankCardMap.get(currCard.value.getCardValue()).size()==2) {
                numPairs++;
                if(numPairs == 1){
                    firstLargestPair = currCard.value.getCardValue();
                }
                if(numPairs == 2){
                    List<Cards.Card> twoPair = rankCardMap.get(currCard.value.getCardValue());
                    twoPair.addAll(rankCardMap.get(firstLargestPair));
                    if(twoPair.size()>4){//happens in the full house case but ignore case handled upstream
                        return new Pair<>(false, null);
                    }
                    return new Pair<>(true, twoPair.toArray(Cards.Card[]::new));
                }
            }

        }
        return new Pair<>(false, null);
    }
    public static Pair<Boolean, Cards.Card[]> hasPair(Cards.Card[] hand){
        Cards.Card previousCheckedCard = hand[hand.length-1];
        for(int i=hand.length-2; i>=0; i--) {
            Cards.Card currCard = hand[i];
            if(previousCheckedCard.value.getCardValue() == currCard.value.getCardValue()){
                return new Pair<>(true, new Cards.Card[]{previousCheckedCard, currCard});
            }
            previousCheckedCard = currCard;
        }

        return new Pair<>(false, null);
    }
}
