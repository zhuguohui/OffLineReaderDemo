package com.zgh.trshttp.util;

/**
 * Created by Wu Jingyu
 * Date: 2015/10/29
 * Time: 14:33
 *
 * raw://news_menu
 * Szqh News Menu: http://www.szqh.gov.cn/pub/szqhzhqhmobile/qhzx_3951/channels.json 工作动态 通知公告 新闻热点
 */
public class Constant {
    public static final String HTTP_PREFIX = "http://";
    public static final String HTTPS_PREFIX = "https://";

    /** 用于保存设置的sharedPreferred文件名 */
    public static final String SP_NAME = "library_sp";
    /** END */

    /** JSON数据中Key数组 */
    public static final String[] ID_NAMES = {"id"};
    public static final String[] TYPE_NAMES = {"type", "t"};
    public static final String[] IMAGE_URL_NAMES = {"picture", "pic", "image",
            "img", "icon", "ic"};
    public static final String[] URL_NAMES = {"url", "link"};
    public static final String[] TITLE_NAMES = {"title"};
    public static final String[] SUBTITLE_NAMES = {"summary", "sub", "subtitle"};
    public static final String[] EXTRA_NAMES = {"extra", "extras", "ex"};
    public static final String[] CHANNEL_NAMES = {"channels", "channel", "chnls"};
    public static final String[] PAGE_INFO_NAMES = {"page_info"};
    public static final String[] PAGE_COUNT_NAMES = {"page_count"};
    public static final String[] TOP_DATAS_NAMES = {"topic_datas", "topic_data"};
    public static final String[] DATAS_NAMES = {"datas", "data"};
    public static final String[] TIME_NAMES = {"date", "time"};
    /** END */

    /** Activity和Fragment类型文件 */
    public static final String BASE_TYPE_FRAGMENT_MAP_PATH = "file://android_raw/type_fragment_map_base";
    public static final String EXT_TYPE_FRAGMENT_MAP_PATH = "file://android_raw/type_fragment_map";
    public static final String BASE_TYPE_ACTIVITY_MAP_PATH = "raw://type_activity_map_base";
    public static final String EXT_TYPE_ACTIVITY_MAP_PATH = "raw://type_activity_map";
    /** END */

    /** Broadcast constants */
    public static final String SET_FONT_ACTION = "com.trs.activity.TRSAbsBaseFragmentActivity.SetFontAction";
    public static final String SET_FONT_ACTION_FONT_NAME = "com.trs.activity.TRSAbsBaseFragmentActivity.FontName";
    public static final String SET_DAY_NIGHT_MODE_ACTION = "com.trs.activity.TRSAbsBaseFragmentActivity.SetDayNightModeAction";
    public static final String SET_DAY_NIGHT_MODE_ACTION_THEME_NAME = "com.trs.activity.TRSAbsBaseFragmentActivity.ThemeName";
    /** END */

}
