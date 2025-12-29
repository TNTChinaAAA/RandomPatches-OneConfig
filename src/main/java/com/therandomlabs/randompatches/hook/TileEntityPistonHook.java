package com.therandomlabs.randompatches.hook;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public final class TileEntityPistonHook {
	private TileEntityPistonHook() {}

	public static void updatePistonExtension(World world, BlockPos pos) {
		final IBlockState state = world.getBlockState(pos);

		world.markBlockForUpdate(
				pos.offset(state.getValue(BlockDirectional.FACING).getOpposite()));
	}
}
