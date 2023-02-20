package com.kpabr.backrooms.entity;

import com.kpabr.backrooms.init.BackroomsSounds;
import name.trimsky.lib_ai.tasks.SingleTask;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.kpabr.backrooms.entity.HoundEntity.AnimationEnum;

public final class HoundEntityTasks {
    private static final double SPEED_WHEN_IDLING_PER_SECOND = 0.9d;
    private static final double SPEED_WHEN_FOLLOWING_PER_SECOND = 2.8d;
    private static final double SPEED_WHEN_PLAYER_HID_PER_SECOND = 1.0d;
    private static final long ATTACK_ANIMATION_LENGTH_IN_MS = 480;
    /**
     * All information about all entities tasks presented in dev channel
     */
    static final class IdleTask extends SingleTask<HoundEntity> {
        private static final double CHANCE_TO_MOVE = 0.5d;

        public IdleTask(HoundEntity owner) {
            super(owner);
            this.owner.setAiTask(new LiteralText("Idling:Nothing"));
        }

        @Override
        public void tick() {
            if(this.owner.getTarget() == null && isPlayerFound()) return;

            if (canCreateNewPathWhenIdling())
            {
                final Vec3d randomPath = FuzzyTargeting.find(this.owner, 15, 4);

                if(randomPath != null && this.owner.getNavigation().startMovingTo(
                        randomPath.x, randomPath.y, randomPath.z, SPEED_WHEN_IDLING_PER_SECOND))
                {
                    this.owner.setAnimation(AnimationEnum.WALKING);
                    this.owner.setAiTask(new LiteralText("Idling:Moving"));
                }
                else
                {
                    this.owner.setAnimation(AnimationEnum.IDLING);
                    this.owner.setAiTask(new LiteralText("Idling:Idling"));
                }
            }
        }

        private boolean isPlayerFound() {
            final var player = this.owner.world.getClosestPlayer(
                    TargetPredicate.createAttackable().setBaseMaxDistance(20.0F).setPredicate(null),
                    this.owner, this.owner.getX(), this.owner.getEyeY(), this.owner.getZ());

            if (player != null && this.owner.canSee(player) && !player.isCreative() && !player.isSpectator()) {
                // I messed up something in the check, and it didn't work lol
                //if (Math.abs(MathUtil.getYawBetweenEntities(this.owner, player) - this.owner.getYaw()) <= 180) {
                this.controller.popState();
                this.controller.pushState(new AttackingTask(this.owner, player));
                this.owner.getNavigation().stop();
                return true;
                //}
            }
            return false;
        }

        private boolean canCreateNewPathWhenIdling() {
            return this.owner.getNavigation().isIdle() && this.owner.getRandom().nextFloat() > CHANCE_TO_MOVE;
        }
    }

    static final class AttackingTask extends SingleTask<HoundEntity> {
        private static final int cooldownBetweenAttacks = 25; // In ticks

        @NotNull
        private final PlayerEntity targetPlayer;
        private int cooldown; // In ticks

        public AttackingTask(HoundEntity owner, @NotNull PlayerEntity targetPlayer) {
            super(owner);
            this.targetPlayer = Objects.requireNonNull(targetPlayer, "targetPlayer parameter must be not null!");
            this.cooldown = 0;

            this.owner.setAiTask(new LiteralText("Attacking:Nothing"));
        }

        @Override
        public void tick() {
            if(this.cooldown > 0) --this.cooldown;

            if(!isPlayerTargetable(targetPlayer))
            {
                this.owner.getNavigation().stop();
                this.owner.setAnimation(AnimationEnum.IDLING);
                this.controller.popState();
                return;
            }

            final boolean cannotSeePlayer = !this.owner.canSee(targetPlayer);

            if(cannotSeePlayer)
            {
                this.controller.popState();
                this.owner.getNavigation().stop();
                if(this.owner.getNavigation().startMovingTo(this.targetPlayer, SPEED_WHEN_PLAYER_HID_PER_SECOND))
                {
                    this.controller.pushState(new SearchingPlayerTask(owner, targetPlayer));
                }
                return;
            }

            if(!isAttackAnimationInProgress()) {
                if(cooldown == 0 && this.owner.squaredDistanceTo(this.targetPlayer) <= getSquaredMaxAttackDistance())
                {
                    attackPlayer();
                    return;
                }

                if(this.owner.getNavigation().startMovingTo(this.targetPlayer, SPEED_WHEN_FOLLOWING_PER_SECOND))
                {
                    this.owner.setAnimation(AnimationEnum.RUNNING);
                    this.owner.setAiTask(new LiteralText("Attacking:Running"));
                }
            }
        }

        private float getSquaredMaxAttackDistance() {
            return this.owner.getWidth() * 2.0F * this.owner.getWidth() * 2.0F + this.targetPlayer.getWidth();
        }
        private static boolean isPlayerTargetable(PlayerEntity player) {
            return player.isAlive() && !player.isSpectator() && !player.isCreative();
        }
        private boolean isAttackAnimationInProgress() {
            return this.owner.getAnimation() == AnimationEnum.ATTACKING.ordinal();
        }
        private void attackPlayer() {
            if(this.owner.tryAttack(this.targetPlayer)) {
                this.owner.setAiTask(new LiteralText("Attacking:Attacking"));
                this.owner.playSound(
                        BackroomsSounds.HOUND_ATTACK,
                        1.0f,
                        (this.owner.getRandom().nextFloat() - this.owner.getRandom().nextFloat()) * 0.2F + 1.0F);

                this.owner.setAnimation(AnimationEnum.ATTACKING);
                this.owner.setAnimationCallback(
                        () -> {
                            this.cooldown = cooldownBetweenAttacks;
                            this.owner.setAnimation(AnimationEnum.IDLING);
                        },
                        ATTACK_ANIMATION_LENGTH_IN_MS);
            } else {
                this.owner.getNavigation().startMovingTo(this.targetPlayer, SPEED_WHEN_FOLLOWING_PER_SECOND);
            }
        }
    }

    static final class SearchingPlayerTask extends SingleTask<HoundEntity> {
        private final PlayerEntity targetPlayer;
        private static final long LOOK_ANIMATION_LENGTH_IN_MS = 480;
        private boolean isAnimationDone = false;

        public SearchingPlayerTask(HoundEntity owner, PlayerEntity targetPlayer) {
            super(owner);
            this.targetPlayer = targetPlayer;
            this.owner.setAiTask(new LiteralText("Searching"));
        }

        @Override
        public void tick() {
            if (this.owner.canSee(targetPlayer)) {
                this.controller.popState();
                this.controller.pushState(new AttackingTask(this.owner, this.targetPlayer));
            } else if (this.owner.getNavigation().isIdle()) {
                if(this.isAnimationDone) {
                    this.controller.popState();
                } else {
                    this.owner.setAiTask(new LiteralText("Searching:Looking"));

                    this.owner.setAnimation(AnimationEnum.LOOKING);
                    this.owner.setAnimationCallback(
                            () -> {
                                this.owner.setAnimation(AnimationEnum.IDLING);
                                this.isAnimationDone = true;
                            },
                            LOOK_ANIMATION_LENGTH_IN_MS);

                    this.controller.popState();
                }
            }
        }
    }
}
