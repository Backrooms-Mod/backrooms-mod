package com.kpabr.backrooms.entity.living;

import com.kpabr.backrooms.entity.goals.HoundAttackGoal;
import com.kpabr.backrooms.entity.goals.HoundRunningGoal;
import com.kpabr.backrooms.entity.goals.ControlGoal;
import com.kpabr.backrooms.init.BackroomsSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Random;

public class HoundEntity extends HostileEntity implements IAnimatable {
    private final Random random = new Random(123456);
    private Long lookaroundtimerON = random.nextLong(6000L);
    private Long lookaroundtimerOFF = 50L;
    public Long LookaroundtimerFOR = 0L;

    private static final TrackedData<Boolean> IS_INVICINITY_OF_PLAYER = DataTracker.registerData(HoundEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_LOOKING_AROUND = DataTracker.registerData(HoundEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private final AnimationFactory factory = new AnimationFactory(this);

    public HoundEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 1;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IS_INVICINITY_OF_PLAYER, false);
        this.dataTracker.startTracking(IS_LOOKING_AROUND, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (LookaroundtimerFOR != 0) {
            this.setVelocity(0, 0, 0);
            --LookaroundtimerFOR;
        }

        if (--lookaroundtimerON <= 0) {
            if (!this.isLooking()) {
                this.lookaroundtimerON = random.nextLong(6000L);
                this.LookaroundtimerFOR = 50L;
                this.setIsLooking(true);
            }
        }
        if (this.isLooking()) {
            if (--lookaroundtimerOFF <= 0) {
                this.setIsLooking(false);
                this.lookaroundtimerOFF = 50L;
            }
        }
    }


    public static DefaultAttributeContainer.Builder createHoundAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 25);
    }


    private PlayState predicate(AnimationEvent<HoundEntity> event) {
        if (isLooking()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.hound.look", false));
            return PlayState.CONTINUE;
        }
        if (event.isMoving()) {
            if (isInVicinity()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.hound.run", true));
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.hound.walk", true));
            }
            return PlayState.CONTINUE;
        }
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.hound.idle", true));
        return PlayState.CONTINUE;
    }

    private PlayState attackPredicate(AnimationEvent<HoundEntity> event) {
        if(this.handSwinging && event.getController().getAnimationState().equals(AnimationState.Stopped)) {
            event.getController().markNeedsReload();
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.hound.attack", false));
            this.handSwinging = false;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        var attackController = new AnimationController<>(this, "attackController", 0, this::attackPredicate);
        var controller = new AnimationController<>(this, "controller", 2, this::predicate);

        animationData.addAnimationController(attackController);
        animationData.addAnimationController(controller);
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new RevengeGoal(this));
        this.goalSelector.add(3, new HoundAttackGoal(this, 0.4f));
        this.goalSelector.add(1, new HoundRunningGoal(this, 1f, true, 1));
        this.goalSelector.add(2, new ControlGoal(this));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 20.0f));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.4f, 1));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
    }

    public void setIsInvicinityOfPlayer(boolean isInvicinityOfPlayer) {
        this.dataTracker.set(IS_INVICINITY_OF_PLAYER, isInvicinityOfPlayer);
    }

    public void setIsLooking(boolean isLooking) {
        this.dataTracker.set(IS_LOOKING_AROUND, isLooking);
    }

    public boolean isInVicinity() {
        return this.dataTracker.get(IS_INVICINITY_OF_PLAYER);
    }

    public boolean isLooking() {
        return this.dataTracker.get(IS_LOOKING_AROUND);
    }
}

