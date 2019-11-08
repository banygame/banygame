package com.bany.game.common.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 安全常量类
 */
public class QIYUConstants {

   public static final String AUTH = "https://qyapi.weibangong.com/api/auth2/enterprise/accesstoken";
   public static final String CLIENTID = "lEwBE9ZfmE7jebW4zDAT";
   public static final String CLIENTSECRET = "P2mZMTZlszkubdoJ8JZV";

   public static final String GETALLUSER = "https://qyapi.weibangong.com/open/security/v1/organizations/contacts";
   public static final String PUNCH = "https://qyapi.weibangong.com/open/kaoqin/v1/punchs";//批量打卡
   public static final String GETNOWINFO = "https://qyapi.weibangong.com/open/kaoqin/v1/allPunchLogDetails";//批量打卡
   public static final String WIFI_NAME = "ZG888";
   public static final String SITE_NAME ="兴业大厦";
   public static final String INIT_LAT="34.769141";
   public static final String INIT_LNG="113.715363";

   public static Map<String,Object> TOKEN = new HashMap<>();

   public static Map<String,Object> AllUser = new HashMap<>();





}
