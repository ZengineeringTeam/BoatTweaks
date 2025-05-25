package snownee.boattweaks.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;
import dev.latvian.mods.kubejs.plugin.builtin.event.BlockEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public interface BoatTweaksKubeJSEvents {

	EventGroup GROUP = EventGroup.of("BoatTweaksEvents");

	TargetedEventHandler<ResourceKey<Block>> ON_SPECIAL_BLOCK =
			GROUP.startup("onSpecialBlock", () -> SpecialBlockEventJS.class).requiredTarget(BlockEvents.TARGET);

}
