package fr.kosmosuniverse.kems.core;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author KosmosUniverse
 */
public enum Ranks {
    HEROBRINE(10000, null, "HEROBRINE", Zombie.class, Stream.of(new String[][] {
            {EquipmentSlot.HAND.toString(), Material.NETHERITE_SWORD.toString()},
            {EquipmentSlot.HEAD.toString(), Material.NETHERITE_HELMET.toString()},
            {EquipmentSlot.CHEST.toString(), Material.NETHERITE_CHESTPLATE.toString()},
            {EquipmentSlot.LEGS.toString(), Material.NETHERITE_LEGGINGS.toString()},
            {EquipmentSlot.FEET.toString(), Material.NETHERITE_BOOTS.toString()},
    }).collect(Collectors.toMap(data -> EquipmentSlot.valueOf(data[0]), data -> new ItemStack(Objects.requireNonNull(Material.getMaterial(data[1]))))),
            PotionEffectType.RESISTANCE, PotionEffectType.FIRE_RESISTANCE),
    WARDEN(8000, HEROBRINE, "WARDEN", Zombie.class, Stream.of(new String[][] {
            {EquipmentSlot.HAND.toString(), Material.NETHERITE_SWORD.toString()},
            {EquipmentSlot.HEAD.toString(), Material.NETHERITE_HELMET.toString()},
            {EquipmentSlot.CHEST.toString(), Material.NETHERITE_CHESTPLATE.toString()},
            {EquipmentSlot.LEGS.toString(), Material.NETHERITE_LEGGINGS.toString()},
            {EquipmentSlot.FEET.toString(), Material.NETHERITE_BOOTS.toString()},
    }).collect(Collectors.toMap(data -> EquipmentSlot.valueOf(data[0]), data -> new ItemStack(Objects.requireNonNull(Material.getMaterial(data[1])))))),
    WITHER(6000, WARDEN, "WITHER", Evoker.class, null),
    ENDER_DRAGON(5000, WITHER, "ENDER DRAGON", Vindicator.class, Stream.of(new String[][] {
            {EquipmentSlot.HAND.toString(), Material.DIAMOND_AXE.toString()},
    }).collect(Collectors.toMap(data -> EquipmentSlot.valueOf(data[0]), data -> new ItemStack(Objects.requireNonNull(Material.getMaterial(data[1])))))),
    EVOKER(3000, ENDER_DRAGON, "EVOKER", Witch.class, null),
    WITHER_SKELETON(2000, EVOKER, "WITHER SKELETON", Witch.class, null),
    WITCH(1000, WITHER_SKELETON, "WITCH", Skeleton.class, Stream.of(new String[][] {
            {EquipmentSlot.HAND.toString(), Material.BOW.toString()},
            {EquipmentSlot.HEAD.toString(), Material.DIAMOND_HELMET.toString()},
            {EquipmentSlot.CHEST.toString(), Material.DIAMOND_CHESTPLATE.toString()},
    }).collect(Collectors.toMap(data -> EquipmentSlot.valueOf(data[0]), data -> new ItemStack(Objects.requireNonNull(Material.getMaterial(data[1])))))),
    SKELETON(900, WITCH, "SKELETON", Skeleton.class, Stream.of(new String[][] {
            {EquipmentSlot.HAND.toString(), Material.BOW.toString()},
            {EquipmentSlot.HEAD.toString(), Material.IRON_HELMET.toString()},
    }).collect(Collectors.toMap(data -> EquipmentSlot.valueOf(data[0]), data -> new ItemStack(Objects.requireNonNull(Material.getMaterial(data[1])))))),
    ZOMBIE(700, SKELETON, "ZOMBIE", Skeleton.class, Stream.of(new String[][] {
            {EquipmentSlot.HAND.toString(), Material.BOW.toString()},
            {EquipmentSlot.HEAD.toString(), Material.IRON_HELMET.toString()},
    }).collect(Collectors.toMap(data -> EquipmentSlot.valueOf(data[0]), data -> new ItemStack(Objects.requireNonNull(Material.getMaterial(data[1])))))),
    LLAMA(500, ZOMBIE, "LLAMA", Zombie.class, Stream.of(new String[][] {
            {EquipmentSlot.HEAD.toString(), Material.IRON_HELMET.toString()},
    }).collect(Collectors.toMap(data -> EquipmentSlot.valueOf(data[0]), data -> new ItemStack(Objects.requireNonNull(Material.getMaterial(data[1])))))),
    VILLAGER(250, LLAMA, "VILLAGER", Zombie.class, Stream.of(new String[][] {
            {EquipmentSlot.HEAD.toString(), Material.GOLDEN_HELMET.toString()},
    }).collect(Collectors.toMap(data -> EquipmentSlot.valueOf(data[0]), data -> new ItemStack(Objects.requireNonNull(Material.getMaterial(data[1])))))),
    BABY_VILLAGER(100, VILLAGER, "BABY VILLAGER", Zombie.class, Stream.of(new String[][] {
            {EquipmentSlot.HEAD.toString(), Material.LEATHER_HELMET.toString()},
            {EquipmentSlot.CHEST.toString(), Material.LEATHER_CHESTPLATE.toString()},
            {EquipmentSlot.LEGS.toString(), Material.LEATHER_LEGGINGS.toString()},
            {EquipmentSlot.FEET.toString(), Material.LEATHER_BOOTS.toString()},
    }).collect(Collectors.toMap(data -> EquipmentSlot.valueOf(data[0]), data -> new ItemStack(Objects.requireNonNull(Material.getMaterial(data[1])))))),
    SAND(0, BABY_VILLAGER, "SAND", Zombie.class, Stream.of(new String[][] {
            {EquipmentSlot.HEAD.toString(), Material.LEATHER_HELMET.toString()},
    }).collect(Collectors.toMap(data -> EquipmentSlot.valueOf(data[0]), data -> new ItemStack(Objects.requireNonNull(Material.getMaterial(data[1]))))));

    @Getter
    private final int points;
    @Getter
    private final Ranks next;
    @Getter
    private final String displayString;
    @Getter
    private final Class<? extends LivingEntity> mobClass;
    private final Map<EquipmentSlot, ItemStack> specialMobInv;
    private final List<PotionEffectType> specialMobEffects;

    Ranks(int points, Ranks next, String displayString, Class<? extends LivingEntity> mobClass, Map<EquipmentSlot, ItemStack> specialMobInv, PotionEffectType... specialMobEffects) {
        this.points = points;
        this.next = next;
        this.displayString = displayString;
        this.mobClass = mobClass;
        this.specialMobInv = specialMobInv;
        this.specialMobEffects = specialMobEffects == null ? null : Arrays.asList(specialMobEffects);
    }

    public void applyEntityStats(Entity e) {
        e.setGlowing(true);

        if (e instanceof LivingEntity le) {
            le.setRemoveWhenFarAway(false);
        }

        if (specialMobInv != null && e instanceof LivingEntity le) {
            specialMobInv.forEach((k, v) -> Objects.requireNonNull(le.getEquipment()).setItem(k, v));
        }

        if (specialMobEffects != null && e instanceof LivingEntity le) {
            specialMobEffects.forEach(pe -> le.addPotionEffect(new PotionEffect(pe, PotionEffect.INFINITE_DURATION, 1, true, true)));
        }
    }
}
