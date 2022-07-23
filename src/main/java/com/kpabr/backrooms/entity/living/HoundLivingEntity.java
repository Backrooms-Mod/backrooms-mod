package com.kpabr.backrooms.entity.living;

import com.kpabr.backrooms.entity.goals.HoundRunningGoal;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.SeedCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.lwjgl.system.CallbackI;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;
import java.util.Random;

public class HoundLivingEntity extends HostileEntity implements IAnimatable {

    public Long attacktimer = 11L;
    private Long runtimer = 60L;
    private Random random = new Random(123456);
    private Long lookaroundtimerON = random.nextLong(6000L);
    private Long lookaroundtimerOFF = 50L;
    public Long LookaroundtimerFOR = 0L;
    public BlockPos pos = this.getBlockPos();

    private double speed = 0.5f;



    private static final TrackedData<Boolean> ISINVICINITYOFPLAYER = DataTracker.registerData(HoundLivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Boolean> ATTACKING = DataTracker.registerData(HoundLivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ISLOOKINGAROUND = DataTracker.registerData(HoundLivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private AnimationFactory factory = new AnimationFactory(this);

    public HoundLivingEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 1;
    }


    @Override
    protected void initDataTracker(){
        super.initDataTracker();
        this.dataTracker.startTracking(ISINVICINITYOFPLAYER, false);
        this.dataTracker.startTracking(ATTACKING, false);
        this.dataTracker.startTracking(ISLOOKINGAROUND, false);
    }

    @Override
    public void tick(){
        super.tick();
        if(this.IsAttacking()){
            if(--attacktimer <= 0L){
                this.setIsAttacking(false);
                this.attacktimer = 20L;
            }
        }

        if(LookaroundtimerFOR != 0){
            this.setVelocity(0, 0, 0);
            --LookaroundtimerFOR;
        }

        if(--lookaroundtimerON <= 0) {
            if (!this.IsLooking()) {
                this.lookaroundtimerON = random.nextLong(6000L);
                this.LookaroundtimerFOR = 50L;
                this.setIsLooking(true);
            }
        }
        if(this.IsLooking())
        {
            if(--lookaroundtimerOFF <= 0) {
                this.setIsLooking(false);
                this.lookaroundtimerOFF = 50L;
            }
        }
    }


    public static DefaultAttributeContainer.Builder createHoundAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35);
    }


    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if(IsLooking()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.hound.look", false));
            return PlayState.CONTINUE;
        }

        if(IsAttacking()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.hound.attack", false));
            return PlayState.CONTINUE;
        }

        if (event.isMoving()) {
            if (IsInVicinity()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.hound.run", true));
                return PlayState.CONTINUE;
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.hound.walk", true));
                return PlayState.CONTINUE;
            }
        }
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.hound.idle", true));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<HoundLivingEntity>(this, "controller", 0, this::predicate));
    }
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new RevengeGoal(this));
        this.goalSelector.add(1, new HoundRunningGoal(this, 0.5f, true, 1));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.4f, 1));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
    }

    public void setIsinvicinityofplayer(boolean isinvicinityofplayer){
        this.dataTracker.set(ISINVICINITYOFPLAYER, isinvicinityofplayer);
    }
    public void setIsAttacking(boolean isAttacking){
        this.dataTracker.set(ATTACKING, isAttacking);
    }
    public void setIsLooking(boolean islooking){
        this.dataTracker.set(ISLOOKINGAROUND, islooking);
    }
    public boolean IsInVicinity(){
        return this.dataTracker.get(ISINVICINITYOFPLAYER);
    }
    public boolean IsAttacking(){
        return this.dataTracker.get(ATTACKING);
    }
    public boolean IsLooking(){
        return this.dataTracker.get(ISLOOKINGAROUND);
    }

    @Override
    public void swingHand(Hand hand) {
        this.setIsAttacking(true);
    }
}
