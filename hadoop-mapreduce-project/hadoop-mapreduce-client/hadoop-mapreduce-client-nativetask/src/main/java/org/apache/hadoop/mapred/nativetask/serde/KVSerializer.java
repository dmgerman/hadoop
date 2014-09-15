begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.serde
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
name|serde
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
name|io
operator|.
name|Writable
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
name|buffer
operator|.
name|DataInputStream
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
name|DataOutputStream
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|KVSerializer
specifier|public
class|class
name|KVSerializer
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
implements|implements
name|IKVSerializer
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|KVSerializer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|KV_HEAD_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|KV_HEAD_LENGTH
init|=
name|Constants
operator|.
name|SIZEOF_KV_LENGTH
decl_stmt|;
DECL|field|keySerializer
specifier|private
specifier|final
name|INativeSerializer
argument_list|<
name|Writable
argument_list|>
name|keySerializer
decl_stmt|;
DECL|field|valueSerializer
specifier|private
specifier|final
name|INativeSerializer
argument_list|<
name|Writable
argument_list|>
name|valueSerializer
decl_stmt|;
DECL|method|KVSerializer (Class<K> kclass, Class<V> vclass)
specifier|public
name|KVSerializer
parameter_list|(
name|Class
argument_list|<
name|K
argument_list|>
name|kclass
parameter_list|,
name|Class
argument_list|<
name|V
argument_list|>
name|vclass
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|keySerializer
operator|=
name|NativeSerialization
operator|.
name|getInstance
argument_list|()
operator|.
name|getSerializer
argument_list|(
name|kclass
argument_list|)
expr_stmt|;
name|this
operator|.
name|valueSerializer
operator|=
name|NativeSerialization
operator|.
name|getInstance
argument_list|()
operator|.
name|getSerializer
argument_list|(
name|vclass
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateLength (SizedWritable<?> key, SizedWritable<?> value)
specifier|public
name|void
name|updateLength
parameter_list|(
name|SizedWritable
argument_list|<
name|?
argument_list|>
name|key
parameter_list|,
name|SizedWritable
argument_list|<
name|?
argument_list|>
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|key
operator|.
name|length
operator|=
name|keySerializer
operator|.
name|getLength
argument_list|(
name|key
operator|.
name|v
argument_list|)
expr_stmt|;
name|value
operator|.
name|length
operator|=
name|valueSerializer
operator|.
name|getLength
argument_list|(
name|value
operator|.
name|v
argument_list|)
expr_stmt|;
return|return;
block|}
annotation|@
name|Override
DECL|method|serializeKV (DataOutputStream out, SizedWritable<?> key, SizedWritable<?> value)
specifier|public
name|int
name|serializeKV
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|SizedWritable
argument_list|<
name|?
argument_list|>
name|key
parameter_list|,
name|SizedWritable
argument_list|<
name|?
argument_list|>
name|value
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|serializePartitionKV
argument_list|(
name|out
argument_list|,
operator|-
literal|1
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|serializePartitionKV (DataOutputStream out, int partitionId, SizedWritable<?> key, SizedWritable<?> value)
specifier|public
name|int
name|serializePartitionKV
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|int
name|partitionId
parameter_list|,
name|SizedWritable
argument_list|<
name|?
argument_list|>
name|key
parameter_list|,
name|SizedWritable
argument_list|<
name|?
argument_list|>
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|key
operator|.
name|length
operator|==
name|SizedWritable
operator|.
name|INVALID_LENGTH
operator|||
name|value
operator|.
name|length
operator|==
name|SizedWritable
operator|.
name|INVALID_LENGTH
condition|)
block|{
name|updateLength
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|keyLength
init|=
name|key
operator|.
name|length
decl_stmt|;
specifier|final
name|int
name|valueLength
init|=
name|value
operator|.
name|length
decl_stmt|;
name|int
name|bytesWritten
init|=
name|KV_HEAD_LENGTH
operator|+
name|keyLength
operator|+
name|valueLength
decl_stmt|;
if|if
condition|(
name|partitionId
operator|!=
operator|-
literal|1
condition|)
block|{
name|bytesWritten
operator|+=
name|Constants
operator|.
name|SIZEOF_PARTITION_LENGTH
expr_stmt|;
block|}
if|if
condition|(
name|out
operator|.
name|hasUnFlushedData
argument_list|()
operator|&&
name|out
operator|.
name|shortOfSpace
argument_list|(
name|bytesWritten
argument_list|)
condition|)
block|{
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|partitionId
operator|!=
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|partitionId
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeInt
argument_list|(
name|keyLength
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|valueLength
argument_list|)
expr_stmt|;
name|keySerializer
operator|.
name|serialize
argument_list|(
name|key
operator|.
name|v
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|valueSerializer
operator|.
name|serialize
argument_list|(
name|value
operator|.
name|v
argument_list|,
name|out
argument_list|)
expr_stmt|;
return|return
name|bytesWritten
return|;
block|}
annotation|@
name|Override
DECL|method|deserializeKV (DataInputStream in, SizedWritable<?> key, SizedWritable<?> value)
specifier|public
name|int
name|deserializeKV
parameter_list|(
name|DataInputStream
name|in
parameter_list|,
name|SizedWritable
argument_list|<
name|?
argument_list|>
name|key
parameter_list|,
name|SizedWritable
argument_list|<
name|?
argument_list|>
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|in
operator|.
name|hasUnReadData
argument_list|()
condition|)
block|{
return|return
literal|0
return|;
block|}
name|key
operator|.
name|length
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|value
operator|.
name|length
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|keySerializer
operator|.
name|deserialize
argument_list|(
name|in
argument_list|,
name|key
operator|.
name|length
argument_list|,
name|key
operator|.
name|v
argument_list|)
expr_stmt|;
name|valueSerializer
operator|.
name|deserialize
argument_list|(
name|in
argument_list|,
name|value
operator|.
name|length
argument_list|,
name|value
operator|.
name|v
argument_list|)
expr_stmt|;
return|return
name|key
operator|.
name|length
operator|+
name|value
operator|.
name|length
operator|+
name|KV_HEAD_LENGTH
return|;
block|}
block|}
end_class

end_unit

