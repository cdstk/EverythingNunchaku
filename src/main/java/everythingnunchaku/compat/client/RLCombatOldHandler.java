package everythingnunchaku.compat.client;

import bettercombat.mod.capability.CapabilityOffhandCooldown;
import bettercombat.mod.client.handler.EventHandlersClient;
import bettercombat.mod.handler.EventHandlers;
import bettercombat.mod.util.ConfigurationHandler;
import bettercombat.mod.util.Helpers;
import com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.INunchakuCombo;
import com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.NunchakuComboProvider;
import com.mujmajnkraft.bettersurvival.packet.BetterSurvivalPacketHandler;
import com.mujmajnkraft.bettersurvival.packet.MessageNunchakuSpinClient;
import everythingnunchaku.EverythingNunchaku;
import everythingnunchaku.handlers.ForgeConfigHandler;
import everythingnunchaku.handlers.ForgeConfigProvider;
import meldexun.reachfix.hook.client.EntityRendererHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class RLCombatOldHandler {

    // I don't like Reflection in this case, but it has to be done for simplicity
    private static class OldRLCombatReflection {

        private final static OldRLCombatReflection ACCESS = create();
        private static OldRLCombatReflection create() {
            try {
                return new OldRLCombatReflection();
            } catch (ReflectiveOperationException | RuntimeException exception) {
                EverythingNunchaku.LOGGER.warn("Failed to read RLCombat's config.", exception);
                return null;
            }
        }

        private final Field enableOffHandAttack;
        private final Method isItemAttackUsable;

        private final Method clearOldModifiers;
        private final Method addNewModifiers;

        private final Field offhandCapability;

        private final Method getOffhandCooldown;

        private OldRLCombatReflection() throws ReflectiveOperationException {
            Field field = null;
            try {
                field = ObfuscationReflectionHelper.findField(ConfigurationHandler.class, "enableOffHandAttack");
            }
            catch (ReflectionHelper.UnableToFindFieldException ignored) {}
            enableOffHandAttack = field;

            field = null;
            try {
                field = ObfuscationReflectionHelper.findField(EventHandlers.class, "TUTO_CAP");
            }
            catch (ReflectionHelper.UnableToFindFieldException ignored) {}
            offhandCapability = field;

            Method method = null;
            try {
                method = ObfuscationReflectionHelper.findMethod(ConfigurationHandler.class, "isItemAttackUsable", boolean.class, Item.class);
            }
            catch (ReflectionHelper.UnableToFindFieldException ignored) {}
            isItemAttackUsable = method;

            method = null;
            try {
                method = ObfuscationReflectionHelper.findMethod(Helpers.class, "clearOldModifiers", void.class, EntityLivingBase.class, ItemStack.class);
            }
            catch (ReflectionHelper.UnableToFindFieldException ignored) {}
            clearOldModifiers = method;

            method = null;
            try {
                method = ObfuscationReflectionHelper.findMethod(Helpers.class, "addNewModifiers", void.class, EntityLivingBase.class, ItemStack.class);
            }
            catch (ReflectionHelper.UnableToFindFieldException ignored) {}
            addNewModifiers = method;

            method = null;
            try {
                method = ObfuscationReflectionHelper.findMethod(CapabilityOffhandCooldown.class, "getOffhandCooldown", int.class);
            }
            catch (ReflectionHelper.UnableToFindFieldException ignored) {}
            getOffhandCooldown = method;
        }

        public static boolean getEnableOffHandAttackConfig() {
            try {
                return (boolean) ACCESS.enableOffHandAttack.get(null);
            }
            catch (IllegalAccessException e) {
                return true;
            }
        }

        public static Capability<CapabilityOffhandCooldown> getOffhandCapability() {
            try {
                return (Capability<CapabilityOffhandCooldown>) ACCESS.offhandCapability.get(null);
            }
            catch (IllegalAccessException e) {
                return null;
            }
        }

        public static boolean invokeIsItemAttackUsable(Item item) {
            try {
                return (boolean) ACCESS.isItemAttackUsable.invoke(null, item);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                return false;
            }
        }

        public static void invokeClearOldModifiers(EntityLivingBase entity, ItemStack stack) {
            try {
                ACCESS.clearOldModifiers.invoke(null, entity, stack);
            }
            catch (IllegalAccessException | InvocationTargetException e) {}
        }

        public static void invokeAddNewModifiers(EntityLivingBase entity, ItemStack stack) {
            try {
                ACCESS.addNewModifiers.invoke(null, entity, stack);
            }
            catch (IllegalAccessException | InvocationTargetException e) {}
        }

        public static int invokeGetOffhandCooldown(CapabilityOffhandCooldown offhandCapability) {
            try {
                return (int) ACCESS.getOffhandCooldown.invoke(offhandCapability);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                return 1;
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if(!ForgeConfigHandler.client.rlCombatOffhand || !OldRLCombatReflection.getEnableOffHandAttackConfig()) return;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        Entity rvEntity = mc.getRenderViewEntity();
        if(player == null || rvEntity == null || player.isSpectator()) return;

        if(!player.getActiveItemStack().isEmpty()) return;
        //Don't allow shield spamming with an offhand weapon
        if(player.getHeldItemMainhand().getItem() instanceof ItemShield) return;

        INunchakuCombo cap = player.getCapability(NunchakuComboProvider.NUNCHAKUCOMBO_CAP, null);
        if(isClientNunchakuOffhand(player.getHeldItemOffhand().getItem()) && EverythingNunchaku.PROXY.iskeyBindUseItemKeyDown()) {
            if(cap != null && !cap.isSpinning() && ForgeConfigHandler.client.rlCombatOffhandNunchaku) {//Don't spam packets if we're already spinning
                BetterSurvivalPacketHandler.NETWORK.sendToServer(new MessageNunchakuSpinClient(true));
                cap.setSpinning(true);
            }

            OldRLCombatReflection.invokeClearOldModifiers(player, player.getHeldItemMainhand());
            OldRLCombatReflection.invokeAddNewModifiers(player, player.getHeldItemOffhand());

            RayTraceResult mov = EntityRendererHook.pointedObject(rvEntity, player, EnumHand.OFF_HAND, mc.world, mc.getRenderPartialTicks());

            OldRLCombatReflection.invokeClearOldModifiers(player, player.getHeldItemOffhand());
            OldRLCombatReflection.invokeAddNewModifiers(player, player.getHeldItemMainhand());

            CapabilityOffhandCooldown coh = player.getCapability(OldRLCombatReflection.getOffhandCapability(), null);
            if(coh != null && OldRLCombatReflection.invokeGetOffhandCooldown(coh) == 0) {
                if(mov != null && mov.entityHit != null && mov.entityHit != player ) {
                    EventHandlersClient.onMouseRightClick();
                }
            }
        }
    }

    // Must match RLCombat config also
    public static boolean isClientNunchakuOffhand(Item item){
        return OldRLCombatReflection.invokeIsItemAttackUsable(item) && ForgeConfigProvider.isClientNunchaku(item);
    }
}
