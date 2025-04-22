import java.util.Random;

public class Monster {
    private String name;
    private int health;
    private int maxHealth;
    private int baseAttack;
    private int difficulty;
    private MonsterType type;
    private int expYield;
    private double potionDropChance;


    // Enum for different monster types (color-coded)
    public enum MonsterType {
        GREEN(1, "Green", 0.05),    // Easy
        YELLOW(2, "Yellow", 0.15),  // Medium
        RED(3, "Red", 0.25),        // Hard
        PURPLE(4, "Purple", 0.5);   // Boss - high potion drop chance

        private final int level;
        private final String displayName;
        private final double baseDropChance;

        MonsterType(int level, String displayName, double baseDropChance) {
            this.level = level;
            this.displayName = displayName;
            this.baseDropChance = baseDropChance;
        }

        public int getLevel() {
            return level;
        }

        public String getDisplayName() {
            return displayName;
        }

        public double getBaseDropChance() {
            return baseDropChance;
        }
    }



}
