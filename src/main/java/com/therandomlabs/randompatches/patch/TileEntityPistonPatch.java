package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randomlib.TRLUtils;
import com.therandomlabs.randompatches.config.RPConfig;
import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

//Ghost block fix taken from
//https://github.com/gnembon/carpetmod112/blob/staging/patches/net/minecraft/tileentity/
//TileEntityPiston.java.patch
//Thanks, gnembon!
public final class TileEntityPistonPatch extends Patch {
	public static final String SET_BLOCK_STATE = getName("setBlockState", "func_180501_a");

	@Override
	public boolean apply(ClassNode node) {
		final InsnList update = findInstructions(node, "update", "func_73660_a");

		if (RPConfig.Misc.pistonGhostBlocksFix && TRLUtils.MC_VERSION_NUMBER > 8) {
			patchUpdateGhostBlockFix(update);
		}

		return true;
	}

	private void patchUpdateGhostBlockFix(InsnList instructions) {
		AbstractInsnNode jumpIfNotPistonExtension = null;

		for (int i = 0; i < instructions.size(); i++) {
			jumpIfNotPistonExtension = instructions.get(i);

			if (jumpIfNotPistonExtension.getOpcode() == Opcodes.IF_ACMPNE) {
				break;
			}

			jumpIfNotPistonExtension = null;
		}

		final InsnList newInstructions = new InsnList();

		//Get TileEntityPiston (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get TileEntity#world
		newInstructions.add(new FieldInsnNode(
				Opcodes.GETFIELD,
				"net/minecraft/tileentity/TileEntity",
				getName("world", "field_145850_b"),
				"Lnet/minecraft/world/World;"
		));

		//Get TileEntityPiston (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get TileEntity#pos
		newInstructions.add(new FieldInsnNode(
				Opcodes.GETFIELD,
				"net/minecraft/tileentity/TileEntity",
				getName("pos", "field_174879_c"),
				"Lnet/minecraft/util/math/BlockPos;"
		));

		//Call TileEntityPistonHook#updatePistonExtension
		newInstructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				hookClass,
				"updatePistonExtension",
				"(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V",
				false
		));

		instructions.insert(jumpIfNotPistonExtension, newInstructions);
	}
}
