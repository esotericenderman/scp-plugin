package dev.enderman.minecraft.plugins.scp.items

import dev.enderman.minecraft.plugins.scp.SCPPlugin
import foundation.esoteric.minecraft.plugins.library.item.TexturedItem
import org.bukkit.Material

class SCP268Item(plugin: SCPPlugin) : TexturedItem(plugin, "scp_268", Material.LEATHER_HELMET) {

  init {
    println(this::class.qualifiedName.hashCode())
  }

}
