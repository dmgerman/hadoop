begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.api
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
package|;
end_package

begin_comment
comment|/**  * These are the keys valid in resource options  *  /*   Container failure window.   The window is calculated in minutes as as (days * 24 *60 + hours* 24 + minutes)   Every interval of this period after the AM is started/restarted becomes  the time period in which the CONTAINER_FAILURE_THRESHOLD value is calculated.    After the window limit is reached, the failure counts are reset. This  is not a sliding window/moving average policy, simply a rule such as  "every six hours the failure count is reset"<pre>  ===========================================================================</pre>   */
end_comment

begin_interface
DECL|interface|ResourceKeys
specifier|public
interface|interface
name|ResourceKeys
block|{
comment|/**    * #of instances of a component: {@value}    *   */
DECL|field|COMPONENT_INSTANCES
name|String
name|COMPONENT_INSTANCES
init|=
literal|"yarn.component.instances"
decl_stmt|;
comment|/**    * Whether to use unique names for each instance of a component: {@value}    */
DECL|field|UNIQUE_NAMES
name|String
name|UNIQUE_NAMES
init|=
literal|"component.unique.names"
decl_stmt|;
comment|/**    *  Amount of memory to ask YARN for in MB.    *<i>Important:</i> this may be a hard limit on the    *  amount of RAM that the service can use    *  {@value}    */
DECL|field|YARN_MEMORY
name|String
name|YARN_MEMORY
init|=
literal|"yarn.memory"
decl_stmt|;
comment|/** {@value} */
DECL|field|DEF_YARN_MEMORY
name|int
name|DEF_YARN_MEMORY
init|=
literal|256
decl_stmt|;
comment|/**    * Number of cores/virtual cores to ask YARN for    *  {@value}    */
DECL|field|YARN_CORES
name|String
name|YARN_CORES
init|=
literal|"yarn.vcores"
decl_stmt|;
comment|/**    * If normalization is set to false, then if the resource (memory and/or    * vcore) requested by a role is higher than YARN limits, then the resource    * request is not normalized. If this causes failures at the YARN level then    * applications are expecting that to happen. Default value is true.    */
DECL|field|YARN_RESOURCE_NORMALIZATION_ENABLED
name|String
name|YARN_RESOURCE_NORMALIZATION_ENABLED
init|=
literal|"yarn.resource.normalization.enabled"
decl_stmt|;
comment|/**    * Number of disks per instance to ask YARN for    *  {@value}    */
DECL|field|YARN_DISKS
name|String
name|YARN_DISKS
init|=
literal|"yarn.disks.count-per-instance"
decl_stmt|;
comment|/**    * Disk size per disk to ask YARN for    *  {@value}    */
DECL|field|YARN_DISK_SIZE
name|String
name|YARN_DISK_SIZE
init|=
literal|"yarn.disk.size"
decl_stmt|;
comment|/** {@value} */
DECL|field|DEF_YARN_CORES
name|int
name|DEF_YARN_CORES
init|=
literal|1
decl_stmt|;
comment|/**    * Label expression that this container must satisfy    *  {@value}    */
DECL|field|YARN_LABEL_EXPRESSION
name|String
name|YARN_LABEL_EXPRESSION
init|=
literal|"yarn.label.expression"
decl_stmt|;
comment|/** default label expression: */
DECL|field|DEF_YARN_LABEL_EXPRESSION
name|String
name|DEF_YARN_LABEL_EXPRESSION
init|=
literal|null
decl_stmt|;
comment|/**    * Constant to indicate that the requirements of a YARN resource limit    * (cores, memory, ...) should be set to the maximum allowed by    * the queue into which the YARN container requests are placed.    */
DECL|field|YARN_RESOURCE_MAX
name|String
name|YARN_RESOURCE_MAX
init|=
literal|"max"
decl_stmt|;
comment|/**    * Mandatory property for all roles    * 1. this must be defined.    * 2. this must be>= 1    * 3. this must not match any other role priority in the cluster.    */
DECL|field|COMPONENT_PRIORITY
name|String
name|COMPONENT_PRIORITY
init|=
literal|"yarn.role.priority"
decl_stmt|;
comment|/**    * placement policy    */
DECL|field|COMPONENT_PLACEMENT_POLICY
name|String
name|COMPONENT_PLACEMENT_POLICY
init|=
literal|"yarn.component.placement.policy"
decl_stmt|;
comment|/**    * Maximum number of node failures that can be tolerated by a component on a specific node    */
DECL|field|NODE_FAILURE_THRESHOLD
name|String
name|NODE_FAILURE_THRESHOLD
init|=
literal|"yarn.node.failure.threshold"
decl_stmt|;
comment|/**    * maximum number of failed containers (in a single role)    * before the cluster is deemed to have failed {@value}    */
DECL|field|CONTAINER_FAILURE_THRESHOLD
name|String
name|CONTAINER_FAILURE_THRESHOLD
init|=
literal|"yarn.container.failure.threshold"
decl_stmt|;
comment|/**    * prefix for the time of the container failure reset window.    * {@value}    */
DECL|field|CONTAINER_FAILURE_WINDOW
name|String
name|CONTAINER_FAILURE_WINDOW
init|=
literal|"yarn.container.failure.window"
decl_stmt|;
DECL|field|DEFAULT_CONTAINER_FAILURE_WINDOW_DAYS
name|long
name|DEFAULT_CONTAINER_FAILURE_WINDOW_DAYS
init|=
literal|0
decl_stmt|;
DECL|field|DEFAULT_CONTAINER_FAILURE_WINDOW_HOURS
name|long
name|DEFAULT_CONTAINER_FAILURE_WINDOW_HOURS
init|=
literal|6
decl_stmt|;
DECL|field|DEFAULT_CONTAINER_FAILURE_WINDOW_MINUTES
name|long
name|DEFAULT_CONTAINER_FAILURE_WINDOW_MINUTES
init|=
literal|0
decl_stmt|;
comment|/**    * Default failure threshold: {@value}    */
DECL|field|DEFAULT_CONTAINER_FAILURE_THRESHOLD
name|int
name|DEFAULT_CONTAINER_FAILURE_THRESHOLD
init|=
literal|5
decl_stmt|;
comment|/**    * Default node failure threshold for a component instance: {@value}    * Should to be lower than default component failure threshold to allow    * the component to start elsewhere    */
DECL|field|DEFAULT_NODE_FAILURE_THRESHOLD
name|int
name|DEFAULT_NODE_FAILURE_THRESHOLD
init|=
literal|3
decl_stmt|;
comment|/**    * Failure threshold is unlimited: {@value}    */
DECL|field|NODE_FAILURE_THRESHOLD_UNLIMITED
name|int
name|NODE_FAILURE_THRESHOLD_UNLIMITED
init|=
operator|-
literal|1
decl_stmt|;
comment|/**    * Time in seconds to escalate placement delay    */
DECL|field|PLACEMENT_ESCALATE_DELAY
name|String
name|PLACEMENT_ESCALATE_DELAY
init|=
literal|"yarn.placement.escalate.seconds"
decl_stmt|;
comment|/**    * Time to have a strict placement policy outstanding before     * downgrading to a lax placement (for those components which permit that).    *<ol>    *<li>For strictly placed components, there's no relaxation.</li>    *<li>For components with no locality, there's no need to relax</li>    *</ol>    *     */
DECL|field|DEFAULT_PLACEMENT_ESCALATE_DELAY_SECONDS
name|int
name|DEFAULT_PLACEMENT_ESCALATE_DELAY_SECONDS
init|=
literal|30
decl_stmt|;
comment|/**    * Log aggregation include, exclude patterns    */
DECL|field|YARN_LOG_INCLUDE_PATTERNS
name|String
name|YARN_LOG_INCLUDE_PATTERNS
init|=
literal|"yarn.log.include.patterns"
decl_stmt|;
DECL|field|YARN_LOG_EXCLUDE_PATTERNS
name|String
name|YARN_LOG_EXCLUDE_PATTERNS
init|=
literal|"yarn.log.exclude.patterns"
decl_stmt|;
DECL|field|YARN_PROFILE_NAME
name|String
name|YARN_PROFILE_NAME
init|=
literal|"yarn.resource-profile-name"
decl_stmt|;
comment|/**    * Window of time where application master's failure count    * can be reset to 0.    */
DECL|field|YARN_RESOURCEMANAGER_AM_RETRY_COUNT_WINDOW_MS
name|String
name|YARN_RESOURCEMANAGER_AM_RETRY_COUNT_WINDOW_MS
init|=
literal|"yarn.resourcemanager.am.retry-count-window-ms"
decl_stmt|;
comment|/**    * The default window for Slider.    */
DECL|field|DEFAULT_AM_RETRY_COUNT_WINDOW_MS
name|long
name|DEFAULT_AM_RETRY_COUNT_WINDOW_MS
init|=
literal|300000
decl_stmt|;
block|}
end_interface

end_unit

