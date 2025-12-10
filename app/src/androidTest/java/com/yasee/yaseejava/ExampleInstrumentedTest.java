package com.yasee.yaseejava;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import android.util.Base64;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.yasee.yasee.core.tools.BleDeviceTypeAdapter;
import com.yasee.yasee.protocols.ble.BleDevice;
import com.google.gson.Gson;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        String str = "{\"autoLink\":1,\"broadcastName\":\"Y917-0472\",\"mac\":\"00:00:00:00:04:72\",\"remoteId\":\"00:00:00:00:04:72\",\"source\":\"QKQ1.191014.001\",\"type\":\"Y917\",\"useMode\":-1,\"deviceModel\":\"Y917\",\"imeiType\":\"\",\"advertisementData\":{\"kCBAdvDataManufacturerData\":\"\",\"kCBAdvDataTimestamp\":1735026177.05,\"kCBAdvDataLocalName\":\"Y917-0472\",\"kCBAdvDataIsConnectable\":false,\"kCBAdvDataRemoteId\":\"00:00:00:00:04:72\",\"kCBAdvDataRxSecondaryPHY\":0,\"kCBAdvDataRxPrimaryPHY\":0,\"kCBAdvDataServiceUUIDs\":[]}}";

        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(BleDeviceTypeAdapter.class,new BleDeviceTypeAdapter());
        Gson g = gb.create();

        try {

            byte[] bs = Base64.decode("SGVsbG8gd29ybGQ", Base64.NO_PADDING | Base64.URL_SAFE);
            byte[] bb = Base64.decode("",Base64.NO_PADDING | Base64.URL_SAFE);
            BleDevice bd = g.fromJson(str,BleDevice.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.yasee.yaseejava", appContext.getPackageName());
    }
}