package croissantnova.sanitydim.config.value;

import croissantnova.sanitydim.config.ConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static croissantnova.sanitydim.api.SanityAPI.MAX_SANITY;
import static croissantnova.sanitydim.api.SanityAPI.MIN_SANITY;

public abstract class ModConfigValue<T> {
    public static final List<ModConfigValue<?>> CONFIG_VALUES = new ArrayList<>();

    private final String proxyKey;
    private final Function<T, T> finalizer;
    protected ForgeConfigSpec.ConfigValue<T> configValue;

    public ModConfigValue(String proxyKey, Function<T, T> finalizer) {
        CONFIG_VALUES.add(this);
        this.proxyKey = proxyKey;
        this.finalizer = finalizer;
    }

    /**
     * Needs to initialize {@link ModConfigProcessableValue#configValue} through a definition call
     */
    public abstract void build(ForgeConfigSpec.Builder builder);

    public void loadProxy(@NotNull Map<String, ConfigManager.ProxyValueEntry<?>> proxies) {
        proxies.put(proxyKey, new ConfigManager.ProxyValueEntry<>(
                configValue,
                finalizer
        ));
    }

    public T getValue(ResourceLocation dim) {
        return ConfigManager.proxy(proxyKey, dim);
    }


    public static <T> ModConfigValue<List<? extends T>> createList(
            String name,
            Supplier<List<? extends T>> defaultSupplier,
            Predicate<Object> elementValidator,
            String... comments) {
        return new ModConfigValue<>(name, ConfigManager::noFinalize, true) {
            @Override
            public void build(ForgeConfigSpec.Builder builder) {
                for (String comment : comments) {
                    builder.comment(comment);
                }
                List<String> path = Collections.singletonList(name.split("\\.")[2]);
                configValue = builder
                        .defineListAllowEmpty(path, defaultSupplier, elementValidator);
            }
        };
    }


    public static ModConfigValue<Double> createPassiveDouble(String name, double defaultValue, double minValue, double maxValue, String... comments) {
        return new ModConfigValue<>(name, ConfigManager::finalizePassive) {
            @Override
            public void build(ForgeConfigSpec.Builder builder) {
                for (String comment : comments) {
                    builder.comment(comment);
                }
                configValue = builder
                        .defineInRange(name.split("\\.")[2], defaultValue, minValue, maxValue);
            }
        };
    }

    /**
     * Assumes min and max values are sanity min and max (-100, 100)
     *
     * @param name
     * @param defaultValue
     * @param comments
     * @return
     */
    public static ModConfigValue<Double> createPassiveDouble(String name, double defaultValue, String... comments) {
        return createPassiveDouble(name, defaultValue, MIN_SANITY, MAX_SANITY, comments);
    }
}
