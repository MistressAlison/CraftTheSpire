package CraftTheSpire.patches;

import CraftTheSpire.CraftTheSpireMod;
import CraftTheSpire.rewards.AbstractRewardLogic;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.rewards.RewardItem;

public class AddRewardsPatch {
    public static final int DROP_PERCENT = 35;
    @SpirePatch2(clz = AbstractMonster.class, method = "die", paramtypez = {boolean.class})
    public static class OnKillLad {
        @SpirePrefixPatch
        public static void addThingy(AbstractMonster __instance) {
            if (!__instance.isDying && !__instance.hasPower(MinionPower.POWER_ID)) {
                AbstractRewardLogic r = rollReward();
                if (r != null) {
                    for (RewardItem reward : AbstractDungeon.getCurrRoom().rewards) {
                        if (reward instanceof AbstractRewardLogic && ((AbstractRewardLogic) reward).rewardID.equals(r.rewardID)) {
                            ((AbstractRewardLogic) reward).amount += r.amount;
                            return;
                        }
                    }
                    AbstractDungeon.getCurrRoom().rewards.add(r);
                }
            }
        }
    }

    public static AbstractRewardLogic rollReward() {
        if (AbstractDungeon.treasureRng.random(99) < DROP_PERCENT) {
            float roll = AbstractDungeon.treasureRng.random(9);
            AbstractRewardLogic r = null;
            if (roll == 0 && !CraftTheSpireMod.rareComponents.isEmpty()) {
                r = CraftTheSpireMod.rareComponents.get(AbstractDungeon.treasureRng.random(CraftTheSpireMod.rareComponents.size()-1)).spawnReward(1);
            } else if (roll < 4 && !CraftTheSpireMod.uncommonComponents.isEmpty()) {
                r = CraftTheSpireMod.uncommonComponents.get(AbstractDungeon.treasureRng.random(CraftTheSpireMod.uncommonComponents.size()-1)).spawnReward(1);
            } else if (!CraftTheSpireMod.commonComponents.isEmpty()) {
                r = CraftTheSpireMod.commonComponents.get(AbstractDungeon.treasureRng.random(CraftTheSpireMod.commonComponents.size()-1)).spawnReward(1);
            }
            return r;
        }
        return null;
    }
}
