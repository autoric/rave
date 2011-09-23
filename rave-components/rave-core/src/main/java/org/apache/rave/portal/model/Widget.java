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
package org.apache.rave.portal.model;

import org.apache.rave.persistence.BasicEntity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import java.io.Serializable;

/**
 * A widget
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name="widget")
@NamedQueries({
        @NamedQuery(name = Widget.WIDGET_GET_ALL, query = "SELECT w from Widget w"),
        @NamedQuery(name = Widget.WIDGET_COUNT_ALL, query = "SELECT count(w) FROM Widget w"),
        @NamedQuery(name = Widget.WIDGET_GET_BY_FREE_TEXT,
                query = "SELECT w FROM Widget w WHERE lower(w.title) LIKE :searchTerm OR w.description LIKE :description"),
        @NamedQuery(name = Widget.WIDGET_COUNT_BY_FREE_TEXT,
                query = "SELECT count(w) FROM Widget w WHERE lower(w.title) LIKE :searchTerm OR w.description LIKE :description"),

        @NamedQuery(name = Widget.WIDGET_GET_BY_STATUS,
                query = "SELECT w from Widget w WHERE w.widgetStatus = :widgetStatus"),
        @NamedQuery(name = Widget.WIDGET_COUNT_BY_STATUS,
                query = "SELECT count(w) FROM Widget w WHERE w.widgetStatus = :widgetStatus"),
        @NamedQuery(name = Widget.WIDGET_GET_BY_STATUS_AND_FREE_TEXT,
                query = "SELECT w FROM Widget w WHERE w.widgetStatus = :widgetStatus AND lower(w.title) LIKE :searchTerm OR w.description LIKE :description"),
        @NamedQuery(name = Widget.WIDGET_COUNT_BY_STATUS_AND_FREE_TEXT,
                query = "SELECT count(w) FROM Widget w WHERE w.widgetStatus = :widgetStatus AND lower(w.title) LIKE :searchTerm OR w.description LIKE :description"),

        @NamedQuery(name = Widget.WIDGET_GET_BY_URL, query = "SELECT w FROM Widget w WHERE w.url = :url")
})
public class Widget implements BasicEntity, Serializable {
    private static final long serialVersionUID = 1L;

    public static final String PARAM_SEARCH_TERM = "searchTerm";
    public static final String PARAM_STATUS = "widgetStatus";
    public static final String PARAM_URL = "url";

    public static final String WIDGET_GET_ALL = "Widget.getAll";
    public static final String WIDGET_COUNT_ALL = "Widget.countAll";
    public static final String WIDGET_GET_BY_FREE_TEXT = "Widget.getByFreeText";
    public static final String WIDGET_COUNT_BY_FREE_TEXT = "Widget.countByFreeText";
    public static final String WIDGET_GET_BY_STATUS = "Widget.getByStatus";
    public static final String WIDGET_COUNT_BY_STATUS = "Widget.countByStatus";
    public static final String WIDGET_GET_BY_STATUS_AND_FREE_TEXT =
            "Widget.getByStatusAndFreeText";
    public static final String WIDGET_COUNT_BY_STATUS_AND_FREE_TEXT =
            "Widget.countByStatusAndFreeText";
    public static final String WIDGET_GET_BY_URL = "Widget.getByUrl";

    @Id @Column(name="entity_id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "widgetIdGenerator")
    @TableGenerator(name = "widgetIdGenerator", table = "RAVE_PORTAL_SEQUENCES", pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_COUNT", pkColumnValue = "widget", allocationSize = 1, initialValue = 1)
    private Long entityID;

    /*
        TODO RAVE-234: Figure out what the OpenJPA strategy is for functionality provided by Eclisplink's @Convert
     */
    @XmlElement
    @Basic @Column(name="title")
    private String title;
    //private InternationalString title;

    @XmlElement
    @Basic @Column(name="url", unique = true)
    private String url;

    @Basic @Column(name="thumbnail_url")
    private String thumbnailUrl;

    @Basic @Column(name="screenshot_url")
    private String screenshotUrl;

    @XmlElement
    @Basic @Column(name="type")
    private String type;

    @XmlElement
    @Basic @Column(name="author")
    private String author;

    @XmlElement
    @Basic @Column(name = "description") @Lob
    private String description;

    @XmlElement(name="status")
    @Basic @Column(name = "widget_status")
    @Enumerated(EnumType.STRING)
    private WidgetStatus widgetStatus;


    public Widget() {
    }

    public Widget(Long entityID, String url) {
        this.entityID = entityID;
        this.url = url;
    }

    /**
     * Gets the persistence unique identifier
     *
     * @return id The ID of persisted object; null if not persisted
     */
    @Override
    public Long getEntityId() {
        return entityID;
    }

    @Override
    public void setEntityId(Long entityId) {
        this.entityID = entityId;
    }

    //See TODO RAVE-234
//    public InternationalString getTitle() {
//        return title;
//    }
//
//    public void setTitle(InternationalString title) {
//        this.title = title;
//
// }

    public String getScreenshotUrl() {
        return screenshotUrl;
    }

    public void setScreenshotUrl(String screenshotUrl) {
        this.screenshotUrl = screenshotUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public WidgetStatus getWidgetStatus() {
        return widgetStatus;
    }

    public void setWidgetStatus(WidgetStatus widgetStatus) {
        this.widgetStatus = widgetStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Widget other = (Widget) obj;
        if (this.entityID != other.entityID && (this.entityID == null || !this.entityID.equals(other.entityID))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.entityID != null ? this.entityID.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Widget{" +
                "entityId=" + entityID +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", screenshotUrl='" + screenshotUrl + '\'' +
                ", type='" + type + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", widgetStatus=" + widgetStatus +
                '}';
    }
}