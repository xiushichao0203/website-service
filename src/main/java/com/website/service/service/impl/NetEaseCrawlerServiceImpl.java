package com.website.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.website.service.constant.Constants;
import com.website.service.dto.WyyContent;
import com.website.service.dto.WyyUserContent;
import com.website.service.service.NetEaseCrawlerService;
import com.website.service.utils.EncryptUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;


@Service
public class NetEaseCrawlerServiceImpl implements NetEaseCrawlerService {

    @Resource
    private RestTemplate restTemplate;

    @Override
    public List<WyyContent> getSongInfoByLimitAndPageNo(String songId, int limit, int pageNo) {
        String offsets = String.valueOf(limit * pageNo);
        String httpUrl = Constants.NET_EASE_COMMENT_API_URL_NO_ENCRYPT
                .replace("limits",String.valueOf(limit)).replace("offsets",offsets).replace("songId",songId);
        try {

            CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(httpUrl);
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");

            List<NameValuePair> list = new ArrayList<NameValuePair>();

            httpPost.setEntity(new UrlEncodedFormEntity(list));
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String ux = EntityUtils.toString(entity, "utf-8");
            JSONObject jsonObject = JSON.parseObject(ux);

            if(Objects.isNull(jsonObject)){
                Thread.sleep((long) (Math.random() * 30000));
                return getSongInfoByLimitAndPageNo(songId,limit,pageNo);
            }else{
                JSONArray comments = jsonObject.getJSONArray("comments");
                return TextToWyyContentListWithJSONObject(comments);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<WyyContent>  getSongInfoByLimitAndPageNoWithEncrtption(String songId, int limit, int pageNo) {
        try {

            String secKey = new BigInteger(100, new SecureRandom()).toString(32).substring(0, 16);
            String firstKey = getContentPageInfo(limit,pageNo);
            String encText = EncryptUtils.aesEncrypt(EncryptUtils.aesEncrypt(firstKey, Constants.NET_EASE_ENCRYPT_KEY), secKey);
            String encSecKey = EncryptUtils.rsaEncrypt(secKey);

            String httpUrl = Constants.NET_EASE_COMMENT_API_URL_ENCRYPT + songId + "/?csrf_token=";


            CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(httpUrl);
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");

            List<NameValuePair> list = new ArrayList<NameValuePair>();


            list.add(new BasicNameValuePair("params", encText));
            list.add(new BasicNameValuePair("encSecKey", encSecKey));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String ux = EntityUtils.toString(entity, "utf-8");
            JSONObject jsonObject = JSON.parseObject(ux);

            if(closeableHttpClient != null){
                closeableHttpClient.close();
            }

            if(Objects.isNull(jsonObject)){
                return getSongInfoByLimitAndPageNoWithEncrtption(songId, limit, pageNo);
            }else{
                JSONArray comments = jsonObject.getJSONArray("comments");
                return TextToWyyContentListWithJSONObject(comments);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private  List<WyyContent> TextToWyyContentList(List objects) {
        List<WyyContent> List = new ArrayList();
        for (Object v : objects) {
            WyyContent wyyContent = new WyyContent();
            Map<String,Object> objectMap = (Map)v;
            Map<String,Object> userMap = (Map)objectMap.get("user");
            wyyContent.setContent((String)objectMap.get("content"));
            wyyContent.setUserimg((String)userMap.get("avatarUrl"));
            wyyContent.setUsername((String)userMap.get("nickname"));
            wyyContent.setLikedCount(String.valueOf(objectMap.get("likedCount")));
            wyyContent.setTime(objectMap.get("time").toString());

            ArrayList arrayList = new ArrayList();
            List beRepliedList = (ArrayList)objectMap.get("beReplied");


            for (Object i : beRepliedList) {
                WyyUserContent wyyUserContent = new WyyUserContent();
                Map<String,Object> objMap = (Map)i;
                wyyUserContent.setContent((String)objMap.get("content"));
                objMap = (Map)objMap.get("user");
                wyyUserContent.setUserimg((String)objMap.get("avatarUrl"));
                wyyUserContent.setUsername((String)objMap.get("nickname"));
                arrayList.add(wyyUserContent);
            }
            wyyContent.setBeReplied(arrayList);
            List.add(wyyContent);
        }
        return List;
    }


    private  List<WyyContent> TextToWyyContentListWithJSONObject(List objects) {
        List<WyyContent> List = new ArrayList();
        for (Object v : objects) {
            WyyContent wyyContent = new WyyContent();
            JSONObject object = (JSONObject) v;
            wyyContent.setContent(object.getString("content"));
            wyyContent.setUserimg(object.getJSONObject("user").getString("avatarUrl"));
            wyyContent.setUsername(object.getJSONObject("user").getString("nickname"));
            wyyContent.setLikedCount(object.getString("likedCount"));
            wyyContent.setTime(object.getString("time"));

            ArrayList arrayList = new ArrayList();
            JSONArray beReplied = object.getJSONArray("beReplied");


            for (Object i : beReplied) {
                WyyUserContent wyyUserContent = new WyyUserContent();
                JSONObject objecti = (JSONObject) i;
                wyyUserContent.setContent(objecti.getString("content"));
                objecti = objecti.getJSONObject("user");
                wyyUserContent.setUserimg(objecti.getString("avatarUrl"));
                wyyUserContent.setUsername(objecti.getString("nickname"));
                arrayList.add(wyyUserContent);
            }
            wyyContent.setBeReplied(arrayList);
            List.add(wyyContent);
        }
        return List;
    }

    private String getContentPageInfo(int limit,int pageNo){
        String firstParam;
        String offset;
        if(pageNo ==0 ){
            firstParam = "{rid:\"\", offset:\"0\", total:\"true\", limit:\""+limit+"\", csrf_token:\"\"}";
        }else{
            offset = String.valueOf(pageNo*limit);
            firstParam = "{rid:\"\", offset:\""+offset+"\", total:\"false\", limit:\""+limit+"\", csrf_token:\"\"}";
        }
        return firstParam;
    }

    @Override
    public String getTotalComments(String songId) {
        String httpUrl = Constants.NET_EASE_COMMENT_API_URL_NO_ENCRYPT
                .replace("limits","1").replace("offsets","0").replace("songId",songId);
        try {
            CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(httpUrl);
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");

            List<NameValuePair> list = new ArrayList<NameValuePair>();

            httpPost.setEntity(new UrlEncodedFormEntity(list));
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String ux = EntityUtils.toString(entity, "utf-8");
            JSONObject jsonObject = JSON.parseObject(ux);

            if(Objects.isNull(jsonObject)){
                Thread.sleep((long) (Math.random() * 30000));
                return getTotalComments(songId);
            }else{
                String total = jsonObject.getString("total");
                return total;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getTotalCommentsWithEncrtption(String songId) {
        try {
            String secKey = new BigInteger(100, new SecureRandom()).toString(32).substring(0, 16);
            String firstKey = getContentPageInfo(1,0);
            String encText = EncryptUtils.aesEncrypt(EncryptUtils.aesEncrypt(firstKey, Constants.NET_EASE_ENCRYPT_KEY), secKey);
            String encSecKey = EncryptUtils.rsaEncrypt(secKey);

            String httpUrl = Constants.NET_EASE_COMMENT_API_URL_ENCRYPT + songId + "/?csrf_token=";


            CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(httpUrl);
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");

            List<NameValuePair> list = new ArrayList<NameValuePair>();


            list.add(new BasicNameValuePair("params", encText));
            list.add(new BasicNameValuePair("encSecKey", encSecKey));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String ux = EntityUtils.toString(entity, "utf-8");
            JSONObject jsonObject = JSON.parseObject(ux);

            if(closeableHttpClient != null){
                closeableHttpClient.close();
            }

            if(Objects.isNull(jsonObject)){
                return getTotalCommentsWithEncrtption(songId);
            }else{
                String total = jsonObject.getString("total");
                return total;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> getNewesttFavoriteSongInfo(String playListId) {
        try {
            String httpUrl = Constants.FAVORATE_SONG_LIST_URL + playListId;
            String reqString = restTemplate.getForObject(httpUrl,String.class);
            if(StringUtils.isNotEmpty(reqString)){
                String songStr = reqString.split("<ul class=\"f-hide\"><li>")[1].split("</li></ul>")[0];
                if(StringUtils.isNotEmpty(songStr)){
                    List<Map<String, Object>> songInfoList = new ArrayList<>();
                    String[] songInfoArr = songStr.split("</li><li>");
                    for(String songInfo:songInfoArr){
                        String[] songArr = songInfo.split("\">");
                        String songId = songArr[0].replace("<a href=\"/song?id=","");
                        String songName = songArr[1].replace("</a>","");
                        Map<String,Object> songInfoMap = new HashMap<>(2);
                        songInfoMap.put("songId",songId);
                        songInfoMap.put("songName",songName);
                        songInfoList.add(songInfoMap);
                    }
                    return songInfoList;
                }
            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
