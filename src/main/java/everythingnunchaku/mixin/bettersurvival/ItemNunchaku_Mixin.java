package everythingnunchaku.mixin.bettersurvival;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import everythingnunchaku.EverythingNunchaku;
import everythingnunchaku.handlers.ForgeConfigHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(targets = "com.mujmajnkraft.bettersurvival.items.ItemNunchaku$1")
public abstract class ItemNunchaku_Mixin {

    @ModifyExpressionValue(
            method = "apply",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getHeldItemMainhand()Lnet/minecraft/item/ItemStack;")
    )
    private ItemStack everythingNunchaku_betterSurvivalItemNunchaku$1_applyOffhandSpin(ItemStack original, ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn){
        if(ForgeConfigHandler.client.rlCombatOffhandNunchaku && entityIn == EverythingNunchaku.PROXY.getSinglePlayerEntity()){
            if(entityIn.getHeldItemMainhand() == stack && EverythingNunchaku.PROXY.iskeyBindAttackKeyDown())
                return stack;
            else if(entityIn.getHeldItemOffhand() == stack && EverythingNunchaku.PROXY.iskeyBindUseItemKeyDown())
                return stack;
        }
        return original;
    }
}
