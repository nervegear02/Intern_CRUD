package com.example.baptcserver.Common;

import com.example.baptcserver.Model.CategoryModel;
import com.example.baptcserver.Model.CropModel;

public class Common {
    public static final int DEFAULT_COLUMN_COUNT = 0;
    public static final int FULL_WIDTH_COLUMN = 1;
    public static final String CATEGORY_REF = "Category";
    public static CategoryModel categorySelected;
    public static CropModel selectedCrop;

    public enum ACTION {
        CREATE,
        UPDATE,
        DELETE
    }

}
