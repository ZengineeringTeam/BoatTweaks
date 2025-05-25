package snownee.boattweaks.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.registries.BuiltInRegistries;
import snownee.boattweaks.BoatSettings;
import snownee.boattweaks.BoatTweaks;
import snownee.boattweaks.BoatTweaksUtil;

public class BoatTweaksKubeJSPlugin extends KubeJSPlugin {

	@Override
	public void init() {
		BoatTweaks.LOGGER.info("KubeJS detected, loading Boat Tweaks KubeJS plugin");
		BoatTweaks.SPECIAL_BLOCK_LISTENERS.add((boat, blockState, blockPos) -> {
			if (!BoatTweaksKubeJSEvents.ON_SPECIAL_BLOCK.hasListeners()) {
				return;
			}
			SpecialBlockEventJS event = new SpecialBlockEventJS(boat, blockState, blockPos);
			BoatTweaksKubeJSEvents.ON_SPECIAL_BLOCK.post(ScriptType.STARTUP, BuiltInRegistries.BLOCK.getKey(blockState.getBlock()), event);
		});
	}

	@Override
	public void registerBindings(BindingsEvent event) {
		event.add("BoatTweaks", BoatTweaksUtil.class);
		event.add("BoatSettings", BoatSettings.class);
	}

	@Override
	public void registerEvents() {
		BoatTweaksKubeJSEvents.GROUP.register();
	}
}