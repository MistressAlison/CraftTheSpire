package CraftTheSpire.components;

import CraftTheSpire.CraftTheSpireMod;
import CraftTheSpire.patches.RewardTypeEnumPatches;
import CraftTheSpire.rewards.AbstractRewardLogic;
import CraftTheSpire.screens.CraftingScreen;
import CraftTheSpire.util.InventoryManager;
import CraftTheSpire.util.TextureLoader;
import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardSave;

public class RareComponent extends AbstractComponent {
    public static final Texture ICON = TextureLoader.getTexture(CraftTheSpireMod.makeUIPath("RareComponent.png"));
    public static final String ID = CraftTheSpireMod.makeID("RareComponent");
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] UI_TEXT = uiStrings.TEXT;

    public static final SpawnRarity RARITY = SpawnRarity.RARE;
    public static final ComponentType TYPE = ComponentType.RARITY_MOD;
    public static final RewardItem.RewardType REWARD = RewardTypeEnumPatches.CTS_RARE_REWARD;


    public RareComponent() {
        super(ID, UI_TEXT[0], RARITY, TYPE, ICON, REWARD);
    }

    @Override
    public boolean canDropOnDisassemble(AbstractCard card) {
        return card.rarity == AbstractCard.CardRarity.RARE;
    }

    @Override
    public CraftingScreen.RarityFilter forceRarity() {
        return CraftingScreen.RarityFilter.RARE;
    }
}
