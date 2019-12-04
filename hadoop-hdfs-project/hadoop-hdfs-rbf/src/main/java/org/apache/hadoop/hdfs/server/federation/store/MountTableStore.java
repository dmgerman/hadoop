begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|MountTableManager
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|router
operator|.
name|MountTableRefresherService
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|router
operator|.
name|RouterQuotaManager
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|driver
operator|.
name|StateStoreDriver
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|MountTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Management API for the HDFS mount table information stored in  * {@link org.apache.hadoop.hdfs.server.federation.store.records.MountTable  * MountTable} records. The mount table contains entries that map a particular  * global namespace path one or more HDFS nameservices (NN) + target path. It is  * possible to map mount locations for root folders, directories or individual  * files.  *<p>  * Once fetched from the  * {@link org.apache.hadoop.hdfs.server.federation.store.driver.StateStoreDriver  * StateStoreDriver}, MountTable records are cached in a tree for faster access.  * Each path in the global namespace is mapped to a nameserivce ID and local  * path upon request. The cache is periodically updated by the @{link  * StateStoreCacheUpdateService}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|MountTableStore
specifier|public
specifier|abstract
class|class
name|MountTableStore
extends|extends
name|CachedRecordStore
argument_list|<
name|MountTable
argument_list|>
implements|implements
name|MountTableManager
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MountTableStore
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|refreshService
specifier|private
name|MountTableRefresherService
name|refreshService
decl_stmt|;
comment|/** Router quota manager to update quota usage in mount table. */
DECL|field|quotaManager
specifier|private
name|RouterQuotaManager
name|quotaManager
decl_stmt|;
DECL|method|MountTableStore (StateStoreDriver driver)
specifier|public
name|MountTableStore
parameter_list|(
name|StateStoreDriver
name|driver
parameter_list|)
block|{
name|super
argument_list|(
name|MountTable
operator|.
name|class
argument_list|,
name|driver
argument_list|)
expr_stmt|;
block|}
DECL|method|setRefreshService (MountTableRefresherService refreshService)
specifier|public
name|void
name|setRefreshService
parameter_list|(
name|MountTableRefresherService
name|refreshService
parameter_list|)
block|{
name|this
operator|.
name|refreshService
operator|=
name|refreshService
expr_stmt|;
block|}
DECL|method|setQuotaManager (RouterQuotaManager quotaManager)
specifier|public
name|void
name|setQuotaManager
parameter_list|(
name|RouterQuotaManager
name|quotaManager
parameter_list|)
block|{
name|this
operator|.
name|quotaManager
operator|=
name|quotaManager
expr_stmt|;
block|}
DECL|method|getQuotaManager ()
specifier|public
name|RouterQuotaManager
name|getQuotaManager
parameter_list|()
block|{
return|return
name|quotaManager
return|;
block|}
comment|/**    * Update mount table cache of this router as well as all other routers.    */
DECL|method|updateCacheAllRouters ()
specifier|protected
name|void
name|updateCacheAllRouters
parameter_list|()
block|{
if|if
condition|(
name|refreshService
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|refreshService
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StateStoreUnavailableException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot refresh mount table: state store not available"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

