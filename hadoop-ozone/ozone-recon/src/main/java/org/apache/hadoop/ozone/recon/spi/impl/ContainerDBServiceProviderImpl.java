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
name|commons
operator|.
name|compress
operator|.
name|utils
operator|.
name|CharsetNames
operator|.
name|UTF_8
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
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|lang3
operator|.
name|ArrayUtils
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
name|MetaStoreIterator
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
name|MetadataStore
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Longs
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
DECL|field|KEY_DELIMITER
specifier|private
specifier|final
specifier|static
name|String
name|KEY_DELIMITER
init|=
literal|"_"
decl_stmt|;
annotation|@
name|Inject
DECL|field|containerDBStore
specifier|private
name|MetadataStore
name|containerDBStore
decl_stmt|;
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
name|byte
index|[]
name|containerIdBytes
init|=
name|Longs
operator|.
name|toByteArray
argument_list|(
name|containerKeyPrefix
operator|.
name|getContainerId
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|keyPrefixBytes
init|=
operator|(
name|KEY_DELIMITER
operator|+
name|containerKeyPrefix
operator|.
name|getKeyPrefix
argument_list|()
operator|)
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
name|byte
index|[]
name|dbKey
init|=
name|ArrayUtils
operator|.
name|addAll
argument_list|(
name|containerIdBytes
argument_list|,
name|keyPrefixBytes
argument_list|)
decl_stmt|;
name|byte
index|[]
name|dbValue
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|)
operator|.
name|putInt
argument_list|(
name|count
argument_list|)
operator|.
name|array
argument_list|()
decl_stmt|;
name|containerDBStore
operator|.
name|put
argument_list|(
name|dbKey
argument_list|,
name|dbValue
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
name|byte
index|[]
name|containerIdBytes
init|=
name|Longs
operator|.
name|toByteArray
argument_list|(
name|containerKeyPrefix
operator|.
name|getContainerId
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|keyPrefixBytes
init|=
operator|(
name|KEY_DELIMITER
operator|+
name|containerKeyPrefix
operator|.
name|getKeyPrefix
argument_list|()
operator|)
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
name|byte
index|[]
name|dbKey
init|=
name|ArrayUtils
operator|.
name|addAll
argument_list|(
name|containerIdBytes
argument_list|,
name|keyPrefixBytes
argument_list|)
decl_stmt|;
name|byte
index|[]
name|dbValue
init|=
name|containerDBStore
operator|.
name|get
argument_list|(
name|dbKey
argument_list|)
decl_stmt|;
return|return
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|dbValue
argument_list|)
operator|.
name|getInt
argument_list|()
return|;
block|}
comment|/**    * Use the DB's prefix seek iterator to start the scan from the given    * container ID prefix.    *    * @param containerId the given containerId.    * @return Map of (Key-Prefix,Count of Keys).    */
annotation|@
name|Override
DECL|method|getKeyPrefixesForContainer (long containerId)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getKeyPrefixesForContainer
parameter_list|(
name|long
name|containerId
parameter_list|)
block|{
name|Map
argument_list|<
name|String
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
name|MetaStoreIterator
argument_list|<
name|MetadataStore
operator|.
name|KeyValue
argument_list|>
name|containerIterator
init|=
name|containerDBStore
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|byte
index|[]
name|containerIdPrefixBytes
init|=
name|Longs
operator|.
name|toByteArray
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|containerIterator
operator|.
name|prefixSeek
argument_list|(
name|containerIdPrefixBytes
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
name|MetadataStore
operator|.
name|KeyValue
name|keyValue
init|=
name|containerIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|byte
index|[]
name|containerKey
init|=
name|keyValue
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|long
name|containerIdFromDB
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|ArrayUtils
operator|.
name|subarray
argument_list|(
name|containerKey
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|BYTES
argument_list|)
argument_list|)
operator|.
name|getLong
argument_list|()
decl_stmt|;
comment|//The prefix seek only guarantees that the iterator's head will be
comment|// positioned at the first prefix match. We still have to check the key
comment|// prefix.
if|if
condition|(
name|containerIdFromDB
operator|==
name|containerId
condition|)
block|{
name|byte
index|[]
name|keyPrefix
init|=
name|ArrayUtils
operator|.
name|subarray
argument_list|(
name|containerKey
argument_list|,
name|containerIdPrefixBytes
operator|.
name|length
operator|+
literal|1
argument_list|,
name|containerKey
operator|.
name|length
argument_list|)
decl_stmt|;
try|try
block|{
name|prefixes
operator|.
name|put
argument_list|(
operator|new
name|String
argument_list|(
name|keyPrefix
argument_list|,
name|UTF_8
argument_list|)
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|keyValue
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|getInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to read key prefix from container DB."
argument_list|,
name|e
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

