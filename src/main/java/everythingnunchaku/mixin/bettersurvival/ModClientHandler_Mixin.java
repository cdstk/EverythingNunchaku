package everythingnunchaku.mixin.bettersurvival;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mujmajnkraft.bettersurvival.client.ModClientHandler;
import com.mujmajnkraft.bettersurvival.items.ItemNunchaku;
import everythingnunchaku.handlers.ForgeConfigProvider;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
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

    @WrapOperation(
            method = "onClientTick",
            at = @At(value = "INVOKE", target = "Lcom/mujmajnkraft/bettersurvival/integration/RLCombatCompat;attackEntityFromClient(Lnet/minecraft/util/math/RayTraceResult;Lnet/minecraft/entity/player/EntityPlayer;)V"),
            remap = false
    )
    private void everythingNunchaku_betterSurvivalModClientHandler_onClientTickSwingRLCombat(RayTraceResult mov, EntityPlayer player, Operation<Void> original){
        original.call(mov, player);
        player.swingArm(EnumHand.MAIN_HAND);
    }

    @WrapOperation(
            method = "onClientTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;attackEntity(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;)V"),
            remap = false
    )
    private void everythingNunchaku_betterSurvivalModClientHandler_onClientTickSwingVanilla(PlayerControllerMP instance, EntityPlayer player, Entity entityHit, Operation<Void> original){
        original.call(instance, player, entityHit);
        player.swingArm(EnumHand.MAIN_HAND);
    }
}