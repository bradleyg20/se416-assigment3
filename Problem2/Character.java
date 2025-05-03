package Problem2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class Character {

    protected Map<CoreAttributes, Integer> coreAttributes;
    protected Map<CoreAttributes, Integer> modifiers = new HashMap<>();
    protected Map<Attribute, Integer> inFight = new HashMap<>();

    public Character(){
        setCoreAttributes();
        applyClassBonus();
        setCoreModifiers();
    };

    public Map<CoreAttributes, Integer> getCoreAttributes() {
        return coreAttributes;
    };

    private void setCoreAttributes(){
        Random dice = new Random();
        for (CoreAttributes att : CoreAttributes.values()) {
            int diceRolls[] = new int[3];
            for (int i = 0; i < diceRolls.length; i++) {
                diceRolls[i] = dice.nextInt(1, 7);
            }
            Arrays.sort(diceRolls);
            coreAttributes.put(att, diceRolls[1] + diceRolls[2]);
        }
    };

    private void setCoreModifiers() {
        for(CoreAttributes att : coreAttributes.keySet()){
            int attVal = coreAttributes.get(att);
            int attValModified = (attVal + 1) / 2;
            int modifier = 0;
            switch (attValModified) {
                case 0:
                    modifier = -4;
                    break;
                case 1:
                    modifier = -3;
                    break;
                case 2:
                    modifier = -2;
                    break;
                case 3:
                    modifier = -1;
                    break;
                case 4:
                    modifier = 0;
                    break;
                case 5:
                    modifier = 1;
                    break;
                case 6:
                    modifier = 2;
                    break;
                case 7:
                    modifier = 3;
                    break;
                case 8:
                    modifier = 4;
                    break;
                default:
                    // Since the Core Attributes are always between 0 and 16 inclusive, the modified attribute value used 
                    // in the switch will always be between 0 and 8 inclusive.
                    break;
            }
            modifiers.put(att, modifier);
        }
    }

    public Map<CoreAttributes, Integer> getModifiers(){
        return modifiers;
    }

    protected abstract void applyClassBonus();

    protected abstract int attack(Weapon Weapon);

    /*
     * This is an example of data coupling.
     * Simple Parameter Type: It accepts only a primitive int parameter (the damage amount), 
     * which is the minimal, exact data needed to perform its function.
     */
    public void damage(int damage) {
        int vit = inFight.get(CoreAttributes.VIT);
        vit = Math.max(vit - damage, 0);
        inFight.replace(CoreAttributes.VIT, vit);
    }
}