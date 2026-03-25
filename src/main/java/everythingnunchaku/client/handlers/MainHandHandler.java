package everythingnunchaku.client.handlers;

import everythingnunchaku.EverythingNunchaku;
import everythingnunchaku.handlers.ForgeConfigProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MainHandHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        Entity rvEntity = mc.getCameraEntity();
        ClientPlayerEntity player = mc.player;

        if(player != null && rvEntity != null) {
            if(ForgeConfigProvider.isClientNunchaku(player.getMainHandItem().getItem())
                    && !player.isHandsBusy()
                    && player.getUseItem() == ItemStack.EMPTY
                    && EverythingNunchaku.iskeyBindAttackKeyDown()) {
                if(player.getAttackStrengthScale(0.5F) >= 1.0f) {
                    RayTraceResult mov = mc.hitResult;
                    if(mov != null && mov.getType() == RayTraceResult.Type.ENTITY && mc.gameMode != null) {
                        Entity target = ((EntityRayTraceResult) mov).getEntity();
                        if(ForgeConfigProvider.shouldAttack(target, player)) {
                            mc.gameMode.attack(player, target);
                            player.swing(Hand.MAIN_HAND);
                        }
                    }
                }
            }
        }
    }
}
