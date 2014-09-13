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

begin_comment
comment|/**  * serializes key-value pair  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|IKVSerializer
specifier|public
interface|interface
name|IKVSerializer
block|{
comment|/**    * update the length field of SizedWritable    */
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
function_decl|;
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
function_decl|;
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
function_decl|;
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
function_decl|;
block|}
end_interface

end_unit

