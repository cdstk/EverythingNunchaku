package everythingnunchaku.mixin.rlcombat;

import bettercombat.mod.compat.BetterSurvivalHandler;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import everythingnunchaku.handlers.ForgeConfigProvider;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BetterSurvivalHandler.class)
public abstract class BetterSurvivalHandler_Mixin {

    @ModifyReturnValue(
            method = "isNunchaku",
            at = @At("RETURN"),
            remap = false
    )
    private static boolean everythingNunchaku_rlCombatBetterSurvivalHandler_isNunchakuAnything(boolean isNunchaku, Item item){
        return isNunchaku || ForgeConfigProvider.isClientNunchaku(item);
    }
}
