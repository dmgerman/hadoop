begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords
package|package
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
name|protocolrecords
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
name|Private
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
name|Evolving
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

begin_comment
comment|/**  * A {@link PreemptionMessage} is part of the RM-AM protocol, and it is used by  * the RM to specify resources that the RM wants to reclaim from this  *<code>ApplicationMaster</code> (AM). The AM receives a {@link  * StrictPreemptionContract} message encoding which containers the platform may  * forcibly kill, granting it an opportunity to checkpoint state or adjust its  * execution plan. The message may also include a {@link PreemptionContract}  * granting the AM more latitude in selecting which resources to return to the  * cluster.  *  * The AM should decode both parts of the message. The {@link  * StrictPreemptionContract} specifies particular allocations that the RM  * requires back. The AM can checkpoint containers' state, adjust its execution  * plan to move the computation, or take no action and hope that conditions that  * caused the RM to ask for the container will change.  *  * In contrast, the {@link PreemptionContract} also includes a description of  * resources with a set of containers. If the AM releases containers matching  * that profile, then the containers enumerated in {@link  * PreemptionContract#getContainers()} may not be killed.  *  * Each preemption message reflects the RM's current understanding of the  * cluster state, so a request to return<emph>N</emph> containers may not  * reflect containers the AM is releasing, recently exited containers the RM has  * yet to learn about, or new containers allocated before the message was  * generated. Conversely, an RM may request a different profile of containers in  * subsequent requests.  *  * The policy enforced by the RM is part of the scheduler. Generally, only  * containers that have been requested consistently should be killed, but the  * details are not specified.  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Evolving
DECL|interface|PreemptionMessage
specifier|public
interface|interface
name|PreemptionMessage
block|{
comment|/**    * @return Specific resources that may be killed by the    *<code>ResourceManager</code>    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|getStrictContract ()
specifier|public
name|StrictPreemptionContract
name|getStrictContract
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setStrictContract (StrictPreemptionContract set)
specifier|public
name|void
name|setStrictContract
parameter_list|(
name|StrictPreemptionContract
name|set
parameter_list|)
function_decl|;
comment|/**    * @return Contract describing resources to return to the cluster.    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|getContract ()
specifier|public
name|PreemptionContract
name|getContract
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setContract (PreemptionContract contract)
specifier|public
name|void
name|setContract
parameter_list|(
name|PreemptionContract
name|contract
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

