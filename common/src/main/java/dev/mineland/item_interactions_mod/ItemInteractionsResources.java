//package dev.mineland.item_interactions_mod;
//
//
//import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.OakLeavesParticleSpawner;
//import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.GuiParticleSpawner;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class ItemInteractionsResources {
//    public static Map<String, GuiParticleSpawner> ParticleList = new HashMap<>();
//
//    public static void onReload() {
//        ParticleList.clear();
//
////        ParticleList.put(Items.OAK_LEAVES.toString(), new OakLeavesParticleSpawner());
//    }
//
//
//    public static GuiParticleSpawner getFromItem(ItemStack item) {
//        return getFromItem(item.getItem());
//    }
//
//    public static GuiParticleSpawner getFromItem(Item item) {
//        return ParticleList.get(item.toString());
//    }
//
//    public static boolean compareSpawner(GuiParticleSpawner one, GuiParticleSpawner other) {
//        return (one != null && other != null) && one.getName().equals(other.getName());
//    }
//
//    public static boolean compareSpawner(GuiParticleSpawner one, ItemStack item) {
//        return compareSpawner(one, getFromItem(item));
//    }
//    public static boolean compareSpawner(ItemStack one, ItemStack other) {
//        return compareSpawner(getFromItem(one), getFromItem(other));
//    }
//
//    public static boolean compareSpawner(ItemStack one, Item other) {
//        return compareSpawner(getFromItem(one), getFromItem(other));
//    }
//
//    public static boolean compareSpawner(Item one, Item other) {
//        return compareSpawner(getFromItem(one), getFromItem(other));
//    }
//    public static boolean compareSpawner(GuiParticleSpawner one, Item other) {
//        return compareSpawner(one, getFromItem(other));
//    }
//
//
//
//
//}
