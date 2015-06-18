begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hbase
operator|.
name|TableName
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
name|hbase
operator|.
name|client
operator|.
name|Admin
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
name|hbase
operator|.
name|client
operator|.
name|BufferedMutator
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
name|hbase
operator|.
name|client
operator|.
name|Connection
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
name|hbase
operator|.
name|client
operator|.
name|ResultScanner
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
name|hbase
operator|.
name|client
operator|.
name|Scan
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
name|hbase
operator|.
name|client
operator|.
name|Table
import|;
end_import

begin_comment
comment|/**  * Implements behavior common to tables used in the timeline service storage.  *  * @param<T> reference to the table instance class itself for type safety.  */
end_comment

begin_class
DECL|class|BaseTable
specifier|public
specifier|abstract
class|class
name|BaseTable
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Name of config variable that is used to point to this table    */
DECL|field|tableNameConfName
specifier|private
specifier|final
name|String
name|tableNameConfName
decl_stmt|;
comment|/**    * Unless the configuration overrides, this will be the default name for the    * table when it is created.    */
DECL|field|defaultTableName
specifier|private
specifier|final
name|String
name|defaultTableName
decl_stmt|;
comment|/**    * @param tableNameConfName name of config variable that is used to point to    *          this table.    */
DECL|method|BaseTable (String tableNameConfName, String defaultTableName)
specifier|protected
name|BaseTable
parameter_list|(
name|String
name|tableNameConfName
parameter_list|,
name|String
name|defaultTableName
parameter_list|)
block|{
name|this
operator|.
name|tableNameConfName
operator|=
name|tableNameConfName
expr_stmt|;
name|this
operator|.
name|defaultTableName
operator|=
name|defaultTableName
expr_stmt|;
block|}
comment|/**    * Used to create a type-safe mutator for this table.    *    * @param hbaseConf used to read table name    * @param conn used to create a table from.    * @return a type safe {@link BufferedMutator} for the entity table.    * @throws IOException    */
DECL|method|getTableMutator (Configuration hbaseConf, Connection conn)
specifier|public
name|TypedBufferedMutator
argument_list|<
name|T
argument_list|>
name|getTableMutator
parameter_list|(
name|Configuration
name|hbaseConf
parameter_list|,
name|Connection
name|conn
parameter_list|)
throws|throws
name|IOException
block|{
name|TableName
name|tableName
init|=
name|this
operator|.
name|getTableName
argument_list|(
name|hbaseConf
argument_list|)
decl_stmt|;
comment|// Plain buffered mutator
name|BufferedMutator
name|bufferedMutator
init|=
name|conn
operator|.
name|getBufferedMutator
argument_list|(
name|tableName
argument_list|)
decl_stmt|;
comment|// Now make this thing type safe.
comment|// This is how service initialization should hang on to this variable, with
comment|// the proper type
name|TypedBufferedMutator
argument_list|<
name|T
argument_list|>
name|table
init|=
operator|new
name|BufferedMutatorDelegator
argument_list|<
name|T
argument_list|>
argument_list|(
name|bufferedMutator
argument_list|)
decl_stmt|;
return|return
name|table
return|;
block|}
comment|/**    * @param hbaseConf used to read settings that override defaults    * @param conn used to create table from    * @param scan that specifies what you want to read from this table.    * @return scanner for the table.    * @throws IOException    */
DECL|method|getResultScanner (Configuration hbaseConf, Connection conn, Scan scan)
specifier|public
name|ResultScanner
name|getResultScanner
parameter_list|(
name|Configuration
name|hbaseConf
parameter_list|,
name|Connection
name|conn
parameter_list|,
name|Scan
name|scan
parameter_list|)
throws|throws
name|IOException
block|{
name|Table
name|table
init|=
name|conn
operator|.
name|getTable
argument_list|(
name|getTableName
argument_list|(
name|hbaseConf
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|table
operator|.
name|getScanner
argument_list|(
name|scan
argument_list|)
return|;
block|}
comment|/**    * Get the table name for this table.    *    * @param hbaseConf    */
DECL|method|getTableName (Configuration hbaseConf)
specifier|public
name|TableName
name|getTableName
parameter_list|(
name|Configuration
name|hbaseConf
parameter_list|)
block|{
name|TableName
name|table
init|=
name|TableName
operator|.
name|valueOf
argument_list|(
name|hbaseConf
operator|.
name|get
argument_list|(
name|tableNameConfName
argument_list|,
name|defaultTableName
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|table
return|;
block|}
comment|/**    * Used to create the table in HBase. Should be called only once (per HBase    * instance).    *    * @param admin    * @param hbaseConf    */
DECL|method|createTable (Admin admin, Configuration hbaseConf)
specifier|public
specifier|abstract
name|void
name|createTable
parameter_list|(
name|Admin
name|admin
parameter_list|,
name|Configuration
name|hbaseConf
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

