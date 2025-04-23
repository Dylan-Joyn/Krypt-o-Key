import java.util.Scanner;

public class TypingRPG {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Welcome message
        System.out.println("==============================");
        System.out.println("  WELCOME TO TYPING RPG GAME  ");
        System.out.println("==============================");
        System.out.println("Type words accurately to cast spells and defeat monsters!");
        System.out.println();

        // Get player name
        System.out.print("Enter your character name: ");
        String playerName = scanner.nextLine().trim();
        if (playerName.isEmpty()) {
            playerName = "Hero";
        }

        // Create player
        Player player = new Player(playerName);

        // Create battle
        BattleLogic battlelogic = new BattleLogic(player);

        try {
            // Run the game
            battlelogic.runGame();
        } finally {
            // Cleanup
            battlelogic.close();
            scanner.close();
        }
    }
}