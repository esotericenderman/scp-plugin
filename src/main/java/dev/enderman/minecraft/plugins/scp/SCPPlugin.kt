package dev.enderman.minecraft.plugins.scp

import dev.enderman.minecraft.plugins.scp.entities.SCP018Entity
import dev.enderman.minecraft.plugins.scp.items.SCP018Item
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import foundation.esoteric.minecraft.plugins.library.commands.GiveCustomItemCommand
import foundation.esoteric.minecraft.plugins.library.entity.CustomEntityManager
import foundation.esoteric.minecraft.plugins.library.entity.CustomEntityPlugin
import foundation.esoteric.minecraft.plugins.library.file.FileManagedPlugin
import foundation.esoteric.minecraft.plugins.library.file.FileManager
import foundation.esoteric.minecraft.plugins.library.item.CustomItemManager
import foundation.esoteric.minecraft.plugins.library.item.CustomItemPlugin
import foundation.esoteric.minecraft.plugins.library.pack.resource.ResourcePackListener
import foundation.esoteric.minecraft.plugins.library.pack.resource.ResourcePackManager
import foundation.esoteric.minecraft.plugins.library.pack.resource.ResourcePackPlugin
import foundation.esoteric.minecraft.plugins.library.pack.resource.ResourcePackServer
import org.bukkit.plugin.java.JavaPlugin

class SCPPlugin : JavaPlugin(), CustomItemPlugin, CustomEntityPlugin, FileManagedPlugin, ResourcePackPlugin {

  override lateinit var fileManager: FileManager
  override lateinit var resourcePackManager: ResourcePackManager
  override lateinit var customItemManager: CustomItemManager
  override lateinit var customEntityManager: CustomEntityManager

  override fun onEnable() {
    fileManager = FileManager(this)

    customItemManager = CustomItemManager(this)
    customEntityManager = CustomEntityManager(this)

    dataFolder.mkdir()
    saveDefaultConfig()

    val commandAPIConfig = CommandAPIBukkitConfig(this)

    CommandAPI.onLoad(commandAPIConfig)
    CommandAPI.onEnable()

    SCP018Item(this)
    SCP018Entity(this)

    resourcePackManager = ResourcePackManager(this)
    val resourcePackServer = ResourcePackServer(this)
    ResourcePackListener(this, resourcePackServer)

    GiveCustomItemCommand(this)
  }
}
