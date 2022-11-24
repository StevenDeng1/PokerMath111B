import javafx.util.Pair;

import java.util.*;

public class SimulateGame {
    private List<Cards.Card> deck;

    private Map<Integer, Cards.Card> removedMap;

    Player ourPlayer;

    Player enemyPlayer;

    int potSize;

    private static final Pair<Character, Integer> SMALL_BLIND_PAIR = new Pair<>('s', 1);
    private static final Pair<Character, Integer> BIG_BLIND_PAIR = new Pair<>('b', 2);

    int rotationNum;

    public enum GameStatus {
        Fold_Player_1,
        Fold_Player_2,
        Resume,
        Showdown;

        public static boolean isFold(GameStatus gameStatus){
            if(gameStatus.equals(Fold_Player_1) || gameStatus.equals(Fold_Player_2)){
                return true;
            }
            return false;
        }
    }

    public enum GameOutcome {
        Draw,
        Player_1_Win,
        Player_2_Win,
    }
    public SimulateGame(List<Cards.Card> deck){
        this.deck = deck;
        removedMap= new HashMap<>();
        ourPlayer = new Player(2500);
        enemyPlayer = new Player(500);
        potSize = 0;
        rotationNum = 0;
    }

    public int play(int numGames, Player.PlayStyle ourPlayStyle,
                     Player.PlayStyle enemyPlayStile) {
        ourPlayer.playStyle = ourPlayStyle;
        enemyPlayer.playStyle = enemyPlayStile;
        rotationNum = 0;


        for (int i = 0; i < numGames; i++) {
            //System.out.println(String.format("Game %d %s v. %s", i+1, ourPlayStyle, enemyPlayStile));

            if(rotationNum %2 == 0){
                //System.out.println(String.format("small blind: our player, big bLind: enemy player"));

            }
            if(rotationNum%2 == 1){
                //System.out.println(String.format("small blind: our player, big bLind: enemy player"));
            }

            //System.out.println();
            Cards.Card[] pair1 = removeCards(2);
            Cards.Card[] pair2 = removeCards(2);
            ourPlayer.setPair(pair1);
            enemyPlayer.setPair(pair2);

            printCards("ourPlayer", pair1);
            //System.out.println();
            printCards("enemyPlayer", pair2);
            //System.out.println();

            if(rotationNum%2==0){
                ourPlayer.chipAmount -= SMALL_BLIND_PAIR.getValue();
                ourPlayer.blind = SMALL_BLIND_PAIR.getKey();;

                enemyPlayer.chipAmount -= BIG_BLIND_PAIR.getValue();
                enemyPlayer.blind = BIG_BLIND_PAIR.getKey();

                potSize += SMALL_BLIND_PAIR.getValue()+ BIG_BLIND_PAIR.getValue();


                playRoundsInHand(ourPlayer, enemyPlayer, potSize);
                printChipStats(ourPlayer, enemyPlayer, potSize);
            }
            if(rotationNum%2==1){
                ourPlayer.chipAmount -= BIG_BLIND_PAIR.getValue();
                ourPlayer.blind = BIG_BLIND_PAIR.getKey();

                enemyPlayer.chipAmount -= SMALL_BLIND_PAIR.getValue();
                enemyPlayer.blind = SMALL_BLIND_PAIR.getKey();;

                potSize += SMALL_BLIND_PAIR.getValue()+ BIG_BLIND_PAIR.getValue();

                playRoundsInHand(enemyPlayer, ourPlayer, potSize);
                printChipStats(ourPlayer, enemyPlayer, potSize);
            }

            reset();
            //System.out.println();
        }
        return ourPlayer.chipAmount;
    }

