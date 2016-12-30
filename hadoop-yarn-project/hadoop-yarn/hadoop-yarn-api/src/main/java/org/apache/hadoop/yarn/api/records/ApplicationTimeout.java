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
comment|/**  * {@code ApplicationTimeout} is a report for configured application timeouts.  * It includes details such as:  *<ul>  *<li>{@link ApplicationTimeoutType} of the timeout type.</li>  *<li>Expiry time in ISO8601 standard with format  *<b>yyyy-MM-dd'T'HH:mm:ss.SSSZ</b> or "UNLIMITED".</li>  *<li>Remaining time in seconds.</li>  *</ul>  * The possible values for {ExpiryTime, RemainingTimeInSeconds} are  *<ul>  *<li>{UNLIMITED,-1} : Timeout is not configured for given timeout type  * (LIFETIME).</li>  *<li>{ISO8601 date string, 0} : Timeout is configured and application has  * completed.</li>  *<li>{ISO8601 date string, greater than zero} : Timeout is configured and  * application is RUNNING. Application will be timed out after configured  * value.</li>  *</ul>  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|ApplicationTimeout
specifier|public
specifier|abstract
class|class
name|ApplicationTimeout
block|{
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|newInstance (ApplicationTimeoutType type, String expiryTime, long remainingTime)
specifier|public
specifier|static
name|ApplicationTimeout
name|newInstance
parameter_list|(
name|ApplicationTimeoutType
name|type
parameter_list|,
name|String
name|expiryTime
parameter_list|,
name|long
name|remainingTime
parameter_list|)
block|{
name|ApplicationTimeout
name|timeouts
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ApplicationTimeout
operator|.
name|class
argument_list|)
decl_stmt|;
name|timeouts
operator|.
name|setTimeoutType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|timeouts
operator|.
name|setExpiryTime
argument_list|(
name|expiryTime
argument_list|)
expr_stmt|;
name|timeouts
operator|.
name|setRemainingTime
argument_list|(
name|remainingTime
argument_list|)
expr_stmt|;
return|return
name|timeouts
return|;
block|}
comment|/**    * Get the application timeout type.    * @return timeoutType of an application timeout.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getTimeoutType ()
specifier|public
specifier|abstract
name|ApplicationTimeoutType
name|getTimeoutType
parameter_list|()
function_decl|;
comment|/**    * Set the application timeout type.    * @param timeoutType of an application timeout.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setTimeoutType (ApplicationTimeoutType timeoutType)
specifier|public
specifier|abstract
name|void
name|setTimeoutType
parameter_list|(
name|ApplicationTimeoutType
name|timeoutType
parameter_list|)
function_decl|;
comment|/**    * Get<code>expiryTime</code> for given timeout type.    * @return expiryTime in ISO8601 standard with format    *<b>yyyy-MM-dd'T'HH:mm:ss.SSSZ</b>.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getExpiryTime ()
specifier|public
specifier|abstract
name|String
name|getExpiryTime
parameter_list|()
function_decl|;
comment|/**    * Set<code>expiryTime</code> for given timeout type.    * @param expiryTime in ISO8601 standard with format    *<b>yyyy-MM-dd'T'HH:mm:ss.SSSZ</b>.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setExpiryTime (String expiryTime)
specifier|public
specifier|abstract
name|void
name|setExpiryTime
parameter_list|(
name|String
name|expiryTime
parameter_list|)
function_decl|;
comment|/**    * Get<code>Remaining Time</code> of an application for given timeout type.    * @return Remaining Time in seconds.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getRemainingTime ()
specifier|public
specifier|abstract
name|long
name|getRemainingTime
parameter_list|()
function_decl|;
comment|/**    * Set<code>Remaining Time</code> of an application for given timeout type.    * @param remainingTime in seconds.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setRemainingTime (long remainingTime)
specifier|public
specifier|abstract
name|void
name|setRemainingTime
parameter_list|(
name|long
name|remainingTime
parameter_list|)
function_decl|;
block|}
end_class

end_unit

