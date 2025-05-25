package snownee.boattweaks.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import snownee.boattweaks.BoatSettings;
import snownee.boattweaks.duck.BTBoostingBoat;
import snownee.boattweaks.duck.BTConfigurableBoat;
import snownee.boattweaks.duck.BTMovementDistance;

@Mixin(value = Boat.class, priority = 900)
public abstract class BoatSettingsMixin implements BTConfigurableBoat {

	@Shadow
	private Boat.Status status;
	@Shadow
	private boolean inputUp;
	@Shadow
	private boolean inputDown;
	@Shadow
	private boolean inputLeft;
	@Shadow
	private boolean inputRight;
	@Shadow
	private float deltaRotation;
	@Unique
	private int boatTweaks$wallHitCd;
	@Unique
	@Nullable
	private BoatSettings boatTweaks$settings;

	@Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("RETURN"))
	private void init(CallbackInfo ci) {
		Boat boat = (Boat) (Object) this;
		boat.setMaxUpStep(BoatSettings.DEFAULT.stepUpHeight);
	}

	@WrapOperation(
			method = "getGroundFriction",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F")
	)
	private float boattweaks$getGroundFriction(
			final BlockState block,
			final LevelReader levelReader,
			final BlockPos blockPos,
			final Entity entity,
			final Operation<Float> original) {
		return boattweaks$getSettings().frictionOverrides().containsKey(block.getBlock())
				? boattweaks$getSettings().frictionOverrides().getFloat(block.getBlock())
				: original.call(block, levelReader, blockPos, entity);
	}

	@ModifyConstant(method = "controlBoat", constant = @Constant(floatValue = 1.0f))
	private float boattweaks$modifyRotation(final float original) {
		var settings = boattweaks$getSettings();
		var distance = ((BTMovementDistance) this).boattweaks$getDistance();
		if (status == Boat.Status.ON_LAND) {
			return settings.getDegradedForce(settings.turningForce(), distance);
		} else if (status == Boat.Status.IN_AIR) {
			return settings.getDegradedForce(settings.turningForceInAir(), distance);
		}
		return original;
	}

	@ModifyExpressionValue(method = "controlBoat", at = @At(value = "CONSTANT", args = "floatValue=0.04"))
	private float boattweaks$modifyForward(final float original) {
		if (status == Boat.Status.ON_LAND) {
			BoatSettings settings = boattweaks$getSettings();
			BTBoostingBoat boat = (BTBoostingBoat) this;
			return original + settings.forwardForce() + boat.boattweaks$getExtraForwardForce();
		}
		return original;
	}

	@ModifyExpressionValue(method = "controlBoat", at = @At(value = "CONSTANT", ordinal = 1, args = "floatValue=0.005"))
	private float boattweaks$modifyBackward(final float original) {
		if (status == Boat.Status.ON_LAND) {
			BoatSettings settings = boattweaks$getSettings();
			return original + settings.backwardForce();
		}
		return original;
	}

	@Inject(
			method = "controlBoat",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/vehicle/Boat;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"))
	private void boattweaks$degradeForce(CallbackInfo ci, @Local LocalFloatRef force) {
		BoatSettings settings = boattweaks$getSettings();
		float distance = ((BTMovementDistance) this).boattweaks$getDistance();
		force.set(settings.getDegradedForce(force.get(), distance));
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void tick(CallbackInfo ci) {
		Boat boat = (Boat) (Object) this;
		if (boatTweaks$wallHitCd > 0) {
			boatTweaks$wallHitCd--;
		} else if (boat.horizontalCollision) {
			BoatSettings settings = boattweaks$getSettings();
			boatTweaks$wallHitCd = settings.wallHitCooldown();
			float scale = 1 - settings.wallHitSpeedLoss();
			boat.setDeltaMovement(boat.getDeltaMovement().multiply(scale, 1, scale));
		}
	}

	@ModifyConstant(method = "tick", constant = @Constant(floatValue = 60F))
	private float modifyTimeOutTicks(float f) {
		return boattweaks$getSettings().outOfControlTicks();
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		if (boatTweaks$settings != null) {
			compoundTag.put(
					"BoatTweaksSettings",
					BoatSettings.CODEC.encodeStart(NbtOps.INSTANCE, boatTweaks$settings)
							.getOrThrow(false, it -> {throw new IllegalStateException(it);}));
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		if (compoundTag.contains("BoatTweaksSettings")) {
			boattweaks$setSettings(BoatSettings.CODEC.parse(NbtOps.INSTANCE, compoundTag.getCompound("BoatTweaksSettings"))
					.getOrThrow(false, it -> {throw new IllegalStateException(it);}));
		}
	}

	@Override
	public BoatSettings boattweaks$getSettings() {
		if (boatTweaks$settings == null) {
			return BoatSettings.DEFAULT;
		}
		return boatTweaks$settings;
	}

	@Override
	public void boattweaks$setSettings(@Nullable BoatSettings settings) {
		this.boatTweaks$settings = settings;
	}
}
