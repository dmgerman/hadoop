begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.store.utils
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
name|utils
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|GetSubClusterInfoRequest
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|SubClusterDeregisterRequest
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|SubClusterHeartbeatRequest
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|SubClusterId
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|SubClusterInfo
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|SubClusterRegisterRequest
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|SubClusterState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Utility class to validate the inputs to  * {@code FederationMembershipStateStore}, allows a fail fast mechanism for  * invalid user inputs.  *  */
end_comment

begin_class
DECL|class|FederationMembershipStateStoreInputValidator
specifier|public
specifier|final
class|class
name|FederationMembershipStateStoreInputValidator
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FederationMembershipStateStoreInputValidator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|FederationMembershipStateStoreInputValidator ()
specifier|private
name|FederationMembershipStateStoreInputValidator
parameter_list|()
block|{   }
comment|/**    * Quick validation on the input to check some obvious fail conditions (fail    * fast). Check if the provided {@link SubClusterRegisterRequest} for    * registration a new subcluster is valid or not.    *    * @param request the {@link SubClusterRegisterRequest} to validate against    * @throws FederationStateStoreInvalidInputException if the request is invalid    */
DECL|method|validateSubClusterRegisterRequest ( SubClusterRegisterRequest request)
specifier|public
specifier|static
name|void
name|validateSubClusterRegisterRequest
parameter_list|(
name|SubClusterRegisterRequest
name|request
parameter_list|)
throws|throws
name|FederationStateStoreInvalidInputException
block|{
comment|// check if the request is present
if|if
condition|(
name|request
operator|==
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"Missing SubClusterRegister Request."
operator|+
literal|" Please try again by specifying a"
operator|+
literal|" SubCluster Register Information."
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FederationStateStoreInvalidInputException
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|// validate subcluster info
name|checkSubClusterInfo
argument_list|(
name|request
operator|.
name|getSubClusterInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Quick validation on the input to check some obvious fail conditions (fail    * fast). Check if the provided {@link SubClusterDeregisterRequest} for    * deregistration a subcluster is valid or not.    *    * @param request the {@link SubClusterDeregisterRequest} to validate against    * @throws FederationStateStoreInvalidInputException if the request is invalid    */
DECL|method|validateSubClusterDeregisterRequest ( SubClusterDeregisterRequest request)
specifier|public
specifier|static
name|void
name|validateSubClusterDeregisterRequest
parameter_list|(
name|SubClusterDeregisterRequest
name|request
parameter_list|)
throws|throws
name|FederationStateStoreInvalidInputException
block|{
comment|// check if the request is present
if|if
condition|(
name|request
operator|==
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"Missing SubClusterDeregister Request."
operator|+
literal|" Please try again by specifying a"
operator|+
literal|" SubCluster Deregister Information."
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FederationStateStoreInvalidInputException
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|// validate subcluster id
name|checkSubClusterId
argument_list|(
name|request
operator|.
name|getSubClusterId
argument_list|()
argument_list|)
expr_stmt|;
comment|// validate subcluster state
name|checkSubClusterState
argument_list|(
name|request
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|request
operator|.
name|getState
argument_list|()
operator|.
name|isFinal
argument_list|()
condition|)
block|{
name|String
name|message
init|=
literal|"Invalid non-final state: "
operator|+
name|request
operator|.
name|getState
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FederationStateStoreInvalidInputException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
comment|/**    * Quick validation on the input to check some obvious fail conditions (fail    * fast). Check if the provided {@link SubClusterHeartbeatRequest} for    * heartbeating a subcluster is valid or not.    *    * @param request the {@link SubClusterHeartbeatRequest} to validate against    * @throws FederationStateStoreInvalidInputException if the request is invalid    */
DECL|method|validateSubClusterHeartbeatRequest ( SubClusterHeartbeatRequest request)
specifier|public
specifier|static
name|void
name|validateSubClusterHeartbeatRequest
parameter_list|(
name|SubClusterHeartbeatRequest
name|request
parameter_list|)
throws|throws
name|FederationStateStoreInvalidInputException
block|{
comment|// check if the request is present
if|if
condition|(
name|request
operator|==
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"Missing SubClusterHeartbeat Request."
operator|+
literal|" Please try again by specifying a"
operator|+
literal|" SubCluster Heartbeat Information."
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FederationStateStoreInvalidInputException
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|// validate subcluster id
name|checkSubClusterId
argument_list|(
name|request
operator|.
name|getSubClusterId
argument_list|()
argument_list|)
expr_stmt|;
comment|// validate last heartbeat timestamp
name|checkTimestamp
argument_list|(
name|request
operator|.
name|getLastHeartBeat
argument_list|()
argument_list|)
expr_stmt|;
comment|// validate subcluster capability
name|checkCapability
argument_list|(
name|request
operator|.
name|getCapability
argument_list|()
argument_list|)
expr_stmt|;
comment|// validate subcluster state
name|checkSubClusterState
argument_list|(
name|request
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Quick validation on the input to check some obvious fail conditions (fail    * fast). Check if the provided {@link GetSubClusterInfoRequest} for querying    * subcluster's information is valid or not.    *    * @param request the {@link GetSubClusterInfoRequest} to validate against    * @throws FederationStateStoreInvalidInputException if the request is invalid    */
DECL|method|validateGetSubClusterInfoRequest ( GetSubClusterInfoRequest request)
specifier|public
specifier|static
name|void
name|validateGetSubClusterInfoRequest
parameter_list|(
name|GetSubClusterInfoRequest
name|request
parameter_list|)
throws|throws
name|FederationStateStoreInvalidInputException
block|{
comment|// check if the request is present
if|if
condition|(
name|request
operator|==
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"Missing GetSubClusterInfo Request."
operator|+
literal|" Please try again by specifying a Get SubCluster information."
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FederationStateStoreInvalidInputException
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|// validate subcluster id
name|checkSubClusterId
argument_list|(
name|request
operator|.
name|getSubClusterId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validate if all the required fields on {@link SubClusterInfo} are present    * or not. {@code Capability} will be empty as the corresponding    * {@code ResourceManager} is in the process of initialization during    * registration.    *    * @param subClusterInfo the information of the subcluster to be verified    * @throws FederationStateStoreInvalidInputException if the SubCluster Info    *           are invalid    */
DECL|method|checkSubClusterInfo (SubClusterInfo subClusterInfo)
specifier|private
specifier|static
name|void
name|checkSubClusterInfo
parameter_list|(
name|SubClusterInfo
name|subClusterInfo
parameter_list|)
throws|throws
name|FederationStateStoreInvalidInputException
block|{
if|if
condition|(
name|subClusterInfo
operator|==
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"Missing SubCluster Information."
operator|+
literal|" Please try again by specifying SubCluster Information."
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FederationStateStoreInvalidInputException
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|// validate subcluster id
name|checkSubClusterId
argument_list|(
name|subClusterInfo
operator|.
name|getSubClusterId
argument_list|()
argument_list|)
expr_stmt|;
comment|// validate AMRM Service address
name|checkAddress
argument_list|(
name|subClusterInfo
operator|.
name|getAMRMServiceAddress
argument_list|()
argument_list|)
expr_stmt|;
comment|// validate ClientRM Service address
name|checkAddress
argument_list|(
name|subClusterInfo
operator|.
name|getClientRMServiceAddress
argument_list|()
argument_list|)
expr_stmt|;
comment|// validate RMClient Service address
name|checkAddress
argument_list|(
name|subClusterInfo
operator|.
name|getRMAdminServiceAddress
argument_list|()
argument_list|)
expr_stmt|;
comment|// validate RMWeb Service address
name|checkAddress
argument_list|(
name|subClusterInfo
operator|.
name|getRMWebServiceAddress
argument_list|()
argument_list|)
expr_stmt|;
comment|// validate last heartbeat timestamp
name|checkTimestamp
argument_list|(
name|subClusterInfo
operator|.
name|getLastHeartBeat
argument_list|()
argument_list|)
expr_stmt|;
comment|// validate last start timestamp
name|checkTimestamp
argument_list|(
name|subClusterInfo
operator|.
name|getLastStartTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// validate subcluster state
name|checkSubClusterState
argument_list|(
name|subClusterInfo
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validate if the timestamp is positive or not.    *    * @param timestamp the timestamp to be verified    * @throws FederationStateStoreInvalidInputException if the timestamp is    *           invalid    */
DECL|method|checkTimestamp (long timestamp)
specifier|private
specifier|static
name|void
name|checkTimestamp
parameter_list|(
name|long
name|timestamp
parameter_list|)
throws|throws
name|FederationStateStoreInvalidInputException
block|{
if|if
condition|(
name|timestamp
operator|<
literal|0
condition|)
block|{
name|String
name|message
init|=
literal|"Invalid timestamp information."
operator|+
literal|" Please try again by specifying valid Timestamp Information."
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FederationStateStoreInvalidInputException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
comment|/**    * Validate if the Capability is present or not.    *    * @param capability the capability of the subcluster to be verified    * @throws FederationStateStoreInvalidInputException if the capability is    *           invalid    */
DECL|method|checkCapability (String capability)
specifier|private
specifier|static
name|void
name|checkCapability
parameter_list|(
name|String
name|capability
parameter_list|)
throws|throws
name|FederationStateStoreInvalidInputException
block|{
if|if
condition|(
name|capability
operator|==
literal|null
operator|||
name|capability
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|message
init|=
literal|"Invalid capability information."
operator|+
literal|" Please try again by specifying valid Capability Information."
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FederationStateStoreInvalidInputException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
comment|/**    * Validate if the SubCluster Id is present or not.    *    * @param subClusterId the identifier of the subcluster to be verified    * @throws FederationStateStoreInvalidInputException if the SubCluster Id is    *           invalid    */
DECL|method|checkSubClusterId (SubClusterId subClusterId)
specifier|protected
specifier|static
name|void
name|checkSubClusterId
parameter_list|(
name|SubClusterId
name|subClusterId
parameter_list|)
throws|throws
name|FederationStateStoreInvalidInputException
block|{
comment|// check if cluster id is present
if|if
condition|(
name|subClusterId
operator|==
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"Missing SubCluster Id information."
operator|+
literal|" Please try again by specifying Subcluster Id information."
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FederationStateStoreInvalidInputException
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|// check if cluster id is valid
if|if
condition|(
name|subClusterId
operator|.
name|getId
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|message
init|=
literal|"Invalid SubCluster Id information."
operator|+
literal|" Please try again by specifying valid Subcluster Id."
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FederationStateStoreInvalidInputException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
comment|/**    * Validate if the SubCluster Address is a valid URL or not.    *    * @param address the endpoint of the subcluster to be verified    * @throws FederationStateStoreInvalidInputException if the address is invalid    */
DECL|method|checkAddress (String address)
specifier|private
specifier|static
name|void
name|checkAddress
parameter_list|(
name|String
name|address
parameter_list|)
throws|throws
name|FederationStateStoreInvalidInputException
block|{
comment|// Ensure url is not null
if|if
condition|(
name|address
operator|==
literal|null
operator|||
name|address
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|message
init|=
literal|"Missing SubCluster Endpoint information."
operator|+
literal|" Please try again by specifying SubCluster Endpoint information."
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FederationStateStoreInvalidInputException
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|// Validate url is well formed
name|boolean
name|hasScheme
init|=
name|address
operator|.
name|contains
argument_list|(
literal|"://"
argument_list|)
decl_stmt|;
name|URI
name|uri
init|=
literal|null
decl_stmt|;
try|try
block|{
name|uri
operator|=
name|hasScheme
condition|?
name|URI
operator|.
name|create
argument_list|(
name|address
argument_list|)
else|:
name|URI
operator|.
name|create
argument_list|(
literal|"dummyscheme://"
operator|+
name|address
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"The provided SubCluster Endpoint does not contain a"
operator|+
literal|" valid host:port authority: "
operator|+
name|address
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FederationStateStoreInvalidInputException
argument_list|(
name|message
argument_list|)
throw|;
block|}
name|String
name|host
init|=
name|uri
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|int
name|port
init|=
name|uri
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|uri
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|host
operator|==
literal|null
operator|)
operator|||
operator|(
name|port
operator|<
literal|0
operator|)
operator|||
operator|(
operator|!
name|hasScheme
operator|&&
name|path
operator|!=
literal|null
operator|&&
operator|!
name|path
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
name|String
name|message
init|=
literal|"The provided SubCluster Endpoint does not contain a"
operator|+
literal|" valid host:port authority: "
operator|+
name|address
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FederationStateStoreInvalidInputException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
comment|/**    * Validate if the SubCluster State is present or not.    *    * @param state the state of the subcluster to be verified    * @throws FederationStateStoreInvalidInputException if the SubCluster State    *           is invalid    */
DECL|method|checkSubClusterState (SubClusterState state)
specifier|private
specifier|static
name|void
name|checkSubClusterState
parameter_list|(
name|SubClusterState
name|state
parameter_list|)
throws|throws
name|FederationStateStoreInvalidInputException
block|{
comment|// check sub-cluster state is not empty
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"Missing SubCluster State information."
operator|+
literal|" Please try again by specifying SubCluster State information."
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FederationStateStoreInvalidInputException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

