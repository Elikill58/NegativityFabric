package com.elikill58.negativity.fabric.impl.location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.fabric.impl.block.FabricBlock;
import com.elikill58.negativity.fabric.impl.entity.FabricEntity;
import com.elikill58.negativity.fabric.impl.entity.FabricEntityManager;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class FabricWorld extends World {

	private final net.minecraft.world.World w;

	public FabricWorld(net.minecraft.world.World w) {
		this.w = w;
	}

	@Override
	public String getName() {
		return w.getRegistryKey().getValue().toString();
	}

	@Override
	public Block getBlockAt0(int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		return new FabricBlock(w.getBlockState(pos).getBlock(), w, pos);
	}

	@Override
	public Difficulty getDifficulty() {
		return Difficulty.valueOf(w.getDifficulty().toString());
	}

	@Override
	public int getMaxHeight() {
		return w.getHeight();
	}

	@Override
	public int getMinHeight() {
		return -64;
	}

	@Override
	public List<Entity> getEntities() {
		List<Entity> list = new ArrayList<>();
		w.getProfiler().visit("getEntities");
		try {
			ServerEntityManager<net.minecraft.entity.Entity> entityManager = ReflectionUtils.getFirstWith(w, ServerWorld.class, ServerEntityManager.class);
			entityManager.getLookup().iterate().forEach(e -> list.add(FabricEntityManager.getEntity(e)));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return list;
	}

	@Override
	public Optional<Entity> getEntityById(int id) {
		net.minecraft.entity.Entity e = w.getEntityById(id);
		return e == null ? Optional.empty() : Optional.of(new FabricEntity<>(e));
	}

	@Override
	public boolean isChunkLoaded(int x, int z) {
		return w.isChunkLoaded(x, z);
	}

	@Override
	public List<Entity> getNearEntity(Location loc, double distance) {
		PlayerEntity cible = w.getClosestPlayer(loc.getX(), loc.getY(), loc.getZ(), distance, false);
		return cible == null ? Collections.emptyList() : cible.getPassengerList().stream().map(FabricEntityManager::getEntity).collect(Collectors.toList());
	}

	@Override
	public Object getDefault() {
		return w;
	}

}
