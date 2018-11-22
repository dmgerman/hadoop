begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
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
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|security
operator|.
name|SaslRpcServer
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
name|security
operator|.
name|UserGroupInformation
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|security
operator|.
name|token
operator|.
name|TokenIdentifier
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
name|Container
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
name|SchedulingRequest
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
name|conf
operator|.
name|YarnConfiguration
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
name|exceptions
operator|.
name|YarnRuntimeException
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
comment|/**  * Utility class for AMRMClient.  */
end_comment

begin_class
annotation|@
name|Private
DECL|class|AMRMClientUtils
specifier|public
specifier|final
class|class
name|AMRMClientUtils
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
name|AMRMClientUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|PRE_REGISTER_RESPONSE_ID
specifier|public
specifier|static
specifier|final
name|int
name|PRE_REGISTER_RESPONSE_ID
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|APP_ALREADY_REGISTERED_MESSAGE
specifier|public
specifier|static
specifier|final
name|String
name|APP_ALREADY_REGISTERED_MESSAGE
init|=
literal|"Application Master is already registered : "
decl_stmt|;
DECL|field|EXPECTED_HB_RESPONSEID_MESSAGE
specifier|public
specifier|static
specifier|final
name|String
name|EXPECTED_HB_RESPONSEID_MESSAGE
init|=
literal|" expect responseId to be "
decl_stmt|;
DECL|field|RECEIVED_HB_RESPONSEID_MESSAGE
specifier|public
specifier|static
specifier|final
name|String
name|RECEIVED_HB_RESPONSEID_MESSAGE
init|=
literal|" but get "
decl_stmt|;
DECL|method|AMRMClientUtils ()
specifier|private
name|AMRMClientUtils
parameter_list|()
block|{   }
comment|/**    * Create a proxy for the specified protocol.    *    * @param configuration Configuration to generate {@link ClientRMProxy}    * @param protocol Protocol for the proxy    * @param user the user on whose behalf the proxy is being created    * @param token the auth token to use for connection    * @param<T> Type information of the proxy    * @return Proxy to the RM    * @throws IOException on failure    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|createRMProxy (final Configuration configuration, final Class<T> protocol, UserGroupInformation user, final Token<? extends TokenIdentifier> token)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|createRMProxy
parameter_list|(
specifier|final
name|Configuration
name|configuration
parameter_list|,
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|protocol
parameter_list|,
name|UserGroupInformation
name|user
parameter_list|,
specifier|final
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|String
name|rmClusterId
init|=
name|configuration
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CLUSTER_ID
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_CLUSTER_ID
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating RMProxy to RM {} for protocol {} for user {}"
argument_list|,
name|rmClusterId
argument_list|,
name|protocol
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|user
argument_list|)
expr_stmt|;
if|if
condition|(
name|token
operator|!=
literal|null
condition|)
block|{
comment|// preserve the token service sent by the RM when adding the token
comment|// to ensure we replace the previous token setup by the RM.
comment|// Afterwards we can update the service address for the RPC layer.
comment|// Same as YarnServerSecurityUtils.updateAMRMToken()
name|user
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|token
operator|.
name|setService
argument_list|(
name|ClientRMProxy
operator|.
name|getAMRMTokenService
argument_list|(
name|configuration
argument_list|)
argument_list|)
expr_stmt|;
name|setAuthModeInConf
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
specifier|final
name|T
name|proxyConnection
init|=
name|user
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|T
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|ClientRMProxy
operator|.
name|createRMProxy
argument_list|(
name|configuration
argument_list|,
name|protocol
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|proxyConnection
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|setAuthModeInConf (Configuration conf)
specifier|private
specifier|static
name|void
name|setAuthModeInConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
name|SaslRpcServer
operator|.
name|AuthMethod
operator|.
name|TOKEN
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Generate the exception message when RM receives an AM heartbeat with    * invalid responseId.    *    * @param appAttemptId the app attempt    * @param expected the expected responseId value    * @param received the received responseId value    * @return the assembled exception message    */
DECL|method|assembleInvalidResponseIdExceptionMessage ( ApplicationAttemptId appAttemptId, int expected, int received)
specifier|public
specifier|static
name|String
name|assembleInvalidResponseIdExceptionMessage
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|int
name|expected
parameter_list|,
name|int
name|received
parameter_list|)
block|{
return|return
literal|"Invalid responseId in AllocateRequest from application attempt: "
operator|+
name|appAttemptId
operator|+
name|EXPECTED_HB_RESPONSEID_MESSAGE
operator|+
name|expected
operator|+
name|RECEIVED_HB_RESPONSEID_MESSAGE
operator|+
name|received
return|;
block|}
comment|/**    * Parse the expected responseId from the exception generated by RM when    * processing AM heartbeat.    *    * @param exceptionMessage the exception message thrown by RM    * @return the parsed expected responseId, -1 if failed    */
DECL|method|parseExpectedResponseIdFromException ( String exceptionMessage)
specifier|public
specifier|static
name|int
name|parseExpectedResponseIdFromException
parameter_list|(
name|String
name|exceptionMessage
parameter_list|)
block|{
if|if
condition|(
name|exceptionMessage
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|int
name|start
init|=
name|exceptionMessage
operator|.
name|indexOf
argument_list|(
name|EXPECTED_HB_RESPONSEID_MESSAGE
argument_list|)
decl_stmt|;
name|int
name|end
init|=
name|exceptionMessage
operator|.
name|indexOf
argument_list|(
name|RECEIVED_HB_RESPONSEID_MESSAGE
argument_list|)
decl_stmt|;
if|if
condition|(
name|start
operator|==
operator|-
literal|1
operator|||
name|end
operator|==
operator|-
literal|1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|start
operator|+=
name|EXPECTED_HB_RESPONSEID_MESSAGE
operator|.
name|length
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|exceptionMessage
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
DECL|method|getNextResponseId (int responseId)
specifier|public
specifier|static
name|int
name|getNextResponseId
parameter_list|(
name|int
name|responseId
parameter_list|)
block|{
comment|// Loop between 0 to Integer.MAX_VALUE
return|return
operator|(
name|responseId
operator|+
literal|1
operator|)
operator|&
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
DECL|method|addToOutstandingSchedulingRequests ( Collection<SchedulingRequest> requests, Map<Set<String>, List<SchedulingRequest>> outstandingSchedRequests)
specifier|public
specifier|static
name|void
name|addToOutstandingSchedulingRequests
parameter_list|(
name|Collection
argument_list|<
name|SchedulingRequest
argument_list|>
name|requests
parameter_list|,
name|Map
argument_list|<
name|Set
argument_list|<
name|String
argument_list|>
argument_list|,
name|List
argument_list|<
name|SchedulingRequest
argument_list|>
argument_list|>
name|outstandingSchedRequests
parameter_list|)
block|{
for|for
control|(
name|SchedulingRequest
name|req
range|:
name|requests
control|)
block|{
name|List
argument_list|<
name|SchedulingRequest
argument_list|>
name|schedulingRequests
init|=
name|outstandingSchedRequests
operator|.
name|computeIfAbsent
argument_list|(
name|req
operator|.
name|getAllocationTags
argument_list|()
argument_list|,
name|x
lambda|->
operator|new
name|LinkedList
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|SchedulingRequest
name|matchingReq
init|=
literal|null
decl_stmt|;
for|for
control|(
name|SchedulingRequest
name|schedReq
range|:
name|schedulingRequests
control|)
block|{
if|if
condition|(
name|isMatchingSchedulingRequests
argument_list|(
name|req
argument_list|,
name|schedReq
argument_list|)
condition|)
block|{
name|matchingReq
operator|=
name|schedReq
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|matchingReq
operator|!=
literal|null
condition|)
block|{
name|matchingReq
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|setNumAllocations
argument_list|(
name|req
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|getNumAllocations
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|schedulingRequests
operator|.
name|add
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|isMatchingSchedulingRequests ( SchedulingRequest schedReq1, SchedulingRequest schedReq2)
specifier|public
specifier|static
name|boolean
name|isMatchingSchedulingRequests
parameter_list|(
name|SchedulingRequest
name|schedReq1
parameter_list|,
name|SchedulingRequest
name|schedReq2
parameter_list|)
block|{
return|return
name|schedReq1
operator|.
name|getPriority
argument_list|()
operator|.
name|equals
argument_list|(
name|schedReq2
operator|.
name|getPriority
argument_list|()
argument_list|)
operator|&&
name|schedReq1
operator|.
name|getExecutionType
argument_list|()
operator|.
name|getExecutionType
argument_list|()
operator|.
name|equals
argument_list|(
name|schedReq1
operator|.
name|getExecutionType
argument_list|()
operator|.
name|getExecutionType
argument_list|()
argument_list|)
operator|&&
name|schedReq1
operator|.
name|getAllocationRequestId
argument_list|()
operator|==
name|schedReq2
operator|.
name|getAllocationRequestId
argument_list|()
return|;
block|}
DECL|method|removeFromOutstandingSchedulingRequests ( Collection<Container> containers, Map<Set<String>, List<SchedulingRequest>> outstandingSchedRequests)
specifier|public
specifier|static
name|void
name|removeFromOutstandingSchedulingRequests
parameter_list|(
name|Collection
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|,
name|Map
argument_list|<
name|Set
argument_list|<
name|String
argument_list|>
argument_list|,
name|List
argument_list|<
name|SchedulingRequest
argument_list|>
argument_list|>
name|outstandingSchedRequests
parameter_list|)
block|{
if|if
condition|(
name|containers
operator|==
literal|null
operator|||
name|containers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
for|for
control|(
name|Container
name|container
range|:
name|containers
control|)
block|{
if|if
condition|(
name|container
operator|.
name|getAllocationTags
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|SchedulingRequest
argument_list|>
name|schedReqs
init|=
name|outstandingSchedRequests
operator|.
name|get
argument_list|(
name|container
operator|.
name|getAllocationTags
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|schedReqs
operator|!=
literal|null
operator|&&
operator|!
name|schedReqs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Iterator
argument_list|<
name|SchedulingRequest
argument_list|>
name|iter
init|=
name|schedReqs
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SchedulingRequest
name|schedReq
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|schedReq
operator|.
name|getPriority
argument_list|()
operator|.
name|equals
argument_list|(
name|container
operator|.
name|getPriority
argument_list|()
argument_list|)
operator|&&
name|schedReq
operator|.
name|getAllocationRequestId
argument_list|()
operator|==
name|container
operator|.
name|getAllocationRequestId
argument_list|()
condition|)
block|{
name|int
name|numAllocations
init|=
name|schedReq
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|getNumAllocations
argument_list|()
decl_stmt|;
name|numAllocations
operator|--
expr_stmt|;
if|if
condition|(
name|numAllocations
operator|==
literal|0
condition|)
block|{
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|schedReq
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|setNumAllocations
argument_list|(
name|numAllocations
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

