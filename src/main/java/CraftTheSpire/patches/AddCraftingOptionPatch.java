package CraftTheSpire.patches;

import CraftTheSpire.ui.CraftOption;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import javassist.CtBehavior;

import java.util.ArrayList;

public class AddCraftingOptionPatch {
    @SpirePatch(clz = CampfireUI.class, method = "initializeButtons")
    public static class AddButton {
        @SpireInsertPatch(locator = Locator.class)
        public static void plz(CampfireUI __instance, ArrayList<AbstractCampfireOption> ___buttons) {
            ___buttons.add(new CraftOption());
        }
    }

    public static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "relics");
            return LineFinder.findInOrder(ctBehavior, finalMatcher);
        }
    }
}
