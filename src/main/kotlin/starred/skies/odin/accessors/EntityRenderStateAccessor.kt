@file:Suppress("FunctionName")

package starred.skies.odin.accessors

import net.minecraft.world.entity.Entity

interface EntityRenderStateAccessor {
    fun `odc$getEntity`(): Entity?
    fun `odc$setEntity`(entity: Entity?)
}