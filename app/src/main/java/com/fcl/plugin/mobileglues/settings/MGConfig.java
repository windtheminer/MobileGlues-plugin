package com.fcl.plugin.mobileglues.settings;

import androidx.annotation.Nullable;

import com.fcl.plugin.mobileglues.utils.Constants;
import com.fcl.plugin.mobileglues.utils.FileUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MGConfig {

    private int enableANGLE;
    private int enableNoError;

    public MGConfig(int enableANGLE, int enableNoError) {
        this.enableANGLE = enableANGLE;
        this.enableNoError = enableNoError;
    }

    public void setEnableANGLE(int enableANGLE) throws IOException {
        this.enableANGLE = enableANGLE;
        saveConfig();
    }

    public void setEnableNoError(int enableNoError) throws IOException {
        this.enableNoError = enableNoError;
        saveConfig();
    }

    public int getEnableANGLE() {
        return enableANGLE;
    }

    public int getEnableNoError() {
        return enableNoError;
    }

    private void saveConfig() throws IOException {
        String configStr = new Gson().toJson(this);
        FileUtils.writeText(new File(Constants.CONFIG_FILE_PATH), configStr);
    }

    @Nullable
    public static MGConfig loadConfig() throws IOException {
        if (!Files.exists(new File(Constants.CONFIG_FILE_PATH).toPath())) {
            Logger.getLogger("MG-Plugin").log(Level.INFO, "MG config file not found, use default.");
            return null;
        }
        String configStr = FileUtils.readText(new File(Constants.CONFIG_FILE_PATH));
        return new Gson().fromJson(configStr, MGConfig.class);
    }
}
