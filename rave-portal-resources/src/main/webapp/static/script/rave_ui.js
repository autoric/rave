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

rave.ui = rave.ui || (function () {
    var exports = {};

    //TODO: rave.getClientMessage view helper? Thank about it.
    Handlebars.registerHelper('getClientMessage', function (key) {
        return rave.getClientMessage(key);
    })

    var templates = {};
    $('[data-template-for]').each(function () {
        var key = $(this).data('template-for');
        var source = $(this).html();

        templates[key] = Handlebars.compile(source);
    });

    exports.templates = templates;

    var views = {};

    var UserPageShareView = rave.View.extend({
        template: templates['user-search-view'],
        modal: $('#sharePageDialog'),
        container: $('#sharePageDialogContent'),
        models: {
            page: rave.models.page,
            users: rave.models.users
        },

        initialize: function(){
            this.constructor.__super__.initialize.apply(this);

            var page = this.models.page;
            var users = this.models.users;

            this.modal.on('show', function(){
                users.fetchPage(1);
            });

            //extend users toViewModel function to include share properties
            this.models.users.toViewModel = _.wrap(this.models.users.toViewModel, function(fn){
                var model = fn.apply(this);

                _.each(model.users, function(user){
                    user.isOwner = page.isUserOwner(user.id);
                    user.hasShare = page.isUserMember(user.id);
                    user.hasEdit = page.isUserEditor(user.id);
                });

                return model;
            });

            this.container.html(this.$el);
        },

        events: {
            'click #shareSearchButton': 'search',
            'keypress #searchTerm': 'search',
            'click #clearSearchButton': 'clearSearch',
            'click #pagingul a': 'page',
            'click .searchResultRecord a': 'shareAction'
        },
        search: function(e){
            if(e.which == 13 || _.isUndefined(e.which)) {
                var term = $('#searchTerm', this.$el).val();

                this.models.users.filter(term);
            }
        },
        clearSearch: function(e){
            this.models.users.filter();
        },
        page: function(e){
            var page = $(e.target).data('pagenumber');

            this.models.users.fetchPage(page);
        },
        shareAction: function(e){
            var userId = $(e.target).data('userid');
            var action = $(e.target).data('action');

            this.models.page[action](userId);
        }
    });

    views.pageShareView = new UserPageShareView();

    exports.views = views;


    return exports;
})()