package name.trimsky.lib_ai.example.entity;

import name.trimsky.lib_ai.LibAI;
import name.trimsky.lib_ai.example.tasks.LookAtEntityTask;
import name.trimsky.lib_ai.tasks.SingleTask;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ExampleEntity extends PathAwareEntity implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);
    private final long entityId;

    public ExampleEntity(EntityType<ExampleEntity> entityType, World world) {
        super(entityType, world);
        entityId = LibAI.generateNewUniqueId(world, new IdleTask(this));
    }

    @Override
    public void onRemoved() {
        LibAI.removeEntity(this.world, this.entityId);
        super.onRemoved();
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.example_entity.idle", true));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    static final class IdleTask extends SingleTask<ExampleEntity> {
        public static final float LOOK_CHANCE = 0.02F;
        public static final TargetPredicate lookAtPlayerPredicate = TargetPredicate.createNonAttackable().setBaseMaxDistance(6.0F);

        public IdleTask(ExampleEntity owner) {
            super(owner);
        }

        @Override
        public void tick() {
            if (this.owner.getRandom().nextFloat() >= LOOK_CHANCE) {
                final var targetPlayer = this.owner.world.getClosestPlayer(
                        lookAtPlayerPredicate,
                        this.owner,
                        this.owner.getX(),
                        this.owner.getEyeY(),
                        this.owner.getZ());

                if (targetPlayer != null) {
                    this.controller.pushState(new LookAtEntityTask<>(this.owner, targetPlayer, 6.0F));
                }
            }
        }
    }
}