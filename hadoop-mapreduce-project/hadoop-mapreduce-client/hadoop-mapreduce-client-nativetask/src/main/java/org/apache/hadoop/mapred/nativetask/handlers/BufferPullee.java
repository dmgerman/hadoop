begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.handlers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
operator|.
name|handlers
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
name|mapred
operator|.
name|RawKeyValueIterator
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
name|mapred
operator|.
name|nativetask
operator|.
name|Constants
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
name|mapred
operator|.
name|nativetask
operator|.
name|NativeDataTarget
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
name|mapred
operator|.
name|nativetask
operator|.
name|buffer
operator|.
name|ByteBufferDataWriter
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
name|mapred
operator|.
name|nativetask
operator|.
name|buffer
operator|.
name|OutputBuffer
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
name|mapred
operator|.
name|nativetask
operator|.
name|serde
operator|.
name|KVSerializer
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
name|mapred
operator|.
name|nativetask
operator|.
name|util
operator|.
name|SizedWritable
import|;
end_import

begin_comment
comment|/**  * load data into a buffer signaled by a {@link BufferPuller}  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BufferPullee
specifier|public
class|class
name|BufferPullee
parameter_list|<
name|IK
parameter_list|,
name|IV
parameter_list|>
implements|implements
name|IDataLoader
block|{
DECL|field|KV_HEADER_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|KV_HEADER_LENGTH
init|=
name|Constants
operator|.
name|SIZEOF_KV_LENGTH
decl_stmt|;
DECL|field|tmpInputKey
specifier|private
specifier|final
name|SizedWritable
argument_list|<
name|IK
argument_list|>
name|tmpInputKey
decl_stmt|;
DECL|field|tmpInputValue
specifier|private
specifier|final
name|SizedWritable
argument_list|<
name|IV
argument_list|>
name|tmpInputValue
decl_stmt|;
DECL|field|inputKVBufferd
specifier|private
name|boolean
name|inputKVBufferd
init|=
literal|false
decl_stmt|;
DECL|field|rIter
specifier|private
name|RawKeyValueIterator
name|rIter
decl_stmt|;
DECL|field|nativeWriter
specifier|private
name|ByteBufferDataWriter
name|nativeWriter
decl_stmt|;
DECL|field|serializer
specifier|protected
name|KVSerializer
argument_list|<
name|IK
argument_list|,
name|IV
argument_list|>
name|serializer
decl_stmt|;
DECL|field|outputBuffer
specifier|private
specifier|final
name|OutputBuffer
name|outputBuffer
decl_stmt|;
DECL|field|target
specifier|private
specifier|final
name|NativeDataTarget
name|target
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|method|BufferPullee (Class<IK> iKClass, Class<IV> iVClass, RawKeyValueIterator rIter, NativeDataTarget target)
specifier|public
name|BufferPullee
parameter_list|(
name|Class
argument_list|<
name|IK
argument_list|>
name|iKClass
parameter_list|,
name|Class
argument_list|<
name|IV
argument_list|>
name|iVClass
parameter_list|,
name|RawKeyValueIterator
name|rIter
parameter_list|,
name|NativeDataTarget
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|rIter
operator|=
name|rIter
expr_stmt|;
name|tmpInputKey
operator|=
operator|new
name|SizedWritable
argument_list|<
name|IK
argument_list|>
argument_list|(
name|iKClass
argument_list|)
expr_stmt|;
name|tmpInputValue
operator|=
operator|new
name|SizedWritable
argument_list|<
name|IV
argument_list|>
argument_list|(
name|iVClass
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|iKClass
operator|&&
literal|null
operator|!=
name|iVClass
condition|)
block|{
name|this
operator|.
name|serializer
operator|=
operator|new
name|KVSerializer
argument_list|<
name|IK
argument_list|,
name|IV
argument_list|>
argument_list|(
name|iKClass
argument_list|,
name|iVClass
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|outputBuffer
operator|=
name|target
operator|.
name|getOutputBuffer
argument_list|()
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load ()
specifier|public
name|int
name|load
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
literal|null
operator|==
name|outputBuffer
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"output buffer not set"
argument_list|)
throw|;
block|}
name|this
operator|.
name|nativeWriter
operator|=
operator|new
name|ByteBufferDataWriter
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|outputBuffer
operator|.
name|rewind
argument_list|()
expr_stmt|;
name|int
name|written
init|=
literal|0
decl_stmt|;
name|boolean
name|firstKV
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|inputKVBufferd
condition|)
block|{
name|written
operator|+=
name|serializer
operator|.
name|serializeKV
argument_list|(
name|nativeWriter
argument_list|,
name|tmpInputKey
argument_list|,
name|tmpInputValue
argument_list|)
expr_stmt|;
name|inputKVBufferd
operator|=
literal|false
expr_stmt|;
name|firstKV
operator|=
literal|false
expr_stmt|;
block|}
while|while
condition|(
name|rIter
operator|.
name|next
argument_list|()
condition|)
block|{
name|inputKVBufferd
operator|=
literal|false
expr_stmt|;
name|tmpInputKey
operator|.
name|readFields
argument_list|(
name|rIter
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|tmpInputValue
operator|.
name|readFields
argument_list|(
name|rIter
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|updateLength
argument_list|(
name|tmpInputKey
argument_list|,
name|tmpInputValue
argument_list|)
expr_stmt|;
specifier|final
name|int
name|kvSize
init|=
name|tmpInputKey
operator|.
name|length
operator|+
name|tmpInputValue
operator|.
name|length
operator|+
name|KV_HEADER_LENGTH
decl_stmt|;
if|if
condition|(
operator|!
name|firstKV
operator|&&
name|nativeWriter
operator|.
name|shortOfSpace
argument_list|(
name|kvSize
argument_list|)
condition|)
block|{
name|inputKVBufferd
operator|=
literal|true
expr_stmt|;
break|break;
block|}
else|else
block|{
name|written
operator|+=
name|serializer
operator|.
name|serializeKV
argument_list|(
name|nativeWriter
argument_list|,
name|tmpInputKey
argument_list|,
name|tmpInputValue
argument_list|)
expr_stmt|;
name|firstKV
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
name|nativeWriter
operator|.
name|hasUnFlushedData
argument_list|()
condition|)
block|{
name|nativeWriter
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
return|return
name|written
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return;
block|}
if|if
condition|(
literal|null
operator|!=
name|rIter
condition|)
block|{
name|rIter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|nativeWriter
condition|)
block|{
name|nativeWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
end_class

end_unit

