package me.voidxwalker.worldpreview.mixin.client;

import me.voidxwalker.worldpreview.WorldPreview;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Mutable @Shadow @Final private ClientChunkManager chunkManager;

    @Shadow @Final private ClientPlayNetworkHandler netHandler;

    /**
     * @author bluesmoke
     * @reason fixes NPE because netHandler is set to null for the preview
     */
    @Overwrite
    public DynamicRegistryManager getRegistryManager(){
        if(this.netHandler==null){
            return DynamicRegistryManager.create();
        }
        return this.netHandler.getRegistryManager();
    }

    @Inject(method ="<init>",at=@At("TAIL"))
    public void worldpreview_oldSodiumCompatibility(ClientPlayNetworkHandler networkHandler, ClientWorld.Properties properties, RegistryKey registryRef, DimensionType dimensionType, int loadDistance, Supplier profiler, WorldRenderer worldRenderer, boolean debugWorld, long seed, CallbackInfo ci){
        if(WorldPreview.camera==null&& WorldPreview.world!=null&& WorldPreview.spawnPos!=null){
            this.chunkManager=worldpreview_getChunkManager(loadDistance);
        }
    }

    private ClientChunkManager worldpreview_getChunkManager(int i){
        return new ClientChunkManager((ClientWorld) (Object)this, i);
    }
}
