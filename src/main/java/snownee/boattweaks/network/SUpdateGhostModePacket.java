package snownee.boattweaks.network;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import snownee.boattweaks.duck.BTClientPacketListener;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "update_ghost_mode", dir = KiwiPacket.Direction.PLAY_TO_CLIENT)
public class SUpdateGhostModePacket extends PacketHandler {
	public static SUpdateGhostModePacket I;

	public static void sync(ServerPlayer player, boolean value) {
		I.send(player, buf -> {
			buf.writeBoolean(value);
		});
	}

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor, FriendlyByteBuf buf, @Nullable ServerPlayer player) {
		boolean value = buf.readBoolean();
		return executor.apply(() -> {
			((BTClientPacketListener) Objects.requireNonNull(Minecraft.getInstance().getConnection())).boattweaks$setGhostMode(value);
		});
	}
}