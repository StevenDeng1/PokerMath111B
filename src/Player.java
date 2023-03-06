import javafx.util.Pair;

import java.util.*;

public class Player {
    public enum CurrHandEnum {
        fiveCard,
        possibleFiveCard,
        pairDoubleOrThreeCard,
        nothing
    }

    int initialAmount;
    int chipAmount;
    PlayStyle playStyle;
    Cards.Card[] pair;
    char blind;
    int bigBlind = 2;
    int smallBlind = bigBlind/2;

    Queue<PlayStyle> predictedPreviousPlayStyle = new ArrayDeque<>(5);
    int opponentChipAmount = 0;

    List<PlayerMove> previousMoves = new ArrayList<>(5);

    public Player(int chipAmount, PlayStyle playStyle){
        this.chipAmount = chipAmount;
        this.initialAmount = chipAmount;
        this.playStyle = playStyle;
    }

    public Player(int chipAmount){
        this.chipAmount = chipAmount;
        this.initialAmount = chipAmount;
    }

    public void setPair(Cards.Card[] pair) {
        this.pair = pair;
    }
    public void setBlind(char blind) {
        this.blind = blind;
    }

    public PlayerMove check(){
        return new PlayerMove(PlayerMove.Move.Check, 0);
    }
    public PlayerMove call(int callAmount){
        if(callAmount > chipAmount){
            return new PlayerMove(PlayerMove.Move.Call, chipAmount);
            //throw new java.lang.Error(String.format("ERROR, calling more than # chips"));
        }
        return new PlayerMove(PlayerMove.Move.Call, callAmount);
    }
    public PlayerMove raise(int raiseAmount){
        if(raiseAmount > chipAmount){
            return new PlayerMove(PlayerMove.Move.Call, chipAmount);
            //throw new java.lang.Error(String.format("ERROR, raising more than # chips"));
        }
        //chipAmount -= raiseAmount;
        return new PlayerMove(PlayerMove.Move.Raise, raiseAmount);
    }
    public PlayerMove fold(){
        return new PlayerMove(PlayerMove.Move.Fold, 0);
    }

    public PlayerMove evaluate (PlayerMove previousMove, Cards.Card[] cardsOut, int potSize){
        return null;
    }

