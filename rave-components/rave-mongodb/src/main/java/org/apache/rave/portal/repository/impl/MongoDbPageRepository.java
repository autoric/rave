/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.rave.portal.repository.impl;

import com.google.common.collect.Lists;
import org.apache.rave.portal.model.*;
import org.apache.rave.portal.model.impl.PageImpl;
import org.apache.rave.portal.model.impl.PageUserImpl;
import org.apache.rave.portal.model.impl.RegionImpl;
import org.apache.rave.portal.model.impl.RegionWidgetImpl;
import org.apache.rave.portal.repository.MongoPageOperations;
import org.apache.rave.portal.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 */
@Repository
public class MongoDbPageRepository implements PageRepository {

    @Autowired
    private MongoPageOperations template;

    @Override
    public List<Page> getAllPages(Long userId, PageType pageType) {
        return template.find(new Query(where("pageType").is(pageType).andOperator(where("ownerId").is(userId))));
    }

    @Override
    public int deletePages(Long userId, PageType pageType) {
        Query query = new Query(where("pageType").is(pageType).andOperator(where("ownerId").is(userId)));
        int count = (int)template.count(query);
        template.remove(query);
        return count;
    }

    @Override
    public Page createPageForUser(User user, PageTemplate pt) {
        return save(convertTemplate(pt, user));
    }

    @Override
    public boolean hasPersonPage(long userId) {
        return template.count(new Query(where("pageType").is(PageType.PERSON_PROFILE).andOperator(where("ownerId").is(userId)))) > 0;
    }

    @Override
    public List<PageUser> getPagesForUser(Long userId, PageType pageType) {
        List<Page> pages = template.find(new Query(where("members").elemMatch(where("userId").is(userId)).andOperator(where("pageType").is(pageType))));
        List<PageUser> userList = Lists.newArrayList();
        for(Page page : pages) {
            userList.add(findPageUser(userId, page));
        }
        return userList;
    }

    @Override
    public PageUser getSingleRecord(Long userId, Long pageId) {
        Page page = get(pageId);
        for(PageUser user : page.getMembers()) {
            if(user.getUser().getId().equals(userId))
                return user;
        }
        return null;
    }

    @Override
    public Class<? extends Page> getType() {
        return MongoDbPage.class;
    }

    @Override
    public Page get(long id) {
        return template.get(id);
    }

    @Override
    public Page save(Page item) {
        return template.save(item);
    }

    @Override
    public void delete(Page item) {
        template.remove(new Query(where("id").is(item.getId())));
    }

    /**
     * convertTemplate: PageTemplate, User -> Page
     * Converts the PageTemplate for Person Profiles into a Person Profile Page
     * @param pt PageTemplate
     * @param user User
     * @return Page
     */
    private Page convertTemplate(PageTemplate pt, User user) {
        Page p = new PageImpl();
        p.setName(pt.getName());
        p.setPageType(pt.getPageType());
        p.setOwner(user);
        PageUser pageUser = new PageUserImpl(user, p, pt.getRenderSequence());
        pageUser.setPageStatus(PageInvitationStatus.OWNER);
        pageUser.setEditor(true);
        List<PageUser> members = new ArrayList<PageUser>();
        members.add(pageUser);
        p.setMembers(members);

        p.setPageLayout(pt.getPageLayout());
        p.setRegions(convertRegions(pt.getPageTemplateRegions(), p));
        p.setSubPages(convertPages(pt.getSubPageTemplates(), p));
        return p;
    }

    /**
     * convertRegions: List of PageTemplateRegion, Page -> List of Regions
     * Converts the JpaRegion Templates of the Page Template to Regions for the page
     * @param pageTemplateRegions List of PageTemplateRegion
     * @param page Page
     * @return list of JpaRegion
     */
    private List<Region> convertRegions(List<PageTemplateRegion> pageTemplateRegions, Page page){
        List<Region> regions = new ArrayList<Region>();
        for (PageTemplateRegion ptr : pageTemplateRegions){
            Region region = new RegionImpl();
            region.setRenderOrder((int) ptr.getRenderSequence());
            region.setPage(page);
            region.setLocked(ptr.isLocked());
            region.setRegionWidgets(convertWidgets(ptr.getPageTemplateWidgets(), region));
            regions.add(region);
        }
        return regions;
    }

    /**
     * convertWidgets: List of PageTemplateWidget, JpaRegion -> List of RegionWidget
     * Converts the Page Template Widgets to RegionWidgets for the given JpaRegion
     * @param pageTemplateWidgets List of PageTemplateWidget
     * @param region JpaRegion
     * @return List of RegionWidget
     */
    private List<RegionWidget> convertWidgets(List<PageTemplateWidget> pageTemplateWidgets, Region region){
        List<RegionWidget> widgets = new ArrayList<RegionWidget>();
        for (PageTemplateWidget ptw : pageTemplateWidgets){
            RegionWidget regionWidget = new RegionWidgetImpl();
            regionWidget.setRegion(region);
            regionWidget.setCollapsed(false);
            regionWidget.setLocked(ptw.isLocked());
            regionWidget.setHideChrome(ptw.isHideChrome());
            regionWidget.setRenderOrder((int) ptw.getRenderSeq());
            regionWidget.setWidget(ptw.getWidget());
            widgets.add(regionWidget);
        }
        return widgets;
    }

    /**
     * convertPages: List of PageTemplate, Page -> List of Page
     * Converts the template subpages in to a list of Pages for the given page object
     * This is a recursive function. A sub page could have a list of sub pages.
     * @param pageTemplates List of PageTemplate
     * @param page Page
     * @return list of Page
     */
    private List<Page> convertPages(List<PageTemplate> pageTemplates, Page page){
        List<Page> pages = new ArrayList<Page>();
        for(PageTemplate pt : pageTemplates){
            Page lPage = new PageImpl();
            lPage.setName(pt.getName());
            lPage.setPageType(pt.getPageType());
            lPage.setOwner(page.getOwner());
            lPage.setPageLayout(pt.getPageLayout());
            lPage.setParentPage(page);
            lPage.setRegions(convertRegions(pt.getPageTemplateRegions(), lPage));

            // create new pageUser tuple
            PageUser pageUser = new PageUserImpl(lPage.getOwner(), lPage, pt.getRenderSequence());
            pageUser.setPageStatus(PageInvitationStatus.OWNER);
            pageUser.setEditor(true);
            List<PageUser> members = new ArrayList<PageUser>();
            members.add(pageUser);
            lPage.setMembers(members);
            // recursive call
            lPage.setSubPages((pt.getSubPageTemplates() == null || pt.getSubPageTemplates().isEmpty()) ? null : convertPages(pt.getSubPageTemplates(), lPage));
            pages.add(lPage);
        }
        return pages;
    }

    private PageUser findPageUser(Long userId, Page page) {
        for(PageUser user : page.getMembers()) {
            if(user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

}
