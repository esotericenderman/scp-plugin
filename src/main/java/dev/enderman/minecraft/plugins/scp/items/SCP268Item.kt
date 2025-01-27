package dev.enderman.minecraft.plugins.scp.items

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import dev.enderman.minecraft.plugins.scp.SCPPlugin
import foundation.esoteric.minecraft.plugins.library.item.TexturedItem
import gg.flyte.twilight.extension.hidePlayer
import gg.flyte.twilight.extension.showPlayer
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class SCP268Item(plugin: SCPPlugin) : TexturedItem(plugin, "scp_268", Material.LEATHER_HELMET) {
  @EventHandler
  private fun onEquip(event: PlayerArmorChangeEvent) {
    val player = event.player

    val newItem = event.newItem
    val oldItem = event.oldItem

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
    }

    if (isItem(oldItem)) {
      plugin.logger.info("[SCP-268] The player has taken off the hat... now visible.")
      player.showPlayer(plugin, player)
      player.showPlayer()

      player.removePotionEffect(
        PotionEffectType.INVISIBILITY
      )
    }
  }
}
