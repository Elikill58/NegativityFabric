package com.elikill58.negativity.fabric.impl.item;

import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.fabric.FabricNegativity;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class FabricEnchants {
	
	public static net.minecraft.enchantment.Enchantment getFabricEnchant(Enchantment enchant) {
		return FabricNegativity.getInstance().getServer().getOverworld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).get(Identifier.of(enchant.getId()));
	}
}
