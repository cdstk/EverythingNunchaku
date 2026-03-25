package everythingnunchaku.compat.client;

import arekkuusu.offhandcombat.api.capability.Capabilities;
import arekkuusu.offhandcombat.common.handler.OffHandHandler;
import arekkuusu.offhandcombat.common.network.OHCPacketHandler;
import arekkuusu.offhandcombat.common.network.PacketOffHandAttack;
import everythingnunchaku.EverythingNunchaku;
import everythingnunchaku.handlers.EverythingNunchakuConfig;
import everythingnunchaku.handlers.ForgeConfigProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public abstract class OffhandCombatHandler {

    @SubscribeEvent
    public static void doConstantRightClick(InputEvent.ClickInputEvent event) {
        if(event.isCanceled()) return;
        if (!EverythingNunchakuConfig.Holder.CLIENT.offhandCombatMod.get()) return;
        if(!event.isUseItem() || event.getHand() != Hand.OFF_HAND) return;
        Minecraft minecraft = Minecraft.getInstance();

        if(event.getKeyBinding() == minecraft.options.keyAttack) {
            ClientPlayerEntity player = minecraft.player;
            if (player != null && player.getItemInHand(Hand.OFF_HAND).getItem().getUseAnimation(player.getItemInHand(Hand.OFF_HAND)) == UseAction.NONE) {
                event.setSwingHand(false);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase != TickEvent.Phase.END) return;
        if (!EverythingNunchakuConfig.Holder.CLIENT.offhandCombatMod.get()) return;
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayerEntity player = minecraft.player;
        Entity rvEntity = minecraft.getCameraEntity();
        if (player == null || rvEntity == null || player.isSpectator()) return;
//        EventHandlersClient.checkItemstacksChanged();
        if (!player.getUseItem().isEmpty()) return;
        //Don't allow shield spamming with an offhand weapon
        if (player.getMainHandItem().getItem() instanceof ShieldItem) return;

        if (ForgeConfigProvider.isClientNunchaku(player.getOffhandItem().getItem()) && EverythingNunchaku.iskeyBindUseItemKeyDown()) {
            ItemStack offhand = player.getOffhandItem();
            ItemStack mainHand = player.getMainHandItem();
            //Switch items
            OffHandHandler.setItemStackToSlot(player, EquipmentSlotType.MAINHAND, offhand);
            OffHandHandler.setItemStackToSlot(player, EquipmentSlotType.OFFHAND, mainHand);
            OffHandHandler.makeActive(player, offhand, mainHand);

            float cooldown = player.getCurrentItemAttackStrengthDelay();

            //Switch back items
            OffHandHandler.setItemStackToSlot(player, EquipmentSlotType.OFFHAND, offhand);
            OffHandHandler.setItemStackToSlot(player, EquipmentSlotType.MAINHAND, mainHand);
            OffHandHandler.makeInactive(player, offhand, mainHand);

            player.getCapability(Capabilities.OFF_HAND, null)
                    .ifPresent(coh -> {
                        float cooledStr = (float) MathHelper.clamp((coh.ticksSinceLastSwing + 0.5) / cooldown, 0.0F, 1.0F);
                        if (cooledStr >= 1.0f) {
                            if (!player.isHandsBusy() && !player.isShiftKeyDown() && OffHandHandler.canUseOffhand(player)) {
                                RayTraceResult mov = minecraft.hitResult;
                                if (mov != null && mov.getType() == RayTraceResult.Type.ENTITY && OffHandHandler.canSwingHand(player, Hand.OFF_HAND)) {
                                    if (minecraft.gameMode != null && minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
                                        Entity target = ((EntityRayTraceResult) mov).getEntity();
                                        if(ForgeConfigProvider.shouldAttack(target, player)) {
                                            OffHandHandler.attackEntity(player, target);
                                            OHCPacketHandler.INSTANCE.sendToServer(new PacketOffHandAttack(target.getId()));
                                            player.swing(Hand.OFF_HAND);
                                        }
                                    }
                                }
                            }
                        }
                    });
        }
    }
}
