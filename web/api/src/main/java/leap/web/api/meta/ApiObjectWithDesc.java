/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package leap.web.api.meta;

import java.util.Map;

import leap.lang.Described;

public abstract class ApiObjectWithDesc extends ApiObject implements Described {
	
	protected final String summary;
	protected final String description;
	
	public ApiObjectWithDesc() {
	    this(null, null, null);
    }
	
	public ApiObjectWithDesc(String summary, String description) {
	    this(summary, description, null);
    }
	
	public ApiObjectWithDesc(String summary, String description, Map<String, Object> attrs) {
	    super(attrs);
	    
	    this.summary = summary;
	    this.description = description;
    }

	@Override
    public String getSummary() {
	    return summary;
    }

	@Override
    public String getDescription() {
	    return description;
    }

}