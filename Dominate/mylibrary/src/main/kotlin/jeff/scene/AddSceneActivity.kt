package jeff.scene

import android.app.Activity
import com.jeff.mylibrary.R
import jeff.bases.BaseActivity
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.config.PictureConfig
import android.content.Intent
import com.bumptech.glide.Glide
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.PictureMimeType.ofImage
import jeff.bases.MainActivity
import jeff.constants.Settings.FILE_PATH
import jeff.utils.ToastUtil


/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2019-01-06 1:32
 * description ：AddSceneActivity 添加情景模式
 */
open class AddSceneActivity : BaseActivity<AddSceneActivityDB>() {
    override fun getContentViewId(): Int = R.layout.activity_add_scene
    override fun initViews() {
        binding.sceneImg.setOnClickListener {
            //选择图片
            selectPicture()
        }
        binding.topLeft.setOnClickListener {
            mActivity.finish()
        }

        binding.btnSave.setOnClickListener {
            ToastUtil.togglesoftinput(binding.btnSave)
            var eneName: String = binding.etSceneName.text.toString()
            if (eneName.isNullOrEmpty()) {
                ToastUtil.show(mActivity, "请用输入昵称！")
                return@setOnClickListener
            }
            if (imgPath.isNullOrEmpty()) {
                ToastUtil.show(mActivity, "请选择图片！")
                return@setOnClickListener
            }
            addSceneSave(imgPath, eneName)

        }
    }

    open fun addSceneSave(imgPath: String, seneName: String) {}
    private fun selectPicture() {
        PictureSelector.create(mActivity)
                .openGallery(ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .maxSelectNum(1)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("${FILE_PATH}/images")// 自定义拍照保存路径,可不填
                .enableCrop(true)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .withAspectRatio(3, 2)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(true)// 是否显示gif图片 true or false
                .compressSavePath("${FILE_PATH}/images")//压缩图片保存地址
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .isDragFrame(true)// 是否可拖动裁剪框(固定)
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    var imgPath: String = ""
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片、视频、音频选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
//                    adapter.setList(selectList)
//                    adapter.notifyDataSetChanged()

                    if (selectList[0].isCompressed) {
                        // 压缩后path
                        imgPath = selectList[0].compressPath
                    } else {
                        //直接使用 裁剪后path
                        imgPath = selectList[0].cutPath
                    }
                    Glide.with(mActivity).load(imgPath)
                }
            }
        }
    }


}