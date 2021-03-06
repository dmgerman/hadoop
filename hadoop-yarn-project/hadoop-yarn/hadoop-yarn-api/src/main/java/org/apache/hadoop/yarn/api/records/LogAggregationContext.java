begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
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
name|Evolving
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
comment|/**  * {@code LogAggregationContext} represents all of the  * information needed by the {@code NodeManager} to handle  * the logs for an application.  *<p>  * It includes details such as:  *<ul>  *<li>  *     includePattern. It uses Java Regex to filter the log files  *     which match the defined include pattern and those log files  *     will be uploaded when the application finishes.  *</li>  *<li>  *     excludePattern. It uses Java Regex to filter the log files  *     which match the defined exclude pattern and those log files  *     will not be uploaded when application finishes. If the log file  *     name matches both the include and the exclude pattern, this file  *     will be excluded eventually.  *</li>  *<li>  *     rolledLogsIncludePattern. It uses Java Regex to filter the log files  *     which match the defined include pattern and those log files  *     will be aggregated in a rolling fashion.  *</li>  *<li>  *     rolledLogsExcludePattern. It uses Java Regex to filter the log files  *     which match the defined exclude pattern and those log files  *     will not be aggregated in a rolling fashion. If the log file  *     name matches both the include and the exclude pattern, this file  *     will be excluded eventually.  *</li>  *<li>  *     policyClassName. The policy class name that implements  *     ContainerLogAggregationPolicy. At runtime, nodemanager will the policy  *     if a given container's log should be aggregated based on the  *     ContainerType and other runtime state such as exit code by calling  *     ContainerLogAggregationPolicy#shouldDoLogAggregation.  *     This is useful when the app only wants to aggregate logs of a subset of  *     containers. Here are the available policies. Please make sure to specify  *     the canonical name by prefixing org.apache.hadoop.yarn.server.  *     nodemanager.containermanager.logaggregation.  *     to the class simple name below.  *     NoneContainerLogAggregationPolicy: skip aggregation for all containers.  *     AllContainerLogAggregationPolicy: aggregate all containers.  *     AMOrFailedContainerLogAggregationPolicy: aggregate application master  *         or failed containers.  *     FailedOrKilledContainerLogAggregationPolicy: aggregate failed or killed  *         containers  *     FailedContainerLogAggregationPolicy: aggregate failed containers  *     AMOnlyLogAggregationPolicy: aggregate application master containers  *     SampleContainerLogAggregationPolicy: sample logs of successful worker  *         containers, in addition to application master and failed/killed  *         containers.  *     If it isn't specified, it will use the cluster-wide default policy  *     defined by configuration yarn.nodemanager.log-aggregation.policy.class.  *     The default value of yarn.nodemanager.log-aggregation.policy.class is  *     AllContainerLogAggregationPolicy.  *</li>  *<li>  *     policyParameters. The parameters passed to the policy class via  *     ContainerLogAggregationPolicy#parseParameters during the policy object  *     initialization. This is optional. Some policy class might use parameters  *     to adjust its settings. It is up to policy class to define the scheme of  *     parameters.  *     For example, SampleContainerLogAggregationPolicy supports the format of  *     "SR:0.5,MIN:50", which means sample rate of 50% beyond the first 50  *     successful worker containers.  *</li>  *</ul>  *  * @see ApplicationSubmissionContext  */
end_comment

