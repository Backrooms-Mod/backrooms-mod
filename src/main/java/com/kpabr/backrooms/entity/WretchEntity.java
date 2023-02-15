package com.kpabr.backrooms.entity;

import com.google.common.collect.ImmutableBiMap;
import com.kpabr.backrooms.config.BackroomsConfig;
import com.kpabr.backrooms.util.SACallbackManager;
import com.kpabr.backrooms.util.ServerAnimationCallback;
import name.trimsky.lib_ai.LibAI;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Optional;
import java.util.function.Consumer;

public final class WretchEntity extends PathAwareEntity implements IAnimatable {
    private static final TrackedData<Integer> CURRENT_ANIMATION =
            DataTracker.registerData(WretchEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Optional<Text>> AI_TASK =
            DataTracker.registerData(WretchEntity.class, TrackedDataHandlerRegistry.OPTIONAL_TEXT_COMPONENT);
    private static final ImmutableBiMap<Integer, Consumer<AnimationEvent<WretchEntity>>> animationValue = new ImmutableBiMap.Builder<Integer, Consumer<AnimationEvent<WretchEntity>>>()
            .put(AnimationEnum.IDLING.ordinal(), (event) -> event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("animation.wretch.idle", true)))

            .put(AnimationEnum.MOVING.ordinal(), (event) -> event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("animation.wretch.walk", true)))

            .put(AnimationEnum.ATTACKING.ordinal(), (event) -> event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("animation.wretch.attack", false)))

            .put(AnimationEnum.SEARCHING.ordinal(), (event) -> event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("animation.wretch.search", true)))

            .buildOrThrow();

    private final AnimationFactory factory = new AnimationFactory(this);
    private final long uniqueId;

    public WretchEntity(EntityType<WretchEntity> entityType, World world) {
        super(entityType, world);
        this.ignoreCameraFrustum = true;

        this.uniqueId = LibAI.generateNewUniqueId(world, new WretchEntityTasks.IdleTask(this));
        this.dataTracker.startTracking(CURRENT_ANIMATION, AnimationEnum.IDLING.ordinal());
        this.dataTracker.startTracking(AI_TASK, Optional.empty());
    }

    public void setAnimation(AnimationEnum animationEnum) {
        this.dataTracker.set(CURRENT_ANIMATION, animationEnum.ordinal());
    }
    public void setAiTask(@NotNull Text text) {
        this.dataTracker.set(AI_TASK, Optional.of(text));
    }
    public Text getAiTask() {
        return this.dataTracker.get(AI_TASK).orElse(null);
    }

    @Override
    public Text getName() {
        if(BackroomsConfig.getInstance().aiDebug) {
            MutableText customName = super.getName().copy();
            Text aiTask = this.getAiTask();
            if (aiTask != null) customName.append(aiTask);

            return customName;
        } else {
            return super.getName();
        }
    }

    public void setAnimationCallback(ServerAnimationCallback callback, long milliseconds) {
        SACallbackManager.addNewCallback(callback, milliseconds);
    }

    @Range(from = 0, to = 3)
    public int getAnimation() {
        return this.dataTracker.get(CURRENT_ANIMATION);
    }

    @Override
    public void onRemoved() {
        LibAI.removeEntity(this.world, uniqueId);
        super.onRemoved();
    }

    public static DefaultAttributeContainer.Builder createWretchAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.134)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0);
    }

    private PlayState predicate(AnimationEvent<WretchEntity> event) {
        final var currentAnimation = animationValue.get(this.getAnimation());
        currentAnimation.accept(event);

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        var controller = new AnimationController<>(this, "controller", 2, this::predicate);
        animationData.addAnimationController(controller);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public enum AnimationEnum {
        IDLING,
        MOVING,
        ATTACKING,
        SEARCHING,
    }
}