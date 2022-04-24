package CraftTheSpire.ui;

import CraftTheSpire.components.AbstractComponent;
import CraftTheSpire.screens.CraftingScreen;
import CraftTheSpire.util.InventoryManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.ArrayList;
import java.util.Collections;

public class ComponentContainer {
    private float x, y;
    private String label;
    public ArrayList<ClickableUIObjects.UIComponentTickBox> components = new ArrayList<>();
    private static final float X_OFFSET = 10F * Settings.scale;
    private static final float Y_OFFSET = 40F * Settings.scale;
    private static final float CONTAINER_OFFSET = 50f * Settings.scale;

    public ComponentContainer(String label, float x, float y) {
        this.x = x;
        this.y = y;
        this.label = label;
    }

    public void addComponent(ClickableUIObjects.UIComponentTickBox componentTickBox) {
        components.add(componentTickBox);
    }

    public boolean hasOneComponentSelected() {
        boolean ret = false;
        for (ClickableUIObjects.UIComponentTickBox t : components) {
            if (t.clicked) {
                if (!ret) {
                    ret = true;
                } else {
                    return false;
                }
            }
        }
        return ret;
    }

    public float getLongestName() {
        float len = 0;
        for (int i = 0 ; i < components.size() ; i++) {
            if (i % 2 == 0 && components.get(i).getAssembledWidth() > len) {
                len = components.get(i).getAssembledWidth();
            }
        }
        return len;
    }

    public void arrangeComponents() {
        Collections.sort(components);
        float offset = getLongestName();
        float ox, oy;
        for (int i = 0 ; i < components.size() ; i++) {
            if (i % 2 == 0) {
                ox = x - X_OFFSET - offset;
            } else {
                ox = x + X_OFFSET;
            }
            oy = y - CONTAINER_OFFSET - Y_OFFSET * (i/2);
            components.get(i).move(ox, oy);
        }
    }

    public float getHeightOffset() {
        if (components.size() % 2 == 0) {
            return 2*CONTAINER_OFFSET + Y_OFFSET * components.size()/2;
        }
        return 2*CONTAINER_OFFSET + Y_OFFSET * (1+components.size()/2);
    }

    public void update() {
        for (ClickableUIObjects.UIComponentTickBox c : components) {
            c.update();
        }
    }

    public void updateOnClicked() {
        for (ClickableUIObjects.UIComponentTickBox c : components) {
            c.checkClickable();
        }
    }

    public void render(SpriteBatch sb) {
        FontHelper.renderFontCentered(sb, FontHelper.charTitleFont, label, x, y, Settings.CREAM_COLOR);
        for (ClickableUIObjects.UIComponentTickBox c : components) {
            c.render(sb);
        }
    }

    public CraftingScreen.RarityFilter getSelectedRarity() {
        for (ClickableUIObjects.UIComponentTickBox c : components) {
            if (c.clicked && c.component.type == AbstractComponent.ComponentType.RARITY_MOD) {
                return c.component.forceRarity();
            }
        }
        return CraftingScreen.RarityFilter.RANDOM;
    }

    public CraftingScreen.TypeFilter getSelectedType() {
        for (ClickableUIObjects.UIComponentTickBox c : components) {
            if (c.clicked && c.component.type == AbstractComponent.ComponentType.TYPE_MOD) {
                return c.component.forceType();
            }
        }
        return CraftingScreen.TypeFilter.RANDOM;
    }

    public ArrayList<AbstractCard> filterCardPool(ArrayList<AbstractCard> cardPool) {
        for (ClickableUIObjects.UIComponentTickBox c : components) {
            if (c.clicked) {
                cardPool = c.component.filterCards(cardPool);
            }
        }
        return cardPool;
    }

    public void modifyCreatedCard(AbstractCard card) {
        for (ClickableUIObjects.UIComponentTickBox c : components) {
            if (c.clicked) {
                c.component.modifyCreatedCard(card);
            }
        }
    }

    public void consumeSelectedComponents() {
        for (ClickableUIObjects.UIComponentTickBox c : components) {
            if (c.clicked) {
                InventoryManager.consumeComponent(c.component);
            }
        }
    }

    public String getPreviewDescription(String desc) {
        for (ClickableUIObjects.UIComponentTickBox c : components) {
            if (c.clicked) {
                desc = c.component.modifyPreviewDescription(desc);
            }
        }
        return desc;
    }
}