begin_class
annotation|@
name|Evolving
annotation|@
name|Public
DECL|class|LogAggregationContext
specifier|public
specifier|abstract
class|class
name|LogAggregationContext
block|{
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|newInstance (String includePattern, String excludePattern)
specifier|public
specifier|static
name|LogAggregationContext
name|newInstance
parameter_list|(
name|String
name|includePattern
parameter_list|,
name|String
name|excludePattern
parameter_list|)
block|{
name|LogAggregationContext
name|context
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|LogAggregationContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|context
operator|.
name|setIncludePattern
argument_list|(
name|includePattern
argument_list|)
expr_stmt|;
name|context
operator|.
name|setExcludePattern
argument_list|(
name|excludePattern
argument_list|)
expr_stmt|;
return|return
name|context
return|;
block|}
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|newInstance (String includePattern, String excludePattern, String rolledLogsIncludePattern, String rolledLogsExcludePattern)
specifier|public
specifier|static
name|LogAggregationContext
name|newInstance
parameter_list|(
name|String
name|includePattern
parameter_list|,
name|String
name|excludePattern
parameter_list|,
name|String
name|rolledLogsIncludePattern
parameter_list|,
name|String
name|rolledLogsExcludePattern
parameter_list|)
block|{
name|LogAggregationContext
name|context
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|LogAggregationContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|context
operator|.
name|setIncludePattern
argument_list|(
name|includePattern
argument_list|)
expr_stmt|;
name|context
operator|.
name|setExcludePattern
argument_list|(
name|excludePattern
argument_list|)
expr_stmt|;
name|context
operator|.
name|setRolledLogsIncludePattern
argument_list|(
name|rolledLogsIncludePattern
argument_list|)
expr_stmt|;
name|context
operator|.
name|setRolledLogsExcludePattern
argument_list|(
name|rolledLogsExcludePattern
argument_list|)
expr_stmt|;
return|return
name|context
return|;
block|}
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|newInstance (String includePattern, String excludePattern, String rolledLogsIncludePattern, String rolledLogsExcludePattern, String policyClassName, String policyParameters)
specifier|public
specifier|static
name|LogAggregationContext
name|newInstance
parameter_list|(
name|String
name|includePattern
parameter_list|,
name|String
name|excludePattern
parameter_list|,
name|String
name|rolledLogsIncludePattern
parameter_list|,
name|String
name|rolledLogsExcludePattern
parameter_list|,
name|String
name|policyClassName
parameter_list|,
name|String
name|policyParameters
parameter_list|)
block|{
name|LogAggregationContext
name|context
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|LogAggregationContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|context
operator|.
name|setIncludePattern
argument_list|(
name|includePattern
argument_list|)
expr_stmt|;
name|context
operator|.
name|setExcludePattern
argument_list|(
name|excludePattern
argument_list|)
expr_stmt|;
name|context
operator|.
name|setRolledLogsIncludePattern
argument_list|(
name|rolledLogsIncludePattern
argument_list|)
expr_stmt|;
name|context
operator|.
name|setRolledLogsExcludePattern
argument_list|(
name|rolledLogsExcludePattern
argument_list|)
expr_stmt|;
name|context
operator|.
name|setLogAggregationPolicyClassName
argument_list|(
name|policyClassName
argument_list|)
expr_stmt|;
name|context
operator|.
name|setLogAggregationPolicyParameters
argument_list|(
name|policyParameters
argument_list|)
expr_stmt|;
return|return
name|context
return|;
block|}
comment|/**    * Get include pattern. This includePattern only takes affect    * on logs that exist at the time of application finish.    *    * @return include pattern    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getIncludePattern ()
specifier|public
specifier|abstract
name|String
name|getIncludePattern
parameter_list|()
function_decl|;
comment|/**    * Set include pattern. This includePattern only takes affect    * on logs that exist at the time of application finish.    *    * @param includePattern    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setIncludePattern (String includePattern)
specifier|public
specifier|abstract
name|void
name|setIncludePattern
parameter_list|(
name|String
name|includePattern
parameter_list|)
function_decl|;
comment|/**    * Get exclude pattern. This excludePattern only takes affect    * on logs that exist at the time of application finish.    *    * @return exclude pattern    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getExcludePattern ()
specifier|public
specifier|abstract
name|String
name|getExcludePattern
parameter_list|()
function_decl|;
comment|/**    * Set exclude pattern. This excludePattern only takes affect    * on logs that exist at the time of application finish.    *    * @param excludePattern    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setExcludePattern (String excludePattern)
specifier|public
specifier|abstract
name|void
name|setExcludePattern
parameter_list|(
name|String
name|excludePattern
parameter_list|)
function_decl|;
comment|/**    * Get include pattern in a rolling fashion.    *     * @return include pattern    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getRolledLogsIncludePattern ()
specifier|public
specifier|abstract
name|String
name|getRolledLogsIncludePattern
parameter_list|()
function_decl|;
comment|/**    * Set include pattern in a rolling fashion.    *     * @param rolledLogsIncludePattern    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setRolledLogsIncludePattern ( String rolledLogsIncludePattern)
specifier|public
specifier|abstract
name|void
name|setRolledLogsIncludePattern
parameter_list|(
name|String
name|rolledLogsIncludePattern
parameter_list|)
function_decl|;
comment|/**    * Get exclude pattern for aggregation in a rolling fashion.    *     * @return exclude pattern    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getRolledLogsExcludePattern ()
specifier|public
specifier|abstract
name|String
name|getRolledLogsExcludePattern
parameter_list|()
function_decl|;
comment|/**    * Set exclude pattern for in a rolling fashion.    *     * @param rolledLogsExcludePattern    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setRolledLogsExcludePattern ( String rolledLogsExcludePattern)
specifier|public
specifier|abstract
name|void
name|setRolledLogsExcludePattern
parameter_list|(
name|String
name|rolledLogsExcludePattern
parameter_list|)
function_decl|;
comment|/**    * Get the log aggregation policy class.    *    * @return log aggregation policy class    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getLogAggregationPolicyClassName ()
specifier|public
specifier|abstract
name|String
name|getLogAggregationPolicyClassName
parameter_list|()
function_decl|;
comment|/**    * Set the log aggregation policy class.    *    * @param className    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setLogAggregationPolicyClassName ( String className)
specifier|public
specifier|abstract
name|void
name|setLogAggregationPolicyClassName
parameter_list|(
name|String
name|className
parameter_list|)
function_decl|;
comment|/**    * Get the log aggregation policy parameters.    *    * @return log aggregation policy parameters    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getLogAggregationPolicyParameters ()
specifier|public
specifier|abstract
name|String
name|getLogAggregationPolicyParameters
parameter_list|()
function_decl|;
comment|/**    * Set the log aggregation policy parameters.    * There is no schema defined for the parameters string.    * It is up to the log aggregation policy class to decide how to parse    * the parameters string.    *    * @param parameters    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setLogAggregationPolicyParameters ( String parameters)
specifier|public
specifier|abstract
name|void
name|setLogAggregationPolicyParameters
parameter_list|(
name|String
name|parameters
parameter_list|)
function_decl|;
block|}
end_class

end_unit

