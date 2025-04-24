package src;

public class DamagePerCharacter {
    private double baseDamagePerChar;
    private double accuracyMultiplier;
    private double criticalThreshold;
    private double criticalMultiplier;

    public DamagePerCharacter() {
        this.baseDamagePerChar = 1.0;         // 1 damage point per character
        this.accuracyMultiplier = 1.0;        // 100% accuracy = 100% of damage
        this.criticalThreshold = 95.0;        // 95% accuracy or above = critical hit
        this.criticalMultiplier = 1.5;        // 50% bonus damage for critical hits
    }

    public DamagePerCharacter(double baseDamagePerChar, double accuracyMultiplier,
                              double criticalThreshold, double criticalMultiplier) {
        this.baseDamagePerChar = baseDamagePerChar;
        this.accuracyMultiplier = accuracyMultiplier;
        this.criticalThreshold = criticalThreshold;
        this.criticalMultiplier = criticalMultiplier;
    }

    public DamageResult calculateDamage(TimedTyping.TypingResult result) {
        // If timed out, no damage
        if (result.isTimedOut()) {
            return new DamageResult(0, HitType.MISS);
        }

        // Get text length and accuracy
        int textLength = result.getUserInput().length();
        double accuracy = result.getAccuracy();

        // Calculate base damage
        double baseDamage = textLength * baseDamagePerChar;

        // Apply accuracy multiplier
        double damageWithAccuracy = baseDamage * (accuracy / 100.0) * accuracyMultiplier;

        // Determine hit type and final damage
        HitType hitType;
        double finalDamage;

        if (accuracy >= criticalThreshold) {
            // Critical hit
            hitType = HitType.CRITICAL;
            finalDamage = damageWithAccuracy * criticalMultiplier;
        } else if (accuracy >= 70.0) {
            // Normal hit
            hitType = HitType.NORMAL;
            finalDamage = damageWithAccuracy;
        } else if (accuracy >= 30.0) {
            // Weak hit
            hitType = HitType.WEAK;
            finalDamage = damageWithAccuracy * 0.7; // 70% of normal damage
        } else {
            // Glancing hit (barely any damage)
            hitType = HitType.GLANCING;
            finalDamage = damageWithAccuracy * 0.3; // 30% of normal damage
        }

        // Round to nearest integer and return
        int roundedDamage = (int)Math.round(finalDamage);
        return new DamageResult(roundedDamage, hitType);
    }

    public int calculateHealing(TimedTyping.TypingResult result) {
        // If timed out, no healing
        if (result.isTimedOut()) {
            return 0;
        }

        // Use the same calculation as damage but apply a different base value
        double healingPerChar = baseDamagePerChar * 1.2; // Healing is slightly more effective

        // Get text length and accuracy
        int textLength = result.getUserInput().length();
        double accuracy = result.getAccuracy();

        // Calculate healing amount
        double baseHealing = textLength * healingPerChar;
        double finalHealing = baseHealing * (accuracy / 100.0) * accuracyMultiplier;

        // Apply critical bonus for very accurate typing
        if (accuracy >= criticalThreshold) {
            finalHealing *= criticalMultiplier;
        }

        // Round to nearest integer
        return (int)Math.round(finalHealing);
    }

    public enum HitType {
        MISS("Miss"),
        GLANCING("Glancing Hit"),
        WEAK("Weak Hit"),
        NORMAL("Hit"),
        CRITICAL("Critical Hit");

        private final String displayName;

        HitType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    /**
     * Class to hold damage calculation result
     */
    public static class DamageResult {
        private int damage;
        private HitType hitType;

        public DamageResult(int damage, HitType hitType) {
            this.damage = damage;
            this.hitType = hitType;
        }

        public int getDamage() {
            return damage;
        }

        public HitType getHitType() {
            return hitType;
        }

        @Override
        public String toString() {
            return hitType + " for " + damage + " damage";
        }
    }
}
