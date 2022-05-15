/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.apm.plugin.okhttp.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.tag.Tags;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;

/**
 * {@link EventListenerInterceptor} intercept the synchronous http calls by the discovery of okhttp.
 */
public class EventListenerInterceptor implements InstanceMethodsAroundInterceptor, InstanceConstructorInterceptor {
    public static class EventListenerModel {
        public Long startTime;

        public List<String> durations;

        public EventListenerModel() {
            durations = new ArrayList<>();
            init();
        }

        public void record(String method) {
            long duration = System.currentTimeMillis() - startTime;
            durations.add(method + "=" + duration);
        }

        public void init() {
            durations.clear();
            startTime = System.currentTimeMillis();
            durations.add("start=" + startTime);
        }

        @Override
        public String toString() {
            return String.join("\n", durations);
        }
    }

    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) {
    }

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                             MethodInterceptResult result) {
        String methodName = method.getName();
        EventListenerModel model = (EventListenerModel) objInst.getSkyWalkingDynamicField();
        if (model == null) {
            model = new EventListenerModel();
            objInst.setSkyWalkingDynamicField(model);
        } else if (methodName.equals("callStart") || methodName.equals("fetchStart")) {
            model.init();
        }

        model.record(methodName);

        if (methodName.equals("callEnd")
                || methodName.equals("callFailed")
                || methodName.equals("fetchEnd")) {
            AbstractSpan span = ContextManager.activeSpan();
            Tags.HTTP.DURATIONS.set(span, model.toString());
        }
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                              Object ret) {
        return ret;
    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments,
                                      Class<?>[] argumentsTypes, Throwable t) {
        ContextManager.activeSpan().log(t);
    }
}
