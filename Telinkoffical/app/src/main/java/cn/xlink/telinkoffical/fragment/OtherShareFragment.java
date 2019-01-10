package cn.xlink.telinkoffical.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.activity.ShareActivity;
import cn.xlink.telinkoffical.adapter.recycler_adapter.CommonAdapter;
import cn.xlink.telinkoffical.adapter.recycler_adapter.RecyclerViewHolder;
import cn.xlink.telinkoffical.bean.ShareBean;
import cn.xlink.telinkoffical.utils.LogUtil;
import cn.xlink.telinkoffical.utils.UserUtil;

/**
 * Created by liucr on 2016/4/8.
 */
public class OtherShareFragment extends BaseFragment {

    private View view;

    @Bind(R.id.fgt_recyclerview)
    RecyclerView recyclerView;

    @Bind(R.id.fgt_recyclerview_empty)
    View emptyView;

    @Bind(R.id.fgt_recyclerview_empty_text)
    TextView emtyText;

    private CommonAdapter<ShareBean> commonAdapter;

    private List<ShareBean> shareBeanList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fgt_recyclerview, container, false);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(View view) {

        emtyText.setText(getString(R.string.other_share_empty_tips));

        //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(getShareActivity()));
        recyclerView.setItemAnimator(null);
        commonAdapter = new CommonAdapter<ShareBean>(R.layout.item_share, shareBeanList) {
            @Override
            public void convert(RecyclerViewHolder holder, ShareBean shareBean, int position) {
                TextView shareState = holder.getView(R.id.item_share_state);
                holder.setText(R.id.item_share_name, shareBean.getFrom_name());
                holder.setText(R.id.item_share_account, shareBean.getFrom_user());

                if (shareBean.getState() == null) {
                    shareState.setText(getString(R.string.share_state_pending));
                } else if (shareBean.getState().equals("pending")) {
                    shareState.setText(getString(R.string.share_state_pending));
                } else if (shareBean.getState().equals("cancel")) {
                    shareState.setText(getString(R.string.share_state_cancel));
                } else if (shareBean.getState().equals("accept")) {
                    shareState.setText(getString(R.string.share_state_accept1));
                } else if (shareBean.getState().equals("deny")) {
                    shareState.setText(getString(R.string.share_state_deny));
                }

            }

            @Override
            public void onItemClick(View v, int position) {
                super.onItemClick(v, position);
                getShareActivity().showWindow(shareBeanList.get(position));
            }
        };

        recyclerView.setAdapter(commonAdapter);
    }

    public void updataUI() {
        shareBeanList.clear();
        for (ShareBean shareBean : getShareActivity().getShareList()) {
            LogUtil.d(UserUtil.getUser().getUid() + " : " + shareBean.getFrom_id());
            if (!UserUtil.getUser().getUid().equals(shareBean.getFrom_id() + "")) {
                shareBeanList.add(shareBean);
            }
        }

        if (shareBeanList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
        commonAdapter.notifyDataSetChanged();
    }

    public ShareActivity getShareActivity() {
        return (ShareActivity) getActivity();
    }

}
