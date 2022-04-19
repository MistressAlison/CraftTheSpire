package CraftTheSpire.screens;

import CraftTheSpire.CraftTheSpireMod;
import CraftTheSpire.components.AbstractComponent;
import CraftTheSpire.patches.ScreenPatches;
import CraftTheSpire.patches.TypeOverridePatch;
import CraftTheSpire.ui.ClickableUIObjects;
import CraftTheSpire.ui.ComponentContainer;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import com.megacrit.cardcrawl.ui.buttons.CancelButton;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import javassist.CtBehavior;

import java.util.ArrayList;

public class CraftingScreen implements ScrollBarListener {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(CraftTheSpireMod.makeID("CraftScreen"));
    private static final UIStrings uiStrings2 = CardCrawlGame.languagePack.getUIString(CraftTheSpireMod.makeID("CraftScreenComponentContainers"));
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String[] CONTAINER_TEXT = uiStrings2.TEXT;
    private static final float SCROLL_BAR_THRESHOLD = 500.0F * Settings.scale;
    private final float CONTAINER_START_X = Settings.WIDTH/12F;
    private final float CONTAINER_START_Y = Settings.HEIGHT*5F/6F;
    private float grabStartY = 0.0F;
    private float currentDiffY = 0.0F;
    public static ArrayList<AbstractCard> createdCards = new ArrayList<>();
    public static ArrayList<ComponentContainer> containers = new ArrayList<>();
    public static ComponentContainer RARITY, TYPE, EXTRA;
    private AbstractCard previewCard = null;
    public static AbstractCard.CardType previewType;
    public static AbstractCard.CardRarity previewRarity;
    private float scrollLowerBound;
    private float scrollUpperBound;
    private boolean grabbedScreen;
    public boolean confirmScreenUp;
    public GridSelectConfirmButton confirmButton;
    private String tipMsg;
    private String lastTip;
    private ScrollBar scrollBar;

    public CraftingScreen() {
        this.scrollLowerBound = -Settings.DEFAULT_SCROLL_LIMIT;// 48
        this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;// 49
        this.grabbedScreen = false;// 50
        this.confirmScreenUp = false;
        this.confirmButton = new GridSelectConfirmButton(TEXT[0]);// 53
        this.tipMsg = "";// 55
        this.lastTip = "";// 56
        this.scrollBar = new ScrollBar(this);// 78
        this.scrollBar.move(0.0F, -30.0F * Settings.scale);// 79
    }// 80

    public void update() {
        boolean isDraggingScrollBar = false;
        if (this.shouldShowScrollBar()) {
            isDraggingScrollBar = this.scrollBar.update();
        }

        if (!isDraggingScrollBar) {
            this.updateScrolling();
        }

        this.confirmButton.isDisabled = !canCreateCard();
        this.confirmButton.update();
        if (!this.confirmScreenUp) {
            updateButtons();
            updatePreviewCard();
            if (!this.confirmButton.isDisabled && this.confirmButton.hb.clicked) {
                this.confirmScreenUp = true;
                AbstractDungeon.overlayMenu.cancelButton.show(TEXT[1]);
                this.confirmButton.show();
                this.confirmButton.isDisabled = false;
                this.lastTip = this.tipMsg;
                this.tipMsg = TEXT[2];
            }
        } else {
            if (this.confirmButton.hb.clicked) {
                this.confirmButton.hb.clicked = false;
                AbstractDungeon.overlayMenu.cancelButton.hide();
                this.confirmScreenUp = false;
                this.createdCards.add(this.previewCard);
                AbstractDungeon.closeCurrentScreen();
            }
        }
    }

    public static void updateOnClicked() {
        for (ComponentContainer c : containers) {
            c.updateOnClicked();
        }
    }

    public void updateButtons() {
        for (ComponentContainer c : containers) {
            c.update();
        }
    }

