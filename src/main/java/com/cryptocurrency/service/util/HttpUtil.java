package com.cryptocurrency.service.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yuvaraj.sanjeevi
 */
public class HttpUtil {


    /**
     * This method is used to get client ip
     * @param request               - http request
     * @param otherHeaderNames      - other headers to lookup
     * @return                      - clientt ip address
     */
    public static String getClientIP(HttpServletRequest request, String... otherHeaderNames) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        if(ArrayUtils.isNotEmpty(headers)){
            headers = ArrayUtils.addAll(headers, otherHeaderNames);
        }

        String ip;
        for (String header : headers) {
            ip = request.getHeader(header);
            if(!isUnknown(ip)){
                return getMultistageReverseProxyIp(ip);
            }
        }

        ip = request.getRemoteAddr();
        return getMultistageReverseProxyIp(ip);
    }


    /**
     * To check given String is unknown or null
     * @param checkString       - string to check
     * @return                  - true or false
     */
    private static boolean isUnknown(String checkString) {
        return StringUtils.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }

    /**
     * To extract multi stage reverse proxy ip
     * @param ip        - ip
     * @return          - single ip
     */
    private static String getMultistageReverseProxyIp(String ip){
        if (ip != null && ip.indexOf(",") > 0) {
            final String[] ips = ip.trim().split(",");
            for (String subIp : ips) {
                if(!isUnknown(subIp)){
                    ip = subIp;
                    break;
                }
            }
        }
        return ip;
    }
}
