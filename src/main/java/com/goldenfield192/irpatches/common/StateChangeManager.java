package com.goldenfield192.irpatches.common;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.event.CommonEvents;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.HashSet;

//Handles smooth toggle
public class StateChangeManager {
    private static final HashMap<EntityRollingStock, HashSet<MutableTriple<String, Integer, Integer>>> controls = new HashMap<>();

//    private static HashMap<EntityRollingStock, HashMap<String>>

    public static void onTick(World world){
        if(world.isRemote)
            return;
        System.out.println(controls);
        HashMap<EntityRollingStock, HashSet<MutableTriple<String, Integer, Integer>>> removal = new HashMap<>();
        controls.forEach((stock, set) -> {
            removal.put(stock, new HashSet<>());
            set.forEach(data -> {
               if(data.getRight() <= 20){
                   stock.setControlPosition(data.getLeft(), (float) (stock.getControlPosition(data.getLeft()) + data.getMiddle() * 0.05));
                   data.setRight(data.getRight()+1);
               } else {
                   //TODO Causing memory leak
                   removal.get(stock).add(data);
               }
            });
        });
        removal.forEach((stock, triples) -> controls.get(stock).removeAll(triples));
    }

    public static void addChange(EntityRollingStock stock, String cg){
        if(stock.getWorld().isServer){
            controls.putIfAbsent(stock, new HashSet<>());
            controls.get(stock).add(MutableTriple.of(cg, (int) (stock.getControlPosition(cg) * (-2) + 1), 1));
        }
    }

    public enum ToggleType{
        LINEAR,
        SMOOTH,
        SMOOTHIN,
        SMOOTHOUT
    }
}
