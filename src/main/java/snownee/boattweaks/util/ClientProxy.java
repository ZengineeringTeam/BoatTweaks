package snownee.boattweaks.util;

import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import snownee.boattweaks.BoatSettings;

public class ClientProxy {
	public static void init() {
		NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> {
			BoatSettings.DEFAULT = new BoatSettings();
		});
	}
}
