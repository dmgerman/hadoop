begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock.jscsiHelper.cache
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
operator|.
name|jscsiHelper
operator|.
name|cache
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

begin_comment
comment|/**  * Logical Block is the data structure that we write to the cache,  * the key and data gets written to remote contianers. Rest is used for  * book keeping for the cache.  */
end_comment

begin_interface
DECL|interface|LogicalBlock
specifier|public
interface|interface
name|LogicalBlock
block|{
comment|/**    * Returns the data stream of this block.    * @return - ByteBuffer    */
DECL|method|getData ()
name|ByteBuffer
name|getData
parameter_list|()
function_decl|;
comment|/**    * Frees the byte buffer since we don't need it any more.    */
DECL|method|clearData ()
name|void
name|clearData
parameter_list|()
function_decl|;
comment|/**    * Returns the Block ID for this Block.    * @return long - BlockID    */
DECL|method|getBlockID ()
name|long
name|getBlockID
parameter_list|()
function_decl|;
comment|/**    * Flag that tells us if this block has been persisted to container.    * @return whether this block is now persistent    */
DECL|method|isPersisted ()
name|boolean
name|isPersisted
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

