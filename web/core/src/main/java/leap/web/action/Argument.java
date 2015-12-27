/*
 * Copyright 2013 the original author or authors.
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
package leap.web.action;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import leap.core.validation.Validator;
import leap.lang.Args;
import leap.lang.Arrays2;
import leap.lang.Classes;
import leap.lang.Named;
import leap.lang.Strings;
import leap.lang.TypeInfo;
import leap.web.annotation.ViewAttribute;
import leap.web.view.ViewData;

public class Argument implements Named {
	
	public static enum BindingFrom {
		UNDEFINED,
		
		PATH_PARAM,
		
		REQUEST_PARAM,
		
		QUERY_PARAM,
		
		PART_PARAM,
		
		REQUEST_BODY;
	}
	
	protected final String       name;
	protected final Class<?>     type;
	protected final Type         genericType;
	protected final TypeInfo	 typeInfo;
	protected final Boolean		 required;
	protected final BindingFrom  bindingFrom;
	protected final Annotation[] annotations;
	protected final Validator[]  validators;
	protected final String		 viewAttributeName;
	
	public Argument(String name, 
					Class<?> type, 
					Type genericType,
					TypeInfo typeInfo,
					Boolean	required,
					BindingFrom bindingFrom,
					Annotation[] annotations,
					Validator[] validators) {
		
		Args.notEmpty(name,   "name");
		Args.notNull(type,    "type");
		Args.notNull(typeInfo,"type info");
		
		this.name              = name;
		this.type              = type;
		this.genericType       = genericType;
		this.typeInfo	       = typeInfo;
		this.required		   = required;
		this.bindingFrom       = null == bindingFrom ? BindingFrom.UNDEFINED : bindingFrom;
		this.annotations       = null == annotations ? Classes.EMPTY_ANNOTATION_ARRAY : annotations;
		this.validators        = null == validators ? (Validator[])Arrays2.EMPTY_OBJECT_ARRAY : validators;
		this.viewAttributeName = resolveViewAttributeName();
	}

	@Override
    public String getName() {
	    return name;
    }

	/**
	 * Returns the type of this {@link Argument}.
	 */
	public Class<?> getType() {
		return type;
	}
	
	/**
	 * Returns the generic type of this argument.
	 * 
	 * <p>
	 * Returns <code>null</code> if no generic type.
	 */
	public Type getGenericType() {
		return genericType;
	}
	
	/**
	 * Returns the type info of this argument.
	 */
	public TypeInfo getTypeInfo() {
		return typeInfo;
	}
	
	/**
	 * Returns <code>true</code> or <code>false</code> if this argument is required or not explicitly declared.
	 * 
	 * <p>
	 * Returns <code>null</code> means that the 'required' validation not declared in this argument.
	 */
	public Boolean getRequired() {
		return required;
	}

	/**
	 * Returns the {@link BindingFrom} of this argument.
	 */
	public BindingFrom getBindingFrom() {
		return bindingFrom;
	}

	/**
	 * Returns the {@link ElementType#PARAMETER} annotations in the {@link Method} of this argument.
	 * 
	 * <p>
	 * Returns <code>{@link Annotation}[]</code> if no these annotations.
	 */
	public Annotation[] getAnnotations() {
		return annotations;
	}
	
	/**
	 * Returns the {@link Validator} arrays.
	 */
	public Validator[] getValidators() {
		return validators;
	}
	
	/**
	 * Returns the name for exposing the argument's value to {@link ViewData}.
	 */
	public String getViewAttributeName() {
		return viewAttributeName;
	}

	private String resolveViewAttributeName() {
		String   n = name;
		ViewAttribute a = Classes.getAnnotation(annotations, ViewAttribute.class);
		if(null != a && !Strings.isEmpty(a.value())){
			n = a.value();
		}
		return n;
	}
}