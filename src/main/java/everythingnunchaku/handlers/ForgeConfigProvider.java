package everythingnunchaku.handlers;

import everythingnunchaku.EverythingNunchaku;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Level;

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

    public static boolean shouldAttack(Entity entHit, PlayerEntity player) {
        if(entHit == null) return false;

        if(entHit instanceof ServerPlayerEntity && entHit.getServer() != null) {
            return entHit.getServer().isPvpAllowed();
        }

        if(entHit instanceof TameableEntity && ((TameableEntity)entHit).getOwner() == player) {
            return false;
        }

        return ForgeConfigProvider.isEntityNunchakable(entHit);
    }

    public static boolean isClientNunchaku(Item item){
        if(EverythingNunchakuConfig.Holder.CLIENT.allowEverything.get()) return true;
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
        ForgeConfigProvider.validNunchakuClasses.addAll(EverythingNunchakuConfig.Holder.CLIENT.itemClassWhitelist.get().stream()
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
        ForgeConfigProvider.validNunchakuItems.addAll(EverythingNunchakuConfig.Holder.CLIENT.itemIDWhitelist.get().stream()
                .map(ResourceLocation::new)
                .filter(resourceLocation -> {
                    if(ForgeRegistries.ITEMS.getValue(resourceLocation) == null){
                        EverythingNunchaku.LOGGER.log(Level.WARN, "Whitelist Item ID not found for entry: {}, ignoring", resourceLocation);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toSet()));
        ForgeConfigProvider.invalidNunchakuItems.addAll(EverythingNunchakuConfig.Holder.CLIENT.itemIDBlacklist.get().stream()
                .map(ResourceLocation::new)
                .filter(resourceLocation -> {
                    if(ForgeRegistries.ITEMS.getValue(resourceLocation) == null){
                        EverythingNunchaku.LOGGER.log(Level.WARN, "Blacklist Item ID not found for entry: {}, ignoring", resourceLocation);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toSet()));
        ForgeConfigProvider.invalidTargetClasses.addAll(EverythingNunchakuConfig.Holder.CLIENT.entityBlacklist.get().stream()
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
