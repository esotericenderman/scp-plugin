package dev.enderman.minecraft.plugins.scp.entities

import dev.enderman.minecraft.plugins.scp.SCPPlugin
import dev.enderman.minecraft.plugins.scp.items.SCP018Item
import foundation.esoteric.minecraft.plugins.library.entity.CustomEntity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent

class SCP018Entity(plugin: SCPPlugin) : CustomEntity<Snowball>(plugin, "scp_018", EntityType.SNOWBALL) {
  @EventHandler
  fun onHit(event: ProjectileHitEvent) {
    val projectile = event.entity

    if (!isEntity(projectile)) {
      return
    }

    val hitEntity = event.hitEntity

    if (hitEntity != null) {
      event.isCancelled = true
      val velocity = projectile.velocity
      velocity.y *= -2.0F
      projectile.velocity = velocity
      return
    }

    val hitFace = event.hitBlockFace!!
    val normalVector = hitFace.direction

    val velocity = projectile.velocity
    val newVelocity = velocity.subtract(normalVector.multiply(2.0F * velocity.dot(normalVector)))

    val newProjectile = createEntity(projectile.location)
    val scpItem = (plugin as SCPPlugin).customItemManager.getItem("scp_018") as SCP018Item

    newProjectile.item = scpItem.createItem()
    newProjectile.velocity = newVelocity
  }
}
