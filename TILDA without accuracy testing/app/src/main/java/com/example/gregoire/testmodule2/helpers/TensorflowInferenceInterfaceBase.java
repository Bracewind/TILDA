package com.example.gregoire.testmodule2.helpers;

/**
 * Created by gregoire on 17/09/18.
 */
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Trace;
import android.util.Log;

import org.tensorflow.Graph;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TensorflowInferenceInterfaceBase extends TensorflowInferenceInterfaceModified implements TensorFlowInferenceInterfaceCustom {
    private static String TAG2 = "TensorflowInferenceInterfaceBase";

    public TensorflowInferenceInterfaceBase(AssetManager assetManager, String modelName) {
        super(assetManager, modelName);
    }
}
