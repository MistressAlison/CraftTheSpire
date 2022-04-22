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

import java.util.ArrayList;

public class HeavyComponent extends AbstractComponent {
    public static final Texture ICON = TextureLoader.getTexture(CraftTheSpireMod.makeUIPath("HeavyComponent.png"));
    public static final String ID = CraftTheSpireMod.makeID("HeavyComponent");
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] UI_TEXT = uiStrings.TEXT;

    public static final SpawnRarity RARITY = SpawnRarity.UNCOMMON;
    public static final ComponentType TYPE = ComponentType.EXTRA;
    public static final RewardItem.RewardType REWARD = RewardTypeEnumPatches.CTS_HEAVY_REWARD;


    public HeavyComponent() {
        super(ID, UI_TEXT[0], RARITY, TYPE, ICON);
    }

    @Override
    public boolean canDropOnDisassemble(AbstractCard card) {
        return card.cost >= 2;
    }

    @Override
    public ArrayList<AbstractCard> filterCards(ArrayList<AbstractCard> input) {
        input.removeIf(c -> c.cost < 2);
        return input;
    }

    @Override
    public String modifyPreviewDescription(String desc) {
        return desc + UI_TEXT[2];
    }

    @Override
    public boolean canSelect() {
        return CraftingScreen.EXTRA.components.stream().noneMatch(c -> c.clicked && c.component.getClass().equals(LightComponent.class));
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
