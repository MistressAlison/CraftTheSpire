package CraftTheSpire.patches;

import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.RenderCardDescriptors;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import javassist.CtBehavior;

import java.util.List;

public class NoCardDescriptorsPlz {
    @SpirePatch(clz= AbstractCard.class, method=SpirePatch.CLASS)
    public static class NoDescriptorsField {
        public static SpireField<Boolean> cease = new SpireField<>(() -> false);
    }



    @SpirePatch2(clz = RenderCardDescriptors.FixDynamicFrame.class, method = "Prefix")
    public static class TeleportsBehindYou {
        @SpirePrefixPatch
        public static SpireReturn<?> nothingPersonnelKid(Object[] __args) {
            if (__args[0] instanceof AbstractCard && NoDescriptorsField.cease.get(__args[0])) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = RenderCardDescriptors.Text.class, method = "Insert")
    @SpirePatch2(clz = RenderCardDescriptors.Frame.class, method = "Insert")
    public static class NoDescriptor {
        @SpireInsertPatch(locator = Locator.class, localvars = {"descriptors"})
        public static void plz(List<String> descriptors) {
            descriptors.clear();
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.MethodCallMatcher(List.class, "size");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }


}
