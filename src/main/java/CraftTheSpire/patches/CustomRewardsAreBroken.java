package CraftTheSpire.patches;

import CraftTheSpire.rewards.AbstractRewardLogic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import javassist.CtBehavior;

public class CustomRewardsAreBroken {
    @SpirePatch2(clz = RewardItem.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {})
    public static class StopFuckingUp {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<?> plz(RewardItem __instance) {
            if (__instance instanceof AbstractRewardLogic) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }

        public static class Locator extends SpireInsertLocator {

            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.MethodCallMatcher(AbstractDungeon.class, "getCurrRoom");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }
}
