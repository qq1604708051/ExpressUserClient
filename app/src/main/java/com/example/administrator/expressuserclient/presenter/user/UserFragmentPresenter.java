package com.example.administrator.expressuserclient.presenter.user;

import android.content.Context;

import com.example.administrator.expressuserclient.base.BaseGson;
import com.example.administrator.expressuserclient.commonUtil.SPUtil;
import com.example.administrator.expressuserclient.commonUtil.ToastUtil;
import com.example.administrator.expressuserclient.contract.user.UserFragmentContract;
import com.example.administrator.expressuserclient.gson.UserGson;
import com.example.administrator.expressuserclient.model.user.UserFragmentModel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.administrator.expressuserclient.R.id.map;

/**
 * Created by Administrator on 2018/7/31.
 */

public class UserFragmentPresenter implements UserFragmentContract.Presenter {
    private UserFragmentContract.Model model = new UserFragmentModel();
    private UserFragmentContract.View view;
    private Context context;

    public UserFragmentPresenter(UserFragmentContract.View view, Context context) {
        this.view = view;
        this.context = context;
    }

    private static final String TAG = "UserFragmentPresenter";


    public RequestBody toRequestBody(String value) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), value);
        return requestBody;
    }


    @Override
    public void addData(String id, String files) {
        File file = new File(files);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        RequestBody body = toRequestBody(id);
        model.uploadAvatar(body, part)
                .enqueue(new Callback<BaseGson<UserGson>>() {
                    @Override
                    public void onResponse(Call<BaseGson<UserGson>> call, Response<BaseGson<UserGson>> response) {
                        if (response.isSuccessful()) {
                            System.out.println(response.body() + "code");
                            Map<String, Object> map = new HashMap<String, Object>();
                            System.out.println(response.body() + "url");
                            map.put("userhead", response.body().getData().get(0).getHead());
                            SPUtil.getInstance().saveSPData(map).save();
                            ToastUtil.showToastSuccess("上传头像成功！");
                        } else {
                            ToastUtil.showToastError("上传头像失败" + response);
                        }

                    }

                    @Override
                    public void onFailure(Call<BaseGson<UserGson>> call, Throwable t) {
                        ToastUtil.showToastError("上传头像失败" + t.getMessage());
                    }
                });
    }
}
