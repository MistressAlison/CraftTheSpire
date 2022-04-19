package CraftTheSpire.components;

import CraftTheSpire.CraftTheSpireMod;
import CraftTheSpire.screens.CraftingScreen;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

public class PowerComponent extends AbstractComponent {
    public static final String ID = CraftTheSpireMod.makeID("PowerComponent");
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;

    public PowerComponent() {
        super(ID, TEXT[0], SpawnRarity.UNCOMMON, ComponentType.TYPE_MOD);
    }

    @Override
    public boolean canSelect() {
        return super.canSelect() && CraftingScreen.RARITY.components.stream().anyMatch(c -> c.clicked && !(c.component instanceof CommonComponent));
    }

    @Override
    public void onClick() {
        CraftingScreen.previewType = AbstractCard.CardType.POWER;
    }
}