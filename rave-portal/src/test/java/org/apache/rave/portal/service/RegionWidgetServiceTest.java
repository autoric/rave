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

package org.apache.rave.portal.service;

import org.apache.commons.lang.StringUtils;
import org.apache.rave.portal.model.RegionWidget;
import org.apache.rave.portal.model.RegionWidgetPreference;
import org.apache.rave.portal.repository.RegionWidgetRepository;
import org.apache.rave.portal.service.impl.DefaultRegionWidgetService;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class RegionWidgetServiceTest {
    private RegionWidgetRepository regionWidgetRepository;
    private RegionWidgetService regionWidgetService;

    final Long VALID_REGION_WIDGET_ID = 1L;
    final Long INVALID_REGION_WIDGET_ID = 100L;

    @Before
    public void setup() {
        regionWidgetRepository = createNiceMock(RegionWidgetRepository.class);
        regionWidgetService = new DefaultRegionWidgetService(regionWidgetRepository);
    }

    @Test
    public void getRegionWidget_validId() {
        final RegionWidget VALID_REGION_WIDGET = new RegionWidget(VALID_REGION_WIDGET_ID);

        expect(regionWidgetRepository.get(VALID_REGION_WIDGET_ID)).andReturn(VALID_REGION_WIDGET);
        replay(regionWidgetRepository);

        assertThat(regionWidgetService.getRegionWidget(VALID_REGION_WIDGET_ID), CoreMatchers.sameInstance(VALID_REGION_WIDGET));
    }

    @Test
    public void getRegionWidget_invalidId() {
        expect(regionWidgetRepository.get(INVALID_REGION_WIDGET_ID)).andReturn(null);
        replay(regionWidgetRepository);

        assertThat(regionWidgetService.getRegionWidget(INVALID_REGION_WIDGET_ID), CoreMatchers.<Object>nullValue());
    }

    @Test
    public void saveRegionWidget() {
        final RegionWidget VALID_REGION_WIDGET = new RegionWidget(VALID_REGION_WIDGET_ID);

        expect(regionWidgetRepository.save(VALID_REGION_WIDGET)).andReturn(VALID_REGION_WIDGET);
        replay(regionWidgetRepository);

        assertThat(regionWidgetService.saveRegionWidget(VALID_REGION_WIDGET), CoreMatchers.sameInstance(VALID_REGION_WIDGET));
    }

    @Test
    public void saveRegionWidgetPreferences() {
        final RegionWidget VALID_REGION_WIDGET = new RegionWidget(VALID_REGION_WIDGET_ID);
        VALID_REGION_WIDGET.setPreferences(getTestExistingRegionWidgetPreferences());

        expect(regionWidgetRepository.get(VALID_REGION_WIDGET_ID)).andReturn(VALID_REGION_WIDGET);
        expect(regionWidgetRepository.save(VALID_REGION_WIDGET)).andReturn(VALID_REGION_WIDGET);
        replay(regionWidgetRepository);

        //Make sure that removing and changing existing preferences is handled properly, and also ensure adding a preference works.
        List<RegionWidgetPreference> updatedPreferences = getTestUpdatedRegionWidgetPreferences();
        List<RegionWidgetPreference> savedPreferences = regionWidgetService.saveRegionWidgetPreferences(VALID_REGION_WIDGET_ID, updatedPreferences);
        assertTrue(preferenceCollectionsMatch(updatedPreferences, savedPreferences));
        assertTrue(preferencesHaveValidRegionWidgetId(savedPreferences));
    }

    @Test
    public void saveRegionWidgetPreference() {
        final RegionWidget VALID_REGION_WIDGET = new RegionWidget(VALID_REGION_WIDGET_ID);
        VALID_REGION_WIDGET.setPreferences(getTestExistingRegionWidgetPreferences());

        expect(regionWidgetRepository.get(VALID_REGION_WIDGET_ID)).andReturn(VALID_REGION_WIDGET).anyTimes();
        expect(regionWidgetRepository.save(VALID_REGION_WIDGET)).andReturn(VALID_REGION_WIDGET).anyTimes();
        replay(regionWidgetRepository);

        //Add and update a preference.
        RegionWidgetPreference newPreference = new RegionWidgetPreference(null, null, "age", "30");
        RegionWidgetPreference savedNewPreference = regionWidgetService.saveRegionWidgetPreference(VALID_REGION_WIDGET_ID, newPreference);
        RegionWidgetPreference updatedPreference = new RegionWidgetPreference(null, null, "color", "purple");
        RegionWidgetPreference savedUpdatedPreference = regionWidgetService.saveRegionWidgetPreference(VALID_REGION_WIDGET_ID, updatedPreference);

        //Make sure the new and updated preference got mixed in properly with the existing preferences.
        List<RegionWidgetPreference> existingPreferences = getTestExistingRegionWidgetPreferences();
        existingPreferences.add(savedNewPreference);
        existingPreferences.get(0).setValue("purple");
        assertTrue(preferenceCollectionsMatch(existingPreferences, VALID_REGION_WIDGET.getPreferences()));
        assertTrue(preferencesHaveValidRegionWidgetId(VALID_REGION_WIDGET.getPreferences()));
    }

    private boolean preferencesHaveValidRegionWidgetId(List<RegionWidgetPreference> savedPreferences) {
        for (RegionWidgetPreference savedPreference : savedPreferences) {
            if (!savedPreference.getRegionWidgetId().equals(VALID_REGION_WIDGET_ID)) {
                return false;
            }
        }

        return true;
    }

    private boolean preferenceCollectionsMatch(List<RegionWidgetPreference> updatedPreferences, List<RegionWidgetPreference> savedPreferences) {
        Map<String, RegionWidgetPreference> updatedPreferencesMap = new HashMap<String, RegionWidgetPreference>();

        //Make sure they're the same size
        if (updatedPreferences.size() != savedPreferences.size()) {
            return false;
        }

        //Map out the updated preferences so we can compare them to the saved preferences
        for (RegionWidgetPreference updatedPreference : updatedPreferences) {
            updatedPreferencesMap.put(updatedPreference.getName(), updatedPreference);
        }

        //We know the lists are the same length - so as long as all the savedPreferences exist and match the updated
        //preferences then we are all good.
        for (RegionWidgetPreference savedPreference : savedPreferences) {
            RegionWidgetPreference updatedPreference = updatedPreferencesMap.get(savedPreference.getName());
            if (updatedPreference == null || !StringUtils.equals(savedPreference.getName(), updatedPreference.getName())
                    || !StringUtils.equals(savedPreference.getValue(), updatedPreference.getValue())) {
                return false;
            }
        }

        return true;
    }

    private List<RegionWidgetPreference> getTestExistingRegionWidgetPreferences() {
        ArrayList<RegionWidgetPreference> regionWidgetPreferences = new ArrayList<RegionWidgetPreference>();
        regionWidgetPreferences.add(new RegionWidgetPreference(1L, VALID_REGION_WIDGET_ID, "color", "blue"));
        regionWidgetPreferences.add(new RegionWidgetPreference(2L, VALID_REGION_WIDGET_ID, "speed", "fast"));
        return regionWidgetPreferences;
    }

    private List<RegionWidgetPreference> getTestUpdatedRegionWidgetPreferences() {
        List<RegionWidgetPreference> regionWidgetPreferences = getTestExistingRegionWidgetPreferences();
        regionWidgetPreferences.remove(0);
        regionWidgetPreferences.get(0).setValue("slow");
        regionWidgetPreferences.add(new RegionWidgetPreference(null, VALID_REGION_WIDGET_ID, "size", "small"));
        return regionWidgetPreferences;
    }
}