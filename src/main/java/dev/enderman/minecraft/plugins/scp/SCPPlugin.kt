package dev.enderman.minecraft.plugins.scp

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import org.bukkit.plugin.java.JavaPlugin

class SCPPlugin : JavaPlugin() {
    override fun onEnable() {
        dataFolder.mkdir()
        saveDefaultConfig()

        val commandAPIConfig = CommandAPIBukkitConfig(this)

        CommandAPI.onLoad(commandAPIConfig)
        CommandAPI.onEnable()
    }
}
