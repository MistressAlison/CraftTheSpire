package CraftTheSpire.components;

import CraftTheSpire.CraftTheSpireMod;
import CraftTheSpire.screens.CraftingScreen;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

public class DubiousComponent extends AbstractComponent {
    public static final String ID = CraftTheSpireMod.makeID("DubiousComponent");
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;

    public DubiousComponent() {
        super(ID, TEXT[0], SpawnRarity.UNCOMMON, ComponentType.EXTRA);
    }

    @Override
    public void onClick() {}
}