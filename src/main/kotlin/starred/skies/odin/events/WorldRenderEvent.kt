package starred.skies.odin.events

import com.mojang.blaze3d.vertex.PoseStack
import com.odtheking.odin.events.core.CancellableEvent
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.state.CameraRenderState

sealed class WorldRenderEvent {
    sealed class Entity {
        data class Pre(
            val renderState: EntityRenderState,
            val poseStack: PoseStack,
            val cameraRenderState: CameraRenderState,
            val entity: net.minecraft.world.entity.Entity?
        ) : CancellableEvent()
    }
}