package com.elikill58.negativity.fabric.impl.item;

import com.elikill58.negativity.api.item.Enchantment;

import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class FabricEnchants {
	
	public static net.minecraft.enchantment.Enchantment getFabricEnchant(Enchantment enchant) {
		return Registries.ENCHANTMENT.get(new Identifier(enchant.getId()));
	}
}
