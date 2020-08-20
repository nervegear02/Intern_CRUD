package com.example.baptcserver.EventBus;

import com.example.baptcserver.Common.Common;

public class ToastEvent {
    private Common.ACTION action;
    private boolean isFromCropList;

    public ToastEvent(Common.ACTION action, boolean isFromCropList) {
        this.action = action;
        this.isFromCropList = isFromCropList;
    }

    public Common.ACTION getAction() {
        return action;
    }

    public void setAction(Common.ACTION action) {
        this.action = action;
    }

    public boolean isFromCropList() {
        return isFromCropList;
    }

    public void setFromCropList(boolean fromCropList) {
        isFromCropList = fromCropList;
    }
}
