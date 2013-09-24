begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|ByteBufferPool
specifier|public
interface|interface
name|ByteBufferPool
block|{
comment|/**    * Get a new direct ByteBuffer.  The pool can provide this from    * removing a buffer from its internal cache, or by allocating a     * new buffer.    *    * @param direct     Whether the buffer should be direct.    * @param minLength  The minimum length the buffer will have.    * @return           A new ByteBuffer.  This ByteBuffer must be direct.    *                   Its capacity can be less than what was requested, but    *                   must be at least 1 byte.    */
DECL|method|getBuffer (boolean direct, int length)
name|ByteBuffer
name|getBuffer
parameter_list|(
name|boolean
name|direct
parameter_list|,
name|int
name|length
parameter_list|)
function_decl|;
comment|/**    * Release a buffer back to the pool.    * The pool may choose to put this buffer into its cache.    *    * @param buffer    a direct bytebuffer    */
DECL|method|putBuffer (ByteBuffer buffer)
name|void
name|putBuffer
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

