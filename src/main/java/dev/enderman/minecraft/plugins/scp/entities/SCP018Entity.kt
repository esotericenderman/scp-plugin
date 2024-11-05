package dev.enderman.minecraft.plugins.scp.entities

import dev.enderman.minecraft.plugins.scp.SCPPlugin
import dev.enderman.minecraft.plugins.scp.items.SCP018Item
import foundation.esoteric.minecraft.plugins.library.entity.CustomEntity
import foundation.esoteric.minecraft.plugins.library.entity.CustomEntityPlugin
import org.bukkit.Color
import org.bukkit.Location
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

  companion object {
    private const val SPEED_INCREASE = 1.2
    private const val DAMAGE_PER_VELOCITY = 5.0
    private const val MAX_EXPLOSION_POWER = 10.0
  }

  private fun createEntity(spawnLocation: Location, scp018: SCP018?): Snowball {
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
        hitEntity.damage(projectile.velocity.length() * DAMAGE_PER_VELOCITY)
      }
    }

    val hitFace = event.hitBlockFace
    if (hitFace != null) {
      val normalVector = hitFace.direction

      val velocity = projectile.velocity
      val newVelocity = velocity.subtract(normalVector.multiply(2.0F * velocity.dot(normalVector)))

      val scp018 = SCP018.entityMap[projectile]!!
      SCP018.entityMap.remove(projectile)
      scp018.collisionCount++

      val velocityLimit = scp018.speedLimit

      newVelocity.multiply(SPEED_INCREASE)
      if (newVelocity.length() > velocityLimit) {
        newVelocity.normalize().multiply(velocityLimit.coerceAtLeast(velocity.length()))
      }

      val newProjectile = createEntity(projectile.location, scp018)
      newProjectile.velocity = newVelocity

      projectile.remove()
    }
  }

  class SCP018(plugin: CustomEntityPlugin, var entity: Snowball) : BukkitRunnable() {

    private var ticksLived = 0
    var collisionCount: Int = 0
      set(value) {
        if (value != collisionCount + 1) {
          throw IllegalArgumentException("Can only increment collision count by 1.")
        }

        field = value
      }

    val speedLimit: Double
      get() = (ticksLived / 200.0).coerceAtLeast(collisionCount / 10.0)

    private val explosionCooldownTicks = 5
    private var ticksSinceLastExplosion = explosionCooldownTicks + 1

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
      ticksLived++
      ticksSinceLastExplosion++
      val velocity = entity.location.toVector().distance(previousLocation)

      if (velocity == 0.0) {
        if (ticksStuck > 2) {
          val explosionPower = 1.0F + 0.25F * ticksStuck

          if (explosionPower >= MAX_EXPLOSION_POWER) {
            die()
            return
          }


          if (ticksSinceLastExplosion >= explosionCooldownTicks) {
            entity.world.createExplosion(entity.location, explosionPower)
            ticksSinceLastExplosion = 0
          }
        }

        ticksStuck++
      } else {
        ticksStuck = 0
      }

      entity.world.spawnParticle(
        Particle.DUST_COLOR_TRANSITION,
        entity.location,
        1,
        0.0, 0.0, 0.0,
        Particle.DustTransition(Color.RED, Color.RED, 0.75F)
      )

      previousLocation = entity.location.toVector()
    }

    private fun die() {
      cancel()
      entity.remove()
    }
  }
}
