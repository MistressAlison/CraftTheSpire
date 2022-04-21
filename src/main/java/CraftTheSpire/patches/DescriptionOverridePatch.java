package CraftTheSpire.patches;

import basemod.abstracts.CustomCard;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.RenderCardDescriptors;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.Handler;

import java.util.List;

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
