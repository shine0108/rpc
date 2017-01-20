package com.scalahome.service;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by fuqing.xfq on 2016/12/28.
 */
public class DefaultHttpClient implements Client {

    private final int DEFAULT_TIMEOUT = 10 * 1000;

    private Logger logger = Logger.getLogger(DefaultHttpClient.class);

    private HttpClient client = HttpClientBuilder.create().build();

    RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(DEFAULT_TIMEOUT)
            .setConnectTimeout(DEFAULT_TIMEOUT)
            .setConnectionRequestTimeout(DEFAULT_TIMEOUT)
            .build();

    @Override
    public void setTimeOut(int timeOut) {
        if(requestConfig.getConnectTimeout() != timeOut) {
            requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeOut)
                    .setConnectTimeout(timeOut)
                    .setConnectionRequestTimeout(timeOut)
                    .build();
        }
    }

    @Override
    public String request(String host, int port, String path, Map<String, String> params) throws Exception {
        path = path.startsWith("/") ? path : "/" + path;
        String uri = "http://" + host + ":" + port + path;
        if(params != null && !params.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('?');
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuilder.append(entry.getKey());
                stringBuilder.append('=');
                stringBuilder.append(entry.getValue());
                stringBuilder.append('&');
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            uri = uri + stringBuilder.toString();
        }
        logger.info("uri:" + uri);
        HttpGet request = new HttpGet(uri);
        request.setConfig(requestConfig);
        Result result = client.execute(request, new ResponseHandler<Result>() {
            @Override
            public Result handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                Result result = new Result();
                try {
                    result.responseCode = httpResponse.getStatusLine().getStatusCode();
                    InputStream is = httpResponse.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                        stringBuilder.append("\n");
                    }
                    result.context = stringBuilder.toString();
                } finally {
                    return result;
                }
            }
        });
        if(result.responseCode != 200) {
            throw new RuntimeException("Request Error, ResponseCode:" + result.responseCode + ", Context:" + result.context);
        }
        return result.context;
    }

    class Result {
        public int responseCode;
        public String context;
    }
}
