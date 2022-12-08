package com.kpabr.backrooms.entity.goals;

import com.kpabr.backrooms.entity.living.WretchEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.util.Hand;

public class WretchAttackGoal extends MeleeAttackGoal {

    public WretchAttackGoal(WretchEntity wretch, double speed) {
        super(wretch, speed, false);
    }

    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
        double d = this.getSquaredMaxAttackDistance(target);
        if (squaredDistance <= d && this.getCooldown() <= 0) {
            this.resetCooldown();
            this.mob.swingHand(Hand.MAIN_HAND);
            this.mob.tryAttack(target);
        }
    }
}
