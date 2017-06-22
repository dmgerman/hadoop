begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.policies
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
name|policies
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|ApplicationSubmissionContext
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
name|server
operator|.
name|federation
operator|.
name|policies
operator|.
name|amrmproxy
operator|.
name|LocalityMulticastAMRMProxyPolicy
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
name|policies
operator|.
name|exceptions
operator|.
name|FederationPolicyException
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
name|policies
operator|.
name|exceptions
operator|.
name|FederationPolicyInitializationException
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
name|policies
operator|.
name|manager
operator|.
name|FederationPolicyManager
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
name|policies
operator|.
name|router
operator|.
name|FederationRouterPolicy
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
name|resolver
operator|.
name|SubClusterResolver
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
name|SubClusterPolicyConfiguration
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
name|utils
operator|.
name|FederationStateStoreFacade
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * This class provides a facade to the policy subsystem, and handles the  * lifecycle of policies (e.g., refresh from remote, default behaviors etc.).  */
end_comment

begin_class
DECL|class|RouterPolicyFacade
specifier|public
class|class
name|RouterPolicyFacade
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|LocalityMulticastAMRMProxyPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|subClusterResolver
specifier|private
specifier|final
name|SubClusterResolver
name|subClusterResolver
decl_stmt|;
DECL|field|federationFacade
specifier|private
specifier|final
name|FederationStateStoreFacade
name|federationFacade
decl_stmt|;
DECL|field|globalConfMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|SubClusterPolicyConfiguration
argument_list|>
name|globalConfMap
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|globalPolicyMap
name|Map
argument_list|<
name|String
argument_list|,
name|FederationRouterPolicy
argument_list|>
name|globalPolicyMap
decl_stmt|;
DECL|method|RouterPolicyFacade (YarnConfiguration conf, FederationStateStoreFacade facade, SubClusterResolver resolver, SubClusterId homeSubcluster)
specifier|public
name|RouterPolicyFacade
parameter_list|(
name|YarnConfiguration
name|conf
parameter_list|,
name|FederationStateStoreFacade
name|facade
parameter_list|,
name|SubClusterResolver
name|resolver
parameter_list|,
name|SubClusterId
name|homeSubcluster
parameter_list|)
throws|throws
name|FederationPolicyInitializationException
block|{
name|this
operator|.
name|federationFacade
operator|=
name|facade
expr_stmt|;
name|this
operator|.
name|subClusterResolver
operator|=
name|resolver
expr_stmt|;
name|this
operator|.
name|globalConfMap
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|globalPolicyMap
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
comment|// load default behavior from store if possible
name|String
name|defaulKey
init|=
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_POLICY_KEY
decl_stmt|;
name|SubClusterPolicyConfiguration
name|configuration
init|=
literal|null
decl_stmt|;
try|try
block|{
name|configuration
operator|=
name|federationFacade
operator|.
name|getPolicyConfiguration
argument_list|(
name|defaulKey
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No fallback behavior defined in store, defaulting to XML "
operator|+
literal|"configuration fallback behavior."
argument_list|)
expr_stmt|;
block|}
comment|// or from XML conf otherwise.
if|if
condition|(
name|configuration
operator|==
literal|null
condition|)
block|{
name|String
name|defaultFederationPolicyManager
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_POLICY_MANAGER
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_POLICY_MANAGER
argument_list|)
decl_stmt|;
name|String
name|defaultPolicyParamString
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_POLICY_MANAGER_PARAMS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_POLICY_MANAGER_PARAMS
argument_list|)
decl_stmt|;
name|ByteBuffer
name|defaultPolicyParam
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|defaultPolicyParamString
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|configuration
operator|=
name|SubClusterPolicyConfiguration
operator|.
name|newInstance
argument_list|(
name|defaulKey
argument_list|,
name|defaultFederationPolicyManager
argument_list|,
name|defaultPolicyParam
argument_list|)
expr_stmt|;
block|}
comment|// construct the required policy manager
name|FederationPolicyInitializationContext
name|fallbackContext
init|=
operator|new
name|FederationPolicyInitializationContext
argument_list|(
name|configuration
argument_list|,
name|subClusterResolver
argument_list|,
name|federationFacade
argument_list|,
name|homeSubcluster
argument_list|)
decl_stmt|;
name|FederationPolicyManager
name|fallbackPolicyManager
init|=
name|FederationPolicyUtils
operator|.
name|instantiatePolicyManager
argument_list|(
name|configuration
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|fallbackPolicyManager
operator|.
name|setQueue
argument_list|(
name|defaulKey
argument_list|)
expr_stmt|;
comment|// add to the cache the fallback behavior
name|globalConfMap
operator|.
name|put
argument_list|(
name|defaulKey
argument_list|,
name|fallbackContext
operator|.
name|getSubClusterPolicyConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|globalPolicyMap
operator|.
name|put
argument_list|(
name|defaulKey
argument_list|,
name|fallbackPolicyManager
operator|.
name|getRouterPolicy
argument_list|(
name|fallbackContext
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * This method provides a wrapper of all policy functionalities for routing .    * Internally it manages configuration changes, and policy init/reinit.    *    * @param appSubmissionContext the {@link ApplicationSubmissionContext} that    *          has to be routed to an appropriate subCluster for execution.    *    * @param blackListSubClusters the list of subClusters as identified by    *          {@link SubClusterId} to blackList from the selection of the home    *          subCluster.    *    * @return the {@link SubClusterId} that will be the "home" for this    *         application.    *    * @throws YarnException if there are issues initializing policies, or no    *           valid sub-cluster id could be found for this app.    */
DECL|method|getHomeSubcluster ( ApplicationSubmissionContext appSubmissionContext, List<SubClusterId> blackListSubClusters)
specifier|public
name|SubClusterId
name|getHomeSubcluster
parameter_list|(
name|ApplicationSubmissionContext
name|appSubmissionContext
parameter_list|,
name|List
argument_list|<
name|SubClusterId
argument_list|>
name|blackListSubClusters
parameter_list|)
throws|throws
name|YarnException
block|{
comment|// the maps are concurrent, but we need to protect from reset()
comment|// reinitialization mid-execution by creating a new reference local to this
comment|// method.
name|Map
argument_list|<
name|String
argument_list|,
name|SubClusterPolicyConfiguration
argument_list|>
name|cachedConfs
init|=
name|globalConfMap
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|FederationRouterPolicy
argument_list|>
name|policyMap
init|=
name|globalPolicyMap
decl_stmt|;
if|if
condition|(
name|appSubmissionContext
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FederationPolicyException
argument_list|(
literal|"The ApplicationSubmissionContext "
operator|+
literal|"cannot be null."
argument_list|)
throw|;
block|}
name|String
name|queue
init|=
name|appSubmissionContext
operator|.
name|getQueue
argument_list|()
decl_stmt|;
comment|// respecting YARN behavior we assume default queue if the queue is not
comment|// specified. This also ensures that "null" can be used as a key to get the
comment|// default behavior.
if|if
condition|(
name|queue
operator|==
literal|null
condition|)
block|{
name|queue
operator|=
name|YarnConfiguration
operator|.
name|DEFAULT_QUEUE_NAME
expr_stmt|;
block|}
comment|// the facade might cache this request, based on its parameterization
name|SubClusterPolicyConfiguration
name|configuration
init|=
literal|null
decl_stmt|;
try|try
block|{
name|configuration
operator|=
name|federationFacade
operator|.
name|getPolicyConfiguration
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
comment|// If there is no policy configured for this queue, fallback to the baseline
comment|// policy that is configured either in the store or via XML config (and
comment|// cached)
if|if
condition|(
name|configuration
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"There is no policies configured for queue: "
operator|+
name|queue
operator|+
literal|" we"
operator|+
literal|" fallback to default policy for: "
operator|+
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_POLICY_KEY
argument_list|)
expr_stmt|;
name|queue
operator|=
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_POLICY_KEY
expr_stmt|;
name|configuration
operator|=
name|federationFacade
operator|.
name|getPolicyConfiguration
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_POLICY_KEY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
comment|// the fallback is not configure via store, but via XML, using
comment|// previously loaded configuration.
name|configuration
operator|=
name|cachedConfs
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_POLICY_KEY
argument_list|)
expr_stmt|;
block|}
block|}
comment|// if the configuration has changed since last loaded, reinit the policy
comment|// based on current configuration
if|if
condition|(
operator|!
name|cachedConfs
operator|.
name|containsKey
argument_list|(
name|queue
argument_list|)
operator|||
operator|!
name|cachedConfs
operator|.
name|get
argument_list|(
name|queue
argument_list|)
operator|.
name|equals
argument_list|(
name|configuration
argument_list|)
condition|)
block|{
name|singlePolicyReinit
argument_list|(
name|policyMap
argument_list|,
name|cachedConfs
argument_list|,
name|queue
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
block|}
name|FederationRouterPolicy
name|policy
init|=
name|policyMap
operator|.
name|get
argument_list|(
name|queue
argument_list|)
decl_stmt|;
if|if
condition|(
name|policy
operator|==
literal|null
condition|)
block|{
comment|// this should never happen, as the to maps are updated together
throw|throw
operator|new
name|FederationPolicyException
argument_list|(
literal|"No FederationRouterPolicy found "
operator|+
literal|"for queue: "
operator|+
name|appSubmissionContext
operator|.
name|getQueue
argument_list|()
operator|+
literal|" (for "
operator|+
literal|"application: "
operator|+
name|appSubmissionContext
operator|.
name|getApplicationId
argument_list|()
operator|+
literal|") "
operator|+
literal|"and no default specified."
argument_list|)
throw|;
block|}
return|return
name|policy
operator|.
name|getHomeSubcluster
argument_list|(
name|appSubmissionContext
argument_list|,
name|blackListSubClusters
argument_list|)
return|;
block|}
comment|/**    * This method reinitializes a policy and loads it in the policyMap.    *    * @param queue the queue to initialize a policy for.    * @param conf the configuration to use for initalization.    *    * @throws FederationPolicyInitializationException if initialization fails.    */
DECL|method|singlePolicyReinit (Map<String, FederationRouterPolicy> policyMap, Map<String, SubClusterPolicyConfiguration> cachedConfs, String queue, SubClusterPolicyConfiguration conf)
specifier|private
name|void
name|singlePolicyReinit
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|FederationRouterPolicy
argument_list|>
name|policyMap
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|SubClusterPolicyConfiguration
argument_list|>
name|cachedConfs
parameter_list|,
name|String
name|queue
parameter_list|,
name|SubClusterPolicyConfiguration
name|conf
parameter_list|)
throws|throws
name|FederationPolicyInitializationException
block|{
name|FederationPolicyInitializationContext
name|context
init|=
operator|new
name|FederationPolicyInitializationContext
argument_list|(
name|conf
argument_list|,
name|subClusterResolver
argument_list|,
name|federationFacade
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|newType
init|=
name|context
operator|.
name|getSubClusterPolicyConfiguration
argument_list|()
operator|.
name|getType
argument_list|()
decl_stmt|;
name|FederationRouterPolicy
name|routerPolicy
init|=
name|policyMap
operator|.
name|get
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|FederationPolicyManager
name|federationPolicyManager
init|=
name|FederationPolicyUtils
operator|.
name|instantiatePolicyManager
argument_list|(
name|newType
argument_list|)
decl_stmt|;
comment|// set queue, reinit policy if required (implementation lazily check
comment|// content of conf), and cache it
name|federationPolicyManager
operator|.
name|setQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|routerPolicy
operator|=
name|federationPolicyManager
operator|.
name|getRouterPolicy
argument_list|(
name|context
argument_list|,
name|routerPolicy
argument_list|)
expr_stmt|;
comment|// we need the two put to be atomic (across multiple threads invoking
comment|// this and reset operations)
synchronized|synchronized
init|(
name|this
init|)
block|{
name|policyMap
operator|.
name|put
argument_list|(
name|queue
argument_list|,
name|routerPolicy
argument_list|)
expr_stmt|;
name|cachedConfs
operator|.
name|put
argument_list|(
name|queue
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This method flushes all cached configurations and policies. This should be    * invoked if the facade remains activity after very large churn of queues in    * the system.    */
DECL|method|reset ()
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
comment|// remember the fallBack
name|SubClusterPolicyConfiguration
name|conf
init|=
name|globalConfMap
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_POLICY_KEY
argument_list|)
decl_stmt|;
name|FederationRouterPolicy
name|policy
init|=
name|globalPolicyMap
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_POLICY_KEY
argument_list|)
decl_stmt|;
name|globalConfMap
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|globalPolicyMap
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
comment|// add to the cache a fallback with keyword null
name|globalConfMap
operator|.
name|put
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_POLICY_KEY
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|globalPolicyMap
operator|.
name|put
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_POLICY_KEY
argument_list|,
name|policy
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

