package com.myuoong.appAdmin.service;

import com.myuoong.appAdmin.mapper.CityMapper;
import com.myuoong.appAdmin.mapper.UserMapper;
import com.myuoong.appAdmin.model.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TestService {
    @Autowired
    CityMapper cityMapper;

    @Autowired
    UserMapper userMapper;

    public City getCityById(Long id) {
        return cityMapper.selectCityById(id);
    }

    public List<City> getAllCity() {
        return cityMapper.selectAllCity();
    }

    public void addCity(City city) {
        cityMapper.insertCity(city);
    }

}
