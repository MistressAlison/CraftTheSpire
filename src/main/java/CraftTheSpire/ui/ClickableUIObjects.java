package CraftTheSpire.ui;

import CraftTheSpire.components.AbstractComponent;
import CraftTheSpire.screens.CraftingScreen;
import CraftTheSpire.util.InventoryManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class ClickableUIObjects {

    public abstract static class UIObject {
        public boolean updateNeeded;
        public abstract void update();
        public abstract void render(SpriteBatch sb);
    }

    public static class UITickBox extends UIObject {
        public Hitbox hb;
        public boolean clicked;
        public float x, y;

        public UITickBox(float width, float height) {
            this(0, 0, width, height);
        }

        public UITickBox(float x, float y, float width, float height) {
            this.hb = new Hitbox(x, y, width, height);
        }

        public void move(float x, float y) {
            this.x = x;
            this.y = y;
            this.hb.move(x, y);
        }

        @Override
        public void update() {
            hb.update();
            if (hb.hovered && InputHelper.justClickedLeft) {
                clicked = !clicked;
            }
        }

        @Override
        public void render(SpriteBatch sb) {
            sb.draw(ImageMaster.OPTION_TOGGLE, this.x - 16.0F, this.y - 16.0F, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);
            if (clicked) {
                sb.draw(ImageMaster.OPTION_TOGGLE_ON, this.x - 16.0F, this.y - 16.0F, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);
            }
            hb.render(sb);
        }
    }

    public abstract static class UIText extends UIObject {
        private String label;
        private Hitbox hb;
        private float x, y;
        private static final float PADDING = 5f * Settings.scale;

        public UIText(String message, float x, float y) {
            this.label = message;
            this.x = x;
            this.y = y;
            hb = new Hitbox(x, y);
            scaleHitbox();
        }

        public abstract void onClick();

        protected void scaleHitbox() {
            hb.resize(FontHelper.getWidth(FontHelper.cardTitleFont, label, Settings.scale)+PADDING*2, FontHelper.getHeight(FontHelper.cardTitleFont, label, Settings.scale)+PADDING*2);
            hb.move(x, y);
        }

        @Override
        public void update() {
            hb.update();
            if (hb.hovered && InputHelper.justClickedLeft) {
                CardCrawlGame.sound.play("UI_CLICK_1");
                onClick();
            }
        }

        @Override
        public void render(SpriteBatch sb) {
            FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, label, x, y, hb.hovered ? Settings.GREEN_TEXT_COLOR : Settings.GOLD_COLOR, Settings.scale);
            hb.render(sb);
        }
    }

    public abstract static class UIComponentTickBox extends UIObject {
        public final AbstractComponent component;
        private final Color notSelectedColor;
        private final Color cantSelectColor;
        private final Hitbox hb;
        public boolean clicked;
        public boolean clickable;
        public float x, y;
        private static final float PAD = 32F * Settings.scale;
        private static final float HB_HEIGHT = 25F * Settings.scale;
        private int currentAmount;

        public UIComponentTickBox(AbstractComponent component) {
            this.component = component;
            this.notSelectedColor = component.getNameColor().cpy().mul(0.8F);
            this.cantSelectColor = notSelectedColor.cpy().mul(0.8F);
            this.hb = new Hitbox(getAssembledWidth(), HB_HEIGHT);
            this.currentAmount = InventoryManager.items.getOrDefault(component.ID, 0);
            checkClickable();
        }

        private float getAssembledWidth() {
            return PAD + FontHelper.getWidth(FontHelper.cardTitleFont, getAssembledName(), 1);
        }

        private String getAssembledName() {
            return component.name + " x" + (clicked ? currentAmount-1 : currentAmount);
        }

        public void move(float x, float y) {
            this.x = x;
            this.y = y;
            hb.move(x+hb.width/2F-PAD/2F, y+hb.height/2F-4F*Settings.scale);
        }

        public void checkClickable() {
            clickable = currentAmount > 0 && component.canSelect();
        }

        @Override
        public void update() {
            hb.update();
            if (!clickable) {
                if (clicked) {
                    clicked = false;
                    CraftingScreen.updateOnClicked();
                }
            } else if (hb.hovered && InputHelper.justClickedLeft) {
                CardCrawlGame.sound.play("UI_CLICK_1");
                clicked = !clicked;
                CraftingScreen.updateOnClicked();
                if (clicked) {
                   component.onSelectThisComponent();
                }
            }
        }

        @Override
        public void render(SpriteBatch sb) {
            FontHelper.cardTitleFont.getData().setScale(1F);
            FontHelper.renderFontLeftDownAligned(sb, FontHelper.cardTitleFont, getAssembledName(), x + PAD, y, clickable ? (hb.hovered ? component.getNameColor() : notSelectedColor) : cantSelectColor);
            if (clickable) {
                sb.draw(ImageMaster.OPTION_TOGGLE, this.x - 0.0F, this.y - 8F * Settings.scale, 0.0F, 0.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);
                if (clicked) {
                    sb.draw(ImageMaster.OPTION_TOGGLE_ON, this.x - 0.0F, this.y - 8F * Settings.scale, 0.0F, 0.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);
                }
            }
            hb.render(sb);
        }
    }

    /*public abstract static class PlusMinusLoadoutOption extends UIObject {
        CharacterLoadout loadout;
        private String label;
        private float x;
        private float y;
        private boolean canUp, canDown, hasUp, hasDown;
        private int amount;
        private int upgrades;
        private int points;
        private final Button lb;
        private final Button rb;
        private final CharacterSaveFile file;
        private final CharacterSaveFile.SaveDataEnum dataEnum;
        private final String baseText;

        public PlusMinusLoadoutOption(CharacterLoadout loadout, CharacterSaveFile file, CharacterSaveFile.SaveDataEnum dataEnum, String labelText, float x, float y) {
            this.x = x;
            this.y = y;
            this.loadout = loadout;
            this.file = file;
            this.dataEnum = dataEnum;
            this.baseText = labelText;
            lb = new Button(false, 0, 0);
            rb = new Button(true, 0, 0);
            readValues();
        }

        protected void readValues() {
            amount = file.getData(dataEnum);
            upgrades = amount/amountPerUpgrade();
            points = file.getData(CharacterSaveFile.SaveDataEnum.CURRENT_PERK_POINTS);
            makeLabel();
            canUp = canUpgrade();
            canDown = canDowngrade();
            hasUp = hasUpgrade();
            hasDown = hasDowngrade();
            lb.move(x-getAssembledWidth()/2f, y);
            rb.move(x+getAssembledWidth()/2f, y);
        }

        protected void saveValues() {
            file.setData(dataEnum, amount);
            file.setData(CharacterSaveFile.SaveDataEnum.CURRENT_PERK_POINTS, points);
        }

        protected void makeLabel() {
            label = baseText+": "+amount;
        }

        public int getUpgradeCost(int currentUpgrades) {
            return 1;
        }

        public int getDowngradeRefund(int currentUpgrades) {
            return 1;
        }

        public int amountPerUpgrade() {
            return 1;
        }

        public int maxUpgrades() {
            return -1;
        }

        public int minUpgrades() {
            return 0;
        }

        protected boolean hasUpgrade() {
            return (maxUpgrades() == -1 || upgrades < maxUpgrades());
        }

        protected boolean hasDowngrade() {
            return upgrades > minUpgrades();
        }

        protected boolean canUpgrade() {
            return points >= getUpgradeCost(upgrades) && hasUpgrade();
        }

        protected boolean canDowngrade() {
            return hasDowngrade();
        }

        protected void onClickArrow(boolean upgrade) {
            if (upgrade) {
                upgrade();
            } else {
                downgrade();
            }
        }

        protected void upgrade() {
            points -= getUpgradeCost(upgrades);
            amount += amountPerUpgrade();
            upgrades++;
            saveValues();
        }

        protected void downgrade() {
            points += getDowngradeRefund(upgrades);
            amount -= amountPerUpgrade();
            upgrades--;
            saveValues();
        }

        public void update() {
            lb.update();
            rb.update();
            if (updateNeeded) {
                updateNeeded = false;
                readValues();
            }
        }

        public void render(SpriteBatch sb) {
            lb.render(sb);
            FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, label, x, y, Settings.GOLD_COLOR, Settings.scale);
            rb.render(sb);
        }

        private float getAssembledWidth() {
            return lb.w + rb.w + FontHelper.getWidth(FontHelper.cardTitleFont, label, Settings.scale);
        }

        private class Button implements IUIElement {
            private final Texture arrow;
            private float x;
            private float y;
            private final int w;
            private final int h;
            private final Hitbox hitbox;
            private final boolean rightButton;
            private boolean enabled;
            private boolean clickable;

            public Button(boolean rightButton, float x, float y) {
                this.rightButton = rightButton;
                this.arrow = rightButton ? ImageMaster.CF_LEFT_ARROW : ImageMaster.CF_RIGHT_ARROW;
                this.x = x;
                this.y = y;
                this.w = (int)(Settings.scale * (float)this.arrow.getWidth() / 2.0F);
                this.h = (int)(Settings.scale * (float)this.arrow.getHeight() / 2.0F);
                this.hitbox = new Hitbox(x, y, (float)this.w, (float)this.h);
            }

            public void move(float newX, float newY) {
                this.x = (int)(newX - (float)this.w / 2.0F);
                this.y = (int)(newY - (float)this.h / 2.0F);
                this.hitbox.move(newX, newY);
            }

            public void render(SpriteBatch sb) {
                if (enabled) {
                    float halfW = (float)this.arrow.getWidth() / 2.0F;
                    float halfH = (float)this.arrow.getHeight() / 2.0F;
                    if (clickable) {
                        if (this.hitbox.hovered) {
                            sb.setColor(Color.WHITE);
                        } else {
                            sb.setColor(Color.LIGHT_GRAY);
                        }
                    } else {
                        sb.setColor(Color.GRAY);
                    }
                    if (this.hitbox.hovered) {
                        if (rightButton) {
                            sb.draw(PERK_IMAGE, x + 2*this.w - PERK_IMAGE.getRegionWidth()/2F, y + halfH/2f - PERK_IMAGE.getRegionHeight()/2F, PERK_IMAGE.getRegionWidth()/2F, PERK_IMAGE.getRegionHeight()/2F, PERK_IMAGE.getRegionWidth(), PERK_IMAGE.getRegionHeight(), Settings.scale/2f, Settings.scale/2f, 0.0F);
                            FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, String.valueOf(getUpgradeCost(amount)), x+3*this.w, y, Settings.GOLD_COLOR, Settings.scale);
                        } else {
                            sb.draw(PERK_IMAGE, x - this.w - PERK_IMAGE.getRegionWidth()/2F, y + halfH/2f - PERK_IMAGE.getRegionHeight()/2F, PERK_IMAGE.getRegionWidth()/2F, PERK_IMAGE.getRegionHeight()/2F, PERK_IMAGE.getRegionWidth(), PERK_IMAGE.getRegionHeight(), Settings.scale/2f, Settings.scale/2f, 0.0F);
                            FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, String.valueOf(getDowngradeRefund(amount)), x-2*this.w, y, Settings.GOLD_COLOR, Settings.scale);
                        }
                    }
                    sb.draw(this.arrow, this.x - halfW/2f, this.y - halfH/2f, halfW, halfH, (float)this.arrow.getWidth(), (float)this.arrow.getHeight(), Settings.scale, Settings.scale, 0.0F, 0, 0, this.arrow.getWidth(), this.arrow.getHeight(), true, false);
                    this.hitbox.render(sb);
                }
            }

            public void update() {
                enabled = rightButton ? hasUp : hasDown;
                clickable = rightButton ? canUp : canDown;
                this.hitbox.update();
                if (this.hitbox.hovered && InputHelper.justClickedLeft && clickable) {
                    CardCrawlGame.sound.play("UI_CLICK_1");
                    onClickArrow(rightButton);
                    loadout.setAllButtonsNeedUpdate();
                }
            }

            public int renderLayer() {
                return 0;
            }

            public int updateOrder() {
                return 0;
            }
        }
    }*/
}