    public Pair<GameOutcome, Integer>  playRoundsInHand(Player smallBlindPlayer, Player bigBlindPlayer, int potSize){
        int [] removeNumberOfCardsPerRound = {3,1,1};
        int roundPointer = 0;
        Cards.Card[] cardsOut = new Cards.Card[0];

        for(Rules.Round currRound : Rules.Round.values()){
            Pair<GameStatus, Integer> roundOutcome =
                    simulateRoundDecision(smallBlindPlayer, bigBlindPlayer, cardsOut, potSize, currRound);
            potSize+=roundOutcome.getValue();
            if(GameStatus.isFold(roundOutcome.getKey())){
                if(roundOutcome.getKey().equals(GameStatus.Fold_Player_1)){
                    setPlayerWin(bigBlindPlayer, GameOutcome.Player_2_Win, null);
                    return new Pair(bigBlindPlayer, potSize);
                }
                if(roundOutcome.getKey().equals(GameStatus.Fold_Player_2)){
                    setPlayerWin(smallBlindPlayer, GameOutcome.Player_1_Win, null);
                    return new Pair(smallBlindPlayer, potSize);
                }
            }
            if(roundOutcome.getKey().equals(GameStatus.Showdown)){ //uses eval to see who had better 5 card

                Pair<SimulateGame.GameOutcome, Pair<Pair<Rules.PokerHand, Cards.Card[]>, Pair<Rules.PokerHand, Cards.Card[]>>> evaluation =
                        Rules.eval(smallBlindPlayer.pair, bigBlindPlayer.pair, cardsOut);
                SimulateGame.GameOutcome evaluationOutcome = evaluation.getKey();
                Pair<Pair<Rules.PokerHand, Cards.Card[]>, Pair<Rules.PokerHand, Cards.Card[]>> winnerAndLoserHands = evaluation.getValue();

                if(evaluationOutcome.equals(GameOutcome.Draw)){
                    //setDraw(smallBlindPlayer, bigBlindPlayer, evaluationOutcome, winnerAndLoserHands);
                    return new Pair(setDraw(smallBlindPlayer, bigBlindPlayer, evaluationOutcome, winnerAndLoserHands), 0);
                }
                if(evaluationOutcome.equals(GameOutcome.Player_1_Win)) {
                    return new Pair(setPlayerWin(smallBlindPlayer, evaluationOutcome, winnerAndLoserHands), potSize);
                }
                if(evaluationOutcome.equals(GameOutcome.Player_2_Win)) {
                    return new Pair(setPlayerWin(bigBlindPlayer, evaluationOutcome, winnerAndLoserHands), potSize);
                }
                throw new java.lang.Error("Unaccounted game out come no tie or win");
            }
            if(!roundOutcome.getKey().equals(GameStatus.Showdown)) {
                burn();
                cardsOut = merge5Cards(cardsOut, removeCards(removeNumberOfCardsPerRound[roundPointer]));
                printCards("cardsOut", cardsOut);
                printCards("ourPlayer", ourPlayer.pair);
                printCards("enemyPlayer", enemyPlayer.pair);
                //System.out.println();
                roundPointer++;
            }
            if(rotationNum%2==0){
                printChipStats(smallBlindPlayer, bigBlindPlayer, this.potSize);
            }
            if(rotationNum%2==1){
                printChipStats(bigBlindPlayer, smallBlindPlayer, this.potSize);
            }

        }
        throw new java.lang.Error("Play Rounds in hand terminated without decision?");
    }

    private GameOutcome setDraw(Player player1, Player player2, GameOutcome outcome,
                                Pair<Pair<Rules.PokerHand, Cards.Card[]>, Pair<Rules.PokerHand, Cards.Card[]>> winAndLostHand){
        player1.chipAmount += potSize/2;
        player2.chipAmount += potSize/2;
        this.potSize = 0;
        //System.out.println(printGameOutcome(outcome, winAndLostHand));

        return outcome;
    }

    private GameOutcome setPlayerWin(Player winningPlayer, GameOutcome outcome,
                              Pair<Pair<Rules.PokerHand, Cards.Card[]>, Pair<Rules.PokerHand, Cards.Card[]>> winAndLostHand) {
        winningPlayer.chipAmount+=this.potSize; //chips are removed from players as games on thus no subtract from losing player needed
        this.potSize = 0;
        if(winAndLostHand == null){
            //System.out.println("Other player folded");
        } else {
            //System.out.println(printGameOutcome(outcome, winAndLostHand));
        }
        return outcome;
    }


    public Pair<GameStatus, Integer> simulateRoundDecision(Player smallBlindPlayer, Player bigBlindPlayer, Cards.Card[] cardsOut,
                                      int potSize, Rules.Round round){
        //System.out.println(round.toString());

        Player.PlayerMove firstPlayerMove = smallBlindPlayer.evalWithPlayStyle(null, cardsOut, potSize, round);
        if(this.rotationNum%2==0){
            printDecision(firstPlayerMove, true);
        }
        if(this.rotationNum%2==1){
            printDecision(firstPlayerMove, false);
        }
        this.potSize += firstPlayerMove.betAmount;
        smallBlindPlayer.chipAmount-=firstPlayerMove.betAmount;
        if(firstPlayerMove.move.equals(Player.PlayerMove.Move.Fold)){
            return new Pair(GameStatus.Fold_Player_1, potSize);
        }

        Player.PlayerMove secondPlayerMove = bigBlindPlayer.evalWithPlayStyle(firstPlayerMove, cardsOut, potSize, round);
        if(this.rotationNum%2==0){
            printDecision(secondPlayerMove, false);
        }
        if(this.rotationNum%2==1){
            printDecision(secondPlayerMove, true);
        }
        this.potSize += secondPlayerMove.betAmount;
        bigBlindPlayer.chipAmount-=secondPlayerMove.betAmount;
        if(secondPlayerMove.move.equals(Player.PlayerMove.Move.Fold)){
            return new Pair(GameStatus.Fold_Player_2, potSize);
        }

        if(round.equals(Rules.Round.River)){ //if river and neither player folded go to showdown
            return new Pair(GameStatus.Showdown, potSize);
        }
        return new Pair(GameStatus.Resume, potSize);
    }