    public PlayerMove evalWithPlayStyle(PlayerMove previousMove, Cards.Card[] cardsOut, int potSize, Rules.Round round){
        switch (playStyle) {
            case Aggressive -> {
                if(round == Rules.Round.Pre_Flop){
                    if(previousMove == null){return call(smallBlind);}
                    if(previousMove.move.equals(PlayerMove.Move.Call)){return check();}
                }
                if(round != Rules.Round.Pre_Flop){
                    Pair<CurrHandEnum, Pair<Rules.PokerHand, Cards.Card[]>> evaluation = evalPairWithCardsOut(cardsOut);;
                    if(previousMove == null){
                        return betBasedOnPlayStyleFirstToGoAfterPreFlop(PlayStyle.Aggressive, evaluation, round, potSize, cardsOut);
                    }
                    if(previousMove != null){
                        return call(previousMove.betAmount);
                    }
                }
            }
            case Normal -> {
                if(round == Rules.Round.Pre_Flop){
                    if(previousMove == null){
                        if(inBestPreFlop(pair)) {
                            return call(smallBlind);
                        } else {
                            return fold();
                        }
                    }
                    if(previousMove.move.equals(PlayerMove.Move.Call))
                    {
                        return check();
                    }
                }
                if(round != Rules.Round.Pre_Flop) {
                    Pair<CurrHandEnum, Pair<Rules.PokerHand, Cards.Card[]>> evaluation = evalPairWithCardsOut(cardsOut);
                    if (previousMove == null) { //sb
                        return betBasedOnPlayStyleFirstToGoAfterPreFlop(PlayStyle.Normal, evaluation, round, potSize, cardsOut);
                    } if (previousMove != null) {
                        if(previousMove.move.equals(PlayerMove.Move.Check)){
                            return check();
                        }
                        if(previousMove.move.equals(PlayerMove.Move.Call)){
                            return check();
                        }
                        if(previousMove.move.equals(PlayerMove.Move.Raise)){
                            if(evaluation.getKey().compareTo(CurrHandEnum.nothing) > 0) {
                                return call(previousMove.betAmount);
                            } else{
                                return fold();
                            }
                        }

                    }
                }
            }
            case Cautious -> {
                if(round == Rules.Round.Pre_Flop){
                    if(previousMove==null){
                        if(inBestCautiousPreFlop(pair)){
                            return call(smallBlind);
                        } else{
                            return fold();
                        }
                    }
                    if(previousMove.move.equals(PlayerMove.Move.Call)){return check();}
                }

                if(round != Rules.Round.Pre_Flop) {
                    Pair<CurrHandEnum, Pair<Rules.PokerHand, Cards.Card[]>> evaluation = evalPairWithCardsOut(cardsOut);
                    if (previousMove == null) { //sb
                        return betBasedOnPlayStyleFirstToGoAfterPreFlop(PlayStyle.Cautious, evaluation, round, potSize, cardsOut);
                    } if (previousMove != null) {
                        if(previousMove.move.equals(PlayerMove.Move.Check)){
                            return check();
                        }
                        if(previousMove.move.equals(PlayerMove.Move.Call)){
                            return check();
                        }
                        if(previousMove.move.equals(PlayerMove.Move.Raise)){
                            if(evaluation.getKey().compareTo(CurrHandEnum.fiveCard) >= 0) {
                                return call(previousMove.betAmount);
                            } else if(!evaluation.getKey().equals(CurrHandEnum.possibleFiveCard) &&
                                    evaluation.getValue().getKey().compareTo(Rules.PokerHand.Two_Pair) >= 0){
                                return call(previousMove.betAmount);
                            } else{
                                return fold();
                            }
                        }
                    }
                }

            }
            case Tempo -> {
                PlayStyle playStyle = predictOpponentPlayStyle(previousMoves, potSize);
                if(playStyle.equals(PlayStyle.Aggressive)){
                    return evalWithPlayStyleMethod(previousMove, cardsOut, potSize, round, PlayStyle.Aggressive);
                }
                if(playStyle.equals(PlayStyle.Cautious)){
                    return evalWithPlayStyleMethod(previousMove, cardsOut, potSize, round, PlayStyle.Aggressive);
                }
                if(playStyle.equals(PlayStyle.Normal)){
                    return evalWithPlayStyleMethod(previousMove, cardsOut, potSize, round, PlayStyle.Normal);
                }

            }
            case Rookie -> {
                if(round == Rules.Round.Pre_Flop){
                    if(previousMove == null) {
                        if(inBestPreFlop(pair)) {
                            return call(smallBlind);
                        } else {
                            return fold();
                        }
                    }
                    if(previousMove != null) {
                        PlayStyle playStyle = predictOpponentPlayStyle(previousMoves, potSize);
                        if(playStyle.equals(PlayStyle.Aggressive)){
                            return evalWithPlayStyleMethod(previousMove, cardsOut, potSize, round, PlayStyle.Cautious);
                        }
                        if(playStyle.equals(PlayStyle.Cautious)){
                            return evalWithPlayStyleMethod(previousMove, cardsOut, potSize, round, PlayStyle.Aggressive);
                        }
                        if(playStyle.equals(PlayStyle.Normal)){
                            return evalWithPlayStyleMethod(previousMove, cardsOut, potSize, round, PlayStyle.Normal);
                        }
                    }
                }
                if(round != Rules.Round.Pre_Flop) {
                    if(previousMove == null) {
                        PlayStyle playStyle = predictOpponentPlayStyle(previousMoves, potSize);
                        if(playStyle.equals(PlayStyle.Aggressive)){
                            return evalWithPlayStyleMethod(previousMove, cardsOut, potSize, round, PlayStyle.Cautious);
                        }
                        if(playStyle.equals(PlayStyle.Cautious)){
                            return evalWithPlayStyleMethod(previousMove, cardsOut, potSize, round, PlayStyle.Aggressive);
                        }
                        if(playStyle.equals(PlayStyle.Normal)){
                            return evalWithPlayStyleMethod(previousMove, cardsOut, potSize, round, PlayStyle.Normal);
                        }
                    }
                    if(previousMove != null) {
                        switch (previousMove.move) {
                            case Check -> {
                                if(getOpponentPlayStyle().equals(PlayStyle.Cautious)){
                                  return evalWithPlayStyleMethod(previousMove, cardsOut, potSize, round, PlayStyle.Aggressive);
                                } else {
                                    return evalWithPlayStyleMethod(previousMove, cardsOut, potSize, round, PlayStyle.Normal);
                                }
                            }
                            case Call -> {
                                if(getOpponentPlayStyle().equals(PlayStyle.Cautious)){
                                    return evalWithPlayStyleMethod(previousMove, cardsOut, potSize, round, PlayStyle.Aggressive);
                                } else {
                                    return evalWithPlayStyleMethod(previousMove, cardsOut, potSize, round, PlayStyle.Normal);
                                }
                            }
                            case Raise -> {
                                if(getOpponentPlayStyle().equals(PlayStyle.Cautious)) {
                                    return evalWithPlayStyleMethod(previousMove, cardsOut, potSize, round, PlayStyle.Aggressive);
                                } else {
                                    return evalWithPlayStyleMethod(previousMove, cardsOut, potSize, round, PlayStyle.Normal);
                                }
                            }
                        }
                    }
                }
            }
        }
        throw new java.lang.Error("Out of Bounds move in player decision");
    }

