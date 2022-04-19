package CraftTheSpire.components;

import CraftTheSpire.CraftTheSpireMod;
import CraftTheSpire.screens.CraftingScreen;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

public class UncommonComponent extends AbstractComponent {
    public static final String ID = CraftTheSpireMod.makeID("UncommonComponent");
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;

    public UncommonComponent() {
        super(ID, TEXT[0], SpawnRarity.UNCOMMON, ComponentType.RARITY_MOD);
    }

    @Override
    public void onClick() {
        CraftingScreen.previewRarity = AbstractCard.CardRarity.UNCOMMON;
    }
}