package croissantnova.sanitydim.client.render;

import croissantnova.sanitydim.SanityMod;
import croissantnova.sanitydim.entity.RottingStalker;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RottingStalkerRenderer extends InnerEntityRenderer<RottingStalker>
{
    public RottingStalkerRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(SanityMod.MOD_ID, "rotting_stalker"), true));

        addRenderLayer(new CustomGlowingGeoLayer<>(this));
    }
}