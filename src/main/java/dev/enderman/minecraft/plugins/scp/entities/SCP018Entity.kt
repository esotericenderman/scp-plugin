package dev.enderman.minecraft.plugins.scp.entities

import dev.enderman.minecraft.plugins.scp.SCPPlugin
import dev.enderman.minecraft.plugins.scp.items.SCP018Item
import foundation.esoteric.minecraft.plugins.library.entity.CustomEntity
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.collections.HashMap

class SCP018Entity<T : Entity>(plugin: SCPPlugin) : CustomEntity<T>(plugin, "scp_018", EntityType.SNOWBALL) {

  private val allEntities: MutableList<Entity> = mutableListOf()

  private val previousPositionMap: Map<Snowball, Vector> = HashMap()
  private val velocityMap: Map<Snowball, Vector> = HashMap()

  override fun toEntity(vararg entities: T): Array<out T> {
    super.toEntity(*entities)

    entities.forEach { allEntities.add(it) }

    val scpItem = (plugin as SCPPlugin).customItemManager.getItem("scp_018") as SCP018Item
    entities.forEach { (it as Snowball).item = scpItem.createItem() }

    SCP018Runnable(plugin as SCPPlugin, this as SCP018Entity<Snowball>).runTaskTimer(plugin, 0L, 1L)

    return entities
  }

  @EventHandler
  fun onHit(event: ProjectileHitEvent) {
    val projectile = event.entity

    if (!isEntity(projectile)) {
      return
    }

    event.isCancelled = true

    val hitEntity = event.hitEntity

    if (hitEntity != null) {
      if (hitEntity is LivingEntity) {
        hitEntity.damage(projectile.velocity.length() * 5.0F)
      }
    }

    val hitFace = event.hitBlockFace
    if (hitFace != null) {
      val normalVector = hitFace.direction

      val velocity = projectile.velocity
      val newVelocity = velocity.subtract(normalVector.multiply(2.0F * velocity.dot(normalVector)))
      newVelocity.multiply(3F/2F)
      if (newVelocity.length() > 4F) {
        newVelocity.normalize().multiply(4F)
      }

      val newProjectile = createEntity(projectile.location)
      newProjectile.velocity = newVelocity

      projectile.remove()
    }
  }

  private class SCP018Runnable(private val plugin: SCPPlugin, private val scpEntity: SCP018Entity<Snowball>) : BukkitRunnable() {
    override fun run() {
      scpEntity.allEntities.forEach {
        if (it.isDead) {
          cancel()
        }

        it.world.spawnParticle(
          Particle.BLOCK,
          it.location,
          1,
          0.5, 0.5, 0.5,
          Material.REDSTONE_BLOCK.createBlockData()
        )

        if (it.velocity.lengthSquared() < 0.05F) {
          it.remove()
          cancel()

          val scpItem = plugin.customItemManager.getItem("scp_018") as SCP018Item
          it.world.dropItemNaturally(it.location, scpItem.createItem())
        }
      }
    }
  }
}
