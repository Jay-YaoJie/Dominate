package cn.xlink.telinkoffical.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.activity.MainActivity;
import cn.xlink.telinkoffical.activity.SceneActivity;
import cn.xlink.telinkoffical.adapter.recycler_adapter.CommonAdapter;
import cn.xlink.telinkoffical.adapter.recycler_adapter.RecyclerViewHolder;
import cn.xlink.telinkoffical.manage.CmdManage;
import cn.xlink.telinkoffical.model.Places;
import cn.xlink.telinkoffical.model.Scene;
import cn.xlink.telinkoffical.model.Scenes;
import cn.xlink.telinkoffical.utils.SceneActionsDbUtils;
import cn.xlink.telinkoffical.utils.TelinkCommon;
import cn.xlink.telinkoffical.view.MyToast;
import cn.xlink.telinkoffical.view.SlideView;

/**
 * Created by liucr on 2016/3/24.
 */
public class SceneListFragment extends BaseFragment {

    private View view;

    @Bind(R.id.fgt_recyclerview)
    RecyclerView recyclerView;

    private CommonAdapter<Scene> sceneListAdapter;

    private List<Scene> sceneList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fgt_scene, container, false);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sceneListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initView(View view) {

        //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(null);
        sceneListAdapter = new CommonAdapter<Scene>(R.layout.view_device_item, Scenes.getInstance().get()) {
            @Override
            public void convert(RecyclerViewHolder holder, final Scene scene, int position) {
                ImageView rightView = holder.getView(R.id.device_item_right);
                holder.getView(R.id.device_item_light).setVisibility(View.GONE);

                holder.setImageResource(R.id.device_item_left, scene.getBigIcon());
                holder.setText(R.id.device_item_name, scene.getSceneSort().getName());

                if(Places.getInstance().curPlaceIsShare()){
                    rightView.setVisibility(View.INVISIBLE);
                }else {
                    rightView.setVisibility(View.VISIBLE);
                }

                rightView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), SceneActivity.class);
                        intent.putExtra(TelinkCommon.ACTIVITY_TYPE_SCENE_MESH, scene.getSceneSort().getSceneId());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onItemClick(View v, int position) {
                super.onItemClick(v, position);
                Scene scene = Scenes.getInstance().get().get(position);
                if(scene.getSceneActionSort().size() == 0) {
                    MyToast.showTopToast(getActivity(), getString(R.string.scene_no_device));
                }else {
                    CmdManage.executeScene(scene);
                    MyToast.showTopToast(getActivity(), getString(R.string.scene_execute));
                }
            }
        };

        recyclerView.setAdapter(sceneListAdapter);
    }

    private MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    @Override
    protected void initData() {

    }
}
