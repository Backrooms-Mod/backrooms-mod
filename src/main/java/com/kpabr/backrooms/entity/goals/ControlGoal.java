package com.kpabr.backrooms.entity.goals;

import com.kpabr.backrooms.entity.HoundEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

public class ControlGoal extends Goal {
    /** maxControlTime
     * How long player can control entity in ticks
     */
    public final static Integer maxControlTime = 200;
    private final HoundEntity hound;

    /**
     * The time how long player already controls the entity.
     *  When the value becomes {@link ControlGoal.maxControlTime} entity will become angry to player again
     */
    private Integer timeInControl;

    /**
     * Hound can only be controlled only once
     */
    private boolean isControlAvailable = true;
    public ControlGoal(HoundEntity hound) {
        this.hound = hound;
    }

    @Override
    public boolean canStart() {
        final LivingEntity player = this.hound.getTarget();
        if (isControlAvailable && player != null && player.canSee(this.hound)) {
            final Vec3d playerVec = player.getRotationVector().normalize();
            Vec3d houndAndPlayerPosDiff =
                    new Vec3d(this.hound.getX() - player.getX(), this.hound.getEyeY() - player.getEyeY(), this.hound.getZ() - player.getZ());
            final double vecLength = houndAndPlayerPosDiff.length();
            houndAndPlayerPosDiff = houndAndPlayerPosDiff.normalize();
            final double facing = playerVec.dotProduct(houndAndPlayerPosDiff);
            return facing > 1.0D - 0.025D / vecLength;
        }
        return false;
    }

    @Override
    public boolean shouldRunEveryTick(){
        return true;
    }

    @Override
    public boolean shouldContinue() {
        return this.hound.getTarget() != null;
    }

    @Override
    public void tick() {
        final LivingEntity player = this.hound.getTarget();
        if (isControlAvailable && player != null) {
            if (++timeInControl >= maxControlTime) {
                isControlAvailable = false;
            } else {
                this.hound.setVelocity(0, 0, 0);
                this.hound.lookAt(player.getCommandSource().getEntityAnchor(), player.getPos());
            }
        }
    }

    @Override
    public void start() {
        this.timeInControl = 0;
    }
}

