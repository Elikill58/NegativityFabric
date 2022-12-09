package com.elikill58.negativity.fabric.impl.location;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.fabric.impl.block.FabricBlock;

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
	public Object getDefault() {
		return w;
	}

}
