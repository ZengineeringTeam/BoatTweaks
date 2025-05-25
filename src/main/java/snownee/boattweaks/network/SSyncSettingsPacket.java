package snownee.boattweaks.network;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.vehicle.Boat;
import snownee.boattweaks.BoatSettings;
import snownee.boattweaks.BoatTweaksUtil;
import snownee.boattweaks.duck.BTConfigurableBoat;
import snownee.boattweaks.util.CommonProxy;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "sync_settings", dir = KiwiPacket.Direction.PLAY_TO_CLIENT)
public class SSyncSettingsPacket extends PacketHandler {
	public static SSyncSettingsPacket I;

	public static void sync(ServerPlayer player, Boat boat) {
		BoatSettings boatSettings = BoatTweaksUtil.getBoatSettings(boat);
		sync(player, boatSettings, boat.getId());
	}

	public static void sync(ServerPlayer player, BoatSettings boatSettings, int entityId) {
		I.send(
				player, buf -> {
					buf.writeUtf(CommonProxy.getVersion());
					buf.writeWithCodec(NbtOps.INSTANCE, BoatSettings.CODEC, boatSettings);
					buf.writeInt(entityId);
				});
	}

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(
			Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor,
			FriendlyByteBuf buf,
			@Nullable ServerPlayer player) {
		if (!Objects.equals(buf.readUtf(), CommonProxy.getVersion())) {
			return null;
		}
		BoatSettings settings = buf.readWithCodec(NbtOps.INSTANCE, BoatSettings.CODEC);
		int entityId = buf.readInt();
		return executor.apply(() -> {
			if (entityId == Integer.MIN_VALUE) {
				BoatSettings.DEFAULT = settings;
				CPingServerPacket.ping();
			} else if (Objects.requireNonNull(Minecraft.getInstance().level).getEntity(entityId) instanceof Boat boat) {
				((BTConfigurableBoat) boat).boattweaks$setSettings(settings);
			}
		});
	}
}