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
comment|/**  *<p><code>LogAggregationContext</code> represents all of the  * information needed by the<code>NodeManager</code> to handle  * the logs for an application.</p>  *  *<p>It includes details such as:  *<ul>  *<li>includePattern. It uses Java Regex to filter the log files  *     which match the defined include pattern and those log files  *     will be uploaded.</li>  *<li>excludePattern. It uses Java Regex to filter the log files  *     which match the defined exclude pattern and those log files  *     will not be uploaded. If the log file name matches both the  *     include and the exclude pattern, this file will be excluded eventually</li>  *<li>rollingIntervalSeconds. The default value is -1. By default,  *     the logAggregationService only uploads container logs when  *     the application is finished. This configure defines  *     how often the logAggregationSerivce uploads container logs in seconds.  *     By setting this configure, the logAggregationSerivce can upload container  *     logs periodically when the application is running.  *</li>  *</ul>  *</p>  *  * @see ApplicationSubmissionContext  */
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
DECL|method|newInstance (String includePattern, String excludePattern, long rollingIntervalSeconds)
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
name|long
name|rollingIntervalSeconds
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
name|setRollingIntervalSeconds
argument_list|(
name|rollingIntervalSeconds
argument_list|)
expr_stmt|;
return|return
name|context
return|;
block|}
comment|/**    * Get include pattern    *    * @return include pattern    */
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
comment|/**    * Set include pattern    *    * @param includePattern    */
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
comment|/**    * Get exclude pattern    *    * @return exclude pattern    */
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
comment|/**    * Set exclude pattern    *    * @param excludePattern    */
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
comment|/**    * Get rollingIntervalSeconds    *    * @return the rollingIntervalSeconds    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getRollingIntervalSeconds ()
specifier|public
specifier|abstract
name|long
name|getRollingIntervalSeconds
parameter_list|()
function_decl|;
comment|/**    * Set rollingIntervalSeconds    *    * @param rollingIntervalSeconds    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setRollingIntervalSeconds (long rollingIntervalSeconds)
specifier|public
specifier|abstract
name|void
name|setRollingIntervalSeconds
parameter_list|(
name|long
name|rollingIntervalSeconds
parameter_list|)
function_decl|;
block|}
end_class

end_unit

