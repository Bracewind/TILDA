package com.example.gregoire.testmodule2.Camera;

public class CameraException {

  public static class PhotoNotAvailableException extends Exception {
    public PhotoNotAvailableException(String m) {
      super(m);
    }
  }
}
