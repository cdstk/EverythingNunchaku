package everythingnunchaku.mixin.rlcombat;

import bettercombat.mod.client.handler.EventHandlersClient;
import com.llamalad7.mixinextras.sugar.Local;
import everythingnunchaku.compat.ModLoadedUtil;
import everythingnunchaku.handlers.ForgeConfigProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.MouseEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EventHandlersClient.class)
public abstract class EventHandlersClient_OldRLCombatMixin {

    @Inject(
            method = "onMouseLeftClick",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/event/MouseEvent;setCanceled(Z)V"),
            remap = false,
            cancellable = true
    )
    private static void everythingNunchaku_rlCombatEventHandlersClient_onMouseLeftClickOldRLCombat(MouseEvent event, CallbackInfo ci, @Local EntityPlayer player){
        if(!ModLoadedUtil.versionInRange(ModLoadedUtil.rlCombat, ModLoadedUtil.RLCOMBAT_VERSION) && ForgeConfigProvider.isClientNunchaku(player.getHeldItemMainhand().getItem())) ci.cancel();
    }
}
