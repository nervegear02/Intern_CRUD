package com.example.baptcserver.EventBus;

import com.example.baptcserver.Model.CropModel;

public class CropItemClick {
    private boolean success;
    private CropModel cropModel;

    public CropItemClick(boolean success, CropModel cropModel) {
        this.success = success;
        this.cropModel = cropModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public CropModel getCropModel() {
        return cropModel;
    }

    public void setCropModel(CropModel cropModel) {
        this.cropModel = cropModel;
    }
}
