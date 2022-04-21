package CraftTheSpire.ui;

import CraftTheSpire.CraftTheSpireMod;
import CraftTheSpire.util.InventoryManager;
import CraftTheSpire.util.TextureLoader;
import basemod.TopPanelItem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.TipHelper;

public class InventoryButton extends TopPanelItem {
    public static final Texture ICON = TextureLoader.getTexture(CraftTheSpireMod.makeUIPath("InventoryButton.png"));
    public static final String ID = CraftTheSpireMod.makeID("InventoryButton");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private static final float TIP_Y = Settings.HEIGHT - (120.0f * Settings.scale);
    private static final float TOP_RIGHT_TIP_X = 1550.0F * Settings.scale;
    private static final float FLASH_ANIM_TIME = 2.0F;
    public static float flashTimer;

    public InventoryButton() {
        super(ICON, ID);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        renderFlash(sb);
        if (hitbox.hovered) {
            TipHelper.renderGenericTip(Math.min(this.x, TOP_RIGHT_TIP_X), TIP_Y, TEXT[0], assembleString());
        }
    }

    public String assembleString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TEXT[1]);
        if (InventoryManager.hasAComponent()) {
            sb.append(TEXT[2]);
            for (String s : InventoryManager.items.keySet()) {
                if (InventoryManager.items.get(s) > 0) {
                    sb.append(" NL ").append(CraftTheSpireMod.componentMap.get(s).name).append(" x").append(InventoryManager.items.get(s));
                }
            }
        }
        return sb.toString();
    }

    @Override
    protected void onClick() {}

    public static void flash() {
        flashTimer = FLASH_ANIM_TIME;
    }

    @Override
    public void update() {
        super.update();
        updateFlash();
    }

    private void updateFlash() {
        if (flashTimer != 0.0F) {
            flashTimer -= Gdx.graphics.getDeltaTime();
        }
    }

    public void renderFlash(SpriteBatch sb) {
        float tmp = Interpolation.exp10In.apply(0.0F, 4.0F, flashTimer / FLASH_ANIM_TIME);
        sb.setBlendFunction(770, 1);
        sb.setColor(new Color(1.0F, 1.0F, 1.0F, flashTimer * FLASH_ANIM_TIME));

        float halfWidth = (float) this.image.getWidth() / 2.0F;
        float halfHeight = (float) this.image.getHeight() / 2.0F;
        sb.draw(this.image, this.x - halfWidth + halfHeight * Settings.scale, this.y - halfHeight + halfHeight * Settings.scale, halfWidth, halfHeight, (float) this.image.getWidth(), (float) this.image.getHeight(), Settings.scale + tmp, Settings.scale + tmp, this.angle, 0, 0, this.image.getWidth(), this.image.getHeight(), false, false);
        sb.draw(this.image, this.x - halfWidth + halfHeight * Settings.scale, this.y - halfHeight + halfHeight * Settings.scale, halfWidth, halfHeight, (float) this.image.getWidth(), (float) this.image.getHeight(), Settings.scale + tmp * 0.66F, Settings.scale + tmp * 0.66F, this.angle, 0, 0, this.image.getWidth(), this.image.getHeight(), false, false);
        sb.draw(this.image, this.x - halfWidth + halfHeight * Settings.scale, this.y - halfHeight + halfHeight * Settings.scale, halfWidth, halfHeight, (float) this.image.getWidth(), (float) this.image.getHeight(), Settings.scale + tmp / 3.0F, Settings.scale + tmp / 3.0F, this.angle, 0, 0, this.image.getWidth(), this.image.getHeight(), false, false);

        sb.setBlendFunction(770, 771);
    }
}
