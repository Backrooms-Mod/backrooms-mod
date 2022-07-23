package com.kpabr.backrooms.entity.goals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;


public class SubmissionGoal extends Goal {
private final PathAwareEntity mob;
private Long submissiontime;
private boolean hangery = false;

private boolean submitted = false;

private Random random = new Random();
    public SubmissionGoal(PathAwareEntity mob, Long submissiontime) {
        this.mob = mob;
        this.submissiontime = submissiontime;
    }





    @Override
    public boolean canStart() {
        LivingEntity player = this.mob.getTarget();
        if (!hangery && player != null) {
            Vec3d vec3d = player.getRotationVec(1.0F).normalize();
            Vec3d vec3d2 = new Vec3d(this.mob.getX() - player.getX(), this.mob.getEyeY() - player.getEyeY(), this.mob.getZ() - player.getZ());
            double d = vec3d2.length();
            vec3d2 = vec3d2.normalize();
            double e = vec3d.dotProduct(vec3d2);
            return e > 1.0D - 0.025D / d ? player.canSee(this.mob) : false;
        }
            return false;
    }

        @Override
    public boolean shouldRunEveryTick(){
            return true;
        }
        @Override
    public void start(){
        LivingEntity player = this.mob.getTarget();
        if(player != null) {

            submitted = true;
        }
    }
        @Override
    public void tick() {
            LivingEntity player = this.mob.getTarget();
            if (!hangery && submitted && player != null) {
                if (--submissiontime > 0L && player != null) {
                    this.mob.setVelocity(0, 0, 0);
                    this.mob.lookAt(player.getCommandSource().getEntityAnchor(), player.getPos());
                } else if (player != null) {
                        hangery = true;
                    }
                }
            }
        }

