package snownee.boattweaks.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;

public interface BoatTweaksKubeJSEvents {

	EventGroup GROUP = EventGroup.of("BoatTweaksEvents");

	EventHandler ON_SPECIAL_BLOCK = GROUP.startup("onSpecialBlock", () -> SpecialBlockEventJS.class).extra(Extra.ID);

}