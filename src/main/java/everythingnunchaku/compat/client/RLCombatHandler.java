package everythingnunchaku.compat.client;

import bettercombat.mod.capability.CapabilityOffhandCooldown;
import bettercombat.mod.client.handler.EventHandlersClient;
import bettercombat.mod.handler.EventHandlers;
import bettercombat.mod.util.ConfigurationHandler;
import bettercombat.mod.util.Helpers;
import bettercombat.mod.util.ReachFixUtil;
import everythingnunchaku.handlers.ForgeConfigProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public abstract class RLCombatHandler {

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();

        if(ConfigurationHandler.server.enableOffhandAttack && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            EntityPlayerSP player = mc.player;
            Entity rvEntity = mc.getRenderViewEntity();
            if(player == null || rvEntity == null || player.isSpectator()) return;

            Item offhandItem = player.getHeldItemOffhand().getItem();
            float cooledStr = 0.0F;

            Helpers.clearOldModifiers(player, player.getHeldItemMainhand(), false, true, true);
            Helpers.addNewModifiers(player, player.getHeldItemOffhand(), false, true, true);

            RayTraceResult mov = null;
            if(ConfigurationHandler.server.swingThroughPassableBlocks) {
                RayTraceResult mov1 = ReachFixUtil.pointedObjectIgnorePassable(rvEntity, player, EnumHand.OFF_HAND, mc.world, mc.getRenderPartialTicks());
                if(mov1 != null && mov1.entityHit != null && mov1.entityHit != player) mov = mov1;
            }
            //If swing through check finds an entity, use that, otherwise use the normal check to not count a passable block hit as a miss
            if(mov == null) mov = ReachFixUtil.pointedObject(rvEntity, player, EnumHand.OFF_HAND, mc.world, mc.getRenderPartialTicks());

            float cooldown = player.getCooldownPeriod();

            Helpers.clearOldModifiers(player, player.getHeldItemOffhand(), false, true, true);
            Helpers.addNewModifiers(player, player.getHeldItemMainhand(), false, true, true);

            CapabilityOffhandCooldown coh = player.getCapability(EventHandlers.OFFHAND_COOLDOWN, null);
            if(coh != null && ConfigurationHandler.isItemAttackUsableOffhand(offhandItem)) cooledStr =  MathHelper.clamp(((float)coh.getTicksSinceLastSwing() + 0.5F) / cooldown, 0.0F, 1.0F);

            if(ForgeConfigProvider.isClientNunchaku(offhandItem) && cooledStr >= 1.0f) {
                if(mov != null && mov.entityHit != null && mov.entityHit != player ) {
                    EventHandlersClient.onMouseRightClick();
                }
            }
        }
    }
}
