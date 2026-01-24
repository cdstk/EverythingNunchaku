package everythingnunchaku.mixin.bettersurvival;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
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

    @Definition(id = "stack", local = @Local(type = ItemStack.class, argsOnly = true))
    @Expression("? == stack")
    @ModifyExpressionValue(
            method = "apply",
            at = @At(value = "MIXINEXTRAS:EXPRESSION")
    )
    private boolean everythingNunchaku_betterSurvivalItemNunchaku$1_applyOffhandSpin(boolean inMainhand, ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn){
        if(ForgeConfigHandler.client.rlCombatOffhandNunchaku && entityIn == EverythingNunchaku.PROXY.getSinglePlayerEntity()){
            if(inMainhand)
                return EverythingNunchaku.PROXY.iskeyBindAttackKeyDown();
            else if(entityIn.getHeldItemOffhand() == stack)
                return EverythingNunchaku.PROXY.iskeyBindUseItemKeyDown();
        }
        return inMainhand;
    }
}
