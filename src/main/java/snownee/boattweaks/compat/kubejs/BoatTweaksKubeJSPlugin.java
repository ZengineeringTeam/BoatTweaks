package snownee.boattweaks.compat.kubejs;

import dev.latvian.mods.kubejs.core.RegistryObjectKJS;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.world.level.block.Block;
import snownee.boattweaks.BoatSettings;
import snownee.boattweaks.BoatTweaks;
import snownee.boattweaks.BoatTweaksUtil;

public class BoatTweaksKubeJSPlugin implements KubeJSPlugin {

	@Override
	public void init() {
		BoatTweaks.LOGGER.info("KubeJS detected, loading Boat Tweaks KubeJS plugin");
		BoatTweaks.SPECIAL_BLOCK_LISTENERS.add((boat, blockState, blockPos) -> {
			RegistryObjectKJS<Block> blockStateKjs = (RegistryObjectKJS<Block>) blockState;
			if (!BoatTweaksKubeJSEvents.ON_SPECIAL_BLOCK.hasListeners(blockStateKjs.kjs$getKey())) {
				return;
			}
			SpecialBlockEventJS event = new SpecialBlockEventJS(boat, blockState, blockPos);
			BoatTweaksKubeJSEvents.ON_SPECIAL_BLOCK.post(ScriptType.STARTUP, blockStateKjs.kjs$getKey(), event);
		});
	}

	@Override
	public void registerBindings(BindingRegistry bindings) {
		bindings.add("BoatTweaks", BoatTweaksUtil.class);
		bindings.add("BoatSettings", BoatSettings.class);
	}

	@Override
	public void registerEvents(EventGroupRegistry registry) {
		registry.register(BoatTweaksKubeJSEvents.GROUP);
	}
}
