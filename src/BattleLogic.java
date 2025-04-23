import java.util.Random;
import java.util.Scanner;

public class BattleLogic {
    private Player player;
    private TimedTyping typingHandler;
    private DamagePerCharacter damageCalculator;
    private int currentDifficulty = 1;
    private Scanner scanner;

    private static final String[] MONSTER_NAMES = {
            "Goblin", "Orc", "Troll", "Skeleton", "Zombie", "Ghost",
            "Slime", "Bat", "Spider", "Wolf", "Bear", "Snake",
            "Dragon", "Demon", "Witch", "Warlock", "Banshee", "Ogre"
    };

    // Constructor
    public BattleLogic(Player player) {
        this.player = player;
        this.typingHandler = new TimedTyping();
        this.damageCalculator = new DamagePerCharacter();
        this.scanner = new Scanner(System.in);
    }

    // Start a new battle
    public void startBattle() {
        // Create a monster with current difficulty
        Monster monster = generateMonster();

        System.out.println("\n==== BATTLE START ====");
        System.out.println("A " + monster + " appears!");
        System.out.println(player);

        // Battle loop
        while (!monster.isDefeated() && !player.isDefeated()) {
            // Display battle menu
            displayBattleMenu();

            // Get player choice
            int choice = getUserChoice(1, 2);

            switch (choice) {
                case 1:
                    // Attack monster
                    playerAttack(monster);
                    break;
                case 2:
                    // Use potion
                    usePotion();
                    break;
            }

            // Check if monster is defeated
            if (monster.isDefeated()) {
                handleVictory(monster);
                break;
            }

            // Monster attacks player
            monsterAttack(monster);

            // Check if player is defeated
            if (player.isDefeated()) {
                handleDefeat();
                break;
            }
        }
    }

    // Generate a monster based on current difficulty
    private Monster generateMonster() {
        Random rand = new Random();
        String name = MONSTER_NAMES[rand.nextInt(MONSTER_NAMES.length)];
        return new Monster(name, currentDifficulty);
    }

    // Display battle menu
    private void displayBattleMenu() {
        System.out.println("\n--- Your Turn ---");
        System.out.println("1. Attack");
        System.out.println("2. Use Potion (" + player.getPotionCount() + " available)");
        System.out.print("Choose an action (1-2): ");
    }

    // Get user choice between min and max (inclusive)
    private int getUserChoice(int min, int max) {
        int choice = -1;
        while (choice < min || choice > max) {
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice < min || choice > max) {
                    System.out.print("Invalid choice. Enter a number between " + min + " and " + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a number: ");
            }
        }
        return choice;
    }

    // Player attacks monster
    private void playerAttack(Monster monster) {
        System.out.println("\n--- ATTACK PHASE ---");

        // Get difficulty based on monster level
        int difficulty = monster.getDifficulty();

        // Get a typing challenge
        String challenge = typingHandler.getChallenge(difficulty);

        // Calculate time limit based on difficulty
        int timeLimit = Math.max(challenge.length() / 2, 5);

        // Run the typing challenge
        TimedTyping.TypingResult result = typingHandler.processInputWithTimer(challenge, timeLimit);

        // Calculate damage
        DamagePerCharacter.DamageResult damageResult = damageCalculator.calculateDamage(result);

        // Apply player's level damage multiplier
        int finalDamage = (int)(damageResult.getDamage() * (player.getDamageMultiplier() / 100.0));

        if (result.isTimedOut()) {
            System.out.println("You failed to cast your spell in time!");
            System.out.println("No damage dealt.");
        } else {
            // Apply damage to monster
            int actualDamage = monster.takeDamage(finalDamage);

            // Show typing accuracy
            System.out.println("Typing Accuracy: " + String.format("%.2f%%", result.getAccuracy()));

            // Show damage dealt
            System.out.println("You dealt " + actualDamage + " damage!");
            System.out.println("Hit type: " + damageResult.getHitType());
            System.out.println(monster.getName() + " HP: " + monster.getHealth() + "/" + monster.getMaxHealth());
        }
    }

    // Use a potion
    private void usePotion() {
        if (player.getPotionCount() == 0) {
            System.out.println("You don't have any potions!");
            return;
        }

        // Use a potion (full heal)
        int healAmount = player.usePotion();

        if (healAmount > 0) {
            System.out.println("You used a potion and restored " + healAmount + " health!");
            System.out.println("Your HP: " + player.getHealth() + "/" + player.getMaxHealth());
        } else {
            System.out.println("You're already at full health!");
        }
    }

    // Handle victory
    private void handleVictory(Monster monster) {
        System.out.println("\n=== VICTORY! ===");
        System.out.println("You defeated the " + monster.getName() + "!");

        // Award experience
        int expGained = monster.getExpYield();
        System.out.println("You gained " + expGained + " experience!");

        String levelUpMessage = player.addExperience(expGained);
        if (!levelUpMessage.isEmpty()) {
            System.out.println(levelUpMessage);
        }

        // Check for potion drop
        if (monster.dropsPotion()) {
            player.addPotion();
            System.out.println("The monster dropped a potion!");
        }

        // Increase difficulty for next battle
        currentDifficulty++;
    }

    // Handle defeat
    private void handleDefeat() {
        System.out.println("\n=== DEFEAT ===");
        System.out.println("You were defeated by the monster!");
        System.out.println("Game Over");
    }

    // Monster attacks player
    private void monsterAttack(Monster monster) {
        System.out.println("\n--- Monster's Turn ---");

        int damage = monster.getAttackDamage();
        int actualDamage = player.takeDamage(damage);

        System.out.println("The " + monster.getName() + " attacks you for " + actualDamage + " damage!");
        System.out.println("Your HP: " + player.getHealth() + "/" + player.getMaxHealth());
    }

    // Run a full game with multiple battles
    public void runGame() {
        System.out.println("=== Welcome to the Typing RPG Game ===");
        System.out.println("Type words quickly and accurately to defeat monsters!");
        System.out.println(player);

        // Main game loop
        boolean gameRunning = true;
        while (gameRunning && !player.isDefeated()) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Battle Monster (Difficulty: " + currentDifficulty + ")");
            System.out.println("2. Use Potion");
            System.out.println("3. View Player Stats");
            System.out.println("4. Quit Game");
            System.out.print("Choose an option (1-4): ");

            int choice = getUserChoice(1, 4);

            switch (choice) {
                case 1:
                    startBattle();
                    break;
                case 2:
                    usePotion();
                    break;
                case 3:
                    System.out.println("\n=== PLAYER STATS ===");
                    System.out.println(player);
                    break;
                case 4:
                    gameRunning = false;
                    System.out.println("Thanks for playing!");
                    break;
            }

            // Check if player was defeated
            if (player.isDefeated()) {
                System.out.println("\nGAME OVER - You were defeated!");
            }
        }
    }

    // Clean up resources when done
    public void close() {
        if (typingHandler != null) {
            typingHandler.close();
        }
        if (scanner != null) {
            scanner.close();
        }
    }
}
