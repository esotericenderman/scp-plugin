package foundation.esoteric.minecraft.plugins.template

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import org.bukkit.plugin.java.JavaPlugin

class PaperTemplatePlugin : JavaPlugin() {
    override fun onEnable() {
        dataFolder.mkdir()
        saveDefaultConfig()

        val commandAPIConfig = CommandAPIBukkitConfig(this)

        CommandAPI.onLoad(commandAPIConfig)
        CommandAPI.onEnable()
    }
}
