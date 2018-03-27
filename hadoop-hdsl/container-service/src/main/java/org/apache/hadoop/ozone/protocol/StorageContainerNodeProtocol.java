begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|hdsl
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdslProtos
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|SCMCommand
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
name|hdsl
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ReportState
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
name|hdsl
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
name|hdsl
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * The protocol spoken between datanodes and SCM.  *  * Please note that the full protocol spoken between a datanode and SCM is  * separated into 2 interfaces. One interface that deals with node state and  * another interface that deals with containers.  *  * This interface has functions that deals with the state of datanode.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|StorageContainerNodeProtocol
specifier|public
interface|interface
name|StorageContainerNodeProtocol
block|{
comment|/**    * Gets the version info from SCM.    * @param versionRequest - version Request.    * @return - returns SCM version info and other required information needed    * by datanode.    */
DECL|method|getVersion (SCMVersionRequestProto versionRequest)
name|VersionResponse
name|getVersion
parameter_list|(
name|SCMVersionRequestProto
name|versionRequest
parameter_list|)
function_decl|;
comment|/**    * Register the node if the node finds that it is not registered with any SCM.    * @param datanodeDetails DatanodeDetails    * @return  SCMHeartbeatResponseProto    */
DECL|method|register (DatanodeDetailsProto datanodeDetails)
name|SCMCommand
name|register
parameter_list|(
name|DatanodeDetailsProto
name|datanodeDetails
parameter_list|)
function_decl|;
comment|/**    * Send heartbeat to indicate the datanode is alive and doing well.    * @param datanodeDetails - Datanode ID.    * @param nodeReport - node report.    * @param reportState - container report.    * @return SCMheartbeat response list    */
DECL|method|sendHeartbeat (DatanodeDetailsProto datanodeDetails, SCMNodeReport nodeReport, ReportState reportState)
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|sendHeartbeat
parameter_list|(
name|DatanodeDetailsProto
name|datanodeDetails
parameter_list|,
name|SCMNodeReport
name|nodeReport
parameter_list|,
name|ReportState
name|reportState
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

