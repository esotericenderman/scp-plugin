package dev.enderman.minecraft.plugins.scp.entities

import dev.enderman.minecraft.plugins.scp.SCPPlugin
import foundation.esoteric.minecraft.plugins.library.entity.CustomEntity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileLaunchEvent

class SCP018Entity(plugin: SCPPlugin) : CustomEntity<Snowball>(plugin, "scp_018", EntityType.SNOWBALL) {
  @EventHandler
  fun onThrow(event: ProjectileLaunchEvent) {
    val entity = event.entity

    if (entity !is Snowball) {
      return
    }

    val source = entity.shooter

    if (source !is Player) {
      return
    }

    val itemInMainHand = source.inventory.itemInMainHand

    if ((plugin as SCPPlugin).customItemManager.getItem("scp_018")!!.isItem(itemInMainHand)) {
      toEntity(entity)
    }
  }
}
