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
comment|/**  * Standard options for roles  */
end_comment

begin_interface
DECL|interface|RoleKeys
specifier|public
interface|interface
name|RoleKeys
block|{
comment|/**    * The name of a role: {@value}    */
DECL|field|ROLE_NAME
name|String
name|ROLE_NAME
init|=
literal|"role.name"
decl_stmt|;
comment|/**    * The group of a role: {@value}    */
DECL|field|ROLE_GROUP
name|String
name|ROLE_GROUP
init|=
literal|"role.group"
decl_stmt|;
comment|/**    * The prefix of a role: {@value}    */
DECL|field|ROLE_PREFIX
name|String
name|ROLE_PREFIX
init|=
literal|"role.prefix"
decl_stmt|;
comment|/**    * Status report: number actually granted : {@value}     */
DECL|field|ROLE_ACTUAL_INSTANCES
name|String
name|ROLE_ACTUAL_INSTANCES
init|=
literal|"role.actual.instances"
decl_stmt|;
comment|/**    * Status report: number currently requested: {@value}     */
DECL|field|ROLE_REQUESTED_INSTANCES
name|String
name|ROLE_REQUESTED_INSTANCES
init|=
literal|"role.requested.instances"
decl_stmt|;
comment|/**    * Status report: number currently being released: {@value}     */
DECL|field|ROLE_RELEASING_INSTANCES
name|String
name|ROLE_RELEASING_INSTANCES
init|=
literal|"role.releasing.instances"
decl_stmt|;
comment|/**    * Status report: total number that have failed: {@value}    */
DECL|field|ROLE_FAILED_INSTANCES
name|String
name|ROLE_FAILED_INSTANCES
init|=
literal|"role.failed.instances"
decl_stmt|;
comment|/**    * Status report: number that have failed recently: {@value}    */
DECL|field|ROLE_FAILED_RECENTLY_INSTANCES
name|String
name|ROLE_FAILED_RECENTLY_INSTANCES
init|=
literal|"role.failed.recently.instances"
decl_stmt|;
comment|/**    * Status report: number that have failed for node-related issues: {@value}    */
DECL|field|ROLE_NODE_FAILED_INSTANCES
name|String
name|ROLE_NODE_FAILED_INSTANCES
init|=
literal|"role.failed.node.instances"
decl_stmt|;
comment|/**    * Status report: number that been pre-empted: {@value}    */
DECL|field|ROLE_PREEMPTED_INSTANCES
name|String
name|ROLE_PREEMPTED_INSTANCES
init|=
literal|"role.failed.preempted.instances"
decl_stmt|;
comment|/**    * Number of pending anti-affine instances: {@value}    */
DECL|field|ROLE_PENDING_AA_INSTANCES
name|String
name|ROLE_PENDING_AA_INSTANCES
init|=
literal|"role.pending.aa.instances"
decl_stmt|;
comment|/**    * Status report: number currently being released: {@value}     */
DECL|field|ROLE_FAILED_STARTING_INSTANCES
name|String
name|ROLE_FAILED_STARTING_INSTANCES
init|=
literal|"role.failed.starting.instances"
decl_stmt|;
comment|/**    * Extra arguments (non-JVM) to use when starting this role    */
DECL|field|ROLE_ADDITIONAL_ARGS
name|String
name|ROLE_ADDITIONAL_ARGS
init|=
literal|"role.additional.args"
decl_stmt|;
comment|/**    *  JVM heap size for Java applications in MB.  Only relevant for Java applications.    *  This MUST be less than or equal to the {@link ResourceKeys#YARN_MEMORY} option    *  {@value}    */
DECL|field|JVM_HEAP
name|String
name|JVM_HEAP
init|=
literal|"jvm.heapsize"
decl_stmt|;
comment|/*    * GC options for Java applications.    */
DECL|field|GC_OPTS
name|String
name|GC_OPTS
init|=
literal|"gc.opts"
decl_stmt|;
comment|/**    * JVM options other than heap size. Only relevant for Java applications.    *  {@value}    */
DECL|field|JVM_OPTS
name|String
name|JVM_OPTS
init|=
literal|"jvm.opts"
decl_stmt|;
comment|/**    * All keys w/ env. are converted into env variables and passed down    */
DECL|field|ENV_PREFIX
name|String
name|ENV_PREFIX
init|=
literal|"env."
decl_stmt|;
comment|/**    * Container service record attribute prefix.    */
DECL|field|SERVICE_RECORD_ATTRIBUTE_PREFIX
name|String
name|SERVICE_RECORD_ATTRIBUTE_PREFIX
init|=
literal|"service.record.attribute"
decl_stmt|;
block|}
end_interface

end_unit

