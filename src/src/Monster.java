package src;

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

    // Constructor
    public Monster(String name, int difficulty) {
        this.name = name;
        this.difficulty = Math.max(1, difficulty);
        this.type = determineType(difficulty);

        // Initialize stats based on difficulty
        initializeStats();
    }

    // Determine monster type based on difficulty
    private MonsterType determineType(int difficulty) {
        // Every 10th monster is a boss (PURPLE)
        if (difficulty % 10 == 0 && difficulty > 0) {
            return MonsterType.PURPLE;
        }

        // Otherwise determine by difficulty range
        if (difficulty <= 3) {
            return MonsterType.GREEN;
        } else if (difficulty <= 7) {
            return MonsterType.YELLOW;
        } else {
            return MonsterType.RED;
        }
    }

    // Monster stats based on type and difficulty
    private void initializeStats() {
        // Base stats scale with difficulty
        int baseHealth = 20 + (difficulty * 5);
        int baseDamage = 5 + (difficulty * 2);

        // Apply multipliers based on type
        switch (type) {
            case GREEN:
                maxHealth = baseHealth;
                baseAttack = baseDamage;
                expYield = 10 * difficulty;
                break;
            case YELLOW:
                maxHealth = (int)(baseHealth * 1.5);
                baseAttack = (int)(baseDamage * 1.3);
                expYield = 15 * difficulty;
                break;
            case RED:
                maxHealth = (int)(baseHealth * 2.0);
                baseAttack = (int)(baseDamage * 1.6);
                expYield = 25 * difficulty;
                break;
            case PURPLE:
                // Boss monsters are big strong
                maxHealth = (int)(baseHealth * 3.0);
                baseAttack = (int)(baseDamage * 2.0);
                expYield = 50 * difficulty;
                break;
        }

        // Set current health to max
        health = maxHealth;

        // Calculate potion drop chance
        potionDropChance = type.getBaseDropChance();
    }

    // Take damage from player
    public int takeDamage(int amount) {
        int actualDamage = Math.min(health, amount);
        health = Math.max(0, health - amount);
        return actualDamage;
    }

    // Check if monster is defeated
    public boolean isDefeated() {
        return health <= 0;
    }

    // Get attack damage (with small random variation)
    public int getAttackDamage() {
        Random rand = new Random();
        int variation = (int)(baseAttack * 0.2);

        return baseAttack + rand.nextInt(variation * 2 + 1) - variation;
    }

    // Check if monster drops a potion
    public boolean dropsPotion() {
        Random rand = new Random();
        return rand.nextDouble() < potionDropChance;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public MonsterType getType() {
        return type;
    }

    public int getExpYield() {
        return expYield;
    }

    @Override
    public String toString() {
        return type.getDisplayName() + " " + name + " [Lvl " + difficulty + "]" +
                " (HP: " + health + "/" + maxHealth + ")";
    }
}
