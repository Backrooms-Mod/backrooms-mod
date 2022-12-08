package com.kpabr.backrooms.entity.goals;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class SmartActiveTargetGoal extends Goal {
    public final static Predicate<LivingEntity> DEFAULT_STATE = Entity::isSneaking;
    private Predicate<LivingEntity> predicate;
    private final static int maxTimeWithoutVisibility = 60; // 3 seconds

    @Nullable
    private PlayerEntity targetPlayer = null;
    private final TargetPredicate targetPredicate;
    private final boolean checkVisibility;
    private final boolean checkCanNavigate;
    private int timeWithoutVisibility;
    private final MobEntity mob;

    public SmartActiveTargetGoal(MobEntity mob, boolean checkVisibility, boolean checkCanNavigate, Predicate<LivingEntity> targetPredicate) {
        this.setControls(EnumSet.of(Control.TARGET));
        this.mob = mob;
        this.checkVisibility = checkVisibility;
        this.checkCanNavigate = checkCanNavigate;
        this.predicate = targetPredicate;
        this.targetPredicate = TargetPredicate
                .createAttackable()
                .setBaseMaxDistance(getFollowRange())
                .setPredicate((entity) -> true);
    }

    @Override
    public boolean canStart() {
        this.findClosestTarget();
        // predicate is state, which can add multiple helpful conditions for player targeting
        if(!this.mob.getVisibilityCache().canSee(this.targetPlayer) && predicate.test(targetPlayer)) {
            return false;
        }
        return this.targetPlayer != null;
    }

    public void start() {
        this.mob.setTarget(this.targetPlayer);
        this.timeWithoutVisibility = 0;
    }

    public boolean shouldContinue() {
        LivingEntity entity = this.mob.getTarget();
        if (entity == null) {
            entity = this.targetPlayer;
        }

        if (!this.mob.canTarget(entity)) {
            return false;
        }
        else {
            final double distance = this.getFollowRange();
            if (this.mob.squaredDistanceTo(entity) > distance * distance) {
                return false;
            }
            else if (this.checkVisibility) {
                if (this.mob.getVisibilityCache().canSee(entity) || this.mob.squaredDistanceTo(entity) < 1.0F) {
                    this.timeWithoutVisibility = 0;
                } else if (++this.timeWithoutVisibility > toGoalTicks(maxTimeWithoutVisibility)) {
                    return false;
                }
            }
            this.mob.setTarget(entity);
            return true;
        }
    }

    @Override
    public void stop() {
        this.mob.setTarget(null);
        this.targetPlayer = null;
    }

    private double getFollowRange() {
        return this.mob.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
    }

    private void findClosestTarget() {
        this.targetPlayer = this.mob.world.getClosestPlayer(targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
    }
}
