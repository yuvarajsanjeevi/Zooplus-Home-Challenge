package com.cryptocurrency.service.service.impl;

import com.cryptocurrency.service.service.GeoIPLocationService;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

/**
 * Service Layer for handling geolocation by IP
 * @author yuvaraj.sanjeevi
 */
@Service
public class GeoIPLocationServiceImpl implements GeoIPLocationService {

    private DatabaseReader dbReader;

    @Override
    public String getLocation(String ip) throws IOException, GeoIp2Exception {
        InetAddress ipAddress = ip.equalsIgnoreCase("0:0:0:0:0:0:0:1")
                ? InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()) : InetAddress.getByName(ip);
        CountryResponse response = dbReader.country(ipAddress);
        return response.getCountry().getIsoCode();
    }

    @PostConstruct
    public void initDbReader() throws IOException {
        InputStream database = getInputStream("location/GeoLite2-Country.mmdb");
        dbReader = new DatabaseReader.Builder(database).build();
    }

    private InputStream getInputStream(String classpath) throws IOException {
        Resource resource = new ClassPathResource(classpath);
        return resource.getInputStream();
    }
}
