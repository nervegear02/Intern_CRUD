package com.example.baptcserver.EventBus;

public class ChangeMenuClick {
    private boolean isFromCropList;

    public ChangeMenuClick(boolean isFromCropList) {
        this.isFromCropList = isFromCropList;
    }

    public boolean isFromCropList() {
        return isFromCropList;
    }

    public void setFromCropList(boolean fromCropList) {
        isFromCropList = fromCropList;
    }
}
