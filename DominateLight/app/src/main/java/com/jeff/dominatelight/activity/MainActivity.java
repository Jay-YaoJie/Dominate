package com.jeff.dominatelight.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jeff.dominatelight.MyApp;
import com.jeff.dominatelight.R;
import com.jeff.dominatelight.adapter.PopWindowAdapter;
import com.jeff.dominatelight.bean.greenDao.PlaceSort;
import com.jeff.dominatelight.eventbus.ChangePlaceEvent;
import com.jeff.dominatelight.eventbus.ConnectStateEvent;
import com.jeff.dominatelight.eventbus.PlacesUpataEvent;
import com.jeff.dominatelight.eventbus.StringEvent;
import com.jeff.dominatelight.fragment.BulbsListFragment;
import com.jeff.dominatelight.fragment.GroupListFragment;
import com.jeff.dominatelight.fragment.HomeFragment;
import com.jeff.dominatelight.fragment.SceneListFragment;
import com.jeff.dominatelight.manage.CmdManage;
import com.jeff.dominatelight.manage.DataToLocalManage;
import com.jeff.dominatelight.manage.PlaceManage;
import com.jeff.dominatelight.manage.ShareManage;
import com.jeff.dominatelight.model.Lights;
import com.jeff.dominatelight.model.Places;
import com.jeff.dominatelight.service.RefreshTokenService;
import com.jeff.dominatelight.utils.EventBusUtils;
import com.jeff.dominatelight.utils.TelinkCommon;
import com.jeff.dominatelight.utils.UserUtil;
import com.jeff.dominatelight.view.MyPopupWindow;
import com.jeff.dominatelight.view.SlideMenu;
import com.jeff.dominatelight.view.TitleBar;
import com.telink.bluetooth.LeBluetooth;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.util.Event;
import com.telink.util.EventListener;
import io.xlink.wifi.sdk.XlinkAgent;
import jeff.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements EventListener {

    @BindView(R.id.main_slide)
    SlideMenu mainSlide;

    @BindView(R.id.main_title)
    TitleBar titleBar;

    @BindView(R.id.main_sliding_ota)
    View otaItem;

    @BindView(R.id.main_sliding_ota_reddot)
    TextView reddot;

    @BindView(R.id.main_sliding_name_tips)
    TextView nameTips;

    @BindView(R.id.main_sliding_name)
    TextView accountName;

    @BindView(R.id.main_sliding_button)
    TextView logButton;

    private MyPopupWindow popupWindow;
    private MyPopupWindow popupWindowTop;
    private List<String> strings = new ArrayList<>();

    // 定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    // 定义一个布局
    private LayoutInflater layoutInflater;
    // 定义数组来存放Fragment界面
    private Class fragmentArray[] = {HomeFragment.class, SceneListFragment.class, GroupListFragment.class, BulbsListFragment.class};

    // 定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.main_tab_item_home,
            R.drawable.main_tab_item_scenc, R.drawable.main_tab_item_group,
            R.drawable.main_tab_item_bulbs};

    // Tab选项卡的文字
    private String mTextviewArray[] = new String[4];

    private String curType = "";

    private Handler handler = new Handler();

    private BroadcastReceiver stateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String connect = intent.getStringExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkBlueEnable();
                }
            }, 1000);

            Log.i("liucr", "connect: " + connect);
        }
    };

    private void registerBoradcastReceiver() {
        IntentFilter connectedFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(stateChangeReceiver, connectedFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBusUtils.getInstance().addEventListener(PlacesUpataEvent.PlacesUpataEvent, this);
        EventBusUtils.getInstance().addEventListener(ConnectStateEvent.ConnectStateEvent, this);
        EventBusUtils.getInstance().addEventListener(ChangePlaceEvent.ChangePlaceEvent, this);
        EventBusUtils.getInstance().addEventListener(StringEvent.CONNECTING, this);
        registerBoradcastReceiver();
        ButterKnife.bind(this);
        mTextviewArray[0] = getString(R.string.main_tab_tiem_home_text);
        mTextviewArray[1] = getString(R.string.main_tab_tiem_scenes_text);
        mTextviewArray[2] = getString(R.string.main_tab_tiem_groups_text);
        mTextviewArray[3] = getString(R.string.main_tab_tiem_bulbs_text);
        initData();
        initView();
        checkHadShare(true);
        startService(new Intent(MainActivity.this, RefreshTokenService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!LeBluetooth.getInstance().isSupport(this)) {

        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (Places.getInstance().getCurPlaceSort() != null
                        && MyApp.getApp().getState() != LightAdapter.STATUS_LOGIN) {
                    MyApp.getApp().autoConnect(false);
                    showConnectDialog();
                }
            }
        }, 1500);
        initMenu();
        checkIsTipToLogin();

        if (Places.getInstance().get().size() > 1
                && mTabHost.getCurrentTab() == 0) {
            titleBar.setCenterRightDrawable(View.VISIBLE);
        } else {
            titleBar.setCenterRightDrawable(View.INVISIBLE);
        }

        if (!Places.getInstance().curPlaceIsShare()) {
            if (Lights.getInstance().size() == 0 && (mTabHost.getCurrentTab() == 1 || mTabHost.getCurrentTab() == 2)) {
                titleBar.getRightItem().setVisibility(View.INVISIBLE);
            } else {
                titleBar.getRightItem().setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkHadShare(false);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
        EventBusUtils.getInstance().removeEventListener(this);
        handler.removeCallbacks(runnable);
        handler = null;
    }

    @Override
    protected void back() {
        showTipsDialog(getString(R.string.exit_tips), getString(R.string.enter), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.getApp().doDestroy();
                finish();
                System.exit(0);
            }
        });
    }

    @OnClick(R.id.main_sliding_account)
    void ClickAccount() {

    }

    @OnClick(R.id.main_sliding_email)
    void ClickEmail() {
        if (UserUtil.isLogin()) {
            openActivity(EditNickActivity.class);
        } else {
            showLoginErrorTips(TelinkCommon.ACTIVITY_TYPE_ACCOUNT);
        }
    }

    @OnClick(R.id.main_sliding_password)
    void ClickPassword() {
        if (UserUtil.isLogin()) {
            openActivity(EditPasswordActivity.class);
        } else {
            showLoginErrorTips(TelinkCommon.ACTIVITY_TYPE_PASSWORD);
        }
    }

    @OnClick(R.id.main_sliding_ota)
    void ClickOTA() {
        if (UserUtil.isLogin()) {
            openActivity(OTAActivity.class);
        } else {
            showLoginErrorTips(TelinkCommon.ACTIVITY_TYPE_OTA);
        }
    }

    @OnClick(R.id.main_sliding_share)
    void ClickShare() {
        if (UserUtil.isLogin()) {
            openActivity(ShareActivity.class);
        } else {
            showLoginErrorTips(TelinkCommon.ACTIVITY_TYPE_SHARE);
        }
    }

    @OnClick(R.id.main_sliding_button)
    void ClickButton() {
        mainSlide.closePane();
        if (UserUtil.isLogin()) {
            showTipsDialog(getString(R.string.logout_enter), getString(R.string.enter), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideDialog();
                    logout();
                    openActivity(LoginActivity.class);
                }
            });
        } else {
            logout();
            openActivity(LoginActivity.class);
        }

    }

    @OnClick(R.id.main_sliding_about)
    void ClickAbout() {
        openActivity(AboutActivity.class);
    }

    @Override
    protected void initView() {
        // 实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        // 实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.setBackgroundColor(Color.WHITE);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                if (tabId.equals(mTextviewArray[0])) {
                    titleBar.getLeftItem().setVisibility(View.VISIBLE);
                    titleBar.getLeftImage().setImageResource(R.mipmap.icon_menu);
                    if (Places.getInstance().curPlaceIsShare()) {
                        titleBar.getCenterText().setText(Places.getInstance().getCurPlaceSort().getCreatorName() +
                                getString(R.string.home_share));
                    } else {
                        titleBar.getCenterText().setText(Places.getInstance().getCurPlaceSort().getName());
                    }
                } else if (tabId.equals(mTextviewArray[1])) {
                    titleBar.getLeftImage().setImageResource(R.mipmap.icon_setting);
                    titleBar.getCenterText().setText(getString(R.string.scene));
                } else if (tabId.equals(mTextviewArray[2])) {
                    titleBar.getLeftImage().setImageResource(R.mipmap.icon_setting);
                    titleBar.getCenterText().setText(getString(R.string.group));
                } else if (tabId.equals(mTextviewArray[3])) {
                    titleBar.getLeftImage().setImageResource(R.mipmap.icon_setting);
                    titleBar.getCenterText().setText(getString(R.string.device));
                }

                if (!tabId.equals(mTextviewArray[0])) {
                    if (Places.getInstance().curPlaceIsShare()) {
                        titleBar.getLeftItem().setVisibility(View.INVISIBLE);
                    } else {
                        titleBar.getLeftItem().setVisibility(View.VISIBLE);
                    }
                    titleBar.setCenterRightDrawable(View.INVISIBLE);

                } else if (Places.getInstance().get().size() > 1) {
                    titleBar.setCenterRightDrawable(View.VISIBLE);
                }

                if (!Places.getInstance().curPlaceIsShare()) {
                    if (Lights.getInstance().size() == 0 && (mTabHost.getCurrentTab() == 1 || mTabHost.getCurrentTab() == 2)) {
                        titleBar.getRightItem().setVisibility(View.INVISIBLE);
                    } else {
                        titleBar.getRightItem().setVisibility(View.VISIBLE);
                    }
                }

                hidePopWindow();
            }
        });

        // 得到fragment的个数
        int count = fragmentArray.length;
        for (int i = 0; i < count; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);//
        }

        titleBar.getRightImage().setImageResource(R.mipmap.icon_add);

        titleBar.setOnClickCenterLister(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTabHost.getCurrentTab() == 0
                        && Places.getInstance().get().size() > 1) {
                    showHomePlace();
                } else {
                    CmdManage.notifyLight(1);
                }
            }
        });

        titleBar.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (popupWindowTop != null && popupWindowTop.isShowing()) {
                    popupWindowTop.myDismiss();
                    return;
                }

                if (mTabHost.getCurrentTab() == 0) {
                    if (mainSlide.isMenuOpen()) {
                        mainSlide.closePane();
                    } else {
                        if (Places.getInstance().curPlaceIsShare()) {
                            otaItem.setVisibility(View.GONE);
                        } else {
                            otaItem.setVisibility(View.VISIBLE);
                        }
                        updataOtatDot();
                        mainSlide.openPane();
                    }
                } else if (mTabHost.getCurrentTab() == 1) {
                    showScenePopWindow();
                } else if (mTabHost.getCurrentTab() == 2) {
                    showGroupPopWindow();
                } else if (mTabHost.getCurrentTab() == 3) {
                    showDevicePopWindow();
                }
            }
        });

        titleBar.setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindowTop != null && popupWindowTop.isShowing()) {
                    popupWindowTop.myDismiss();
                    return;
                }
                if (mTabHost.getCurrentTab() == 0) {
                    showHomePopWindow();
                } else if (mTabHost.getCurrentTab() == 1) {
                    openActivity(SceneActivity.class);
                } else if (mTabHost.getCurrentTab() == 2) {
                    openActivity(NewGroupActivity.class);
                } else if (mTabHost.getCurrentTab() == 3) {
                    openActivity(FindNewActivity.class);
                }
            }
        });

        titleBar.setOnClickLeftBottomImageListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleBar.setProgressAni(true);
                MyApp.getApp().autoConnect(false);
            }
        });

        initMenu();
        checkIsShareUi();
    }

    @Override
    protected void initData() {
        BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
        if (blueadapter != null && !blueadapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }

    private TextView[] tabView = new TextView[fragmentArray.length];

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        TextView tv_hint = (TextView) view.findViewById(R.id.tv_hint);
        tabView[index] = tv_hint;
        textView.setText(mTextviewArray[index]);
        return view;
    }

    private void initMenu() {
        if (UserUtil.isLogin()) {
            nameTips.setText(getString(R.string.hallo));
            accountName.setText(UserUtil.getUser().getName());
            accountName.setVisibility(View.VISIBLE);
            logButton.setBackgroundResource(R.drawable.gray_rect_hollow);
            logButton.setText(getString(R.string.logout_account));
        } else {
            nameTips.setText(getString(R.string.please_login_account));
            accountName.setVisibility(View.GONE);
            logButton.setBackgroundResource(R.drawable.blue_rect_bg);
            logButton.setText(getString(R.string.login_account));
        }
    }

    /**
     * 更新可升级灯数量
     */
    public void updataOtatDot() {
        int count = Lights.getInstance().getOTACount();
        if (count == 0) {
            reddot.setVisibility(View.GONE);
        } else {
            reddot.setVisibility(View.VISIBLE);
            reddot.setText(count + "");
        }
    }

    private void checkIsTipToLogin() {
        if (!UserUtil.isLogin()) {
            curType = "";
            return;
        }
        if (curType.equals(TelinkCommon.ACTIVITY_TYPE_ACCOUNT)) {
            openActivity(EditNickActivity.class);
        } else if (curType.equals(TelinkCommon.ACTIVITY_TYPE_PASSWORD)) {
            openActivity(EditPasswordActivity.class);
        } else if (curType.equals(TelinkCommon.ACTIVITY_TYPE_SHARE)) {
            openActivity(ShareActivity.class);
        } else if (curType.equals(TelinkCommon.ACTIVITY_TYPE_OTA)) {
            openActivity(OTAActivity.class);
        }
        curType = "";
    }

    /**
     * 隐藏弹窗
     */
    private void hidePopWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    private void showHomePlace() {

        if (popupWindowTop != null && popupWindowTop.isShowing()) {
            popupWindowTop.myDismiss();
            return;
        }

        strings.clear();
        for (PlaceSort sort : Places.getInstance().get()) {
            if (!sort.getCreatorId().equals(UserUtil.getUser().getUid())) {
                strings.add(sort.getCreatorName() + getString(R.string.home_share));
            } else {
                strings.add(sort.getName());
            }
        }

        popupWindowTop = new MyPopupWindow(this, strings, 1, false, new PopWindowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                PlaceManage.changePlace(Places.getInstance().get().get(position));
                popupWindowTop.myDismiss();
            }
        });

        popupWindowTop.showAsDropDown(titleBar, Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 展示home界面更多
     */
    private void showHomePopWindow() {
        final List<String> strings = new ArrayList<>();

        if (Lights.getInstance().get().size() != 0) {
            strings.add(getString(R.string.add_new_scene));
            strings.add(getString(R.string.add_new_group));
        }

        strings.add(getString(R.string.add_new_device));

        popupWindow = new MyPopupWindow(this, strings, 0, new PopWindowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                if (strings.get(position).equals(getString(R.string.add_new_scene))) {
                    openActivity(SceneActivity.class);
                } else if (strings.get(position).equals(getString(R.string.add_new_group))) {
                    openActivity(NewGroupActivity.class);
                } else if (strings.get(position).equals(getString(R.string.add_new_device))) {
                    openActivity(FindNewActivity.class);
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(titleBar, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 展示Scene界面更多
     */
    private void showScenePopWindow() {
        List<String> strings = new ArrayList<>();

        strings.add(getString(R.string.manage_home_scene));

        popupWindow = new MyPopupWindow(this, strings, 0, new PopWindowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                switch (position) {
                    case 0:
                        openActivity(HomeSceneActivity.class);
                        break;
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(titleBar, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 展示Group界面更多
     */
    private void showGroupPopWindow() {
        List<String> strings = new ArrayList<>();

        strings.add(getString(R.string.manage_home_group));

        popupWindow = new MyPopupWindow(this, strings, 0, new PopWindowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                switch (position) {
                    case 0:
                        openActivity(HomeGroupActivity.class);
                        break;
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(titleBar, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 展示Device界面更多
     */
    private void showDevicePopWindow() {
        List<String> strings = new ArrayList<>();

        strings.add(getString(R.string.manage_home_device));

        popupWindow = new MyPopupWindow(this, strings, 0, new PopWindowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                switch (position) {
                    case 0:
                        openActivity(HomeDeviceActivity.class);
                        break;
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(titleBar, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 查询是否有新的分享并更新place
     */
    public void checkHadShare(final boolean isEmptyCheck) {
        ShareManage.getInstance(this).getSharePlace().setShareAcceptListener(new ShareManage.ShareAcceptListener() {
            @Override
            public void onCheckListener(boolean isCheckFinish) {

            }

            @Override
            public void onAnswerListener(boolean isEmpty, boolean isAnswerFinish) {
                if (isEmpty && isAnswerFinish && isEmptyCheck) {
                    DataToLocalManage.getUserDeviceList();
                }
            }

            @Override
            public void onAcceptListener(int position, boolean isAcceptFinish) {
                if (isAcceptFinish) {
                    DataToLocalManage.getUserDeviceList();
                }
            }
        });
    }

    private void checkIsShareUi() {
        if (Places.getInstance().curPlaceIsShare()) {
            titleBar.getRightItem().setVisibility(View.INVISIBLE);

            titleBar.getCenterText().setText(Places.getInstance().getCurPlaceSort().getCreatorName() +
                    getString(R.string.home_share));
        } else {
            titleBar.getRightItem().setVisibility(View.VISIBLE);
            titleBar.getCenterText().setText(Places.getInstance().getCurPlaceSort().getName());
        }
    }

    /**
     * 退出登录
     */
    private void logout() {
        if (UserUtil.isLogin()) {
            UserUtil.clearUser(true);
            XlinkAgent.getInstance().stop();
        }
        PlaceManage.clearPlace();
        finish();
    }

    private void showLoginErrorTips(final String type) {

        showTipsDialog(getString(R.string.login_please), getString(R.string.login), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDialog();
                curType = type;
                openActivity(LoginActivity.class);
            }
        });
    }

    private void checkBlueEnable() {
        //添加检查权限
        PermissionUtils.INSTANCE.checkPermission();

        final BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
        if (!blueadapter.isEnabled() && !tipsDialog.isShowing()) {
            tipsDialog.showDialogWithTips(getString(R.string.blue_close_tips), getString(R.string.get_it), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideDialog();
                }
            }, getString(R.string.open_now), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideDialog();
                    blueadapter.enable();
                }
            });
        } else {
            MyApp.getApp().autoConnect(false);
        }

    }

    public void showConnectDialog() {
        if (!waitingDialog.isShowing() && MyApp.getApp().isCanShowConnectLoading()
                && Lights.getInstance().size() != 0) {
            waitingDialog.setCancelable(true);
            waitingDialog.setCanceledOnTouchOutside(false);
            showWaitingDialog(getString(R.string.connect_device));
            MyApp.getApp().setCanShowConnectLoading(false);
            handler.postDelayed(runnable, 15000);

            titleBar.setBottomVisibility(View.VISIBLE);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            checkIsShareUi();
            hideWaitingDialog();
            tipsDialog.showDialogWithTips(getString(R.string.connect_error_title), getString(R.string.connect_error_tips),
                    getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideDialog();
                        }
                    }, getString(R.string.is_reconnect), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideDialog();
                            MyApp.getApp().autoConnect(false);
                            MyApp.getApp().setCanShowConnectLoading(true);
                            showConnectDialog();
                        }
                    });
        }
    };

    @Override
    public void performed(Event event) {
        if (event.getType().equals(ChangePlaceEvent.ChangePlaceEvent)) {
            checkIsShareUi();
        } else if (event.getType().equals(ConnectStateEvent.ConnectStateEvent)) {

            int state = ((ConnectStateEvent) event).getArgs();
            if (state == LightAdapter.STATUS_LOGIN) {
                hideWaitingDialog();
                hideDialog();
                titleBar.setBottomVisibility(View.GONE);
                handler.removeCallbacks(runnable);
            }else if(state == LightAdapter.STATUS_LOGOUT) {
                titleBar.setBottomVisibility(View.VISIBLE);
            }
        } else if (event.getType().equals(StringEvent.CONNECTING)) {
            showConnectDialog();
        } else if (event.getType().equals(PlacesUpataEvent.PlacesUpataEvent)) {
            PlacesUpataEvent upataEvent = (PlacesUpataEvent) event;
            if (upataEvent.getArgs().getMeshAddress().equals(Places.getInstance().getCurPlaceSort().getMeshAddress())) {
                PlaceManage.loadCurPlaceData();
            }
        }
    }

}
