begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
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
name|yarn
operator|.
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|CancelUpgradeRequestProto
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
name|yarn
operator|.
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|CancelUpgradeResponseProto
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
name|yarn
operator|.
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|CompInstancesUpgradeResponseProto
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
name|yarn
operator|.
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|CompInstancesUpgradeRequestProto
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
name|yarn
operator|.
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|FlexComponentsRequestProto
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
name|yarn
operator|.
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|FlexComponentsResponseProto
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
name|yarn
operator|.
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|GetCompInstancesRequestProto
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
name|yarn
operator|.
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|GetCompInstancesResponseProto
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
name|yarn
operator|.
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|GetStatusResponseProto
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
name|yarn
operator|.
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|GetStatusRequestProto
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
name|yarn
operator|.
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|RestartServiceRequestProto
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
name|yarn
operator|.
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|RestartServiceResponseProto
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
name|yarn
operator|.
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|StopResponseProto
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
name|yarn
operator|.
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|StopRequestProto
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
name|yarn
operator|.
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|UpgradeServiceRequestProto
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
name|yarn
operator|.
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|UpgradeServiceResponseProto
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

begin_interface
DECL|interface|ClientAMProtocol
specifier|public
interface|interface
name|ClientAMProtocol
block|{
DECL|method|flexComponents (FlexComponentsRequestProto request)
name|FlexComponentsResponseProto
name|flexComponents
parameter_list|(
name|FlexComponentsRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
DECL|method|getStatus (GetStatusRequestProto requestProto)
name|GetStatusResponseProto
name|getStatus
parameter_list|(
name|GetStatusRequestProto
name|requestProto
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
DECL|method|stop (StopRequestProto requestProto)
name|StopResponseProto
name|stop
parameter_list|(
name|StopRequestProto
name|requestProto
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
DECL|method|upgrade (UpgradeServiceRequestProto request)
name|UpgradeServiceResponseProto
name|upgrade
parameter_list|(
name|UpgradeServiceRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
DECL|method|restart (RestartServiceRequestProto request)
name|RestartServiceResponseProto
name|restart
parameter_list|(
name|RestartServiceRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
DECL|method|upgrade ( CompInstancesUpgradeRequestProto request)
name|CompInstancesUpgradeResponseProto
name|upgrade
parameter_list|(
name|CompInstancesUpgradeRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
DECL|method|getCompInstances ( GetCompInstancesRequestProto request)
name|GetCompInstancesResponseProto
name|getCompInstances
parameter_list|(
name|GetCompInstancesRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
DECL|method|cancelUpgrade ( CancelUpgradeRequestProto request)
name|CancelUpgradeResponseProto
name|cancelUpgrade
parameter_list|(
name|CancelUpgradeRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
block|}
end_interface

end_unit

