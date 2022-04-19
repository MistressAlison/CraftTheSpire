package CraftTheSpire.components;

import CraftTheSpire.CraftTheSpireMod;
import CraftTheSpire.screens.CraftingScreen;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

public class RareComponent extends AbstractComponent {
    public static final String ID = CraftTheSpireMod.makeID("RareComponent");
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;

    public RareComponent() {
        super(ID, TEXT[0], SpawnRarity.RARE, ComponentType.RARITY_MOD);
    }

    @Override
    public void onClick() {
        CraftingScreen.previewRarity = AbstractCard.CardRarity.RARE;
    }
}