    public void updatePreviewCard() {
        previewCard.update();
        if (TYPE.components.stream().noneMatch(c -> c.clicked)) {
            if (previewCard.type != AbstractCard.CardType.SKILL) {
                previewCard.type = AbstractCard.CardType.SKILL;
                previewCard.setLocked();
            }
            TypeOverridePatch.TypeOverrideField.typeOverride.set(previewCard, "? ? ?");
        } else if (previewType != null) {
            if (previewCard.type != previewType) {
                previewCard.type = previewType;
                previewCard.setLocked();
            }
            TypeOverridePatch.TypeOverrideField.typeOverride.set(previewCard, null);
        }

        if (RARITY.components.stream().noneMatch(c -> c.clicked)) {
            previewCard.rarity = AbstractCard.CardRarity.COMMON;
        } else if (previewRarity != null) {
            previewCard.rarity = previewRarity;
        }
    }

    public boolean canCreateCard() {
        return true;
    }

    public void open(String msg) {
        this.tipMsg = msg;
        this.callOnOpen();
        this.calculateScrollBounds();
        this.previewCard = AbstractDungeon.commonCardPool.getRandomCard(false);
        prepPreviewCard();
        prepContainers();
        this.confirmButton.hideInstantly();
        this.confirmButton.show();
        this.confirmButton.updateText(TEXT[0]);
    }

    public void prepPreviewCard() {
        previewCard.setLocked();
        previewCard.name = "? ? ?";
        previewCard.cost = -2;
        previewCard.drawScale = 1F;
        previewCard.targetDrawScale = 1.7F;
        previewCard.current_x = Settings.WIDTH/2F;
        previewCard.current_y = Settings.HEIGHT/2F;
        previewCard.target_x = previewCard.current_x;
        previewCard.target_y = previewCard.current_y;
    }

    public void prepContainers() {
        containers.clear();
        float offset = 0f;
        RARITY = new ComponentContainer(CONTAINER_TEXT[0], CONTAINER_START_X, CONTAINER_START_Y + offset);
        for (AbstractComponent c : CraftTheSpireMod.componentMap.values()) {
            if (c.type == AbstractComponent.ComponentType.RARITY_MOD) {
                RARITY.addComponent(new ClickableUIObjects.UIComponentTickBox(c) {});
            }
        }
        offset += RARITY.getHeightOffset();

        TYPE = new ComponentContainer(CONTAINER_TEXT[1], CONTAINER_START_X, CONTAINER_START_Y - offset);
        for (AbstractComponent c : CraftTheSpireMod.componentMap.values()) {
            if (c.type == AbstractComponent.ComponentType.TYPE_MOD) {
                TYPE.addComponent(new ClickableUIObjects.UIComponentTickBox(c) {});
            }
        }
        offset += TYPE.getHeightOffset();

        EXTRA = new ComponentContainer(CONTAINER_TEXT[2], CONTAINER_START_X, CONTAINER_START_Y - offset);
        for (AbstractComponent c : CraftTheSpireMod.componentMap.values()) {
            if (c.type == AbstractComponent.ComponentType.EXTRA) {
                EXTRA.addComponent(new ClickableUIObjects.UIComponentTickBox(c) {});
            }
        }
        offset += EXTRA.getHeightOffset();

        containers.add(RARITY);
        containers.add(TYPE);
        containers.add(EXTRA);
    }

    private void callOnOpen() {
        this.confirmScreenUp = false;
        AbstractDungeon.overlayMenu.proceedButton.hide();
        this.createdCards.clear();
        AbstractDungeon.topPanel.unhoverHitboxes();
        this.currentDiffY = 0.0F;
        this.grabStartY = 0.0F;
        this.grabbedScreen = false;
        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.screen = ScreenPatches.Enums.CRAFT_SCREEN;
        AbstractDungeon.overlayMenu.showBlackScreen(0.75F);
        this.confirmButton.hideInstantly();
        AbstractDungeon.overlayMenu.cancelButton.show(TEXT[1]);
    }

