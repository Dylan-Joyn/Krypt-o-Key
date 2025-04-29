package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimedTyping {
    private List<String> wordBank;
    private Random random;
    private Scanner scanner;

    public TimedTyping() {
        this.wordBank = new ArrayList<>();
        populateDefaultWordBank();
        this.random = new Random();
        this.scanner = new Scanner(System.in);
    }

    public TimedTyping(List<String> customWordBank) {
        this.wordBank = new ArrayList<>(customWordBank);
        this.random = new Random();
        this.scanner = new Scanner(System.in);
    }


    private void populateDefaultWordBank() {
        // Basic spell words
        String[] defaultWords = {
                "fireball", "icebolt", "lightning", "earthquake",
                "tornado", "poison", "healing", "shield",
                "attack", "defend", "slice", "stab"
        };

        // Add all words to the bank
        for (String word : defaultWords) {
            wordBank.add(word);
        }
    }


    public void addWord(String word) {
        wordBank.add(word);
    }


    public String getRandomWord() {
        int index = random.nextInt(wordBank.size());
        return wordBank.get(index);
    }


    public String getChallenge(int difficulty) {
        StringBuilder challenge = new StringBuilder();

        // Number of words based on difficulty
        int wordCount = 1;
        if (difficulty > 1) {
            wordCount = Math.min(difficulty, 4); // Cap at 4 words
        }

        for (int i = 0; i < wordCount; i++) {
            challenge.append(getRandomWord());
            if (i < wordCount - 1) {
                challenge.append(" ");
            }
        }

        return challenge.toString();
    }


    public TypingResult processInputWithTimer(String targetText, int timeLimit) {
        System.out.println("Type this: " + targetText);
        System.out.println("You have " + timeLimit + " seconds. Press Enter to start.");
        scanner.nextLine(); // Wait for Enter to start

        // Start timer in a separate thread
        AtomicBoolean timedOut = new AtomicBoolean(false);
        Thread timerThread = startTimerThread(timeLimit, timedOut);

        // Prompt for input
        System.out.print("> ");
        String userInput = "";

        try {
            userInput = scanner.nextLine();
            // If we get here, user completed input before timeout
            timerThread.interrupt(); // Stop the timer
        } catch (Exception e) {
            // This might happen if the thread is interrupted
            timedOut.set(true);
        }

        // Check if timed out
        if (timedOut.get()) {
            System.out.println("TIME'S UP!");
            return new TypingResult(0.0, "", true);
        }

        // Calculate accuracy
        return calculateResult(targetText, userInput);
    }


    private Thread startTimerThread(int seconds, AtomicBoolean timedOut) {
        Thread timerThread = new Thread(() -> {
            try {
                // Just sleep for the full duration
                Thread.sleep(seconds * 1000);

                // Time's up!
                timedOut.set(true);
                System.out.println("\nTIME'S UP!");

                // Try to interrupt the main thread waiting for input
                Thread.currentThread().interrupt();
            } catch (InterruptedException e) {
                // Timer was interrupted, probably because user finished typing
            }
        });

        timerThread.start();
        return timerThread;
    }


    public static int editDistance(String s1, String s2) {
        int lenS1 = s1.length();
        int lenS2 = s2.length();
        int[][] dp = new int[lenS1 + 1][lenS2 + 1];

        for (int i = 0; i <= lenS1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= lenS2; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= lenS1; i++) {
            for (int j = 1; j <= lenS2; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1])) + 1;
                }
            }
        }
        return dp[lenS1][lenS2];
    }


    private TypingResult calculateResult(String target, String input) {
        int editDistance = editDistance(input, target);
        int maxPossibleDistance = Math.max(input.length(), target.length());

        double accuracy = (1 - (double) editDistance / maxPossibleDistance) * 100;

        return new TypingResult(accuracy, input, false);
    }

    public TypingResult[] processMultipleRounds(int difficulty, int rounds) {
        TypingResult[] results = new TypingResult[rounds];

        for (int i = 0; i < rounds; i++) {
            System.out.println("\n=== Round " + (i + 1) + " of " + rounds + " ===");

            // Generate challenge
            String challenge = getChallenge(difficulty);

            // Calculate time based on length and difficulty
            int timeLimit = calculateTimeLimit(challenge, difficulty);

            // Run the challenge
            results[i] = processInputWithTimer(challenge, timeLimit);

            // Display result
            System.out.println("Round " + (i + 1) + " Result: " + results[i]);

            // Pause between rounds
            if (i < rounds - 1) {
                System.out.println("Press Enter to continue to the next round...");
                scanner.nextLine();
            }
        }

        return results;
    }


    private int calculateTimeLimit(String text, int difficulty) {
        // Base time: about 1 second per character
        double baseTime = text.length() * 1.0;

        // Adjust for difficulty (higher difficulty = less time)
        double adjustedTime = baseTime * (1.0 - ((difficulty - 1) * 0.2));

        // Ensure minimum time
        return Math.max((int)Math.ceil(adjustedTime), 3);
    }

    public void close() {
        scanner.close();
    }


    public static class TypingResult {
        private double accuracy;
        private String userInput;
        private boolean timedOut;

        public TypingResult(double accuracy, String userInput, boolean timedOut) {
            this.accuracy = accuracy;
            this.userInput = userInput;
            this.timedOut = timedOut;
        }

        public double getAccuracy() {
            return accuracy;
        }

        public String getUserInput() {
            return userInput;
        }

        public boolean isTimedOut() {
            return timedOut;
        }

        @Override
        public String toString() {
            if (timedOut) {
                return "TIMED OUT - Failed to complete in time";
            }
            return String.format("Accuracy: %.2f%%", accuracy);
        }
    }
}