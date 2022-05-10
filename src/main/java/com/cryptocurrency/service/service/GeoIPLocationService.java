package com.cryptocurrency.service.service;

import com.maxmind.geoip2.exception.GeoIp2Exception;

import java.io.IOException;

/**
 * @author yuvaraj.sanjeevi
 */
public interface GeoIPLocationService {
    String getLocation(String ip) throws IOException, GeoIp2Exception;
}
