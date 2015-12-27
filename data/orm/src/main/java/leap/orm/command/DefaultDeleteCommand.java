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
package leap.orm.command;

import leap.lang.params.Params;
import leap.orm.dao.Dao;
import leap.orm.mapping.EntityMapping;
import leap.orm.sql.SqlCommand;

public class DefaultDeleteCommand extends AbstractEntityDaoCommand implements DeleteCommand {
	
	protected final Object		  id;
	protected final Params    idParameter;
	protected final SqlCommand    sqlCommand;
	
	public DefaultDeleteCommand(Dao dao,EntityMapping em,Object id) {
	    super(dao,em);
	    
	    this.sqlCommand  = metadata.getSqlCommand(em.getEntityName(), SqlCommand.DELETE_COMMAND_NAME);
	    this.id 		 = id;
    	this.idParameter = context.getParameterStrategy().createIdParameters(context, em, id);
    }

	@Override
	public int execute() {
		return sqlCommand.executeUpdate(this, idParameter);
	}

}