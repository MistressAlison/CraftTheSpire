package CraftTheSpire.components;

import CraftTheSpire.screens.CraftingScreen;
import CraftTheSpire.ui.ClickableUIObjects;
import CraftTheSpire.ui.ComponentContainer;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.Settings;

public abstract class AbstractComponent {
    public enum SpawnRarity {
        COMMON,
        UNCOMMON,
        RARE
    }

    public enum ComponentType {
        RARITY_MOD,
        TYPE_MOD,
        EXTRA
    }

    public String name;
    public String ID;
    public SpawnRarity rarity;
    public ComponentType type;

    public AbstractComponent(String ID, String name, SpawnRarity rarity, ComponentType type) {
        this.ID = ID;
        this.name = name;
        this.rarity = rarity;
        this.type = type;
    }

    public Color getNameColor() {
        switch (rarity) {
            case COMMON:
                return Settings.CREAM_COLOR;
            case UNCOMMON:
                return Settings.BLUE_TEXT_COLOR;
            case RARE:
                return Settings.GOLD_COLOR;
        }
        return Settings.CREAM_COLOR;
    }

    public boolean canSelect() {
        switch (type) {
            case RARITY_MOD:
                return CraftingScreen.RARITY.components.stream().noneMatch(c -> c.clicked && !c.component.getClass().equals(this.getClass()));
            case TYPE_MOD:
                return CraftingScreen.TYPE.components.stream().noneMatch(c -> c.clicked && !c.component.getClass().equals(this.getClass()));
            case EXTRA:
                return CraftingScreen.EXTRA.components.stream().noneMatch(c -> c.clicked && !c.component.getClass().equals(this.getClass()));
        }
        return true;
    }

    public abstract void onClick();
}
