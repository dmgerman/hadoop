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
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|io
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  * Information about a cache pool.  */
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
DECL|class|CachePoolInfo
specifier|public
class|class
name|CachePoolInfo
block|{
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
DECL|field|weight
name|Integer
name|weight
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
DECL|method|getWeight ()
specifier|public
name|Integer
name|getWeight
parameter_list|()
block|{
return|return
name|weight
return|;
block|}
DECL|method|setWeight (Integer weight)
specifier|public
name|CachePoolInfo
name|setWeight
parameter_list|(
name|Integer
name|weight
parameter_list|)
block|{
name|this
operator|.
name|weight
operator|=
name|weight
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
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|", weight:"
argument_list|)
operator|.
name|append
argument_list|(
name|weight
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
try|try
block|{
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
name|weight
argument_list|,
name|other
operator|.
name|weight
argument_list|)
operator|.
name|isEquals
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
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
name|weight
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
name|IOException
argument_list|(
literal|"CachePoolInfo is null"
argument_list|)
throw|;
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
DECL|method|readFrom (DataInput in)
specifier|public
specifier|static
name|CachePoolInfo
name|readFrom
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|poolName
init|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
decl_stmt|;
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
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|info
operator|.
name|setOwnerName
argument_list|(
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|info
operator|.
name|setGroupName
argument_list|(
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|info
operator|.
name|setMode
argument_list|(
name|FsPermission
operator|.
name|read
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|info
operator|.
name|setWeight
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|info
return|;
block|}
DECL|method|writeTo (DataOutput out)
specifier|public
name|void
name|writeTo
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|poolName
argument_list|)
expr_stmt|;
name|boolean
name|hasOwner
decl_stmt|,
name|hasGroup
decl_stmt|,
name|hasMode
decl_stmt|,
name|hasWeight
decl_stmt|;
name|hasOwner
operator|=
name|ownerName
operator|!=
literal|null
expr_stmt|;
name|hasGroup
operator|=
name|groupName
operator|!=
literal|null
expr_stmt|;
name|hasMode
operator|=
name|mode
operator|!=
literal|null
expr_stmt|;
name|hasWeight
operator|=
name|weight
operator|!=
literal|null
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|hasOwner
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasOwner
condition|)
block|{
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|ownerName
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|hasGroup
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasGroup
condition|)
block|{
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|groupName
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|hasMode
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasMode
condition|)
block|{
name|mode
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|hasWeight
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasWeight
condition|)
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|weight
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

