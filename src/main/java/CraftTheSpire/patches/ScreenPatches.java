package CraftTheSpire.patches;

import CraftTheSpire.screens.CraftingScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;

public class ScreenPatches {
    public static class Enums {
        @SpireEnum
        public static AbstractDungeon.CurrentScreen CRAFT_SCREEN;
    }

    public static CraftingScreen craftingScreen = new CraftingScreen();

    @SpirePatch2(clz = AbstractDungeon.class, method = "update")
    public static class UpdateCraftingScreen {
        @SpireInsertPatch(locator= UpdateLocator.class)
        public static void updateTime() {
            if (AbstractDungeon.screen == Enums.CRAFT_SCREEN) {
                craftingScreen.update();
            }
        }

        private static class UpdateLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "topLevelEffects");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = AbstractDungeon.class, method = "render")
    public static class RenderCraftingScreen {
        @SpireInsertPatch(locator= RenderLocator.class)
        public static void renderTime(SpriteBatch sb) {
            if (AbstractDungeon.screen == Enums.CRAFT_SCREEN) {
                craftingScreen.render(sb);
            }
        }

        private static class RenderLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(OverlayMenu.class, "renderBlackScreen");
                int[] r = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
                r[0]++;
                return r;
            }
        }
    }
}
