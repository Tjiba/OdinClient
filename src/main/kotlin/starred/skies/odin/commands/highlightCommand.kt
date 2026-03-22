package starred.skies.odin.commands

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.utils.GreedyString
import com.odtheking.odin.utils.Color
import com.odtheking.odin.utils.modMessage
import starred.skies.odin.OdinClient
import starred.skies.odin.features.impl.cheats.Highlight
import starred.skies.odin.features.impl.cheats.Highlight.highlightLineSet
import starred.skies.odin.features.impl.cheats.Highlight.highlightMap

val highlightCommand = Commodore("highlight") {
    val colorRegex = Regex("^(.*?)(?:\\s+#?([0-9a-fA-F]{6}))?$")

    fun String.parse(): Pair<String, Color?>? {
        val match = colorRegex.matchEntire(trim()) ?: return null
        val name = match.groupValues[1].trim().takeIf { it.isNotEmpty() } ?: return null
        val hex = match.groupValues[2]

        return name.lowercase() to if (hex.isNotEmpty()) Color(hex + "ff") else null
    }

    literal("add").runs { arg: GreedyString ->
        val (key, color) = arg.string.parse() ?: return@runs modMessage("Invalid format. Use: /highlight add <mob name> [#RRGGBB or #RRGGBBAA]")
        if (highlightMap.containsKey(key)) return@runs modMessage("\"$key\" is already highlighted.")

        highlightMap[key] = color ?: Highlight.color

        OdinClient.moduleConfig.save()
        modMessage(buildString {
            append("Added \"$key\" to highlight list")
            color?.let { append(" with color #%02X%02X%02X".format(it.red, it.green, it.blue)) }
            append(".")
        })
    }

    literal("remove").runs { arg: GreedyString ->
        val key = arg.string.trim().lowercase()
        if (highlightMap.remove(key) == null) return@runs modMessage("\"$key\" isn't highlighted.")
        highlightLineSet.remove(key)

        OdinClient.moduleConfig.save()
        modMessage("Removed \"$key\" from highlight list.")
    }

    literal("clear").runs {
        if (highlightMap.isEmpty()) return@runs modMessage("Highlight list is already empty.")

        highlightMap.clear()
        highlightLineSet.clear()

        OdinClient.moduleConfig.save()
        modMessage("Highlight list cleared.")
    }

    literal("list").runs {
        if (highlightMap.isEmpty()) return@runs modMessage("Highlight list is empty.")

        val text = highlightMap.entries.joinToString("\n") { (name, color) ->
            "$name - #%02X%02X%02X".format(color.red, color.green, color.blue)
        }

        modMessage("Highlight list:\n$text")
    }
}
