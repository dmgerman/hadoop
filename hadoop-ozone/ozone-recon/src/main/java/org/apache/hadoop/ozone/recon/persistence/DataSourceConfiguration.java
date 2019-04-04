begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.ozone.recon.persistence
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
operator|.
name|persistence
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Common configuration needed to instantiate {@link javax.sql.DataSource}.  */
end_comment

begin_interface
DECL|interface|DataSourceConfiguration
specifier|public
interface|interface
name|DataSourceConfiguration
block|{
comment|/**    * Get database driver class name available on the classpath.    */
DECL|method|getDriverClass ()
name|String
name|getDriverClass
parameter_list|()
function_decl|;
comment|/**    * Get Jdbc Url for the database server.    */
DECL|method|getJdbcUrl ()
name|String
name|getJdbcUrl
parameter_list|()
function_decl|;
comment|/**    * Get username for the db.    */
DECL|method|getUserName ()
name|String
name|getUserName
parameter_list|()
function_decl|;
comment|/**    * Get password for the db.    */
DECL|method|getPassword ()
name|String
name|getPassword
parameter_list|()
function_decl|;
comment|/**    * Should autocommit be turned on for the datasource.    */
DECL|method|setAutoCommit ()
name|boolean
name|setAutoCommit
parameter_list|()
function_decl|;
comment|/**    * Sets the maximum time (in milliseconds) to wait before a call to    * getConnection is timed out.    */
DECL|method|getConnectionTimeout ()
name|long
name|getConnectionTimeout
parameter_list|()
function_decl|;
comment|/**    * Get a string representation of {@link org.jooq.SQLDialect}.    */
DECL|method|getSqlDialect ()
name|String
name|getSqlDialect
parameter_list|()
function_decl|;
comment|/**    * In a production database this should be set to something like 10.    * SQLite does not allow multiple connections, hence this defaults to 1.    */
DECL|method|getMaxActiveConnections ()
name|Integer
name|getMaxActiveConnections
parameter_list|()
function_decl|;
comment|/**    * Sets the maximum connection age (in seconds).    */
DECL|method|getMaxConnectionAge ()
name|Integer
name|getMaxConnectionAge
parameter_list|()
function_decl|;
comment|/**    * Sets the maximum idle connection age (in seconds).    */
DECL|method|getMaxIdleConnectionAge ()
name|Integer
name|getMaxIdleConnectionAge
parameter_list|()
function_decl|;
comment|/**    * Statement specific to database, usually SELECT 1.    */
DECL|method|getConnectionTestStatement ()
name|String
name|getConnectionTestStatement
parameter_list|()
function_decl|;
comment|/**    * How often to test idle connections for being active (in seconds).    */
DECL|method|getIdleConnectionTestPeriod ()
name|Integer
name|getIdleConnectionTestPeriod
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

