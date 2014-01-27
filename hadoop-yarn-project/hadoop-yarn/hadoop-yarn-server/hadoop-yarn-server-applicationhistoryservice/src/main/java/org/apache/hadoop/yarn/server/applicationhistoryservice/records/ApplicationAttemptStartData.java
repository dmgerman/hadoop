begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice.records
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
name|applicationhistoryservice
operator|.
name|records
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
operator|.
name|Public
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
operator|.
name|Unstable
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
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  * The class contains the fields that can be determined when  *<code>RMAppAttempt</code> starts, and that need to be stored persistently.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|ApplicationAttemptStartData
specifier|public
specifier|abstract
class|class
name|ApplicationAttemptStartData
block|{
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|newInstance ( ApplicationAttemptId appAttemptId, String host, int rpcPort, ContainerId masterContainerId)
specifier|public
specifier|static
name|ApplicationAttemptStartData
name|newInstance
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|String
name|host
parameter_list|,
name|int
name|rpcPort
parameter_list|,
name|ContainerId
name|masterContainerId
parameter_list|)
block|{
name|ApplicationAttemptStartData
name|appAttemptSD
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ApplicationAttemptStartData
operator|.
name|class
argument_list|)
decl_stmt|;
name|appAttemptSD
operator|.
name|setApplicationAttemptId
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|appAttemptSD
operator|.
name|setHost
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|appAttemptSD
operator|.
name|setRPCPort
argument_list|(
name|rpcPort
argument_list|)
expr_stmt|;
name|appAttemptSD
operator|.
name|setMasterContainerId
argument_list|(
name|masterContainerId
argument_list|)
expr_stmt|;
return|return
name|appAttemptSD
return|;
block|}
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getApplicationAttemptId ()
specifier|public
specifier|abstract
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setApplicationAttemptId ( ApplicationAttemptId applicationAttemptId)
specifier|public
specifier|abstract
name|void
name|setApplicationAttemptId
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|)
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getHost ()
specifier|public
specifier|abstract
name|String
name|getHost
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setHost (String host)
specifier|public
specifier|abstract
name|void
name|setHost
parameter_list|(
name|String
name|host
parameter_list|)
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getRPCPort ()
specifier|public
specifier|abstract
name|int
name|getRPCPort
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setRPCPort (int rpcPort)
specifier|public
specifier|abstract
name|void
name|setRPCPort
parameter_list|(
name|int
name|rpcPort
parameter_list|)
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getMasterContainerId ()
specifier|public
specifier|abstract
name|ContainerId
name|getMasterContainerId
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setMasterContainerId (ContainerId masterContainerId)
specifier|public
specifier|abstract
name|void
name|setMasterContainerId
parameter_list|(
name|ContainerId
name|masterContainerId
parameter_list|)
function_decl|;
block|}
end_class

end_unit

