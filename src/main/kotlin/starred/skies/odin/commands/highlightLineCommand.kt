package starred.skies.odin.commands

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.utils.GreedyString
import com.odtheking.odin.utils.modMessage
import starred.skies.odin.OdinClient
import starred.skies.odin.features.impl.cheats.Highlight.highlightLineSet
import starred.skies.odin.features.impl.cheats.Highlight.highlightMap

val highlightLineCommand = Commodore("highlightline") {
    literal("add").runs { arg: GreedyString ->
        val key = arg.string.trim().lowercase()
        if (key.isEmpty()) return@runs modMessage("Invalid format. Use: /highlightline add <mob name>")
        if (key !in highlightMap) return@runs modMessage("\"$key\" isn't highlighted. Add it with /highlight add first.")
        if (key in highlightLineSet) return@runs modMessage("\"$key\" already has a highlight line.")

        highlightLineSet.add(key)
        OdinClient.moduleConfig.save()
        modMessage("Added highlight line for \"$key\".")
    }

    literal("remove").runs { arg: GreedyString ->
        val key = arg.string.trim().lowercase()
        if (highlightLineSet.remove(key).not()) return@runs modMessage("\"$key\" doesn't have a highlight line.")

        OdinClient.moduleConfig.save()
        modMessage("Removed highlight line for \"$key\".")
    }

    literal("clear").runs {
        if (highlightLineSet.isEmpty()) return@runs modMessage("Highlight line list is already empty.")

        highlightLineSet.clear()
        OdinClient.moduleConfig.save()
        modMessage("Highlight line list cleared.")
    }

    literal("list").runs {
        if (highlightLineSet.isEmpty()) return@runs modMessage("Highlight line list is empty.")

        modMessage("Highlight line list:\n${highlightLineSet.joinToString("\n")}")
    }
}
