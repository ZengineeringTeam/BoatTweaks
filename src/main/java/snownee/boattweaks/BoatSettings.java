package snownee.boattweaks;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.Reference2FloatMap;
import it.unimi.dsi.fastutil.objects.Reference2FloatMaps;
import it.unimi.dsi.fastutil.objects.Reference2FloatOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

// Normalize it to the vanilla values
public final class BoatSettings implements Cloneable {
	public static BoatSettings DEFAULT = new BoatSettings();

	public static final Codec<BoatSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					Codec.simpleMap(
							BuiltInRegistries.BLOCK.byNameCodec(),
							Codec.FLOAT,
							BuiltInRegistries.BLOCK).<Reference2FloatMap<Block>>xmap(
							Reference2FloatOpenHashMap::new,
							Reference2FloatMaps::unmodifiable).fieldOf("frictionOverrides").forGetter(BoatSettings::frictionOverrides),
					Codec.FLOAT.fieldOf("forwardForce").forGetter(BoatSettings::forwardForce),
					Codec.FLOAT.fieldOf("backwardForce").forGetter(BoatSettings::backwardForce),
					Codec.FLOAT.fieldOf("turningForce").forGetter(BoatSettings::turningForce),
					Codec.FLOAT.fieldOf("turningForceInAir").forGetter(BoatSettings::turningForceInAir),
					Codec.FLOAT.fieldOf("stepUpHeight").forGetter(BoatSettings::stepUpHeight),
					Codec.FLOAT.fieldOf("outOfControlTicks").forGetter(BoatSettings::outOfControlTicks),
					BuiltInRegistries.BLOCK.byNameCodec().fieldOf("boostingBlock").forGetter(BoatSettings::boostingBlock),
					Codec.INT.fieldOf("boostingTicks").forGetter(BoatSettings::boostingTicks),
					Codec.FLOAT.fieldOf("boostingForce").forGetter(BoatSettings::boostingForce),
					BuiltInRegistries.BLOCK.byNameCodec().fieldOf("ejectingBlock").forGetter(BoatSettings::ejectingBlock),
					Codec.FLOAT.fieldOf("ejectingForce").forGetter(BoatSettings::ejectingForce),
					Codec.FLOAT.fieldOf("wallHitSpeedLoss").forGetter(BoatSettings::wallHitSpeedLoss),
					Codec.INT.fieldOf("wallHitCooldown").forGetter(BoatSettings::wallHitCooldown),
					Codec.FLOAT.fieldOf("degradeForceLossPerMeter").forGetter(BoatSettings::degradeForceLossPerMeter),
					Codec.mapPair(Codec.INT.fieldOf("degradeForceLossStartFrom"), Codec.FLOAT.fieldOf("degradeForceMaxLoss"))
							.forGetter(it -> Pair.of(it.degradeForceLossStartFrom(), it.degradeForceMaxLoss())))
			.apply(instance, BoatSettings::new));
	public Reference2FloatMap<Block> frictionOverrides;
	public float forwardForce;
	public float backwardForce;
	public float turningForce;
	public float turningForceInAir;
	public float stepUpHeight;
	public float outOfControlTicks;
	public Block boostingBlock;
	public int boostingTicks;
	public float boostingForce;
	public Block ejectingBlock;
	public float ejectingForce;
	public float wallHitSpeedLoss;
	public int wallHitCooldown;
	public float degradeForceLossPerMeter;
	public int degradeForceLossStartFrom;
	public float degradeForceMaxLoss;

	public BoatSettings(
			Reference2FloatMap<Block> frictionOverrides,
			float forwardForce,
			float backwardForce,
			float turningForce,
			float turningForceInAir,
			float stepUpHeight,
			float outOfControlTicks,
			Block boostingBlock,
			int boostingTicks,
			float boostingForce,
			Block ejectingBlock,
			float ejectingForce,
			float wallHitSpeedLoss,
			int wallHitCooldown,
			float degradeForceLossPerMeter,
			int degradeForceLossStartFrom,
			float degradeForceMaxLoss) {
		this.frictionOverrides = frictionOverrides;
		this.forwardForce = forwardForce;
		this.backwardForce = backwardForce;
		this.turningForce = turningForce;
		this.turningForceInAir = turningForceInAir;
		this.stepUpHeight = stepUpHeight;
		this.outOfControlTicks = outOfControlTicks;
		this.boostingBlock = boostingBlock;
		this.boostingTicks = boostingTicks;
		this.boostingForce = boostingForce;
		this.ejectingBlock = ejectingBlock;
		this.ejectingForce = ejectingForce;
		this.wallHitSpeedLoss = wallHitSpeedLoss;
		this.wallHitCooldown = wallHitCooldown;
		this.degradeForceLossPerMeter = degradeForceLossPerMeter;
		this.degradeForceLossStartFrom = degradeForceLossStartFrom;
		this.degradeForceMaxLoss = degradeForceMaxLoss;
	}

	public BoatSettings(
			Reference2FloatMap<Block> frictionOverrides,
			float forwardForce,
			float backwardForce,
			float turningForce,
			float turningForceInAir,
			float stepUpHeight,
			float outOfControlTicks,
			Block boostingBlock,
			int boostingTicks,
			float boostingForce,
			Block ejectingBlock,
			float ejectingForce,
			float wallHitSpeedLoss,
			int wallHitCooldown,
			float degradeForceLossPerMeter,
			Pair<Integer, Float> pair0) {
		this(
				frictionOverrides,
				forwardForce,
				backwardForce,
				turningForce,
				turningForceInAir,
				stepUpHeight,
				outOfControlTicks,
				boostingBlock,
				boostingTicks,
				boostingForce,
				ejectingBlock,
				ejectingForce,
				wallHitSpeedLoss,
				wallHitCooldown,
				degradeForceLossPerMeter,
				pair0.getFirst(),
				pair0.getSecond());
	}

	public static BoatSettings fromLocal() {
		return new BoatSettings(
				BoatTweaksCommonConfig.frictionOverrides.entrySet()
						.stream()
						.reduce(
								new Reference2FloatOpenHashMap<>(BoatTweaksCommonConfig.frictionOverrides.size()), (acc, entry) -> {
									var block = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(entry.getKey()));
									if (block != Blocks.AIR) {
										acc.put(block, entry.getValue().floatValue());
									}
									return acc;
								}, (first, second) -> second),
				BoatTweaksCommonConfig.forwardForce,
				BoatTweaksCommonConfig.backwardForce,
				BoatTweaksCommonConfig.turningForce,
				BoatTweaksCommonConfig.turningForceInAir,
				BoatTweaksCommonConfig.stepUpHeight,
				BoatTweaksCommonConfig.outOfControlTicks,
				BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(BoatTweaksCommonConfig.boostingBlock)),
				BoatTweaksCommonConfig.boostingTicks,
				BoatTweaksCommonConfig.boostingForce,
				BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(BoatTweaksCommonConfig.ejectingBlock)),
				BoatTweaksCommonConfig.ejectingForce,
				BoatTweaksCommonConfig.wallHitSpeedLoss,
				BoatTweaksCommonConfig.wallHitCooldown,
				BoatTweaksCommonConfig.degradeForceLossPerMeter,
				BoatTweaksCommonConfig.degradeForceLossStartFrom,
				BoatTweaksCommonConfig.degradeForceMaxLoss);
	}

	public BoatSettings(
	) {
		this(

				Reference2FloatMaps.emptyMap(), 0.04F, 0.005F, 1F, 1F, 0F, 60F, Blocks.AIR, 0, 0F, Blocks.AIR, 0F, 0F, 0, 0F, 0, 0F);
	}


	public float getDegradedForce(float force, float distance) {
		if (degradeForceMaxLoss == 0 || degradeForceLossPerMeter == 0 || distance <= degradeForceLossStartFrom) {
			return force;
		}
		return force * (1 - Math.min((distance - degradeForceLossStartFrom) * degradeForceLossPerMeter, degradeForceMaxLoss));
	}

	@Override
	public BoatSettings clone() throws CloneNotSupportedException {
		return new BoatSettings(
				frictionOverrides,
				forwardForce,
				backwardForce,
				turningForce,
				turningForceInAir,
				stepUpHeight,
				outOfControlTicks,
				boostingBlock,
				boostingTicks,
				boostingForce,
				ejectingBlock,
				ejectingForce,
				wallHitSpeedLoss,
				wallHitCooldown,
				degradeForceLossPerMeter,
				degradeForceLossStartFrom,
				degradeForceMaxLoss
		);
	}

	public Reference2FloatMap<Block> frictionOverrides() {return frictionOverrides;}

	public float forwardForce() {return forwardForce;}

	public float backwardForce() {return backwardForce;}

	public float turningForce() {return turningForce;}

	public float turningForceInAir() {return turningForceInAir;}

	public float stepUpHeight() {return stepUpHeight;}

	public float outOfControlTicks() {return outOfControlTicks;}

	public Block boostingBlock() {return boostingBlock;}

	public int boostingTicks() {return boostingTicks;}

	public float boostingForce() {return boostingForce;}

	public Block ejectingBlock() {return ejectingBlock;}

	public float ejectingForce() {return ejectingForce;}

	public float wallHitSpeedLoss() {return wallHitSpeedLoss;}

	public int wallHitCooldown() {return wallHitCooldown;}

	public float degradeForceLossPerMeter() {return degradeForceLossPerMeter;}

	public int degradeForceLossStartFrom() {return degradeForceLossStartFrom;}

	public float degradeForceMaxLoss() {return degradeForceMaxLoss;}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final BoatSettings that = (BoatSettings) o;
		return Float.compare(forwardForce, that.forwardForce) == 0 && Float.compare(
				backwardForce,
				that.backwardForce
		) == 0 && Float.compare(turningForce, that.turningForce) == 0 &&
				Float.compare(turningForceInAir, that.turningForceInAir) == 0 && Float.compare(
				stepUpHeight,
				that.stepUpHeight
		) == 0 && Float.compare(outOfControlTicks, that.outOfControlTicks) == 0 &&
				boostingTicks == that.boostingTicks && Float.compare(boostingForce, that.boostingForce) == 0 &&
				Float.compare(ejectingForce, that.ejectingForce) == 0 && Float.compare(
				wallHitSpeedLoss,
				that.wallHitSpeedLoss
		) == 0 && wallHitCooldown == that.wallHitCooldown && Float.compare(
				degradeForceLossPerMeter,
				that.degradeForceLossPerMeter
		) == 0 && degradeForceLossStartFrom == that.degradeForceLossStartFrom &&
				Float.compare(degradeForceMaxLoss, that.degradeForceMaxLoss) == 0 &&
				Objects.equal(frictionOverrides, that.frictionOverrides) &&
				Objects.equal(boostingBlock, that.boostingBlock) &&
				Objects.equal(ejectingBlock, that.ejectingBlock);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(
				frictionOverrides,
				forwardForce,
				backwardForce,
				turningForce,
				turningForceInAir,
				stepUpHeight,
				outOfControlTicks,
				boostingBlock,
				boostingTicks,
				boostingForce,
				ejectingBlock,
				ejectingForce,
				wallHitSpeedLoss,
				wallHitCooldown,
				degradeForceLossPerMeter,
				degradeForceLossStartFrom,
				degradeForceMaxLoss
		);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("frictionOverrides", frictionOverrides)
				.add("forwardForce", forwardForce)
				.add("backwardForce", backwardForce)
				.add("turningForce", turningForce)
				.add("turningForceInAir", turningForceInAir)
				.add("stepUpHeight", stepUpHeight)
				.add("outOfControlTicks", outOfControlTicks)
				.add("boostingBlock", boostingBlock)
				.add("boostingTicks", boostingTicks)
				.add("boostingForce", boostingForce)
				.add("ejectingBlock", ejectingBlock)
				.add("ejectingForce", ejectingForce)
				.add("wallHitSpeedLoss", wallHitSpeedLoss)
				.add("wallHitCooldown", wallHitCooldown)
				.add("degradeForceLossPerMeter", degradeForceLossPerMeter)
				.add("degradeForceLossStartFrom", degradeForceLossStartFrom)
				.add("degradeForceMaxLoss", degradeForceMaxLoss)
				.toString();
	}
}
