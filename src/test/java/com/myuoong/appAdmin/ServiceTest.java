package com.myuoong.appAdmin;

import com.myuoong.appAdmin.common.ComConst;
import com.myuoong.appAdmin.config.security.jwt.JwtSettings;
import com.myuoong.appAdmin.model.City;
import com.myuoong.appAdmin.model.User;
import com.myuoong.appAdmin.service.TestService;
import com.myuoong.appAdmin.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@Slf4j
public class ServiceTest {

    @Autowired
    UserService service;

    @Test
    public void 유저_조회() {
        User user = service.selectUserById("admin");

        assertNotNull(user);
        log.info("user => {}",user.toString());
    }
/*
    @Test
    public void getAllCity() {
        List<City> cities = service.getAllCity();
        log.info("cities : {}", cities);
    }


    @Test
    public void addCities() {
        service.addCity(new City("뉴욕", "미국", 1_000_000L));
        service.addCity(new City("런던", "영국", 2_000_000L));
        service.addCity(new City("파리", "프랑스", 3_000_000L));
    }*/

}
