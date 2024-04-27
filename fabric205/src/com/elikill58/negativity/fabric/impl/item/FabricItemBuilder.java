package com.elikill58.negativity.fabric.impl.item;

import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemFlag;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Unit;

public class FabricItemBuilder extends ItemBuilder {

	private final net.minecraft.item.ItemStack item;

	public FabricItemBuilder(ItemStack def) {
		this.item = (net.minecraft.item.ItemStack) def.getDefault();
	}
	
	public FabricItemBuilder(Material type) {
		this.item = new net.minecraft.item.ItemStack(((Item) type.getDefault()).asItem());
	}
	
	public FabricItemBuilder(OfflinePlayer owner) {
		this.item = new net.minecraft.item.ItemStack(Items.SKELETON_SKULL);
		//item.getOrCreateNbt().put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), new GameProfile(owner.getUniqueId(), owner.getName())));
	}

	@Override
	public ItemBuilder displayName(String displayName) {
		item.set(DataComponentTypes.CUSTOM_NAME, Text.of(ChatColor.WHITE + displayName));
		return this;
	}

	@Override
	public ItemBuilder resetDisplayName() {
		item.remove(DataComponentTypes.CUSTOM_NAME);
		return this;
	}

	@Override
	public ItemBuilder enchant(Enchantment enchantment, int level) {
		item.addEnchantment(FabricEnchants.getFabricEnchant(enchantment), level);
		return this;
	}
	
	@Override
	public ItemBuilder itemFlag(ItemFlag... itemFlag) {
		for(ItemFlag flag : itemFlag) {
			switch (flag) {
			case HIDE_ATTRIBUTES:
				item.set(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
				break;
			case HIDE_ENCHANTS:
				item.set(DataComponentTypes.HIDE_TOOLTIP, Unit.INSTANCE);
				break;
			case HIDE_UNBREAKABLE:
				item.set(DataComponentTypes.HIDE_TOOLTIP, Unit.INSTANCE);
				break;
			}
		}
		return this;
	}

	@Override
	public ItemBuilder unsafeEnchant(Enchantment enchantment, int level) {
		return enchant(enchantment, level);
	}

	@Override
	public ItemBuilder amount(int amount) {
		item.setCount(amount);
		return this;
	}

	@Override
	public ItemBuilder color(com.elikill58.negativity.api.colors.DyeColor color) {
		// TODO implement color
		//item.offer(Keys.DYE_COLOR, getColor(color));
		return this;
	}
	
	public DyeColor getColor(com.elikill58.negativity.api.colors.DyeColor color) {
		switch (color) {
		case GRAY:
			return DyeColor.GRAY;
		case LIME:
			return DyeColor.LIME;
		case RED:
			return DyeColor.RED;
		case WHITE:
			return DyeColor.WHITE;
		case YELLOW:
			return DyeColor.YELLOW;
		case LIGHT_BLUE:
			return DyeColor.LIGHT_BLUE;
		case MAGENTA:
			return DyeColor.MAGENTA;
		case ORANGE:
			return DyeColor.ORANGE;
		case PINK:
			return DyeColor.PINK;
		case PURPLE:
			return DyeColor.PURPLE;
		}
		return DyeColor.BROWN;
	}

	@Override
	public ItemBuilder lore(List<String> lore) {
		item.set(DataComponentTypes.LORE, new LoreComponent(lore.stream().map(Text::of).toList()));
		return this;
	}

	@Override
	public ItemBuilder lore(String... lore) {
		item.set(DataComponentTypes.LORE, new LoreComponent(Arrays.asList(lore).stream().map(Text::of).toList()));
		return this;
	}

	@Override
	public ItemBuilder addToLore(String... loreToAdd) {
		// Fix getting lore
		LoreComponent lore = item.get(DataComponentTypes.LORE);
		Arrays.asList(loreToAdd).stream().map(Text::of).forEach(lore::with);
		item.set(DataComponentTypes.LORE, lore);
		return this;
	}

	@Override
	public ItemStack build() {
		return new FabricItemStack(item);
	}

}
