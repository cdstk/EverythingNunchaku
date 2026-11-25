package everythingnunchaku.mixin.rlcombat;

import bettercombat.mod.client.handler.EventHandlersClient;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import everythingnunchaku.handlers.ForgeConfigProvider;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EventHandlersClient.class)
public abstract class EventHandlersClient_Mixin {

    @ModifyExpressionValue(
            method = "onMouseLeftClick",
            at = @At(value = "INVOKE", target = "Lbettercombat/mod/compat/BetterSurvivalHandler;isNunchaku(Lnet/minecraft/item/Item;)Z"),
            remap = false
    )
    private static boolean everythingNunchaku_rlCombatEventHandlersClient_onMouseLeftClickIsNunchakuAnything(boolean isNunchaku, @Local EntityPlayer player){
        if(!isNunchaku && ForgeConfigProvider.isClientNunchaku(player.getHeldItemMainhand().getItem())) {
            // Skip the other checks, latest RLCombat validates on server anyway
            EventHandlersClient.resetMainhandAnimationCooldown(player.getCooldownPeriod());
            EventHandlersClient.betterCombatMainhand.initiateAnimation();
            return true;
        }
        return isNunchaku;
    }
}
