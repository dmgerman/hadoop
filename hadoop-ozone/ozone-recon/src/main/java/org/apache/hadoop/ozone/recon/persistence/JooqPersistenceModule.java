begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|matcher
operator|.
name|Matchers
operator|.
name|annotatedWith
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|matcher
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

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
name|javax
operator|.
name|sql
operator|.
name|DataSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jooq
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jooq
operator|.
name|ConnectionProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jooq
operator|.
name|SQLDialect
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jooq
operator|.
name|impl
operator|.
name|DefaultConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|dao
operator|.
name|DataAccessException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|jdbc
operator|.
name|datasource
operator|.
name|DataSourceTransactionManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|jdbc
operator|.
name|datasource
operator|.
name|DataSourceUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|jdbc
operator|.
name|datasource
operator|.
name|TransactionAwareDataSourceProxy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|transaction
operator|.
name|annotation
operator|.
name|Transactional
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|AbstractModule
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Provider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Provides
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Singleton
import|;
end_import

begin_comment
comment|/**  * Persistence module that provides binding for {@link DataSource} and  * a MethodInterceptor for nested transactions support.  */
end_comment

begin_class
DECL|class|JooqPersistenceModule
specifier|public
class|class
name|JooqPersistenceModule
extends|extends
name|AbstractModule
block|{
DECL|field|configurationProvider
specifier|private
name|Provider
argument_list|<
name|DataSourceConfiguration
argument_list|>
name|configurationProvider
decl_stmt|;
DECL|field|DEFAULT_DIALECT
specifier|public
specifier|static
specifier|final
name|SQLDialect
name|DEFAULT_DIALECT
init|=
name|SQLDialect
operator|.
name|SQLITE
decl_stmt|;
DECL|method|JooqPersistenceModule ( Provider<DataSourceConfiguration> configurationProvider)
specifier|public
name|JooqPersistenceModule
parameter_list|(
name|Provider
argument_list|<
name|DataSourceConfiguration
argument_list|>
name|configurationProvider
parameter_list|)
block|{
name|this
operator|.
name|configurationProvider
operator|=
name|configurationProvider
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|DataSourceConfiguration
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|configurationProvider
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|DataSource
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|DefaultDataSourceProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|Singleton
operator|.
name|class
argument_list|)
expr_stmt|;
name|TransactionalMethodInterceptor
name|interceptor
init|=
operator|new
name|TransactionalMethodInterceptor
argument_list|(
name|getProvider
argument_list|(
name|DataSourceTransactionManager
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|bindInterceptor
argument_list|(
name|annotatedWith
argument_list|(
name|Transactional
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|()
argument_list|,
name|interceptor
argument_list|)
expr_stmt|;
name|bindInterceptor
argument_list|(
name|any
argument_list|()
argument_list|,
name|annotatedWith
argument_list|(
name|Transactional
operator|.
name|class
argument_list|)
argument_list|,
name|interceptor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Provides
annotation|@
name|Singleton
DECL|method|getConfiguration (DefaultDataSourceProvider provider)
name|Configuration
name|getConfiguration
parameter_list|(
name|DefaultDataSourceProvider
name|provider
parameter_list|)
block|{
name|DataSource
name|dataSource
init|=
name|provider
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
operator|new
name|DefaultConfiguration
argument_list|()
operator|.
name|set
argument_list|(
name|dataSource
argument_list|)
operator|.
name|set
argument_list|(
operator|new
name|SpringConnectionProvider
argument_list|(
name|dataSource
argument_list|)
argument_list|)
operator|.
name|set
argument_list|(
name|SQLDialect
operator|.
name|valueOf
argument_list|(
name|configurationProvider
operator|.
name|get
argument_list|()
operator|.
name|getSqlDialect
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Provides
annotation|@
name|Singleton
DECL|method|provideDataSourceTransactionManager ( DataSource dataSource)
name|DataSourceTransactionManager
name|provideDataSourceTransactionManager
parameter_list|(
name|DataSource
name|dataSource
parameter_list|)
block|{
return|return
operator|new
name|DataSourceTransactionManager
argument_list|(
operator|new
name|TransactionAwareDataSourceProxy
argument_list|(
name|dataSource
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * This connection provider uses Spring to extract the    * {@link TransactionAwareDataSourceProxy} from our BoneCP pooled connection    * {@link DataSource}.    */
DECL|class|SpringConnectionProvider
specifier|static
class|class
name|SpringConnectionProvider
implements|implements
name|ConnectionProvider
block|{
DECL|field|dataSource
specifier|private
specifier|final
name|DataSource
name|dataSource
decl_stmt|;
DECL|method|SpringConnectionProvider (DataSource dataSource)
name|SpringConnectionProvider
parameter_list|(
name|DataSource
name|dataSource
parameter_list|)
block|{
name|this
operator|.
name|dataSource
operator|=
name|dataSource
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acquire ()
specifier|public
name|Connection
name|acquire
parameter_list|()
throws|throws
name|DataAccessException
block|{
return|return
name|DataSourceUtils
operator|.
name|getConnection
argument_list|(
name|dataSource
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|release (Connection connection)
specifier|public
name|void
name|release
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|DataAccessException
block|{
name|DataSourceUtils
operator|.
name|releaseConnection
argument_list|(
name|connection
argument_list|,
name|dataSource
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

