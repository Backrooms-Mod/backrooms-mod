package com.kpabr.backrooms.entity;

import name.trimsky.lib_ai.tasks.SingleTask;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import static com.kpabr.backrooms.entity.WretchEntity.AnimationEnum;

import java.util.Objects;

public final class WretchEntityTasks {
    /**
     * All information about all entities tasks presented in dev channel
     */
    static final class IdleTask extends SingleTask<WretchEntity> {
        private static final double CHANCE_TO_MOVE = 0.5d;
        private static final double SPEED_WHEN_FOLLOWING_PER_SECOND = 0.9d;

        public IdleTask(WretchEntity owner) {
            super(owner);
            this.owner.setCustomName(new LiteralText("Idling:Nothing"));
        }

        @Override
        public void tick() {
            if(this.owner.getTarget() == null && isPlayerFound()) return;

            if (canCreateNewPathWhenIdling())
            {
                final Vec3d randomPath = FuzzyTargeting.find(this.owner, 15, 4);

                if(randomPath != null && this.owner.getNavigation().startMovingTo(
                        randomPath.x, randomPath.y, randomPath.z, SPEED_WHEN_FOLLOWING_PER_SECOND))
                {
                    this.owner.setAnimation(AnimationEnum.MOVING);
                    this.owner.setCustomName(new LiteralText("Idling:Moving"));
                }
                else
                {
                    this.owner.setAnimation(AnimationEnum.IDLING);
                    this.owner.setCustomName(new LiteralText("Idling:Idling"));
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

    static final class AttackingTask extends SingleTask<WretchEntity> {
        private static final double SPEED_WHEN_FOLLOWING_PER_SECOND = 0.9d;
        private static final double SPEED_WHEN_PLAYER_HID_PER_SECOND = 1.0d;
        private static final long ATTACK_ANIMATION_LENGTH_IN_MS = 583;
        private static final int cooldownBetweenAttacks = 50; // In ticks

        @NotNull
        private final PlayerEntity targetPlayer;
        private int cooldown; // In ticks

        public AttackingTask(WretchEntity owner, @NotNull PlayerEntity targetPlayer) {
            super(owner);
            this.targetPlayer = Objects.requireNonNull(targetPlayer, "targetPlayer parameter must be not null!");;
            this.cooldown = 0;

            owner.setCustomName(new LiteralText("Attacking:Nothing"));
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
                    this.owner.setAnimation(AnimationEnum.MOVING);
                    this.owner.setCustomName(new LiteralText("Attacking:Moving"));
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
                this.owner.setCustomName(new LiteralText("Attacking:Attacking"));

                this.owner.setAnimation(AnimationEnum.ATTACKING);
                this.owner.setAnimationCallback(
                        () -> this.owner.setAnimation(AnimationEnum.MOVING),
                        ATTACK_ANIMATION_LENGTH_IN_MS);

                this.cooldown = cooldownBetweenAttacks;
            } else {
                this.owner.getNavigation().startMovingTo(this.targetPlayer, SPEED_WHEN_FOLLOWING_PER_SECOND);
            }
        }
    }

    static final class SearchingPlayerTask extends SingleTask<WretchEntity> {
        private final PlayerEntity targetPlayer;

        public SearchingPlayerTask(WretchEntity owner, PlayerEntity targetPlayer) {
            super(owner);
            this.targetPlayer = targetPlayer;
            owner.setCustomName(new LiteralText("Searching player"));
        }

        @Override
        public void tick() {
            if (this.owner.canSee(targetPlayer)) {
                this.controller.popState();
                this.controller.pushState(new AttackingTask(this.owner, this.targetPlayer));
            } else if (this.owner.getNavigation().isIdle()) {
                // TODO: Search animation
                this.controller.popState();
            }
        }
    }
}