    public PlayerMove evalWithPlayStyleMethod(PlayerMove previousMove, Cards.Card[] cardsOut, int potSize, Rules.Round round,
                                                 PlayStyle userPlayStyle) {
        switch (userPlayStyle) {
            case Aggressive -> {
                if (round == Rules.Round.Pre_Flop) {
                    if (previousMove == null) {
                        return call(smallBlind);
                    }
                    if (previousMove.move.equals(PlayerMove.Move.Call)) {
                        return check();
                    }
                }
                if (round != Rules.Round.Pre_Flop) {
                    Pair<CurrHandEnum, Pair<Rules.PokerHand, Cards.Card[]>> evaluation = evalPairWithCardsOut(cardsOut);
                    ;
                    if (previousMove == null) {
                        return betBasedOnPlayStyleFirstToGoAfterPreFlop(PlayStyle.Aggressive, evaluation, round, potSize, cardsOut);
                    }
                    if (previousMove != null) {
                        return call(previousMove.betAmount);
                    }
                }
            }
            case Normal -> {
                if (round == Rules.Round.Pre_Flop) {
                    if (previousMove == null) {
                        if (inBestPreFlop(pair)) {
                            return call(smallBlind);
                        } else {
                            return fold();
                        }
                    }
                    if (previousMove.move.equals(PlayerMove.Move.Call)) {
                        return check();
                    }
                }
                if (round != Rules.Round.Pre_Flop) {
                    Pair<CurrHandEnum, Pair<Rules.PokerHand, Cards.Card[]>> evaluation = evalPairWithCardsOut(cardsOut);
                    if (previousMove == null) { //sb
                        return betBasedOnPlayStyleFirstToGoAfterPreFlop(PlayStyle.Normal, evaluation, round, potSize, cardsOut);
                    }
                    if (previousMove != null) {
                        if (previousMove.move.equals(PlayerMove.Move.Check)) {
                            return check();
                        }
                        if (previousMove.move.equals(PlayerMove.Move.Call)) {
                            return check();
                        }
                        if (previousMove.move.equals(PlayerMove.Move.Raise)) {
                            if (evaluation.getKey().compareTo(CurrHandEnum.nothing) > 0) {
                                return call(previousMove.betAmount);
                            } else {
                                return fold();
                            }
                        }

                    }
                }
            }
            case Cautious -> {
                if (round == Rules.Round.Pre_Flop) {
                    if (previousMove == null) {
                        if (inBestCautiousPreFlop(pair)) {
                            return call(smallBlind);
                        } else {
                            return fold();
                        }
                    }
                    if (previousMove.move.equals(PlayerMove.Move.Call)) {
                        return check();
                    }
                }

                if (round != Rules.Round.Pre_Flop) {
                    Pair<CurrHandEnum, Pair<Rules.PokerHand, Cards.Card[]>> evaluation = evalPairWithCardsOut(cardsOut);
                    if (previousMove == null) { //sb
                        return betBasedOnPlayStyleFirstToGoAfterPreFlop(PlayStyle.Cautious, evaluation, round, potSize, cardsOut);
                    }
                    if (previousMove != null) {
                        if (previousMove.move.equals(PlayerMove.Move.Check)) {
                            return check();
                        }
                        if (previousMove.move.equals(PlayerMove.Move.Call)) {
                            return check();
                        }
                        if (previousMove.move.equals(PlayerMove.Move.Raise)) {
                            if (evaluation.getKey().compareTo(CurrHandEnum.fiveCard) >= 0) {
                                return call(previousMove.betAmount);
                            } else if (!evaluation.getKey().equals(CurrHandEnum.possibleFiveCard) &&
                                    evaluation.getValue().getKey().compareTo(Rules.PokerHand.Two_Pair) >= 0) {
                                return call(previousMove.betAmount);
                            } else {
                                return fold();
                            }
                        }
                    }
                }

            }
        }
        throw new Error("evalWithPlayStyleMethod broke");
    }

