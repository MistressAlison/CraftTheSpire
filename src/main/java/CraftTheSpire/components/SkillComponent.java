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

public class SkillComponent extends AbstractComponent {
    public static final Texture ICON = TextureLoader.getTexture(CraftTheSpireMod.makeUIPath("SkillComponent.png"));
    public static final String ID = CraftTheSpireMod.makeID("SkillComponent");
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] UI_TEXT = uiStrings.TEXT;

    public static final SpawnRarity RARITY = SpawnRarity.COMMON;
    public static final ComponentType TYPE = ComponentType.TYPE_MOD;
    public static final RewardItem.RewardType REWARD = RewardTypeEnumPatches.CTS_SKILL_REWARD;


    public SkillComponent() {
        super(ID, UI_TEXT[0], RARITY, TYPE, ICON);
    }

    @Override
    public boolean canDropOnDisassemble(AbstractCard card) {
        return card.type == AbstractCard.CardType.SKILL;
    }

    @Override
    public CraftingScreen.TypeFilter forceType() {
        return CraftingScreen.TypeFilter.SKILL;
    }

    @Override
    public AbstractRewardLogic spawnReward(int amount) {
        return new RewardLogic(amount);
    }

    public static class RewardLogic extends AbstractRewardLogic {

        public RewardLogic(int amount) {
            super(ICON, ID, UI_TEXT[0], REWARD, amount);
        }

        @Override
        public CustomReward onLoad(RewardSave rewardSave) {
            return new RewardLogic(rewardSave.amount);
        }

        @Override
        public boolean claimReward() {
            InventoryManager.addComponent(ID, amount);
            return true;
        }
    }
}
