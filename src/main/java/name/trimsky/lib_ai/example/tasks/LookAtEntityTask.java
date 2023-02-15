package name.trimsky.lib_ai.example.tasks;

import name.trimsky.lib_ai.tasks.SingleTask;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

public class LookAtEntityTask<T extends MobEntity> extends SingleTask<T> {
    protected LivingEntity target;
    private int lookTime;
    protected final float range;

    public LookAtEntityTask(T owner, LivingEntity target, float range) {
        super(owner);

        if(owner == null) {
            throw new NullPointerException("owner parameter must be not null!");
        }
        if(target == null) {
            throw new NullPointerException("target parameter must be not null!");
        }
        this.target = target;
        this.range = range;
        this.lookTime = 40 + this.owner.getRandom().nextInt(40);
    }

    @Override
    public void tick() {
        if (!this.target.isAlive()
                || this.lookTime <= 0
                || this.owner.squaredDistanceTo(this.target) > (double) (this.range * this.range)) {
            this.controller.popState();
        }
        double d = this.target.getEyeY();
        this.owner.getLookControl().lookAt(this.target.getX(), d, this.target.getZ());
        --this.lookTime;
    }
}