    private boolean inBestCautiousPreFlop(Cards.Card[] pair) {
        if((pair[0].value.getCardValue() == 13 || pair[1].value.getCardValue() == 13) &&
                (pair[0].suit == pair[1].suit)){
            return true;
        }
        if(pair[0].value.getCardValue() == pair[1].value.getCardValue()){
            return true;
        }
        return false;
    }

    private boolean inBestPreFlop(Cards.Card[] pair) {
        StringBuilder sb1 = new StringBuilder();
        if(pair[0].value.getCardValue() > pair[1].value.getCardValue()){
            sb1.append(pair[0].value.getSymbol());
            sb1.append(pair[1].value.getSymbol());
        }
        if(pair[1].value.getCardValue() > pair[0].value.getCardValue()){
            sb1.append(pair[1].value.getSymbol());
            sb1.append(pair[0].value.getSymbol());
        }

        Pair<String, Boolean> pairAndSameSuit =
                new Pair<>(sb1.toString(), pair[0].suit == pair[1].suit);

        return Rules.bestPreFlop.contains(pairAndSameSuit);
    }

    public PlayerMove betBasedOnPlayStyleFirstToGoAfterPreFlop(PlayStyle playStyle,
                                                                      Pair<CurrHandEnum, Pair<Rules.PokerHand, Cards.Card[]>> eval,
                                                                      Rules.Round round, int potSize, Cards.Card[] cardsOut) {
        CurrHandEnum currHandClass = eval.getKey();
        Pair<Rules.PokerHand, Cards.Card[]> bestHand = eval.getValue();
        switch (playStyle) {
            case Aggressive -> {
                switch (currHandClass) {
                    case fiveCard -> {
                        return raise((int) Math.ceil((double) round.getRoundNumber() / 4 * potSize));
                    }
                    case possibleFiveCard -> {
                        return raise((int) Math.ceil( 1.5* bigBlind * potSize * getClosestHandProb(bestHand)));
                    }
                    case pairDoubleOrThreeCard -> {
                        return raise((int) Math.ceil((double) bigBlind /  2));
                    }
                    case nothing -> {
                        return raise(bigBlind);
                    }
                }
            }
            case Normal -> {
                switch (currHandClass) {
                    case fiveCard -> {
                        return raise((int) (round.getRoundNumber() / 4.0 * potSize));
                    }
                    case possibleFiveCard -> {
                        return raise((int) (bigBlind * Math.ceil(potSize * getClosestHandProb(bestHand))));
                    }
                    case pairDoubleOrThreeCard -> {
                        return raise(bigBlind * Rules.getPairRelativeRanking(pair, cardsOut));
                    }
                    case nothing -> {
                        return check();
                    }
                }
            }
            case Cautious -> {
                switch (currHandClass) {
                    case fiveCard -> {
                        return raise((int) (round.getRoundNumber() / 4.0 * potSize));
                    }
                    case possibleFiveCard -> {
                        return raise((int) (.6*bigBlind * Math.ceil(potSize * getClosestHandProb(bestHand))));
                    }
                    case pairDoubleOrThreeCard -> {
                        return raise(bigBlind * Rules.getPairRelativeRanking(pair, cardsOut));
                    }
                    case nothing -> {
                        return check();
                    }
                }
            }
        }
        return null;
    }
    public PlayerMove reraise(PlayerMove previousMove, Cards.Card[] cardsOut, int potSize, Rules.Round round,
                              PlayStyle userPlayStyle) {
        return evalWithPlayStyleMethod(previousMove, cardsOut, potSize, round, userPlayStyle);
    }
    public PlayerMove bluff(PlayerMove previousMove, Cards.Card[] cardsOut, int potSize, Rules.Round round,
                              PlayStyle userPlayStyle) {
        if((int)(Math.random()*52) > 40 ){
            return evalWithPlayStyleMethod(previousMove, cardsOut, potSize, round, userPlayStyle);
        }
            return null;
    }

