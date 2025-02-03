package dev.enderman.minecraft.plugins.scp.items

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import dev.enderman.minecraft.plugins.scp.SCPPlugin
import foundation.esoteric.minecraft.plugins.library.item.TexturedItem
import gg.flyte.twilight.extension.hidePlayer
import gg.flyte.twilight.extension.showPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.math.pow

private val unaffectedEntities = listOfNotNull(
  EntityType.WARDEN,
  EntityType.ENDERMAN,
  EntityType.GUARDIAN,
  EntityType.ILLUSIONER,
  EntityType.ELDER_GUARDIAN,
)

/**
 * Entities in this list would be able to see the player after they put on SCP-268, but only in the case that they were already attacking them.
 *
 * However, they won't be able to notice a player if they haven't noticed them already.
 */
private val lessAffectedEntities = listOfNotNull(
  EntityType.SHULKER
)

/**
 * These entities have methods of sensing the player other than sight, meaning SCP-268 might confuse them and disorient them, but ultimately they still might be able to sense the player through their other means.
 */
private val confusedEntities = listOfNotNull(
  EntityType.ZOMBIE,
  EntityType.ZOMBIE,
  EntityType.HUSK,
  EntityType.ZOMBIE_VILLAGER,
  EntityType.ZOMBIE_HORSE,
  EntityType.GIANT,
  EntityType.CAT,
  EntityType.WOLF,
  EntityType.EVOKER,
  EntityType.POLAR_BEAR,
  EntityType.HOGLIN,
  EntityType.FOX,
  EntityType.IRON_GOLEM,
  EntityType.ZOMBIFIED_PIGLIN,
  EntityType.CAVE_SPIDER,
  EntityType.SPIDER
)

private const val TWENTY_MINECRAFT_HOURS_IN_TICKS = 20_000
private const val INFINITE_LINGER_DURATION_IN_TICKS = 60_000
private const val INFINITE_LINGER_DURATION = -1.0

/**
 A formula to calculate how long the effect should linger
 based on how long someone has worn the hat:

 1000000000000 / (t-60000)^2 where t is in ticks that has been worn, and the output is how long in ticks the effect should linger, eventually becoming permanent.
 */

class SCP268Item(plugin: SCPPlugin) : TexturedItem(plugin, "scp_268", Material.LEATHER_HELMET) {

  private val timeHatPutOnKey = NamespacedKey(plugin, "time_scp_268_put_on")
  private val timeHatWornKey = NamespacedKey(plugin, "time_worn_scp_268")
  private val lingerTimeExpirationKey = NamespacedKey(plugin, "scp_268_linger_expiration")

  @EventHandler
  private fun onEquip(event: PlayerArmorChangeEvent) {
    val player = event.player

    val newItem = event.newItem

    if (!isItem(newItem)) return

    plugin.logger.info("[SCP-268] The player has put on the hat... now invisible.")
    player.hidePlayer(plugin, player)
    player.hidePlayer()

    player.addPotionEffect(
      PotionEffect(
        PotionEffectType.INVISIBILITY,
        PotionEffect.INFINITE_DURATION,
        1,
        false,
        false,
        false
      )
    )

    player.persistentDataContainer[timeHatPutOnKey, PersistentDataType.INTEGER] = Bukkit.getServer().currentTick

    for (entity in player.world.livingEntities) {
      if (entity !is Mob) continue

      if (confusedEntities.contains(entity.type) && Math.random() > 0.75) return

      if (unaffectedEntities.contains(entity.type) || lessAffectedEntities.contains(entity.type)) continue

      if (entity.target != player) continue

      entity.target = null
    }
  }

  @EventHandler
  private fun onUnEquip(event: PlayerArmorChangeEvent) {
    val player = event.player
    val oldItem = event.oldItem

    if (!isItem(oldItem)) return

    plugin.logger.info("[SCP-268] The player has taken off the hat... now visible.")
    player.showPlayer(plugin, player)
    player.showPlayer()

    player.removePotionEffect(
      PotionEffectType.INVISIBILITY
    )

    val timePutOn = player.persistentDataContainer[timeHatPutOnKey, PersistentDataType.INTEGER]!!
    val currentTime = Bukkit.getServer().currentTick

    val difference = currentTime - timePutOn

    var timeWorn = player.persistentDataContainer[timeHatWornKey, PersistentDataType.INTEGER] ?: 0

    timeWorn += difference

    player.persistentDataContainer[timeHatPutOnKey, PersistentDataType.INTEGER] = timeWorn

    if (timeWorn >= TWENTY_MINECRAFT_HOURS_IN_TICKS) makeEffectLinger(player, timeWorn)
  }

  @EventHandler
  private fun onMobTarget(event: EntityTargetLivingEntityEvent) {
    val hostile = event.entity

    if (confusedEntities.contains(hostile.type) && Math.random() > 0.5) return

    if (unaffectedEntities.contains(hostile.type)) return

    val target = event.target ?: return

    val equipment = target.equipment ?: return

    val helmet = equipment.helmet

    val lingerExpiry = target.persistentDataContainer[lingerTimeExpirationKey, PersistentDataType.DOUBLE] ?: 0.0
    val currentTick = Bukkit.getServer().currentTick.toDouble()

    if (isItem(helmet) || lingerExpiry > currentTick || lingerExpiry == INFINITE_LINGER_DURATION) {
      event.isCancelled = true
    }
  }

  @EventHandler
  private fun onJoinWithoutHatData(event: PlayerJoinEvent) {
    val player = event.player
    val dataContainer = player.persistentDataContainer

    val timePutOn = dataContainer[timeHatPutOnKey, PersistentDataType.INTEGER]

    if (timePutOn != null) return

    dataContainer[timeHatPutOnKey, PersistentDataType.INTEGER] = Bukkit.getServer().currentTick
  }

  private fun makeEffectLinger(entity: LivingEntity, timeWorn: Int) {
    val lingerTime = if (timeWorn >= INFINITE_LINGER_DURATION_IN_TICKS) INFINITE_LINGER_DURATION else 1000000000000.0 / (timeWorn.toDouble() - 60000.0).pow(2.0)

    val currentTick = Bukkit.getServer().currentTick.toDouble()

    entity.persistentDataContainer[lingerTimeExpirationKey, PersistentDataType.DOUBLE] = if (lingerTime == INFINITE_LINGER_DURATION) INFINITE_LINGER_DURATION else currentTick + lingerTime
  }
}
