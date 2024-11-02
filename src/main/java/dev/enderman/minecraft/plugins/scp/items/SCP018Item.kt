package dev.enderman.minecraft.plugins.scp.items

import dev.enderman.minecraft.plugins.scp.SCPPlugin
import dev.enderman.minecraft.plugins.scp.entities.SCP018Entity
import foundation.esoteric.minecraft.plugins.library.item.TexturedItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileLaunchEvent

class SCP018Item(plugin: SCPPlugin) : TexturedItem(plugin, "scp_018", Material.SNOWBALL) {
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

    if (isItem(itemInMainHand)) {
      val scpEntity: SCP018Entity = (plugin as SCPPlugin).customEntityManager.getEntity("scp_018") as SCP018Entity
      scpEntity.toEntity(entity)
      entity.item = toItem(entity.item)
    }
  }
}
