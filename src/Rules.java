
import javafx.util.Pair;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Rules {

    public static double rfCount = 0;
    public static double sfCount = 0;
    public static double quadCount = 0;
    public static double fhCount = 0;
    public static double flushCount = 0;
    public static double straightCount = 0;
    public static double threeKindCount = 0;
    public static double twoPairCount = 0;
    public static double pairCount = 0;

    public enum PokerHand{
        High_Card,
        Pair,
        Two_Pair,
        Three_Of_A_Kind,
        Straight,
        Flush,
        Full_House,
        Four_Of_A_Kind,
        Straight_Flush,
        Royal_Flush,
    }

    public enum Round{
        Pre_Flop(1),
        Flop(2),
        Turn(3),
        River(4);
        int roundNumber;
        Round(int i) {
            roundNumber = i;
        }
        public int getRoundNumber() {
            return roundNumber;
        }
    }

    public static Set<Pair<String,Boolean>> bestPreFlop = fillBestPreFlopMatrix();
    public Rules(){

    }

    public static Pair<SimulateGame.GameOutcome, Pair<Pair<PokerHand, Cards.Card[]>, Pair<PokerHand, Cards.Card[]>>> eval(Cards.Card[] pair1, Cards.Card[] pair2, Cards.Card[] fiveCards ){

        Pair<PokerHand, Cards.Card[]> hand1 = getHighestHandRank(pair1, fiveCards);
        Pair<PokerHand, Cards.Card[]> hand2= getHighestHandRank(pair2, fiveCards);
        if(hand1 == null){
            hand1 = getHighCard(pair1);
        }
        if(hand2 == null){
            hand2 = getHighCard(pair2);
        }
        if(hand1.getKey().compareTo(hand2.getKey()) == 0){
            Pair<String, Pair<PokerHand, Cards.Card[]>> sameHandResult =
                    sameHandEval(hand1.getKey(), hand1.getValue(), hand2.getValue());
            if(sameHandResult.getKey().equals("hand1")){
                return new Pair(SimulateGame.GameOutcome.Player_1_Win, new Pair(hand1, hand2));
                /*
                return String.format("Player 1 won\n" +
                                "%s: %s\n" +
                                "against\n" +
                                "%s: %s" , hand1.getKey().toString(),
                        Arrays.toString(hand1.getValue()),
                        hand2.getKey().toString(),
                        Arrays.toString(hand2.getValue()));*/
            }
            if(sameHandResult.getKey().equals("hand2")){
                return new Pair(SimulateGame.GameOutcome.Player_2_Win, new Pair(hand2, hand1));
                /*
                return String.format("Player 2 won\n" +
                                "%s: %s\n" +
                                "against\n" +
                                "%s: %s" , hand2.getKey().toString(),
                        Arrays.toString(hand2.getValue()),
                        hand1.getKey().toString(),
                        Arrays.toString(hand1.getValue()));*/
            }
            if(sameHandResult.getKey().equals("draw")){
                return new Pair(SimulateGame.GameOutcome.Draw, new Pair(hand1, hand2));
                /*
                return String.format("Draw\n" +
                                "%s: %s\n" +
                                "against\n" +
                                "%s: %s" , hand2.getKey().toString(),
                        Arrays.toString(hand2.getValue()),
                        hand1.getKey().toString(),
                        Arrays.toString(hand1.getValue()));*/
            }
            if(sameHandResult.getKey().equals("kickerDecide")){
                String kickerResult = kickerDecider(pair1, pair2);
                if(kickerResult.equals("pair1")){
                    return new Pair(SimulateGame.GameOutcome.Player_1_Win, new Pair(hand1, hand2));
                    /*
                    return String.format("Player 1 won\n" +
                                    "%s: %s\n" +
                                    "against\n" +
                                    "%s: %s" , hand1.getKey().toString(),
                            Arrays.toString(hand1.getValue()),
                            hand2.getKey().toString(),
                            Arrays.toString(hand2.getValue()));*/
                }
                if(kickerResult.equals("pair2")){
                    return new Pair(SimulateGame.GameOutcome.Player_2_Win, new Pair(hand2, hand1));
                    /*
                    return String.format("Player 2 won\n" +
                                    "%s: %s\n" +
                                    "against\n" +
                                    "%s: %s" , hand2.getKey().toString(),
                            Arrays.toString(hand2.getValue()),
                            hand1.getKey().toString(),
                            Arrays.toString(hand1.getValue()));*/
                }
                if(kickerResult.equals("draw")){
                    return new Pair(SimulateGame.GameOutcome.Draw, new Pair(hand1, hand2));
                    /*
                    return String.format("Draw\n" +
                                    "%s: %s\n" +
                                    "against\n" +
                                    "%s: %s" , hand2.getKey().toString(),
                            Arrays.toString(hand2.getValue()),
                            hand1.getKey().toString(),
                            Arrays.toString(hand1.getValue()));*/
                }
            }
        }
        if(hand1.getKey().compareTo(hand2.getKey()) > 0){
            return new Pair(SimulateGame.GameOutcome.Player_1_Win, new Pair(hand1, hand2));
            /*
            return String.format("Player 1 won\n" +
                            "%s: %s\n" +
                            "against\n" +
                            "%s: %s" , hand1.getKey().toString(),
                    Arrays.toString(hand1.getValue()),
                    hand2.getKey().toString(),
                    Arrays.toString(hand2.getValue()));*/
        }
        if(hand1.getKey().compareTo(hand2.getKey()) < 0){
            return new Pair(SimulateGame.GameOutcome.Player_2_Win, new Pair(hand2, hand1));
            /*
            return String.format("Player 2 won\n" +
                            "%s: %s\n" +
                            "against\n" +
                            "%s: %s" , hand2.getKey().toString(),
                    Arrays.toString(hand2.getValue()),
                    hand1.getKey().toString(),
                    Arrays.toString(hand1.getValue()));*/
        }
        return null;
    }
    public static Pair<String, Pair<PokerHand, Cards.Card[]>> sameHandEval(PokerHand handType,
                                                                           Cards.Card[] hand1, Cards.Card[] hand2){
        Pair<String, Pair<PokerHand, Cards.Card[]>> hand1Winner = new Pair<>("hand1", new Pair<>(handType, hand1));
        Pair<String, Pair<PokerHand, Cards.Card[]>> hand2Winner = new Pair<>("hand2", new Pair<>(handType, hand2));
        Pair<String, Pair<PokerHand, Cards.Card[]>> drawWinner = new Pair<>("draw", new Pair<>(handType, hand1));
        Pair<String, Pair<PokerHand, Cards.Card[]>> kickerWinner = new Pair<>("kickerDecide", new Pair<>(handType, hand1));

        Pair<String, Pair<PokerHand, Cards.Card[]>> winner = null;
        if(handType.equals(PokerHand.Royal_Flush)){
            winner = drawWinner;
        }
        if(handType.equals(PokerHand.Straight_Flush)){
            int highestRankHand1Value = 0;
            for(Cards.Card card : hand1){
                highestRankHand1Value = Math.max(card.value.getCardValue(), highestRankHand1Value);
            }
            int highestRankHand2Value = 0;
            for(Cards.Card card : hand2){
                highestRankHand2Value = Math.max(card.value.getCardValue(), highestRankHand2Value);
            }
            if(highestRankHand1Value > highestRankHand2Value){
                winner = hand1Winner;
            } if(highestRankHand1Value < highestRankHand2Value){
                winner = hand2Winner;
            } if(highestRankHand1Value == highestRankHand2Value){
                winner = kickerWinner;
            }
        }
        if(handType.equals(PokerHand.Four_Of_A_Kind)){
            if(hand1[0].value.getCardValue() > hand2[0].value.getCardValue()){
                winner = hand1Winner;
            } if((hand1[0].value.getCardValue() < hand2[0].value.getCardValue())){
                winner = hand2Winner;
            }
            if((hand1[0].value.getCardValue() == hand2[0].value.getCardValue())){
                winner = kickerWinner;
            }
        }
        if(handType.equals(PokerHand.Full_House)){
            int hand1TripleValue = 0;
            int hand1DoubleValue = 0;
            Map<Integer, Integer> rankCount = new HashMap<>();
            for(int i=0; i<hand1.length-3; i++){ //process the 3 and 2
                rankCount.put(hand1[i].value.getCardValue(),
                        rankCount.getOrDefault(hand1[i].value.getCardValue(),0)+1);
            }
            for(Map.Entry<Integer,Integer> cardRankCount : rankCount.entrySet()){
                if(cardRankCount.getValue() == 3){
                    hand1TripleValue = cardRankCount.getKey();
                }
                if(cardRankCount.getValue() == 2){
                    hand1DoubleValue = cardRankCount.getKey();
                }
            }
            int hand2TripleValue = 0;
            int hand2DoubleValue = 0;
            rankCount = new HashMap<>();
            for(int i=0; i<hand1.length-3; i++){ //process the 3 and 2
                rankCount.put(hand1[i].value.getCardValue(),
                        rankCount.getOrDefault(hand1[i].value.getCardValue(),0)+1);
            }
            for(Map.Entry<Integer,Integer> cardRankCount : rankCount.entrySet()){
                if(cardRankCount.getValue() == 3){
                    hand2TripleValue = cardRankCount.getKey();
                }
                if(cardRankCount.getValue() == 2){
                    hand2DoubleValue = cardRankCount.getKey();
                }
            }
            if(hand1TripleValue > hand2TripleValue){
                winner = hand1Winner;
            } if(hand1TripleValue < hand2TripleValue){
                winner = hand2Winner;
            } if(hand1TripleValue == hand2TripleValue){
                if(hand1DoubleValue > hand2DoubleValue){
                    winner = hand1Winner;
                }
                if(hand1DoubleValue < hand2DoubleValue){
                    winner = hand2Winner;
                }
                if(hand1DoubleValue == hand2DoubleValue) {
                    winner = kickerWinner;
                }
            }

        }
        if(handType.equals(PokerHand.Flush)){
            int highestRankHand1Value = 0;
            for(Cards.Card card : hand1){
                highestRankHand1Value = Math.max(card.value.getCardValue(), highestRankHand1Value);
            }
            int highestRankHand2Value = 0;
            for(Cards.Card card : hand2){
                highestRankHand2Value = Math.max(card.value.getCardValue(), highestRankHand2Value);
            }
            if(highestRankHand1Value > highestRankHand2Value){
                winner = hand1Winner;
            } if(highestRankHand1Value < highestRankHand2Value){
                winner = hand2Winner;
            } if(highestRankHand1Value == highestRankHand2Value){
                winner = kickerWinner;
            }
        }
        if(handType.equals(PokerHand.Straight)){
            int highestRankHand1Value = 0;
            for(Cards.Card card : hand1){
                highestRankHand1Value = Math.max(card.value.getCardValue(), highestRankHand1Value);
            }
            int highestRankHand2Value = 0;
            for(Cards.Card card : hand2){
                highestRankHand2Value = Math.max(card.value.getCardValue(), highestRankHand2Value);
            }
            if(highestRankHand1Value > highestRankHand2Value){
                winner = hand1Winner;
            } if(highestRankHand1Value < highestRankHand2Value){
                winner = hand2Winner;
            } if(highestRankHand1Value == highestRankHand2Value){
                winner = kickerWinner;
            }
        }
        if(handType.equals(PokerHand.Three_Of_A_Kind)){
            if(hand1[0].value.getCardValue() > hand2[0].value.getCardValue()){
                winner = hand1Winner;
            } if((hand1[0].value.getCardValue() < hand2[0].value.getCardValue())){
                winner = hand2Winner;
            } if((hand1[0].value.getCardValue() == hand2[0].value.getCardValue())){
                winner = drawWinner;
            }
        }
        if(handType.equals(PokerHand.Two_Pair)){
            int highestRankHand1Value = 0;
            for(Cards.Card card : hand1){
                highestRankHand1Value = Math.max(card.value.getCardValue(), highestRankHand1Value);
            }
            int highestRankHand2Value = 0;
            for(Cards.Card card : hand2){
                highestRankHand2Value = Math.max(card.value.getCardValue(), highestRankHand2Value);
            }
            if(highestRankHand1Value > highestRankHand2Value){
                winner = hand1Winner;
            } if(highestRankHand1Value < highestRankHand2Value){
                winner = hand2Winner;
            } if(highestRankHand1Value == highestRankHand2Value){
                winner = kickerWinner;
            }
        }
        if(handType.equals(PokerHand.Pair)){
            if(hand1[0].value.getCardValue() > hand2[0].value.getCardValue()){
                winner = hand1Winner;
            } if((hand1[0].value.getCardValue() < hand2[0].value.getCardValue())){
                winner = hand2Winner;
            }
            if((hand1[0].value.getCardValue() == hand2[0].value.getCardValue())){
                winner = kickerWinner;
            }
        }
        if(handType.equals(PokerHand.High_Card)){
            if(hand1[0].value.getCardValue() > hand2[0].value.getCardValue()){
                winner = hand1Winner;
            } else if((hand1[0].value.getCardValue() < hand2[0].value.getCardValue())){
                winner = hand2Winner;
            } else if(hand1[1].value.getCardValue() > hand2[1].value.getCardValue()){
                winner = hand1Winner;
            } else if(hand1[1].value.getCardValue() < hand2[1].value.getCardValue()){
                winner = hand2Winner;
            } else {
                winner = kickerWinner;
            }
        }
        return winner;
    }
    /**
     * gets the highest hand possible of a pair and fiveCard as well as the hand
     *
     * @param  pair represents user pair fiveCards represents the fiveCards in hand
     * @return     List of string representing greatest hand produced by pair and fivecard
     * pair[0]: handType, pair[1]: highest rank
     */
    public static Pair<PokerHand, Cards.Card[]> getHighestHandRank(Cards.Card[] pair, Cards.Card[] fiveCards){
        Cards.Card[] hand = ArrayUtils.addAll(pair, fiveCards);
        Arrays.sort(hand, Comparator.comparing(card -> card.value));

        Pair<Boolean, Cards.Card[]> bestHand = null;
        bestHand = hasRoyalFlush(hand);
        if(bestHand.getKey()){
            rfCount++;
            return new Pair<>(PokerHand.Royal_Flush, bestHand.getValue());
        }
        bestHand = hasStraightFlush(hand);
        if(bestHand.getKey()){
            sfCount++;
            return new Pair<>(PokerHand.Straight_Flush, bestHand.getValue());
        }
        bestHand = hasFourOfAKind(hand);
        if(bestHand.getKey()){
            quadCount++;
            return new Pair<>(PokerHand.Four_Of_A_Kind, bestHand.getValue());
        }
        bestHand = hasFullHouse(hand);
        if(bestHand.getKey()){
            fhCount++;
            return new Pair<>(PokerHand.Full_House, bestHand.getValue());
        }
        bestHand = hasFlush(hand);
        if(bestHand.getKey()){
            flushCount++;
            return new Pair<>(PokerHand.Flush, bestHand.getValue());
        }
        bestHand = hasStraight(hand);
        if(bestHand.getKey()){
            straightCount++;
            return new Pair<>(PokerHand.Straight, bestHand.getValue());
        }
        bestHand = hasThreeOfAKind(hand);
        if(bestHand.getKey()){
            threeKindCount++;
            return new Pair<>(PokerHand.Three_Of_A_Kind, bestHand.getValue());
        }
        bestHand = hasTwoPair(hand);
        if(bestHand.getKey()){
            twoPairCount++;
            return new Pair<>(PokerHand.Two_Pair, bestHand.getValue());
        }
        bestHand = hasPair(hand);
        if(bestHand.getKey()){
            pairCount++;
            return new Pair<>(PokerHand.Pair, bestHand.getValue());
        }
        return new Pair(PokerHand.High_Card, getHighCard(pair).getValue());
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
    public static Pair<Boolean, Cards.Card[]> hasFullHouse(Cards.Card[] hand){
        List<List<Cards.Card>> cardsByRank = new ArrayList<>(12);
        for(int i=0; i<14; i++){
            cardsByRank.add(new ArrayList<>());
        }
        for(int i=hand.length-1; i>=0; i--) {
            Cards.Card currCard = hand[i];
            cardsByRank.get(currCard.value.getCardValue()-2).add(currCard);
        }

        List<Cards.Card> highestTriplet = null;
        List<Cards.Card> highestPair = null;
        for(int i=13; i>=0; i--){
            if(cardsByRank.get(i).size() == 2 || (highestTriplet != null && cardsByRank.get(i).size() == 3)){
                highestPair = cardsByRank.get(i).subList(0,2);
            }
            if(cardsByRank.get(i).size() == 3){
                highestTriplet = cardsByRank.get(i);
            }
        }
        if(highestTriplet != null && highestPair != null){
            highestTriplet.addAll(highestPair);
            return new Pair<>(true, highestTriplet.toArray(Cards.Card[]::new));
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
    public static Pair<PokerHand, Cards.Card[]> getHighCard(Cards.Card[] pair){
        Cards.Card highCard = pair[1].value.getCardValue() > pair[0].value.getCardValue()
                ? pair[1]: pair[0];
        Cards.Card lowCard = pair[1].value.getCardValue() < pair[0].value.getCardValue()
                ? pair[1]: pair[0];
        return new Pair<>(PokerHand.High_Card, new Cards.Card[]{highCard, lowCard});
    }
    public static String kickerDecider(Cards.Card[] pair1, Cards.Card[] pair2){
        pair1 = getHighCard(pair1).getValue();
        pair2 = getHighCard(pair2).getValue();
        if(pair1[0].value.getCardValue() > pair2[0].value.getCardValue()){
            return "pair1";
        }
        if(pair2[0].value.getCardValue() > pair1[0].value.getCardValue()){
            return "pair2";
        }
        if(pair1[1].value.getCardValue() > pair2[1].value.getCardValue()){
            return "pair1";
        }
        if(pair2[1].value.getCardValue() > pair1[1].value.getCardValue()){
            return "pair2";
        }
        return "draw";
    }

//can modify code to return after any 5 card rather than looking for highest 5 card
    public static boolean oneCardAwayFromFiveCard(Cards.Card[] pair, Cards.Card[] hand){
        Set<Cards.Card> inHand = new HashSet<>();
        for(Cards.Card card : hand){
            inHand.add(card);
        }
        PokerHand closestHighestHandRank = PokerHand.High_Card;
        Cards.Card [] addCardToHand = new Cards.Card[hand.length+1];
        System.arraycopy(hand, 0, addCardToHand, 0, hand.length);
        for(Cards.Card card  : new Cards().deck){
            if(!inHand.contains(card)){
                addCardToHand[hand.length] = card;
                PokerHand currHighestCardRank = getHighestHandRank(pair, addCardToHand).getKey();
                closestHighestHandRank = closestHighestHandRank.compareTo(currHighestCardRank) > 0 ?
                        closestHighestHandRank : currHighestCardRank;
                }
            }
        if(closestHighestHandRank.compareTo(PokerHand.Straight) >=0){
            return true;
        }
        return false;
    }
    public static int getPairRelativeRanking(Cards.Card[] pair, Cards.Card[] cardsOut){
        int card1 = pair[0].value.getCardValue();
        int card2 = pair[0].value.getCardValue();
        int card1Pos = -1;
        int card2Pos = -1;
        Cards.Card[] hand = ArrayUtils.addAll(pair, cardsOut);
        Arrays.sort(hand, Comparator.comparing(card -> card.value));
        for(int i=0; i<hand.length; i++){
            Cards.Card card = hand[i];
            if(card1Pos != -1 && card2Pos != -1){
                break;
            }
            if(card1 == card.value.getCardValue()){
                card1Pos = i;
            }
            if(card2 == card.value.getCardValue()){
                card2Pos = i;
            }
        }
        return hand.length-Math.max(card1Pos,card2Pos);

    }
    public static Set<Pair<String,Boolean>> fillBestPreFlopMatrix(){
        Set<Pair<String,Boolean>> bestPreFlopMatrix = new HashSet<>();
        bestPreFlopMatrix.add(new Pair<>("AA", false));
        bestPreFlopMatrix.add(new Pair<>("AK", true));
        bestPreFlopMatrix.add(new Pair<>("AQ", true));
        bestPreFlopMatrix.add(new Pair<>("AJ", true));
        bestPreFlopMatrix.add(new Pair<>("A10", true));
        bestPreFlopMatrix.add(new Pair<>("A9", true));
        bestPreFlopMatrix.add(new Pair<>("A8", true));
        bestPreFlopMatrix.add(new Pair<>("A7", true));
        bestPreFlopMatrix.add(new Pair<>("A6", true));
        bestPreFlopMatrix.add(new Pair<>("A5", true));
        bestPreFlopMatrix.add(new Pair<>("A4", true));
        bestPreFlopMatrix.add(new Pair<>("A3", true));
        bestPreFlopMatrix.add(new Pair<>("A2", true));
        bestPreFlopMatrix.add(new Pair<>("AK", false));
        bestPreFlopMatrix.add(new Pair<>("KK", false));
        bestPreFlopMatrix.add(new Pair<>("KQ", true));
        bestPreFlopMatrix.add(new Pair<>("KJ", true));
        bestPreFlopMatrix.add(new Pair<>("K10", true));
        bestPreFlopMatrix.add(new Pair<>("K9", true));
        bestPreFlopMatrix.add(new Pair<>("AQ", true));
        bestPreFlopMatrix.add(new Pair<>("KQ", false));
        bestPreFlopMatrix.add(new Pair<>("QQ", false));
        bestPreFlopMatrix.add(new Pair<>("QJ", true));
        bestPreFlopMatrix.add(new Pair<>("Q10", true));
        bestPreFlopMatrix.add(new Pair<>("Q9", true));
        bestPreFlopMatrix.add(new Pair<>("AJ", false));
        bestPreFlopMatrix.add(new Pair<>("JJ", false));
        bestPreFlopMatrix.add(new Pair<>("J10", true));
        bestPreFlopMatrix.add(new Pair<>("A10", false));
        bestPreFlopMatrix.add(new Pair<>("1010", false));
        bestPreFlopMatrix.add(new Pair<>("109", true));
        bestPreFlopMatrix.add(new Pair<>("99", false));
        bestPreFlopMatrix.add(new Pair<>("98", true));
        bestPreFlopMatrix.add(new Pair<>("88", false));
        bestPreFlopMatrix.add(new Pair<>("77", false));
        bestPreFlopMatrix.add(new Pair<>("66", false));
        bestPreFlopMatrix.add(new Pair<>("55", false));
        bestPreFlopMatrix.add(new Pair<>("44", false));
        bestPreFlopMatrix.add(new Pair<>("33", false));
        bestPreFlopMatrix.add(new Pair<>("22", false));
        return bestPreFlopMatrix;
    }
}
