package CraftTheSpire.patches;

import CraftTheSpire.CraftTheSpireMod;
import CraftTheSpire.components.AbstractComponent;
import CraftTheSpire.rewards.AbstractRewardLogic;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.rewards.RewardItem;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AddRewardsPatch {
    @SpirePatch2(clz = AbstractMonster.class, method = "die", paramtypez = {boolean.class})
    public static class OnKillLad {
        @SpirePrefixPatch
        public static void addThingy(AbstractMonster __instance) {
            if (!__instance.isDying && !__instance.hasPower(MinionPower.POWER_ID)) {
                AbstractRewardLogic r = rollReward(__instance);
                if (r != null) {
                    for (RewardItem reward : AbstractDungeon.getCurrRoom().rewards) {
                        if (reward instanceof AbstractRewardLogic && ((AbstractRewardLogic) reward).rewardID.equals(r.rewardID)) {
                            ((AbstractRewardLogic) reward).stack(r.amount);
                            return;
                        }
                    }
                    AbstractDungeon.getCurrRoom().rewards.add(r);
                }
            }
        }
    }

    public static AbstractRewardLogic rollReward(AbstractMonster m) {
        if (AbstractDungeon.treasureRng.random(99) < CraftTheSpireMod.dropProbability) {
            float roll = AbstractDungeon.treasureRng.random(9);
            ArrayList<AbstractComponent> validComponents = CraftTheSpireMod.componentMap.values().stream().filter(comp -> comp.canDropOnKill(m)).collect(Collectors.toCollection(ArrayList::new));
            if (roll == 0 && !CraftTheSpireMod.rareComponents.isEmpty()) {
                validComponents.removeIf(c -> c.rarity != AbstractComponent.SpawnRarity.RARE);
            } else if (roll < 4 && !CraftTheSpireMod.uncommonComponents.isEmpty()) {
                validComponents.removeIf(c -> c.rarity != AbstractComponent.SpawnRarity.UNCOMMON);
            } else if (!CraftTheSpireMod.commonComponents.isEmpty()) {
                validComponents.removeIf(c -> c.rarity != AbstractComponent.SpawnRarity.COMMON);
            }
            if (!validComponents.isEmpty()) {
                return validComponents.get(AbstractDungeon.treasureRng.random(validComponents.size()-1)).spawnReward(1);
            }
        }
        return null;
    }
}
