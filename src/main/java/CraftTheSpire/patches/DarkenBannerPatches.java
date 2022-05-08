package CraftTheSpire.patches;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class DarkenBannerPatches {
    @SpirePatch(clz= AbstractCard.class, method=SpirePatch.CLASS)
    public static class DarkenField {
        public static SpireField<Boolean> darken = new SpireField<>(() -> false);
    }

    public static final Color darkenColor = new Color(0.4f, 0.4f, 0.4f, 1.0f);

    @SpirePatch2(clz = AbstractCard.class, method = "renderPortraitFrame")
    @SpirePatch2(clz = AbstractCard.class, method = "renderBannerImage")
    public static class DarkenFrameAndBanner {
        private static final Color backupColor = Color.WHITE.cpy();
        @SpirePrefixPatch
        public static void darkenBefore(AbstractCard __instance, SpriteBatch sb, Color ___renderColor) {
            if (DarkenField.darken.get(__instance)) {
                backupColor.set(___renderColor);
                ___renderColor.mul(darkenColor);
            }
        }

        @SpirePostfixPatch
        public static void lightenAfter(AbstractCard __instance, SpriteBatch sb, Color ___renderColor) {
            if (DarkenField.darken.get(__instance)) {
                ___renderColor.set(backupColor);
            }
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "renderType")
    public static class LightenTypeText {
        private static final Color backupColor = Color.WHITE.cpy();
        @SpirePrefixPatch
        public static void lightenBefore(AbstractCard __instance, SpriteBatch sb, Color ___typeColor) {
            if (DarkenField.darken.get(__instance)) {
                backupColor.set(___typeColor);
                ___typeColor.mul(2.0f);
            }
        }

        @SpirePostfixPatch
        public static void darkenAfter(AbstractCard __instance, SpriteBatch sb, Color ___typeColor) {
            if (DarkenField.darken.get(__instance)) {
                ___typeColor.set(backupColor);
            }
        }
    }
}
