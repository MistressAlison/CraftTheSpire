package CraftTheSpire.rewards;

import basemod.BaseMod;
import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.rewards.RewardSave;

public abstract class AbstractRewardLogic extends CustomReward implements BaseMod.LoadCustomReward, BaseMod.SaveCustomReward {
    public String rewardID;
    public int amount;

    public AbstractRewardLogic(Texture icon, String rewardID, String name, RewardType type) {
        super(icon, name, type);
        this.rewardID = rewardID;
    }

    @Override
    public RewardSave onSave(CustomReward customReward) {
        return new RewardSave(type.toString(), rewardID, amount, 0);
    }

}
