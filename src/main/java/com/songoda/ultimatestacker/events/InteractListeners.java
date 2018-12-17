package com.songoda.ultimatestacker.events;

import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.entity.EntityStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class InteractListeners implements Listener {

    private final UltimateStacker instance;

    public InteractListeners(UltimateStacker instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onBread(EntityBreedEvent event) {
        event.getFather().removeMetadata("inLove", instance);
        event.getMother().removeMetadata("inLove", instance);
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        ItemStack item = player.getInventory().getItemInHand();

        if (!instance.getEntityStackManager().isStacked(entity)) return;

        if (item.getType() != Material.NAME_TAG && !correctFood(item, entity)) return;

        EntityStack stack = instance.getEntityStackManager().getStack(entity);

        if (stack.getAmount() == 1) return;

        if (item.getType() == Material.NAME_TAG)
            event.setCancelled(true);

        Entity newEntity = entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
        entity.setVelocity(getRandomVector());

        if (entity instanceof Ageable) {
            if (((Ageable) entity).isAdult()) {
                ((Ageable) newEntity).setAdult();
            } else {
                ((Ageable) entity).setBaby();
            }
        }

        if (entity instanceof Sheep) {
            Sheep sheep = ((Sheep) newEntity);
            sheep.setSheared(sheep.isSheared());
            sheep.setColor(sheep.getColor());
        }

        if (entity instanceof Villager) {
            Villager villager = ((Villager) newEntity);
            villager.setProfession(villager.getProfession());
        }


        instance.getEntityStackManager().addStack(new EntityStack(newEntity, stack.getAmount() - 1));
        stack.setAmount(1);
        instance.getEntityStackManager().removeStack(entity);

        if (item.getType() == Material.NAME_TAG) {
            entity.setCustomName(item.getItemMeta().getDisplayName());
        } else {
            entity.setMetadata("inLove", new FixedMetadataValue(instance, true));

            Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () -> {
                if (entity.isDead()) return;
                entity.removeMetadata("inLove", instance);
            }, 20 * 20);
        }
    }

    private Vector getRandomVector() {
        return new Vector(ThreadLocalRandom.current().nextDouble(-1, 1.01), 0, ThreadLocalRandom.current().nextDouble(-1, 1.01)).normalize().multiply(0.5);
    }

    private boolean correctFood(ItemStack is, Entity entity) {
        Material type = is.getType();
        switch (entity.getType()) {
            case COW:
            case SHEEP:
                return type == Material.WHEAT;
            case PIG:
                return (type == Material.CARROT || type == Material.BEETROOT || type == Material.POTATO);
            case CHICKEN:
                return type == Material.WHEAT_SEEDS
                        || type == Material.MELON_SEEDS
                        || type == Material.BEETROOT_SEEDS
                        || type == Material.PUMPKIN_SEEDS;
            case HORSE:
                return type == Material.GOLDEN_APPLE || type == Material.GOLDEN_CARROT;
            case WOLF:
                return type == Material.BEEF
                        || type == Material.CHICKEN
                        || type == Material.COD
                        || type == Material.MUTTON
                        || type == Material.PORKCHOP
                        || type == Material.RABBIT
                        || type == Material.SALMON
                        || type == Material.COOKED_BEEF
                        || type == Material.COOKED_CHICKEN
                        || type == Material.COOKED_COD
                        || type == Material.COOKED_MUTTON
                        || type == Material.COOKED_PORKCHOP
                        || type == Material.COOKED_RABBIT
                        || type == Material.COOKED_SALMON;
            case OCELOT:
                return type == Material.SALMON
                        || type == Material.COD
                        || type == Material.PUFFERFISH
                        || type == Material.TROPICAL_FISH;
            case RABBIT:
                return type == Material.CARROT || type == Material.GOLDEN_CARROT || type == Material.DANDELION;
            case LLAMA:
                return type == Material.HAY_BLOCK;
            case TURTLE:
                return type == Material.SEAGRASS;
        }
        return false;
    }
}