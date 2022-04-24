package CraftTheSpire.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnCraftRelic {
    void onCraft();
    void modifyCraftedCard(AbstractCard card);
}
