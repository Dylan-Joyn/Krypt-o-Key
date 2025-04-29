package src;

import java.util.Random;

public class UtilFunc {

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

    public static boolean rollD20(int DC) {
        Random random = new Random();
        String clrscr = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
        String die1 = """
                       .-------.
                      /   o   /|
                     /_______/o|
                     | o     | |
                     |   o   |o/
                     |     o |/
                     '-------'""";
        String die2 = """
                    ______
                   /\\     \\
                  /o \\  o  \\
                 /   o\\_____\\
                 \\o   /o    /
                  \\ o/  o  /
                   \\/____o/""";

        for(int i = 0; i < 6; i++) {
            System.out.println(clrscr);
            if(i % 2 == 0) {
                System.out.println(die1);
            } else {
                System.out.println(die2);
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(clrscr);

        int roll = random.nextInt(20) + 1;
        System.out.println(roll);
        return roll >= DC;
    }

}
