package CraftTheSpire.util;

import CraftTheSpire.CraftTheSpireMod;
import CraftTheSpire.components.AbstractComponent;
import CraftTheSpire.ui.InventoryButton;

import java.util.TreeMap;

public class InventoryManager {
    public static final String saveString = CraftTheSpireMod.makeID("InventoryManager");
    public static final TreeMap<String, Integer> items = new TreeMap<>();

    public static void addComponent(AbstractComponent c, int amount) {
        if (amount > 0) {
            items.put(c.ID, items.getOrDefault(c.ID,0) + amount);
            c.playPickupSFX();
            InventoryButton.flash();
        }
    }

    public static void consumeComponent(AbstractComponent c) {
        items.put(c.ID, items.getOrDefault(c.ID, 0) - 1);
        if (items.get(c.ID) < 1) {
            items.remove(c.ID);
        }
        //c.consume(); // TODO add functionality?
    }

    public static void consumeComponent(String s) {
        consumeComponent(CraftTheSpireMod.componentMap.get(s));
    }

    public static void addComponent(String s, int amount) {
        addComponent(CraftTheSpireMod.componentMap.get(s), amount);
    }

    public static boolean hasAComponent() {
        return items.values().stream().anyMatch(i -> i > 0);
    }

    public static boolean hasRarityComponent() {
        return items.keySet().stream().anyMatch(s -> items.get(s) > 0 && CraftTheSpireMod.componentMap.get(s).type == AbstractComponent.ComponentType.RARITY_MOD);
    }

    public static boolean hasTypeComponent() {
        return items.keySet().stream().anyMatch(s -> items.get(s) > 0 && CraftTheSpireMod.componentMap.get(s).type == AbstractComponent.ComponentType.TYPE_MOD);
    }

    public static boolean hasExtraComponent() {
        return items.keySet().stream().anyMatch(s -> items.get(s) > 0 && CraftTheSpireMod.componentMap.get(s).type == AbstractComponent.ComponentType.EXTRA);
    }

    public static boolean canCraft() {
        return hasRarityComponent() && hasTypeComponent();
    }
}
