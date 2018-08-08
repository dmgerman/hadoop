begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

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

begin_comment
comment|/**  * Opaque, serializable reference to an entity in the FileSystem. May contain  * metadata sufficient to resolve or verify subsequent accesses independent of  * other modifications to the FileSystem.  */
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
annotation|@
name|FunctionalInterface
DECL|interface|PathHandle
specifier|public
interface|interface
name|PathHandle
extends|extends
name|Serializable
block|{
comment|/**    * @return Serialized form in bytes.    */
DECL|method|toByteArray ()
specifier|default
name|byte
index|[]
name|toByteArray
parameter_list|()
block|{
name|ByteBuffer
name|bb
init|=
name|bytes
argument_list|()
decl_stmt|;
name|byte
index|[]
name|ret
init|=
operator|new
name|byte
index|[
name|bb
operator|.
name|remaining
argument_list|()
index|]
decl_stmt|;
name|bb
operator|.
name|get
argument_list|(
name|ret
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
comment|/**    * Get the bytes of this path handle.    * @return the bytes to get to the process completing the upload.    */
DECL|method|bytes ()
name|ByteBuffer
name|bytes
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|equals (Object other)
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

