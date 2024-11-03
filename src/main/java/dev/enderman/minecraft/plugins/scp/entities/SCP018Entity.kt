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

class SCP018Entity<T : Entity>(plugin: SCPPlugin) : CustomEntity<T>(plugin, "scp_018", EntityType.SNOWBALL) {

  fun createEntity(spawnLocation: Location, scp018: SCP018?): T {
    return spawnLocation.world.spawnEntity(spawnLocation, EntityType.SNOWBALL, CreatureSpawnEvent.SpawnReason.DEFAULT) { entity ->
      toEntity(
        scp018,
        entity as T,
      )
    } as T
  }

  fun toEntity(scp018: SCP018?, vararg entities: T): Array<out T> {
    super.toEntity(*entities)

    entities.forEach {
      if (scp018 == null) {
        println("Creating new SCP-018 instance.")
        SCP018(plugin, it as Snowball)
      } else {
        println("Updating SCP-018 instance Snowball entity.")
        SCP018.entityMap[it] = scp018
        scp018.entity = it as Snowball
      }
    }

    val scpItem = (plugin as SCPPlugin).customItemManager.getItem("scp_018") as SCP018Item
    entities.forEach { (it as Snowball).item = scpItem.createItem() }

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
      if (newVelocity.length() > 2F) {
        newVelocity.normalize().multiply(2F)
      }

      val scp018 = SCP018.entityMap[projectile]
      if (scp018 != null) {
        println("Found existing SCP-018 instance corresponding to projectile entity.")
        println("Remove old entity instance from map.")
        SCP018.entityMap.remove(projectile)
      } else {
        println("No SCP-018 instance found. It must be a newly thrown instance.")
      }

      val newProjectile = createEntity(projectile.location, scp018)
      newProjectile.velocity = newVelocity

      projectile.remove()
    }
  }

  class SCP018(plugin: CustomEntityPlugin, var entity: Snowball) : BukkitRunnable() {

    companion object {
      val entityMap: MutableMap<Entity, SCP018> = mutableMapOf()
    }

    init {
        entityMap[entity] = this
        runTaskTimer(plugin, 0L, 1L)
    }

    override fun run() {
      println("Running SCP-018 runnable.")
      println("Number of SCP-018 entities in the world: " + entityMap.size)

      if (!entity.location.block.isEmpty) {
        println("SCP-018 stuck in block! At tick " + Bukkit.getServer().currentTick)
      }

      entity.world.spawnParticle(
        Particle.BLOCK,
        entity.location,
        1,
        0.5, 0.5, 0.5,
        Material.REDSTONE_BLOCK.createBlockData()
      )
    }
  }
}
