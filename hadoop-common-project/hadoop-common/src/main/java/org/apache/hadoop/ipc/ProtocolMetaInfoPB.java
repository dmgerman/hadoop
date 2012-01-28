begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
operator|.
name|protobuf
operator|.
name|ProtocolInfoProtos
operator|.
name|ProtocolInfoService
import|;
end_import

begin_comment
comment|/**  * Protocol to get versions and signatures for supported protocols from the  * server.  *   * Note: This extends the protocolbuffer service based interface to  * add annotations.  */
end_comment

begin_interface
annotation|@
name|ProtocolInfo
argument_list|(
name|protocolName
operator|=
literal|"org.apache.hadoop.ipc.ProtocolMetaInfoPB"
argument_list|,
name|protocolVersion
operator|=
literal|1
argument_list|)
DECL|interface|ProtocolMetaInfoPB
specifier|public
interface|interface
name|ProtocolMetaInfoPB
extends|extends
name|ProtocolInfoService
operator|.
name|BlockingInterface
block|{ }
end_interface

end_unit

