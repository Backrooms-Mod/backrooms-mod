package com.kpabr.backrooms.entity.goals;

import com.kpabr.backrooms.entity.living.HoundLivingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class HoundRunningGoal extends MeleeAttackGoal {

    private final double RunningSpeed;

    public HoundRunningGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle, double runningSpeed) {
        super(mob, speed, pauseWhenMobIdle);
        RunningSpeed = runningSpeed;
    }

    @Override
    public boolean canStart() {
        return super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue();
    }
    @Override
    public void start() {
        LivingEntity livingEntity = this.mob.getTarget();
        Path path = this.mob.getNavigation().findPathTo(livingEntity, 0);
        if(path.getLength() > 15) {
            super.start();
        }
        else if(this.mob instanceof HoundLivingEntity){
            ((HoundLivingEntity)this.mob).setIsinvicinityofplayer(true);
            this.mob.getNavigation().startMovingAlong(path, this.RunningSpeed);
        }
    }
    protected void attack(LivingEntity target, double squaredDistance) {
        if (this.mob instanceof HoundLivingEntity) {
            ((HoundLivingEntity) this.mob).setIsAttacking(true);
            double d = this.getSquaredMaxAttackDistance(target);
            if (squaredDistance <= d && this.getCooldown() <= 0) {
                this.resetCooldown();
                this.mob.tryAttack(target);
            }
        }
        else {super.attack(target, squaredDistance);}
    }
}
