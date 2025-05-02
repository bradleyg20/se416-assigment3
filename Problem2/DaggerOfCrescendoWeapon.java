package Problem2;

import java.util.Map;
import java.util.Random;

public class DaggerOfCrescendoWeapon extends Weapon {

    public DaggerOfCrescendoWeapon(){}

    @Override
    public EquipmentType getType() {
        return WeaponType.Dagger;
    }

    @Override
    public Map<CoreAttributes, Integer> getRequirements() {
        return Map.of(
            CoreAttributes.AGI, 10
        );
    }

    @Override
    public Map<Attribute, Integer> getBonuses() {
        return Map.of(
            CoreAttributes.AGI, 1
        );
    }

    @Override
    public String getEffects() {
        return "1d6 Melee";
    }

    @Override
    public String getSpecialAbilities() {
        return null;
    }

    @Override
    public int damage() {
        return new Random().nextInt(1, 7);
    }

    @Override
    public String getName() {
        return "Dagger of Crescendo";
    }
    
}
