package dev.enderman.minecraft.plugins.scp.entities

import dev.enderman.minecraft.plugins.scp.SCPPlugin
import foundation.esoteric.minecraft.plugins.library.entity.CustomEntity
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Snowball

class SCP018Entity(plugin: SCPPlugin) : CustomEntity<Snowball>(plugin, "scp_018") {
  override fun generateCustomEntity(spawnLocation: Location?): List<Snowball> {
    val snowball = spawnLocation!!.world.spawnEntity(spawnLocation, EntityType.SNOWBALL) as Snowball
    return listOf(snowball)
  }
}