    private void updateScrolling() {
        int y = InputHelper.mY;
        boolean isDraggingScrollBar = this.scrollBar.update();
        if (!isDraggingScrollBar) {
            if (!this.grabbedScreen) {
                if (InputHelper.scrolledDown) {
                    this.currentDiffY += Settings.SCROLL_SPEED;
                } else if (InputHelper.scrolledUp) {
                    this.currentDiffY -= Settings.SCROLL_SPEED;
                }

                if (InputHelper.justClickedLeft) {
                    this.grabbedScreen = true;
                    this.grabStartY = (float)y - this.currentDiffY;
                }
            } else if (InputHelper.isMouseDown) {
                this.currentDiffY = (float)y - this.grabStartY;
            } else {
                this.grabbedScreen = false;
            }
        }

        this.calculateScrollBounds();
        this.resetScrolling();
        this.updateBarPosition();
    }

    private void calculateScrollBounds() {
        this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;
    }

    private void resetScrolling() {
        if (this.currentDiffY < this.scrollLowerBound) {
            this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollLowerBound);
        } else if (this.currentDiffY > this.scrollUpperBound) {
            this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollUpperBound);
        }
    }

    public void cancelCrafting() {
        this.confirmScreenUp = false;
        this.confirmButton.hide();
        this.confirmButton.isDisabled = true;
        AbstractDungeon.overlayMenu.cancelButton.show(TEXT[1]);
        this.tipMsg = this.lastTip;
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        if (this.shouldShowScrollBar()) {// 747
            this.scrollBar.render(sb);// 748
        }
        renderButtons(sb);
        this.previewCard.render(sb);
        if (this.confirmScreenUp) {// 808
            sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.8F));// 809
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float)Settings.WIDTH, (float)Settings.HEIGHT - 64.0F * Settings.scale);// 810
            // 812
            this.previewCard.current_x = (float)Settings.WIDTH / 2.0F;// 834
            this.previewCard.current_y = (float)Settings.HEIGHT / 2.0F;// 835
            this.previewCard.render(sb);// 836
            //this.previewCard.updateHoverLogic();// 837
        }

        this.confirmButton.render(sb);

        FontHelper.renderDeckViewTip(sb, this.tipMsg, 96.0F * Settings.scale, Settings.CREAM_COLOR);

    }

    public void renderButtons(SpriteBatch sb) {
        for (ComponentContainer c : containers) {
            c.render(sb);
        }
    }

    public void scrolledUsingBar(float newPercent) {
        this.currentDiffY = MathHelper.valueFromPercentBetween(this.scrollLowerBound, this.scrollUpperBound, newPercent);// 924
        this.updateBarPosition();// 925
    }// 926

    private void updateBarPosition() {
        float percent = MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound, this.currentDiffY);// 929
        this.scrollBar.parentScrolledToPercent(percent);// 930
    }// 931

    private boolean shouldShowScrollBar() {
        return !this.confirmScreenUp && this.scrollUpperBound > SCROLL_BAR_THRESHOLD;// 934
    }

    @SpirePatch2(clz = CancelButton.class, method = "update")
    public static class CancelPlz {
        @SpireInsertPatch(locator = Locator.class)
        public static void plz() {
            if (AbstractDungeon.screen == ScreenPatches.Enums.CRAFT_SCREEN) {
                if (!ScreenPatches.craftingScreen.confirmScreenUp) {
                    ReflectionHacks.RStaticMethod reset = ReflectionHacks.privateStaticMethod(AbstractDungeon.class, "genericScreenOverlayReset");
                    reset.invoke();
                    AbstractDungeon.closeCurrentScreen();
                    if (AbstractDungeon.getCurrRoom() instanceof RestRoom) {
                        RestRoom r = (RestRoom)AbstractDungeon.getCurrRoom();
                        r.campfireUI.reopen();
                    }
                    return;
                }
                ScreenPatches.craftingScreen.cancelCrafting();
            }
        }

        public static class Locator extends SpireInsertLocator {

            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "screen");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }
}