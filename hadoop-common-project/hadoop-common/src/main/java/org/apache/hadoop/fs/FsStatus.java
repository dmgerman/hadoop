begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|io
operator|.
name|Writable
import|;
end_import

begin_comment
comment|/** This class is used to represent the capacity, free and used space on a   * {@link FileSystem}.   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|FsStatus
specifier|public
class|class
name|FsStatus
implements|implements
name|Writable
block|{
DECL|field|capacity
specifier|private
name|long
name|capacity
decl_stmt|;
DECL|field|used
specifier|private
name|long
name|used
decl_stmt|;
DECL|field|remaining
specifier|private
name|long
name|remaining
decl_stmt|;
comment|/** Construct a FsStatus object, using the specified statistics */
DECL|method|FsStatus (long capacity, long used, long remaining)
specifier|public
name|FsStatus
parameter_list|(
name|long
name|capacity
parameter_list|,
name|long
name|used
parameter_list|,
name|long
name|remaining
parameter_list|)
block|{
name|this
operator|.
name|capacity
operator|=
name|capacity
expr_stmt|;
name|this
operator|.
name|used
operator|=
name|used
expr_stmt|;
name|this
operator|.
name|remaining
operator|=
name|remaining
expr_stmt|;
block|}
comment|/** Return the capacity in bytes of the file system */
DECL|method|getCapacity ()
specifier|public
name|long
name|getCapacity
parameter_list|()
block|{
return|return
name|capacity
return|;
block|}
comment|/** Return the number of bytes used on the file system */
DECL|method|getUsed ()
specifier|public
name|long
name|getUsed
parameter_list|()
block|{
return|return
name|used
return|;
block|}
comment|/** Return the number of remaining bytes on the file system */
DECL|method|getRemaining ()
specifier|public
name|long
name|getRemaining
parameter_list|()
block|{
return|return
name|remaining
return|;
block|}
comment|//////////////////////////////////////////////////
comment|// Writable
comment|//////////////////////////////////////////////////
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|used
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|remaining
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|capacity
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|used
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|remaining
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

