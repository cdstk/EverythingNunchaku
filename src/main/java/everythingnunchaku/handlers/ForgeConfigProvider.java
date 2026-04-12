package everythingnunchaku.handlers;

import bettercombat.mod.util.Helpers;
import everythingnunchaku.EverythingNunchaku;
import everythingnunchaku.compat.ModLoadedUtil;
import everythingnunchaku.mixin.rlcombat.EventHandlersClient_Invoker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ForgeConfigProvider {

    private static final Set<Class<?>> validNunchakuClasses = new HashSet<>();
    private static final Set<ResourceLocation> validNunchakuItems = new HashSet<>();
    private static final Set<ResourceLocation> invalidNunchakuItems = new HashSet<>();
    private static final Set<Class<?>> invalidTargetClasses = new HashSet<>();

    public static void init(){
        ForgeConfigProvider.initClientNunchakus();
    }

    public static boolean shouldAttack(Entity entHit, EntityPlayer player) {
        if(ForgeConfigHandler.client.rlCombatEntityBlacklist && ModLoadedUtil.versionInRange(ModLoadedUtil.rlCombat, ModLoadedUtil.RLCOMBAT_VERSION)) {
            if(!EventHandlersClient_Invoker.invokeShouldAttack(entHit, player)) return false;
        }
        else {
            if(entHit == null) return false;

            if(entHit instanceof EntityPlayerMP) {
                return Helpers.execNullable(entHit.getServer(), MinecraftServer::isPVPEnabled, false);
            }

            if(entHit instanceof IEntityOwnable && ((IEntityOwnable)entHit).getOwner() == player) {
                return false;
            }
        }

        return ForgeConfigProvider.isEntityNunchakable(entHit);
    }

    public static boolean isClientNunchaku(Item item){
        if(ForgeConfigHandler.client.allowEverything) return true;
        if(invalidNunchakuItems.contains(item.getRegistryName())) return false;
        if(validNunchakuItems.contains(item.getRegistryName())) return true;
        for(Class<?> clazz : validNunchakuClasses) {
            if(clazz.isInstance(item)) return true;
        }
        return false;
    }

    public static boolean isEntityNunchakable(Entity entity){
        for(Class<?> clazz : invalidTargetClasses) {
            if(clazz.isInstance(entity)) return false;
        }
        return true;
    }

    public static void initClientNunchakus(){
        ForgeConfigProvider.validNunchakuClasses.clear();
        ForgeConfigProvider.validNunchakuItems.clear();
        ForgeConfigProvider.invalidNunchakuItems.clear();
        ForgeConfigProvider.invalidTargetClasses.clear();
        ForgeConfigProvider.validNunchakuClasses.addAll(Arrays.stream(ForgeConfigHandler.client.itemClassWhitelist)
                .map(line -> {
                    try {
                        return Class.forName(line.trim());
                    } catch (ClassNotFoundException e) {
                        EverythingNunchaku.LOGGER.log(Level.WARN, "Item Class not found for entry: {}, ignoring", line);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
        ForgeConfigProvider.validNunchakuItems.addAll(Arrays.stream(ForgeConfigHandler.client.itemIDWhitelist)
                .map(ResourceLocation::new)
                .filter(resourceLocation -> {
                    if(ForgeRegistries.ITEMS.getValue(resourceLocation) == null){
                        EverythingNunchaku.LOGGER.log(Level.WARN, "Whitelist Item ID not found for entry: {}, ignoring", resourceLocation);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toSet()));
        ForgeConfigProvider.invalidNunchakuItems.addAll(Arrays.stream(ForgeConfigHandler.client.itemIDBlacklist)
                .map(ResourceLocation::new)
                .filter(resourceLocation -> {
                    if(ForgeRegistries.ITEMS.getValue(resourceLocation) == null){
                        EverythingNunchaku.LOGGER.log(Level.WARN, "Blacklist Item ID not found for entry: {}, ignoring", resourceLocation);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toSet()));
        ForgeConfigProvider.invalidTargetClasses.addAll(Arrays.stream(ForgeConfigHandler.client.entityBlacklist)
                .map(line -> {
                    try {
                        return Class.forName(line.trim());
                    } catch (ClassNotFoundException e) {
                        EverythingNunchaku.LOGGER.log(Level.WARN, "Entity Class not found for entry: {}, ignoring", line);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
    }
}
