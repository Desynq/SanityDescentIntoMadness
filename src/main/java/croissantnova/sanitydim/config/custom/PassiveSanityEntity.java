package croissantnova.sanitydim.config.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public record PassiveSanityEntity(ResourceLocation id, float radius, float sanity) {

    public EntityType<?> getEntityType() {
        return ForgeRegistries.ENTITY_TYPES.getValue(id);
    }
}
