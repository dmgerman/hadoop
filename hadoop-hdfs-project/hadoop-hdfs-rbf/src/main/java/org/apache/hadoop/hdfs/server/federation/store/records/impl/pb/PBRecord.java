begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.records.impl.pb
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
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
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|Message
import|;
end_import

begin_comment
comment|/**  * A record implementation using Protobuf.  */
end_comment

begin_interface
DECL|interface|PBRecord
specifier|public
interface|interface
name|PBRecord
block|{
comment|/**    * Get the protocol for the record.    * @return The protocol for this record.    */
DECL|method|getProto ()
name|Message
name|getProto
parameter_list|()
function_decl|;
comment|/**    * Set the protocol for the record.    * @param proto Protocol for this record.    */
DECL|method|setProto (Message proto)
name|void
name|setProto
parameter_list|(
name|Message
name|proto
parameter_list|)
function_decl|;
comment|/**    * Populate this record with serialized data.    * @param base64String Serialized data in base64.    * @throws IOException If it cannot read the data.    */
DECL|method|readInstance (String base64String)
name|void
name|readInstance
parameter_list|(
name|String
name|base64String
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

