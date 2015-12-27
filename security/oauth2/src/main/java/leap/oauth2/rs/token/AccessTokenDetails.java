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
package leap.oauth2.rs.token;

public interface AccessTokenDetails {

    /**
     * Returns the authenticated client id or <code>null</code> if user only.
     */
    String getClientId();
    
    /**
     * Returns the authenticated user id or <code>null</code> if client only.
     */
    String getUserId();
    
    /**
     * Returns the granted scope or <code>null</code>.
     */
    String getScope();
    
    /**
     * Returns <code>true</code> if the token was expired.
     */
    boolean isExpired();
    
}