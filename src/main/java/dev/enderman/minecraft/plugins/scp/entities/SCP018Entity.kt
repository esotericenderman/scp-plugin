package dev.enderman.minecraft.plugins.scp.entities

import dev.enderman.minecraft.plugins.scp.SCPPlugin
import dev.enderman.minecraft.plugins.scp.items.SCP018Item
import foundation.esoteric.minecraft.plugins.library.entity.CustomEntity
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import kotlin.math.max

class SCP018Entity<T : Entity>(plugin: SCPPlugin) : CustomEntity<T>(plugin, "scp_018", EntityType.SNOWBALL) {
  override fun toEntity(vararg entities: T): Array<out T> {
    super.toEntity(*entities)

    val scpItem = (plugin as SCPPlugin).customItemManager.getItem("scp_018") as SCP018Item
    entities.forEach { entity -> (entity as Snowball).item = scpItem.createItem() }

    return entities
  }

  @EventHandler
  fun onHit(event: ProjectileHitEvent) {
    val projectile = event.entity

    if (!isEntity(projectile)) {
      return
    }

    println("Processing projectile hit event on tick: " + plugin.server.currentTick)
    println("Current ball velocity: " + projectile.velocity)

    val hitEntity = event.hitEntity

    event.isCancelled = true

    if (hitEntity != null) {
      val velocity = projectile.velocity

      velocity.y *= -1.5F
      projectile.velocity = velocity

      if (hitEntity is LivingEntity) {
        hitEntity.damage(projectile.velocity.length() * 5.0F)
      }

      return
    }

    val hitFace = event.hitBlockFace!!
    val normalVector = hitFace.direction

    val velocity = projectile.velocity
    val newVelocity = velocity.subtract(normalVector.multiply(2.0F * velocity.dot(normalVector)))
    newVelocity.multiply(3F/2F)
    if (newVelocity.length() > 100.0F) {
      newVelocity.multiply(2F/3F)
    }

    val newProjectile = createEntity(projectile.location)
    newProjectile.velocity = newVelocity

    projectile.remove()
  }
}
