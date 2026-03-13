package starred.skies.odin.utils

import com.odtheking.odin.OdinMod.mc
import net.minecraft.client.KeyMapping
import net.minecraft.world.inventory.ClickType
import starred.skies.odin.mixin.accessors.KeyMappingAccessor

fun rightClick() {
    val options = mc.options ?: return
    val key = (options.keyUse as KeyMappingAccessor).boundKey
    KeyMapping.set(key, true)
    KeyMapping.click(key)
    KeyMapping.set(key, false)
}

fun leftClick() {
    val options = mc.options ?: return
    val key = (options.keyAttack as KeyMappingAccessor).boundKey
    KeyMapping.set(key, true)
    KeyMapping.click(key)
    KeyMapping.set(key, false)
}

fun guiClick(id: Int, index: Int, button: Int = 0, clickType: ClickType = ClickType.PICKUP) {
    val player = mc.player ?: return
    mc.gameMode?.handleInventoryMouseClick(id, index, button, clickType, player)
}