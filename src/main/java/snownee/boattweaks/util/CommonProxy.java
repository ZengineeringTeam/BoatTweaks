package snownee.boattweaks.util;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import snownee.boattweaks.BoatSettings;
import snownee.boattweaks.BoatTweaks;
import snownee.boattweaks.BoatTweaksCommonConfig;
import snownee.boattweaks.duck.BTServerPlayer;
import snownee.boattweaks.network.SSyncSettingsPacket;
import snownee.boattweaks.network.SUpdateGhostModePacket;
import snownee.kiwi.config.KiwiConfigManager;
import snownee.kiwi.network.KPacketSender;

@Mod(BoatTweaks.ID)
public class CommonProxy {
	private static String version;

	public static String getVersion() {
		return version;
	}

	public CommonProxy(IEventBus modBus) {
		version = ModList.get()
				.getModFileById(BoatTweaks.ID)
				.getMods()
				.stream()
				.map(it -> it.getVersion().toString())
				.findFirst()
				.orElseThrow();

		modBus.addListener((FMLClientSetupEvent event) -> {
			ClientProxy.init();
		});

		NeoForge.EVENT_BUS.addListener((ServerStartingEvent event) -> {
			if (!event.getServer().isDedicatedServer()) {
				KiwiConfigManager.getHandler(BoatTweaksCommonConfig.class).refresh();
			}
			BoatTweaksCommonConfig.refresh();
		});

		NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
			var player = event.getEntity();
			KPacketSender.send(new SSyncSettingsPacket(BoatSettings.DEFAULT, Integer.MIN_VALUE), player);
			if (player.level().getGameRules().getBoolean(BoatTweaks.GHOST_MODE)) {
				KPacketSender.send(new SUpdateGhostModePacket(true), player);
			}
		});

		NeoForge.EVENT_BUS.addListener((PlayerEvent.Clone event) -> {
			boolean verified = ((BTServerPlayer) event.getOriginal()).boattweaks$isVerified();
			((BTServerPlayer) event.getEntity()).boattweaks$setVerified(verified);
		});
	}
}
