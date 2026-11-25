package everythingnunchaku.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import everythingnunchaku.handlers.ForgeConfigProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class Minecraft_LeftClickMixin {

    @Shadow public WorldClient world;
    @Shadow public EntityPlayerSP player;

    @Unique
    private boolean everythingNunchaku$nunchakuClickedBlock = false;

    @Inject(
            method = "clickMouse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;clickBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z"),
            cancellable = true
    )
    private void everythingNunchaku_vanillaMinecraft_clickMouseNunchakuClickBlockCheck(CallbackInfo ci, @Local(name = "blockpos") BlockPos blockpos){
        ItemStack itemStack = this.player.getHeldItemMainhand();
        IBlockState blockState = this.world.getBlockState(blockpos);

        this.everythingNunchaku$nunchakuClickedBlock = blockState.getBlockHardness(this.world, blockpos) != 0 && !itemStack.canHarvestBlock(blockState) && ForgeConfigProvider.isClientNunchaku(itemStack.getItem());

        if(this.everythingNunchaku$nunchakuClickedBlock)
            ci.cancel();
    }

    @Inject(
            method = "clickMouse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;swingArm(Lnet/minecraft/util/EnumHand;)V"),
            cancellable = true
    )
    private void everythingNunchaku_vanillaMinecraft_clickMouseNunchakuClickBlockNoSwing(CallbackInfo ci){
        if(this.everythingNunchaku$nunchakuClickedBlock)
            ci.cancel();
    }

    @ModifyExpressionValue(
            method = "sendClickBlockToController",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;isAirBlock(Lnet/minecraft/util/math/BlockPos;)Z")
    )
    private boolean everythingNunchaku_vanillaMinecraft_sendClickBlockToControllerNoSwing(boolean isAirBlock){
        return isAirBlock || this.everythingNunchaku$nunchakuClickedBlock;
    }
}
