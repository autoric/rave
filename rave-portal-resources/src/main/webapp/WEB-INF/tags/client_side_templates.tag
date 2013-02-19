<script type="text/handlebars" data-template-for="widget-preferences">
    <table>
        <tbody>
        {{#each userPrefs}}
        {{#if this.HIDDEN}}
        <input type="hidden" id="{{this.name}}" name="{{this.name}}" value="{{this.value}}">
        {{else}}
        <tr>
            <td class="widget-prefs-label {{#if this.required}}widget-prefs-label-required{{/if}}">
                {{#if this.required}}*{{/if}}{{this.displayName}}
            </td>
            {{#if this.STRING}}
            <td>
                <input type="text" id="{{this.name}}" name="{{this.name}}" value="{{this.value}}"
                       class="widget-prefs-input {{#if this.required}}widget-prefs-input-required{{/if}}">
            </td>
            {{/if}}
            {{#if this.BOOL}}
            <td>
                <input type="checkbox" id="{{this.name}}" name="{{this.name}}"
                       class="widget-prefs-input {{#if this.required}}widget-prefs-input-required{{/if}}"
                {{#if this.value}}checked="true"{{/if}}>
            </td>
            {{/if}}
            {{#if this.ENUM}}
            <td>
                <select id="{{this.name}}" name="{{this.name}}"
                        class="widget-prefs-input {{#if this.required}}widget-prefs-input-required{{/if}}">
                    {{#each this.orderedEnumValues}}
                    <option value="{{this.value}}"
                    {{#if this.selected}}selected{{/if}}>{{this.displayValue}}</option>
                    {{/each}}
                </select>
            </td>
            {{/if}}
            {{#if this.LIST}}
            <td>
                <textarea id="{{this.name}}" name="{{this.name}}" rows="5" cols="12"
                          class="widget-prefs-input {{#if this.required}}widget-prefs-input-required{{/if}}">
                    {{this.value}}
                </textarea>
            </td>
            {{/if}}
        </tr>
        {{/if}}
        {{/each}}
        {{#if hasRequiredUserPrefs}}
        <tr>
            <td colspan='2' class='widget-prefs-required-text'>{{getClientMessage "widget.prefs.required.title"}}</td>
        </tr>
        {{/if}}
        <tr>
            <td colspan='2'>
                <button type="button" class="prefs-save-button">{{getClientMessage "common.save"}}
                </button>
                <button type="button" class="prefs-cancel-button">{{getClientMessage "common.cancel"}}
                </button>
            </td>
        </tr>
        </tbody>
    </table>
</script>

<script type="text/handlebars" data-template-for="popup-slideout">
    <div class="popup slideout">
        <div class="slideout-content">
        </div>
    </div>
</script>

<script type="text/handlebars" data-template-for="popup-dialog">
    <div class="popup dialog modal fade">
        <a href="#" class="close" data-dismiss="modal">&times;</a>

        <div class="modal-body"></div>
    </div>
</script>

<script type="text/handlebars" data-template-for="popup-modal">
    <div class="popup modal_dialog modal fade">
        <a href="#" class="close" data-dismiss="modal">&times;</a>

        <div class="modal-body"></div>
    </div>
</script>

<script type="text/handlebars" data-template-for="users-of-widget">
    <div class="dialog widget-users-dialog" title="{{widgetName}} {{getClientMessage " widget.users.added_by
    "}}">
    <ul class="widget-users">
        {{#each users}}
        <li class="widget-user">{{this.name}}</li>
        {{/each}}
    </ul>
    </div>
</script>

<script type="text/handlebars" data-template-for="info-message">
    <div class="alert alert-success navbar-spacer">
        {{message}}
    </div>
</script>

<script type="text/handlebars" data-template-for="user-search-pagination">
    <div class="pagination">
        <ul id="pagingul">
            {{#if prevLink.show}}
            <li><a href="#" data-pagenumber="{{prevLink.pageNumber}}">&lt;</a></li>
            {{/if}}
            {{#each pages}}
            <li class="{{#if this.current}}active{{/if}}"><a href="#" data-pagenumber="{{this.pageNumber}}">{{this.pageNumber}}</a>
            </li>
            {{/each}}
            {{#if nextLink.show}}
            <li><a href="#" data-pagenumber="{{nextLink.pageNumber}}">&gt;</a></li>
            {{/if}}
        </ul>
    </div>
</script>


<script type="text/handlebars" data-template-for="user-search-results">
    <table class="searchdialogcontent">
        <thead>
        <tr>
            <td class="textcell">
                <b>{{getClientMessage "common.username"}}</b>
            </td>
            <td class="booleancell">
                {{getClientMessage "common.username"}}
            </td>
            <td class="booleancell">
                {{getClientMessage "common.editing.auth"}}
            </td>
            <td class="booleancell">
                {{getClientMessage "page.clone.dialog.title"}}
            </td>
        </tr>
        </thead>
        <tbody>
        {{#each users}}
        <tr class="searchResultRecord">
            <td>{{this.username}}</td>
            {{#if this.isOwner}}
            <td></td>
            <td></td>
            <td></td>
            {{else}}
            <td class="shareButtonHolder">
                {{#if this.hasShare}}
                <a href="#" data-userid="{{this.id}}" data-username="{{this.username}}"
                   data-action="removeMember">
                    {{getClientMessage "common.remove"}}
                </a>
                {{else}}
                <a href="#" data-userid="{{this.id}}" data-username="{{this.username}}"
                   data-action="addMember">
                    {{getClientMessage "common.add"}}
                </a>
                {{/if}}
            </td>
            <td class="pageEditorStatusHolder">
                {{#if this.hasShare}}
                {{#if this.hasEdit}}
                <a href="#" data-userid="{{this.id}}" data-username="{{this.username}}"
                   data-action="removeEditor">
                    {{getClientMessage "common.remove"}}
                </a>
                {{else}}
                <a href="#" data-userid="{{this.id}}" data-username="{{this.username}}"
                   data-action="addEditor">
                    {{getClientMessage "common.add"}}
                </a>
                {{/if}}
                {{/if}}
            </td>
            <td class="cloneButtonHolder">
                <a href="#" data-userid="{{this.id}}" data-username="{{this.username}}"
                   data-action="clonePageForUser">
                    {{getClientMessage "page.clone.dialog.detail"}}
                </a>
            </td>
            {{/if}}
        </tr>
        {{/each}}
        </tbody>
    </table>
</script>

<script type="text/handlebars" data-template-for="user-search-view">
    <div id="shareContent">
        <div id="searchControls"><input id="searchTerm" name="searchTerm" type="text" value="{{users.searchTerm}}"/>
            {{!TODO: these buttons should be using client messages}}
            <input id="shareSearchButton" value="Search" type="submit"/>
            <input id="clearSearchButton" value="Clear" type="submit" class="{{#unless users.searchTerm}}hide{{/unless}}"/>
        </div>
        <div id="shareSearchListHeader">
            {{#if users.pagination.total}}
            Showing {{users.pagination.start}} - {{users.pagination.finish}} of {{users.pagination.total}} results
            {{else}}
            No results found
            {{/if}}
        </div>
        <div id="shareSearchListPaging">
            <div class="pagination">
                {{#with users.pagination}}
                <ul id="pagingul">
                    {{#if prevLink.show}}
                    <li><a href="#" data-pagenumber="{{prevLink.pageNumber}}">&lt;</a></li>
                    {{/if}}
                    {{#each pages}}
                    <li class="{{#if this.current}}active{{/if}}"><a href="#" data-pagenumber="{{this.pageNumber}}">{{this.pageNumber}}</a>
                    </li>
                    {{/each}}
                    {{#if nextLink.show}}
                    <li><a href="#" data-pagenumber="{{nextLink.pageNumber}}">&gt;</a></li>
                    {{/if}}
                </ul>
                {{/with}}
            </div>
        </div>
        <div id="shareSearchResults">
            <table class="searchdialogcontent">
                <thead>
                <tr>
                    <td class="textcell">
                        <b>{{getClientMessage "common.username"}}</b>
                    </td>
                    <td class="booleancell">
                        {{getClientMessage "common.username"}}
                    </td>
                    <td class="booleancell">
                        {{getClientMessage "common.editing.auth"}}
                    </td>
                    <td class="booleancell">
                        {{getClientMessage "page.clone.dialog.title"}}
                    </td>
                </tr>
                </thead>
                <tbody>
                {{#each users.users}}
                <tr class="searchResultRecord">
                    <td>{{this.username}}</td>
                    {{#if this.isOwner}}
                    <td></td>
                    <td></td>
                    <td></td>
                    {{else}}
                    <td class="shareButtonHolder">
                        {{#if this.hasShare}}
                        <a href="#" data-userid="{{this.id}}" data-username="{{this.username}}"
                           data-action="removeMember">
                            {{getClientMessage "common.remove"}}
                        </a>
                        {{else}}
                        <a href="#" data-userid="{{this.id}}" data-username="{{this.username}}"
                           data-action="addMember">
                            {{getClientMessage "common.add"}}
                        </a>
                        {{/if}}
                    </td>
                    <td class="pageEditorStatusHolder">
                        {{#if this.hasShare}}
                        {{#if this.hasEdit}}
                        <a href="#" data-userid="{{this.id}}" data-username="{{this.username}}"
                           data-action="removeEditor">
                            {{getClientMessage "common.remove"}}
                        </a>
                        {{else}}
                        <a href="#" data-userid="{{this.id}}" data-username="{{this.username}}"
                           data-action="addEditor">
                            {{getClientMessage "common.add"}}
                        </a>
                        {{/if}}
                        {{/if}}
                    </td>
                    <td class="cloneButtonHolder">
                        <a href="#" data-userid="{{this.id}}" data-username="{{this.username}}"
                           data-action="clonePageForUser">
                            {{getClientMessage "page.clone.dialog.detail"}}
                        </a>
                    </td>
                    {{/if}}
                </tr>
                {{/each}}
                </tbody>
            </table>
        </div>
    </div>
</script>


