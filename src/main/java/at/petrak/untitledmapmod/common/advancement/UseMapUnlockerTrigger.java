package at.petrak.untitledmapmod.common.advancement;

import at.petrak.untitledmapmod.UntitledMapMod;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class UseMapUnlockerTrigger extends SimpleCriterionTrigger<UseMapUnlockerTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(UntitledMapMod.MOD_ID, "use_map_unlocker");

    private static final String TAG_IS_MINIMAP = "is_minimap";

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Instance createInstance(JsonObject json, EntityPredicate.Composite predicate,
        DeserializationContext ctx) {
        return new Instance(predicate, json.get(TAG_IS_MINIMAP).getAsBoolean());
    }

    public void trigger(ServerPlayer player, boolean usedMinimap) {
        super.trigger(player, inst -> inst.test(usedMinimap));
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        protected final boolean isMinimap;

        public Instance(EntityPredicate.Composite predicate, boolean isMinimap) {
            super(UseMapUnlockerTrigger.ID, predicate);
            this.isMinimap = isMinimap;
        }

        @Override
        public ResourceLocation getCriterion() {
            return ID;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext ctx) {
            var json = super.serializeToJson(ctx);
            json.addProperty(TAG_IS_MINIMAP, this.isMinimap);
            return json;
        }

        private boolean test(boolean isMinimap) {
            return this.isMinimap == isMinimap;
        }
    }
}
