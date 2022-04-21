package CraftTheSpire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.rewards.RewardItem;

public class RewardTypeEnumPatches {
    @SpireEnum
    public static RewardItem.RewardType CTS_COMMON_REWARD; // Common Card
    @SpireEnum
    public static RewardItem.RewardType CTS_UNCOMMON_REWARD; //Uncommon Card
    @SpireEnum
    public static RewardItem.RewardType CTS_RARE_REWARD; // Rare Card
    @SpireEnum
    public static RewardItem.RewardType CTS_ATTACK_REWARD; // Attack Card
    @SpireEnum
    public static RewardItem.RewardType CTS_SKILL_REWARD; // Skill Card
    @SpireEnum
    public static RewardItem.RewardType CTS_POWER_REWARD; // Power Card
    @SpireEnum
    public static RewardItem.RewardType CTS_DUBIOUS_REWARD; // Who Knows :D
    @SpireEnum
    public static RewardItem.RewardType CTS_IRONCLAD_REWARD; // Iron Clad Card
    @SpireEnum
    public static RewardItem.RewardType CTS_SILENT_REWARD; // Silent Card
    @SpireEnum
    public static RewardItem.RewardType CTS_DEFECT_REWARD; // Defect Card
    @SpireEnum
    public static RewardItem.RewardType CTS_WATCHER_REWARD; // Watcher Card
    @SpireEnum
    public static RewardItem.RewardType CTS_LIGHT_REWARD; // 0 or 1 Cost Card
    @SpireEnum
    public static RewardItem.RewardType CTS_HEAVY_REWARD; // 2+ Cost Card
    @SpireEnum
    public static RewardItem.RewardType CTS_FORGE_REWARD; // Upgrade the card
    @SpireEnum
    public static RewardItem.RewardType CTS_MAGIC_REWARD; // Uses Magic Number
    @SpireEnum
    public static RewardItem.RewardType CTS_PLATED_REWARD; // Gives Block
}
