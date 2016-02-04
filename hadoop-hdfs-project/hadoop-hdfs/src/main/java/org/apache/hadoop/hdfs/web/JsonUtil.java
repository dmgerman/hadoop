begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
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
name|fs
operator|.
name|*
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
name|fs
operator|.
name|permission
operator|.
name|AclEntry
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
name|fs
operator|.
name|permission
operator|.
name|AclStatus
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|XAttrHelper
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
name|protocol
operator|.
name|*
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
name|ipc
operator|.
name|RemoteException
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|security
operator|.
name|token
operator|.
name|TokenIdentifier
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
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
name|collect
operator|.
name|Lists
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
name|*
import|;
end_import

begin_comment
comment|/** JSON Utilities */
end_comment

begin_class
DECL|class|JsonUtil
specifier|public
class|class
name|JsonUtil
block|{
DECL|field|EMPTY_OBJECT_ARRAY
specifier|private
specifier|static
specifier|final
name|Object
index|[]
name|EMPTY_OBJECT_ARRAY
init|=
block|{}
decl_stmt|;
comment|// Reuse ObjectMapper instance for improving performance.
comment|// ObjectMapper is thread safe as long as we always configure instance
comment|// before use. We don't have a re-entrant call pattern in WebHDFS,
comment|// so we just need to worry about thread-safety.
DECL|field|MAPPER
specifier|private
specifier|static
specifier|final
name|ObjectMapper
name|MAPPER
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
comment|/** Convert a token object to a Json string. */
DECL|method|toJsonString (final Token<? extends TokenIdentifier> token )
specifier|public
specifier|static
name|String
name|toJsonString
parameter_list|(
specifier|final
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|toJsonString
argument_list|(
name|Token
operator|.
name|class
argument_list|,
name|toJsonMap
argument_list|(
name|token
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toJsonMap ( final Token<? extends TokenIdentifier> token)
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|toJsonMap
parameter_list|(
specifier|final
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"urlString"
argument_list|,
name|token
operator|.
name|encodeToUrlString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|m
return|;
block|}
comment|/** Convert an exception object to a Json string. */
DECL|method|toJsonString (final Exception e)
specifier|public
specifier|static
name|String
name|toJsonString
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"exception"
argument_list|,
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"message"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"javaClassName"
argument_list|,
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|toJsonString
argument_list|(
name|RemoteException
operator|.
name|class
argument_list|,
name|m
argument_list|)
return|;
block|}
DECL|method|toJsonString (final Class<?> clazz, final Object value)
specifier|private
specifier|static
name|String
name|toJsonString
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
specifier|final
name|Object
name|value
parameter_list|)
block|{
return|return
name|toJsonString
argument_list|(
name|clazz
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/** Convert a key-value pair to a Json string. */
DECL|method|toJsonString (final String key, final Object value)
specifier|public
specifier|static
name|String
name|toJsonString
parameter_list|(
specifier|final
name|String
name|key
parameter_list|,
specifier|final
name|Object
name|value
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|MAPPER
operator|.
name|writeValueAsString
argument_list|(
name|m
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{     }
return|return
literal|null
return|;
block|}
comment|/** Convert a FsPermission object to a string. */
DECL|method|toString (final FsPermission permission)
specifier|private
specifier|static
name|String
name|toString
parameter_list|(
specifier|final
name|FsPermission
name|permission
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%o"
argument_list|,
name|permission
operator|.
name|toShort
argument_list|()
argument_list|)
return|;
block|}
comment|/** Convert a HdfsFileStatus object to a Json string. */
DECL|method|toJsonString (final HdfsFileStatus status, boolean includeType)
specifier|public
specifier|static
name|String
name|toJsonString
parameter_list|(
specifier|final
name|HdfsFileStatus
name|status
parameter_list|,
name|boolean
name|includeType
parameter_list|)
block|{
if|if
condition|(
name|status
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"pathSuffix"
argument_list|,
name|status
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
name|WebHdfsConstants
operator|.
name|PathType
operator|.
name|valueOf
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|status
operator|.
name|isSymlink
argument_list|()
condition|)
block|{
name|m
operator|.
name|put
argument_list|(
literal|"symlink"
argument_list|,
name|status
operator|.
name|getSymlink
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|m
operator|.
name|put
argument_list|(
literal|"length"
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"owner"
argument_list|,
name|status
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"group"
argument_list|,
name|status
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
name|FsPermission
name|perm
init|=
name|status
operator|.
name|getPermission
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"permission"
argument_list|,
name|toString
argument_list|(
name|perm
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|perm
operator|.
name|getAclBit
argument_list|()
condition|)
block|{
name|m
operator|.
name|put
argument_list|(
literal|"aclBit"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|perm
operator|.
name|getEncryptedBit
argument_list|()
condition|)
block|{
name|m
operator|.
name|put
argument_list|(
literal|"encBit"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|m
operator|.
name|put
argument_list|(
literal|"accessTime"
argument_list|,
name|status
operator|.
name|getAccessTime
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"modificationTime"
argument_list|,
name|status
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"blockSize"
argument_list|,
name|status
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"replication"
argument_list|,
name|status
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"fileId"
argument_list|,
name|status
operator|.
name|getFileId
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"childrenNum"
argument_list|,
name|status
operator|.
name|getChildrenNum
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"storagePolicy"
argument_list|,
name|status
operator|.
name|getStoragePolicy
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|includeType
condition|?
name|toJsonString
argument_list|(
name|FileStatus
operator|.
name|class
argument_list|,
name|m
argument_list|)
else|:
name|MAPPER
operator|.
name|writeValueAsString
argument_list|(
name|m
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{     }
return|return
literal|null
return|;
block|}
comment|/** Convert an ExtendedBlock to a Json map. */
DECL|method|toJsonMap (final ExtendedBlock extendedblock)
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|toJsonMap
parameter_list|(
specifier|final
name|ExtendedBlock
name|extendedblock
parameter_list|)
block|{
if|if
condition|(
name|extendedblock
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"blockPoolId"
argument_list|,
name|extendedblock
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"blockId"
argument_list|,
name|extendedblock
operator|.
name|getBlockId
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"numBytes"
argument_list|,
name|extendedblock
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"generationStamp"
argument_list|,
name|extendedblock
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|m
return|;
block|}
comment|/** Convert a DatanodeInfo to a Json map. */
DECL|method|toJsonMap (final DatanodeInfo datanodeinfo)
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|toJsonMap
parameter_list|(
specifier|final
name|DatanodeInfo
name|datanodeinfo
parameter_list|)
block|{
if|if
condition|(
name|datanodeinfo
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// TODO: Fix storageID
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"ipAddr"
argument_list|,
name|datanodeinfo
operator|.
name|getIpAddr
argument_list|()
argument_list|)
expr_stmt|;
comment|// 'name' is equivalent to ipAddr:xferPort. Older clients (1.x, 0.23.x)
comment|// expects this instead of the two fields.
name|m
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|datanodeinfo
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"hostName"
argument_list|,
name|datanodeinfo
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"storageID"
argument_list|,
name|datanodeinfo
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"xferPort"
argument_list|,
name|datanodeinfo
operator|.
name|getXferPort
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"infoPort"
argument_list|,
name|datanodeinfo
operator|.
name|getInfoPort
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"infoSecurePort"
argument_list|,
name|datanodeinfo
operator|.
name|getInfoSecurePort
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"ipcPort"
argument_list|,
name|datanodeinfo
operator|.
name|getIpcPort
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"capacity"
argument_list|,
name|datanodeinfo
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"dfsUsed"
argument_list|,
name|datanodeinfo
operator|.
name|getDfsUsed
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"remaining"
argument_list|,
name|datanodeinfo
operator|.
name|getRemaining
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"blockPoolUsed"
argument_list|,
name|datanodeinfo
operator|.
name|getBlockPoolUsed
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"cacheCapacity"
argument_list|,
name|datanodeinfo
operator|.
name|getCacheCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"cacheUsed"
argument_list|,
name|datanodeinfo
operator|.
name|getCacheUsed
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"lastUpdate"
argument_list|,
name|datanodeinfo
operator|.
name|getLastUpdate
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"lastUpdateMonotonic"
argument_list|,
name|datanodeinfo
operator|.
name|getLastUpdateMonotonic
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"xceiverCount"
argument_list|,
name|datanodeinfo
operator|.
name|getXceiverCount
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"networkLocation"
argument_list|,
name|datanodeinfo
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"adminState"
argument_list|,
name|datanodeinfo
operator|.
name|getAdminState
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|datanodeinfo
operator|.
name|getUpgradeDomain
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|m
operator|.
name|put
argument_list|(
literal|"upgradeDomain"
argument_list|,
name|datanodeinfo
operator|.
name|getUpgradeDomain
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|m
return|;
block|}
comment|/** Convert a DatanodeInfo[] to a Json array. */
DECL|method|toJsonArray (final DatanodeInfo[] array)
specifier|private
specifier|static
name|Object
index|[]
name|toJsonArray
parameter_list|(
specifier|final
name|DatanodeInfo
index|[]
name|array
parameter_list|)
block|{
if|if
condition|(
name|array
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|array
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|EMPTY_OBJECT_ARRAY
return|;
block|}
else|else
block|{
specifier|final
name|Object
index|[]
name|a
init|=
operator|new
name|Object
index|[
name|array
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
name|array
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|a
index|[
name|i
index|]
operator|=
name|toJsonMap
argument_list|(
name|array
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
block|}
comment|/** Convert a StorageType[] to a Json array. */
DECL|method|toJsonArray (final StorageType[] array)
specifier|private
specifier|static
name|Object
index|[]
name|toJsonArray
parameter_list|(
specifier|final
name|StorageType
index|[]
name|array
parameter_list|)
block|{
if|if
condition|(
name|array
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|array
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|EMPTY_OBJECT_ARRAY
return|;
block|}
else|else
block|{
specifier|final
name|Object
index|[]
name|a
init|=
operator|new
name|Object
index|[
name|array
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
name|array
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|a
index|[
name|i
index|]
operator|=
name|array
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
block|}
comment|/** Convert a LocatedBlock to a Json map. */
DECL|method|toJsonMap (final LocatedBlock locatedblock )
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|toJsonMap
parameter_list|(
specifier|final
name|LocatedBlock
name|locatedblock
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|locatedblock
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"blockToken"
argument_list|,
name|toJsonMap
argument_list|(
name|locatedblock
operator|.
name|getBlockToken
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"isCorrupt"
argument_list|,
name|locatedblock
operator|.
name|isCorrupt
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"startOffset"
argument_list|,
name|locatedblock
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"block"
argument_list|,
name|toJsonMap
argument_list|(
name|locatedblock
operator|.
name|getBlock
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"storageTypes"
argument_list|,
name|toJsonArray
argument_list|(
name|locatedblock
operator|.
name|getStorageTypes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"locations"
argument_list|,
name|toJsonArray
argument_list|(
name|locatedblock
operator|.
name|getLocations
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"cachedLocations"
argument_list|,
name|toJsonArray
argument_list|(
name|locatedblock
operator|.
name|getCachedLocations
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|m
return|;
block|}
comment|/** Convert a LocatedBlock[] to a Json array. */
DECL|method|toJsonArray (final List<LocatedBlock> array )
specifier|private
specifier|static
name|Object
index|[]
name|toJsonArray
parameter_list|(
specifier|final
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|array
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|array
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|array
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|EMPTY_OBJECT_ARRAY
return|;
block|}
else|else
block|{
specifier|final
name|Object
index|[]
name|a
init|=
operator|new
name|Object
index|[
name|array
operator|.
name|size
argument_list|()
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
name|array
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|a
index|[
name|i
index|]
operator|=
name|toJsonMap
argument_list|(
name|array
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
block|}
comment|/** Convert LocatedBlocks to a Json string. */
DECL|method|toJsonString (final LocatedBlocks locatedblocks )
specifier|public
specifier|static
name|String
name|toJsonString
parameter_list|(
specifier|final
name|LocatedBlocks
name|locatedblocks
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|locatedblocks
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"fileLength"
argument_list|,
name|locatedblocks
operator|.
name|getFileLength
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"isUnderConstruction"
argument_list|,
name|locatedblocks
operator|.
name|isUnderConstruction
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"locatedBlocks"
argument_list|,
name|toJsonArray
argument_list|(
name|locatedblocks
operator|.
name|getLocatedBlocks
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"lastLocatedBlock"
argument_list|,
name|toJsonMap
argument_list|(
name|locatedblocks
operator|.
name|getLastLocatedBlock
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"isLastBlockComplete"
argument_list|,
name|locatedblocks
operator|.
name|isLastBlockComplete
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|toJsonString
argument_list|(
name|LocatedBlocks
operator|.
name|class
argument_list|,
name|m
argument_list|)
return|;
block|}
comment|/** Convert a ContentSummary to a Json string. */
DECL|method|toJsonString (final ContentSummary contentsummary)
specifier|public
specifier|static
name|String
name|toJsonString
parameter_list|(
specifier|final
name|ContentSummary
name|contentsummary
parameter_list|)
block|{
if|if
condition|(
name|contentsummary
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"length"
argument_list|,
name|contentsummary
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"fileCount"
argument_list|,
name|contentsummary
operator|.
name|getFileCount
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"directoryCount"
argument_list|,
name|contentsummary
operator|.
name|getDirectoryCount
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"quota"
argument_list|,
name|contentsummary
operator|.
name|getQuota
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"spaceConsumed"
argument_list|,
name|contentsummary
operator|.
name|getSpaceConsumed
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"spaceQuota"
argument_list|,
name|contentsummary
operator|.
name|getSpaceQuota
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|typeQuota
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|StorageType
name|t
range|:
name|StorageType
operator|.
name|getTypesSupportingQuota
argument_list|()
control|)
block|{
name|long
name|tQuota
init|=
name|contentsummary
operator|.
name|getTypeQuota
argument_list|(
name|t
argument_list|)
decl_stmt|;
if|if
condition|(
name|tQuota
operator|!=
name|HdfsConstants
operator|.
name|QUOTA_RESET
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|type
init|=
name|typeQuota
operator|.
name|get
argument_list|(
name|t
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|()
expr_stmt|;
name|typeQuota
operator|.
name|put
argument_list|(
name|t
operator|.
name|toString
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
name|type
operator|.
name|put
argument_list|(
literal|"quota"
argument_list|,
name|contentsummary
operator|.
name|getTypeQuota
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
name|type
operator|.
name|put
argument_list|(
literal|"consumed"
argument_list|,
name|contentsummary
operator|.
name|getTypeConsumed
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|m
operator|.
name|put
argument_list|(
literal|"typeQuota"
argument_list|,
name|typeQuota
argument_list|)
expr_stmt|;
return|return
name|toJsonString
argument_list|(
name|ContentSummary
operator|.
name|class
argument_list|,
name|m
argument_list|)
return|;
block|}
comment|/** Convert a MD5MD5CRC32FileChecksum to a Json string. */
DECL|method|toJsonString (final MD5MD5CRC32FileChecksum checksum)
specifier|public
specifier|static
name|String
name|toJsonString
parameter_list|(
specifier|final
name|MD5MD5CRC32FileChecksum
name|checksum
parameter_list|)
block|{
if|if
condition|(
name|checksum
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"algorithm"
argument_list|,
name|checksum
operator|.
name|getAlgorithmName
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"length"
argument_list|,
name|checksum
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"bytes"
argument_list|,
name|StringUtils
operator|.
name|byteToHexString
argument_list|(
name|checksum
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|toJsonString
argument_list|(
name|FileChecksum
operator|.
name|class
argument_list|,
name|m
argument_list|)
return|;
block|}
comment|/** Convert a AclStatus object to a Json string. */
DECL|method|toJsonString (final AclStatus status)
specifier|public
specifier|static
name|String
name|toJsonString
parameter_list|(
specifier|final
name|AclStatus
name|status
parameter_list|)
block|{
if|if
condition|(
name|status
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"owner"
argument_list|,
name|status
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"group"
argument_list|,
name|status
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"stickyBit"
argument_list|,
name|status
operator|.
name|isStickyBit
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|stringEntries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AclEntry
name|entry
range|:
name|status
operator|.
name|getEntries
argument_list|()
control|)
block|{
name|stringEntries
operator|.
name|add
argument_list|(
name|entry
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|m
operator|.
name|put
argument_list|(
literal|"entries"
argument_list|,
name|stringEntries
argument_list|)
expr_stmt|;
name|FsPermission
name|perm
init|=
name|status
operator|.
name|getPermission
argument_list|()
decl_stmt|;
if|if
condition|(
name|perm
operator|!=
literal|null
condition|)
block|{
name|m
operator|.
name|put
argument_list|(
literal|"permission"
argument_list|,
name|toString
argument_list|(
name|perm
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|perm
operator|.
name|getAclBit
argument_list|()
condition|)
block|{
name|m
operator|.
name|put
argument_list|(
literal|"aclBit"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|perm
operator|.
name|getEncryptedBit
argument_list|()
condition|)
block|{
name|m
operator|.
name|put
argument_list|(
literal|"encBit"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|finalMap
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|finalMap
operator|.
name|put
argument_list|(
name|AclStatus
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|m
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|MAPPER
operator|.
name|writeValueAsString
argument_list|(
name|finalMap
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{     }
return|return
literal|null
return|;
block|}
DECL|method|toJsonMap (final XAttr xAttr, final XAttrCodec encoding)
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|toJsonMap
parameter_list|(
specifier|final
name|XAttr
name|xAttr
parameter_list|,
specifier|final
name|XAttrCodec
name|encoding
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|xAttr
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|XAttrHelper
operator|.
name|getPrefixedName
argument_list|(
name|xAttr
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"value"
argument_list|,
name|xAttr
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|?
name|XAttrCodec
operator|.
name|encodeValue
argument_list|(
name|xAttr
operator|.
name|getValue
argument_list|()
argument_list|,
name|encoding
argument_list|)
else|:
literal|null
argument_list|)
expr_stmt|;
return|return
name|m
return|;
block|}
DECL|method|toJsonArray (final List<XAttr> array, final XAttrCodec encoding)
specifier|private
specifier|static
name|Object
index|[]
name|toJsonArray
parameter_list|(
specifier|final
name|List
argument_list|<
name|XAttr
argument_list|>
name|array
parameter_list|,
specifier|final
name|XAttrCodec
name|encoding
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|array
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|array
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|EMPTY_OBJECT_ARRAY
return|;
block|}
else|else
block|{
specifier|final
name|Object
index|[]
name|a
init|=
operator|new
name|Object
index|[
name|array
operator|.
name|size
argument_list|()
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
name|array
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|a
index|[
name|i
index|]
operator|=
name|toJsonMap
argument_list|(
name|array
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
block|}
DECL|method|toJsonString (final List<XAttr> xAttrs, final XAttrCodec encoding)
specifier|public
specifier|static
name|String
name|toJsonString
parameter_list|(
specifier|final
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
parameter_list|,
specifier|final
name|XAttrCodec
name|encoding
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|finalMap
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|finalMap
operator|.
name|put
argument_list|(
literal|"XAttrs"
argument_list|,
name|toJsonArray
argument_list|(
name|xAttrs
argument_list|,
name|encoding
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|MAPPER
operator|.
name|writeValueAsString
argument_list|(
name|finalMap
argument_list|)
return|;
block|}
DECL|method|toJsonString (final List<XAttr> xAttrs)
specifier|public
specifier|static
name|String
name|toJsonString
parameter_list|(
specifier|final
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|xAttrs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|XAttr
name|xAttr
range|:
name|xAttrs
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|XAttrHelper
operator|.
name|getPrefixedName
argument_list|(
name|xAttr
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|ret
init|=
name|MAPPER
operator|.
name|writeValueAsString
argument_list|(
name|names
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|finalMap
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|finalMap
operator|.
name|put
argument_list|(
literal|"XAttrNames"
argument_list|,
name|ret
argument_list|)
expr_stmt|;
return|return
name|MAPPER
operator|.
name|writeValueAsString
argument_list|(
name|finalMap
argument_list|)
return|;
block|}
block|}
end_class

end_unit

