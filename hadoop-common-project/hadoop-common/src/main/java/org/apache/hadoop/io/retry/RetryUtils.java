begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.retry
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|retry
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|conf
operator|.
name|Configuration
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
name|ipc
operator|.
name|RemoteException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ServiceException
import|;
end_import

begin_class
DECL|class|RetryUtils
specifier|public
class|class
name|RetryUtils
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RetryUtils
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Return the default retry policy set in conf.    *     * If the value retryPolicyEnabledKey is set to false in conf,    * use TRY_ONCE_THEN_FAIL.    *     * Otherwise, get the MultipleLinearRandomRetry policy specified in the conf    * and then    * (1) use multipleLinearRandomRetry for    *     - remoteExceptionToRetry, or    *     - IOException other than RemoteException, or    *     - ServiceException; and    * (2) use TRY_ONCE_THEN_FAIL for    *     - non-remoteExceptionToRetry RemoteException, or    *     - non-IOException.    *         *    * @param conf    * @param retryPolicyEnabledKey     conf property key for enabling retry    * @param defaultRetryPolicyEnabled default retryPolicyEnabledKey conf value     * @param retryPolicySpecKey        conf property key for retry policy spec    * @param defaultRetryPolicySpec    default retryPolicySpecKey conf value    * @param remoteExceptionToRetry    The particular RemoteException to retry    * @return the default retry policy.    */
DECL|method|getDefaultRetryPolicy ( Configuration conf, String retryPolicyEnabledKey, boolean defaultRetryPolicyEnabled, String retryPolicySpecKey, String defaultRetryPolicySpec, final Class<? extends Exception> remoteExceptionToRetry )
specifier|public
specifier|static
name|RetryPolicy
name|getDefaultRetryPolicy
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|retryPolicyEnabledKey
parameter_list|,
name|boolean
name|defaultRetryPolicyEnabled
parameter_list|,
name|String
name|retryPolicySpecKey
parameter_list|,
name|String
name|defaultRetryPolicySpec
parameter_list|,
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
name|remoteExceptionToRetry
parameter_list|)
block|{
specifier|final
name|RetryPolicy
name|multipleLinearRandomRetry
init|=
name|getMultipleLinearRandomRetry
argument_list|(
name|conf
argument_list|,
name|retryPolicyEnabledKey
argument_list|,
name|defaultRetryPolicyEnabled
argument_list|,
name|retryPolicySpecKey
argument_list|,
name|defaultRetryPolicySpec
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"multipleLinearRandomRetry = "
operator|+
name|multipleLinearRandomRetry
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|multipleLinearRandomRetry
operator|==
literal|null
condition|)
block|{
comment|//no retry
return|return
name|RetryPolicies
operator|.
name|TRY_ONCE_THEN_FAIL
return|;
block|}
else|else
block|{
return|return
operator|new
name|RetryPolicy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RetryAction
name|shouldRetry
parameter_list|(
name|Exception
name|e
parameter_list|,
name|int
name|retries
parameter_list|,
name|int
name|failovers
parameter_list|,
name|boolean
name|isMethodIdempotent
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|e
operator|instanceof
name|ServiceException
condition|)
block|{
comment|//unwrap ServiceException
specifier|final
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|!=
literal|null
operator|&&
name|cause
operator|instanceof
name|Exception
condition|)
block|{
name|e
operator|=
operator|(
name|Exception
operator|)
name|cause
expr_stmt|;
block|}
block|}
comment|//see (1) and (2) in the javadoc of this method.
specifier|final
name|RetryPolicy
name|p
decl_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|RemoteException
condition|)
block|{
specifier|final
name|RemoteException
name|re
init|=
operator|(
name|RemoteException
operator|)
name|e
decl_stmt|;
name|p
operator|=
name|remoteExceptionToRetry
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|re
operator|.
name|getClassName
argument_list|()
argument_list|)
condition|?
name|multipleLinearRandomRetry
else|:
name|RetryPolicies
operator|.
name|TRY_ONCE_THEN_FAIL
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|e
operator|instanceof
name|IOException
operator|||
name|e
operator|instanceof
name|ServiceException
condition|)
block|{
name|p
operator|=
name|multipleLinearRandomRetry
expr_stmt|;
block|}
else|else
block|{
comment|//non-IOException
name|p
operator|=
name|RetryPolicies
operator|.
name|TRY_ONCE_THEN_FAIL
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"RETRY "
operator|+
name|retries
operator|+
literal|") policy="
operator|+
name|p
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|", exception="
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|p
operator|.
name|shouldRetry
argument_list|(
name|e
argument_list|,
name|retries
argument_list|,
name|failovers
argument_list|,
name|isMethodIdempotent
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"RetryPolicy["
operator|+
name|multipleLinearRandomRetry
operator|+
literal|", "
operator|+
name|RetryPolicies
operator|.
name|TRY_ONCE_THEN_FAIL
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"]"
return|;
block|}
block|}
return|;
block|}
block|}
comment|/**    * Return the MultipleLinearRandomRetry policy specified in the conf,    * or null if the feature is disabled.    * If the policy is specified in the conf but the policy cannot be parsed,    * the default policy is returned.    *     * Retry policy spec:    *   N pairs of sleep-time and number-of-retries "s1,n1,s2,n2,..."    *     * @param conf    * @param retryPolicyEnabledKey     conf property key for enabling retry    * @param defaultRetryPolicyEnabled default retryPolicyEnabledKey conf value     * @param retryPolicySpecKey        conf property key for retry policy spec    * @param defaultRetryPolicySpec    default retryPolicySpecKey conf value    * @return the MultipleLinearRandomRetry policy specified in the conf,    *         or null if the feature is disabled.    */
DECL|method|getMultipleLinearRandomRetry ( Configuration conf, String retryPolicyEnabledKey, boolean defaultRetryPolicyEnabled, String retryPolicySpecKey, String defaultRetryPolicySpec )
specifier|public
specifier|static
name|RetryPolicy
name|getMultipleLinearRandomRetry
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|retryPolicyEnabledKey
parameter_list|,
name|boolean
name|defaultRetryPolicyEnabled
parameter_list|,
name|String
name|retryPolicySpecKey
parameter_list|,
name|String
name|defaultRetryPolicySpec
parameter_list|)
block|{
specifier|final
name|boolean
name|enabled
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|retryPolicyEnabledKey
argument_list|,
name|defaultRetryPolicyEnabled
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|String
name|policy
init|=
name|conf
operator|.
name|get
argument_list|(
name|retryPolicySpecKey
argument_list|,
name|defaultRetryPolicySpec
argument_list|)
decl_stmt|;
specifier|final
name|RetryPolicy
name|r
init|=
name|RetryPolicies
operator|.
name|MultipleLinearRandomRetry
operator|.
name|parseCommaSeparatedString
argument_list|(
name|policy
argument_list|)
decl_stmt|;
return|return
operator|(
name|r
operator|!=
literal|null
operator|)
condition|?
name|r
else|:
name|RetryPolicies
operator|.
name|MultipleLinearRandomRetry
operator|.
name|parseCommaSeparatedString
argument_list|(
name|defaultRetryPolicySpec
argument_list|)
return|;
block|}
block|}
end_class

end_unit

