package com.example.wordmagic.spell;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SpellState {
    public static final Map<UUID, Set<Integer>> KNOWN = new ConcurrentHashMap<>();
    public static final Map<UUID, Integer> MANA = new ConcurrentHashMap<>();
    public static final Map<UUID, Integer> ACTIVE = new ConcurrentHashMap<>();

    public static Set<Integer> known(UUID id) { return KNOWN.computeIfAbsent(id, k -> new HashSet<>()); }
    public static int mana(UUID id) { return MANA.computeIfAbsent(id, k -> 100); }
    public static void setMana(UUID id, int v) { MANA.put(id, Math.max(0, Math.min(100, v))); }
    public static int active(UUID id) { return ACTIVE.getOrDefault(id, -1); }
    public static void setActive(UUID id, int v) { ACTIVE.put(id, v); }
}
