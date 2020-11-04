package com.airtel.iq.config;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.airtel.iq.utils.AppUtil;

@Configuration
public class HttpClientConfig {
    @Value("${voice.manager.keep.alive.duration:15000}")
    private Integer voiceManagerKeepAliveDuration;


    @Bean(name = "voiceManagerConnectionKeepAliveStrategy")
    public ConnectionKeepAliveStrategy voiceManagerConnectionKeepAliveStrategy() {
        return new ConnectionKeepAliveStrategy() {

            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                HeaderElementIterator headerElementIterator = new BasicHeaderElementIterator(
                        response.headerIterator(HTTP.CONN_KEEP_ALIVE));

                while (headerElementIterator.hasNext()) {
                    HeaderElement he = headerElementIterator.nextElement();
                    String param = he.getValue();
                    String value = he.getValue();

                    if (!AppUtil.isEmpty(value) && param.equalsIgnoreCase("timeout")) {
                        return Long.parseLong(value) * 1000;
                    }
                }
                return voiceManagerKeepAliveDuration;
            }
        };

    }

   

}
