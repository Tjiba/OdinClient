package starred.skies.odin.features.impl.cheats

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.skyblock.Island
import com.odtheking.odin.utils.skyblock.LocationUtils
import net.minecraft.client.renderer.entity.state.CreeperRenderState
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.monster.Creeper
import starred.skies.odin.events.WorldRenderEvent
import starred.skies.odin.utils.Skit

object Ghosts : Module(
    name = "Ghosts",
    description = "Things for Ghosts",
    category = Skit.CHEATS
) {
    private val showGhosts by BooleanSetting("Show Ghosts", desc = "Show the creeper entities.")
    private val showPowered by BooleanSetting("Show Powered Layer", true, desc = "Show the powered layer for creepers.")

    init {
        on<WorldRenderEvent.Entity.Pre> {
            if (LocationUtils.currentArea != Island.DwarvenMines) return@on

            val r = renderState as? CreeperRenderState ?: return@on
            val e = entity as? Creeper ?: return@on
            if (e.getAttributeBaseValue(Attributes.MAX_HEALTH) < 1_000_000) return@on

            if (r.isPowered && !showPowered) r.isPowered = false
            if (r.isInvisible && showGhosts) r.isInvisible = false
        }
    }
}