
#2019 -01- 12创建新的项目名，使用的是demo修改的，重新布置环境和更新接口
#使用包如下
 implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation project(':BluetoothLightLib')
    implementation project(':mylibrary')
       androidTestImplementation('com.android.support.test.espresso:espresso-core:2.2.2', {
        exclude group: 'com.android.support', module: 'support-annotations'
    })

    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support:support-v4:28.0.0'
    implementation 'com.android.support:design:28.0.0'
    implementation 'com.android.support:cardview-v7:28.0.0'

//    implementation 'com.jakewharton:butterknife:10.0.0'
//    annotationProcessor 'com.jakewharton:butterknife-compiler:10.0.0'

    implementation'com.jakewharton:butterknife:9.0.0'
    annotationProcessor'com.jakewharton:butterknife-compiler:9.0.0'


    implementation 'de.greenrobot:greendao:2.1.0'

    implementation 'com.squareup.okhttp:okhttp:2.7.5'

    implementation 'com.google.code.gson:gson:2.8.5'

    //Loading
    implementation 'com.github.rahatarmanahmed:circularprogressview:2.5.0'
    //时间选择
    implementation 'com.bigkoo:pickerview:2.1.1'
    //item侧滑
    implementation 'com.daimajia.swipelayout:library:1.2.0'


    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
