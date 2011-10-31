begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
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
name|base
operator|.
name|Splitter
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

begin_class
DECL|class|YarnConfiguration
specifier|public
class|class
name|YarnConfiguration
extends|extends
name|Configuration
block|{
DECL|field|ADDR_SPLITTER
specifier|private
specifier|static
specifier|final
name|Splitter
name|ADDR_SPLITTER
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|':'
argument_list|)
operator|.
name|trimResults
argument_list|()
decl_stmt|;
DECL|field|JOINER
specifier|private
specifier|static
specifier|final
name|Joiner
name|JOINER
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|""
argument_list|)
decl_stmt|;
DECL|field|YARN_DEFAULT_XML_FILE
specifier|private
specifier|static
specifier|final
name|String
name|YARN_DEFAULT_XML_FILE
init|=
literal|"yarn-default.xml"
decl_stmt|;
DECL|field|YARN_SITE_XML_FILE
specifier|private
specifier|static
specifier|final
name|String
name|YARN_SITE_XML_FILE
init|=
literal|"yarn-site.xml"
decl_stmt|;
static|static
block|{
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
name|YARN_DEFAULT_XML_FILE
argument_list|)
expr_stmt|;
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
name|YARN_SITE_XML_FILE
argument_list|)
expr_stmt|;
block|}
comment|//Configurations
DECL|field|YARN_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|YARN_PREFIX
init|=
literal|"yarn."
decl_stmt|;
comment|/** Delay before deleting resource to ease debugging of NM issues */
DECL|field|DEBUG_NM_DELETE_DELAY_SEC
specifier|public
specifier|static
specifier|final
name|String
name|DEBUG_NM_DELETE_DELAY_SEC
init|=
name|YarnConfiguration
operator|.
name|NM_PREFIX
operator|+
literal|"delete.debug-delay-sec"
decl_stmt|;
comment|////////////////////////////////
comment|// IPC Configs
comment|////////////////////////////////
DECL|field|IPC_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|IPC_PREFIX
init|=
name|YARN_PREFIX
operator|+
literal|"ipc."
decl_stmt|;
comment|/** Factory to create client IPC classes.*/
DECL|field|IPC_CLIENT_FACTORY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_FACTORY
init|=
name|IPC_PREFIX
operator|+
literal|"client.factory.class"
decl_stmt|;
comment|/** Type of serialization to use.*/
DECL|field|IPC_SERIALIZER_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|IPC_SERIALIZER_TYPE
init|=
name|IPC_PREFIX
operator|+
literal|"serializer.type"
decl_stmt|;
DECL|field|DEFAULT_IPC_SERIALIZER_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_IPC_SERIALIZER_TYPE
init|=
literal|"protocolbuffers"
decl_stmt|;
comment|/** Factory to create server IPC classes.*/
DECL|field|IPC_SERVER_FACTORY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_SERVER_FACTORY
init|=
name|IPC_PREFIX
operator|+
literal|"server.factory.class"
decl_stmt|;
comment|/** Factory to create IPC exceptions.*/
DECL|field|IPC_EXCEPTION_FACTORY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_EXCEPTION_FACTORY
init|=
name|IPC_PREFIX
operator|+
literal|"exception.factory.class"
decl_stmt|;
comment|/** Factory to create serializeable records.*/
DECL|field|IPC_RECORD_FACTORY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_RECORD_FACTORY
init|=
name|IPC_PREFIX
operator|+
literal|"record.factory.class"
decl_stmt|;
comment|/** RPC class implementation*/
DECL|field|IPC_RPC_IMPL
specifier|public
specifier|static
specifier|final
name|String
name|IPC_RPC_IMPL
init|=
name|IPC_PREFIX
operator|+
literal|"rpc.class"
decl_stmt|;
DECL|field|DEFAULT_IPC_RPC_IMPL
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_IPC_RPC_IMPL
init|=
literal|"org.apache.hadoop.yarn.ipc.HadoopYarnProtoRPC"
decl_stmt|;
comment|////////////////////////////////
comment|// Resource Manager Configs
comment|////////////////////////////////
DECL|field|RM_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|RM_PREFIX
init|=
literal|"yarn.resourcemanager."
decl_stmt|;
comment|/** The address of the applications manager interface in the RM.*/
DECL|field|RM_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|RM_ADDRESS
init|=
name|RM_PREFIX
operator|+
literal|"address"
decl_stmt|;
DECL|field|DEFAULT_RM_PORT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_PORT
init|=
literal|8040
decl_stmt|;
DECL|field|DEFAULT_RM_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_RM_ADDRESS
init|=
literal|"0.0.0.0:"
operator|+
name|DEFAULT_RM_PORT
decl_stmt|;
comment|/** The number of threads used to handle applications manager requests.*/
DECL|field|RM_CLIENT_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|RM_CLIENT_THREAD_COUNT
init|=
name|RM_PREFIX
operator|+
literal|"client.thread-count"
decl_stmt|;
DECL|field|DEFAULT_RM_CLIENT_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_CLIENT_THREAD_COUNT
init|=
literal|10
decl_stmt|;
comment|/** The expiry interval for application master reporting.*/
DECL|field|RM_AM_EXPIRY_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|RM_AM_EXPIRY_INTERVAL_MS
init|=
name|RM_PREFIX
operator|+
literal|"am.liveness-monitor.expiry-interval-ms"
decl_stmt|;
DECL|field|DEFAULT_RM_AM_EXPIRY_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_AM_EXPIRY_INTERVAL_MS
init|=
literal|600000
decl_stmt|;
comment|/** The Kerberos principal for the resource manager.*/
DECL|field|RM_PRINCIPAL
specifier|public
specifier|static
specifier|final
name|String
name|RM_PRINCIPAL
init|=
name|RM_PREFIX
operator|+
literal|"principal"
decl_stmt|;
comment|/** The address of the scheduler interface.*/
DECL|field|RM_SCHEDULER_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|RM_SCHEDULER_ADDRESS
init|=
name|RM_PREFIX
operator|+
literal|"scheduler.address"
decl_stmt|;
DECL|field|DEFAULT_RM_SCHEDULER_PORT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_SCHEDULER_PORT
init|=
literal|8030
decl_stmt|;
DECL|field|DEFAULT_RM_SCHEDULER_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_RM_SCHEDULER_ADDRESS
init|=
literal|"0.0.0.0:"
operator|+
name|DEFAULT_RM_SCHEDULER_PORT
decl_stmt|;
comment|/** Number of threads to handle scheduler interface.*/
DECL|field|RM_SCHEDULER_CLIENT_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|RM_SCHEDULER_CLIENT_THREAD_COUNT
init|=
name|RM_PREFIX
operator|+
literal|"scheduler.client.thread-count"
decl_stmt|;
DECL|field|DEFAULT_RM_SCHEDULER_CLIENT_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_SCHEDULER_CLIENT_THREAD_COUNT
init|=
literal|10
decl_stmt|;
comment|/** The address of the RM web application.*/
DECL|field|RM_WEBAPP_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|RM_WEBAPP_ADDRESS
init|=
name|RM_PREFIX
operator|+
literal|"webapp.address"
decl_stmt|;
DECL|field|DEFAULT_RM_WEBAPP_PORT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_WEBAPP_PORT
init|=
literal|8088
decl_stmt|;
DECL|field|DEFAULT_RM_WEBAPP_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_RM_WEBAPP_ADDRESS
init|=
literal|"0.0.0.0:"
operator|+
name|DEFAULT_RM_WEBAPP_PORT
decl_stmt|;
DECL|field|RM_RESOURCE_TRACKER_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|RM_RESOURCE_TRACKER_ADDRESS
init|=
name|RM_PREFIX
operator|+
literal|"resource-tracker.address"
decl_stmt|;
DECL|field|DEFAULT_RM_RESOURCE_TRACKER_PORT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_RESOURCE_TRACKER_PORT
init|=
literal|8025
decl_stmt|;
DECL|field|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
init|=
literal|"0.0.0.0:"
operator|+
name|DEFAULT_RM_RESOURCE_TRACKER_PORT
decl_stmt|;
comment|/** Are acls enabled.*/
DECL|field|YARN_ACL_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|YARN_ACL_ENABLE
init|=
name|YARN_PREFIX
operator|+
literal|"acl.enable"
decl_stmt|;
DECL|field|DEFAULT_YARN_ACL_ENABLE
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_YARN_ACL_ENABLE
init|=
literal|true
decl_stmt|;
comment|/** ACL of who can be admin of YARN cluster.*/
DECL|field|YARN_ADMIN_ACL
specifier|public
specifier|static
specifier|final
name|String
name|YARN_ADMIN_ACL
init|=
name|YARN_PREFIX
operator|+
literal|"admin.acl"
decl_stmt|;
DECL|field|DEFAULT_YARN_ADMIN_ACL
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_YARN_ADMIN_ACL
init|=
literal|"*"
decl_stmt|;
comment|/** ACL used in case none is found. Allows nothing. */
DECL|field|DEFAULT_YARN_APP_ACL
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_YARN_APP_ACL
init|=
literal|" "
decl_stmt|;
comment|/** The address of the RM admin interface.*/
DECL|field|RM_ADMIN_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|RM_ADMIN_ADDRESS
init|=
name|RM_PREFIX
operator|+
literal|"admin.address"
decl_stmt|;
DECL|field|DEFAULT_RM_ADMIN_PORT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_ADMIN_PORT
init|=
literal|8141
decl_stmt|;
DECL|field|DEFAULT_RM_ADMIN_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_RM_ADMIN_ADDRESS
init|=
literal|"0.0.0.0:"
operator|+
name|DEFAULT_RM_ADMIN_PORT
decl_stmt|;
comment|/**Number of threads used to handle RM admin interface.*/
DECL|field|RM_ADMIN_CLIENT_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|RM_ADMIN_CLIENT_THREAD_COUNT
init|=
name|RM_PREFIX
operator|+
literal|"admin.client.thread-count"
decl_stmt|;
DECL|field|DEFAULT_RM_ADMIN_CLIENT_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_ADMIN_CLIENT_THREAD_COUNT
init|=
literal|1
decl_stmt|;
comment|/** The maximum number of application master retries.*/
DECL|field|RM_AM_MAX_RETRIES
specifier|public
specifier|static
specifier|final
name|String
name|RM_AM_MAX_RETRIES
init|=
name|RM_PREFIX
operator|+
literal|"am.max-retries"
decl_stmt|;
DECL|field|DEFAULT_RM_AM_MAX_RETRIES
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_AM_MAX_RETRIES
init|=
literal|1
decl_stmt|;
comment|/** The keytab for the resource manager.*/
DECL|field|RM_KEYTAB
specifier|public
specifier|static
specifier|final
name|String
name|RM_KEYTAB
init|=
name|RM_PREFIX
operator|+
literal|"keytab"
decl_stmt|;
comment|/** How long to wait until a node manager is considered dead.*/
DECL|field|RM_NM_EXPIRY_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|RM_NM_EXPIRY_INTERVAL_MS
init|=
name|RM_PREFIX
operator|+
literal|"nm.liveness-monitor.expiry-interval-ms"
decl_stmt|;
DECL|field|DEFAULT_RM_NM_EXPIRY_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_NM_EXPIRY_INTERVAL_MS
init|=
literal|600000
decl_stmt|;
comment|/** How long to wait until a container is considered dead.*/
DECL|field|RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS
init|=
name|RM_PREFIX
operator|+
literal|"rm.container-allocation.expiry-interval-ms"
decl_stmt|;
DECL|field|DEFAULT_RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS
init|=
literal|600000
decl_stmt|;
comment|/** Path to file with nodes to include.*/
DECL|field|RM_NODES_INCLUDE_FILE_PATH
specifier|public
specifier|static
specifier|final
name|String
name|RM_NODES_INCLUDE_FILE_PATH
init|=
name|RM_PREFIX
operator|+
literal|"nodes.include-path"
decl_stmt|;
DECL|field|DEFAULT_RM_NODES_INCLUDE_FILE_PATH
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_RM_NODES_INCLUDE_FILE_PATH
init|=
literal|""
decl_stmt|;
comment|/** Path to file with nodes to exclude.*/
DECL|field|RM_NODES_EXCLUDE_FILE_PATH
specifier|public
specifier|static
specifier|final
name|String
name|RM_NODES_EXCLUDE_FILE_PATH
init|=
name|RM_PREFIX
operator|+
literal|"nodes.exclude-path"
decl_stmt|;
DECL|field|DEFAULT_RM_NODES_EXCLUDE_FILE_PATH
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_RM_NODES_EXCLUDE_FILE_PATH
init|=
literal|""
decl_stmt|;
comment|/** Number of threads to handle resource tracker calls.*/
DECL|field|RM_RESOURCE_TRACKER_CLIENT_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|RM_RESOURCE_TRACKER_CLIENT_THREAD_COUNT
init|=
name|RM_PREFIX
operator|+
literal|"resource-tracker.client.thread-count"
decl_stmt|;
DECL|field|DEFAULT_RM_RESOURCE_TRACKER_CLIENT_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_RESOURCE_TRACKER_CLIENT_THREAD_COUNT
init|=
literal|10
decl_stmt|;
comment|/** The class to use as the resource scheduler.*/
DECL|field|RM_SCHEDULER
specifier|public
specifier|static
specifier|final
name|String
name|RM_SCHEDULER
init|=
name|RM_PREFIX
operator|+
literal|"scheduler.class"
decl_stmt|;
comment|/** The class to use as the persistent store.*/
DECL|field|RM_STORE
specifier|public
specifier|static
specifier|final
name|String
name|RM_STORE
init|=
name|RM_PREFIX
operator|+
literal|"store.class"
decl_stmt|;
comment|/** The address of the zookeeper instance to use with ZK store.*/
DECL|field|RM_ZK_STORE_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|RM_ZK_STORE_ADDRESS
init|=
name|RM_PREFIX
operator|+
literal|"zookeeper-store.address"
decl_stmt|;
comment|/** The zookeeper session timeout for the zookeeper store.*/
DECL|field|RM_ZK_STORE_TIMEOUT_MS
specifier|public
specifier|static
specifier|final
name|String
name|RM_ZK_STORE_TIMEOUT_MS
init|=
name|RM_PREFIX
operator|+
literal|"zookeeper-store.session.timeout-ms"
decl_stmt|;
DECL|field|DEFAULT_RM_ZK_STORE_TIMEOUT_MS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_ZK_STORE_TIMEOUT_MS
init|=
literal|60000
decl_stmt|;
comment|/** The maximum number of completed applications RM keeps. */
DECL|field|RM_MAX_COMPLETED_APPLICATIONS
specifier|public
specifier|static
specifier|final
name|String
name|RM_MAX_COMPLETED_APPLICATIONS
init|=
name|RM_PREFIX
operator|+
literal|"max-completed-applications"
decl_stmt|;
DECL|field|DEFAULT_RM_MAX_COMPLETED_APPLICATIONS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_MAX_COMPLETED_APPLICATIONS
init|=
literal|10000
decl_stmt|;
comment|/** Default application name */
DECL|field|DEFAULT_APPLICATION_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_APPLICATION_NAME
init|=
literal|"N/A"
decl_stmt|;
comment|/** Default queue name */
DECL|field|DEFAULT_QUEUE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_QUEUE_NAME
init|=
literal|"default"
decl_stmt|;
comment|////////////////////////////////
comment|// Node Manager Configs
comment|////////////////////////////////
comment|/** Prefix for all node manager configs.*/
DECL|field|NM_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|NM_PREFIX
init|=
literal|"yarn.nodemanager."
decl_stmt|;
comment|/** Environment variables that will be sent to containers.*/
DECL|field|NM_ADMIN_USER_ENV
specifier|public
specifier|static
specifier|final
name|String
name|NM_ADMIN_USER_ENV
init|=
name|NM_PREFIX
operator|+
literal|"admin-env"
decl_stmt|;
DECL|field|DEFAULT_NM_ADMIN_USER_ENV
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NM_ADMIN_USER_ENV
init|=
literal|"MALLOC_ARENA_MAX=$MALLOC_ARENA_MAX"
decl_stmt|;
comment|/** Environment variables that containers may override rather than use NodeManager's default.*/
DECL|field|NM_ENV_WHITELIST
specifier|public
specifier|static
specifier|final
name|String
name|NM_ENV_WHITELIST
init|=
name|NM_PREFIX
operator|+
literal|"env-whitelist"
decl_stmt|;
DECL|field|DEFAULT_NM_ENV_WHITELIST
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NM_ENV_WHITELIST
init|=
literal|"JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,YARN_HOME"
decl_stmt|;
comment|/** address of node manager IPC.*/
DECL|field|NM_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|NM_ADDRESS
init|=
name|NM_PREFIX
operator|+
literal|"address"
decl_stmt|;
DECL|field|DEFAULT_NM_PORT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NM_PORT
init|=
literal|0
decl_stmt|;
DECL|field|DEFAULT_NM_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NM_ADDRESS
init|=
literal|"0.0.0.0:"
operator|+
name|DEFAULT_NM_PORT
decl_stmt|;
comment|/** who will execute(launch) the containers.*/
DECL|field|NM_CONTAINER_EXECUTOR
specifier|public
specifier|static
specifier|final
name|String
name|NM_CONTAINER_EXECUTOR
init|=
name|NM_PREFIX
operator|+
literal|"container-executor.class"
decl_stmt|;
comment|/** Number of threads container manager uses.*/
DECL|field|NM_CONTAINER_MGR_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|NM_CONTAINER_MGR_THREAD_COUNT
init|=
name|NM_PREFIX
operator|+
literal|"container-manager.thread-count"
decl_stmt|;
DECL|field|DEFAULT_NM_CONTAINER_MGR_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NM_CONTAINER_MGR_THREAD_COUNT
init|=
literal|5
decl_stmt|;
comment|/** Number of threads used in cleanup.*/
DECL|field|NM_DELETE_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|NM_DELETE_THREAD_COUNT
init|=
name|NM_PREFIX
operator|+
literal|"delete.thread-count"
decl_stmt|;
DECL|field|DEFAULT_NM_DELETE_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NM_DELETE_THREAD_COUNT
init|=
literal|4
decl_stmt|;
comment|// TODO: Should this instead be dictated by RM?
comment|/** Heartbeat interval to RM*/
DECL|field|NM_TO_RM_HEARTBEAT_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|NM_TO_RM_HEARTBEAT_INTERVAL_MS
init|=
name|NM_PREFIX
operator|+
literal|"heartbeat.interval-ms"
decl_stmt|;
DECL|field|DEFAULT_NM_TO_RM_HEARTBEAT_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NM_TO_RM_HEARTBEAT_INTERVAL_MS
init|=
literal|1000
decl_stmt|;
comment|/** Keytab for NM.*/
DECL|field|NM_KEYTAB
specifier|public
specifier|static
specifier|final
name|String
name|NM_KEYTAB
init|=
name|NM_PREFIX
operator|+
literal|"keytab"
decl_stmt|;
comment|/**List of directories to store localized files in.*/
DECL|field|NM_LOCAL_DIRS
specifier|public
specifier|static
specifier|final
name|String
name|NM_LOCAL_DIRS
init|=
name|NM_PREFIX
operator|+
literal|"local-dirs"
decl_stmt|;
DECL|field|DEFAULT_NM_LOCAL_DIRS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NM_LOCAL_DIRS
init|=
literal|"/tmp/nm-local-dir"
decl_stmt|;
comment|/** Address where the localizer IPC is.*/
DECL|field|NM_LOCALIZER_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|NM_LOCALIZER_ADDRESS
init|=
name|NM_PREFIX
operator|+
literal|"localizer.address"
decl_stmt|;
DECL|field|DEFAULT_NM_LOCALIZER_PORT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NM_LOCALIZER_PORT
init|=
literal|4344
decl_stmt|;
DECL|field|DEFAULT_NM_LOCALIZER_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NM_LOCALIZER_ADDRESS
init|=
literal|"0.0.0.0:"
operator|+
name|DEFAULT_NM_LOCALIZER_PORT
decl_stmt|;
comment|/** Interval in between cache cleanups.*/
DECL|field|NM_LOCALIZER_CACHE_CLEANUP_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|NM_LOCALIZER_CACHE_CLEANUP_INTERVAL_MS
init|=
name|NM_PREFIX
operator|+
literal|"localizer.cache.cleanup.interval-ms"
decl_stmt|;
DECL|field|DEFAULT_NM_LOCALIZER_CACHE_CLEANUP_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_NM_LOCALIZER_CACHE_CLEANUP_INTERVAL_MS
init|=
literal|10
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|/** Target size of localizer cache in MB, per local directory.*/
DECL|field|NM_LOCALIZER_CACHE_TARGET_SIZE_MB
specifier|public
specifier|static
specifier|final
name|String
name|NM_LOCALIZER_CACHE_TARGET_SIZE_MB
init|=
name|NM_PREFIX
operator|+
literal|"localizer.cache.target-size-mb"
decl_stmt|;
DECL|field|DEFAULT_NM_LOCALIZER_CACHE_TARGET_SIZE_MB
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_NM_LOCALIZER_CACHE_TARGET_SIZE_MB
init|=
literal|10
operator|*
literal|1024
decl_stmt|;
comment|/** Number of threads to handle localization requests.*/
DECL|field|NM_LOCALIZER_CLIENT_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|NM_LOCALIZER_CLIENT_THREAD_COUNT
init|=
name|NM_PREFIX
operator|+
literal|"localizer.client.thread-count"
decl_stmt|;
DECL|field|DEFAULT_NM_LOCALIZER_CLIENT_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NM_LOCALIZER_CLIENT_THREAD_COUNT
init|=
literal|5
decl_stmt|;
comment|/** Number of threads to use for localization fetching.*/
DECL|field|NM_LOCALIZER_FETCH_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|NM_LOCALIZER_FETCH_THREAD_COUNT
init|=
name|NM_PREFIX
operator|+
literal|"localizer.fetch.thread-count"
decl_stmt|;
DECL|field|DEFAULT_NM_LOCALIZER_FETCH_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NM_LOCALIZER_FETCH_THREAD_COUNT
init|=
literal|4
decl_stmt|;
comment|/** Where to store container logs.*/
DECL|field|NM_LOG_DIRS
specifier|public
specifier|static
specifier|final
name|String
name|NM_LOG_DIRS
init|=
name|NM_PREFIX
operator|+
literal|"log-dirs"
decl_stmt|;
DECL|field|DEFAULT_NM_LOG_DIRS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NM_LOG_DIRS
init|=
literal|"/tmp/logs"
decl_stmt|;
comment|/** Whether to enable log aggregation */
DECL|field|NM_LOG_AGGREGATION_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|NM_LOG_AGGREGATION_ENABLED
init|=
name|NM_PREFIX
operator|+
literal|"log-aggregation-enable"
decl_stmt|;
DECL|field|DEFAULT_NM_LOG_AGGREGATION_ENABLED
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_NM_LOG_AGGREGATION_ENABLED
init|=
literal|false
decl_stmt|;
comment|/**    * Number of seconds to retain logs on the NodeManager. Only applicable if Log    * aggregation is disabled    */
DECL|field|NM_LOG_RETAIN_SECONDS
specifier|public
specifier|static
specifier|final
name|String
name|NM_LOG_RETAIN_SECONDS
init|=
name|NM_PREFIX
operator|+
literal|"log.retain-seconds"
decl_stmt|;
comment|/**    * Number of threads used in log cleanup. Only applicable if Log aggregation    * is disabled    */
DECL|field|NM_LOG_DELETION_THREADS_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|NM_LOG_DELETION_THREADS_COUNT
init|=
name|NM_PREFIX
operator|+
literal|"log.deletion-threads-count"
decl_stmt|;
DECL|field|DEFAULT_NM_LOG_DELETE_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NM_LOG_DELETE_THREAD_COUNT
init|=
literal|4
decl_stmt|;
comment|/** Where to aggregate logs to.*/
DECL|field|NM_REMOTE_APP_LOG_DIR
specifier|public
specifier|static
specifier|final
name|String
name|NM_REMOTE_APP_LOG_DIR
init|=
name|NM_PREFIX
operator|+
literal|"remote-app-log-dir"
decl_stmt|;
DECL|field|DEFAULT_NM_REMOTE_APP_LOG_DIR
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NM_REMOTE_APP_LOG_DIR
init|=
literal|"/tmp/logs"
decl_stmt|;
comment|/**    * The remote log dir will be created at    * NM_REMOTE_APP_LOG_DIR/${user}/NM_REMOTE_APP_LOG_DIR_SUFFIX/${appId}    */
DECL|field|NM_REMOTE_APP_LOG_DIR_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|NM_REMOTE_APP_LOG_DIR_SUFFIX
init|=
name|NM_PREFIX
operator|+
literal|"remote-app-log-dir-suffix"
decl_stmt|;
DECL|field|DEFAULT_NM_REMOTE_APP_LOG_DIR_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NM_REMOTE_APP_LOG_DIR_SUFFIX
init|=
literal|"logs"
decl_stmt|;
DECL|field|YARN_LOG_SERVER_URL
specifier|public
specifier|static
specifier|final
name|String
name|YARN_LOG_SERVER_URL
init|=
name|YARN_PREFIX
operator|+
literal|"log.server.url"
decl_stmt|;
comment|/** Amount of memory in GB that can be allocated for containers.*/
DECL|field|NM_PMEM_MB
specifier|public
specifier|static
specifier|final
name|String
name|NM_PMEM_MB
init|=
name|NM_PREFIX
operator|+
literal|"resource.memory-mb"
decl_stmt|;
DECL|field|DEFAULT_NM_PMEM_MB
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NM_PMEM_MB
init|=
literal|8
operator|*
literal|1024
decl_stmt|;
DECL|field|NM_VMEM_PMEM_RATIO
specifier|public
specifier|static
specifier|final
name|String
name|NM_VMEM_PMEM_RATIO
init|=
name|NM_PREFIX
operator|+
literal|"vmem-pmem-ratio"
decl_stmt|;
DECL|field|DEFAULT_NM_VMEM_PMEM_RATIO
specifier|public
specifier|static
specifier|final
name|float
name|DEFAULT_NM_VMEM_PMEM_RATIO
init|=
literal|2.1f
decl_stmt|;
comment|/** NM Webapp address.**/
DECL|field|NM_WEBAPP_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|NM_WEBAPP_ADDRESS
init|=
name|NM_PREFIX
operator|+
literal|"webapp.address"
decl_stmt|;
DECL|field|DEFAULT_NM_WEBAPP_PORT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NM_WEBAPP_PORT
init|=
literal|9999
decl_stmt|;
DECL|field|DEFAULT_NM_WEBAPP_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NM_WEBAPP_ADDRESS
init|=
literal|"0.0.0.0:"
operator|+
name|DEFAULT_NM_WEBAPP_PORT
decl_stmt|;
comment|/** How often to monitor containers.*/
DECL|field|NM_CONTAINER_MON_INTERVAL_MS
specifier|public
specifier|final
specifier|static
name|String
name|NM_CONTAINER_MON_INTERVAL_MS
init|=
name|NM_PREFIX
operator|+
literal|"container-monitor.interval-ms"
decl_stmt|;
DECL|field|DEFAULT_NM_CONTAINER_MON_INTERVAL_MS
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_NM_CONTAINER_MON_INTERVAL_MS
init|=
literal|3000
decl_stmt|;
comment|/** Class that calculates containers current resource utilization.*/
DECL|field|NM_CONTAINER_MON_RESOURCE_CALCULATOR
specifier|public
specifier|static
specifier|final
name|String
name|NM_CONTAINER_MON_RESOURCE_CALCULATOR
init|=
name|NM_PREFIX
operator|+
literal|"container-monitor.resource-calculator.class"
decl_stmt|;
comment|/** Frequency of running node health script.*/
DECL|field|NM_HEALTH_CHECK_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|NM_HEALTH_CHECK_INTERVAL_MS
init|=
name|NM_PREFIX
operator|+
literal|"health-checker.interval-ms"
decl_stmt|;
DECL|field|DEFAULT_NM_HEALTH_CHECK_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_NM_HEALTH_CHECK_INTERVAL_MS
init|=
literal|10
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|/** Script time out period.*/
DECL|field|NM_HEALTH_CHECK_SCRIPT_TIMEOUT_MS
specifier|public
specifier|static
specifier|final
name|String
name|NM_HEALTH_CHECK_SCRIPT_TIMEOUT_MS
init|=
name|NM_PREFIX
operator|+
literal|"health-checker.script.timeout-ms"
decl_stmt|;
DECL|field|DEFAULT_NM_HEALTH_CHECK_SCRIPT_TIMEOUT_MS
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_NM_HEALTH_CHECK_SCRIPT_TIMEOUT_MS
init|=
literal|2
operator|*
name|DEFAULT_NM_HEALTH_CHECK_INTERVAL_MS
decl_stmt|;
comment|/** The health check script to run.*/
DECL|field|NM_HEALTH_CHECK_SCRIPT_PATH
specifier|public
specifier|static
specifier|final
name|String
name|NM_HEALTH_CHECK_SCRIPT_PATH
init|=
name|NM_PREFIX
operator|+
literal|"health-checker.script.path"
decl_stmt|;
comment|/** The arguments to pass to the health check script.*/
DECL|field|NM_HEALTH_CHECK_SCRIPT_OPTS
specifier|public
specifier|static
specifier|final
name|String
name|NM_HEALTH_CHECK_SCRIPT_OPTS
init|=
name|NM_PREFIX
operator|+
literal|"health-checker.script.opts"
decl_stmt|;
comment|/** The path to the Linux container executor.*/
DECL|field|NM_LINUX_CONTAINER_EXECUTOR_PATH
specifier|public
specifier|static
specifier|final
name|String
name|NM_LINUX_CONTAINER_EXECUTOR_PATH
init|=
name|NM_PREFIX
operator|+
literal|"linux-container-executor.path"
decl_stmt|;
comment|/**     * The UNIX group that the linux-container-executor should run as.    * This is intended to be set as part of container-executor.cfg.     */
DECL|field|NM_LINUX_CONTAINER_GROUP
specifier|public
specifier|static
specifier|final
name|String
name|NM_LINUX_CONTAINER_GROUP
init|=
name|NM_PREFIX
operator|+
literal|"linux-container-executor.group"
decl_stmt|;
comment|/** T-file compression types used to compress aggregated logs.*/
DECL|field|NM_LOG_AGG_COMPRESSION_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|NM_LOG_AGG_COMPRESSION_TYPE
init|=
name|NM_PREFIX
operator|+
literal|"log-aggregation.compression-type"
decl_stmt|;
DECL|field|DEFAULT_NM_LOG_AGG_COMPRESSION_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NM_LOG_AGG_COMPRESSION_TYPE
init|=
literal|"none"
decl_stmt|;
comment|/** The kerberos principal for the node manager.*/
DECL|field|NM_PRINCIPAL
specifier|public
specifier|static
specifier|final
name|String
name|NM_PRINCIPAL
init|=
name|NM_PREFIX
operator|+
literal|"principal"
decl_stmt|;
DECL|field|NM_AUX_SERVICES
specifier|public
specifier|static
specifier|final
name|String
name|NM_AUX_SERVICES
init|=
name|NM_PREFIX
operator|+
literal|"aux-services"
decl_stmt|;
DECL|field|NM_AUX_SERVICE_FMT
specifier|public
specifier|static
specifier|final
name|String
name|NM_AUX_SERVICE_FMT
init|=
name|NM_PREFIX
operator|+
literal|"aux-services.%s.class"
decl_stmt|;
DECL|field|NM_USER_HOME_DIR
specifier|public
specifier|static
specifier|final
name|String
name|NM_USER_HOME_DIR
init|=
name|NM_PREFIX
operator|+
literal|"user-home-dir"
decl_stmt|;
DECL|field|DEFAULT_NM_USER_HOME_DIR
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NM_USER_HOME_DIR
init|=
literal|"/home/"
decl_stmt|;
DECL|field|INVALID_CONTAINER_EXIT_STATUS
specifier|public
specifier|static
specifier|final
name|int
name|INVALID_CONTAINER_EXIT_STATUS
init|=
operator|-
literal|1000
decl_stmt|;
DECL|field|ABORTED_CONTAINER_EXIT_STATUS
specifier|public
specifier|static
specifier|final
name|int
name|ABORTED_CONTAINER_EXIT_STATUS
init|=
operator|-
literal|100
decl_stmt|;
comment|////////////////////////////////
comment|// Web Proxy Configs
comment|////////////////////////////////
DECL|field|PROXY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_PREFIX
init|=
literal|"yarn.web-proxy."
decl_stmt|;
comment|/** The kerberos principal for the proxy.*/
DECL|field|PROXY_PRINCIPAL
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_PRINCIPAL
init|=
name|PROXY_PREFIX
operator|+
literal|"principal"
decl_stmt|;
comment|/** Keytab for Proxy.*/
DECL|field|PROXY_KEYTAB
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_KEYTAB
init|=
name|PROXY_PREFIX
operator|+
literal|"keytab"
decl_stmt|;
comment|/** The address for the web proxy.*/
DECL|field|PROXY_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_ADDRESS
init|=
name|PROXY_PREFIX
operator|+
literal|"address"
decl_stmt|;
comment|/**    * YARN Service Level Authorization    */
specifier|public
specifier|static
specifier|final
name|String
DECL|field|YARN_SECURITY_SERVICE_AUTHORIZATION_RESOURCETRACKER
name|YARN_SECURITY_SERVICE_AUTHORIZATION_RESOURCETRACKER
init|=
literal|"security.resourcetracker.protocol.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|YARN_SECURITY_SERVICE_AUTHORIZATION_CLIENT_RESOURCEMANAGER
name|YARN_SECURITY_SERVICE_AUTHORIZATION_CLIENT_RESOURCEMANAGER
init|=
literal|"security.client.resourcemanager.protocol.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|YARN_SECURITY_SERVICE_AUTHORIZATION_ADMIN
name|YARN_SECURITY_SERVICE_AUTHORIZATION_ADMIN
init|=
literal|"security.admin.protocol.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|YARN_SECURITY_SERVICE_AUTHORIZATION_APPLICATIONMASTER_RESOURCEMANAGER
name|YARN_SECURITY_SERVICE_AUTHORIZATION_APPLICATIONMASTER_RESOURCEMANAGER
init|=
literal|"security.applicationmaster.resourcemanager.protocol.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|YARN_SECURITY_SERVICE_AUTHORIZATION_CONTAINER_MANAGER
name|YARN_SECURITY_SERVICE_AUTHORIZATION_CONTAINER_MANAGER
init|=
literal|"security.containermanager.protocol.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|YARN_SECURITY_SERVICE_AUTHORIZATION_RESOURCE_LOCALIZER
name|YARN_SECURITY_SERVICE_AUTHORIZATION_RESOURCE_LOCALIZER
init|=
literal|"security.resourcelocalizer.protocol.acl"
decl_stmt|;
comment|/** No. of milliseconds to wait between sending a SIGTERM and SIGKILL    * to a running container */
DECL|field|NM_SLEEP_DELAY_BEFORE_SIGKILL_MS
specifier|public
specifier|static
specifier|final
name|String
name|NM_SLEEP_DELAY_BEFORE_SIGKILL_MS
init|=
name|NM_PREFIX
operator|+
literal|"sleep-delay-before-sigkill.ms"
decl_stmt|;
DECL|field|DEFAULT_NM_SLEEP_DELAY_BEFORE_SIGKILL_MS
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_NM_SLEEP_DELAY_BEFORE_SIGKILL_MS
init|=
literal|250
decl_stmt|;
comment|/** Max time to wait for a process to come up when trying to cleanup    * container resources */
DECL|field|NM_PROCESS_KILL_WAIT_MS
specifier|public
specifier|static
specifier|final
name|String
name|NM_PROCESS_KILL_WAIT_MS
init|=
name|NM_PREFIX
operator|+
literal|"process-kill-wait.ms"
decl_stmt|;
DECL|field|DEFAULT_NM_PROCESS_KILL_WAIT_MS
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_NM_PROCESS_KILL_WAIT_MS
init|=
literal|2000
decl_stmt|;
DECL|method|YarnConfiguration ()
specifier|public
name|YarnConfiguration
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|YarnConfiguration (Configuration conf)
specifier|public
name|YarnConfiguration
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|conf
operator|instanceof
name|YarnConfiguration
operator|)
condition|)
block|{
name|this
operator|.
name|reloadConfiguration
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getProxyHostAndPort (Configuration conf)
specifier|public
specifier|static
name|String
name|getProxyHostAndPort
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|addr
init|=
name|conf
operator|.
name|get
argument_list|(
name|PROXY_ADDRESS
argument_list|)
decl_stmt|;
if|if
condition|(
name|addr
operator|==
literal|null
operator|||
name|addr
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|addr
operator|=
name|getRMWebAppHostAndPort
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
return|return
name|addr
return|;
block|}
DECL|method|getRMWebAppHostAndPort (Configuration conf)
specifier|public
specifier|static
name|String
name|getRMWebAppHostAndPort
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|addr
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_ADDRESS
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|ADDR_SPLITTER
operator|.
name|split
argument_list|(
name|addr
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// ignore the bind host
name|String
name|port
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// Use apps manager address to figure out the host for webapp
name|addr
operator|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_ADDRESS
argument_list|)
expr_stmt|;
name|String
name|host
init|=
name|ADDR_SPLITTER
operator|.
name|split
argument_list|(
name|addr
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|JOINER
operator|.
name|join
argument_list|(
name|host
argument_list|,
literal|":"
argument_list|,
name|port
argument_list|)
return|;
block|}
DECL|method|getRMWebAppURL (Configuration conf)
specifier|public
specifier|static
name|String
name|getRMWebAppURL
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|JOINER
operator|.
name|join
argument_list|(
literal|"http://"
argument_list|,
name|getRMWebAppHostAndPort
argument_list|(
name|conf
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

