/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.rave.portal.repository;

import org.apache.rave.portal.model.Page;
import org.apache.rave.portal.model.User;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/main/webapp/WEB-INF/dataContext.xml", "file:src/main/webapp/WEB-INF/applicationContext.xml"})
public class JpaUserRepositoryTest {

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "canonical";
    private static final Long INVALID_USER = -2L;

    @Autowired
    private UserRepository repository;

    @Test
    public void getById_validId() {
        User user = repository.getById(USER_ID);
        assertThat(user, CoreMatchers.notNullValue());
        assertThat(user.getUsername(), is(equalTo(USER_NAME)));
        assertThat(user.getPassword(), is(equalTo(USER_NAME)));
        assertThat(user.isAccountNonExpired(), is(true));
    }
    @Test
    public void getById_invalidId() {
        User user = repository.getById(INVALID_USER);
        assertThat(user, is(nullValue()));
    }
    @Test
    public void getByUsername_valid() {
        User user = repository.getByUsername(USER_NAME);
        assertThat(user, CoreMatchers.notNullValue());
        assertThat(user.getUserId(), is(equalTo(USER_ID)));
        assertThat(user.getPassword(), is(equalTo(USER_NAME)));
        assertThat(user.isAccountNonExpired(), is(true));
    }
    @Test
    public void getByUsername_invalid() {
        User user = repository.getById(INVALID_USER);
        assertThat(user, is(nullValue()));
    }
}