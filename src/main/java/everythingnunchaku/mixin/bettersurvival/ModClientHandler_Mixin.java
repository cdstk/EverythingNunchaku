package everythingnunchaku.mixin.bettersurvival;

import bettercombat.mod.util.ConfigurationHandler;
import bettercombat.mod.util.ReachFixUtil;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mujmajnkraft.bettersurvival.client.ModClientHandler;
import com.mujmajnkraft.bettersurvival.items.ItemNunchaku;
import everythingnunchaku.compat.ModLoadedUtil;
import everythingnunchaku.compat.client.RLCombatHandler;
import everythingnunchaku.handlers.ForgeConfigProvider;
import net.minecraft.client.Minecraft;
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

    @ModifyExpressionValue(
            method = "onClientTick",
            at = @At(value = "INVOKE", target = "Lmeldexun/reachfix/hook/client/EntityRendererHook;pointedObject(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;Lnet/minecraft/world/World;F)Lnet/minecraft/util/math/RayTraceResult;"),
            remap = false
    )
    private RayTraceResult everythingNunchaku_betterSurvivalModClientHandler_onClientTickSwingThroughPassable(RayTraceResult mov, @Local Minecraft mc, @Local Entity rvEntity, @Local EntityPlayerSP player){
        if(ModLoadedUtil.rlCombat.isLoaded() && ModLoadedUtil.versionInRange(ModLoadedUtil.rlCombat, ModLoadedUtil.RLCOMBAT_VERSION)) {
            RayTraceResult mov1 = RLCombatHandler.getSwingThroughPassableTarget(mc, rvEntity, player);
            if(mov1 != null && mov1.entityHit != null && mov1.entityHit != player) mov = mov1;
        }
        return mov;
    }

    @WrapOperation(
            method = "onClientTick",
            at = @At(value = "INVOKE", target = "Lcom/mujmajnkraft/bettersurvival/integration/RLCombatCompat;attackEntityFromClient(Lnet/minecraft/util/math/RayTraceResult;Lnet/minecraft/entity/player/EntityPlayer;)V"),
            remap = false
    )
    private void everythingNunchaku_betterSurvivalModClientHandler_onClientTickSwingRLCombat(RayTraceResult mov, EntityPlayer player, Operation<Void> original){
        original.call(mov, player);
        player.swingArm(EnumHand.MAIN_HAND);
        if(ModLoadedUtil.rlCombat.isLoaded() && ModLoadedUtil.versionInRange(ModLoadedUtil.rlCombat, ModLoadedUtil.RLCOMBAT_VERSION)) {
            RLCombatHandler.initiateMainhandAnimation(player);
        }
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

    @Definition(id = "player", local = @Local(type = EntityPlayerSP.class))
    @Expression("? != player")
    @ModifyExpressionValue(
            method = "onClientTick",
            at = @At("MIXINEXTRAS:EXPRESSION"),
            remap = false
    )
    private boolean everythingNunchaku_betterSurvivalModClientHandler_onClientTickShouldAttack(boolean original, @Local EntityPlayerSP player, @Local RayTraceResult mov){
        if(player.getHeldItemMainhand().getItem() instanceof ItemNunchaku) return original;
        else return original && ForgeConfigProvider.shouldAttack(mov.entityHit, player);
    }
}