package main

import co.metalab.asyncawait.async
import jeff.main.MainFragment

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2018-12-27 23:16
 * description ：MainFragment 主页显示
 */
class MainFragment : MainFragment() {
    override fun initViews() {
        super.initViews()
        async {
            await<Unit> {
                //加载测试数据

            }
            info()//加载数据列表适配器
        }

    }
}