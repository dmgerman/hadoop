begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|conf
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|TestConfigurationFieldsBase
import|;
end_import

begin_comment
comment|/**  * Unit test class to compare  * {@link org.apache.hadoop.yarn.conf.YarnConfiguration} and  * yarn-default.xml for missing properties.  Currently only throws an error  * if the class is missing a property.  *<p></p>  * Refer to {@link org.apache.hadoop.conf.TestConfigurationFieldsBase}  * for how this class works.  */
end_comment

begin_class
DECL|class|TestYarnConfigurationFields
specifier|public
class|class
name|TestYarnConfigurationFields
extends|extends
name|TestConfigurationFieldsBase
block|{
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"deprecation"
block|,
literal|"methodlength"
block|}
argument_list|)
annotation|@
name|Override
DECL|method|initializeMemberVariables ()
specifier|public
name|void
name|initializeMemberVariables
parameter_list|()
block|{
name|xmlFilename
operator|=
operator|new
name|String
argument_list|(
literal|"yarn-default.xml"
argument_list|)
expr_stmt|;
name|configurationClasses
operator|=
operator|new
name|Class
index|[]
block|{
name|YarnConfiguration
operator|.
name|class
block|}
expr_stmt|;
comment|// Allocate for usage
name|configurationPropsToSkipCompare
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|configurationPrefixToSkipCompare
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
comment|// Set error modes
name|errorIfMissingConfigProps
operator|=
literal|true
expr_stmt|;
name|errorIfMissingXmlProps
operator|=
literal|true
expr_stmt|;
comment|// Specific properties to skip
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_SERVICE_AUTHORIZATION_APPLICATIONCLIENT_PROTOCOL
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_SERVICE_AUTHORIZATION_APPLICATIONMASTER_PROTOCOL
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_SERVICE_AUTHORIZATION_CONTAINER_MANAGEMENT_PROTOCOL
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_SERVICE_AUTHORIZATION_RESOURCE_LOCALIZER
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_SERVICE_AUTHORIZATION_RESOURCEMANAGER_ADMINISTRATION_PROTOCOL
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_SERVICE_AUTHORIZATION_RESOURCETRACKER_PROTOCOL
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_SERVICE_AUTHORIZATION_COLLECTOR_NODEMANAGER_PROTOCOL
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_SERVICE_AUTHORIZATION_DISTRIBUTEDSCHEDULING_PROTOCOL
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_SERVICE_AUTHORIZATION_APPLICATIONMASTER_NODEMANAGER_PROTOCOL
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|CURATOR_LEADER_ELECTOR
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|RM_RESERVATION_SYSTEM_MAX_PERIODICITY
argument_list|)
expr_stmt|;
comment|// Federation default configs to be ignored
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_STATESTORE_CLIENT_CLASS
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_FAILOVER_ENABLED
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_STATESTORE_HEARTBEAT_INTERVAL_SECS
argument_list|)
expr_stmt|;
name|configurationPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_FLUSH_CACHE_FOR_RM_ADDR
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|RM_EPOCH
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|ROUTER_CLIENTRM_ADDRESS
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|ROUTER_RMADMIN_ADDRESS
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|ROUTER_WEBAPP_DEFAULT_INTERCEPTOR_CLASS
argument_list|)
expr_stmt|;
comment|// Federation policies configs to be ignored
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_POLICY_MANAGER
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_POLICY_MANAGER_PARAMS
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_POLICY_KEY
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_POLICY_MANAGER
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_POLICY_MANAGER_PARAMS
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_AMRMPROXY_HB_MAX_WAIT_MS
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_AMRMPROXY_SUBCLUSTER_TIMEOUT
argument_list|)
expr_stmt|;
comment|// Federation StateStore ZK implementation configs to be ignored
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_STATESTORE_ZK_PARENT_PATH
argument_list|)
expr_stmt|;
comment|// Federation StateStore SQL implementation configs to be ignored
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_STATESTORE_SQL_JDBC_CLASS
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_STATESTORE_SQL_JDBC_CLASS
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_STATESTORE_SQL_USERNAME
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_STATESTORE_SQL_PASSWORD
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_STATESTORE_SQL_URL
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_STATESTORE_SQL_MAXCONNECTIONS
argument_list|)
expr_stmt|;
comment|// Ignore blacklisting nodes for AM failures feature since it is still a
comment|// "work in progress"
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|AM_SCHEDULING_NODE_BLACKLISTING_ENABLED
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|AM_SCHEDULING_NODE_BLACKLISTING_DISABLE_THRESHOLD
argument_list|)
expr_stmt|;
comment|// Ignore all YARN Application Timeline Service (version 1) properties
name|configurationPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"yarn.timeline-service."
argument_list|)
expr_stmt|;
comment|// skip deprecated RM_SYSTEM_METRICS_PUBLISHER_ENABLED
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SYSTEM_METRICS_PUBLISHER_ENABLED
argument_list|)
expr_stmt|;
comment|// skip deprecated ZooKeeper settings
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_ADDRESS
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_NUM_RETRIES
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_TIMEOUT_MS
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_RETRY_INTERVAL_MS
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_AUTH
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_ACL
argument_list|)
expr_stmt|;
comment|// Used as Java command line properties, not XML
name|configurationPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"yarn.app.container"
argument_list|)
expr_stmt|;
comment|// Ignore default file name for resource profiles
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_PROFILES_SOURCE_FILE
argument_list|)
expr_stmt|;
comment|// Ignore NodeManager "work in progress" variables
name|configurationPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NETWORK_RESOURCE_ENABLED
argument_list|)
expr_stmt|;
name|configurationPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NETWORK_RESOURCE_INTERFACE
argument_list|)
expr_stmt|;
name|configurationPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NETWORK_RESOURCE_OUTBOUND_BANDWIDTH_MBIT
argument_list|)
expr_stmt|;
name|configurationPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NETWORK_RESOURCE_OUTBOUND_BANDWIDTH_YARN_MBIT
argument_list|)
expr_stmt|;
name|configurationPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|NM_DISK_RESOURCE_ENABLED
argument_list|)
expr_stmt|;
name|configurationPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CPU_RESOURCE_ENABLED
argument_list|)
expr_stmt|;
name|configurationPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NETWORK_TAG_MAPPING_MANAGER
argument_list|)
expr_stmt|;
name|configurationPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NETWORK_TAG_MAPPING_FILE_PATH
argument_list|)
expr_stmt|;
name|configurationPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NETWORK_TAG_PREFIX
argument_list|)
expr_stmt|;
comment|// Ignore all Router Federation variables
name|configurationPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|ROUTER_CLIENTRM_SUBMIT_RETRY
argument_list|)
expr_stmt|;
name|configurationPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|ROUTER_WEBAPP_PARTIAL_RESULTS_ENABLED
argument_list|)
expr_stmt|;
comment|// Set by container-executor.cfg
name|configurationPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|NM_USER_HOME_DIR
argument_list|)
expr_stmt|;
comment|// Ignore deprecated properties
name|configurationPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_CLIENT_APP_SUBMISSION_POLL_INTERVAL_MS
argument_list|)
expr_stmt|;
name|configurationPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|YarnConfiguration
operator|.
name|DISPLAY_APPS_FOR_LOGGED_IN_USER
argument_list|)
expr_stmt|;
comment|// Allocate for usage
name|xmlPropsToSkipCompare
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|xmlPrefixToSkipCompare
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
comment|// Possibly obsolete, but unable to verify 100%
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"yarn.nodemanager.aux-services.mapreduce_shuffle.class"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"yarn.resourcemanager.container.liveness-monitor.interval-ms"
argument_list|)
expr_stmt|;
comment|// Used in the XML file as a variable reference internal to the XML file
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"yarn.nodemanager.hostname"
argument_list|)
expr_stmt|;
comment|// Ignore all YARN Application Timeline Service (version 1) properties
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"yarn.timeline-service"
argument_list|)
expr_stmt|;
comment|// Currently defined in RegistryConstants/core-site.xml
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"hadoop.registry"
argument_list|)
expr_stmt|;
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"yarn.log-aggregation.file-controller.TFile.class"
argument_list|)
expr_stmt|;
comment|// Add the filters used for checking for collision of default values.
name|initDefaultValueCollisionCheck
argument_list|()
expr_stmt|;
block|}
comment|/**    * Add filters used to perform the check of default values collision by    * {@link TestConfigurationFieldsBase#filtersForDefaultValueCollisionCheck}.    */
DECL|method|initDefaultValueCollisionCheck ()
specifier|private
name|void
name|initDefaultValueCollisionCheck
parameter_list|()
block|{
name|filtersForDefaultValueCollisionCheck
operator|.
name|add
argument_list|(
literal|"_PORT"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

