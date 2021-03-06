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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * A Rating for a Widget
 */
@Entity
@Table(name = "widget_rating")
@NamedQueries ({
        @NamedQuery(name = WidgetRating.WIDGET_ALL_TOTAL_LIKES,
                query = "SELECT COUNT(wr) total, wr.widgetId widgetIt FROM WidgetRating wr WHERE wr.score = 10 GROUP BY wr.widgetId"),
        @NamedQuery(name = WidgetRating.WIDGET_TOTAL_LIKES,
                query = "SELECT COUNT(wr) FROM WidgetRating wr WHERE wr.widgetId = :widgetId AND wr.score = 10"),
        @NamedQuery(name = WidgetRating.WIDGET_ALL_TOTAL_DISLIKES,
                query = "SELECT COUNT(wr) total, wr.widgetId widgetId FROM WidgetRating wr WHERE wr.score = 0 GROUP BY wr.widgetId"),
        @NamedQuery(name = WidgetRating.WIDGET_TOTAL_DISLIKES,
                query = "SELECT COUNT(wr) FROM WidgetRating wr WHERE wr.widgetId = :widgetId AND wr.score = 0"),
        @NamedQuery(name = WidgetRating.WIDGET_ALL_USER_RATINGS,
                query = "SELECT wr FROM WidgetRating wr WHERE wr.userId = :userId"),
        @NamedQuery(name = WidgetRating.WIDGET_RATING_BY_WIDGET_AND_USER,
                query = "SELECT wr FROM WidgetRating wr WHERE wr.widgetId = :widgetId AND wr.userId = :userId"),
        @NamedQuery(name = WidgetRating.WIDGET_USER_RATING,
                query = "SELECT wr.score FROM WidgetRating wr WHERE wr.widgetId = :widgetId AND wr.userId = :userId"),
        @NamedQuery(name = WidgetRating.DELETE_ALL_BY_USER,
                query="DELETE FROM WidgetRating wr WHERE wr.userId = :userId")
})
@XmlRootElement
public class WidgetRating implements BasicEntity, Serializable {

    public static final String WIDGET_ALL_TOTAL_LIKES = "widget_all_total_likes";
    public static final String WIDGET_TOTAL_LIKES = "widget_total_likes";
    public static final String WIDGET_ALL_TOTAL_DISLIKES = "widget_all_total_dislikes";
    public static final String WIDGET_TOTAL_DISLIKES = "widget_total_dislikes";
    public static final String WIDGET_ALL_USER_RATINGS = "widget_all_user_ratings";
    public static final String WIDGET_RATING_BY_WIDGET_AND_USER = "widget_rating_by_widget_and_user";
    public static final String WIDGET_USER_RATING = "widget_user_rating";
    public static final String DELETE_ALL_BY_USER = "delete_all_for_user";

    public static final String PARAM_WIDGET_ID = "widgetId";
    public static final String PARAM_USER_ID = "userId";

    @Id
    @Column(name = "entity_id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "widgetRatingIdGenerator")
    @TableGenerator(name = "widgetRatingIdGenerator", table = "RAVE_PORTAL_SEQUENCES", pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_COUNT", pkColumnValue = "widget_rating", allocationSize = 1, initialValue = 1)
    private Long entityId;
    
    @Basic
    @Column(name = "widget_id")
    private Long widgetId;
    
    @Basic
    @Column(name = "user_id")
    private Long userId;
    
    @Basic
    @Column(name = "score")
    private Integer score;
    
    public static final Integer LIKE = 10; 
    public static final Integer DISLIKE = 0;
    public static final Integer UNSET = -1;

    public WidgetRating() {
        
    }
    
    public WidgetRating(Long entityId, Long widgetId, Long userId, Integer score) {
        this.entityId = entityId;
        this.widgetId = widgetId;
        this.userId = userId;
        this.score = score;
    }
    
    /**
     * Gets the persistence unique identifier
     *
     * @return id The ID of persisted object; null if not persisted
     */
    @Override
    public Long getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
    
    /**
     * Gets the ID of the Widget this rating is for
     * @return The ID of the Widget this rating is for
     */
    public Long getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(Long widgetId) {
        this.widgetId = widgetId;
    }
    
    /**
     * Gets the ID of the User this rating is for
     * @return The ID of the User this rating is for
     */
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    /**
     * Gets the score of this rating
     *
     * @return The score of this rating
     */
    public Integer getScore() {
        return score;
    }

    public void setScore(Integer value) {
        this.score = value;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.entityId != null ? this.entityId.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WidgetRating other = (WidgetRating) obj;
        if (this.entityId != other.entityId && (this.entityId == null || !this.entityId.equals(other.entityId))) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "WidgetRating{" +
                "entityId=" + entityId +
                ", widgetId=" + widgetId + 
                ", userId=" + userId + 
                ", score=" + score + 
                '}';
    }
}
