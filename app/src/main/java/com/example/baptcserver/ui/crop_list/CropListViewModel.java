package com.example.baptcserver.ui.crop_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.baptcserver.Common.Common;
import com.example.baptcserver.Model.CropModel;

import java.util.List;

public class CropListViewModel extends ViewModel {

    private MutableLiveData<List<CropModel>> mutableLiveDataCropList;
    public CropListViewModel() {
    }
    public MutableLiveData<List<CropModel>> getMutableLiveDataCropList() {
        if(mutableLiveDataCropList == null)
            mutableLiveDataCropList = new MutableLiveData<>();
        mutableLiveDataCropList.setValue(Common.categorySelected.getCrops());
        return mutableLiveDataCropList;
    }
}