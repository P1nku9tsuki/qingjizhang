package com.natsuki.qingjizhang

object CategoryRecommender {

    private val EXPENSE_MAP: Map<String, List<String>> = mapOf(
        "餐饮" to listOf(
            "早餐", "午餐", "晚餐", "午饭", "晚饭", "夜宵", "宵夜",
            "咖啡", "奶茶", "星巴克", "瑞幸", "costa", "喜茶", "奈雪", "蜜雪冰城",
            "肯德基", "kfc", "麦当劳", "mcdonald", "汉堡", "披萨", "pizza",
            "外卖", "美团", "饿了么", "食堂", "火锅", "烧烤", "串串",
            "零食", "水果", "蛋糕", "甜点", "面包", "饼干",
            "餐厅", "饭店", "小吃", "早饭", "中饭", "喝酒", "啤酒", "饮料",
            "泡面", "方便面", "米线", "拉面", "螺蛳粉", "黄焖鸡",
            "买菜", "菜市场", "饭团", "寿司", "烤鸭", "自助餐","吃饭","吃了个","下馆子"
        ),
        "交通" to listOf(
            "地铁", "公交", "打车", "滴滴", "出租车", "的士",
            "高铁", "火车", "机票", "飞机", "航班",
            "加油", "油费", "充电", "停车", "过路费", "高速费",
            "共享单车", "摩拜", "哈啰", "青桔", "单车",
            "车票", "船票", "轮渡", "etc", "顺风车"
        ),
        "购物" to listOf(
            "淘宝", "天猫", "京东", "拼多多", "唯品会", "抖音购物",
            "超市", "商场", "便利店", "711", "全家", "罗森", "盒马", "山姆", "costco",
            "衣服", "裤子", "鞋", "外套", "毛衣", "羽绒服", "裙子",
            "化妆品", "护肤", "口红", "面膜", "洗发水", "沐浴露",
            "纸巾", "牙刷", "牙膏", "日用品", "家居", "厨具",
            "电器", "数码", "耳机", "手机", "电脑", "充电器", "数据线","买东西","下单"
        ),
        "学习" to listOf(
            "书", "图书", "教材", "参考书", "考试", "报名", "培训",
            "网课", "课程", "学费", "文具", "笔", "笔记本", "打印",
            "复印", "kindle", "亚马逊", "驾校", "驾照"
        ),
        "娱乐" to listOf(
            "电影", "电影院", "ktv", "唱歌", "酒吧",
            "游戏", "steam", "网易游戏", "腾讯游戏", "点券",
            "演唱会", "livehouse", "话剧", "展览", "门票",
            "旅游", "度假", "vip", "会员", "订阅", "netflix", "spotify",
            "麻将", "桌游", "密室逃脱", "剧本杀","充钱","氪金"
        ),
        "医疗" to listOf(
            "医院", "挂号", "看病", "买药", "药店", "药",
            "体检", "牙医", "看牙", "配镜", "眼镜",
            "疫苗", "医保", "诊所", "门诊", "住院", "手术"
        ),
        "住房" to listOf(
            "房租", "水费", "电费", "燃气", "煤气", "物业",
            "宽带", "网费", "取暖费", "装修", "家具", "供暖费"
        )
    )


    private val INCOME_MAP: Map<String, List<String>> = mapOf(
        "工资" to listOf(
            "工资", "薪水", "月薪", "薪资", "发工资", "发薪",
            "到手", "税前", "税后", "基本工资", "底薪"
        ),
        "奖金" to listOf(
            "奖金", "年终奖", "绩效", "提成", "季度奖", "项目奖",
            "全勤奖", "优秀员工", "奖金收入"
        ),
        "投资" to listOf(
            "理财", "基金", "股票", "分红", "利息", "定期",
            "余额宝", "债券", "收益", "炒股", "定投", "活期",
            "国债", "黄金", "数字货币"
        ),
        "兼职" to listOf(
            "兼职", "外快", "副业", "接单", "稿费", "私活",
            "小时工", "代驾", "跑腿", "直播", "带货"
        ),
        "红包" to listOf(
            "红包", "礼金", "压岁钱", "拜年", "随礼", "份子钱",
            "结婚礼金", "白事礼金"
        ),
        "其他" to listOf(
            "报销", "退费", "退款", "返现", "补贴", "补助",
            "中奖", "中签", "赔付", "保险理赔", "二手", "闲置出售"
        )
    )

    private val sortedExpense: List<Pair<String, String>> by lazy { buildSorted(EXPENSE_MAP) }
    private val sortedIncome: List<Pair<String, String>> by lazy { buildSorted(INCOME_MAP) }

    private fun buildSorted(map: Map<String, List<String>>): List<Pair<String, String>> =
        map.flatMap { (category, keywords) ->
            keywords.map { it.lowercase() to category }
        }.sortedByDescending { it.first.length }

    fun recommendWithKeyword(note: String, isExpense: Boolean): Pair<String, String>? {
        if (note.isBlank()) return null
        val lower = note.lowercase()
        val list = if (isExpense) sortedExpense else sortedIncome
        for ((keyword, category) in list) {
            if (lower.contains(keyword)) return keyword to category
        }
        return null
    }

    fun recommendWithLearned(
        note: String,
        learned: Map<String, String>,
        isExpense: Boolean,
        availableCategories: Set<String>
    ): Pair<String, String>? {
        if (note.isBlank()) return null
        val lower = note.lowercase()

        //
        val learnedSorted = learned.entries
            .filter { it.key.isNotBlank() && it.value in availableCategories }
            .sortedByDescending { it.key.length }
        for (entry in learnedSorted) {
            if (lower.contains(entry.key.lowercase())) {
                return entry.key to entry.value
            }
        }

        //
        return recommendWithKeyword(note, isExpense)
    }


    fun recommend(note: String, isExpense: Boolean): String? =
        recommendWithKeyword(note, isExpense)?.second

    fun preload() {
        sortedExpense
        sortedIncome
    }
}