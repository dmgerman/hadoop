begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.store.records
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
name|federation
operator|.
name|store
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  *<p>  * SubClusterInfo is a report of the runtime information of the subcluster that  * is participating in federation.  *  *<p>  * It includes information such as:  *<ul>  *<li>{@link SubClusterId}</li>  *<li>The URL of the subcluster</li>  *<li>The timestamp representing the last start time of the subCluster</li>  *<li>{@code FederationsubClusterState}</li>  *<li>The current capacity and utilization of the subCluster</li>  *</ul>  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SubClusterInfo
specifier|public
specifier|abstract
class|class
name|SubClusterInfo
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (SubClusterId subClusterId, String amRMServiceAddress, String clientRMServiceAddress, String rmAdminServiceAddress, String rmWebServiceAddress, SubClusterState state, long lastStartTime, String capability)
specifier|public
specifier|static
name|SubClusterInfo
name|newInstance
parameter_list|(
name|SubClusterId
name|subClusterId
parameter_list|,
name|String
name|amRMServiceAddress
parameter_list|,
name|String
name|clientRMServiceAddress
parameter_list|,
name|String
name|rmAdminServiceAddress
parameter_list|,
name|String
name|rmWebServiceAddress
parameter_list|,
name|SubClusterState
name|state
parameter_list|,
name|long
name|lastStartTime
parameter_list|,
name|String
name|capability
parameter_list|)
block|{
return|return
name|newInstance
argument_list|(
name|subClusterId
argument_list|,
name|amRMServiceAddress
argument_list|,
name|clientRMServiceAddress
argument_list|,
name|rmAdminServiceAddress
argument_list|,
name|rmWebServiceAddress
argument_list|,
literal|0
argument_list|,
name|state
argument_list|,
name|lastStartTime
argument_list|,
name|capability
argument_list|)
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (SubClusterId subClusterId, String amRMServiceAddress, String clientRMServiceAddress, String rmAdminServiceAddress, String rmWebServiceAddress, long lastHeartBeat, SubClusterState state, long lastStartTime, String capability)
specifier|public
specifier|static
name|SubClusterInfo
name|newInstance
parameter_list|(
name|SubClusterId
name|subClusterId
parameter_list|,
name|String
name|amRMServiceAddress
parameter_list|,
name|String
name|clientRMServiceAddress
parameter_list|,
name|String
name|rmAdminServiceAddress
parameter_list|,
name|String
name|rmWebServiceAddress
parameter_list|,
name|long
name|lastHeartBeat
parameter_list|,
name|SubClusterState
name|state
parameter_list|,
name|long
name|lastStartTime
parameter_list|,
name|String
name|capability
parameter_list|)
block|{
name|SubClusterInfo
name|subClusterInfo
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|SubClusterInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|subClusterInfo
operator|.
name|setSubClusterId
argument_list|(
name|subClusterId
argument_list|)
expr_stmt|;
name|subClusterInfo
operator|.
name|setAMRMServiceAddress
argument_list|(
name|amRMServiceAddress
argument_list|)
expr_stmt|;
name|subClusterInfo
operator|.
name|setClientRMServiceAddress
argument_list|(
name|clientRMServiceAddress
argument_list|)
expr_stmt|;
name|subClusterInfo
operator|.
name|setRMAdminServiceAddress
argument_list|(
name|rmAdminServiceAddress
argument_list|)
expr_stmt|;
name|subClusterInfo
operator|.
name|setRMWebServiceAddress
argument_list|(
name|rmWebServiceAddress
argument_list|)
expr_stmt|;
name|subClusterInfo
operator|.
name|setLastHeartBeat
argument_list|(
name|lastHeartBeat
argument_list|)
expr_stmt|;
name|subClusterInfo
operator|.
name|setState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|subClusterInfo
operator|.
name|setLastStartTime
argument_list|(
name|lastStartTime
argument_list|)
expr_stmt|;
name|subClusterInfo
operator|.
name|setCapability
argument_list|(
name|capability
argument_list|)
expr_stmt|;
return|return
name|subClusterInfo
return|;
block|}
comment|/**    * Get the {@link SubClusterId} representing the unique identifier of the    * subcluster.    *    * @return the subcluster identifier    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getSubClusterId ()
specifier|public
specifier|abstract
name|SubClusterId
name|getSubClusterId
parameter_list|()
function_decl|;
comment|/**    * Set the {@link SubClusterId} representing the unique identifier of the    * subCluster.    *    * @param subClusterId the subCluster identifier    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setSubClusterId (SubClusterId subClusterId)
specifier|public
specifier|abstract
name|void
name|setSubClusterId
parameter_list|(
name|SubClusterId
name|subClusterId
parameter_list|)
function_decl|;
comment|/**    * Get the URL of the AM-RM service endpoint of the subcluster    *<code>ResourceManager</code>.    *    * @return the URL of the AM-RM service endpoint of the subcluster    *<code>ResourceManager</code>    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getAMRMServiceAddress ()
specifier|public
specifier|abstract
name|String
name|getAMRMServiceAddress
parameter_list|()
function_decl|;
comment|/**    * Set the URL of the AM-RM service endpoint of the subcluster    *<code>ResourceManager</code>.    *    * @param amRMServiceAddress the URL of the AM-RM service endpoint of the    *          subcluster<code>ResourceManager</code>    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setAMRMServiceAddress (String amRMServiceAddress)
specifier|public
specifier|abstract
name|void
name|setAMRMServiceAddress
parameter_list|(
name|String
name|amRMServiceAddress
parameter_list|)
function_decl|;
comment|/**    * Get the URL of the client-RM service endpoint of the subcluster    *<code>ResourceManager</code>.    *    * @return the URL of the client-RM service endpoint of the subcluster    *<code>ResourceManager</code>    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getClientRMServiceAddress ()
specifier|public
specifier|abstract
name|String
name|getClientRMServiceAddress
parameter_list|()
function_decl|;
comment|/**    * Set the URL of the client-RM service endpoint of the subcluster    *<code>ResourceManager</code>.    *    * @param clientRMServiceAddress the URL of the client-RM service endpoint of    *          the subCluster<code>ResourceManager</code>    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setClientRMServiceAddress (String clientRMServiceAddress)
specifier|public
specifier|abstract
name|void
name|setClientRMServiceAddress
parameter_list|(
name|String
name|clientRMServiceAddress
parameter_list|)
function_decl|;
comment|/**    * Get the URL of the<code>ResourceManager</code> administration service.    *    * @return the URL of the<code>ResourceManager</code> administration service    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getRMAdminServiceAddress ()
specifier|public
specifier|abstract
name|String
name|getRMAdminServiceAddress
parameter_list|()
function_decl|;
comment|/**    * Set the URL of the<code>ResourceManager</code> administration service.    *    * @param rmAdminServiceAddress the URL of the<code>ResourceManager</code>    *          administration service.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setRMAdminServiceAddress (String rmAdminServiceAddress)
specifier|public
specifier|abstract
name|void
name|setRMAdminServiceAddress
parameter_list|(
name|String
name|rmAdminServiceAddress
parameter_list|)
function_decl|;
comment|/**    * Get the URL of the<code>ResourceManager</code> web application interface.    *    * @return the URL of the<code>ResourceManager</code> web application    *         interface.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getRMWebServiceAddress ()
specifier|public
specifier|abstract
name|String
name|getRMWebServiceAddress
parameter_list|()
function_decl|;
comment|/**    * Set the URL of the<code>ResourceManager</code> web application interface.    *    * @param rmWebServiceAddress the URL of the<code>ResourceManager</code> web    *          application interface.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setRMWebServiceAddress (String rmWebServiceAddress)
specifier|public
specifier|abstract
name|void
name|setRMWebServiceAddress
parameter_list|(
name|String
name|rmWebServiceAddress
parameter_list|)
function_decl|;
comment|/**    * Get the last heart beat time of the subcluster.    *    * @return the state of the subcluster    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getLastHeartBeat ()
specifier|public
specifier|abstract
name|long
name|getLastHeartBeat
parameter_list|()
function_decl|;
comment|/**    * Set the last heartbeat time of the subcluster.    *    * @param time the last heartbeat time of the subcluster    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setLastHeartBeat (long time)
specifier|public
specifier|abstract
name|void
name|setLastHeartBeat
parameter_list|(
name|long
name|time
parameter_list|)
function_decl|;
comment|/**    * Get the {@link SubClusterState} of the subcluster.    *    * @return the state of the subcluster    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getState ()
specifier|public
specifier|abstract
name|SubClusterState
name|getState
parameter_list|()
function_decl|;
comment|/**    * Set the {@link SubClusterState} of the subcluster.    *    * @param state the state of the subCluster    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setState (SubClusterState state)
specifier|public
specifier|abstract
name|void
name|setState
parameter_list|(
name|SubClusterState
name|state
parameter_list|)
function_decl|;
comment|/**    * Get the timestamp representing the last start time of the subcluster.    *    * @return the timestamp representing the last start time of the subcluster    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getLastStartTime ()
specifier|public
specifier|abstract
name|long
name|getLastStartTime
parameter_list|()
function_decl|;
comment|/**    * Set the timestamp representing the last start time of the subcluster.    *    * @param lastStartTime the timestamp representing the last start time of the    *          subcluster    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setLastStartTime (long lastStartTime)
specifier|public
specifier|abstract
name|void
name|setLastStartTime
parameter_list|(
name|long
name|lastStartTime
parameter_list|)
function_decl|;
comment|/**    * Get the current capacity and utilization of the subcluster. This is the    * JAXB marshalled string representation of the<code>ClusterMetrics</code>.    *    * @return the current capacity and utilization of the subcluster    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getCapability ()
specifier|public
specifier|abstract
name|String
name|getCapability
parameter_list|()
function_decl|;
comment|/**    * Set the current capacity and utilization of the subCluster. This is the    * JAXB marshalled string representation of the<code>ClusterMetrics</code>.    *    * @param capability the current capacity and utilization of the subcluster    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setCapability (String capability)
specifier|public
specifier|abstract
name|void
name|setCapability
parameter_list|(
name|String
name|capability
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SubClusterInfo [getSubClusterId() = "
operator|+
name|getSubClusterId
argument_list|()
operator|+
literal|", getAMRMServiceAddress() = "
operator|+
name|getAMRMServiceAddress
argument_list|()
operator|+
literal|", getClientRMServiceAddress() = "
operator|+
name|getClientRMServiceAddress
argument_list|()
operator|+
literal|", getRMAdminServiceAddress() = "
operator|+
name|getRMAdminServiceAddress
argument_list|()
operator|+
literal|", getRMWebServiceAddress() = "
operator|+
name|getRMWebServiceAddress
argument_list|()
operator|+
literal|", getState() = "
operator|+
name|getState
argument_list|()
operator|+
literal|", getLastStartTime() = "
operator|+
name|getLastStartTime
argument_list|()
operator|+
literal|", getCapability() = "
operator|+
name|getCapability
argument_list|()
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

