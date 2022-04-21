package CraftTheSpire.patches;

import CraftTheSpire.screens.CraftingScreen;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
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

    @SpirePatch2(clz = AbstractDungeon.class, method = "openPreviousScreen")
    public static class ReOpenPlz {
        @SpirePrefixPatch()
        public static void updateTime(AbstractDungeon.CurrentScreen s) {
            if (s == Enums.CRAFT_SCREEN) {
                craftingScreen.reopen();
            }
        }
    }

    @SpirePatch2(clz = TopPanel.class, method = "updateSettingsButtonLogic")
    public static class GimmeMyScreenBack {
        @SpireInsertPatch(locator= UpdateLocator.class)
        public static void plz() {
            if (AbstractDungeon.screen == Enums.CRAFT_SCREEN) {
                AbstractDungeon.previousScreen = Enums.CRAFT_SCREEN;
            }
        }

        private static class UpdateLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "gridSelectScreen");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = TopPanel.class, method = "updateMapButtonLogic")
    public static class GimmeMyScreenBack2 {
        @SpireInsertPatch(locator= UpdateLocator.class)
        public static SpireReturn<?> plz() {
            if (AbstractDungeon.screen == Enums.CRAFT_SCREEN) {
                AbstractDungeon.previousScreen = Enums.CRAFT_SCREEN;
                AbstractDungeon.dungeonMapScreen.open(false);
                InputHelper.justClickedLeft = false;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }

        private static class UpdateLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(DungeonMapScreen.class, "dismissable");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = TopPanel.class, method = "updateDeckViewButtonLogic")
    public static class GimmeMyScreenBack3 {
        @SpireInsertPatch(locator= UpdateLocator.class)
        public static SpireReturn<?> plz() {
            if (AbstractDungeon.screen == Enums.CRAFT_SCREEN) {
                AbstractDungeon.previousScreen = Enums.CRAFT_SCREEN;
                AbstractDungeon.deckViewScreen.open();
                InputHelper.justClickedLeft = false;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }

        private static class UpdateLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardCrawlGame.class, "isPopupOpen");
                int[] r = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
                r[0]++;
                return r;
            }
        }
    }

    @SpirePatch2(clz = TopPanel.class, method = "updateMapButtonLogic")
    public static class EnableMapButton {
        @SpireInsertPatch(locator= UpdateLocator.class)
        public static void plz(TopPanel __instance) {
            if (AbstractDungeon.screen == Enums.CRAFT_SCREEN) {
                ReflectionHacks.setPrivate(__instance, TopPanel.class, "mapButtonDisabled", false);
                __instance.mapHb.update();
            }
        }

        private static class UpdateLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(InputHelper.class, "justClickedLeft");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = TopPanel.class, method = "updateDeckViewButtonLogic")
    public static class EnableDeckButton {
        @SpireInsertPatch(locator= ReEnableLocator.class)
        public static void plz(TopPanel __instance) {
            if (AbstractDungeon.screen == Enums.CRAFT_SCREEN) {
                ReflectionHacks.setPrivate(__instance, TopPanel.class, "deckButtonDisabled", false);
                __instance.deckHb.update();
            }
        }

        private static class ReEnableLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(InputHelper.class, "justClickedLeft");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
