package com.kpabr.backrooms.client.render.sky;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import net.minecraft.util.math.random.Random;
import java.util.function.Consumer;

import org.lwjgl.system.MemoryStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class SkyboxShaders {

	public static ShaderProgram SKYBOX_SHADER;

	public static void addAll(List<BakedQuad> list, BakedModel model, BlockState state, Direction dir, Random random) {
		list.addAll(model.getQuads(state, dir, random).stream()
				.filter((quad) -> quad.getSprite().getAtlasId().getPath().startsWith("sky/")).toList());
	}

	public static void addAll(List<BakedQuad> list, BakedModel model, BlockState state, Direction dir) {
		addAll(list, model, state, dir, Random.create(0));
	}

	public static void addAll(List<BakedQuad> list, BakedModel model, BlockState state) {
		addAll(list, model, state, (Direction) null);
	}

	public static void addAll(List<BakedQuad> list, BakedModel model, BlockState state, Random random) {
		addAll(list, model, state, null, random);
	}

	public static void quad(Consumer<Vector3f> consumer, Matrix4f matrix4f, BakedQuad quad) {
		int[] js = quad.getVertexData();
		int j = js.length / 8;
		MemoryStack memoryStack = MemoryStack.stackPush();
		try {
			ByteBuffer byteBuffer = memoryStack
					.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSizeByte());
			IntBuffer intBuffer = byteBuffer.asIntBuffer();

			for (int k = 0; k < j; ++k) {
				intBuffer.clear();
				intBuffer.put(js, k * 8, 8);
				float f = byteBuffer.getFloat(0);
				float g = byteBuffer.getFloat(4);
				float h = byteBuffer.getFloat(8);

				Vector4f vector4f = new Vector4f(f, g, h, 1.0F);
				matrix4f.transform(vector4f);
				consumer.accept(new Vector3f(vector4f.x, vector4f.y, vector4f.z));
			}
		} catch (Throwable var33) {
			try {
				memoryStack.close();
			} catch (Throwable var32) {
				var33.addSuppressed(var32);
			}
			throw var33;
		}
		memoryStack.close();
	}

}
