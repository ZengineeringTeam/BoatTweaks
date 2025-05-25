package snownee.boattweaks.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import snownee.boattweaks.BoatSettings;
import snownee.boattweaks.BoatTweaks;
import snownee.boattweaks.BoatTweaksCommonConfig;
import snownee.boattweaks.duck.BTServerPlayer;
import snownee.boattweaks.network.SSyncSettingsPacket;
import snownee.boattweaks.network.SUpdateGhostModePacket;
import snownee.kiwi.config.KiwiConfigManager;

@Mod(BoatTweaks.ID)
public class CommonProxy {
	private static String version;

	public static String getVersion() {
		return version;
	}

	public CommonProxy() {
		version = ModList.get()
				.getModFileById(BoatTweaks.ID)
				.getMods()
				.stream()
				.map(it -> it.getVersion().toString())
				.findFirst()
				.orElseThrow();

		FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLClientSetupEvent event) -> {
			ClientProxy.init();
		});

		MinecraftForge.EVENT_BUS.addListener((ServerStartingEvent event) -> {
			if (!event.getServer().isDedicatedServer()) {
				KiwiConfigManager.getHandler(BoatTweaksCommonConfig.class).refresh();
			}
			BoatTweaksCommonConfig.refresh();
		});

		MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
			var player = (ServerPlayer) event.getEntity();
			SSyncSettingsPacket.sync(player, BoatSettings.DEFAULT, Integer.MIN_VALUE);
			if (player.level().getGameRules().getBoolean(BoatTweaks.GHOST_MODE)) {
				SUpdateGhostModePacket.sync(player, true);
			}
		});

		MinecraftForge.EVENT_BUS.addListener((PlayerEvent.Clone event) -> {
			boolean verified = ((BTServerPlayer) event.getOriginal()).boattweaks$isVerified();
			((BTServerPlayer) event.getEntity()).boattweaks$setVerified(verified);
		});
	}
}
