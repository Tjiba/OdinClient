package starred.skies.odin.utils

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.odtheking.odin.OdinMod.mc
import com.odtheking.odin.utils.customData
import com.odtheking.odin.utils.handlers.schedule
import com.odtheking.odin.utils.modMessage
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import starred.skies.odin.OdinClient.joinListeners
import kotlin.jvm.optionals.getOrNull

fun Component.notify() {
    if (mc.level != null) modMessage(this)
    else joinListeners.add { schedule(20) { modMessage(this) } }
}

val JsonElement.asJsonObjectOrNull: JsonObject?
    get() = try {
        asJsonObject
    } catch (_: IllegalStateException) {
        null
    }

inline val ItemStack.nullableID: String?
    get() = customData.getString("id").getOrNull()

inline val ItemStack.nullableUUID: String?
    get() = customData.getString("uuid").getOrNull()