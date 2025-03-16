package croissantnova.sanitydim.client.render;

import croissantnova.sanitydim.SanityMod;
import croissantnova.sanitydim.entity.SneakingTerror;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SneakingTerrorRenderer extends InnerEntityRenderer<SneakingTerror>
{
    public SneakingTerrorRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(SanityMod.MODID, "sneaking_terror"), true));

        addRenderLayer(new CustomGlowingGeoLayer<>(this));
    }
}
