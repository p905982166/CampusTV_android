package com.panxy.campustv.web.service;

public interface ProgressListener {
    void onProgress(long totalBytes, long remainingBytes, boolean done);
}
