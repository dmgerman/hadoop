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
name|OutputCollector
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
name|serde
operator|.
name|IKVSerializer
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * actively push data into a buffer and signal a {@link BufferPushee} to collect it  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BufferPusher
specifier|public
class|class
name|BufferPusher
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
implements|implements
name|OutputCollector
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BufferPusher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|tmpInputKey
specifier|private
specifier|final
name|SizedWritable
argument_list|<
name|K
argument_list|>
name|tmpInputKey
decl_stmt|;
DECL|field|tmpInputValue
specifier|private
specifier|final
name|SizedWritable
argument_list|<
name|V
argument_list|>
name|tmpInputValue
decl_stmt|;
DECL|field|out
specifier|private
name|ByteBufferDataWriter
name|out
decl_stmt|;
DECL|field|serializer
name|IKVSerializer
name|serializer
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|method|BufferPusher (Class<K> iKClass, Class<V> iVClass, NativeDataTarget target)
specifier|public
name|BufferPusher
parameter_list|(
name|Class
argument_list|<
name|K
argument_list|>
name|iKClass
parameter_list|,
name|Class
argument_list|<
name|V
argument_list|>
name|iVClass
parameter_list|,
name|NativeDataTarget
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|tmpInputKey
operator|=
operator|new
name|SizedWritable
argument_list|<
name|K
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
name|V
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
name|K
argument_list|,
name|V
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
name|out
operator|=
operator|new
name|ByteBufferDataWriter
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
DECL|method|collect (K key, V value, int partition)
specifier|public
name|void
name|collect
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|,
name|int
name|partition
parameter_list|)
throws|throws
name|IOException
block|{
name|tmpInputKey
operator|.
name|reset
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|tmpInputValue
operator|.
name|reset
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|serializePartitionKV
argument_list|(
name|out
argument_list|,
name|partition
argument_list|,
name|tmpInputKey
argument_list|,
name|tmpInputValue
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
annotation|@
name|Override
DECL|method|collect (K key, V value)
specifier|public
name|void
name|collect
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
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
name|tmpInputKey
operator|.
name|reset
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|tmpInputValue
operator|.
name|reset
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|serializeKV
argument_list|(
name|out
argument_list|,
name|tmpInputKey
argument_list|,
name|tmpInputValue
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
literal|null
operator|!=
name|out
condition|)
block|{
if|if
condition|(
name|out
operator|.
name|hasUnFlushedData
argument_list|()
condition|)
block|{
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
block|}
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
name|out
condition|)
block|{
name|out
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

