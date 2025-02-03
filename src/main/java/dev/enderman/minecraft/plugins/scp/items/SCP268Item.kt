package dev.enderman.minecraft.plugins.scp.items

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import dev.enderman.minecraft.plugins.scp.SCPPlugin
import foundation.esoteric.minecraft.plugins.library.item.TexturedItem
import gg.flyte.twilight.extension.hidePlayer
import gg.flyte.twilight.extension.showPlayer
import org.bukkit.Material
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class SCP268Item(plugin: SCPPlugin) : TexturedItem(plugin, "scp_268", Material.LEATHER_HELMET) {
  @EventHandler
  private fun onEquip(event: PlayerArmorChangeEvent) {
    val player = event.player

    val newItem = event.newItem

    if (isItem(newItem)) {
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

      for (entity in player.world.livingEntities) {
        if (entity !is Mob) continue

        if (entity.target != player) continue

        entity.target = null
      }
    }
  }

  @EventHandler
  private fun onUnEquip(event: PlayerArmorChangeEvent) {
    val player = event.player
    val oldItem = event.oldItem

    if (isItem(oldItem)) {
      plugin.logger.info("[SCP-268] The player has taken off the hat... now visible.")
      player.showPlayer(plugin, player)
      player.showPlayer()

      player.removePotionEffect(
        PotionEffectType.INVISIBILITY
      )
    }
  }

  @EventHandler
  private fun onMobTarget(event: EntityTargetLivingEntityEvent) {
    val target = event.target ?: return

    val equipment = target.equipment ?: return

    val helmet = equipment.helmet

    if (!isItem(helmet)) return

    event.isCancelled = true
  }
}
