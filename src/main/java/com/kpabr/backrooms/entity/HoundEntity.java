package com.kpabr.backrooms.entity;

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

public class HoundEntity extends PathAwareEntity implements IAnimatable {
    private static final TrackedData<Integer> CURRENT_ANIMATION =
            DataTracker.registerData(HoundEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Optional<Text>> AI_TASK =
            DataTracker.registerData(HoundEntity.class, TrackedDataHandlerRegistry.OPTIONAL_TEXT_COMPONENT);

    private final AnimationFactory factory = new AnimationFactory(this);
    public final long uniqueId;

    public HoundEntity(EntityType<HoundEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 1;
        this.ignoreCameraFrustum = true;

        this.uniqueId = LibAI.generateNewUniqueId(world, new HoundEntityTasks.IdleTask(this));
    }

    @Override
    public void onRemoved() {
        LibAI.removeEntity(this.world, uniqueId);
        super.onRemoved();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CURRENT_ANIMATION, AnimationEnum.IDLING.ordinal());
        this.dataTracker.startTracking(AI_TASK, Optional.empty());
    }

    public static DefaultAttributeContainer.Builder createHoundAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 25)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.134);
    }

    @Range(from = 0, to = 4)
    public int getAnimation() {
        return this.dataTracker.get(CURRENT_ANIMATION);
    }

    public void setAnimation(AnimationEnum animationEnum) {
        this.dataTracker.set(CURRENT_ANIMATION, animationEnum.ordinal());
    }

    /**
     * @return Current AI task
     */
    public Text getAiTask() {
        return this.dataTracker.get(AI_TASK).orElse(null);
    }

    public void setAiTask(@NotNull Text text) {
        this.dataTracker.set(AI_TASK, Optional.of(text));
    }

    @Override
    public Text getName() {
        MutableText firstName = super.getName().copy();

        if(BackroomsConfig.getInstance().aiDebug) {
            Text aiTask = this.getAiTask();
            firstName.append("; ");
            if (aiTask != null) firstName.append(aiTask);
            return firstName;
        }
        return firstName;
    }

    @Override
    public boolean isCustomNameVisible() {
        return super.isCustomNameVisible() || BackroomsConfig.getInstance().aiDebug;
    }

    public void setAnimationCallback(ServerAnimationCallback callback, long milliseconds) {
        SACallbackManager.addNewCallback(callback, milliseconds);
    }

    private PlayState predicate(AnimationEvent<HoundEntity> event) {
        AnimationEnum.values()[this.getAnimation()]
                .animation.accept(event);

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
        IDLING((event) -> event.getController().setAnimation(
                new AnimationBuilder().addAnimation("animation.hound.idle", true))),
        WALKING((event) -> event.getController().setAnimation(
                new AnimationBuilder().addAnimation("animation.hound.walk", true))),
        RUNNING((event) -> event.getController().setAnimation(
                new AnimationBuilder().addAnimation("animation.hound.run", true))),
        ATTACKING((event) -> event.getController().setAnimation(
                new AnimationBuilder().addAnimation("animation.hound.attack", false))),
        LOOKING((event) -> event.getController().setAnimation(
                new AnimationBuilder().addAnimation("animation.hound.look", false)));

        private final Consumer<AnimationEvent<HoundEntity>> animation;
        AnimationEnum(Consumer<AnimationEvent<HoundEntity>> animation) {
            this.animation = animation;
        }
    }
}

