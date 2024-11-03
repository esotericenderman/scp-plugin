package dev.enderman.minecraft.plugins.scp.entities

import dev.enderman.minecraft.plugins.scp.SCPPlugin
import dev.enderman.minecraft.plugins.scp.items.SCP018Item
import foundation.esoteric.minecraft.plugins.library.entity.CustomEntity
import foundation.esoteric.minecraft.plugins.library.entity.CustomEntityPlugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class SCP018Entity(plugin: SCPPlugin) : CustomEntity<Snowball>(plugin, "scp_018", EntityType.SNOWBALL) {

  fun createEntity(spawnLocation: Location, scp018: SCP018?): Snowball {
    return spawnLocation.world.spawnEntity(spawnLocation, EntityType.SNOWBALL, CreatureSpawnEvent.SpawnReason.DEFAULT) { entity ->
      toEntity(
        scp018,
        entity as Snowball,
      )
    } as Snowball
  }

  fun toEntity(scp018: SCP018?, vararg entities: Snowball): Array<out Snowball> {
    super.toEntity(*entities)

    entities.forEach {
      if (scp018 == null) {
        SCP018(plugin, it)
      } else {
        SCP018.entityMap[it] = scp018
        scp018.entity = it
      }
    }

    val scpItem = (plugin as SCPPlugin).customItemManager.getItem("scp_018") as SCP018Item
    entities.forEach { it.item = scpItem.createItem() }

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
      if (newVelocity.length() > 5F) {
        newVelocity.normalize().multiply(5F)
      }

      val scp018 = SCP018.entityMap[projectile]
      SCP018.entityMap.remove(projectile)

      val newProjectile = createEntity(projectile.location, scp018)
      newProjectile.velocity = newVelocity

      projectile.remove()
    }
  }

  class SCP018(plugin: CustomEntityPlugin, var entity: Snowball) : BukkitRunnable() {

    private var previousLocation: Vector = entity.location.toVector()
    private var ticksStuck: Int = 0

    companion object {
      val entityMap: MutableMap<Entity, SCP018> = mutableMapOf()
    }

    init {
      entityMap[entity] = this
      runTaskTimer(plugin, 0L, 1L)
    }

    override fun run() {
      val velocity = entity.location.toVector().distance(previousLocation)

      if (velocity == 0.0) {
        if (ticksStuck > 1) {
          val explosionPower = 1.0F + 0.25F * ticksStuck

          if (explosionPower >= 5F) {
            die()
            return
          }

          entity.world.createExplosion(entity.location, explosionPower)
        }

        ticksStuck++
      } else {
        ticksStuck = 0
      }

      entity.world.spawnParticle(
        Particle.BLOCK,
        entity.location,
        1,
        0.5, 0.5, 0.5,
        Material.REDSTONE_BLOCK.createBlockData()
      )

      previousLocation = entity.location.toVector()
    }

    private fun die() {
      cancel()
      entity.remove()
    }
  }
}
