begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|fs
operator|.
name|permission
operator|.
name|FsAction
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
name|protocol
operator|.
name|CacheDirective
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
name|CachePoolEntry
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
name|CachePoolInfo
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
name|CachePoolStats
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
name|AccessControlException
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
name|UserGroupInformation
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
name|IntrusiveCollection
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
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * A CachePool describes a set of cache resources being managed by the NameNode.  * User caching requests are billed to the cache pool specified in the request.  *  * This is an internal class, only used on the NameNode.  For identifying or  * describing a cache pool to clients, please use CachePoolInfo.  *   * CachePools must be accessed under the FSNamesystem lock.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|CachePool
specifier|public
specifier|final
class|class
name|CachePool
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
name|CachePool
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Nonnull
DECL|field|poolName
specifier|private
specifier|final
name|String
name|poolName
decl_stmt|;
annotation|@
name|Nonnull
DECL|field|ownerName
specifier|private
name|String
name|ownerName
decl_stmt|;
annotation|@
name|Nonnull
DECL|field|groupName
specifier|private
name|String
name|groupName
decl_stmt|;
comment|/**    * Cache pool permissions.    *     * READ permission means that you can list the cache directives in this pool.    * WRITE permission means that you can add, remove, or modify cache directives    *       in this pool.    * EXECUTE permission is unused.    */
annotation|@
name|Nonnull
DECL|field|mode
specifier|private
name|FsPermission
name|mode
decl_stmt|;
comment|/**    * Maximum number of bytes that can be cached in this pool.    */
DECL|field|limit
specifier|private
name|long
name|limit
decl_stmt|;
comment|/**    * Maximum duration that a CacheDirective in this pool remains valid,    * in milliseconds.    */
DECL|field|maxRelativeExpiryMs
specifier|private
name|long
name|maxRelativeExpiryMs
decl_stmt|;
DECL|field|bytesNeeded
specifier|private
name|long
name|bytesNeeded
decl_stmt|;
DECL|field|bytesCached
specifier|private
name|long
name|bytesCached
decl_stmt|;
DECL|field|filesNeeded
specifier|private
name|long
name|filesNeeded
decl_stmt|;
DECL|field|filesCached
specifier|private
name|long
name|filesCached
decl_stmt|;
DECL|class|DirectiveList
specifier|public
specifier|final
specifier|static
class|class
name|DirectiveList
extends|extends
name|IntrusiveCollection
argument_list|<
name|CacheDirective
argument_list|>
block|{
DECL|field|cachePool
specifier|private
name|CachePool
name|cachePool
decl_stmt|;
DECL|method|DirectiveList (CachePool cachePool)
specifier|private
name|DirectiveList
parameter_list|(
name|CachePool
name|cachePool
parameter_list|)
block|{
name|this
operator|.
name|cachePool
operator|=
name|cachePool
expr_stmt|;
block|}
DECL|method|getCachePool ()
specifier|public
name|CachePool
name|getCachePool
parameter_list|()
block|{
return|return
name|cachePool
return|;
block|}
block|}
annotation|@
name|Nonnull
DECL|field|directiveList
specifier|private
specifier|final
name|DirectiveList
name|directiveList
init|=
operator|new
name|DirectiveList
argument_list|(
name|this
argument_list|)
decl_stmt|;
comment|/**    * Create a new cache pool based on a CachePoolInfo object and the defaults.    * We will fill in information that was not supplied according to the    * defaults.    */
DECL|method|createFromInfoAndDefaults (CachePoolInfo info)
specifier|static
name|CachePool
name|createFromInfoAndDefaults
parameter_list|(
name|CachePoolInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
name|UserGroupInformation
name|ugi
init|=
literal|null
decl_stmt|;
name|String
name|ownerName
init|=
name|info
operator|.
name|getOwnerName
argument_list|()
decl_stmt|;
if|if
condition|(
name|ownerName
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|ugi
operator|==
literal|null
condition|)
block|{
name|ugi
operator|=
name|NameNode
operator|.
name|getRemoteUser
argument_list|()
expr_stmt|;
block|}
name|ownerName
operator|=
name|ugi
operator|.
name|getShortUserName
argument_list|()
expr_stmt|;
block|}
name|String
name|groupName
init|=
name|info
operator|.
name|getGroupName
argument_list|()
decl_stmt|;
if|if
condition|(
name|groupName
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|ugi
operator|==
literal|null
condition|)
block|{
name|ugi
operator|=
name|NameNode
operator|.
name|getRemoteUser
argument_list|()
expr_stmt|;
block|}
name|groupName
operator|=
name|ugi
operator|.
name|getPrimaryGroupName
argument_list|()
expr_stmt|;
block|}
name|FsPermission
name|mode
init|=
operator|(
name|info
operator|.
name|getMode
argument_list|()
operator|==
literal|null
operator|)
condition|?
name|FsPermission
operator|.
name|getCachePoolDefault
argument_list|()
else|:
name|info
operator|.
name|getMode
argument_list|()
decl_stmt|;
name|long
name|limit
init|=
name|info
operator|.
name|getLimit
argument_list|()
operator|==
literal|null
condition|?
name|CachePoolInfo
operator|.
name|DEFAULT_LIMIT
else|:
name|info
operator|.
name|getLimit
argument_list|()
decl_stmt|;
name|long
name|maxRelativeExpiry
init|=
name|info
operator|.
name|getMaxRelativeExpiryMs
argument_list|()
operator|==
literal|null
condition|?
name|CachePoolInfo
operator|.
name|DEFAULT_MAX_RELATIVE_EXPIRY
else|:
name|info
operator|.
name|getMaxRelativeExpiryMs
argument_list|()
decl_stmt|;
return|return
operator|new
name|CachePool
argument_list|(
name|info
operator|.
name|getPoolName
argument_list|()
argument_list|,
name|ownerName
argument_list|,
name|groupName
argument_list|,
name|mode
argument_list|,
name|limit
argument_list|,
name|maxRelativeExpiry
argument_list|)
return|;
block|}
comment|/**    * Create a new cache pool based on a CachePoolInfo object.    * No fields in the CachePoolInfo can be blank.    */
DECL|method|createFromInfo (CachePoolInfo info)
specifier|static
name|CachePool
name|createFromInfo
parameter_list|(
name|CachePoolInfo
name|info
parameter_list|)
block|{
return|return
operator|new
name|CachePool
argument_list|(
name|info
operator|.
name|getPoolName
argument_list|()
argument_list|,
name|info
operator|.
name|getOwnerName
argument_list|()
argument_list|,
name|info
operator|.
name|getGroupName
argument_list|()
argument_list|,
name|info
operator|.
name|getMode
argument_list|()
argument_list|,
name|info
operator|.
name|getLimit
argument_list|()
argument_list|,
name|info
operator|.
name|getMaxRelativeExpiryMs
argument_list|()
argument_list|)
return|;
block|}
DECL|method|CachePool (String poolName, String ownerName, String groupName, FsPermission mode, long limit, long maxRelativeExpiry)
name|CachePool
parameter_list|(
name|String
name|poolName
parameter_list|,
name|String
name|ownerName
parameter_list|,
name|String
name|groupName
parameter_list|,
name|FsPermission
name|mode
parameter_list|,
name|long
name|limit
parameter_list|,
name|long
name|maxRelativeExpiry
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|poolName
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|ownerName
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|groupName
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|mode
argument_list|)
expr_stmt|;
name|this
operator|.
name|poolName
operator|=
name|poolName
expr_stmt|;
name|this
operator|.
name|ownerName
operator|=
name|ownerName
expr_stmt|;
name|this
operator|.
name|groupName
operator|=
name|groupName
expr_stmt|;
name|this
operator|.
name|mode
operator|=
operator|new
name|FsPermission
argument_list|(
name|mode
argument_list|)
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
name|this
operator|.
name|maxRelativeExpiryMs
operator|=
name|maxRelativeExpiry
expr_stmt|;
block|}
DECL|method|getPoolName ()
specifier|public
name|String
name|getPoolName
parameter_list|()
block|{
return|return
name|poolName
return|;
block|}
DECL|method|getOwnerName ()
specifier|public
name|String
name|getOwnerName
parameter_list|()
block|{
return|return
name|ownerName
return|;
block|}
DECL|method|setOwnerName (String ownerName)
specifier|public
name|CachePool
name|setOwnerName
parameter_list|(
name|String
name|ownerName
parameter_list|)
block|{
name|this
operator|.
name|ownerName
operator|=
name|ownerName
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getGroupName ()
specifier|public
name|String
name|getGroupName
parameter_list|()
block|{
return|return
name|groupName
return|;
block|}
DECL|method|setGroupName (String groupName)
specifier|public
name|CachePool
name|setGroupName
parameter_list|(
name|String
name|groupName
parameter_list|)
block|{
name|this
operator|.
name|groupName
operator|=
name|groupName
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getMode ()
specifier|public
name|FsPermission
name|getMode
parameter_list|()
block|{
return|return
name|mode
return|;
block|}
DECL|method|setMode (FsPermission mode)
specifier|public
name|CachePool
name|setMode
parameter_list|(
name|FsPermission
name|mode
parameter_list|)
block|{
name|this
operator|.
name|mode
operator|=
operator|new
name|FsPermission
argument_list|(
name|mode
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getLimit ()
specifier|public
name|long
name|getLimit
parameter_list|()
block|{
return|return
name|limit
return|;
block|}
DECL|method|setLimit (long bytes)
specifier|public
name|CachePool
name|setLimit
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
name|this
operator|.
name|limit
operator|=
name|bytes
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getMaxRelativeExpiryMs ()
specifier|public
name|long
name|getMaxRelativeExpiryMs
parameter_list|()
block|{
return|return
name|maxRelativeExpiryMs
return|;
block|}
DECL|method|setMaxRelativeExpiryMs (long expiry)
specifier|public
name|CachePool
name|setMaxRelativeExpiryMs
parameter_list|(
name|long
name|expiry
parameter_list|)
block|{
name|this
operator|.
name|maxRelativeExpiryMs
operator|=
name|expiry
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Get either full or partial information about this CachePool.    *    * @param fullInfo    *          If true, only the name will be returned (i.e., what you     *          would get if you didn't have read permission for this pool.)    * @return    *          Cache pool information.    */
DECL|method|getInfo (boolean fullInfo)
name|CachePoolInfo
name|getInfo
parameter_list|(
name|boolean
name|fullInfo
parameter_list|)
block|{
name|CachePoolInfo
name|info
init|=
operator|new
name|CachePoolInfo
argument_list|(
name|poolName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fullInfo
condition|)
block|{
return|return
name|info
return|;
block|}
return|return
name|info
operator|.
name|setOwnerName
argument_list|(
name|ownerName
argument_list|)
operator|.
name|setGroupName
argument_list|(
name|groupName
argument_list|)
operator|.
name|setMode
argument_list|(
operator|new
name|FsPermission
argument_list|(
name|mode
argument_list|)
argument_list|)
operator|.
name|setLimit
argument_list|(
name|limit
argument_list|)
operator|.
name|setMaxRelativeExpiryMs
argument_list|(
name|maxRelativeExpiryMs
argument_list|)
return|;
block|}
comment|/**    * Resets statistics related to this CachePool    */
DECL|method|resetStatistics ()
specifier|public
name|void
name|resetStatistics
parameter_list|()
block|{
name|bytesNeeded
operator|=
literal|0
expr_stmt|;
name|bytesCached
operator|=
literal|0
expr_stmt|;
name|filesNeeded
operator|=
literal|0
expr_stmt|;
name|filesCached
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|addBytesNeeded (long bytes)
specifier|public
name|void
name|addBytesNeeded
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
name|bytesNeeded
operator|+=
name|bytes
expr_stmt|;
block|}
DECL|method|addBytesCached (long bytes)
specifier|public
name|void
name|addBytesCached
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
name|bytesCached
operator|+=
name|bytes
expr_stmt|;
block|}
DECL|method|addFilesNeeded (long files)
specifier|public
name|void
name|addFilesNeeded
parameter_list|(
name|long
name|files
parameter_list|)
block|{
name|filesNeeded
operator|+=
name|files
expr_stmt|;
block|}
DECL|method|addFilesCached (long files)
specifier|public
name|void
name|addFilesCached
parameter_list|(
name|long
name|files
parameter_list|)
block|{
name|filesCached
operator|+=
name|files
expr_stmt|;
block|}
DECL|method|getBytesNeeded ()
specifier|public
name|long
name|getBytesNeeded
parameter_list|()
block|{
return|return
name|bytesNeeded
return|;
block|}
DECL|method|getBytesCached ()
specifier|public
name|long
name|getBytesCached
parameter_list|()
block|{
return|return
name|bytesCached
return|;
block|}
DECL|method|getBytesOverlimit ()
specifier|public
name|long
name|getBytesOverlimit
parameter_list|()
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|bytesNeeded
operator|-
name|limit
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|getFilesNeeded ()
specifier|public
name|long
name|getFilesNeeded
parameter_list|()
block|{
return|return
name|filesNeeded
return|;
block|}
DECL|method|getFilesCached ()
specifier|public
name|long
name|getFilesCached
parameter_list|()
block|{
return|return
name|filesCached
return|;
block|}
comment|/**    * Get statistics about this CachePool.    *    * @return   Cache pool statistics.    */
DECL|method|getStats ()
specifier|private
name|CachePoolStats
name|getStats
parameter_list|()
block|{
return|return
operator|new
name|CachePoolStats
operator|.
name|Builder
argument_list|()
operator|.
name|setBytesNeeded
argument_list|(
name|bytesNeeded
argument_list|)
operator|.
name|setBytesCached
argument_list|(
name|bytesCached
argument_list|)
operator|.
name|setBytesOverlimit
argument_list|(
name|getBytesOverlimit
argument_list|()
argument_list|)
operator|.
name|setFilesNeeded
argument_list|(
name|filesNeeded
argument_list|)
operator|.
name|setFilesCached
argument_list|(
name|filesCached
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Returns a CachePoolInfo describing this CachePool based on the permissions    * of the calling user. Unprivileged users will see only minimal descriptive    * information about the pool.    *     * @param pc Permission checker to be used to validate the user's permissions,    *          or null    * @return CachePoolEntry describing this CachePool    */
DECL|method|getEntry (FSPermissionChecker pc)
specifier|public
name|CachePoolEntry
name|getEntry
parameter_list|(
name|FSPermissionChecker
name|pc
parameter_list|)
block|{
name|boolean
name|hasPermission
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|pc
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|pc
operator|.
name|checkPermission
argument_list|(
name|this
argument_list|,
name|FsAction
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
name|hasPermission
operator|=
literal|false
expr_stmt|;
block|}
block|}
return|return
operator|new
name|CachePoolEntry
argument_list|(
name|getInfo
argument_list|(
name|hasPermission
argument_list|)
argument_list|,
name|hasPermission
condition|?
name|getStats
argument_list|()
else|:
operator|new
name|CachePoolStats
operator|.
name|Builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
literal|"{ "
argument_list|)
operator|.
name|append
argument_list|(
literal|"poolName:"
argument_list|)
operator|.
name|append
argument_list|(
name|poolName
argument_list|)
operator|.
name|append
argument_list|(
literal|", ownerName:"
argument_list|)
operator|.
name|append
argument_list|(
name|ownerName
argument_list|)
operator|.
name|append
argument_list|(
literal|", groupName:"
argument_list|)
operator|.
name|append
argument_list|(
name|groupName
argument_list|)
operator|.
name|append
argument_list|(
literal|", mode:"
argument_list|)
operator|.
name|append
argument_list|(
name|mode
argument_list|)
operator|.
name|append
argument_list|(
literal|", limit:"
argument_list|)
operator|.
name|append
argument_list|(
name|limit
argument_list|)
operator|.
name|append
argument_list|(
literal|", maxRelativeExpiryMs:"
argument_list|)
operator|.
name|append
argument_list|(
name|maxRelativeExpiryMs
argument_list|)
operator|.
name|append
argument_list|(
literal|" }"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getDirectiveList ()
specifier|public
name|DirectiveList
name|getDirectiveList
parameter_list|()
block|{
return|return
name|directiveList
return|;
block|}
block|}
end_class

end_unit

