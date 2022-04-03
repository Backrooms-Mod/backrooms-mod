package com.kpabr.backrooms.init;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.fablabsmc.fablabs.api.bannerpattern.v1.LoomPattern;
import io.github.fablabsmc.fablabs.api.bannerpattern.v1.LoomPatterns;
import com.kpabr.backrooms.BackroomsMod;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BackroomsLoomPatterns {

	private static final Map<Identifier, LoomPattern> LOOM_PATTERNS = new LinkedHashMap<>();

	public static final LoomPattern PATTERNED = add("patterned", new LoomPattern(false));
	public static final LoomPattern STRIPED = add("striped", new LoomPattern(false));
	public static final LoomPattern DOTTED = add("dotted", new LoomPattern(false));
	public static final LoomPattern BLANK = add("blank", new LoomPattern(false));

	public static final LoomPattern RED_PATTERNED = add("red_patterned", new LoomPattern(false));
	public static final LoomPattern RED_STRIPED = add("red_striped", new LoomPattern(false));
	public static final LoomPattern RED_DOTTED = add("red_dotted", new LoomPattern(false));
	public static final LoomPattern RED_BLANK = add("red_blank", new LoomPattern(false));

	public static void init() {
		for (Identifier id : LOOM_PATTERNS.keySet()) {
			Registry.register(LoomPatterns.REGISTRY, id, LOOM_PATTERNS.get(id));
		}
	}

	private static LoomPattern add(String s, LoomPattern p) {
		Identifier id = BackroomsMod.id(s);
		LOOM_PATTERNS.put(id, p);
		return p;
	}

}
