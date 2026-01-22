package everythingnunchaku.mixin.bettersurvival;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mujmajnkraft.bettersurvival.client.ModClientHandler;
import com.mujmajnkraft.bettersurvival.items.ItemNunchaku;
import everythingnunchaku.handlers.ForgeConfigProvider;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModClientHandler.class)
public abstract class ModClientHandler_Mixin {

    @Definition(id = "ItemNunchaku", type = ItemNunchaku.class)
    @Expression("? instanceof ItemNunchaku")
    @ModifyExpressionValue(
            method = "onClientTick",
            at = @At("MIXINEXTRAS:EXPRESSION"),
            remap = false
    )
    private boolean everythingNunchaku_betterSurvivalModClientHandler_onClientTickAnyItem(boolean isNunchaku, @Local EntityPlayerSP player){
        return isNunchaku || ForgeConfigProvider.isClientNunchaku(player.getHeldItemMainhand().getItem());
    }
}