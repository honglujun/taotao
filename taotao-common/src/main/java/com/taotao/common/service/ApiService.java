package com.taotao.common.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.bean.HttpResult;

@Service
public class ApiService {

    @Autowired(required = false)
    private CloseableHttpClient httpClient;

    @Autowired(required = false)
    private RequestConfig requestConfig;

    /**
     * 指定GET请求
     * 
     * @param url
     * @return 如果请求成功，返回具体内容，如果失败，返回null
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String doGet(String url) throws ClientProtocolException, IOException {
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpClient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
            return null;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 带有参数的Delete请求(通过post请求完成)
     * 
     * @param url
     * @param params
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResult doDelete(String url, Map<String, Object> params) throws ClientProtocolException,
            IOException {
        if (null != params) {
            params.put("_method", "DELETE");
        }
        return this.doPost(url, params);
    }

    /**
     * 执行delete请求（真正的delete请求）
     * 
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResult doDelete(String url) throws ClientProtocolException, IOException {
        // 创建http POST请求
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = this.httpClient.execute(httpDelete);
            HttpResult httpResult = new HttpResult();
            httpResult.setCode(response.getStatusLine().getStatusCode());
            if (response.getEntity() != null) {
                // 如果响应中有内容就设置到HttpResult，无需考虑状态码
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                httpResult.setData(content);
            }
            return httpResult;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 带有参数的PUT请求
     * 
     * @param url
     * @param params
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResult doPut(String url, Map<String, Object> params) throws ClientProtocolException,
            IOException {
        // 创建http POST请求
        HttpPut httpPut = new HttpPut(url);
        httpPut.setConfig(requestConfig);
        if (null != params) {
            // 设置post参数
            List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (null == entry.getValue()) {
                    // 忽略该参数
                    continue;
                }
                parameters.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
            }

            // 构造一个form表单式的实体
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
            // 将请求实体设置到httpPost对象中
            httpPut.setEntity(formEntity);
        }
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = this.httpClient.execute(httpPut);
            HttpResult httpResult = new HttpResult();
            httpResult.setCode(response.getStatusLine().getStatusCode());
            if (response.getEntity() != null) {
                // 如果响应中有内容就设置到HttpResult，无需考虑状态码
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                httpResult.setData(content);
            }
            return httpResult;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 执行put请求
     * 
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResult doPut(String url) throws ClientProtocolException, IOException {
        return doPut(url, null);
    }

    /**
     * 带有参数的GET请求
     * 
     * @param url
     * @param params
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public String doGet(String url, Map<String, Object> params) throws ClientProtocolException, IOException,
            URISyntaxException {
        // 定义请求的参数
        URIBuilder builder = new URIBuilder(url);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.addParameter(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return this.doGet(builder.build().toString());
    }

    /**
     * 执行不带参数的POST请求
     * 
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResult doPost(String url) throws ClientProtocolException, IOException {
        return this.doPost(url, null);
    }

    /**
     * 执行POST请求
     * 
     * @param url
     * @param params
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResult doPost(String url, Map<String, Object> params) throws ClientProtocolException,
            IOException {
        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        if (null != params) {
            // 设置post参数
            List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (null == entry.getValue()) {
                    // 忽略该参数
                    continue;
                }
                parameters.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
            }

            // 构造一个form表单式的实体
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(formEntity);
        }
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = this.httpClient.execute(httpPost);
            HttpResult httpResult = new HttpResult();
            httpResult.setCode(response.getStatusLine().getStatusCode());
            if (response.getEntity() != null) {
                // 如果响应中有内容就设置到HttpResult，无需考虑状态码
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                httpResult.setData(content);
            }
            return httpResult;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 提交json数据
     * 
     * @param url
     * @param json
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResult doPostJson(String url, String json) throws ClientProtocolException, IOException {
        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        if (StringUtils.isNotEmpty(json)) {
            // 构造一个String的实体
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(stringEntity);
        }
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = this.httpClient.execute(httpPost);
            HttpResult httpResult = new HttpResult();
            httpResult.setCode(response.getStatusLine().getStatusCode());
            if (response.getEntity() != null) {
                // 如果响应中有内容就设置到HttpResult，无需考虑状态码
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                httpResult.setData(content);
            }
            return httpResult;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

}
