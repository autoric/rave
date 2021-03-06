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

package org.apache.rave.portal.web.renderer.impl;

import org.apache.rave.exception.NotSupportedException;
import org.apache.rave.portal.web.renderer.model.RenderContext;
import org.apache.rave.portal.web.renderer.RenderScope;
import org.apache.rave.portal.web.renderer.ScriptLocation;
import org.apache.rave.portal.web.renderer.ScriptManager;
import org.apache.rave.synchronization.annotation.Synchronized;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Default implementation of a script manager
 * <p />
 * Stores all global scripts locally and any request scripts within the RenderContext
 */
@Service
public class DefaultScriptManager implements ScriptManager{
    private static final String KEY = "KEY";
    private final Map<ScriptLocation, List<String>> scriptRenderers;

    public DefaultScriptManager() {
        this.scriptRenderers = new HashMap<ScriptLocation, List<String>>();
    }

    @Override
    public List<String> getScriptBlocks(ScriptLocation location, RenderContext context) {
        List<String> combined = new LinkedList<String>();
        getAndAdd(location, combined, scriptRenderers);
        getAndAdd(location, combined, getOrCreateScriptMap(context));
        return combined;
    }

    @Override
    public void registerScriptBlock(String script, ScriptLocation location) {
        addScriptToMap(script, location, scriptRenderers);
    }

    @Override
    public void registerScriptBlock(String script, ScriptLocation location, RenderScope scope, RenderContext context) {
        switch(scope) {
            case GLOBAL:
                registerScriptBlock(script, location);
                break;
            case CURRENT_REQUEST:
                addScriptToContext(script, location, context);
                break;
            default:
                throw new NotSupportedException("The scope, " + scope + " , is not supported by the script manager");
        }
    }

    private static void getAndAdd(ScriptLocation location, List<String> output, Map<ScriptLocation, List<String>> map) {
        List<String> scripts = map.get(location);
        if(scripts != null) {
            output.addAll(scripts);
        }
    }

    private static void addScriptToContext(String script, ScriptLocation location, RenderContext context) {
        Map<ScriptLocation, List<String>> scriptMap = getOrCreateScriptMap(context);
        addScriptToMap(script, location, scriptMap);
    }


    private static void addScriptToMap(String script, ScriptLocation location, Map<ScriptLocation, List<String>> scripts) {
        if (!scripts.containsKey(location)) {
            addListForLocation(location, scripts);
        }
        scripts.get(location).add(script);
    }

    //Lock on the instance of hte map to ensure that only one put per location takes place
    @Synchronized(discriminator = "'SM_#map.toString()'", id = "#location")
    private static void addListForLocation(ScriptLocation location, Map<ScriptLocation, List<String>> map) {
        if (!map.containsKey(location)) {
            map.put(location, new ArrayList<String>());
        }
    }
    @SuppressWarnings("unchecked")
    private static Map<ScriptLocation, List<String>> getOrCreateScriptMap(RenderContext context) {
        if(context == null) {
            throw new IllegalArgumentException("Cannot have a null render context");
        }
        Map<ScriptLocation, List<String>> scriptMap = (Map<ScriptLocation, List<String>>)context.getProperties().get(KEY);
        if(scriptMap == null) {
            scriptMap = new HashMap<ScriptLocation, List<String>>();
            context.getProperties().put(KEY, scriptMap);
        }
        return scriptMap;
    }
}
