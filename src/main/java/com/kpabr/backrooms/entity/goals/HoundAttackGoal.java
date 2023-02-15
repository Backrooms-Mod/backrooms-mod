package com.kpabr.backrooms.entity.goals;

import com.kpabr.backrooms.entity.HoundEntity;
import com.kpabr.backrooms.init.BackroomsSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.util.Hand;

public class HoundAttackGoal extends MeleeAttackGoal {

    public HoundAttackGoal(HoundEntity hound, double speed) {
        super(hound, speed, false);
    }

    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
        final double d = this.getSquaredMaxAttackDistance(target);
        if (squaredDistance <= d && this.getCooldown() <= 0) {
            this.resetCooldown();
            this.mob.swingHand(Hand.MAIN_HAND);
            this.mob.tryAttack(target);
            this.mob.playSound(BackroomsSounds.HOUND_ATTACK, 10f, 10f);
        }
    }
}