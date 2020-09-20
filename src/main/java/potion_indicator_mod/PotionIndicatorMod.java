package potion_indicator_mod;

import basemod.*;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.BloodPotion;
import com.megacrit.cardcrawl.potions.FairyPotion;

@SpireInitializer
public class PotionIndicatorMod implements PostInitializeSubscriber {

    public PotionIndicatorMod() {
        System.out.println("Potion Indicator Mod initialized");
    }

    public static void initialize() {
        BaseMod.subscribe(new PotionIndicatorMod());
    }

    @Override
    public void receivePostInitialize() {
    }

    @SpirePatch(
            clz = AbstractPotion.class,
            method = "render"
    )
    public static class RenderPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractPotion __instance, SpriteBatch sb) {
            if (__instance instanceof FairyPotion || __instance instanceof BloodPotion) {
                // Determine heal amount
                int healAmount = (int)(AbstractDungeon.player.maxHealth * __instance.getPotency()/100f);
                if (__instance instanceof FairyPotion && healAmount < 1) {
                    // The player must live, so at least 1 hp must be restored
                    healAmount = 1;
                }

                // Take overhealing into account
                int overheal = AbstractDungeon.player.currentHealth + healAmount - AbstractDungeon.player.maxHealth;
                if(overheal > 0){
                    healAmount -= overheal;
                }

                // Render
                Color color;
                if(healAmount == 0){
                    color = Color.WHITE;
                }else{
                    color = overheal > 0 ? Color.YELLOW : Color.GREEN;
                }
                float x = __instance.posX + __instance.hb.width / 4;
                float y = __instance.posY - __instance.hb.height / 2;
                String text = "+" + healAmount;
                FontHelper.renderFontCentered(sb, FontHelper.tipHeaderFont, text, x, y, color);
            }
        }
    }
}