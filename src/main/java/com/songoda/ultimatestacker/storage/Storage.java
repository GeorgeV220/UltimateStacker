package com.songoda.ultimatestacker.storage;

import com.songoda.core.configuration.Config;
import com.songoda.ultimatestacker.UltimateStacker;

import java.util.List;

public abstract class Storage {

    protected final UltimateStacker instance;
    protected final Config dataFile;

    public Storage(UltimateStacker instance) {
        this.instance = instance;
        this.dataFile = new Config(instance, "data.yml");
        this.dataFile.setHeader("UltimateStacker Data File");
        this.dataFile.setAutosave(true).setAutosaveInterval(120);
    }

    public abstract boolean containsGroup(String group);

    public abstract List<StorageRow> getRowsByGroup(String group);

    public abstract void prepareSaveItem(String group, StorageItem... items);

    public void updateData(UltimateStacker instance) {
    }

    public abstract void doSave();

    public abstract void save();

    public abstract void makeBackup();

    public abstract void closeConnection();

}
