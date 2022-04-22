package CraftTheSpire.rewards;

import basemod.BaseMod;
import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.rewards.RewardSave;

public abstract class AbstractRewardLogic extends CustomReward implements BaseMod.LoadCustomReward, BaseMod.SaveCustomReward {
    public String rewardID;
    public int amount;
    public String baseName;

    public AbstractRewardLogic(Texture icon, String rewardID, String name, RewardType type, int amount) {
        super(icon, name, type);
        this.baseName = name;
        this.rewardID = rewardID;
        this.amount = amount;
        updateName();
    }

    public void stack(int amount) {
        this.amount += amount;
        updateName();
    }

    public void updateName() {
        this.text = this.amount == 1 ? this.baseName : this.amount + " " + this.baseName;
    }

    @Override
    public RewardSave onSave(CustomReward customReward) {
        return new RewardSave(type.toString(), rewardID, ((AbstractRewardLogic)customReward).amount, 0);
    }

}
