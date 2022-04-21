package CraftTheSpire.components;

import CraftTheSpire.rewards.AbstractRewardLogic;
import CraftTheSpire.screens.CraftingScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;

import java.util.ArrayList;

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
    public Texture icon;

    public AbstractComponent(String ID, String name, SpawnRarity rarity, ComponentType type, Texture icon) {
        this.ID = ID;
        this.name = name;
        this.rarity = rarity;
        this.type = type;
        this.icon = icon;
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
            /*case EXTRA:
                return CraftingScreen.EXTRA.components.stream().noneMatch(c -> c.clicked && !c.component.getClass().equals(this.getClass()));*/
        }
        return true;
    }

    public void playPickupSFX() {
        CardCrawlGame.sound.playA("KEY_OBTAIN", -0.2F);
    }

    public void onSelectThisComponent() {}

    public CraftingScreen.RarityFilter forceRarity() {
        return CraftingScreen.RarityFilter.RANDOM;
    }

    public CraftingScreen.TypeFilter forceType() {
        return CraftingScreen.TypeFilter.RANDOM;
    }

    public ArrayList<AbstractCard> filterCards(ArrayList<AbstractCard> input) {
        return input;
    }

    public String modifyPreviewDescription(String desc) {
        return desc;
    }

    public void modifyCreatedCard(AbstractCard card) {}

    public abstract AbstractRewardLogic spawnReward(int amount);
}
