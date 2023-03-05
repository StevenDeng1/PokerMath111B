import javafx.util.Pair;

import java.util.*;

public class Math111A {

    public static Map<Pair<Player.PlayStyle, Player.PlayStyle>, Integer> averageWinningsInTotalSimulations;

    public static Player.PlayStyle [] playStyles = {
            Player.PlayStyle.Aggressive,
            Player.PlayStyle.Normal,
            Player.PlayStyle.Cautious,
            Player.PlayStyle.Rookie,
            Player.PlayStyle.Tempo
    };

    public static void main(String[] args) {
        Cards cards = new Cards();
        List<Cards.Card> deck = cards.deck;
        SimulateGame game = new SimulateGame(deck);
        Scanner inputScanner = new Scanner(System.in);
        System.out.print("Enter number of simulations: ");
        int totalSimulations = inputScanner.nextInt();
        System.out.print("Enter number of hands: ");
        int totalHands = inputScanner.nextInt();

        Map<Pair<Player.PlayStyle, Player.PlayStyle>, Integer> maxWinningsInTotalSimulations = new HashMap<>();
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Aggressive, Player.PlayStyle.Aggressive), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Aggressive, Player.PlayStyle.Normal), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Aggressive, Player.PlayStyle.Cautious), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Aggressive, Player.PlayStyle.Tempo), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Aggressive, Player.PlayStyle.Rookie), 0);

        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Normal, Player.PlayStyle.Aggressive), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Normal, Player.PlayStyle.Normal), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Normal, Player.PlayStyle.Cautious), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Normal, Player.PlayStyle.Tempo), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Normal, Player.PlayStyle.Rookie), 0);

        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Cautious, Player.PlayStyle.Aggressive), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Cautious, Player.PlayStyle.Normal), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Cautious, Player.PlayStyle.Cautious), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Cautious, Player.PlayStyle.Tempo), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Cautious, Player.PlayStyle.Rookie), 0);

        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Rookie, Player.PlayStyle.Aggressive), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Rookie, Player.PlayStyle.Normal), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Rookie, Player.PlayStyle.Cautious), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Rookie, Player.PlayStyle.Tempo), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Rookie, Player.PlayStyle.Rookie), 0);

        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Tempo, Player.PlayStyle.Aggressive), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Tempo, Player.PlayStyle.Normal), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Tempo, Player.PlayStyle.Cautious), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Tempo, Player.PlayStyle.Tempo), 0);
        maxWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Tempo, Player.PlayStyle.Rookie), 0);


        //TODO: add the other two new play styles


        Map<String, Pair<Integer, Integer>> statusMap = Map.of("Advantage", new Pair(2500, 500),
                "Disadvantage", new Pair(500, 2500), "Equal", new Pair(1500, 1500));

        long startTime = System.nanoTime();
        for (Map.Entry<String, Pair<Integer, Integer>> gameStatuses : statusMap.entrySet()) {
            resetAverageWinnings();
            System.out.println(gameStatuses.getKey());
            for (Player.PlayStyle ourPlayStyle : playStyles) {
                for (Player.PlayStyle enemyPlayStyle : playStyles) {
                    System.out.println(String.format("%s vs %s", ourPlayStyle, enemyPlayStyle));
                    for (int i = 0; i < totalSimulations; i++) { //10000 simulations of 100 hands
                        //System.out.println(gameStatuses.getValue().getKey());
                        //System.out.print(gameStatuses.getValue().getValue());
                        int amountWon = game.play(gameStatuses.getValue().getKey(), gameStatuses.getValue().getValue(),
                                totalHands, ourPlayStyle, enemyPlayStyle);
                        Pair<Player.PlayStyle, Player.PlayStyle> pairedPlayStyle = new Pair(ourPlayStyle, enemyPlayStyle);
                        game = new SimulateGame(deck);
                        maxWinningsInTotalSimulations.put(new Pair(ourPlayStyle, enemyPlayStyle),
                                Math.max(amountWon, maxWinningsInTotalSimulations.get(pairedPlayStyle)));
                        averageWinningsInTotalSimulations.put(new Pair(ourPlayStyle, enemyPlayStyle),
                                (averageWinningsInTotalSimulations.getOrDefault(new Pair(ourPlayStyle, enemyPlayStyle), 0) + amountWon)
                        );
                    }
                }
                //System.out.println(String.format("%s done", ourPlayStyle));
            }
            System.out.println();
            System.out.println(String.format("Stats for %s match situation", gameStatuses.getKey()));
/*
            System.out.println(String.format("Max winnings in %d simulations of %d hands:", totalSimulations, totalHands));
            for(Map.Entry<Pair<Player.PlayStyle, Player.PlayStyle>, Integer> playStyleIntegerEntry: maxWinningsInTotalSimulations.entrySet()){
                //System.out.println(playStyleIntegerEntry);
                System.out.println(String.format("%s vs. %s : \n %d highest win amount", playStyleIntegerEntry.getKey().getKey(),
                        playStyleIntegerEntry.getKey().getValue(), playStyleIntegerEntry.getValue()));
            }*/


            System.out.println();
            System.out.println(String.format("Average winnings in %d simulations of %d hands:", totalSimulations, totalHands));
            for(Map.Entry<Pair<Player.PlayStyle, Player.PlayStyle>, Integer> playStyleIntegerEntry: averageWinningsInTotalSimulations.entrySet()){
                System.out.println(String.format("%s vs. %s : \n %.2f%% from original", playStyleIntegerEntry.getKey().getKey(),
                        playStyleIntegerEntry.getKey().getValue(),
                        (double)playStyleIntegerEntry.getValue()/totalSimulations/gameStatuses.getValue().getKey()*100
                ));
            }
            System.out.println();
    }




        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000;  //divide by 1000000 to get milliseconds.
        long durationInSeconds = duration/1000;

        System.out.println();
        System.out.println(String.format("time it took %d seconds", durationInSeconds));


        /* // Calculates odds all acccurate
        System.out.printf("Royal Flush: %4f%n", Rules.rfCount/totalSimulations);
        System.out.printf("Straight Flush: %4f%n", Rules.sfCount/totalSimulations);
       System.out.printf("4 of a kind: %4f%n", Rules.quadCount/totalSimulations);
       System.out.printf("Full House: %4f%n", Rules.fhCount/totalSimulations);
       System.out.printf("Flush : %4f%n", Rules.flushCount/totalSimulations);
       System.out.printf("Straight: %4f%n", Rules.straightCount/totalSimulations);
       System.out.printf("3 of a Kind: %4f%n", Rules.threeKindCount/totalSimulations);
       System.out.printf("Two Pair: %4f%n", Rules.twoPairCount/totalSimulations);
       System.out.printf("Pair: %4f%n", Rules.pairCount/totalSimulations);
       */

        //System.out.println(Rules.rf/totalSimulations);
    }

    public static void resetAverageWinnings() {
        averageWinningsInTotalSimulations = new HashMap<>();
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Aggressive, Player.PlayStyle.Aggressive), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Aggressive, Player.PlayStyle.Normal), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Aggressive, Player.PlayStyle.Cautious), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Aggressive, Player.PlayStyle.Tempo), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Aggressive, Player.PlayStyle.Rookie), 0);

        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Normal, Player.PlayStyle.Aggressive), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Normal, Player.PlayStyle.Normal), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Normal, Player.PlayStyle.Cautious), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Normal, Player.PlayStyle.Tempo), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Normal, Player.PlayStyle.Rookie), 0);

        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Cautious, Player.PlayStyle.Aggressive), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Cautious, Player.PlayStyle.Normal), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Cautious, Player.PlayStyle.Cautious), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Cautious, Player.PlayStyle.Tempo), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Cautious, Player.PlayStyle.Rookie), 0);

        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Rookie, Player.PlayStyle.Aggressive), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Rookie, Player.PlayStyle.Normal), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Rookie, Player.PlayStyle.Cautious), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Rookie, Player.PlayStyle.Tempo), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Rookie, Player.PlayStyle.Rookie), 0);

        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Tempo, Player.PlayStyle.Aggressive), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Tempo, Player.PlayStyle.Normal), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Tempo, Player.PlayStyle.Cautious), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Tempo, Player.PlayStyle.Tempo), 0);
        averageWinningsInTotalSimulations.put(new Pair<>(Player.PlayStyle.Tempo, Player.PlayStyle.Rookie), 0);
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
