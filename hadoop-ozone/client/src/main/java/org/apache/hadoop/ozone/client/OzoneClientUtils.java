begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|io
operator|.
name|retry
operator|.
name|RetryPolicies
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
name|io
operator|.
name|retry
operator|.
name|RetryPolicy
import|;
end_import

begin_comment
comment|/** A utility class for OzoneClient. */
end_comment

begin_class
DECL|class|OzoneClientUtils
specifier|public
specifier|final
class|class
name|OzoneClientUtils
block|{
DECL|method|OzoneClientUtils ()
specifier|private
name|OzoneClientUtils
parameter_list|()
block|{}
DECL|method|createRetryPolicy (int maxRetryCount, long retryInterval)
specifier|public
specifier|static
name|RetryPolicy
name|createRetryPolicy
parameter_list|(
name|int
name|maxRetryCount
parameter_list|,
name|long
name|retryInterval
parameter_list|)
block|{
comment|// retry with fixed sleep between retries
return|return
name|RetryPolicies
operator|.
name|retryUpToMaximumCountWithFixedSleep
argument_list|(
name|maxRetryCount
argument_list|,
name|retryInterval
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
return|;
block|}
block|}
end_class

end_unit

