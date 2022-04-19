package CraftTheSpire.components;

import CraftTheSpire.CraftTheSpireMod;
import CraftTheSpire.screens.CraftingScreen;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

public class SkillComponent extends AbstractComponent {
    public static final String ID = CraftTheSpireMod.makeID("SkillComponent");
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;

    public SkillComponent() {
        super(ID, TEXT[0], SpawnRarity.COMMON, ComponentType.TYPE_MOD);
    }

    @Override
    public void onClick() {
        CraftingScreen.previewType = AbstractCard.CardType.SKILL;
    }
}
