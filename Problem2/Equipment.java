package Problem2;

import java.util.Map;

public interface Equipment {

    public EquipmentType getType();
    public Map<CoreAttributes, Integer> getRequirements();
    public Map<Attribute, Integer> getBonuses();
    public String getEffects();
    public String getSpecialAbilities();
    public String getName();
}
