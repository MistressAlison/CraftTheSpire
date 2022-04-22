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
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardSave;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.NewExpr;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class MagicComponent extends AbstractComponent {
    public static final Texture ICON = TextureLoader.getTexture(CraftTheSpireMod.makeUIPath("MagicComponent.png"));
    public static final String ID = CraftTheSpireMod.makeID("MagicComponent");
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] UI_TEXT = uiStrings.TEXT;

    public static final SpawnRarity RARITY = SpawnRarity.COMMON;
    public static final ComponentType TYPE = ComponentType.EXTRA;
    public static final RewardItem.RewardType REWARD = RewardTypeEnumPatches.CTS_MAGIC_REWARD;


    public MagicComponent() {
        super(ID, UI_TEXT[0], RARITY, TYPE, ICON);
    }

    @Override
    public boolean canDropOnDisassemble(AbstractCard card) {
        return appliesDebuff(card);
    }

    @Override
    public ArrayList<AbstractCard> filterCards(ArrayList<AbstractCard> input) {
        input.removeIf(c -> !appliesDebuff(c));
        return input;
    }

    @Override
    public String modifyPreviewDescription(String desc) {
        return desc + UI_TEXT[2];
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

    public static boolean appliesDebuff(AbstractCard card) {
        //Set up some flags
        final boolean[] foundDebuff = {false};
        final boolean[] foundBuff = {false};
        final boolean[] isDebuff = {false};
        try {
            //Grab the use method
            ClassPool pool = Loader.getClassPool();
            CtMethod useMethod = pool.get(card.getClass().getName()).getDeclaredMethod("use");

            useMethod.instrument(new ExprEditor() {
                @Override
                public void edit(NewExpr n) {
                    try {
                        //Check if the new object extends AbstractPower
                        CtConstructor constructor = n.getConstructor();
                        CtClass originalClass = constructor.getDeclaringClass();

                        if (originalClass != null) {
                            CtClass currentClass = originalClass;
                            while (currentClass != null && !currentClass.getName().equals(AbstractPower.class.getName())) {
                                currentClass = currentClass.getSuperclass();
                            }
                            //We found AbstractPower, good to go
                            if (currentClass != null && currentClass.getName().equals(AbstractPower.class.getName())) {
                                //Define a checker for finding the power type
                                ExprEditor debuffChecker = new ExprEditor() {
                                    @Override
                                    public void edit(FieldAccess f) {
                                        if (f.getClassName().equals(AbstractPower.PowerType.class.getName())) {
                                            if (f.getFieldName().equals("DEBUFF")) {
                                                foundDebuff[0] = true;
                                            }
                                            if (f.getClassName().equals("BUFF")) {
                                                foundBuff[0] = true;
                                            }
                                        }
                                    }
                                };

                                //Check both the constructor and the updateDescription to catch things like Strength
                                constructor.instrument(debuffChecker);
                                CtMethod descriptionMethod = currentClass.getDeclaredMethod("updateDescription");
                                descriptionMethod.instrument(debuffChecker);

                                //If we actually found a debuff
                                if (foundDebuff[0]) {
                                    //Check if it also isn't a buff sometimes
                                    if (!foundBuff[0]) {
                                        isDebuff[0] = true;
                                    } else {
                                        //Guess based on the card target
                                        if (card.target == AbstractCard.CardTarget.ENEMY || card.target == AbstractCard.CardTarget.ALL_ENEMY) {
                                            isDebuff[0] = true;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception ignored) {}
                }
            });
        } catch (Exception ignored) {
            return false;
        }
        return isDebuff[0];
    }
}
