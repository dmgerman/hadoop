begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|api
operator|.
name|records
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

begin_interface
DECL|interface|MasterKey
specifier|public
interface|interface
name|MasterKey
block|{
DECL|method|getKeyId ()
name|int
name|getKeyId
parameter_list|()
function_decl|;
DECL|method|setKeyId (int keyId)
name|void
name|setKeyId
parameter_list|(
name|int
name|keyId
parameter_list|)
function_decl|;
DECL|method|getBytes ()
name|ByteBuffer
name|getBytes
parameter_list|()
function_decl|;
DECL|method|setBytes (ByteBuffer bytes)
name|void
name|setBytes
parameter_list|(
name|ByteBuffer
name|bytes
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

