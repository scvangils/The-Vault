// Created by carme
// Creation date 15/12/2021

package com.example.thevault.database.mapping.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class JDBCTestDAOTest {

    @Test
    public void setupTest(){
        assertThat(1).isNotNull();
    }
}
