package CraftTheSpire.components;

import CraftTheSpire.CraftTheSpireMod;
import CraftTheSpire.patches.ScreenPatches;
import CraftTheSpire.screens.CraftingScreen;
import CraftTheSpire.ui.ClickableUIObjects;
import CraftTheSpire.ui.ComponentContainer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

public class CommonComponent extends AbstractComponent {
    public static final String ID = CraftTheSpireMod.makeID("CommonComponent");
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;

    public CommonComponent() {
        super(ID, TEXT[0], SpawnRarity.COMMON, ComponentType.RARITY_MOD);
    }

    @Override
    public boolean canSelect() {
        for (ComponentContainer con : CraftingScreen.containers) {
            for (ClickableUIObjects.UIComponentTickBox t : con.components) {
                if (t.clicked) {
                    if (t.component.getClass().equals(this.getClass())) {
                        return true;
                    } else if (t.component.type == this.type) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onClick() {
        CraftingScreen.previewRarity = AbstractCard.CardRarity.COMMON;
    }
}
