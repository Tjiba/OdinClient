package starred.skies.odin.mixin.mixins;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import starred.skies.odin.accessors.EntityRenderStateAccessor;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements EntityRenderStateAccessor {
    @Unique
    private Entity odc$entity;

    @Override
    public Entity odc$getEntity() {
        return this.odc$entity;
    }

    @Override
    public void odc$setEntity(Entity entity) {
        this.odc$entity = entity;
    }
}