package com.example.gregoire.testmodule2.helpers;

import org.tensorflow.Graph;
import org.tensorflow.Operation;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

/**
 * Created by gregoire on 18/09/18.
 */

public interface TensorFlowInferenceInterfaceCustom {
    void run(String[] outputNames);
    void run(String[] outputNames, boolean enableStats);
    void run(String[] outputNames, boolean enableStats, String[] targetNodeNames);
    Graph graph();
    void close();

    void feed(String inputName, boolean[] src, long... dims);
    void feed(String inputName, float[] src, long... dims);

    void fetch(String outputName, float[] dst);
    void fetch(String outputName, FloatBuffer dst);
    void fetch(String outputName, IntBuffer dst);
    void fetch(String outputName, LongBuffer dst);
    void fetch(String outputName, DoubleBuffer dst);
    void fetch(String outputName, ByteBuffer dst);

    Operation graphOperation(String operationName);
    String getStatString();
}
