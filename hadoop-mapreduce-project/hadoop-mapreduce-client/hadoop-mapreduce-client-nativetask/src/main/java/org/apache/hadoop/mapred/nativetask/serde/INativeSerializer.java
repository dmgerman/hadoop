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

begin_comment
comment|/**  * an INativeSerializer serializes and deserializes data transferred between  * Java and native. {@link DefaultSerializer} provides default implementations.  *  * Note: if you implemented your customized NativeSerializer instead of DefaultSerializer,  * you have to make sure the native side can serialize it correctly.  *   */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|INativeSerializer
specifier|public
interface|interface
name|INativeSerializer
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * get length of data to be serialized. If the data length is already known (like IntWritable)    * and could immediately be returned from this method, it is good chance to implement customized    * NativeSerializer for efficiency    */
DECL|method|getLength (T w)
specifier|public
name|int
name|getLength
parameter_list|(
name|T
name|w
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|serialize (T w, DataOutput out)
specifier|public
name|void
name|serialize
parameter_list|(
name|T
name|w
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|deserialize (DataInput in, int length, T w)
specifier|public
name|void
name|deserialize
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|int
name|length
parameter_list|,
name|T
name|w
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

