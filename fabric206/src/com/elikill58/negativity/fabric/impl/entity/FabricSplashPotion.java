package com.elikill58.negativity.fabric.impl.entity;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.entity.SplashPotion;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.fabric.impl.FabricPotionEffectType;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.PotionEntity;

public class FabricSplashPotion extends FabricEntity<PotionEntity> implements SplashPotion {
	
	public FabricSplashPotion(PotionEntity entity) {
		super(entity);
	}

	@Override
	public List<PotionEffect> getEffects() {
		List<PotionEffect> potions = new ArrayList<>();
		for(StatusEffectInstance effect : entity.getStack().get(DataComponentTypes.POTION_CONTENTS).getEffects()) {
			potions.add(new PotionEffect(FabricPotionEffectType.getEffect(effect.getEffectType()), effect.getDuration(), effect.getAmplifier()));
		}
		return potions;
	}
}
