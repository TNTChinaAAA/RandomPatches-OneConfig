package com.therandomlabs.randompatches;

import static com.therandomlabs.randompatches.core.RPTransformer.register;

import com.google.common.eventbus.Subscribe;
import com.therandomlabs.randomlib.TRLUtils;
import com.therandomlabs.randomlib.config.CommandConfigReload;
import com.therandomlabs.randomlib.config.ConfigManager;
import com.therandomlabs.randompatches.config.RPConfig;
import com.therandomlabs.randompatches.patch.EntityMinecartPatch;
import com.therandomlabs.randompatches.patch.NBTTagCompoundPatch;
import com.therandomlabs.randompatches.patch.ItemInWorldManagerPatch;
import com.therandomlabs.randompatches.patch.ServerRecipeBookHelperPatch;
import com.therandomlabs.randompatches.patch.TileEntityPistonPatch;
import com.therandomlabs.randompatches.patch.client.EntityRendererPatch;
import com.therandomlabs.randompatches.patch.client.GuiIngameMenuPatch;
import com.therandomlabs.randompatches.patch.client.GuiLanguageListPatch;
import com.therandomlabs.randompatches.patch.client.ItemPotionPatch;
import com.therandomlabs.randompatches.patch.client.MinecraftPatch;
import com.therandomlabs.randompatches.patch.client.NetworkManagerPatch;
import com.therandomlabs.randompatches.patch.packetsize.NettyCompressionDecoderPatch;
import com.therandomlabs.randompatches.patch.packetsize.PacketBufferPatch;
import com.therandomlabs.randompatches.util.RPUtils;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RandomPatches {
	public static final String MOD_ID = "randompatches";
	public static final String NAME = "RandomPatches";
	public static final String VERSION = "@VERSION@";
	public static final String MINECRAFT_VERSIONS = "[1.8,1.13)";
	public static final String CERTIFICATE_FINGERPRINT = "@FINGERPRINT@";

	public static final int SORTING_INDEX = Integer.MAX_VALUE - 10000;

	public static final boolean IS_DEOBFUSCATED =
			(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

	public static final String DEFAULT_WINDOW_TITLE = "Minecraft " + TRLUtils.MC_VERSION;

	public static final boolean BIGGER_PACKETS_PLEASE_INSTALLED =
			RPUtils.detect("net.elnounch.mc.biggerpacketsplz.BiggerPacketsPlzCoreMod");

	public static final boolean EIGENCRAFT_INSTALLED =
			RPUtils.detect("org.gr1m.mc.mup.core.MupCore");

	public static final boolean ICE_AND_FIRE_INSTALLED =
			RPUtils.detect("com.github.alexthe666.iceandfire.asm.IceAndFirePlugin");

	public static final boolean LITELOADER_INSTALLED =
			RPUtils.detect("com.mumfrey.liteloader.core.LiteLoader");

	public static final boolean LITTLETILES_INSTALLED =
			RPUtils.detect("com.creativemd.littletiles.LittleTilesCore");

	public static final boolean PARTICLE_FIXES_INSTALLED =
			RPUtils.detect("com.fuzs.particlefixes.ParticleFixes");

	public static final boolean REBIND_INSTALLED =
			RPUtils.detect("austeretony.rebind.common.core.ReBindCorePlugin");

	public static final boolean REPLAY_MOD_INSTALLED =
			RPUtils.detect("com.replaymod.core.ReplayMod");

	public static final boolean REBIND_NARRATOR_INSTALLED =
			RPUtils.detect("quaternary.rebindnarrator.RebindNarrator");

	public static final boolean SPONGEFORGE_INSTALLED =
			RPUtils.detect("org.spongepowered.mod.SpongeMod");

	public static final boolean UNRIDE_KEYBIND_INSTALLED =
			RPUtils.detect("io.github.barteks2x.unridekeybind.core.UnRideKeybindCoremod");

	public static final boolean VANILLAFIX_INSTALLED =
			RPUtils.detect("org.dimdev.vanillafix.VanillaFix");

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		if (TRLUtils.IS_CLIENT && RPConfig.Client.rpreloadclient &&
				TRLUtils.MC_VERSION_NUMBER > 8) {
			ClientCommandHandler.instance.registerCommand(CommandConfigReload.client(
					"rpreloadclient",
					RPConfig.class,
					(phase, command, sender) -> RPConfig.Window.setWindowSettings =
							phase == CommandConfigReload.ReloadPhase.POST
			));
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		ConfigManager.registerEventHandler();
	}

	@Subscribe
	public void serverStarting(FMLServerStartingEvent event) {
		if (RPConfig.Misc.rpreload && TRLUtils.MC_VERSION_NUMBER > 8) {
			event.registerServerCommand(CommandConfigReload.server(
					"rpreload",
					"rpreloadclient",
					RPConfig.class,
					"RandomPatches configuration reloaded!",
					(phase, command, sender) -> RPConfig.Window.setWindowSettings =
							phase == CommandConfigReload.ReloadPhase.POST
			));
		}
	}

	public static void containerInit() {
		if (!RPUtils.hasFingerprint(RandomPatches.class, CERTIFICATE_FINGERPRINT)) {
			if (IS_DEOBFUSCATED) {
				LOGGER.debug("Invalid fingerprint detected!");
			} else {
				LOGGER.error("Invalid fingerprint detected!");
			}
		}
	}

	public static void registerPatches() {
		if (RPConfig.Client.fastLanguageSwitch && TRLUtils.IS_CLIENT) {
			register("net.minecraft.client.gui.GuiLanguage$List", new GuiLanguageListPatch());
		}

		if (RPConfig.Client.patchMinecraftClass && TRLUtils.IS_CLIENT && !LITELOADER_INSTALLED) {
			register("net.minecraft.client.Minecraft", new MinecraftPatch());
		}

		if (RPConfig.Client.patchPotionGlint && TRLUtils.IS_CLIENT) {
			register("net.minecraft.item.ItemPotion", new ItemPotionPatch());
		}

		if (RPConfig.Client.patchTitleScreenOnDisconnect) {
			register("net.minecraft.client.gui.GuiIngameMenu", new GuiIngameMenuPatch());
		}

		if (RPConfig.Client.patchSmoothEyeLevelChanges && TRLUtils.IS_CLIENT) {
			register("net.minecraft.client.renderer.EntityRenderer", new EntityRendererPatch());
		}

		if (RPConfig.Misc.minecartAIFix && !EIGENCRAFT_INSTALLED) {
			register("net.minecraft.entity.item.EntityMinecart", new EntityMinecartPatch());
		}

		if (RPConfig.Misc.miningGhostBlocksFix) {
			register(
					"net.minecraft.server.management.ItemInWorldManager",
					new ItemInWorldManagerPatch()
			);
		}

		if (RPConfig.Misc.patchPacketSizeLimit && !BIGGER_PACKETS_PLEASE_INSTALLED &&
				!LITTLETILES_INSTALLED && !SPONGEFORGE_INSTALLED) {
			register(
					"net.minecraft.network.NettyCompressionDecoder",
					new NettyCompressionDecoderPatch()
			);
			register("net.minecraft.network.PacketBuffer", new PacketBufferPatch());
		}

		register("net.minecraft.tileentity.TileEntityPiston", new TileEntityPistonPatch());

		if (RPConfig.Misc.isRecipeBookNBTFixEnabled()) {
			register(
					"net.minecraft.util.ServerRecipeBookHelper",
					new ServerRecipeBookHelperPatch()
			);
		}

		if (RPConfig.Misc.skullStackingFix) {
			register("net.minecraft.nbt.NBTTagCompound", new NBTTagCompoundPatch());
		}

		if (RPConfig.Timeouts.patchNetworkManager && TRLUtils.IS_CLIENT) {
			register("net.minecraft.network.NetworkManager$5", new NetworkManagerPatch());
		}
	}
}
