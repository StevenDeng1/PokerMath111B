# Optimizing Single Hand Poker Strategy

The following code is for Math 111A. The project attempts to create a model 
to optimize winnings over three poker strategies in a head-to-head game of Texas HoldEm. 

The following describes the three poker strategies.

## Aggressive:
In our aggressive play-style strategy we will use the following rules to dictate how one plays. Preflop our player will play with any cards. At flop/turn/river we will play based on the following rules:
* If a 5 card hand (straight, flush, etc) is present raise:
round/4 ∗ pot size (1)
* If possibility of 5 card in next turn raise
(1.5 ∗ big blind ∗ ceil(pot size * odds of needed card within rounds left)) (2)
* If player has a pair, 3 card, or 2 pair
big blind ∗ [1/(# of pairs ≥ pair)] (3)
* If player has nothing raise big blind amount


## Normal:
In our normal play-style strategy we will play only cards within the matrix
in the blue at pre-flop. At flop/turn/river we will play based on the following rules:
* If a 5 card hand (straight, flush, etc) is present raise:
round/4 ∗ pot size (4)
* If possibility of 5 card in next turn raise
(big blind ∗ ceil(pot size * odds of needed card within rounds left)) (5)
* If player has a pair, 3 card, or 2 pair (if the pair is high relative to the cards out otherwise check)
big blind ∗ [1/(# of pairs ≥ pair)] (6)
* If player has nothing check
* If other player raises, player will fold if they have nothing and call only if they are close to a 5 card have a 5 card or have a high pair/triplet/2 pair.


## Cautious:
Our player will only play with pairs of cards and cards with an Ace and other
card with the same suit. At flop/turn/river we will play based on the following rules:
* If a 5 card hand (straight, flush, etc) is present raise:
round/4 ∗ pot size (7)
* If possibility of 5 card in next turn raise
(.8 ∗ big blind ∗ ceil(pot size * odds of needed card within rounds left)) (8)
* If player has a pair, 3 card, or 2 pair (if the pair is high relative to the cards out otherwise check)
big blind ∗ [1/(# of pairs ≥ pair)] (9)
* If player has nothing check
* If other player raises, player will fold unless they have a 2 pair 3 card or 5 card.

The code is broken into the following files. 
Math 111A is the runner.
Player creates players with associated blind, poker style, pair-of cards etc.
Rules has the rules of poker.
Simulate games handles simulation the flow of texas hold em.
