package dev.enderman.minecraft.plugins.scp

import dev.enderman.minecraft.plugins.scp.entities.SCP018Entity
import dev.enderman.minecraft.plugins.scp.items.SCP018Item
import dev.enderman.minecraft.plugins.scp.items.SCP268Item
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import foundation.esoteric.minecraft.plugins.library.commands.GiveCustomItemCommand
import foundation.esoteric.minecraft.plugins.library.entity.CustomEntityManager
import foundation.esoteric.minecraft.plugins.library.entity.CustomEntityPlugin
import foundation.esoteric.minecraft.plugins.library.item.CustomItemManager
import foundation.esoteric.minecraft.plugins.library.item.CustomItemPlugin
import foundation.esoteric.minecraft.plugins.library.pack.resource.ResourcePackManager
import gg.flyte.twilight.Twilight
import org.bukkit.plugin.java.JavaPlugin

class SCPPlugin : JavaPlugin(), CustomItemPlugin, CustomEntityPlugin {

  override lateinit var customItemManager: CustomItemManager
  override lateinit var customEntityManager: CustomEntityManager

  lateinit var twilight: Twilight

  override fun onEnable() {
    customItemManager = CustomItemManager(this)
    customEntityManager = CustomEntityManager(this)

    twilight = Twilight(this)

    dataFolder.mkdir()
    saveDefaultConfig()

    val commandAPIConfig = CommandAPIBukkitConfig(this)

    CommandAPI.onLoad(commandAPIConfig)
    CommandAPI.onEnable()

    SCP018Entity(this)
    SCP018Item(this)

    SCP268Item(this)

    ResourcePackManager(this)

    GiveCustomItemCommand(this)
  }
}
