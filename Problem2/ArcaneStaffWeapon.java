package Problem2;

import java.util.Map;
import java.util.Random;

public class ArcaneStaffWeapon extends Weapon {
 
    /*
     * This function is an exmaple of polymorphism because it is overriding it from the 
     * Equipment.
     */
    @Override
    public EquipmentType getType() {
        return WeaponType.Staff_Magic;
    }

    /*
     * This function is an exmaple of polymorphism because it is overriding it from the 
     * Equipment.
     */
    @Override
    public Map<CoreAttributes, Integer> getRequirements() {
        return Map.of(
            CoreAttributes.INT, 12
        );
    }

    @Override
    public Map<Attribute, Integer> getBonuses() {
        return Map.of(
            CoreAttributes.INT, 1
        );
    }

    @Override
    public String getEffects() {
        return "1d8 Melee";
    }

    @Override
    public String getSpecialAbilities() {
        return null;
    }

    /*
     * This function is an example of polymorphism.
     */
    @Override
    public int damage() {
        return new Random().nextInt(1, 9);
    }
    
    @Override
    public String getName() {
      return "Arcane Staff";
  }
}
