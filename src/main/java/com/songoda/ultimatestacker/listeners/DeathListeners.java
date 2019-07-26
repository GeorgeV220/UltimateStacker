package com.songoda.ultimatestacker.listeners;

import com.songoda.lootables.loot.Drop;
import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.utils.Methods;
import com.songoda.ultimatestacker.utils.settings.Setting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DeathListeners implements Listener {

    private final UltimateStacker instance;

    public DeathListeners(UltimateStacker instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return;

        List<Drop> drops = instance.getLootablesManager().getDrops(event.getEntity());

        boolean custom = false;
        if (Setting.CUSTOM_DROPS.getBoolean()) {
            event.getDrops().clear();

            for (Drop drop : drops) {
                if (drop == null) continue;
                Methods.processDrop(event.getEntity(), drop);
            }
            custom = true;
        } else {
            for (ItemStack item : event.getDrops())
                drops.add(new Drop(item));
        }


        if (instance.getEntityStackManager().isStacked(event.getEntity()))
            instance.getEntityStackManager().getStack(event.getEntity())
                    .onDeath(event.getEntity(), drops, custom, event.getDroppedExp());
    }
}
