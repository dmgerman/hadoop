begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.recon.spi.impl
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
name|spi
operator|.
name|impl
package|;
end_package

begin_import
import|import static
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
name|ReconConstants
operator|.
name|CONTAINER_KEY_TABLE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Singleton
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|ozone
operator|.
name|recon
operator|.
name|api
operator|.
name|types
operator|.
name|ContainerKeyPrefix
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
name|ozone
operator|.
name|recon
operator|.
name|spi
operator|.
name|ContainerDBServiceProvider
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
name|utils
operator|.
name|db
operator|.
name|DBStore
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
name|utils
operator|.
name|db
operator|.
name|Table
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
name|utils
operator|.
name|db
operator|.
name|Table
operator|.
name|KeyValue
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
name|utils
operator|.
name|db
operator|.
name|TableIterator
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
comment|/**  * Implementation of the Recon Container DB Service.  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|ContainerDBServiceProviderImpl
specifier|public
class|class
name|ContainerDBServiceProviderImpl
implements|implements
name|ContainerDBServiceProvider
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
name|ContainerDBServiceProviderImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|containerKeyTable
specifier|private
name|Table
argument_list|<
name|ContainerKeyPrefix
argument_list|,
name|Integer
argument_list|>
name|containerKeyTable
decl_stmt|;
annotation|@
name|Inject
DECL|field|configuration
specifier|private
name|OzoneConfiguration
name|configuration
decl_stmt|;
annotation|@
name|Inject
DECL|field|containerDbStore
specifier|private
name|DBStore
name|containerDbStore
decl_stmt|;
annotation|@
name|Inject
DECL|method|ContainerDBServiceProviderImpl (DBStore dbStore)
specifier|public
name|ContainerDBServiceProviderImpl
parameter_list|(
name|DBStore
name|dbStore
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|containerKeyTable
operator|=
name|dbStore
operator|.
name|getTable
argument_list|(
name|CONTAINER_KEY_TABLE
argument_list|,
name|ContainerKeyPrefix
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to create Container Key Table. "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Initialize a new container DB instance, getting rid of the old instance    * and then storing the passed in container prefix counts into the created    * DB instance.    * @param containerKeyPrefixCounts Map of containerId, key-prefix tuple to    * @throws IOException    */
annotation|@
name|Override
DECL|method|initNewContainerDB (Map<ContainerKeyPrefix, Integer> containerKeyPrefixCounts)
specifier|public
name|void
name|initNewContainerDB
parameter_list|(
name|Map
argument_list|<
name|ContainerKeyPrefix
argument_list|,
name|Integer
argument_list|>
name|containerKeyPrefixCounts
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|oldDBLocation
init|=
name|containerDbStore
operator|.
name|getDbLocation
argument_list|()
decl_stmt|;
name|containerDbStore
operator|=
name|ReconContainerDBProvider
operator|.
name|getNewDBStore
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|containerKeyTable
operator|=
name|containerDbStore
operator|.
name|getTable
argument_list|(
name|CONTAINER_KEY_TABLE
argument_list|,
name|ContainerKeyPrefix
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|oldDBLocation
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Cleaning up old Recon Container DB at {}."
argument_list|,
name|oldDBLocation
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|oldDBLocation
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ContainerKeyPrefix
argument_list|,
name|Integer
argument_list|>
name|entry
range|:
name|containerKeyPrefixCounts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|containerKeyTable
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Concatenate the containerId and Key Prefix using a delimiter and store the    * count into the container DB store.    *    * @param containerKeyPrefix the containerId, key-prefix tuple.    * @param count Count of the keys matching that prefix.    * @throws IOException    */
annotation|@
name|Override
DECL|method|storeContainerKeyMapping (ContainerKeyPrefix containerKeyPrefix, Integer count)
specifier|public
name|void
name|storeContainerKeyMapping
parameter_list|(
name|ContainerKeyPrefix
name|containerKeyPrefix
parameter_list|,
name|Integer
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|containerKeyTable
operator|.
name|put
argument_list|(
name|containerKeyPrefix
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
comment|/**    * Put together the key from the passed in object and get the count from    * the container DB store.    *    * @param containerKeyPrefix the containerId, key-prefix tuple.    * @return count of keys matching the containerId, key-prefix.    * @throws IOException    */
annotation|@
name|Override
DECL|method|getCountForForContainerKeyPrefix ( ContainerKeyPrefix containerKeyPrefix)
specifier|public
name|Integer
name|getCountForForContainerKeyPrefix
parameter_list|(
name|ContainerKeyPrefix
name|containerKeyPrefix
parameter_list|)
throws|throws
name|IOException
block|{
name|Integer
name|count
init|=
name|containerKeyTable
operator|.
name|get
argument_list|(
name|containerKeyPrefix
argument_list|)
decl_stmt|;
return|return
name|count
operator|==
literal|null
condition|?
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
else|:
name|count
return|;
block|}
comment|/**    * Use the DB's prefix seek iterator to start the scan from the given    * container ID prefix.    *    * @param containerId the given containerId.    * @return Map of (Key-Prefix,Count of Keys).    */
annotation|@
name|Override
DECL|method|getKeyPrefixesForContainer ( long containerId)
specifier|public
name|Map
argument_list|<
name|ContainerKeyPrefix
argument_list|,
name|Integer
argument_list|>
name|getKeyPrefixesForContainer
parameter_list|(
name|long
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|ContainerKeyPrefix
argument_list|,
name|Integer
argument_list|>
name|prefixes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|TableIterator
argument_list|<
name|ContainerKeyPrefix
argument_list|,
name|?
extends|extends
name|KeyValue
argument_list|<
name|ContainerKeyPrefix
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|containerIterator
init|=
name|containerKeyTable
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|containerIterator
operator|.
name|seek
argument_list|(
operator|new
name|ContainerKeyPrefix
argument_list|(
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
name|containerIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|KeyValue
argument_list|<
name|ContainerKeyPrefix
argument_list|,
name|Integer
argument_list|>
name|keyValue
init|=
name|containerIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|ContainerKeyPrefix
name|containerKeyPrefix
init|=
name|keyValue
operator|.
name|getKey
argument_list|()
decl_stmt|;
comment|//The prefix seek only guarantees that the iterator's head will be
comment|// positioned at the first prefix match. We still have to check the key
comment|// prefix.
if|if
condition|(
name|containerKeyPrefix
operator|.
name|getContainerId
argument_list|()
operator|==
name|containerId
condition|)
block|{
if|if
condition|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|containerKeyPrefix
operator|.
name|getKeyPrefix
argument_list|()
argument_list|)
condition|)
block|{
name|prefixes
operator|.
name|put
argument_list|(
operator|new
name|ContainerKeyPrefix
argument_list|(
name|containerId
argument_list|,
name|containerKeyPrefix
operator|.
name|getKeyPrefix
argument_list|()
argument_list|,
name|containerKeyPrefix
operator|.
name|getKeyVersion
argument_list|()
argument_list|)
argument_list|,
name|keyValue
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Null key prefix returned for containerId = "
operator|+
name|containerId
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
comment|//Break when the first mismatch occurs.
block|}
block|}
return|return
name|prefixes
return|;
block|}
block|}
end_class

end_unit

