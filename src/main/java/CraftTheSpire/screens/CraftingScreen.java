package CraftTheSpire.screens;

import CraftTheSpire.CraftTheSpireMod;
import CraftTheSpire.components.AbstractComponent;
import CraftTheSpire.patches.DescriptionOverridePatch;
import CraftTheSpire.patches.NoCardDescriptorsPlz;
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
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import com.megacrit.cardcrawl.ui.buttons.CancelButton;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import javassist.CtBehavior;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CraftingScreen implements ScrollBarListener {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(CraftTheSpireMod.makeID("CraftScreen"));
    private static final UIStrings uiStrings2 = CardCrawlGame.languagePack.getUIString(CraftTheSpireMod.makeID("CraftScreenComponentContainers"));
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String[] CONTAINER_TEXT = uiStrings2.TEXT;
    private static final float SCROLL_BAR_THRESHOLD = 500.0F * Settings.scale;
    private static final float CONTAINER_START_X = Settings.WIDTH/12F;
    private static final float CONTAINER_START_Y = Settings.HEIGHT*5F/6F;
    private static final float EXAMPLE_CX = Settings.WIDTH*5/6F;
    private static final float EXAMPLE_Y = CONTAINER_START_Y;
    private static final float CARD_SCALE = 0.5F;
    private static final float CARD_SCALE_TARGET = 0.7F;
    private static final float CARD_DX = (AbstractCard.IMG_WIDTH * CARD_SCALE_TARGET)/2F + 10F * Settings.scale;
    private static final float CARD_DY = (AbstractCard.IMG_HEIGHT * CARD_SCALE_TARGET)/2F + 10F * Settings.scale;
    private static final float EXAMPLE_Y_DY = 40F * Settings.scale;
    private float grabStartY = 0.0F;
    private float currentDiffY = 0.0F;
    public static ArrayList<AbstractCard> createdCards = new ArrayList<>();
    public static ArrayList<ComponentContainer> containers = new ArrayList<>();
    public static ArrayList<AbstractCard> examplePreviews = new ArrayList<>();
    public static ComponentContainer RARITY, TYPE, EXTRA;
    private AbstractCard previewCard = null;
    private float scrollLowerBound;
    private float scrollUpperBound;
    private boolean grabbedScreen;
    public boolean confirmScreenUp;
    public GridSelectConfirmButton confirmButton;
    private String tipMsg;
    private String lastTip;
    private ScrollBar scrollBar;
    private static Random previewRandom = new Random();
    private static boolean generateNewExamples;

    public enum RarityFilter {
        COMMON(AbstractCard.CardRarity.COMMON),
        UNCOMMON(AbstractCard.CardRarity.UNCOMMON),
        RARE(AbstractCard.CardRarity.RARE),
        RANDOM(AbstractCard.CardRarity.COMMON); //Default to Common Skill, but obfuscated

        AbstractCard.CardRarity r;

        RarityFilter(AbstractCard.CardRarity r) {
            this.r = r;
        }

        public AbstractCard.CardRarity getRarity() {
            return r;
        }
    }

    public enum TypeFilter {
        ATTACK(AbstractCard.CardType.ATTACK),
        SKILL(AbstractCard.CardType.SKILL),
        POWER(AbstractCard.CardType.POWER),
        RANDOM(AbstractCard.CardType.SKILL); //Default to Common Skill, but obfuscated

        AbstractCard.CardType t;

        TypeFilter(AbstractCard.CardType t) {
            this.t = t;
        }

        public AbstractCard.CardType getType() {
            return t;
        }
    }

    public CraftingScreen() {
        this.scrollLowerBound = -Settings.DEFAULT_SCROLL_LIMIT;
        this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;
        this.grabbedScreen = false;
        this.confirmScreenUp = false;
        this.confirmButton = new GridSelectConfirmButton(TEXT[0]);
        this.tipMsg = "";
        this.lastTip = "";
        this.scrollBar = new ScrollBar(this);
        this.scrollBar.move(0.0F, -30.0F * Settings.scale);
    }

    public void update() {
        boolean isDraggingScrollBar = false;
        if (this.shouldShowScrollBar()) {
            isDraggingScrollBar = this.scrollBar.update();
        }

        if (!isDraggingScrollBar) {
            this.updateScrolling();
        }

        updateButtons();
        updatePreviewCard();
        updateExampleCards();
        this.confirmButton.isDisabled = examplePreviews.isEmpty();
        this.confirmButton.update();
        if (!this.confirmButton.isDisabled && this.confirmButton.hb.clicked) {
            this.confirmButton.hb.clicked = false;
            AbstractDungeon.overlayMenu.cancelButton.hide();
            generateCraftedCard();
            ReflectionHacks.RStaticMethod reset = ReflectionHacks.privateStaticMethod(AbstractDungeon.class, "genericScreenOverlayReset");
            reset.invoke();
            AbstractDungeon.closeCurrentScreen();
            if (AbstractDungeon.getCurrRoom() instanceof RestRoom) {
                RestRoom r = (RestRoom)AbstractDungeon.getCurrRoom();
                r.campfireUI.reopen();
            }
        }
    }

    public static void updateOnClicked() {
        for (ComponentContainer c : containers) {
            c.updateOnClicked();
        }
        if (hasMinimumCraftingRequirements()) {
            generateNewExamples = true;
        } else {
            examplePreviews.clear();
        }
    }

    public void updateButtons() {
        for (ComponentContainer c : containers) {
            c.update();
        }
    }

    public void updatePreviewCard() {
        previewCard.update();
        TypeFilter type = TYPE.getSelectedType();
        String current = previewCard.rawDescription;
        previewCard.rawDescription = getPreviewDescription("");
        if (!current.equals(previewCard.rawDescription)) {
            previewCard.initializeDescription();
        }
        DescriptionOverridePatch.DescriptionOverrideField.descriptionOverride.set(previewCard, !previewCard.rawDescription.equals(""));
        if (type == TypeFilter.RANDOM) {
            if (previewCard.type != AbstractCard.CardType.SKILL) {
                previewCard.type = AbstractCard.CardType.SKILL;
                previewCard.setLocked();
            }
            TypeOverridePatch.TypeOverrideField.typeOverride.set(previewCard, "? ? ?");
        } else {
            if (previewCard.type != type.getType()) {
                previewCard.type = type.getType();
                previewCard.setLocked();
            }
            TypeOverridePatch.TypeOverrideField.typeOverride.set(previewCard, null);
        }

        RarityFilter rarity = RARITY.getSelectedRarity();
        if (rarity == RarityFilter.RANDOM) {
            previewCard.rarity = AbstractCard.CardRarity.COMMON;
        } else {
            previewCard.rarity = rarity.getRarity();
        }
        if (generateNewExamples) {
            generateNewExamples = false;
            examplePreviews.clear();
            ArrayList<AbstractCard> cards = getValidCards();
            for (int i = 0 ; i < 4 ; i++) {
                if (!cards.isEmpty()) {
                    AbstractCard card = cards.get(previewRandom.random(cards.size() - 1));
                    cards.remove(card);
                    AbstractCard copy = card.makeStatEquivalentCopy();
                    setupExampleCard(copy);
                    modifyCreatedCard(copy);
                    examplePreviews.add(copy);
                }
            }
            if (examplePreviews.isEmpty()) {
                this.confirmButton.isDisabled = true;
            }
        }
    }

    public void setupExampleCard(AbstractCard card) {
        if (examplePreviews.isEmpty()) {
            scaleCard(card, CARD_SCALE, CARD_SCALE_TARGET);
            moveCard(card, EXAMPLE_CX - CARD_DX, EXAMPLE_Y - EXAMPLE_Y_DY - CARD_DY);
        } else if (examplePreviews.size() == 1) {
            scaleCard(card, CARD_SCALE, CARD_SCALE_TARGET);
            moveCard(card, EXAMPLE_CX + CARD_DX, EXAMPLE_Y - EXAMPLE_Y_DY - CARD_DY);
        } else if (examplePreviews.size() == 2) {
            scaleCard(card, CARD_SCALE, CARD_SCALE_TARGET);
            moveCard(card, EXAMPLE_CX - CARD_DX, EXAMPLE_Y - EXAMPLE_Y_DY - 3*CARD_DY);
        } else {
            scaleCard(card, CARD_SCALE, CARD_SCALE_TARGET);
            moveCard(card, EXAMPLE_CX + CARD_DX, EXAMPLE_Y - EXAMPLE_Y_DY - 3*CARD_DY);
        }
    }

    public void updateExampleCards() {
        for (AbstractCard c : examplePreviews) {
            c.update();
        }
    }

    public String getPreviewDescription(String desc) {
        return EXTRA.getPreviewDescription(TYPE.getPreviewDescription(RARITY.getPreviewDescription(desc)));
    }

    public void modifyCreatedCard(AbstractCard card) {
        RARITY.modifyCreatedCard(card);
        TYPE.modifyCreatedCard(card);
        EXTRA.modifyCreatedCard(card);
    }

    public void consumeComponents() {
        RARITY.consumeSelectedComponents();
        TYPE.consumeSelectedComponents();
        EXTRA.consumeSelectedComponents();
    }

    public void generateCraftedCard() {
        ArrayList<AbstractCard> cards = getValidCards();
        AbstractCard card = cards.get(AbstractDungeon.cardRandomRng.random(cards.size() - 1)).makeStatEquivalentCopy();
        modifyCreatedCard(card);
        createdCards.add(card);
        consumeComponents();
    }

    public static ArrayList<AbstractCard> getValidCards() {
        ArrayList<AbstractCard> validCards = new ArrayList<>();
        TypeFilter type = TYPE.getSelectedType();
        RarityFilter rarity = RARITY.getSelectedRarity();
        switch (rarity) {
            case COMMON:
                validCards.addAll(AbstractDungeon.srcCommonCardPool.group);
                break;
            case UNCOMMON:
                validCards.addAll(AbstractDungeon.srcUncommonCardPool.group);
                break;
            case RARE:
                validCards.addAll(AbstractDungeon.srcRareCardPool.group);
                break;
            case RANDOM:
                validCards.addAll(AbstractDungeon.srcCommonCardPool.group);
                validCards.addAll(AbstractDungeon.srcUncommonCardPool.group);
                validCards.addAll(AbstractDungeon.srcRareCardPool.group);
                break;
        }
        if (type != TypeFilter.RANDOM) {
            validCards.removeIf(c -> c.type != type.getType());
        }
        validCards = EXTRA.filterCardPool(TYPE.filterCardPool(RARITY.filterCardPool(validCards)));
        return validCards;
    }

    public static boolean hasMinimumCraftingRequirements() {
        return RARITY.hasOneComponentSelected() && TYPE.hasOneComponentSelected();
    }

    public void open(String msg) {
        this.tipMsg = msg;
        this.callOnOpen();
        this.calculateScrollBounds();
        this.previewCard = AbstractDungeon.commonCardPool.getRandomCard(false).makeStatEquivalentCopy();
        prepPreviewCard();
        prepContainers();
        this.confirmButton.hideInstantly();
        this.confirmButton.show();
        this.confirmButton.updateText(TEXT[0]);
        examplePreviews.clear();
    }

    public void prepPreviewCard() {
        previewCard.setLocked();
        previewCard.name = "? ? ?";
        NoCardDescriptorsPlz.NoDescriptorsField.cease.set(previewCard, true);
        previewCard.cost = -2;
        scaleCard(previewCard, 1F, 1.7F);
        moveCard(previewCard, Settings.WIDTH/2F, Settings.HEIGHT/2F);
    }

    public static void moveCard(AbstractCard card, float x, float y) {
        card.current_x = x;
        card.current_y = y;
        card.target_x = x;
        card.target_y = y;
    }

    public static void scaleCard(AbstractCard card, float current, float target) {
        card.drawScale = current;
        card.targetDrawScale = target;
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
        createdCards.clear();
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
        renderExampleCards(sb);
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

    public void renderExampleCards(SpriteBatch sb) {
        FontHelper.renderFontCentered(sb, FontHelper.charTitleFont, TEXT[4], EXAMPLE_CX, EXAMPLE_Y, Settings.CREAM_COLOR);
        for (AbstractCard c : examplePreviews) {
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

    public void reopen() {
        AbstractDungeon.overlayMenu.showBlackScreen(0.75F);
        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.screen = ScreenPatches.Enums.CRAFT_SCREEN;
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.overlayMenu.cancelButton.show(TEXT[1]);
        this.scrollBar.reset();
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