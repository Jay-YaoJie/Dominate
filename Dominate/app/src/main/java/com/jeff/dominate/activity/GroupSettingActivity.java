package com.jeff.dominate.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeff.dominate.R;
import com.jeff.dominate.TelinkBaseActivity;
import com.jeff.dominate.fragments.GroupSettingFragment;
import com.jeff.dominate.model.Group;
import com.jeff.dominate.model.Groups;


public final class GroupSettingActivity extends TelinkBaseActivity {

	private ImageView backView;
	private GroupSettingFragment settingFragment;

	private int groupAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_group_setting);

		this.groupAddress = this.getIntent().getIntExtra("groupAddress", 0);

		Group group = Groups.getInstance().getByMeshAddress(groupAddress);

		if (group != null) {
			TextView txtTitle = (TextView) this
					.findViewById(R.id.txt_header_title);
			txtTitle.setText(group.name);
		}

		this.backView = (ImageView) this
				.findViewById(R.id.img_header_menu_left);
		this.backView.setOnClickListener(this.clickListener);

		this.settingFragment = (GroupSettingFragment) this.getFragmentManager()
				.findFragmentById(R.id.group_setting_fragment);

		this.settingFragment.groupAddress = groupAddress;
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == backView) {
				finish();
			}
		}
	};
}