    /*
        Resets the game
    */
    public void reset(){
        for(Map.Entry<Integer, Cards.Card> idxAndCard : removedMap.entrySet()){
            deck.set(idxAndCard.getKey(), idxAndCard.getValue());
        }
        removedMap = new HashMap<>();
        ourPlayer.pair = null;
        enemyPlayer.pair = null;
        rotationNum++;
    }

    public Cards.Card[] merge5Cards(Cards.Card[]... rounds)
    {
        return Arrays.stream(rounds)
                .flatMap(Arrays::stream)
                .toArray(Cards.Card[]::new);
    }

    public void burn(){
        removeCards(1);
    }
/*
Remove cards from deck, setting the idx associated with card to null and then adds removed entry to map
 */
    public Cards.Card [] removeCards(int n){ //n represents num cards to remove
        Cards.Card [] cardsRemoved = new Cards.Card[n];
       // //System.out.printf("Removing %d cards from deck%n", n);
        int cardCount = 0;
        int initialSize = removedMap.size();
        while(removedMap.size() < initialSize + n ){
            int cardIdxToRemove = getCardInRange();
            if(deck.get(cardIdxToRemove) != null){
                Cards.Card card = deck.get(cardIdxToRemove);
                removedMap.put(cardIdxToRemove, card);
                deck.set(cardIdxToRemove, null);
                cardsRemoved[cardCount++] = card;
                ////System.out.printf("Removed %s from idx %d%n", card, cardIdxToRemove);
            }
        }
        ////System.out.println(deck);
        return cardsRemoved;
    }

    public int getCardInRange(){
        return (int)(Math.random()*52);
    }

    public static void printChipStats(Player ourPlayer, Player enemyPlayer, int potSize){
        //System.out.println("~~~~~~~");
        //System.out.printf("our Player: %d%n", ourPlayer.chipAmount);
        //System.out.printf("enemy Player: %d%n", enemyPlayer.chipAmount);
        //System.out.printf("potSize: %d%n", potSize);
        //System.out.println("~~~~~~~");
    }

    public static void printDecision(Player.PlayerMove move, boolean isOurPlayer){
        if(isOurPlayer){
            //System.out.printf("our player: %s %d%n", move.move, move.betAmount);
        } else{
            //System.out.printf("enemy player: %s %d%n", move.move, move.betAmount);
        }

    }

    public static void printCards(String belongTo, Cards.Card[] cards){
        //System.out.printf("%s: %s\t", belongTo, Arrays.toString(cards));
    }

    public String printGameOutcome(GameOutcome outcome,
                                   Pair<Pair<Rules.PokerHand, Cards.Card[]>, Pair<Rules.PokerHand, Cards.Card[]>> winAndLostHand){

        Pair<Rules.PokerHand, Cards.Card[]> hand1 = winAndLostHand.getKey();
        Pair<Rules.PokerHand, Cards.Card[]> hand2 = winAndLostHand.getValue();
        switch (outcome) {
            case Player_1_Win -> {
                return String.format("--------\nPlayer 1 won\n" +
                                "%s: %s\n" +
                                "against\n" +
                                "%s: %s\n--------\n" , hand1.getKey().toString(),
                        Arrays.toString(hand1.getValue()),
                        hand2.getKey().toString(),
                        Arrays.toString(hand2.getValue()));
            }
            case Player_2_Win -> {
                return String.format("--------\nPlayer 2 won\n" +
                                "%s: %s\n" +
                                "against\n" +
                                "%s: %s\n--------\n" , hand2.getKey().toString(),
                        Arrays.toString(hand2.getValue()),
                        hand1.getKey().toString(),
                        Arrays.toString(hand1.getValue()));
            }
            case Draw -> {
                return String.format("--------\nDraw\n" +
                                "%s: %s\n" +
                                "against\n" +
                                "%s: %s\n--------\n" , hand2.getKey().toString(),
                        Arrays.toString(hand2.getValue()),
                        hand1.getKey().toString(),
                        Arrays.toString(hand1.getValue()));
            }
        }
        throw new java.lang.Error("Error in printing game outcome");
    }

}

