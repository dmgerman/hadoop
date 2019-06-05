begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset.impl
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
name|datanode
operator|.
name|fsdataset
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|MappedByteBuffer
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
name|nativeio
operator|.
name|NativeIO
import|;
end_import

begin_comment
comment|/**  * Represents an HDFS block that is mapped to memory by the DataNode.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|MemoryMappedBlock
specifier|public
class|class
name|MemoryMappedBlock
implements|implements
name|MappableBlock
block|{
DECL|field|mmap
specifier|private
name|MappedByteBuffer
name|mmap
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|method|MemoryMappedBlock (MappedByteBuffer mmap, long length)
name|MemoryMappedBlock
parameter_list|(
name|MappedByteBuffer
name|mmap
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|mmap
operator|=
name|mmap
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
assert|assert
name|length
operator|>
literal|0
assert|;
block|}
annotation|@
name|Override
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|getAddress ()
specifier|public
name|long
name|getAddress
parameter_list|()
block|{
return|return
operator|-
literal|1L
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|mmap
operator|!=
literal|null
condition|)
block|{
name|NativeIO
operator|.
name|POSIX
operator|.
name|munmap
argument_list|(
name|mmap
argument_list|)
expr_stmt|;
name|mmap
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

