begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.db
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|db
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|PreparedStatement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * A RecordReader that reads records from a MySQL table via DataDrivenDBRecordReader  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|MySQLDataDrivenDBRecordReader
specifier|public
class|class
name|MySQLDataDrivenDBRecordReader
parameter_list|<
name|T
extends|extends
name|DBWritable
parameter_list|>
extends|extends
name|DataDrivenDBRecordReader
argument_list|<
name|T
argument_list|>
block|{
DECL|method|MySQLDataDrivenDBRecordReader (DBInputFormat.DBInputSplit split, Class<T> inputClass, Configuration conf, Connection conn, DBConfiguration dbConfig, String cond, String [] fields, String table)
specifier|public
name|MySQLDataDrivenDBRecordReader
parameter_list|(
name|DBInputFormat
operator|.
name|DBInputSplit
name|split
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|inputClass
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|Connection
name|conn
parameter_list|,
name|DBConfiguration
name|dbConfig
parameter_list|,
name|String
name|cond
parameter_list|,
name|String
index|[]
name|fields
parameter_list|,
name|String
name|table
parameter_list|)
throws|throws
name|SQLException
block|{
name|super
argument_list|(
name|split
argument_list|,
name|inputClass
argument_list|,
name|conf
argument_list|,
name|conn
argument_list|,
name|dbConfig
argument_list|,
name|cond
argument_list|,
name|fields
argument_list|,
name|table
argument_list|,
literal|"MYSQL"
argument_list|)
expr_stmt|;
block|}
comment|// Execute statements for mysql in unbuffered mode.
DECL|method|executeQuery (String query)
specifier|protected
name|ResultSet
name|executeQuery
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|SQLException
block|{
name|statement
operator|=
name|getConnection
argument_list|()
operator|.
name|prepareStatement
argument_list|(
name|query
argument_list|,
name|ResultSet
operator|.
name|TYPE_FORWARD_ONLY
argument_list|,
name|ResultSet
operator|.
name|CONCUR_READ_ONLY
argument_list|)
expr_stmt|;
name|statement
operator|.
name|setFetchSize
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
comment|// MySQL: read row-at-a-time.
return|return
name|statement
operator|.
name|executeQuery
argument_list|()
return|;
block|}
block|}
end_class

end_unit