    private double getClosestHandProb(Pair<Rules.PokerHand,Cards.Card[]> bestHand) {
        if(bestHand.getKey().equals(Rules.PokerHand.Straight)){
            return (1.0/13);
        }
        if(bestHand.getKey().equals(Rules.PokerHand.Flush)){
            return 9.0/52;
        }
        if(bestHand.getKey().equals(Rules.PokerHand.Full_House)){
            return 1.0/26;
        }
        return 1.0/52;
    }

    public Pair<CurrHandEnum, Pair<Rules.PokerHand, Cards.Card[]>> evalPairWithCardsOut(Cards.Card[] cardsOut){
        Pair<Rules.PokerHand, Cards.Card[]> currHighestHand = Rules.getHighestHandRank(pair, cardsOut);
        if(currHighestHand.getKey().compareTo(Rules.PokerHand.Straight) >= 0){
            return new Pair<>(CurrHandEnum.fiveCard, currHighestHand);
        } else if(Rules.oneCardAwayFromFiveCard(pair, cardsOut)){
           //System.out.println(String.format("One card away from 5 card with pair:{%s},\n" +
                          //"cards out: {%s}", Arrays.toString(pair), Arrays.toString(cardsOut)));
            return new Pair<>(CurrHandEnum.possibleFiveCard, currHighestHand);
        } else if (currHighestHand.getKey().compareTo(Rules.PokerHand.High_Card) > 0){
            return new Pair<>(CurrHandEnum.pairDoubleOrThreeCard, currHighestHand);
        }
        return new Pair<>(CurrHandEnum.nothing,currHighestHand);
    }

    public PlayStyle predictOpponentPlayStyle(List<PlayerMove> previousBets, int potSize){
        int raiseCount = 0;
        int checkCount = 0;
        int callCount = 0;
        int foldCount = 0;
        int averageRaiseAmount = 0;
        for(PlayerMove bet: previousBets){
            if(bet.move.equals(PlayerMove.Move.Raise)){
                raiseCount++;
                averageRaiseAmount = (averageRaiseAmount*(raiseCount-1) + bet.betAmount)/raiseCount;
            }
            if(bet.move.equals(PlayerMove.Move.Check)){
                checkCount++;
            }
            if(bet.move.equals(PlayerMove.Move.Call)){
                callCount++;
            }
            if(bet.move.equals(PlayerMove.Move.Fold)){
                foldCount++;
            }
        }
        if(foldCount > callCount || foldCount > checkCount || foldCount > raiseCount){
            return PlayStyle.Cautious;
        }
        if(raiseCount > checkCount && raiseCount > callCount){
            if(averageRaiseAmount >= (int) bigBlind * Math.ceil(potSize /13)){
                return PlayStyle.Aggressive;
            }
        }
        return PlayStyle.Normal;
    }

    public PlayStyle getOpponentPlayStyle(){
        Map<PlayStyle, Integer> playStyleCount = new HashMap<>();
        Pair<PlayStyle, Integer> maxPlayAndCount = new Pair<>(null,0);
        for (PlayStyle predictedPreviousPlayStyle : predictedPreviousPlayStyle){
            int count = playStyleCount.getOrDefault(predictedPreviousPlayStyle,0)+1;
            playStyleCount.put(predictedPreviousPlayStyle, count);
            if(Math.max(count, maxPlayAndCount.getValue()) == count){
                maxPlayAndCount = new Pair<>(predictedPreviousPlayStyle, count);
            }
        }
        return maxPlayAndCount.getKey();
    }

    public void addPlayStyleGuess(PlayStyle playStyleGuess){
        if(predictedPreviousPlayStyle.size()==5){
            predictedPreviousPlayStyle.remove();
        }
        predictedPreviousPlayStyle.add(playStyleGuess);
    }

    public enum PlayStyle{
        Aggressive,
        Normal,
        Cautious,
        Tempo,
        Rookie
    }

    class PlayerMove{
        public enum Move{
            Check,
            Call,
            Raise,
            Fold,
        }
        int betAmount;
        Move move;

        public PlayerMove(Move move, int betAmount){
            this.betAmount = betAmount;
            this.move = move;
        }
    }

}
