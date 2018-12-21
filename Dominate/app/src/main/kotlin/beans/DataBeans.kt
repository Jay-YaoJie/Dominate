package beans

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-21.
 * description ：DataBeans
 */
//主页列表显示
data class MainBean(
        //  @SerializedName("title")  var title:String
        //top 数据
        var title: String,
        var imgUrl: Any,
        var id: Int,
        //group 数据
        var groupName: String,
        var Of: Boolean
)