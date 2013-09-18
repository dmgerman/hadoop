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
comment|/**  * An exception which occurred when trying to remove a PathBasedCache entry.  */
end_comment

begin_class
DECL|class|RemovePathBasedCacheDescriptorException
specifier|public
specifier|abstract
class|class
name|RemovePathBasedCacheDescriptorException
extends|extends
name|IOException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|entryId
specifier|private
specifier|final
name|long
name|entryId
decl_stmt|;
DECL|method|RemovePathBasedCacheDescriptorException (String description, long entryId)
specifier|public
name|RemovePathBasedCacheDescriptorException
parameter_list|(
name|String
name|description
parameter_list|,
name|long
name|entryId
parameter_list|)
block|{
name|super
argument_list|(
name|description
argument_list|)
expr_stmt|;
name|this
operator|.
name|entryId
operator|=
name|entryId
expr_stmt|;
block|}
DECL|method|getEntryId ()
specifier|public
name|long
name|getEntryId
parameter_list|()
block|{
return|return
name|this
operator|.
name|entryId
return|;
block|}
DECL|class|InvalidIdException
specifier|public
specifier|final
specifier|static
class|class
name|InvalidIdException
extends|extends
name|RemovePathBasedCacheDescriptorException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|InvalidIdException (long entryId)
specifier|public
name|InvalidIdException
parameter_list|(
name|long
name|entryId
parameter_list|)
block|{
name|super
argument_list|(
literal|"invalid PathBasedCacheDescriptor id "
operator|+
name|entryId
argument_list|,
name|entryId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|RemovePermissionDeniedException
specifier|public
specifier|final
specifier|static
class|class
name|RemovePermissionDeniedException
extends|extends
name|RemovePathBasedCacheDescriptorException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|RemovePermissionDeniedException (long entryId)
specifier|public
name|RemovePermissionDeniedException
parameter_list|(
name|long
name|entryId
parameter_list|)
block|{
name|super
argument_list|(
literal|"permission denied when trying to remove "
operator|+
literal|"PathBasedCacheDescriptor id "
operator|+
name|entryId
argument_list|,
name|entryId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|NoSuchIdException
specifier|public
specifier|final
specifier|static
class|class
name|NoSuchIdException
extends|extends
name|RemovePathBasedCacheDescriptorException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|NoSuchIdException (long entryId)
specifier|public
name|NoSuchIdException
parameter_list|(
name|long
name|entryId
parameter_list|)
block|{
name|super
argument_list|(
literal|"there is no PathBasedCacheDescriptor with id "
operator|+
name|entryId
argument_list|,
name|entryId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|UnexpectedRemovePathBasedCacheDescriptorException
specifier|public
specifier|final
specifier|static
class|class
name|UnexpectedRemovePathBasedCacheDescriptorException
extends|extends
name|RemovePathBasedCacheDescriptorException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|UnexpectedRemovePathBasedCacheDescriptorException (long id)
specifier|public
name|UnexpectedRemovePathBasedCacheDescriptorException
parameter_list|(
name|long
name|id
parameter_list|)
block|{
name|super
argument_list|(
literal|"encountered an unexpected error when trying to "
operator|+
literal|"remove PathBasedCacheDescriptor with id "
operator|+
name|id
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

