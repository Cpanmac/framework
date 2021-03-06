/*
 * Copyright 2014 the original author or authors.
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
package leap.web.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import leap.core.annotation.Inject;
import leap.core.annotation.M;
import leap.lang.Types;
import leap.lang.convert.Converts;
import leap.lang.http.ContentTypes;
import leap.lang.http.MimeTypes;
import leap.web.Content;
import leap.web.Contents;
import leap.web.Request;
import leap.web.Response;

public class TextFormat extends AbstractResponseFormat {
	
	protected @Inject @M JsonFormatWriter jsonWriter;
	
	public TextFormat() {
		super(MimeTypes.TEXT_PLAIN_TYPE);
	}
	
	@Override
    public Content getContent(Class<?> type, Type genericType, Annotation[] annotations, Object value) throws Exception {
		if(null == value){
			return Contents.text("");
		}else if(Types.isSimpleType(type,genericType)){
			return Contents.text(Converts.toString(value));
		}else{
			return new Content() {
				@Override
				public void render(Request request, Response response) throws Throwable {
					jsonWriter.write(response.getWriter(), type, genericType, annotations, value);
				}
				
				@Override
				public String getContentType(Request request) throws Throwable {
					return ContentTypes.TEXT_PLAIN_UTF8;
				}
			};
					
		}
    }

}