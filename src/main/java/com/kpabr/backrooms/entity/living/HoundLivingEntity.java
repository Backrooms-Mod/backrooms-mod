package com.kpabr.backrooms.entity.living;

import com.kpabr.backrooms.entity.goals.HoundRunningGoal;
import com.kpabr.backrooms.entity.goals.SubmissionGoal;
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
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Random;

public class HoundLivingEntity extends HostileEntity implements IAnimatable {
    public Long attacktimer = 18L;
    private Long runtimer = 60L;
    private Random random = new Random(123456);
    private Long lookaroundtimerON = random.nextLong(6000L);
    private Long lookaroundtimerOFF = 50L;
    public Long LookaroundtimerFOR = 0L;
    public BlockPos pos = this.getBlockPos();

    private final double speed = 1f;


    private static final TrackedData<Boolean> IS_INVICINITY_OF_PLAYER = DataTracker.registerData(HoundLivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Boolean> ATTACKING = DataTracker.registerData(HoundLivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_LOOKING_AROUND = DataTracker.registerData(HoundLivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private final AnimationFactory factory = new AnimationFactory(this);

    public HoundLivingEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 1;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IS_INVICINITY_OF_PLAYER, false);
        this.dataTracker.startTracking(ATTACKING, false);
        this.dataTracker.startTracking(IS_LOOKING_AROUND, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.IsAttacking()) {
            if(attacktimer == 20) {
                this.playSound(BackroomsSounds.HOUND_ATTACK, 10f, 10f);
            } else if (--attacktimer == 0L) {
                this.setIsAttacking(false);
                this.attacktimer = 20L;
            }
        }

        if (LookaroundtimerFOR != 0) {
            this.setVelocity(0, 0, 0);
            --LookaroundtimerFOR;
        }

        if (--lookaroundtimerON <= 0) {
            if (!this.IsLooking()) {
                this.lookaroundtimerON = random.nextLong(6000L);
                this.LookaroundtimerFOR = 50L;
                this.setIsLooking(true);
            }
        }
        if (this.IsLooking()) {
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


    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (IsLooking()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.hound.look", false));
            return PlayState.CONTINUE;
        }

        if (IsAttacking()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.hound.attack", false));
            return PlayState.CONTINUE;
        }

        if (event.isMoving()) {
            if (IsInVicinity()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.hound.run", true));
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.hound.walk", true));
            }
            return PlayState.CONTINUE;
        }
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.hound.idle", true));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new RevengeGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 0.4f, false));
        this.goalSelector.add(1, new HoundRunningGoal(this, 1f, true, 1));
        this.goalSelector.add(2, new SubmissionGoal(this, 200L));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 20.0f));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.4f, 1));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
    }

    public void setIsInvicinityOfPlayer(boolean isInvicinityOfPlayer) {
        this.dataTracker.set(IS_INVICINITY_OF_PLAYER, isInvicinityOfPlayer);
    }

    public void setIsAttacking(boolean isAttacking) {
        this.dataTracker.set(ATTACKING, isAttacking);
    }

    public void setIsLooking(boolean isLooking) {
        this.dataTracker.set(IS_LOOKING_AROUND, isLooking);
    }

    public boolean IsInVicinity() {
        return this.dataTracker.get(IS_INVICINITY_OF_PLAYER);
    }

    public boolean IsAttacking() {
        return this.dataTracker.get(ATTACKING);
    }

    public boolean IsLooking() {
        return this.dataTracker.get(IS_LOOKING_AROUND);
    }

    @Override
    public void swingHand(Hand hand) {
        this.setIsAttacking(true);
    }
}

