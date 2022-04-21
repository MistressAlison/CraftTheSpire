package CraftTheSpire.patches;

import CraftTheSpire.CraftTheSpireMod;
import CraftTheSpire.components.AbstractComponent;
import CraftTheSpire.util.InventoryManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class RemoveMasterDeckPatch {
    @SpirePatch2(clz = CardGroup.class, method = "removeCard", paramtypez = {AbstractCard.class})
    public static class FreeComponentsTime {
        @SpireInsertPatch(locator = Locator.class)
        public static void plz(AbstractCard c) {
            if (CraftTheSpireMod.dropOnMasterDeckRemoval) {
                ArrayList<AbstractComponent> validComponents = CraftTheSpireMod.componentMap.values().stream().filter(comp -> comp.canDropOnDisassemble(c)).collect(Collectors.toCollection(ArrayList::new));
                if (!validComponents.isEmpty()) {
                    InventoryManager.addComponent(validComponents.get(AbstractDungeon.treasureRng.random(validComponents.size()-1)), 1);
                }
            }
        }
    }

    public static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher m = new Matcher.MethodCallMatcher(AbstractCard.class, "onRemoveFromMasterDeck");
            return LineFinder.findInOrder(ctBehavior, m);
        }
    }
}
