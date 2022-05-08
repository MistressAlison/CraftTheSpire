package CraftTheSpire.vfx;

import CraftTheSpire.CraftTheSpireMod;
import CraftTheSpire.patches.ScreenPatches;
import CraftTheSpire.relics.OnCraftRelic;
import CraftTheSpire.screens.CraftingScreen;
import CraftTheSpire.ui.CraftOption;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;

public class CampfireCraftEffect extends AbstractGameEffect {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(CraftTheSpireMod.makeID("CampfireCraftEffect"));
    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DUR = 1.5F;
    private boolean openedScreen = false;
    private final Color screenColor;

    public CampfireCraftEffect() {
        this.screenColor = AbstractDungeon.fadeColor.cpy();
        this.duration = DUR;
        this.screenColor.a = 0.0F;
        AbstractDungeon.overlayMenu.proceedButton.hide();
    }

    @Override
    public void update() {
        if (!AbstractDungeon.isScreenUp) {
            this.duration -= Gdx.graphics.getDeltaTime();
            this.updateBlackScreenColor();
        }


        if (!AbstractDungeon.isScreenUp && !CraftingScreen.createdCards.isEmpty()) {
            for (AbstractCard c : CraftingScreen.createdCards) {
                //++CardCrawlGame.metricData.campfire_upgraded;// 55
                //CardCrawlGame.metricData.addCampfireChoiceData("SMITH", c.getMetricID());// 56
                AbstractDungeon.effectsQueue.add(new CraftCardEffect(c, Settings.WIDTH/2F, Settings.HEIGHT/2F));// 59
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r instanceof OnCraftRelic) {
                    ((OnCraftRelic) r).onCraft();
                }
            }

            CraftingScreen.createdCards.clear();
            ((RestRoom)AbstractDungeon.getCurrRoom()).fadeIn();
            for (AbstractCampfireOption o : ReflectionHacks.<ArrayList<AbstractCampfireOption>>getPrivate(((RestRoom)AbstractDungeon.getCurrRoom()).campfireUI, CampfireUI.class, "buttons")) {
                if (o instanceof CraftOption) {
                    ((CraftOption) o).onCraftCard();
                }
            }
        }

        if (this.duration < 1.0F && !this.openedScreen) {
            this.openedScreen = true;
            ScreenPatches.craftingScreen.open(TEXT[0]);
            /*for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r instanceof OnCraftRelic) {
                    ((OnCraftRelic) r).onCraft();
                }
            }*/
        }

        if (this.duration < 0.0F) {
            this.isDone = true;
            if (CampfireUI.hidden) {
                AbstractRoom.waitTimer = 0.0F;
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                ((RestRoom)AbstractDungeon.getCurrRoom()).cutFireSound();
            }
        }
    }

    private void updateBlackScreenColor() {
        if (this.duration > 1.0F) {
            this.screenColor.a = Interpolation.fade.apply(1.0F, 0.0F, (this.duration - 1.0F) * 2.0F);
        } else {
            this.screenColor.a = Interpolation.fade.apply(0.0F, 1.0F, this.duration / 1.5F);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(this.screenColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float)Settings.WIDTH, (float)Settings.HEIGHT);
        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID) {
            AbstractDungeon.gridSelectScreen.render(sb);
        }
    }

    @Override
    public void dispose() {}
}
