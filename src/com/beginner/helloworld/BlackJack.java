package com.beginner.helloworld;

import java.util.*;
import static java.util.Map.entry;


public class BlackJack {
    /*
    Black Jack game basic implementation in Java.
    - the player can only "Hit" or "Stand".
    - min 2 players (1 human + PC dealer)
    - max 7 players in total (6 human + PC dealer)
    */

    protected static boolean playing = true;
    protected static int id = 1;

    public static void main (String[] args) {
        String[] suits = {"Hearts", "Diamonds", "Spades", "Clubs"};
        String[] ranks = {"Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Jack", "Queen",
                "King", "Ace"};

        // Welcome
        System.out.println("Welcome to Black Jack.");

        // Game setup stage

        // Check number of human players (1-6). Dealer will play by default.
        int totalNumPlayers;
        while (true) {
            try {
                Scanner input = new Scanner(System.in);
                System.out.print("How many human players will play (1-6)? --> ");
                totalNumPlayers = Math.abs(input.nextInt());
                if (totalNumPlayers > 6) {
                    System.out.println("Maximum 6 players. Choose a number between 1 to 6");
                    continue;
                } else if (totalNumPlayers == 0) {
                    System.out.println("Cannot have 0. Need at least one human player.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please input an integer.");
            }
        }

        // Create a list of players that will participate.
        ArrayList<Player> playersList = new ArrayList<>();
        Player playerDealer = new Player("Dealer", true);
        playersList.add(playerDealer);
        for (int i=1; i<totalNumPlayers+1; i++) {
            Scanner nameRequest = new Scanner(System.in);
            System.out.println("Player " + i + " name?");
            String playerName = nameRequest.nextLine();
            Player playerHuman = new Player(playerName, false);
            playersList.add(playerHuman);
        }

        System.out.println("***************************");
        System.out.println("The final list of players in this game is as follows:");
        for (Player player: playersList) {
            System.out.println(player.name + " --> Player " + player.id);
        }
        System.out.println("***************************");

        //Create & shuffle the deck of cards. Deal two cars to each player.
        Deck gameDeck = new Deck(suits, ranks);
        gameDeck.shuffleDeck();

        for (Player player: playersList) {
            player.hand.addCard(gameDeck.dealCard());
            player.hand.addCard(gameDeck.dealCard());

            // Prompt each player for their bet.
            if (!player.dealer) {
                player.takeBet(player.chips);
            }
        }

        // Show each player's hand (dealer keeps one of the cards hidden).
        for (Player player: playersList) {
            player.showSome();
        }

        // while playing (start of game)
        while (BlackJack.playing) {

            // temp list to avoid ConcurrentModificationException ^_^
            List<Player> toBeRemoved = new ArrayList<>();
            // Prompt each player to Hit or Stand.
            for (Player player: playersList) {
                if (!player.dealer) {
                    System.out.println("***************************");
                    System.out.println(player.name + "'s turn");
                    player.hitOrStand(gameDeck, player.hand);
                    // Show each player's hand (dealer keeps one of the cards hidden).
                    player.showSome();
                }

                // If a player's hand exceeds 21, player busts and removed from player's list.
                if (!player.dealer && player.hand.value > 21) {
                    player.playerBusts(player.chips);
                    toBeRemoved.add(player);
                }
            }
            playersList.removeAll(toBeRemoved);
            System.out.println("***************************");
            System.out.println("Remaining players:");
            for (Player player: playersList) {
                System.out.println(player.name + " --> " + player.hand.value + " points");
            }

            // at least one human player still in the game
            if (playersList.size() > 1) {
                // If there's still players in the players list (didn't bust), play Dealer's hand until it reaches 17
                // or higher
                while (playersList.get(0).hand.value < 17) {
                    playersList.get(0).hit(gameDeck, playersList.get(0).hand);
                }

                // Show all cards
                for (Player player: playersList) {
                    player.showAll();
                }
            }

            // Run different winning scenarios & inform players of their chips total.
            if (playersList.get(0).hand.value > 21) {
                playersList.get(0).playerBusts(playersList.get(0).chips);
                for (Player player: playersList) {
                    if (!player.dealer) {
                        player.playerWins(player.chips);
                        System.out.println("Your total number of chips are: " + player.chips.total);
                    }
                }
                break;
            }

            // find out what was the best hand
            int biggestHand = 0;
            for (Player player: playersList) {
                if (player.hand.value > biggestHand) {
                    biggestHand = player.hand.value;
                }
            }

            System.out.println("***************************");
            System.out.println("** Biggest hand was: " + biggestHand);
            System.out.println("***************************");

            // determine the winner/s
            int winnerCounter = 0;
            int winnerIdx = 0;
            for (Player player: playersList) {
                if (player.hand.value < biggestHand) {
                    player.playerBusts(player.chips);
                    System.out.println(player.name + "'s total number of chips are: " + player.chips.total);
                } else if (player.hand.value == biggestHand) {
                    winnerCounter += 1;
                    winnerIdx = playersList.indexOf(player);
                } else if (winnerCounter > 1) {
                    System.out.println("We have a draw");
                    player.playerPush(player.chips);
                }
            }
            playersList.get(winnerIdx).playerWins(playersList.get(winnerIdx).chips);
            if (!playersList.get(winnerIdx).dealer) {
                System.out.println(playersList.get(winnerIdx).name + "'s total number of chips are: " +
                        playersList.get(winnerIdx).chips.total);
            }

            // Ask to play again.
            Scanner askForNewGame = new Scanner(System.in);
            System.out.println("Would you like to play another round? Y/N");
            String newGame = askForNewGame.nextLine().toLowerCase();

            if (newGame.startsWith("y")) {
                BlackJack.playing = true;
            } else {
                System.out.println("Thanks for playing!");
                break;
            }
        }
    }

}

class Player {
    String name;
    boolean dealer;
    Hand hand;
    Chips chips;
    int id = BlackJack.id;

