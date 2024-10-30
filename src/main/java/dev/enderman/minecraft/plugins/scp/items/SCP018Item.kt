package dev.enderman.minecraft.plugins.scp.items

import dev.enderman.minecraft.plugins.scp.SCPPlugin
import foundation.esoteric.minecraft.plugins.library.item.CustomItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class SCP018Item(plugin: SCPPlugin) : CustomItem(plugin, "scp_018", Material.SNOWBALL) {
  override fun generateCustomItem(baseCustomItem: ItemStack, player: Player): ItemStack {
    baseCustomItem.editMeta {
        meta ->
        meta.displayName(Component.text("SCP-018").color(NamedTextColor.RED))
    }

    return baseCustomItem
  }
}
