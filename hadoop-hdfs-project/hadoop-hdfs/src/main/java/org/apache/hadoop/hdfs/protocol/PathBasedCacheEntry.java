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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|CachePool
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
comment|/**  * Represents an entry in the PathBasedCache on the NameNode.  *  * This is an implementation class, not part of the public API.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|PathBasedCacheEntry
specifier|public
specifier|final
class|class
name|PathBasedCacheEntry
block|{
DECL|field|entryId
specifier|private
specifier|final
name|long
name|entryId
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|pool
specifier|private
specifier|final
name|CachePool
name|pool
decl_stmt|;
DECL|method|PathBasedCacheEntry (long entryId, String path, CachePool pool)
specifier|public
name|PathBasedCacheEntry
parameter_list|(
name|long
name|entryId
parameter_list|,
name|String
name|path
parameter_list|,
name|CachePool
name|pool
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|entryId
operator|>
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|entryId
operator|=
name|entryId
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
DECL|method|getEntryId ()
specifier|public
name|long
name|getEntryId
parameter_list|()
block|{
return|return
name|entryId
return|;
block|}
DECL|method|getPath ()
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|getPool ()
specifier|public
name|CachePool
name|getPool
parameter_list|()
block|{
return|return
name|pool
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
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"{ entryId:"
argument_list|)
operator|.
name|append
argument_list|(
name|entryId
argument_list|)
operator|.
name|append
argument_list|(
literal|", path:"
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|)
operator|.
name|append
argument_list|(
literal|", pool:"
argument_list|)
operator|.
name|append
argument_list|(
name|pool
argument_list|)
operator|.
name|append
argument_list|(
literal|" }"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getDescriptor ()
specifier|public
name|PathBasedCacheDescriptor
name|getDescriptor
parameter_list|()
block|{
return|return
operator|new
name|PathBasedCacheDescriptor
argument_list|(
name|entryId
argument_list|,
name|path
argument_list|,
name|pool
operator|.
name|getPoolName
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_empty_stmt
empty_stmt|;
end_empty_stmt

end_unit

