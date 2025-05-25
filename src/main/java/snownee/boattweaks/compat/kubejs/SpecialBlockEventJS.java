package snownee.boattweaks.compat.kubejs;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.BlockState;

public class SpecialBlockEventJS implements KubeEntityEvent {
	public final Boat boat;
	public final BlockState blockState;
	public final BlockPos pos;

	public SpecialBlockEventJS(Boat boat, BlockState blockState, BlockPos pos) {
		this.boat = boat;
		this.blockState = blockState;
		this.pos = pos;
	}

	@Override
	public Entity getEntity() {
		return boat;
	}
}
