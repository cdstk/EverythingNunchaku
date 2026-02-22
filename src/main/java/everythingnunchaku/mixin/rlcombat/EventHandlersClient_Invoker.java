package everythingnunchaku.mixin.rlcombat;

import bettercombat.mod.client.handler.EventHandlersClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EventHandlersClient.class)
public interface EventHandlersClient_Invoker {

    @Invoker(value = "shouldAttack", remap = false)
    static boolean invokeShouldAttack(Entity entHit, EntityPlayer player) {
        throw new AssertionError();
    }
}
