package cn.xlink.telinkoffical.manage;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.bean.ShareBean;
import cn.xlink.telinkoffical.http.HttpHelper;
import cn.xlink.telinkoffical.http.HttpManage;
import cn.xlink.telinkoffical.http.constant.HttpConstant;
import cn.xlink.telinkoffical.utils.UserUtil;
import cn.xlink.telinkoffical.view.dialog.TipsDialog;

/**
 * Created by liucr on 2016/4/11.
 */
public class ShareManage {

    private static Context mContext;

    private static ShareManage shareManage;

    List<ShareBean> shareBeens = new ArrayList<ShareBean>();
    List<ShareBean> pendShareBeens = new ArrayList<>();
    private int curPendNum = -1;
    private int curAcceptOrDenyNum = 0;

    public static ShareManage getInstance(Activity context) {
        if (shareManage == null) {
            shareManage = new ShareManage();
        }
        mContext = context;
        return shareManage;
    }

    public void executeShareFinish() {
        if (curPendNum < pendShareBeens.size()) {
            showDialog(pendShareBeens.get(curPendNum).getFrom_name());
        } else {
            shareAcceptListener.onAnswerListener(false, true);
        }
    }

    public ShareManage getSharePlace() {
        curPendNum = -1;
        curAcceptOrDenyNum = 0;
        shareBeens = new ArrayList<>();
        pendShareBeens = new ArrayList<>();
        HttpManage.getInstance().getAllShare(new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                shareAcceptListener.onAnswerListener(true, true);
            }

            @Override
            public void onResponse(int code, String response) {
                if (((Activity) mContext).isDestroyed()) {
                    return;
                }
                if (code == HttpConstant.PARAM_SUCCESS) {
                    shareBeens.clear();
                    pendShareBeens.clear();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ShareBean shareBean = new ShareBean();
                            JSONObject msg = (JSONObject) jsonArray.get(i);
                            shareBean = new Gson().fromJson(msg.toString(), ShareBean.class);
                            shareBeens.add(shareBean);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (ShareBean shareBean : shareBeens) {
                        if (shareBean.getState() == null) {
                            deleteShare(shareBean.getInvite_code());
                        } else if (shareBean.getState().equals("pending") &&
                                !shareBean.getFrom_user().equals(UserUtil.getUser().getAccount() + "")) {
                            pendShareBeens.add(shareBean);
                        } else if (shareBean.getState().equals("cancel") &&
                                !shareBean.getFrom_user().equals(UserUtil.getUser().getAccount() + "")) {
                            deleteShare(shareBean.getInvite_code());
                        } else if (shareBean.getState().equals("accept")) {

                        } else if (shareBean.getState().equals("deny")) {
                            deleteShare(shareBean.getInvite_code());
                        }
                    }
                }
                shareAcceptListener.onCheckListener(true);
                if (pendShareBeens.size() > 0) {
                    curPendNum = 0;
                    showDialog(pendShareBeens.get(0).getFrom_name());
                } else {
                    shareAcceptListener.onAnswerListener(true, true);
                }
            }
        });

        return this;
    }

    private TipsDialog customDialog;

    /**
     * 显示是否接受应答框
     *
     * @param name
     */
    public void showDialog(String name) {
        customDialog = new TipsDialog(mContext);
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.setCancelable(false);
        customDialog.showDialogWithTips(mContext.getResources().getString(R.string.have_share_tips, name),
                "不接受", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                        setShareDeny(pendShareBeens.get(curPendNum));
                        curPendNum++;
                        executeShareFinish();
                    }
                }, "接受", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                        setShareAccept(pendShareBeens.get(curPendNum));
                        curPendNum++;
                        executeShareFinish();
                    }
                });
    }

    /**
     * 接受分享
     *
     * @param shareBean
     */
    public void setShareAccept(ShareBean shareBean) {
        HttpManage.getInstance().setShareAccept(shareBean.getInvite_code(), new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                curAcceptOrDenyNum++;
                if (shareAcceptListener != null) {
                    if (curAcceptOrDenyNum == pendShareBeens.size()) {
                        shareAcceptListener.onAcceptListener(curAcceptOrDenyNum, true);
                    } else {
                        shareAcceptListener.onAcceptListener(curAcceptOrDenyNum, false);
                    }
                }
            }

            @Override
            public void onResponse(int code, String response) {
                curAcceptOrDenyNum++;
                if (shareAcceptListener != null) {
                    if (curAcceptOrDenyNum == pendShareBeens.size()) {
                        shareAcceptListener.onAcceptListener(curAcceptOrDenyNum, true);
                    } else {
                        shareAcceptListener.onAcceptListener(curAcceptOrDenyNum, false);
                    }
                }
            }
        });
    }

    /**
     * 不接受分享
     *
     * @param shareBean
     */
    public void setShareDeny(ShareBean shareBean) {
        HttpManage.getInstance().setShareDeny(shareBean.getInvite_code(), new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                curAcceptOrDenyNum++;
                if (shareAcceptListener != null) {
                    if (curAcceptOrDenyNum == pendShareBeens.size()) {
                        shareAcceptListener.onAcceptListener(curAcceptOrDenyNum, true);
                    } else {
                        shareAcceptListener.onAcceptListener(curAcceptOrDenyNum, false);
                    }
                }
            }

            @Override
            public void onResponse(int code, String response) {
                curAcceptOrDenyNum++;
                if (shareAcceptListener != null) {
                    if (curAcceptOrDenyNum == pendShareBeens.size()) {
                        shareAcceptListener.onAcceptListener(curAcceptOrDenyNum, true);
                    } else {
                        shareAcceptListener.onAcceptListener(curAcceptOrDenyNum, false);
                    }
                }
                if (code == HttpConstant.PARAM_SUCCESS) {

                }
            }
        });
    }

    /**
     * 删除分享记录
     *
     * @param inviteCode
     */
    public void deleteShare(String inviteCode) {
        HttpManage.getInstance().deleteShare(inviteCode, new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(int code, String response) {

            }
        });
    }

    public void setShareAcceptListener(ShareAcceptListener shareAcceptListener) {
        this.shareAcceptListener = shareAcceptListener;
    }

    private ShareAcceptListener shareAcceptListener;

    public interface ShareAcceptListener {
        void onCheckListener(boolean isCheckFinish);

        void onAnswerListener(boolean isEmpty, boolean isAnswerFinish);

        void onAcceptListener(int position, boolean isAcceptFinish);
    }

    public void clear() {
        mContext = null;
        shareAcceptListener = null;
        shareBeens = null;
        pendShareBeens = null;
    }

}
