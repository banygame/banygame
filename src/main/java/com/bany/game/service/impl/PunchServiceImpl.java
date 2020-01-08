package com.bany.game.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bany.game.common.Util.HttpsUtil;
import com.bany.game.service.PunchService;
import org.jsoup.Connection;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.bany.game.common.constants.QIYUConstants.*;
@Service
public class PunchServiceImpl implements PunchService {
    @Override
    public String punch(String username, String punchTime) {

       String result = "";
        String AUTHJson = "{" +
                "\"clientId\":\"" + CLIENTID + "\"," +
                "\"clientSecret\":\"" + CLIENTSECRET + "\"" +
                "}";
        System.out.println(AUTHJson);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("charset", "utf-8");


        try {

            String accessToken = "";
            String tenantId = "";
            FIFOCache<String, String> cache = CacheUtil.newFIFOCache(2);

            if(ObjectUtil.isNull(cache.get("accessToken"))){
                Connection.Response res = null;
                res = HttpsUtil.post(AUTH, headers, AUTHJson);
                JSONObject jsonObject = JSONObject.parseObject(res.body().toString());
                JSONObject data = jsonObject.getJSONObject("data");
                 accessToken = data.getString("accessToken");
                tenantId = data.getString("tenantId");
                 //存入缓存
                cache.put("accessToken",accessToken, DateUnit.DAY.getMillis() * 3);
                cache.put("tenantId",accessToken, DateUnit.DAY.getMillis() * 3);



            }else {
                accessToken = cache.get("accessToken");
                tenantId = cache.get("tenantId");

            }


            //存储token
            System.out.println("accessToken==" + accessToken);
            //获取所有员工记录
            String dateTime = String.valueOf(new Date().getTime());
            headers.put("AccessToken", accessToken);
            headers.put("ClientId", CLIENTID);
            headers.put("CreateTime", String.valueOf(new Date().getTime()));
            String tenantJson = "{\"tenantId\":" + tenantId + "}";

            //查询员工数据
            Connection.Response allUser = HttpsUtil.post(GETALLUSER, headers, tenantJson);
            System.out.println(allUser.body());

            JSONObject allEmp = JSONObject.parseObject(allUser.body());
            String id = getUserId(allEmp, username);


            //获取时间戳
            if ("".equals(punchTime)) {
                punchTime = "" + new Date().getTime();
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = null;
                try {
                    date = simpleDateFormat.parse(punchTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                punchTime = String.valueOf(date.getTime());
                System.out.println(punchTime);
            }

            JSONObject resultObj = buildUserInfo(id, Integer.valueOf(tenantId), INIT_LNG, INIT_LAT, punchTime);
            System.out.println(resultObj.toJSONString());

            //给员工打卡
            Connection.Response punch = HttpsUtil.post(PUNCH, headers, resultObj.toJSONString());
            System.out.println(punch.body().toString());
            result = punch.body().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;
    }

    public static JSONObject buildUserInfo(String empid, Integer company_id, String longitude, String latitude, String punch_timestamp) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map userMap = new HashMap();
        userMap.put("user_id", Integer.valueOf(empid));
        userMap.put("company_id", company_id);
        userMap.put("punch_timestamp", punch_timestamp);
        userMap.put("longitude", longitude);
        userMap.put("latitude", latitude);
        userMap.put("wifi_name", WIFI_NAME);
        userMap.put("site_name", SITE_NAME);
        result.add(userMap);
        JSONObject obj = new JSONObject();
        obj.put("items", result);

        return obj;
    }


    //查询员工数据
    public static String getUserId(JSONObject jsonObject, String username) {
        JSONArray array = jsonObject.getJSONArray("data");
        String id = "";
        for (int i = 0; i < array.size(); i++) {
            if (array.getJSONObject(i).getString("fullname").equals(username)) {
                id = array.getJSONObject(i).getString("id");
            }
        }
        return id;
    }
}

/*
    public static void main(String[] args) {
        String allUser = "{\n" +
                "\t\"data\": [{\n" +
                "\t\t\"jobTitle\": \"管委会委员，执行主任，专职执业律师\",\n" +
                "\t\t\"id\": 741008,\n" +
                "\t\t\"fullname\": \"老顽童(刘梦娟)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"管委会委员，主任\",\n" +
                "\t\t\"id\": 742362,\n" +
                "\t\t\"fullname\": \"逍遥子(陈洪东)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"管理员\",\n" +
                "\t\t\"id\": 1691460,\n" +
                "\t\t\"fullname\": \"灭绝师太(管理员)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"行政总监\",\n" +
                "\t\t\"id\": 2104200,\n" +
                "\t\t\"fullname\": \"大禹(安鹏程)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"客户顾问\",\n" +
                "\t\t\"id\": 2125534,\n" +
                "\t\t\"fullname\": \"任我行(蒋艳)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"行政专员\",\n" +
                "\t\t\"id\": 2250840,\n" +
                "\t\t\"fullname\": \"苗若兰(任正晨)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"客户顾问\",\n" +
                "\t\t\"id\": 2501225,\n" +
                "\t\t\"fullname\": \"林朝英(李可)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"人资专员\",\n" +
                "\t\t\"id\": 2553989,\n" +
                "\t\t\"fullname\": \"林夕(周梦依)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"诉讼主管，专职执业律师\",\n" +
                "\t\t\"id\": 2614744,\n" +
                "\t\t\"fullname\": \"风清扬(董光辉)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"专职执业律师\",\n" +
                "\t\t\"id\": 2693301,\n" +
                "\t\t\"fullname\": \"马基亚\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"非诉主管，实习律师\",\n" +
                "\t\t\"id\": 2696719,\n" +
                "\t\t\"fullname\": \"蘅芜(杨艳)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"财务专员\",\n" +
                "\t\t\"id\": 2842327,\n" +
                "\t\t\"fullname\": \"刘旺财(刘晴)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"主任办秘书\",\n" +
                "\t\t\"id\": 2868035,\n" +
                "\t\t\"fullname\": \"主任办秘书\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师助理，实习律师\",\n" +
                "\t\t\"id\": 2870979,\n" +
                "\t\t\"fullname\": \"木婉清(张婼翾)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"实习\",\n" +
                "\t\t\"id\": 3014668,\n" +
                "\t\t\"fullname\": \"孟军\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"暂缺\",\n" +
                "\t\t\"id\": 3030085,\n" +
                "\t\t\"fullname\": \"钢铁侠（王猛）\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"产品经理\",\n" +
                "\t\t\"id\": 3031199,\n" +
                "\t\t\"fullname\": \"杨洋\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"开发\",\n" +
                "\t\t\"id\": 3033357,\n" +
                "\t\t\"fullname\": \"崔亚强\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师助理\",\n" +
                "\t\t\"id\": 3045579,\n" +
                "\t\t\"fullname\": \"浩克(程麟)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"UI设计师\",\n" +
                "\t\t\"id\": 3079989,\n" +
                "\t\t\"fullname\": \"汤璐瑶\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"执业律师\",\n" +
                "\t\t\"id\": 3100812,\n" +
                "\t\t\"fullname\": \"何向前\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"web前端开发\",\n" +
                "\t\t\"id\": 3156903,\n" +
                "\t\t\"fullname\": \"刘欢欢\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"java开发\",\n" +
                "\t\t\"id\": 3163335,\n" +
                "\t\t\"fullname\": \"杨晓昆\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"总经理\",\n" +
                "\t\t\"id\": 3167995,\n" +
                "\t\t\"fullname\": \"魏晓利\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"客户经理\",\n" +
                "\t\t\"id\": 3169532,\n" +
                "\t\t\"fullname\": \"钱镕基\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 1\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"主任\",\n" +
                "\t\t\"id\": 744743,\n" +
                "\t\t\"fullname\": \"钱东霞\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"专职执业律师\",\n" +
                "\t\t\"id\": 745249,\n" +
                "\t\t\"fullname\": \"王英美\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"市场部\",\n" +
                "\t\t\"id\": 745433,\n" +
                "\t\t\"fullname\": \"吴佳佳\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"专职执业律师\",\n" +
                "\t\t\"id\": 747214,\n" +
                "\t\t\"fullname\": \"徐雅莉\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"行政\",\n" +
                "\t\t\"id\": 747219,\n" +
                "\t\t\"fullname\": \"陈贞杰\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师\",\n" +
                "\t\t\"id\": 832537,\n" +
                "\t\t\"fullname\": \"赵彦涛\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"实习律师\",\n" +
                "\t\t\"id\": 832559,\n" +
                "\t\t\"fullname\": \"马基亚\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"市场部\",\n" +
                "\t\t\"id\": 895306,\n" +
                "\t\t\"fullname\": \"孙金\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"专职执业律师\",\n" +
                "\t\t\"id\": 957776,\n" +
                "\t\t\"fullname\": \"李晓宸\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师\",\n" +
                "\t\t\"id\": 958906,\n" +
                "\t\t\"fullname\": \"王九超\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师\",\n" +
                "\t\t\"id\": 1072083,\n" +
                "\t\t\"fullname\": \"李东丽\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"专职执业律师\",\n" +
                "\t\t\"id\": 1105101,\n" +
                "\t\t\"fullname\": \"李更\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师\",\n" +
                "\t\t\"id\": 1108641,\n" +
                "\t\t\"fullname\": \"邓飞\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"财务\",\n" +
                "\t\t\"id\": 1145465,\n" +
                "\t\t\"fullname\": \"宋云龙\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"专职执业律师\",\n" +
                "\t\t\"id\": 1171263,\n" +
                "\t\t\"fullname\": \"张森\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"人力资源\",\n" +
                "\t\t\"id\": 1176933,\n" +
                "\t\t\"fullname\": \"韩丹丹\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"实习律师\",\n" +
                "\t\t\"id\": 1282866,\n" +
                "\t\t\"fullname\": \"户天亮\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"实习生\",\n" +
                "\t\t\"id\": 1282943,\n" +
                "\t\t\"fullname\": \"王炳森\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"实习生\",\n" +
                "\t\t\"id\": 1282996,\n" +
                "\t\t\"fullname\": \"张贺\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"实习生\",\n" +
                "\t\t\"id\": 1310966,\n" +
                "\t\t\"fullname\": \"李文沙\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"主任助理\",\n" +
                "\t\t\"id\": 1350160,\n" +
                "\t\t\"fullname\": \"陈嘉怡\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"实习律师\",\n" +
                "\t\t\"id\": 1368413,\n" +
                "\t\t\"fullname\": \"袁文娟\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师\",\n" +
                "\t\t\"id\": 1441355,\n" +
                "\t\t\"fullname\": \"李东丽\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"行政人事\",\n" +
                "\t\t\"id\": 1454203,\n" +
                "\t\t\"fullname\": \"胡少晨\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师\",\n" +
                "\t\t\"id\": 1461403,\n" +
                "\t\t\"fullname\": \"马基亚\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"合伙人\",\n" +
                "\t\t\"id\": 1463304,\n" +
                "\t\t\"fullname\": \"魏晓利\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师顾问\",\n" +
                "\t\t\"id\": 1479955,\n" +
                "\t\t\"fullname\": \"王家盟\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师顾问\",\n" +
                "\t\t\"id\": 1480155,\n" +
                "\t\t\"fullname\": \"刘奇\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"洛阳综合管理\",\n" +
                "\t\t\"id\": 1593701,\n" +
                "\t\t\"fullname\": \"徐艳\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"专职执业律师\",\n" +
                "\t\t\"id\": 1635583,\n" +
                "\t\t\"fullname\": \"陈群英\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"市场顾问\",\n" +
                "\t\t\"id\": 1654342,\n" +
                "\t\t\"fullname\": \"韩晓雅\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"市场顾问\",\n" +
                "\t\t\"id\": 1669062,\n" +
                "\t\t\"fullname\": \"李真\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"会计\",\n" +
                "\t\t\"id\": 1685111,\n" +
                "\t\t\"fullname\": \"邱瑾\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"实习生\",\n" +
                "\t\t\"id\": 1693498,\n" +
                "\t\t\"fullname\": \"罗汀杏\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"实习律师\",\n" +
                "\t\t\"id\": 1717042,\n" +
                "\t\t\"fullname\": \"刘慧丽\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"市场专员\",\n" +
                "\t\t\"id\": 1739505,\n" +
                "\t\t\"fullname\": \"孙贝贝\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"洲冠学院\",\n" +
                "\t\t\"id\": 1739511,\n" +
                "\t\t\"fullname\": \"王望望\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"法务助理\",\n" +
                "\t\t\"id\": 1758140,\n" +
                "\t\t\"fullname\": \"刘学海\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"市场\",\n" +
                "\t\t\"id\": 1758753,\n" +
                "\t\t\"fullname\": \"吴欣\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"法务助理\",\n" +
                "\t\t\"id\": 1760375,\n" +
                "\t\t\"fullname\": \"景田梦\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"\",\n" +
                "\t\t\"id\": 1771677,\n" +
                "\t\t\"fullname\": \"张广星\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"法务助理\",\n" +
                "\t\t\"id\": 1771731,\n" +
                "\t\t\"fullname\": \"周思远\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"法务助理\",\n" +
                "\t\t\"id\": 1771733,\n" +
                "\t\t\"fullname\": \"王相林\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"主任助理\",\n" +
                "\t\t\"id\": 1806029,\n" +
                "\t\t\"fullname\": \"王俊英\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"客户顾问\",\n" +
                "\t\t\"id\": 1809265,\n" +
                "\t\t\"fullname\": \"马少华\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"市场\",\n" +
                "\t\t\"id\": 1814876,\n" +
                "\t\t\"fullname\": \"谢海洋\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"人资\",\n" +
                "\t\t\"id\": 1822293,\n" +
                "\t\t\"fullname\": \"王琳燕\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"客户开发\",\n" +
                "\t\t\"id\": 1824978,\n" +
                "\t\t\"fullname\": \"王琬珺\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"法务助理\",\n" +
                "\t\t\"id\": 1825913,\n" +
                "\t\t\"fullname\": \"李微\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"行政\",\n" +
                "\t\t\"id\": 1828912,\n" +
                "\t\t\"fullname\": \"杨爽\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"市场\",\n" +
                "\t\t\"id\": 1832865,\n" +
                "\t\t\"fullname\": \"刘晓姣\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"行政\",\n" +
                "\t\t\"id\": 1832959,\n" +
                "\t\t\"fullname\": \"张佩瑶\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"行政\",\n" +
                "\t\t\"id\": 1838972,\n" +
                "\t\t\"fullname\": \"张璐凯\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"人力资源专员\",\n" +
                "\t\t\"id\": 1871308,\n" +
                "\t\t\"fullname\": \"何宾华\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"新人\",\n" +
                "\t\t\"id\": 1874961,\n" +
                "\t\t\"fullname\": \"吴浩\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"实习律师\",\n" +
                "\t\t\"id\": 1912743,\n" +
                "\t\t\"fullname\": \"李晓丹\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"实习律师\",\n" +
                "\t\t\"id\": 2012140,\n" +
                "\t\t\"fullname\": \"李欣欣\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"实习律师\",\n" +
                "\t\t\"id\": 2012156,\n" +
                "\t\t\"fullname\": \"曹源\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"人力资源专员\",\n" +
                "\t\t\"id\": 2024290,\n" +
                "\t\t\"fullname\": \"敬中路\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"专职执业律师\",\n" +
                "\t\t\"id\": 2054892,\n" +
                "\t\t\"fullname\": \"张振辉\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"专职执业律师\",\n" +
                "\t\t\"id\": 2116302,\n" +
                "\t\t\"fullname\": \"孙宏磊\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"行政助理\",\n" +
                "\t\t\"id\": 2140929,\n" +
                "\t\t\"fullname\": \"行政助理\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"前台\",\n" +
                "\t\t\"id\": 2148230,\n" +
                "\t\t\"fullname\": \"赵小燕\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"专职律师\",\n" +
                "\t\t\"id\": 2243298,\n" +
                "\t\t\"fullname\": \"王九超\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师\",\n" +
                "\t\t\"id\": 2302622,\n" +
                "\t\t\"fullname\": \"李会轩\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"实习律师\",\n" +
                "\t\t\"id\": 2382168,\n" +
                "\t\t\"fullname\": \"杨新龙\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"洲冠学院\",\n" +
                "\t\t\"id\": 2408313,\n" +
                "\t\t\"fullname\": \"于浩\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"实习律师\",\n" +
                "\t\t\"id\": 2463692,\n" +
                "\t\t\"fullname\": \"张炎\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师助理\",\n" +
                "\t\t\"id\": 2614690,\n" +
                "\t\t\"fullname\": \"刘硕\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师助理\",\n" +
                "\t\t\"id\": 2614754,\n" +
                "\t\t\"fullname\": \"郭炜阳\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"客户顾问\",\n" +
                "\t\t\"id\": 2614760,\n" +
                "\t\t\"fullname\": \"冯振\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"客户顾问\",\n" +
                "\t\t\"id\": 2626014,\n" +
                "\t\t\"fullname\": \"熊松齐\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师助理\",\n" +
                "\t\t\"id\": 2735032,\n" +
                "\t\t\"fullname\": \"张健\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"文案策划\",\n" +
                "\t\t\"id\": 2737932,\n" +
                "\t\t\"fullname\": \"李晨茗\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"客户顾问\",\n" +
                "\t\t\"id\": 2776051,\n" +
                "\t\t\"fullname\": \"无崖子(董鹏)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"出纳\",\n" +
                "\t\t\"id\": 2776058,\n" +
                "\t\t\"fullname\": \"杨莹莹\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师助理\",\n" +
                "\t\t\"id\": 2776665,\n" +
                "\t\t\"fullname\": \"黄药师(姚景元)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"新媒体运营\",\n" +
                "\t\t\"id\": 2786797,\n" +
                "\t\t\"fullname\": \"周三千(周尹琪)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"业务\",\n" +
                "\t\t\"id\": 2833112,\n" +
                "\t\t\"fullname\": \"张无忌(刘雯丽)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"员工\",\n" +
                "\t\t\"id\": 2861642,\n" +
                "\t\t\"fullname\": \"令狐冲(李卫帅)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师助理\",\n" +
                "\t\t\"id\": 2871144,\n" +
                "\t\t\"fullname\": \"虚竹(钟键)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"实习律师\",\n" +
                "\t\t\"id\": 2926814,\n" +
                "\t\t\"fullname\": \"宋英杰\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"高级顾问\",\n" +
                "\t\t\"id\": 3011259,\n" +
                "\t\t\"fullname\": \"张三丰(苏雨)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师助理\",\n" +
                "\t\t\"id\": 3015284,\n" +
                "\t\t\"fullname\": \"钟灵儿（王思媛）\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"运营总监\",\n" +
                "\t\t\"id\": 3015548,\n" +
                "\t\t\"fullname\": \"刘萍筱\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"客户顾问\",\n" +
                "\t\t\"id\": 3015579,\n" +
                "\t\t\"fullname\": \"石破天(陈东亮)\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"律师助理\",\n" +
                "\t\t\"id\": 3029391,\n" +
                "\t\t\"fullname\": \"段誉（王金晗）\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"管委会委员，专业建设委员会主任\",\n" +
                "\t\t\"id\": 3046119,\n" +
                "\t\t\"fullname\": \"费先梅\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"法务\",\n" +
                "\t\t\"id\": 3100682,\n" +
                "\t\t\"fullname\": \"陈涛涛\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}, {\n" +
                "\t\t\"jobTitle\": \"产品经理\",\n" +
                "\t\t\"id\": 3139989,\n" +
                "\t\t\"fullname\": \"杨洋\",\n" +
                "\t\t\"sn\": null,\n" +
                "\t\t\"status\": 5\n" +
                "\t}],\n" +
                "\t\"status\": 200\n" +
                "}";


        JSONObject allEmp = JSONObject.parseObject(allUser);
        String id = getUserId(allEmp,"崔亚强");
        String punchTime ="2019-7-17 15:30";

        //获取时间戳
        if("".equals(punchTime)){
            punchTime =""+new Date().getTime();
        }else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = null;
            try {
                date = simpleDateFormat.parse(punchTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            punchTime = String.valueOf(date.getTime());
            System.out.println(punchTime);
        }

        JSONObject resultObj = buildUserInfo(id,Integer.valueOf(123456),INIT_LNG,INIT_LAT,punchTime);
        System.out.println(resultObj.toJSONString());
    }
}*/
