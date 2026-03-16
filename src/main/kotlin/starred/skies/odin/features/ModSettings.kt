package starred.skies.odin.features

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.features.Module
import starred.skies.odin.utils.Skit

object ModSettings : Module(
    name = "--- OdinClient settings ---",
    description = "Settings for some of the internal stuff in OdinClient!",
    category = Skit.CHEATS
) {
    val updateOnce by BooleanSetting("Only show update once", desc = "Toggle to only show the message to update once!")
}