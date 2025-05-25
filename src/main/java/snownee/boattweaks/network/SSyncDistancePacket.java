package snownee.boattweaks.network;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.vehicle.Boat;
import snownee.boattweaks.duck.BTMovementDistance;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "sync_distance", dir = KiwiPacket.Direction.PLAY_TO_CLIENT)
public class SSyncDistancePacket extends PacketHandler {
	public static SSyncDistancePacket I;

	public static void sync(Boat boat, ServerPlayer player) {
		I.send(player, buf -> {
			buf.writeInt(boat.getId());
			buf.writeFloat(((BTMovementDistance) boat).boattweaks$getDistance());
		});
	}

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor, FriendlyByteBuf buf, @Nullable ServerPlayer player) {
		int entityId = buf.readInt();
		float distance = buf.readFloat();
		return executor.apply(() -> {
			if (Objects.requireNonNull(Minecraft.getInstance().level).getEntity(entityId) instanceof Boat boat) {
				((BTMovementDistance) boat).boattweaks$setDistance(distance);
			}
		});
	}
}