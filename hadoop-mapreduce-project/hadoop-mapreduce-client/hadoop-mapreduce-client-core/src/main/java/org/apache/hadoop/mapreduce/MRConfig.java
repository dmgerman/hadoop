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
DECL|field|RESOURCE_CALCULATOR_PLUGIN
specifier|public
specifier|static
specifier|final
name|String
name|RESOURCE_CALCULATOR_PLUGIN
init|=
literal|"mapreduce.job.resourcecalculatorplugin"
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
block|}
end_interface

end_unit

