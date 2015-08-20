begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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
name|Nullable
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
name|lang
operator|.
name|builder
operator|.
name|EqualsBuilder
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
name|lang
operator|.
name|builder
operator|.
name|HashCodeBuilder
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
name|fs
operator|.
name|InvalidRequestException
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
name|CacheDirectiveInfo
operator|.
name|Expiration
import|;
end_import

begin_comment
comment|/**  * CachePoolInfo describes a cache pool.  *  * This class is used in RPCs to create and modify cache pools.  * It is serializable and can be stored in the edit log.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|CachePoolInfo
specifier|public
class|class
name|CachePoolInfo
block|{
comment|/**    * Indicates that the pool does not have a maximum relative expiry.    */
DECL|field|RELATIVE_EXPIRY_NEVER
specifier|public
specifier|static
specifier|final
name|long
name|RELATIVE_EXPIRY_NEVER
init|=
name|Expiration
operator|.
name|MAX_RELATIVE_EXPIRY_MS
decl_stmt|;
comment|/**    * Default max relative expiry for cache pools.    */
DECL|field|DEFAULT_MAX_RELATIVE_EXPIRY
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_MAX_RELATIVE_EXPIRY
init|=
name|RELATIVE_EXPIRY_NEVER
decl_stmt|;
DECL|field|LIMIT_UNLIMITED
specifier|public
specifier|static
specifier|final
name|long
name|LIMIT_UNLIMITED
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|DEFAULT_LIMIT
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_LIMIT
init|=
name|LIMIT_UNLIMITED
decl_stmt|;
DECL|field|poolName
specifier|final
name|String
name|poolName
decl_stmt|;
annotation|@
name|Nullable
DECL|field|ownerName
name|String
name|ownerName
decl_stmt|;
annotation|@
name|Nullable
DECL|field|groupName
name|String
name|groupName
decl_stmt|;
annotation|@
name|Nullable
DECL|field|mode
name|FsPermission
name|mode
decl_stmt|;
annotation|@
name|Nullable
DECL|field|limit
name|Long
name|limit
decl_stmt|;
annotation|@
name|Nullable
DECL|field|maxRelativeExpiryMs
name|Long
name|maxRelativeExpiryMs
decl_stmt|;
DECL|method|CachePoolInfo (String poolName)
specifier|public
name|CachePoolInfo
parameter_list|(
name|String
name|poolName
parameter_list|)
block|{
name|this
operator|.
name|poolName
operator|=
name|poolName
expr_stmt|;
block|}
comment|/**    * @return Name of the pool.    */
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
comment|/**    * @return The owner of the pool. Along with the group and mode, determines    *         who has access to view and modify the pool.    */
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
name|CachePoolInfo
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
comment|/**    * @return The group of the pool. Along with the owner and mode, determines    *         who has access to view and modify the pool.    */
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
name|CachePoolInfo
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
comment|/**    * @return Unix-style permissions of the pool. Along with the owner and group,    *         determines who has access to view and modify the pool.    */
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
name|CachePoolInfo
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
name|mode
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * @return The maximum aggregate number of bytes that can be cached by    *         directives in this pool.    */
DECL|method|getLimit ()
specifier|public
name|Long
name|getLimit
parameter_list|()
block|{
return|return
name|limit
return|;
block|}
DECL|method|setLimit (Long bytes)
specifier|public
name|CachePoolInfo
name|setLimit
parameter_list|(
name|Long
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
comment|/**    * @return The maximum relative expiration of directives of this pool in    *         milliseconds    */
DECL|method|getMaxRelativeExpiryMs ()
specifier|public
name|Long
name|getMaxRelativeExpiryMs
parameter_list|()
block|{
return|return
name|maxRelativeExpiryMs
return|;
block|}
comment|/**    * Set the maximum relative expiration of directives of this pool in    * milliseconds.    *     * @param ms in milliseconds    * @return This builder, for call chaining.    */
DECL|method|setMaxRelativeExpiryMs (Long ms)
specifier|public
name|CachePoolInfo
name|setMaxRelativeExpiryMs
parameter_list|(
name|Long
name|ms
parameter_list|)
block|{
name|this
operator|.
name|maxRelativeExpiryMs
operator|=
name|ms
expr_stmt|;
return|return
name|this
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
literal|"{"
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
operator|(
name|mode
operator|==
literal|null
operator|)
condition|?
literal|"null"
else|:
name|String
operator|.
name|format
argument_list|(
literal|"0%03o"
argument_list|,
name|mode
operator|.
name|toShort
argument_list|()
argument_list|)
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
literal|"}"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|o
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|CachePoolInfo
name|other
init|=
operator|(
name|CachePoolInfo
operator|)
name|o
decl_stmt|;
return|return
operator|new
name|EqualsBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|poolName
argument_list|,
name|other
operator|.
name|poolName
argument_list|)
operator|.
name|append
argument_list|(
name|ownerName
argument_list|,
name|other
operator|.
name|ownerName
argument_list|)
operator|.
name|append
argument_list|(
name|groupName
argument_list|,
name|other
operator|.
name|groupName
argument_list|)
operator|.
name|append
argument_list|(
name|mode
argument_list|,
name|other
operator|.
name|mode
argument_list|)
operator|.
name|append
argument_list|(
name|limit
argument_list|,
name|other
operator|.
name|limit
argument_list|)
operator|.
name|append
argument_list|(
name|maxRelativeExpiryMs
argument_list|,
name|other
operator|.
name|maxRelativeExpiryMs
argument_list|)
operator|.
name|isEquals
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|new
name|HashCodeBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|poolName
argument_list|)
operator|.
name|append
argument_list|(
name|ownerName
argument_list|)
operator|.
name|append
argument_list|(
name|groupName
argument_list|)
operator|.
name|append
argument_list|(
name|mode
argument_list|)
operator|.
name|append
argument_list|(
name|limit
argument_list|)
operator|.
name|append
argument_list|(
name|maxRelativeExpiryMs
argument_list|)
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|validate (CachePoolInfo info)
specifier|public
specifier|static
name|void
name|validate
parameter_list|(
name|CachePoolInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidRequestException
argument_list|(
literal|"CachePoolInfo is null"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|info
operator|.
name|getLimit
argument_list|()
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|info
operator|.
name|getLimit
argument_list|()
operator|<
literal|0
operator|)
condition|)
block|{
throw|throw
operator|new
name|InvalidRequestException
argument_list|(
literal|"Limit is negative."
argument_list|)
throw|;
block|}
if|if
condition|(
name|info
operator|.
name|getMaxRelativeExpiryMs
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|long
name|maxRelativeExpiryMs
init|=
name|info
operator|.
name|getMaxRelativeExpiryMs
argument_list|()
decl_stmt|;
if|if
condition|(
name|maxRelativeExpiryMs
operator|<
literal|0l
condition|)
block|{
throw|throw
operator|new
name|InvalidRequestException
argument_list|(
literal|"Max relative expiry is negative."
argument_list|)
throw|;
block|}
if|if
condition|(
name|maxRelativeExpiryMs
operator|>
name|Expiration
operator|.
name|MAX_RELATIVE_EXPIRY_MS
condition|)
block|{
throw|throw
operator|new
name|InvalidRequestException
argument_list|(
literal|"Max relative expiry is too big."
argument_list|)
throw|;
block|}
block|}
name|validateName
argument_list|(
name|info
operator|.
name|poolName
argument_list|)
expr_stmt|;
block|}
DECL|method|validateName (String poolName)
specifier|public
specifier|static
name|void
name|validateName
parameter_list|(
name|String
name|poolName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|poolName
operator|==
literal|null
operator|||
name|poolName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Empty pool names are not allowed because they would be highly
comment|// confusing.  They would also break the ability to list all pools
comment|// by starting with prevKey = ""
throw|throw
operator|new
name|IOException
argument_list|(
literal|"invalid empty cache pool name"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

