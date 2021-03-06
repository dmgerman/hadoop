begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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

begin_comment
comment|/**  * Place holder for cluster level configuration keys.  *   * The keys should have "mapreduce.cluster." as the prefix.   *  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|MRConfig
specifier|public
interface|interface
name|MRConfig
block|{
comment|// Cluster-level configuration parameters
DECL|field|TEMP_DIR
specifier|public
specifier|static
specifier|final
name|String
name|TEMP_DIR
init|=
literal|"mapreduce.cluster.temp.dir"
decl_stmt|;
DECL|field|LOCAL_DIR
specifier|public
specifier|static
specifier|final
name|String
name|LOCAL_DIR
init|=
literal|"mapreduce.cluster.local.dir"
decl_stmt|;
DECL|field|MAPMEMORY_MB
specifier|public
specifier|static
specifier|final
name|String
name|MAPMEMORY_MB
init|=
literal|"mapreduce.cluster.mapmemory.mb"
decl_stmt|;
DECL|field|REDUCEMEMORY_MB
specifier|public
specifier|static
specifier|final
name|String
name|REDUCEMEMORY_MB
init|=
literal|"mapreduce.cluster.reducememory.mb"
decl_stmt|;
DECL|field|MR_ACLS_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|MR_ACLS_ENABLED
init|=
literal|"mapreduce.cluster.acls.enabled"
decl_stmt|;
DECL|field|MR_ADMINS
specifier|public
specifier|static
specifier|final
name|String
name|MR_ADMINS
init|=
literal|"mapreduce.cluster.administrators"
decl_stmt|;
annotation|@
name|Deprecated
DECL|field|MR_SUPERGROUP
specifier|public
specifier|static
specifier|final
name|String
name|MR_SUPERGROUP
init|=
literal|"mapreduce.cluster.permissions.supergroup"
decl_stmt|;
comment|//Delegation token related keys
DECL|field|DELEGATION_KEY_UPDATE_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_KEY_UPDATE_INTERVAL_KEY
init|=
literal|"mapreduce.cluster.delegation.key.update-interval"
decl_stmt|;
DECL|field|DELEGATION_KEY_UPDATE_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|DELEGATION_KEY_UPDATE_INTERVAL_DEFAULT
init|=
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 1 day
DECL|field|DELEGATION_TOKEN_RENEW_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_RENEW_INTERVAL_KEY
init|=
literal|"mapreduce.cluster.delegation.token.renew-interval"
decl_stmt|;
DECL|field|DELEGATION_TOKEN_RENEW_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|DELEGATION_TOKEN_RENEW_INTERVAL_DEFAULT
init|=
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 1 day
DECL|field|DELEGATION_TOKEN_MAX_LIFETIME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_MAX_LIFETIME_KEY
init|=
literal|"mapreduce.cluster.delegation.token.max-lifetime"
decl_stmt|;
DECL|field|DELEGATION_TOKEN_MAX_LIFETIME_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|DELEGATION_TOKEN_MAX_LIFETIME_DEFAULT
init|=
literal|7
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 7 days
DECL|field|RESOURCE_CALCULATOR_PROCESS_TREE
specifier|public
specifier|static
specifier|final
name|String
name|RESOURCE_CALCULATOR_PROCESS_TREE
init|=
literal|"mapreduce.job.process-tree.class"
decl_stmt|;
DECL|field|STATIC_RESOLUTIONS
specifier|public
specifier|static
specifier|final
name|String
name|STATIC_RESOLUTIONS
init|=
literal|"mapreduce.job.net.static.resolutions"
decl_stmt|;
DECL|field|MASTER_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|MASTER_ADDRESS
init|=
literal|"mapreduce.jobtracker.address"
decl_stmt|;
DECL|field|MASTER_USER_NAME
specifier|public
specifier|static
specifier|final
name|String
name|MASTER_USER_NAME
init|=
literal|"mapreduce.jobtracker.kerberos.principal"
decl_stmt|;
DECL|field|FRAMEWORK_NAME
specifier|public
specifier|static
specifier|final
name|String
name|FRAMEWORK_NAME
init|=
literal|"mapreduce.framework.name"
decl_stmt|;
DECL|field|CLASSIC_FRAMEWORK_NAME
specifier|public
specifier|static
specifier|final
name|String
name|CLASSIC_FRAMEWORK_NAME
init|=
literal|"classic"
decl_stmt|;
DECL|field|YARN_FRAMEWORK_NAME
specifier|public
specifier|static
specifier|final
name|String
name|YARN_FRAMEWORK_NAME
init|=
literal|"yarn"
decl_stmt|;
DECL|field|LOCAL_FRAMEWORK_NAME
specifier|public
specifier|static
specifier|final
name|String
name|LOCAL_FRAMEWORK_NAME
init|=
literal|"local"
decl_stmt|;
DECL|field|TASK_LOCAL_OUTPUT_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|TASK_LOCAL_OUTPUT_CLASS
init|=
literal|"mapreduce.task.local.output.class"
decl_stmt|;
DECL|field|PROGRESS_STATUS_LEN_LIMIT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|PROGRESS_STATUS_LEN_LIMIT_KEY
init|=
literal|"mapreduce.task.max.status.length"
decl_stmt|;
DECL|field|PROGRESS_STATUS_LEN_LIMIT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|PROGRESS_STATUS_LEN_LIMIT_DEFAULT
init|=
literal|512
decl_stmt|;
DECL|field|MAX_BLOCK_LOCATIONS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|MAX_BLOCK_LOCATIONS_DEFAULT
init|=
literal|15
decl_stmt|;
DECL|field|MAX_BLOCK_LOCATIONS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|MAX_BLOCK_LOCATIONS_KEY
init|=
literal|"mapreduce.job.max.split.locations"
decl_stmt|;
DECL|field|SHUFFLE_SSL_ENABLED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SHUFFLE_SSL_ENABLED_KEY
init|=
literal|"mapreduce.shuffle.ssl.enabled"
decl_stmt|;
DECL|field|SHUFFLE_SSL_ENABLED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|SHUFFLE_SSL_ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|SHUFFLE_CONSUMER_PLUGIN
specifier|public
specifier|static
specifier|final
name|String
name|SHUFFLE_CONSUMER_PLUGIN
init|=
literal|"mapreduce.job.reduce.shuffle.consumer.plugin.class"
decl_stmt|;
comment|/**    * Configuration key to enable/disable IFile readahead.    */
DECL|field|MAPRED_IFILE_READAHEAD
specifier|public
specifier|static
specifier|final
name|String
name|MAPRED_IFILE_READAHEAD
init|=
literal|"mapreduce.ifile.readahead"
decl_stmt|;
DECL|field|DEFAULT_MAPRED_IFILE_READAHEAD
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_MAPRED_IFILE_READAHEAD
init|=
literal|true
decl_stmt|;
comment|/**    * Configuration key to set the IFile readahead length in bytes.    */
DECL|field|MAPRED_IFILE_READAHEAD_BYTES
specifier|public
specifier|static
specifier|final
name|String
name|MAPRED_IFILE_READAHEAD_BYTES
init|=
literal|"mapreduce.ifile.readahead.bytes"
decl_stmt|;
DECL|field|DEFAULT_MAPRED_IFILE_READAHEAD_BYTES
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAPRED_IFILE_READAHEAD_BYTES
init|=
literal|4
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|/**    * Whether users are explicitly trying to control resource monitoring    * configuration for the MiniMRCluster. Disabled by default.    */
DECL|field|MAPREDUCE_MINICLUSTER_CONTROL_RESOURCE_MONITORING
specifier|public
specifier|static
specifier|final
name|String
name|MAPREDUCE_MINICLUSTER_CONTROL_RESOURCE_MONITORING
init|=
literal|"mapreduce.minicluster.control-resource-monitoring"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
DECL|field|DEFAULT_MAPREDUCE_MINICLUSTER_CONTROL_RESOURCE_MONITORING
name|DEFAULT_MAPREDUCE_MINICLUSTER_CONTROL_RESOURCE_MONITORING
init|=
literal|false
decl_stmt|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|field|MAPREDUCE_APP_SUBMISSION_CROSS_PLATFORM
specifier|public
specifier|static
specifier|final
name|String
name|MAPREDUCE_APP_SUBMISSION_CROSS_PLATFORM
init|=
literal|"mapreduce.app-submission.cross-platform"
decl_stmt|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|field|DEFAULT_MAPREDUCE_APP_SUBMISSION_CROSS_PLATFORM
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_MAPREDUCE_APP_SUBMISSION_CROSS_PLATFORM
init|=
literal|false
decl_stmt|;
comment|/**    * Enable application master webapp ui actions.    */
DECL|field|MASTER_WEBAPP_UI_ACTIONS_ENABLED
name|String
name|MASTER_WEBAPP_UI_ACTIONS_ENABLED
init|=
literal|"mapreduce.webapp.ui-actions.enabled"
decl_stmt|;
DECL|field|DEFAULT_MASTER_WEBAPP_UI_ACTIONS_ENABLED
name|boolean
name|DEFAULT_MASTER_WEBAPP_UI_ACTIONS_ENABLED
init|=
literal|true
decl_stmt|;
block|}
end_interface

end_unit

