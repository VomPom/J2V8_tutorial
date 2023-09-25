package julis.wang.template

/**
 * Created by @juliswang on 2023/09/07 15:38
 *
 * @Description
 */
data class TestObj(
    val id: String? = null,
    val info: Info? = null
)


data class Info(
    val version: String? = null,
    val code: Int? = null
)