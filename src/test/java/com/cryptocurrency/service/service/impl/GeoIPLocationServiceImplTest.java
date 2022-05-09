package com.cryptocurrency.service.service.impl;

import com.cryptocurrency.service.TestUtil;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

/**
 * @author yuvaraj.sanjeevi
 */
class GeoIPLocationServiceImplTest {

    @Mock
    private DatabaseReader dbReader;

    @InjectMocks
    private GeoIPLocationServiceImpl geoIPLocationServiceImpl;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @Test
    void getLocation_ShouldThrowException() throws IOException, GeoIp2Exception {
        when(dbReader.country(any())).thenThrow(new RuntimeException("The address is not in the database."));
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            geoIPLocationServiceImpl.getLocation("1.1.1.1");
        });
        Assertions.assertEquals("The address is not in the database.", exception.getMessage());
    }

    @Test
    void getLocation_ShouldSuccess() throws IOException, GeoIp2Exception {
        CountryResponse countryResponse = Mockito.mock(CountryResponse.class, Mockito.RETURNS_DEEP_STUBS);
        when(dbReader.country(any())).thenReturn(countryResponse);

        when(countryResponse.getCountry().getIsoCode()).thenReturn("GB");
        Assertions.assertEquals("GB", geoIPLocationServiceImpl.getLocation("2.31.255.255"));
    }
}