    public Player(String playerName, boolean isDealer) {
        this.name = playerName;
        this.dealer = isDealer;
        this.hand = new Hand();
        this.chips = new Chips();
        if (this.dealer) {
            BlackJack.id++;
            System.out.println("Dealer player created.");
        } else {
            BlackJack.id++;
            System.out.println("Player " + this.name + " has been created.");
        }
    }

    public void takeBet(Chips chipsBet) {
        while (true) {
            try {
                Scanner input = new Scanner(System.in);
                System.out.print("How many chips would " + this.name + " like to bet? --> ");
                chipsBet.bet = Math.abs(input.nextInt());
            } catch (InputMismatchException e) {
                System.out.println("Sorry, please provide an integer.");
                continue;
            }
            if (chipsBet.bet > chipsBet.total) {
                System.out.println("Sorry, you don't have enough chips. You currently have " + chipsBet.total + ".");
            } else {
                break;
            }

        }
    }

    public void hit(Deck playerDeck, Hand playerHand) {
        Card singleCard = playerDeck.dealCard();
        playerHand.addCard(singleCard);
        playerHand.adjustForAce();
    }

    public void hitOrStand(Deck playerDeck, Hand playerHand) {
        while (true) {
            Scanner input = new Scanner(System.in);
            System.out.print("Hit or Stand? Enter h or s: --> ");
            String answer = input.nextLine();

            if (answer.toLowerCase().startsWith("h")) {
                hit(playerDeck, playerHand);
            } else if (answer.toLowerCase().startsWith("s")) {
                System.out.println("Player stands. Dealer's turn");
                BlackJack.playing = false;
            } else {
                System.out.println("Sorry, I don't understand that. Please enter h or s only.");
                continue;
            }
            break;
        }
    }

    public void showSome() {
        System.out.println("***************************");
        System.out.println(this.name + "'s hand:");
        if (this.dealer) {
            System.out.println("<Hidden card>");
            System.out.println(this.hand.handCards.get(1));
        } else {
            for (Card card : this.hand.handCards) {
                System.out.println(card);
            }
        }
    }

    public void showAll() {
        System.out.println("***************************");
        System.out.println(this.name + "'s hand:");
        for (Card card: this.hand.handCards) {
            System.out.println(card);
        }
        System.out.print(this.name + "'s hand value = ");
        System.out.println(this.hand.value);
    }

    public void playerBusts(Chips playerChips) {
        System.out.println(this.name + " BUSTS!");
        playerChips.loseBet();
    }

    public void playerWins(Chips playerChips) {
        System.out.println(this.name + " WINS!");
        playerChips.winBet();
    }

    public void playerPush(Chips playerChips) {
        System.out.println("There is a tie. PUSH!");
        playerChips.winBet();
    }
}

class Card {
    String suite;
    String rank;

    public Card(String cardSuite, String cardRank) {
        this.suite = cardSuite;
        this.rank = cardRank;
    }

    public String toString() {
        return this.rank + " of " + this.suite;
    }
}

class Deck {
    ArrayList<Card> deckCards = new ArrayList<>();

    public Deck(String[] suits, String[] ranks) {
        for (String suite: suits) {
            for (String rank: ranks) {
                Card card = new Card(suite, rank);
                deckCards.add(card);
            }
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(deckCards);
    }

    public Card dealCard() {
        return deckCards.remove(deckCards.size() - 1);
    }
}

class Hand {
    ArrayList<Card> handCards = new ArrayList<>();
    int value = 0;
    int aces = 0;

    public void addCard(Card deckCard) {
        Map<String, Integer> values = Map.ofEntries(
                entry("Two", 2),
                entry("Three", 3),
                entry("Four", 4),
                entry("Five", 5),
                entry("Six", 6),
                entry("Seven", 7),
                entry("Eight", 8),
                entry("Nine", 9),
                entry("Ten", 10),
                entry("Jack", 10),
                entry("Queen", 10),
                entry("King", 10),
                entry("Ace", 11)
        );

        handCards.add(deckCard);
        this.value += values.get(deckCard.rank);
        if (deckCard.rank.equals("Ace")) {
            this.aces += 1;
        }
    }

    public void adjustForAce() {
        while (this.value > 21 && this.aces != 0) {
            this.value -= 10;
            this.aces -= 1;
        }
    }
}

class Chips {
    int total = 100;
    int bet = 0;

    public void winBet() {
        this.total += this.bet;
    }

    public void loseBet() {
        this.total -= this.bet;
    }
}