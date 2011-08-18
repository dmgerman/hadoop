begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.streaming.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|streaming
operator|.
name|io
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
name|streaming
operator|.
name|PipeMapRed
import|;
end_import

begin_comment
comment|/**  * Abstract base for classes that write the client's input.  */
end_comment

begin_class
DECL|class|InputWriter
specifier|public
specifier|abstract
class|class
name|InputWriter
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
comment|/**    * Initializes the InputWriter. This method has to be called before calling    * any of the other methods.    */
DECL|method|initialize (PipeMapRed pipeMapRed)
specifier|public
name|void
name|initialize
parameter_list|(
name|PipeMapRed
name|pipeMapRed
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nothing here yet, but that might change in the future
block|}
comment|/**    * Writes an input key.    */
DECL|method|writeKey (K key)
specifier|public
specifier|abstract
name|void
name|writeKey
parameter_list|(
name|K
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Writes an input value.    */
DECL|method|writeValue (V value)
specifier|public
specifier|abstract
name|void
name|writeValue
parameter_list|(
name|V
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

