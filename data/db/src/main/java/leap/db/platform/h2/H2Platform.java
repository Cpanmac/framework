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
package leap.db.platform.h2;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import leap.db.DbPlatforms;
import leap.db.platform.GenericDbComparator;
import leap.db.platform.GenericDbDialect;
import leap.db.platform.GenericDbMetadataReader;
import leap.db.platform.GenericDbPlatform;

public class H2Platform extends GenericDbPlatform {
	
	public H2Platform(){
		super(DbPlatforms.H2,productNameEqualsIgnorecaseMatcher("H2"));
	}

	public H2Platform(String type){
		super(type,productNameEqualsIgnorecaseMatcher("H2"));
	}

	@Override
    protected GenericDbDialect createDialect(DatabaseMetaData jdbcMetadata) throws SQLException {
		return new H2Dialect();
    }

	@Override
    protected GenericDbMetadataReader createMetadataReader(DatabaseMetaData jdbcMetadata) throws SQLException {
		return new H2MetadataReader();
    }

	@Override
    protected GenericDbComparator createComparator(DatabaseMetaData jdbcMetadata) throws SQLException {
	    return new H2Comparator();
    }
}