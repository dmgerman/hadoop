begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocol
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|DatanodeDetailsProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerBlocksDeletionACKProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerBlocksDeletionACKResponseProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsRequestProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsResponseProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMHeartbeatResponseProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMNodeReport
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMRegisteredCmdResponseProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMVersionRequestProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMVersionResponseProto
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

begin_comment
comment|/**  * The protocol spoken between datanodes and SCM. For specifics please the  * Protoc file that defines this protocol.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|StorageContainerDatanodeProtocol
specifier|public
interface|interface
name|StorageContainerDatanodeProtocol
block|{
comment|/**    * Returns SCM version.    * @return Version info.    */
DECL|method|getVersion (SCMVersionRequestProto versionRequest)
name|SCMVersionResponseProto
name|getVersion
parameter_list|(
name|SCMVersionRequestProto
name|versionRequest
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Used by data node to send a Heartbeat.    * @param datanodeDetails - Datanode Details.    * @param nodeReport - node report state    * @return - SCMHeartbeatResponseProto    * @throws IOException    */
DECL|method|sendHeartbeat (DatanodeDetailsProto datanodeDetails, SCMNodeReport nodeReport)
name|SCMHeartbeatResponseProto
name|sendHeartbeat
parameter_list|(
name|DatanodeDetailsProto
name|datanodeDetails
parameter_list|,
name|SCMNodeReport
name|nodeReport
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Register Datanode.    * @param datanodeDetails - Datanode Details.    * @param nodeReport - Node Report.    * @param containerReportsRequestProto - Container Reports.    * @return SCM Command.    */
DECL|method|register (DatanodeDetailsProto datanodeDetails, SCMNodeReport nodeReport, ContainerReportsRequestProto containerReportsRequestProto)
name|SCMRegisteredCmdResponseProto
name|register
parameter_list|(
name|DatanodeDetailsProto
name|datanodeDetails
parameter_list|,
name|SCMNodeReport
name|nodeReport
parameter_list|,
name|ContainerReportsRequestProto
name|containerReportsRequestProto
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Send a container report.    * @param reports -- Container report.    * @return container reports response.    * @throws IOException    */
DECL|method|sendContainerReport ( ContainerReportsRequestProto reports)
name|ContainerReportsResponseProto
name|sendContainerReport
parameter_list|(
name|ContainerReportsRequestProto
name|reports
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Used by datanode to send block deletion ACK to SCM.    * @param request block deletion transactions.    * @return block deletion transaction response.    * @throws IOException    */
DECL|method|sendContainerBlocksDeletionACK ( ContainerBlocksDeletionACKProto request)
name|ContainerBlocksDeletionACKResponseProto
name|sendContainerBlocksDeletionACK
parameter_list|(
name|ContainerBlocksDeletionACKProto
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

