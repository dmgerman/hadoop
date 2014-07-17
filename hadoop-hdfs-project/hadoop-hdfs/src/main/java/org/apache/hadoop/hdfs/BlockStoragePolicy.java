begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * A block storage policy describes how to select the storage types  * for the replicas of a block.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockStoragePolicy
specifier|public
class|class
name|BlockStoragePolicy
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|BlockStoragePolicy
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DFS_BLOCK_STORAGE_POLICIES_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_BLOCK_STORAGE_POLICIES_KEY
init|=
literal|"dfs.block.storage.policies"
decl_stmt|;
DECL|field|DFS_BLOCK_STORAGE_POLICY_KEY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|DFS_BLOCK_STORAGE_POLICY_KEY_PREFIX
init|=
literal|"dfs.block.storage.policy."
decl_stmt|;
DECL|field|DFS_BLOCK_STORAGE_POLICY_CREATION_FALLBACK_KEY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|DFS_BLOCK_STORAGE_POLICY_CREATION_FALLBACK_KEY_PREFIX
init|=
literal|"dfs.block.storage.policy.creation-fallback."
decl_stmt|;
DECL|field|DFS_BLOCK_STORAGE_POLICY_REPLICATION_FALLBACK_KEY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|DFS_BLOCK_STORAGE_POLICY_REPLICATION_FALLBACK_KEY_PREFIX
init|=
literal|"dfs.block.storage.policy.replication-fallback."
decl_stmt|;
DECL|field|ID_BIT_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|ID_BIT_LENGTH
init|=
literal|4
decl_stmt|;
DECL|field|ID_MAX
specifier|public
specifier|static
specifier|final
name|int
name|ID_MAX
init|=
operator|(
literal|1
operator|<<
name|ID_BIT_LENGTH
operator|)
operator|-
literal|1
decl_stmt|;
comment|/** A block storage policy suite. */
DECL|class|Suite
specifier|public
specifier|static
class|class
name|Suite
block|{
DECL|field|defaultPolicyID
specifier|private
specifier|final
name|byte
name|defaultPolicyID
decl_stmt|;
DECL|field|policies
specifier|private
specifier|final
name|BlockStoragePolicy
index|[]
name|policies
decl_stmt|;
DECL|method|Suite (byte defaultPolicyID, BlockStoragePolicy[] policies)
specifier|private
name|Suite
parameter_list|(
name|byte
name|defaultPolicyID
parameter_list|,
name|BlockStoragePolicy
index|[]
name|policies
parameter_list|)
block|{
name|this
operator|.
name|defaultPolicyID
operator|=
name|defaultPolicyID
expr_stmt|;
name|this
operator|.
name|policies
operator|=
name|policies
expr_stmt|;
block|}
comment|/** @return the corresponding policy. */
DECL|method|getPolicy (byte id)
specifier|public
name|BlockStoragePolicy
name|getPolicy
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
comment|// id == 0 means policy not specified.
return|return
name|id
operator|==
literal|0
condition|?
name|getDefaultPolicy
argument_list|()
else|:
name|policies
index|[
name|id
index|]
return|;
block|}
comment|/** @return the default policy. */
DECL|method|getDefaultPolicy ()
specifier|public
name|BlockStoragePolicy
name|getDefaultPolicy
parameter_list|()
block|{
return|return
name|getPolicy
argument_list|(
name|defaultPolicyID
argument_list|)
return|;
block|}
block|}
comment|/** A 4-bit policy ID */
DECL|field|id
specifier|private
specifier|final
name|byte
name|id
decl_stmt|;
comment|/** Policy name */
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/** The storage types to store the replicas of a new block. */
DECL|field|storageTypes
specifier|private
specifier|final
name|StorageType
index|[]
name|storageTypes
decl_stmt|;
comment|/** The fallback storage type for block creation. */
DECL|field|creationFallbacks
specifier|private
specifier|final
name|StorageType
index|[]
name|creationFallbacks
decl_stmt|;
comment|/** The fallback storage type for replication. */
DECL|field|replicationFallbacks
specifier|private
specifier|final
name|StorageType
index|[]
name|replicationFallbacks
decl_stmt|;
DECL|method|BlockStoragePolicy (byte id, String name, StorageType[] storageTypes, StorageType[] creationFallbacks, StorageType[] replicationFallbacks)
name|BlockStoragePolicy
parameter_list|(
name|byte
name|id
parameter_list|,
name|String
name|name
parameter_list|,
name|StorageType
index|[]
name|storageTypes
parameter_list|,
name|StorageType
index|[]
name|creationFallbacks
parameter_list|,
name|StorageType
index|[]
name|replicationFallbacks
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|storageTypes
operator|=
name|storageTypes
expr_stmt|;
name|this
operator|.
name|creationFallbacks
operator|=
name|creationFallbacks
expr_stmt|;
name|this
operator|.
name|replicationFallbacks
operator|=
name|replicationFallbacks
expr_stmt|;
block|}
comment|/**    * @return a list of {@link StorageType}s for storing the replicas of a block.    */
DECL|method|chooseStorageTypes (final short replication)
specifier|public
name|List
argument_list|<
name|StorageType
argument_list|>
name|chooseStorageTypes
parameter_list|(
specifier|final
name|short
name|replication
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|StorageType
argument_list|>
name|types
init|=
operator|new
name|LinkedList
argument_list|<
name|StorageType
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|replication
operator|&&
name|i
operator|<
name|storageTypes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|types
operator|.
name|add
argument_list|(
name|storageTypes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
specifier|final
name|StorageType
name|last
init|=
name|storageTypes
index|[
name|storageTypes
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|replication
condition|;
name|i
operator|++
control|)
block|{
name|types
operator|.
name|add
argument_list|(
name|last
argument_list|)
expr_stmt|;
block|}
return|return
name|types
return|;
block|}
comment|/**    * Choose the storage types for storing the remaining replicas, given the    * replication number and the storage types of the chosen replicas.    *    * @param replication the replication number.    * @param chosen the storage types of the chosen replicas.    * @return a list of {@link StorageType}s for storing the replicas of a block.    */
DECL|method|chooseStorageTypes (final short replication, final Iterable<StorageType> chosen)
specifier|public
name|List
argument_list|<
name|StorageType
argument_list|>
name|chooseStorageTypes
parameter_list|(
specifier|final
name|short
name|replication
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|StorageType
argument_list|>
name|chosen
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|StorageType
argument_list|>
name|types
init|=
name|chooseStorageTypes
argument_list|(
name|replication
argument_list|)
decl_stmt|;
comment|//remove the chosen storage types
for|for
control|(
name|StorageType
name|c
range|:
name|chosen
control|)
block|{
specifier|final
name|int
name|i
init|=
name|types
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
name|types
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|types
return|;
block|}
comment|/** @return the fallback {@link StorageType} for creation. */
DECL|method|getCreationFallback (EnumSet<StorageType> unavailables)
specifier|public
name|StorageType
name|getCreationFallback
parameter_list|(
name|EnumSet
argument_list|<
name|StorageType
argument_list|>
name|unavailables
parameter_list|)
block|{
return|return
name|getFallback
argument_list|(
name|unavailables
argument_list|,
name|creationFallbacks
argument_list|)
return|;
block|}
comment|/** @return the fallback {@link StorageType} for replication. */
DECL|method|getReplicationFallback (EnumSet<StorageType> unavailables)
specifier|public
name|StorageType
name|getReplicationFallback
parameter_list|(
name|EnumSet
argument_list|<
name|StorageType
argument_list|>
name|unavailables
parameter_list|)
block|{
return|return
name|getFallback
argument_list|(
name|unavailables
argument_list|,
name|replicationFallbacks
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"{"
operator|+
name|name
operator|+
literal|":"
operator|+
name|id
operator|+
literal|", storageTypes="
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|storageTypes
argument_list|)
operator|+
literal|", creationFallbacks="
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|creationFallbacks
argument_list|)
operator|+
literal|", replicationFallbacks="
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|replicationFallbacks
argument_list|)
return|;
block|}
DECL|method|getFallback (EnumSet<StorageType> unavailables, StorageType[] fallbacks)
specifier|private
specifier|static
name|StorageType
name|getFallback
parameter_list|(
name|EnumSet
argument_list|<
name|StorageType
argument_list|>
name|unavailables
parameter_list|,
name|StorageType
index|[]
name|fallbacks
parameter_list|)
block|{
for|for
control|(
name|StorageType
name|fb
range|:
name|fallbacks
control|)
block|{
if|if
condition|(
operator|!
name|unavailables
operator|.
name|contains
argument_list|(
name|fb
argument_list|)
condition|)
block|{
return|return
name|fb
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|parseID (String idString, String element, Configuration conf)
specifier|private
specifier|static
name|byte
name|parseID
parameter_list|(
name|String
name|idString
parameter_list|,
name|String
name|element
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|Byte
name|id
init|=
literal|null
decl_stmt|;
try|try
block|{
name|id
operator|=
name|Byte
operator|.
name|parseByte
argument_list|(
name|idString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|throwIllegalArgumentException
argument_list|(
literal|"Failed to parse policy ID \""
operator|+
name|idString
operator|+
literal|"\" to a "
operator|+
name|ID_BIT_LENGTH
operator|+
literal|"-bit integer"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|id
operator|<
literal|0
condition|)
block|{
name|throwIllegalArgumentException
argument_list|(
literal|"Invalid policy ID: id = "
operator|+
name|id
operator|+
literal|"< 1 in \""
operator|+
name|element
operator|+
literal|"\""
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Policy ID 0 is reserved: "
operator|+
name|element
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|id
operator|>
name|ID_MAX
condition|)
block|{
name|throwIllegalArgumentException
argument_list|(
literal|"Invalid policy ID: id = "
operator|+
name|id
operator|+
literal|"> MAX = "
operator|+
name|ID_MAX
operator|+
literal|" in \""
operator|+
name|element
operator|+
literal|"\""
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
DECL|method|parseStorageTypes (String[] strings)
specifier|private
specifier|static
name|StorageType
index|[]
name|parseStorageTypes
parameter_list|(
name|String
index|[]
name|strings
parameter_list|)
block|{
if|if
condition|(
name|strings
operator|==
literal|null
operator|||
name|strings
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|StorageType
operator|.
name|EMPTY_ARRAY
return|;
block|}
specifier|final
name|StorageType
index|[]
name|types
init|=
operator|new
name|StorageType
index|[
name|strings
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|types
index|[
name|i
index|]
operator|=
name|StorageType
operator|.
name|valueOf
argument_list|(
name|strings
index|[
name|i
index|]
operator|.
name|trim
argument_list|()
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|types
return|;
block|}
DECL|method|readStorageTypes (byte id, String keyPrefix, Configuration conf)
specifier|private
specifier|static
name|StorageType
index|[]
name|readStorageTypes
parameter_list|(
name|byte
name|id
parameter_list|,
name|String
name|keyPrefix
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
specifier|final
name|String
name|key
init|=
name|keyPrefix
operator|+
name|id
decl_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
name|conf
operator|.
name|getStrings
argument_list|(
name|key
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|parseStorageTypes
argument_list|(
name|values
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Failed to parse "
operator|+
name|key
operator|+
literal|" \""
operator|+
name|conf
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|readBlockStoragePolicy (byte id, String name, Configuration conf)
specifier|private
specifier|static
name|BlockStoragePolicy
name|readBlockStoragePolicy
parameter_list|(
name|byte
name|id
parameter_list|,
name|String
name|name
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
specifier|final
name|StorageType
index|[]
name|storageTypes
init|=
name|readStorageTypes
argument_list|(
name|id
argument_list|,
name|DFS_BLOCK_STORAGE_POLICY_KEY_PREFIX
argument_list|,
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|storageTypes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|DFS_BLOCK_STORAGE_POLICY_KEY_PREFIX
operator|+
name|id
operator|+
literal|" is missing or is empty."
argument_list|)
throw|;
block|}
specifier|final
name|StorageType
index|[]
name|creationFallbacks
init|=
name|readStorageTypes
argument_list|(
name|id
argument_list|,
name|DFS_BLOCK_STORAGE_POLICY_CREATION_FALLBACK_KEY_PREFIX
argument_list|,
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|StorageType
index|[]
name|replicationFallbacks
init|=
name|readStorageTypes
argument_list|(
name|id
argument_list|,
name|DFS_BLOCK_STORAGE_POLICY_REPLICATION_FALLBACK_KEY_PREFIX
argument_list|,
name|conf
argument_list|)
decl_stmt|;
return|return
operator|new
name|BlockStoragePolicy
argument_list|(
name|id
argument_list|,
name|name
argument_list|,
name|storageTypes
argument_list|,
name|creationFallbacks
argument_list|,
name|replicationFallbacks
argument_list|)
return|;
block|}
comment|/** Read {@link Suite} from conf. */
DECL|method|readBlockStorageSuite (Configuration conf)
specifier|public
specifier|static
name|Suite
name|readBlockStorageSuite
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
specifier|final
name|BlockStoragePolicy
index|[]
name|policies
init|=
operator|new
name|BlockStoragePolicy
index|[
literal|1
operator|<<
name|ID_BIT_LENGTH
index|]
decl_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
name|conf
operator|.
name|getStrings
argument_list|(
name|DFS_BLOCK_STORAGE_POLICIES_KEY
argument_list|)
decl_stmt|;
name|byte
name|firstID
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|String
name|v
range|:
name|values
control|)
block|{
name|v
operator|=
name|v
operator|.
name|trim
argument_list|()
expr_stmt|;
specifier|final
name|int
name|i
init|=
name|v
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
condition|)
block|{
name|throwIllegalArgumentException
argument_list|(
literal|"Failed to parse element \""
operator|+
name|v
operator|+
literal|"\" (expected format is NAME:ID)"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|throwIllegalArgumentException
argument_list|(
literal|"Policy name is missing in \""
operator|+
name|v
operator|+
literal|"\""
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
name|v
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
name|throwIllegalArgumentException
argument_list|(
literal|"Policy ID is missing in \""
operator|+
name|v
operator|+
literal|"\""
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|name
init|=
name|v
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|policies
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|policies
index|[
name|j
index|]
operator|!=
literal|null
operator|&&
name|policies
index|[
name|j
index|]
operator|.
name|name
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|throwIllegalArgumentException
argument_list|(
literal|"Policy name duplication: \""
operator|+
name|name
operator|+
literal|"\" appears more than once"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|byte
name|id
init|=
name|parseID
argument_list|(
name|v
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|,
name|v
argument_list|,
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|policies
index|[
name|id
index|]
operator|!=
literal|null
condition|)
block|{
name|throwIllegalArgumentException
argument_list|(
literal|"Policy duplication: ID "
operator|+
name|id
operator|+
literal|" appears more than once"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
name|policies
index|[
name|id
index|]
operator|=
name|readBlockStoragePolicy
argument_list|(
name|id
argument_list|,
name|name
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|String
name|prefix
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|firstID
operator|==
operator|-
literal|1
condition|)
block|{
name|firstID
operator|=
name|id
expr_stmt|;
name|prefix
operator|=
literal|"(default) "
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|prefix
operator|+
name|policies
index|[
name|id
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|firstID
operator|==
operator|-
literal|1
condition|)
block|{
name|throwIllegalArgumentException
argument_list|(
literal|"Empty list is not allowed"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Suite
argument_list|(
name|firstID
argument_list|,
name|policies
argument_list|)
return|;
block|}
DECL|method|throwIllegalArgumentException (String message, Configuration conf)
specifier|private
specifier|static
name|void
name|throwIllegalArgumentException
parameter_list|(
name|String
name|message
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|message
operator|+
literal|" in "
operator|+
name|DFS_BLOCK_STORAGE_POLICIES_KEY
operator|+
literal|" \""
operator|+
name|conf
operator|.
name|get
argument_list|(
name|DFS_BLOCK_STORAGE_POLICIES_KEY
argument_list|)
operator|+
literal|"\"."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

