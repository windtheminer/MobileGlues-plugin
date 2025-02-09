package com.fcl.plugin.mobileglues;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fcl.plugin.mobileglues.settings.MGConfig;
import com.fcl.plugin.mobileglues.utils.ResultListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends ComponentActivity implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private MGConfig config = null;

    private Button openOptions;

    private LinearLayout optionLayout;
    private Spinner angleSpinner;
    private Spinner noErrorSpinner;
    private Switch extGL43Switch;
    private Switch extCsSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openOptions = findViewById(R.id.open_options);

        optionLayout = findViewById(R.id.option_layout);
        angleSpinner = findViewById(R.id.spinner_angle);
        noErrorSpinner = findViewById(R.id.spinner_no_error);
        extGL43Switch = findViewById(R.id.switch_ext_gl43);
        extCsSwitch = findViewById(R.id.switch_ext_cs);

        ArrayList<String> angleOptions = new ArrayList<>();
        angleOptions.add(getString(R.string.option_angle_disable_if_possible));
        angleOptions.add(getString(R.string.option_angle_enable_if_possible));
        angleOptions.add(getString(R.string.option_angle_disable));
        angleOptions.add(getString(R.string.option_angle_enable));
        ArrayAdapter<String> angleAdapter = new ArrayAdapter<>(this, R.layout.spinner, angleOptions);
        angleSpinner.setAdapter(angleAdapter);

        ArrayList<String> noErrorOptions = new ArrayList<>();
        noErrorOptions.add(getString(R.string.option_no_error_auto));
        noErrorOptions.add(getString(R.string.option_no_error_enable));
        noErrorOptions.add(getString(R.string.option_no_error_disable_pri));
        noErrorOptions.add(getString(R.string.option_no_error_disable_sec));
        ArrayAdapter<String> noErrorAdapter = new ArrayAdapter<>(this, R.layout.spinner, noErrorOptions);
        noErrorSpinner.setAdapter(noErrorAdapter);

        openOptions.setOnClickListener(view -> checkPermission());

        checkPermissionSilently();
    }

    private void showOptions() {
        try {
            config = MGConfig.loadConfig();

            if (config == null)
                config = new MGConfig(0, 0, false, false);

            if (config.getEnableANGLE() > 3 || config.getEnableANGLE() < 0)
                config.setEnableANGLE(0);
            if (config.getEnableNoError() > 3 || config.getEnableNoError() < 0)
                config.setEnableNoError(0);

            angleSpinner.setSelection(config.getEnableANGLE());
            noErrorSpinner.setSelection(config.getEnableNoError());
            extGL43Switch.setChecked(config.isEnableExtGL43());
            extCsSwitch.setChecked(config.isEnableExtComputeShader());

            angleSpinner.setOnItemSelectedListener(this);
            noErrorSpinner.setOnItemSelectedListener(this);
            extGL43Switch.setOnCheckedChangeListener(this);
            extCsSwitch.setOnCheckedChangeListener(this);

            openOptions.setVisibility(View.GONE);
            optionLayout.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            Logger.getLogger("MG").log(Level.SEVERE, "Failed to load config! Exception: ", e.getCause());
            Toast.makeText(this, getString(R.string.warning_load_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissionSilently() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                showOptions();
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showOptions();
            }
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                showOptions();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                ResultListener.startActivityForResult(this, intent, 1000, (requestCode, resultCode, data) -> {
                    if (requestCode == 1000) {
                        if (Environment.isExternalStorageManager()) {
                            showOptions();
                        } else {
                            recheckPermission();
                        }
                    }
                });
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showOptions();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                    recheckPermission();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 1000);
                }
            }
        }
    }

    private void recheckPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_msg));
        builder.setPositiveButton(R.string.dialog_positive, (dialogInterface, i) -> checkPermission());
        builder.setNegativeButton(R.string.dialog_negative, (dialogInterface, i) -> {
            // Do nothing here.
        });
        builder.create().show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == angleSpinner && config != null) {
            try {
                config.setEnableANGLE(i);
            } catch (IOException e) {
                Logger.getLogger("MG").log(Level.SEVERE, "Failed to save config! Exception: ", e.getCause());
                Toast.makeText(this, getString(R.string.warning_save_failed), Toast.LENGTH_SHORT).show();
            }
        }
        if (adapterView == noErrorSpinner && config != null) {
            try {
                config.setEnableNoError(i);
            } catch (IOException e) {
                Logger.getLogger("MG").log(Level.SEVERE, "Failed to save config! Exception: ", e.getCause());
                Toast.makeText(this, getString(R.string.warning_save_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == extGL43Switch && config != null) {
            try {
                config.setEnableExtGL43(b);
            } catch (IOException e) {
                Logger.getLogger("MG").log(Level.SEVERE, "Failed to save config! Exception: ", e.getCause());
                Toast.makeText(this, getString(R.string.warning_save_failed), Toast.LENGTH_SHORT).show();
            }
        }
        if (compoundButton == extCsSwitch && config != null) {
            try {
                config.setEnableExtComputeShader(b);
            } catch (IOException e) {
                Logger.getLogger("MG").log(Level.SEVERE, "Failed to save config! Exception: ", e.getCause());
                Toast.makeText(this, getString(R.string.warning_save_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ResultListener.onActivityResult(requestCode, resultCode, data);
    }
}
