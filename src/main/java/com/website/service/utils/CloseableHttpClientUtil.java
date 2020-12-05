package com.website.service.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CloseableHttpClientUtil {

    public static JSONObject httpPost(String reqUrl, Map<String,Object> reqMap){

        try {

            CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(reqUrl);
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");

            List<NameValuePair> list = new ArrayList<NameValuePair>();

            for(String key:reqMap.keySet()){
                list.add(new BasicNameValuePair(key, reqMap.get(key).toString()));
            }

            httpPost.setEntity(new UrlEncodedFormEntity(list));
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String ux = EntityUtils.toString(entity, "utf-8");
            JSONObject jsonObject = JSON.parseObject(ux);

            if(closeableHttpClient != null){
                closeableHttpClient.close();
            }
            return jsonObject;

        } catch (Exception e) {
            throw new RuntimeException("请求异常",e);
        }
    }
}
