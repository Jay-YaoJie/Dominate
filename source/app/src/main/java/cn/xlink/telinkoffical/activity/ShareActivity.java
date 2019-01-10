package cn.xlink.telinkoffical.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.adapter.MyFragmentPagerAdapter;
import cn.xlink.telinkoffical.adapter.PopWindowAdapter;
import cn.xlink.telinkoffical.bean.DeviceUser;
import cn.xlink.telinkoffical.bean.DeviceUserList;
import cn.xlink.telinkoffical.bean.ShareBean;
import cn.xlink.telinkoffical.bean.greenDao.PlaceSort;
import cn.xlink.telinkoffical.fragment.BaseFragment;
import cn.xlink.telinkoffical.fragment.MyShareFragment;
import cn.xlink.telinkoffical.fragment.OtherShareFragment;
import cn.xlink.telinkoffical.http.HttpHelper;
import cn.xlink.telinkoffical.http.HttpManage;
import cn.xlink.telinkoffical.http.constant.HttpConstant;
import cn.xlink.telinkoffical.manage.DataToHostManage;
import cn.xlink.telinkoffical.manage.DataToLocalManage;
import cn.xlink.telinkoffical.manage.PlaceManage;
import cn.xlink.telinkoffical.manage.ShareManage;
import cn.xlink.telinkoffical.model.Places;
import cn.xlink.telinkoffical.utils.TelinkCommon;
import cn.xlink.telinkoffical.utils.UserUtil;
import cn.xlink.telinkoffical.view.IndexViewPager;
import cn.xlink.telinkoffical.view.MyPopupWindow;
import cn.xlink.telinkoffical.view.TitleBar;

/**
 * Created by liucr on 2016/4/8.
 */
public class ShareActivity extends BaseActivity {

    @Bind(R.id.act_share_title)
    TitleBar titleBar;

    @Bind(R.id.act_share_my)
    TextView shareMyButton;

    @Bind(R.id.act_share_other)
    TextView shareOtherButton;

    @Bind(R.id.act_share_viewpager)
    IndexViewPager viewPager;

    private BaseFragment baseFragment[];

    private MyFragmentPagerAdapter pagersAdapter;

    private ShareBean operationShar;

    List<ShareBean> shareBeens = new ArrayList<>();

    private List<DeviceUser> deviceUsers = null;

