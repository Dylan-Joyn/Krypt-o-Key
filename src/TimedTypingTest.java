public class TimedTypingTest {
    public static void main(String[] args) {
        // Create the typing handler and damage calculator
        TimedTyping typingHandler = new TimedTyping();
        DamagePerCharacter damageCalculator = new DamagePerCharacter();

        System.out.println("=== Krypt-o-Key Typing Test ===");
        System.out.println("Type the word as accurately as possible to deal damage!");

        try {
            // Get a random word
            String challenge = typingHandler.getRandomWord();

            // Set time limit based on word length (minimum 5 seconds)
            int timeLimit = Math.max(challenge.length(), 5);

            // Run the typing challenge
            TimedTyping.TypingResult result = typingHandler.processInputWithTimer(challenge, timeLimit);

            // Calculate damage
            DamagePerCharacter.DamageResult damageResult = damageCalculator.calculateDamage(result);

            // Display results
            System.out.println("\n=== Results ===");

            if (result.isTimedOut()) {
                System.out.println("TIMED OUT - You failed to complete the challenge in time!");
                System.out.println("No damage dealt.");
            } else {
                // Show typing accuracy
                System.out.println("Accuracy: " + String.format("%.2f%%", result.getAccuracy()));

                // Show damage dealt
                System.out.println("You dealt " + damageResult.getDamage() + " damage!");
                System.out.println("Hit type: " + damageResult.getHitType());

            }
        } finally {
            // Close the typing handler
            typingHandler.close();
        }
    }
}
