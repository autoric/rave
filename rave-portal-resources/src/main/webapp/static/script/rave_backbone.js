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
var rave = rave || {};

rave.Model = Backbone.Model.extend({
    get: function(attr){
        //tweak model get so that array / object members are passed by value instead of reference
        return _.clone(this.attributes[attr]);
    },
    toViewModel: function () {
        return this.toJSON();
    }
});

rave.Collection = Backbone.Collection.extend({
    toViewModel: function () {
        return this.map(function (model) {
            return model.toViewModel();
        });
    }
})


/*
 A view has a hash of models (can be a model or a collection) that will be merged at render time.
 By default any change will re-render the view
 */
rave.View = Backbone.View.extend({
    initialize: function () {
        var self = this;
        _.bindAll(this);

        _.each(this.models, function (model) {
            model.on('change', self.render);
            model.on('reset', self.render);
        });
    },
    render: function () {
        var template = this.template;

        var viewData = {};
        _.each(this.models, function (model, key) {
            viewData[key] = model.toViewModel();
        });

        this.$el.html(template(viewData));
        return this;
    }
});


/*
 Models:

 Users (?)

 Widget
 - Add / delete like
 - Add / delete dislike
 - Create, update, delete comment
 - Get users of widget
 - Create / Delete tag
 - Add to page

 RegionWidget
 - Save collapsed state
 - Save Preferences
 - Move

 Page
 - Add, Delete, Get
 - Add / get "members" and "editors" (page sharing)

 */

/*
 What do I need from a model?

 - Data representation as retrieved
 - toJSON and toViewModel representation
 - manipulation methods- change and save
 - change events
 */