    private MyPopupWindow popupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_share);
        ButterKnife.bind(this);
        initData();
        initView();
        checkHadShare();
        getDeviceUserList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getShare();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void initData() {
        baseFragment = new BaseFragment[]{new MyShareFragment(), new OtherShareFragment()};
        pagersAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), baseFragment);
    }

    @Override
    protected void initView() {
        titleBar.getCenterText().setText(R.string.share_device);
        titleBar.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleBar.getRightImage().setImageResource(R.mipmap.icon_add);
        titleBar.setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(ShareToActivity.class);
            }
        });

        viewPager.setAdapter(pagersAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeTextButton(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initPopUpWindow();
    }

    @OnClick(R.id.act_share_my)
    void ClickShareMy() {
        viewPager.setCurrentItem(0);
    }

    @OnClick(R.id.act_share_other)
    void ClickShareOther() {
        viewPager.setCurrentItem(1);
    }

    private void changeTextButton(int position) {
        if (position == 0) {
            shareMyButton.setBackgroundResource(R.drawable.white_left_radius);
            shareMyButton.setTextColor(getResources().getColor(R.color.text_blue));

            shareOtherButton.setBackgroundResource(R.drawable.white_right_rect_hollow);
            shareOtherButton.setTextColor(Color.WHITE);
        } else {
            shareOtherButton.setBackgroundResource(R.drawable.white_right_radius);
            shareOtherButton.setTextColor(getResources().getColor(R.color.text_blue));

            shareMyButton.setBackgroundResource(R.drawable.white_left_rect_hollow);
            shareMyButton.setTextColor(Color.WHITE);
        }
    }

    /**
     * 初始化更多
     */
    private void initPopUpWindow() {
        List<String> strings = new ArrayList<>();

        strings.add(getString(R.string.share_cancel));

        popupWindow = new MyPopupWindow(this, strings, 0, new PopWindowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        if (!UserUtil.getUser().getUid().equals(operationShar.getFrom_id() + "")) {
                            unSubscribeDevice(operationShar);
                        } else {
                            cancelShare(operationShar);
                        }
                        break;
                }
                popupWindow.dismiss();
            }
        });
    }

    public void showWindow(ShareBean shareBean) {
        operationShar = shareBean;
        popupWindow.showAtLocation(titleBar, Gravity.BOTTOM, 0, 0);
    }

    public void checkHadShare() {
        ShareManage.getInstance(this).getSharePlace().setShareAcceptListener(new ShareManage.ShareAcceptListener() {
            @Override
            public void onCheckListener(boolean isCheckFinish) {

            }

            @Override
            public void onAnswerListener(boolean isEmpty, boolean isAnswerFinish) {
                if (isEmpty && isAnswerFinish) {
                    getShare();
                }
            }

            @Override
            public void onAcceptListener(int position, boolean isAcceptFinish) {
                if (isAcceptFinish) {
                    getShare();
                    DataToLocalManage.getUserDeviceList();
                }
            }
        });
    }

    /**
     * 获取用户分享列表
     */
    public void getShare() {

        HttpManage.getInstance().getAllShare(new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(int code, String response) {
                Log.i("liucr", response);
                if (isDestroyed()) {
                    return;
                }
                if (code == HttpConstant.PARAM_SUCCESS) {
                    shareBeens.clear();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            boolean isRepeat = false;
                            ShareBean shareBean = new ShareBean();
                            JSONObject msg = (JSONObject) jsonArray.get(i);
                            shareBean = new Gson().fromJson(msg.toString(), ShareBean.class);

                            //过滤重复并删除记录
                            for (int t = 0; t < shareBeens.size(); t++) {
                                ShareBean shareBean1 = shareBeens.get(t);
                                if (shareBean.getDevice_id() == shareBean1.getDevice_id() &&
                                        shareBean.getUser_id() == shareBean1.getUser_id()) {
                                    if (shareBean.getExpire_date() > shareBean1.getExpire_date()) {
                                        deleteShare(shareBean1);
                                        shareBeens.remove(shareBean1);
                                        shareBeens.add(shareBean);
                                    } else {
                                        deleteShare(shareBean);
                                    }
                                    isRepeat = true;
                                }
                            }

                            if (!isRepeat) {
                                shareBeens.add(shareBean);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    notifyMyShare();
                    notifyOtherShare();
                    checkIsCancel();
                }
            }
        });
    }

    /**
     * 取消分享
     *
     * @param shareBean
     */
    public void cancelShare(final ShareBean shareBean) {
        Log.i("liucr", "getInvite_code: " + shareBean.getInvite_code());
        HttpManage.getInstance().cancelShare(shareBean.getInvite_code(), new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(int code, String response) {
                if (code == HttpConstant.PARAM_SUCCESS) {
                    deleteShare(shareBean);
                }
            }
        });
    }

    /**
     * 取消订阅
     *
     * @param shareBean
     */
    private void unSubscribeDevice(final ShareBean shareBean) {
        HttpManage.getInstance().unSubscribeDevice(UserUtil.getUser().getUid() + "", shareBean.getDevice_id() + "",
                new HttpHelper.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(int code, String response) {
                        deleteSharePlace(shareBean);
                        deleteShare(shareBean);
                    }
                });
    }

    /**
     * 删除用户分享记录消息
     *
     * @param shareBean
     */
    public void deleteShare(final ShareBean shareBean) {
        HttpManage.getInstance().deleteShare(shareBean.getInvite_code(), new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(int code, String response) {
                if (code == HttpConstant.PARAM_SUCCESS) {
                    shareBeens.remove(shareBean);
                    notifyMyShare();
                    notifyOtherShare();
                }
            }
        });
    }

    /**
     * 删除被分享家的数据
     *
     * @param shareBean
     */
    public void deleteSharePlace(ShareBean shareBean) {

        if (UserUtil.getUser().getUid().equals(shareBean.getFrom_id())) {
            return;
        }

        boolean isCurPlace = false;
        PlaceSort curPlaceSort = Places.getInstance().getCurPlaceSort();
        if (Places.getInstance().getCurPlaceSort().getPlaceId().equals(shareBean.getDevice_id() + "")) {
            isCurPlace = true;
        }
        for (int i = 0; i < Places.getInstance().get().size(); i++) {
            PlaceSort placeSort = Places.getInstance().get(i);
            if (placeSort.getPlaceId().equals(shareBean.getDevice_id() + "")) {
                PlaceManage.clearPlaceDb(UserUtil.getUser().getUid(), placeSort);
                Places.getInstance().remove(placeSort);
            }
        }

        if (isCurPlace) {
            PlaceManage.changePlace(Places.getInstance().get(0));
        }
    }

    /**
     * 获取订阅此设备的所有用户
     */
    public void getDeviceUserList() {

        PlaceSort placeSort = null;
        for (int i = 0; i < Places.getInstance().size(); i++) {
            placeSort = Places.getInstance().get(i);
            if (placeSort.getCreatorId().equals(UserUtil.getUser().getUid() + "")) {
                break;
            }
            placeSort = null;
        }
        HttpManage.getInstance().getDeviceUserList(UserUtil.getUser().getUid() + "", placeSort.getPlaceId(), new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(int code, String response) {
                if (code == HttpConstant.PARAM_SUCCESS) {
                    Log.i("liucr", "getDeviceUserList:    " + response);
                    DeviceUserList deviceUserList = new Gson().fromJson(response.toString(), DeviceUserList.class);
                    deviceUsers = deviceUserList.getList();
                    checkIsCancel();
                }
            }
        });
    }

    public void checkIsCancel() {
        if (deviceUsers != null && shareBeens != null) {
            for (int i = 0; i < shareBeens.size(); i++) {
                boolean isHad = false;
                for (int t = 0; t < deviceUsers.size(); t++) {
                    if (shareBeens.get(i).getUser_id() == deviceUsers.get(t).getUser_id()) {
                        isHad = true;
                        break;
                    }
                }
                if (!isHad && shareBeens.get(i).getState().equals("accept")) {
                    deleteShare(shareBeens.get(i));
                }
            }
        }
    }

    public List<ShareBean> getShareList() {
        return shareBeens;
    }

    public void notifyMyShare() {
        ((MyShareFragment) baseFragment[0]).updataUI();
    }

    public void notifyOtherShare() {
        ((OtherShareFragment) baseFragment[1]).updataUI();
    }

}
