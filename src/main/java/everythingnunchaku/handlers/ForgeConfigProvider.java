package everythingnunchaku.handlers;

import everythingnunchaku.EverythingNunchaku;
import net.minecraft.item.Item;
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

    public static void init(){
        ForgeConfigProvider.initClientNunchakus();
    }

    public static boolean isClientNunchaku(Item item){
        if(ForgeConfigHandler.client.allowEverything) return true;
        if(validNunchakuItems.contains(item.getRegistryName())) return true;
        for(Class<?> clazz : validNunchakuClasses) {
            if(clazz.isInstance(item)) return true;
        }
        return false;
    }


    public static void initClientNunchakus(){
        ForgeConfigProvider.validNunchakuClasses.clear();
        ForgeConfigProvider.validNunchakuItems.clear();
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
                        EverythingNunchaku.LOGGER.log(Level.WARN, "Item ID not found for entry: {}, ignoring", resourceLocation);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toSet()));
    }
}
