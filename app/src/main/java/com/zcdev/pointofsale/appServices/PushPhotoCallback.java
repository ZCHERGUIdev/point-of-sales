package com.zcdev.pointofsale.appServices;

public interface PushPhotoCallback {
    void onSuccess(String image_path);

    void onFail();
}
