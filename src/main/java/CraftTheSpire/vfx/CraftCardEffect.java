package CraftTheSpire.vfx;

import CraftTheSpire.screens.CraftingScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.UpgradeHammerImprintEffect;
import com.megacrit.cardcrawl.vfx.UpgradeShineParticleEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class CraftCardEffect extends ShowCardAndObtainEffect {
    private static final float EFFECT_DUR = 2.0F;
    private static final float CLANG_DELAY = 0.5F;
    private final AbstractCard obtainedCard;
    private final AbstractCard lockedCard;
    private static final float PADDING = 30.0F * Settings.scale;
    private int clangs = 0;

    public CraftCardEffect(AbstractCard obtainedCard, float x, float y) {
        super(obtainedCard, x, y, true);
        this.obtainedCard = obtainedCard;
        this.lockedCard = CraftingScreen.previewCard; //TODO make a proper copy of this otherwise when trying to make more than 1 card at once this will be very buggy looking
        this.duration = EFFECT_DUR;
        this.identifySpawnLocation(x, y);
        obtainedCard.drawScale = lockedCard.drawScale;
        obtainedCard.targetDrawScale = 1.0F;
        obtainedCard.transparency = 0.0F;
        obtainedCard.targetTransparency = 0.0F;
        lockedCard.targetDrawScale = 1.0F;
    }

    private void identifySpawnLocation(float x, float y) {
        int effectCount = 0;
        for (AbstractGameEffect e : AbstractDungeon.effectList) {
            if (e instanceof CraftCardEffect) {
                ++effectCount;
            }
        }

        this.obtainedCard.current_x = x;
        this.obtainedCard.current_y = y;
        this.obtainedCard.target_y = (float)Settings.HEIGHT * 0.5F;
        switch(effectCount) {
            case 0:
                this.obtainedCard.target_x = (float)Settings.WIDTH * 0.5F;
                break;
            case 1:
                this.obtainedCard.target_x = (float)Settings.WIDTH * 0.5F - PADDING - AbstractCard.IMG_WIDTH;
                break;
            case 2:
                this.obtainedCard.target_x = (float)Settings.WIDTH * 0.5F + PADDING + AbstractCard.IMG_WIDTH;
                break;
            case 3:
                this.obtainedCard.target_x = (float)Settings.WIDTH * 0.5F - (PADDING + AbstractCard.IMG_WIDTH) * 2.0F;
                break;
            case 4:
                this.obtainedCard.target_x = (float)Settings.WIDTH * 0.5F + (PADDING + AbstractCard.IMG_WIDTH) * 2.0F;
                break;
            default:
                this.obtainedCard.target_x = MathUtils.random((float)Settings.WIDTH * 0.1F, (float)Settings.WIDTH * 0.9F);
                this.obtainedCard.target_y = MathUtils.random((float)Settings.HEIGHT * 0.2F, (float)Settings.HEIGHT * 0.8F);
        }
    }

    @Override
    public void update() {
        super.update();
        lockedCard.update();
        if (this.duration < EFFECT_DUR - CLANG_DELAY && clangs == 0) {
            CardCrawlGame.sound.play("CARD_UPGRADE");
            clangs++;
            this.clank(obtainedCard.current_x - 80.0F * Settings.scale, obtainedCard.current_y + 0.0F * Settings.scale);
            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.SHORT, false);
            obtainedCard.transparency = obtainedCard.targetTransparency = 0.25F;
        }

        if (this.duration < EFFECT_DUR - CLANG_DELAY - 0.4F && clangs == 1) {
            clangs++;
            this.clank(obtainedCard.current_x  + 90.0F * Settings.scale, obtainedCard.current_y - 110.0F * Settings.scale);
            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.SHORT, false);
            obtainedCard.transparency = obtainedCard.targetTransparency = 0.5F;
        }

        if (this.duration < EFFECT_DUR - CLANG_DELAY - 0.6F && clangs == 2) {
            clangs++;
            this.clank(obtainedCard.current_x  + 30.0F * Settings.scale, obtainedCard.current_y + 120.0F * Settings.scale);
            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.SHORT, false);
            obtainedCard.transparency = obtainedCard.targetTransparency = 1.0F;
        }
    }

    private void clank(float x, float y) {
        AbstractDungeon.topLevelEffectsQueue.add(new UpgradeHammerImprintEffect(x, y));
        if (!Settings.DISABLE_EFFECTS) {
            for(int i = 0; i < 30; ++i) {
                AbstractDungeon.topLevelEffectsQueue.add(new UpgradeShineParticleEffect(x + MathUtils.random(-10.0F, 10.0F) * Settings.scale, y + MathUtils.random(-10.0F, 10.0F) * Settings.scale));
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        lockedCard.render(sb);
        if (this.duration < EFFECT_DUR - CLANG_DELAY) {
            obtainedCard.render(sb);
        }
    }
}
