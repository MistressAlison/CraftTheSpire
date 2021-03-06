package CraftTheSpire.ui;

import CraftTheSpire.CraftTheSpireMod;
import CraftTheSpire.util.InventoryManager;
import CraftTheSpire.vfx.CampfireCraftEffect;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;

public class CraftOption extends AbstractCampfireOption {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(CraftTheSpireMod.makeID("CraftOption"));
    public static final String[] TEXT = uiStrings.TEXT;

    public CraftOption() {
        super();
        this.label = TEXT[0];
        this.description = TEXT[1];
        //this.img = ImageMaster.CAMPFIRE_RECALL_BUTTON;
        this.img = ImageMaster.loadImage("images/ui/campfire/meditate.png");
        updateUsability(InventoryManager.canCraft());
    }

    public void updateUsability(boolean canUse) {
        this.usable = canUse;
        this.description = canUse ? TEXT[1] : TEXT[2];// 20
    }

    public void onCraftCard() {
        this.usable = false;
        this.description = TEXT[3];
    }

    public void useOption() {
        if (usable) {
            AbstractDungeon.effectList.add(new CampfireCraftEffect());
        }
    }
}
