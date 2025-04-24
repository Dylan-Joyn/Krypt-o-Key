package src;

public class Player {
    private String name;
    private int level;
    private int experience;
    private int experienceToNextLevel;
    private int health;
    private int maxHealth;
    private int damageMultiplier;
    private int potionCount;

    // Constructor
    public Player(String name) {
        this.name = name;
        this.level = 1;
        this.experience = 0;
        this.experienceToNextLevel = calculateExpForNextLevel(level);
        this.maxHealth = 100;  // Starting max health
        this.health = maxHealth;
        this.damageMultiplier = 100;  // 100% base damage
        this.potionCount = 3;  // Start with 3 potions
    }

    // Calculate experience needed for next level
    private int calculateExpForNextLevel(int currentLevel) {
        return 100 * currentLevel * currentLevel;
    }

    // Add experience and check for level up
    public String addExperience(int amount) {
        experience += amount;
        StringBuilder levelUpMessage = new StringBuilder();

        // Check for level up
        while (experience >= experienceToNextLevel) {
            level++;
            experience -= experienceToNextLevel;
            experienceToNextLevel = calculateExpForNextLevel(level);

            // Increase stats with level
            int previousMaxHealth = maxHealth;
            maxHealth = 100 + (level - 1) * 20;  // Each level adds 20 HP
            health += (maxHealth - previousMaxHealth);  // Heal by the amount of max health increase

            damageMultiplier += 5;  // Each level adds 5% to base damage

            levelUpMessage.append("LEVEL UP! You are now level ").append(level).append("!\n");
            levelUpMessage.append("Max Health increased to ").append(maxHealth).append("\n");
            levelUpMessage.append("Base Damage increased by 5%!\n");
        }

        return levelUpMessage.toString();
    }

    // Take damage from monster
    public int takeDamage(int amount) {
        int actualDamage = Math.min(health, amount);
        health = Math.max(0, health - amount);
        return actualDamage;
    }

    // Heal player using a potion
    public int usePotion() {
        if (potionCount <= 0) {
            return 0; // No potions left
        }

        // Reduce potion count
        potionCount--;

        // Calculate healing (full heal)
        int startHealth = health;
        health = maxHealth;
        int healAmount = health - startHealth;

        return healAmount;
    }

    // Add a potion to inventory
    public void addPotion() {
        potionCount++;
    }

    // Get damage multiplier
    public int getDamageMultiplier() {
        return damageMultiplier;
    }

    // Check if player is defeated
    public boolean isDefeated() {
        return health <= 0;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getExperienceToNextLevel() {
        return experienceToNextLevel;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getPotionCount() {
        return potionCount;
    }

    @Override
    public String toString() {
        return name + " [Level " + level + "] " +
                "HP: " + health + "/" + maxHealth + " " +
                "XP: " + experience + "/" + experienceToNextLevel + " " +
                "Potions: " + potionCount;
    }
}