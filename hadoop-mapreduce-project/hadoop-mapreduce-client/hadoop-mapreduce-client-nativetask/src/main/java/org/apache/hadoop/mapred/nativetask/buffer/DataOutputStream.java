begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.buffer
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
name|buffer
package|;
end_package

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
name|java
operator|.
name|io
operator|.
name|OutputStream
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DataOutputStream
specifier|public
specifier|abstract
class|class
name|DataOutputStream
extends|extends
name|OutputStream
implements|implements
name|DataOutput
block|{
comment|/**    * Check whether this buffer has enough space to store length of bytes    *     * @param length length of bytes    */
DECL|method|shortOfSpace (int length)
specifier|public
specifier|abstract
name|boolean
name|shortOfSpace
parameter_list|(
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Check whether there is unflushed data stored in the stream    */
DECL|method|hasUnFlushedData ()
specifier|public
specifier|abstract
name|boolean
name|hasUnFlushedData
parameter_list|()
function_decl|;
block|}
end_class

end_unit

