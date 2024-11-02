package dev.enderman.minecraft.plugins.scp.entities

import dev.enderman.minecraft.plugins.scp.SCPPlugin
import foundation.esoteric.minecraft.plugins.library.entity.CustomEntity
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.util.Vector

class SCP018Entity(plugin: SCPPlugin) : CustomEntity<Snowball>(plugin, "scp_018", EntityType.SNOWBALL) {
  @EventHandler
  fun onHit(event: ProjectileHitEvent) {
    val projectile = event.entity

    if (!isEntity(projectile)) {
      return
    }

    val hitBlockFace = event.hitBlockFace
    if (hitBlockFace != null) {
      val velocity = projectile.velocity

      val newVelocity = when (hitBlockFace) {
        BlockFace.UP, BlockFace.DOWN -> velocity.multiply(Vector(1.0, -1.0, 1.0))
        BlockFace.NORTH, BlockFace.SOUTH -> velocity.multiply(Vector(1.0, 1.0, -1.0))
        BlockFace.EAST, BlockFace.WEST -> velocity.multiply(Vector(-1.0, 1.0, 1.0))
        else -> velocity
      }

      val newProjectile = createEntity(projectile.location)
      newProjectile.item = (plugin as SCPPlugin).customItemManager.getItem("scp_018")!!.toItem(newProjectile.item)
      newProjectile.velocity = newVelocity

      projectile.remove()
    }
  }
}
