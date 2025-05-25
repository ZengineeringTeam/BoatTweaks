package snownee.boattweaks.util;

import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import snownee.boattweaks.BoatSettings;

public class ClientProxy {
	public static void init() {
		MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> {
			BoatSettings.DEFAULT = new BoatSettings();
		});
	}
}
