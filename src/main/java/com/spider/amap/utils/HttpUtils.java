package com.spider.amap.utils;

import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 网络请求工具类
 *
 * @author zhangjun 1580043700@qq.com
 * @date 2018/9/26
 */

public class HttpUtils {

    private static final int TIME_OUT = 10000;//超时时间


    /**
     * post请求
     *
     * @param url 请求地址
     * @return
     */
    public static String post(String url) {
        return post(url, null);
    }

    /**
     * post请求
     *
     * @param url       请求地址
     * @param listParam 参数
     * @return
     */
    public static String post(String url, Map<String, Object> listParam) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        RequestConfig config = RequestConfig.custom().setConnectTimeout(TIME_OUT)
                .setConnectionRequestTimeout(TIME_OUT).setSocketTimeout(TIME_OUT).setRedirectsEnabled(true).build();
        post.setConfig(config);
        post.setHeader("Content-type", "application/x-www-form-urlencoded");
        List<NameValuePair> param = new ArrayList<>();
        for (Map.Entry<String, Object> entry : listParam.entrySet()) {
            param.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
        }
        try {
            post.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
            HttpResponse response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, "utf-8");
            }
        } catch (UnsupportedEncodingException e) {

        } catch (ClientProtocolException e) {

        } catch (IOException e) {

        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 请求
     *
     * @param reqURL  请求URL
     * @param jsonStr json参数
     * @return
     */
    public static String postJson(String reqURL, String jsonStr) {
        CloseableHttpClient httpclient = HttpClients.custom().build();
        try {
            RequestBuilder builder = RequestBuilder.post(reqURL);
            builder.setCharset(Charset.forName("UTF-8"));
            StringEntity stringEntity = new StringEntity(jsonStr, "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            builder.setEntity(stringEntity);
            HttpUriRequest post = builder.build();
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(entity);
                return result;
            } finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    /**
     * get请求
     *
     * @param url 请求地址
     * @return
     */
    public static String get(String url) {
        return get(url, null);
    }


    /**
     * get请求
     *
     * @param url       请求地址
     * @param listParam 参数
     * @return
     */
    public static String get(String url, Map<String, String> listParam) {
        if (!MapUtils.isEmpty(listParam)) {
            StringBuffer param = new StringBuffer("?");
            for (Map.Entry<String, String> entry : listParam.entrySet()) {
                param.append(String.valueOf(entry.getKey())).append("=").append(String.valueOf(entry.getValue())).append("&");
            }
            url += param;
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        RequestConfig config = RequestConfig.custom().setConnectTimeout(TIME_OUT)
                .setConnectionRequestTimeout(TIME_OUT).setSocketTimeout(TIME_OUT).setRedirectsEnabled(true).build();
        httpGet.setConfig(config);
        httpGet.setHeader("Content-type", "application/x-www-form-urlencoded");
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, "utf-8");
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
