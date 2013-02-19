var rave = rave || {};

rave.models = (function () {

    var User = rave.Model.extend({
        acceptShare: function () {

        },
        declineShare: function () {

        }
    });

    var Users = rave.Collection.extend({
        model: User,
        pageSize: 10,
        initialize: function(){
            _.bindAll(this, 'parse');
        },
        filter: function (term) {
            this.searchTerm = term;

            if (this.searchTerm) {
                rave.api.rpc.searchUsers({searchTerm: this.searchTerm, offset: 0, successCallback: this.parse });
            }
            else {
                rave.api.rpc.getUsers({offset: 0, successCallback: this.parse });
            }
        },
        fetchPage: function (page) {
            var self = this;

            var offset = page?(page-1):0;
            offset *= this.pageSize;

            if (this.searchTerm) {
                rave.api.rpc.searchUsers({searchTerm: this.searchTerm, offset: offset, successCallback: this.parse });
            }
            else {
                rave.api.rpc.getUsers({offset: offset, successCallback: this.parse });
            }
        },
        parse: function (data) {
            var result = data.result;
            this.pageSize = result.pageSize || 10;

            this.paginationData = {
                start: result.offset + 1,
                finish: result.resultSet.length + result.offset,
                total: result.totalResults,
                pageSize: result.pageSize,
                prevLink: {
                    show: result.currentPage > 1 ? true : false,
                    pageNumber: result.currentPage - 1
                },
                nextLink: {
                    show: result.currentPage < result.numberOfPages ? true : false,
                    pageNumber: result.currentPage + 1
                },
                //pages will be an array of objects from 1 to number of pages
                pages: _.map(_.range(1, result.numberOfPages + 1), function (pageNumber) {
                    return {
                        pageNumber: pageNumber,
                        current: pageNumber == result.currentPage
                    }
                })
            }

            this.reset(result.resultSet);
        },
        toViewModel: function () {
            return {
                searchTerm: this.searchTerm,
                pagination: this.paginationData,
                users: this.constructor.__super__.toViewModel.apply(this)
            }
        }
    });

    var Page = rave.Model.extend({
        defaults: {
            id: -1,
            ownerId: -1,
            members: {}
        },
        addInitData: function (userId, isEditor) {
            var members = this.get('members');

            members[userId] = {
                userId: userId,
                editor: isEditor
            }

            this.set('members', members, {silent:true});
        },

        isUserOwner: function (userId) {
            return userId == this.get('ownerId');
        },

        isUserMember: function (userId) {
            return this.get('members')[userId] ? true : false;
        },

        isUserEditor: function (userId) {
            var member = this.get('members')[userId];
            return member && member.editor;
        },

        addMember: function (userId) {
            var self = this;
            rave.api.rpc.addMemberToPage({pageId: self.id, userId: userId,
                successCallback: function (result) {
                    var members = self.get('members');

                    members[userId] = {
                        userId: userId,
                        editor: false
                    }

                    self.set('members', members);
                }
            });

        },
        removeMember: function (userId) {
            var self = this;
            //removeMemberFromPage
            rave.api.rpc.removeMemberFromPage({pageId: self.id, userId: userId,
                successCallback: function (result) {
                    var members = self.get('members');

                    delete members[userId];

                    self.set('members', members);
                }
            });
        },
        addEditor: function (userId) {
            var self = this;
            //updatePageEditingStatus
            rave.api.rpc.updatePageEditingStatus({pageId: self.id, userId: userId, isEditor: true,
                successCallback: function () {
                    var members = self.get('members');

                    members[userId] = {
                        userId: userId,
                        editor: true
                    }

                    self.set('members', members);
                }
            });
        },
        removeEditor: function (userId) {
            var self = this;
            rave.api.rpc.updatePageEditingStatus({pageId: self.id, userId: userId, isEditor: false,
                successCallback: function () {

                    var members = self.get('members');

                    members[userId] = {
                        userId: userId,
                        editor: false
                    }

                    self.set('members', members);
                }
            });
        },
        cloneForUser: function (userId, pageName) {
            rave.api.rpc.updatePageEditingStatus({pageId: this.id, userId: userId, pageName: pageName,
                successCallback: function () {

                }
            });
        }
    });

    return {
        page: new Page(),
        users: new Users()
    }

})();


