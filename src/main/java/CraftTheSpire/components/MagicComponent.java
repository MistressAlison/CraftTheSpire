package CraftTheSpire.components;

import CraftTheSpire.CraftTheSpireMod;
import CraftTheSpire.patches.RewardTypeEnumPatches;
import CraftTheSpire.rewards.AbstractRewardLogic;
import CraftTheSpire.util.InventoryManager;
import CraftTheSpire.util.TextureLoader;
import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.PanicButton;
import com.megacrit.cardcrawl.cards.colorless.RitualDagger;
import com.megacrit.cardcrawl.cards.purple.Halt;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardSave;
import javassist.ClassPool;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class MagicComponent extends AbstractComponent {
    public static final Texture ICON = TextureLoader.getTexture(CraftTheSpireMod.makeUIPath("MagicComponent.png"));
    public static final String ID = CraftTheSpireMod.makeID("MagicComponent");
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] UI_TEXT = uiStrings.TEXT;

    public static final SpawnRarity RARITY = SpawnRarity.UNCOMMON;
    public static final ComponentType TYPE = ComponentType.EXTRA;
    public static final RewardItem.RewardType REWARD = RewardTypeEnumPatches.CTS_MAGIC_REWARD;


    public MagicComponent() {
        super(ID, UI_TEXT[0], RARITY, TYPE, ICON);
    }

    @Override
    public ArrayList<AbstractCard> filterCards(ArrayList<AbstractCard> input) {
        input.removeIf(c -> !usesMagic(c));
        return input;
    }

    @Override
    public String modifyPreviewDescription(String desc) {
        return desc + UI_TEXT[2];
    }

    private static boolean usesMagicBool;
    public static boolean usesMagic(AbstractCard card) {
        usesMagicBool = false;
        if (card.baseMagicNumber > 0 && StringUtils.containsIgnoreCase(card.rawDescription, "!M!")) {
            try {
                ClassPool pool = Loader.getClassPool();
                CtMethod ctClass = pool.get(card.getClass().getName()).getDeclaredMethod("use");

                ctClass.instrument(new ExprEditor() {
                    @Override
                    public void edit(FieldAccess f) {

                        if (f.getFieldName().equals("magicNumber") && !f.isWriter()) {
                            usesMagicBool = true;
                        }

                    }
                });

            } catch (Exception ignored) { }
        }
        return usesMagicBool;
    }

    @Override
    public AbstractRewardLogic spawnReward(int amount) {
        return new RewardLogic(amount);
    }

    public static class RewardLogic extends AbstractRewardLogic {

        public RewardLogic(int amount) {
            super(ICON, ID, UI_TEXT[0], REWARD);
            this.amount = amount;
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
