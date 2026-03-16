package starred.skies.odin.commands

import com.github.stivais.commodore.Commodore
import com.odtheking.odin.utils.getChatBreak
import com.odtheking.odin.utils.modMessage
import starred.skies.odin.features.impl.cheats.AutoClicker
import starred.skies.odin.features.impl.cheats.AutoClicker.held

val autoClickerCommand = Commodore("autoclicker") {
    literal("add") {
        literal("left").runs {
            val item = held() ?: return@runs modMessage("Hold an item to whitelist.")
            if (item in AutoClicker.leftWhitelist.value) return@runs modMessage("\"$item\" is already in the left whitelist.")

            AutoClicker.leftWhitelist.update { add(item) }
            modMessage("Added \"$item\" to the left whitelist.")
        }

        literal("right").runs {
            val item = held() ?: return@runs modMessage("Hold an item to whitelist.")
            if (item in AutoClicker.rightWhitelist.value) return@runs modMessage("\"$item\" is already in the right whitelist.")

            AutoClicker.rightWhitelist.update { add(item) }
            modMessage("Added \"$item\" to the right whitelist.")
        }
    }

    literal("remove") {
        literal("left").runs {
            val item = held() ?: return@runs modMessage("Hold an item to remove from whitelist.")
            if (item !in AutoClicker.leftWhitelist.value) return@runs modMessage("\"$item\" isn't in the left whitelist.")

            AutoClicker.leftWhitelist.update { remove(item) }
            modMessage("Removed \"$item\" from the left whitelist.")
        }

        literal("right").runs {
            val item = held() ?: return@runs modMessage("Hold an item to remove from whitelist.")
            if (item !in AutoClicker.rightWhitelist.value) return@runs modMessage("\"$item\" isn't in the right whitelist.")

            AutoClicker.rightWhitelist.update { remove(item) }
            modMessage("Removed \"$item\" from the right whitelist.")
        }
    }

    literal("clear") {
        literal("left").runs {
            AutoClicker.leftWhitelist.update { clear() }
            modMessage("Left whitelist cleared.")
        }

        literal("right").runs {
            AutoClicker.rightWhitelist.update { clear() }
            modMessage("Right whitelist cleared.")
        }

        literal("all").runs {
            AutoClicker.leftWhitelist.update { clear() }
            AutoClicker.rightWhitelist.update { clear() }
            modMessage("All whitelists cleared.")
        }
    }

    literal("list").runs {
        val left = AutoClicker.leftWhitelist.value.joinToString(", ").ifEmpty { "empty" }
        val right = AutoClicker.rightWhitelist.value.joinToString(", ").ifEmpty { "empty" }

        modMessage("Autoclicker whitelist:")
        modMessage("Left: $left")
        modMessage(getChatBreak().drop(20))
        modMessage("Right: $right")
    }
}