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

import okhttp3.Request;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;

/**
 * {@link EnhanceRealCallInfo} storage the `Request` and `HttpMetric` and `AbstractSpan` for support
 * the EventListener methods.
 */
public class EnhanceRealCallInfo {
    private AbstractSpan span;
    private HttpMetric httpMetric;
    private Request request;

    public EnhanceRealCallInfo(Request request, HttpMetric httpMetric) {
        this.request = request;
        this.httpMetric = httpMetric;
    }

    public AbstractSpan getSpan() {
        return span;
    }

    public void setSpan(AbstractSpan span) {
        this.span = span;
    }

    public HttpMetric getHttpMetric() {
        return httpMetric;
    }

    public Request getRequest() {
        return request;
    }
}
