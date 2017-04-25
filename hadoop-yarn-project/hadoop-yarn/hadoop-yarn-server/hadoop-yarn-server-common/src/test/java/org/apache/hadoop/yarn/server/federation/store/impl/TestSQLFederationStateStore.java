begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.store.impl
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
name|federation
operator|.
name|store
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|FederationStateStore
import|;
end_import

begin_comment
comment|/**  * Unit tests for SQLFederationStateStore.  */
end_comment

begin_class
DECL|class|TestSQLFederationStateStore
specifier|public
class|class
name|TestSQLFederationStateStore
extends|extends
name|FederationStateStoreBaseTest
block|{
DECL|field|HSQLDB_DRIVER
specifier|private
specifier|static
specifier|final
name|String
name|HSQLDB_DRIVER
init|=
literal|"org.hsqldb.jdbc.JDBCDataSource"
decl_stmt|;
DECL|field|DATABASE_URL
specifier|private
specifier|static
specifier|final
name|String
name|DATABASE_URL
init|=
literal|"jdbc:hsqldb:mem:state"
decl_stmt|;
DECL|field|DATABASE_USERNAME
specifier|private
specifier|static
specifier|final
name|String
name|DATABASE_USERNAME
init|=
literal|"SA"
decl_stmt|;
DECL|field|DATABASE_PASSWORD
specifier|private
specifier|static
specifier|final
name|String
name|DATABASE_PASSWORD
init|=
literal|""
decl_stmt|;
annotation|@
name|Override
DECL|method|createStateStore ()
specifier|protected
name|FederationStateStore
name|createStateStore
parameter_list|()
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_STATESTORE_SQL_JDBC_CLASS
argument_list|,
name|HSQLDB_DRIVER
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_STATESTORE_SQL_USERNAME
argument_list|,
name|DATABASE_USERNAME
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_STATESTORE_SQL_PASSWORD
argument_list|,
name|DATABASE_PASSWORD
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_STATESTORE_SQL_URL
argument_list|,
name|DATABASE_URL
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
operator|new
name|HSQLDBFederationStateStore
argument_list|()
return|;
block|}
block|}
end_class

end_unit

