package cn.xlink.telinkoffical.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telink.bluetooth.light.ConnectionStatus;

import java.util.List;

import butterknife.Bind;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.activity.SceneActivity;
import cn.xlink.telinkoffical.adapter.recycler_adapter.CommonAdapter;
import cn.xlink.telinkoffical.adapter.recycler_adapter.RecyclerViewHolder;
import cn.xlink.telinkoffical.model.Light;

/**
 * Created by liucr on 2016/3/29.
 */
public class SceneFirstFragment extends BaseFragment {

    private View view;

    @Bind(R.id.new_event_tips)
    TextView nameTips;

    @Bind(R.id.new_event_edit)
    EditText nameEdit;

    @Bind(R.id.new_event_list)
    RecyclerView selectListView;

    private CommonAdapter<Light> deviceListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.view_new_sg, container, false);
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
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(View view) {

        nameTips.setText(getString(R.string.name_scene_tips));
        nameEdit.setHint(getString(R.string.edit_name_scene_tips));

        initRecyclerView();
    }

    private void initRecyclerView() {
        //设置布局管理器
        selectListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        selectListView.setItemAnimator(null);
        deviceListAdapter = new CommonAdapter<Light>(R.layout.view_common_item, getLightList()) {
            @Override
            public void convert(RecyclerViewHolder holder, final Light light, final int position) {
                ImageView selectImage = holder.getView(R.id.commom_right_image);
                View line = holder.getView(R.id.view_common_line);
                holder.setText(R.id.commom_center_text, light.getLightSort().getName());
                holder.setImageResource(R.id.commom_left_image, light.getSelectIcon());
                if (getSelectList().contains(light)) {
                    selectImage.setVisibility(View.VISIBLE);
                } else {
                    selectImage.setVisibility(View.INVISIBLE);
                }

                if (position == getLightList().size() - 1) {
                    line.setVisibility(View.INVISIBLE);
                } else {
                    line.setVisibility(View.VISIBLE);
                }

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                layoutParams.setMargins(0, 0, 0, 0);
                line.setLayoutParams(layoutParams);
            }

            @Override
            public void onItemClick(View v, int position) {
                super.onItemClick(v, position);
                if(getLightList().get(position).status == ConnectionStatus.OFFLINE){
                    return;
                }
                if (getSelectList().contains(getLightList().get(position))) {
                    getSelectList().remove(getLightList().get(position));
                } else {
                    getSelectList().add(getLightList().get(position));
                }
                notifyDataSetChanged();
            }
        };

        selectListView.setAdapter(deviceListAdapter);
    }

    public String getName(){
        return nameEdit.getText().toString();
    }

    private List<Light> getLightList() {
        return getNewSceneActivity().getLightList();
    }

    private List<Light> getSelectList() {
        return getNewSceneActivity().getSelectLightList();
    }

    private SceneActivity getNewSceneActivity() {
        return (SceneActivity) getActivity();
    }

}
