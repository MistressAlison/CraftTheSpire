package CraftTheSpire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class DescriptionOverridePatch {

    @SpirePatch(clz= AbstractCard.class, method=SpirePatch.CLASS)
    public static class DescriptionOverrideField {
        public static SpireField<Boolean> descriptionOverride = new SpireField<>(() -> false);
    }

    @SpirePatch2(clz = AbstractCard.class, method = "renderDescription")
    public static class RenderMyDescriptionAnyway {
        @SpireInstrumentPatch
        public static ExprEditor patch() {
            return new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.getFieldName().equals("isLocked") && f.getClassName().equals(AbstractCard.class.getName())) {
                        f.replace("$_ = CraftTheSpire.patches.DescriptionOverridePatch.actuallyLock(this, $proceed($$));");
                    }
                }
            };
        }
    }

    public static boolean actuallyLock(AbstractCard card, boolean locked) {
        return locked && !DescriptionOverrideField.descriptionOverride.get(card);
    }

}
