package CraftTheSpire.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.ArrayList;

public class ComponentContainer {
    private float x, y;
    private String label;
    public ArrayList<ClickableUIObjects.UIComponentTickBox> components = new ArrayList<>();
    private static final float X_OFFSET = 80F * Settings.scale;
    private static final float Y_OFFSET = 40F * Settings.scale;
    private static final float CONTAINER_OFFSET = 50f * Settings.scale;

    public ComponentContainer(String label, float x, float y) {
        this.x = x;
        this.y = y;
        this.label = label;
    }

    public void addComponent(ClickableUIObjects.UIComponentTickBox componentTickBox) {
        float xo = x + X_OFFSET;
        float yo = y - CONTAINER_OFFSET - Y_OFFSET * components.size();
        componentTickBox.move(xo, yo);
        components.add(componentTickBox);
    }

    public float getHeightOffset() {
        return 2*CONTAINER_OFFSET + Y_OFFSET * components.size();
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
        FontHelper.renderFontLeftDownAligned(sb, FontHelper.charTitleFont, label, x, y, Settings.CREAM_COLOR);
        for (ClickableUIObjects.UIComponentTickBox c : components) {
            c.render(sb);
        }
    }
}
