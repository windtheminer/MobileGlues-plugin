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
    private int enableExtGL43;
    private int enableExtComputeShader;

    public MGConfig(int enableANGLE, int enableNoError, int enableExtGL43, int enableExtComputeShader) {
        this.enableANGLE = enableANGLE;
        this.enableNoError = enableNoError;
        this.enableExtGL43 = enableExtGL43;
        this.enableExtComputeShader = enableExtComputeShader;
    }

    public void setEnableANGLE(int enableANGLE) throws IOException {
        this.enableANGLE = enableANGLE;
        saveConfig();
    }

    public void setEnableNoError(int enableNoError) throws IOException {
        this.enableNoError = enableNoError;
        saveConfig();
    }

    public void setEnableExtGL43(int enableExtGL43) throws IOException {
        this.enableExtGL43 = enableExtGL43;
        saveConfig();
    }

    public void setEnableExtComputeShader(int enableExtComputeShader) throws IOException {
        this.enableExtComputeShader = enableExtComputeShader;
        saveConfig();
    }

    public int getEnableANGLE() {
        return enableANGLE;
    }

    public int getEnableNoError() {
        return enableNoError;
    }

    public int getEnableExtGL43() {
        return enableExtGL43;
    }

    public int getEnableExtComputeShader() {
        return enableExtComputeShader;
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
