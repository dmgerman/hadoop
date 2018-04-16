begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|client
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|UserGroupInformation
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
name|client
operator|.
name|ServerProxy
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
name|ipc
operator|.
name|YarnRPC
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
name|service
operator|.
name|conf
operator|.
name|YarnServiceConf
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import static
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
operator|.
name|TRY_ONCE_THEN_FAIL
import|;
end_import

begin_class
DECL|class|ClientAMProxy
specifier|public
class|class
name|ClientAMProxy
extends|extends
name|ServerProxy
block|{
DECL|method|createProxy (final Configuration conf, final Class<T> protocol, final UserGroupInformation ugi, final YarnRPC rpc, final InetSocketAddress serverAddress)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|createProxy
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|protocol
parameter_list|,
specifier|final
name|UserGroupInformation
name|ugi
parameter_list|,
specifier|final
name|YarnRPC
name|rpc
parameter_list|,
specifier|final
name|InetSocketAddress
name|serverAddress
parameter_list|)
block|{
name|Configuration
name|confClone
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|confClone
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|confClone
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SOCKET_TIMEOUTS_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|RetryPolicy
name|retryPolicy
decl_stmt|;
if|if
condition|(
name|conf
operator|.
name|getLong
argument_list|(
name|YarnServiceConf
operator|.
name|CLIENT_AM_RETRY_MAX_WAIT_MS
argument_list|,
literal|0
argument_list|)
operator|==
literal|0
condition|)
block|{
comment|// by default no retry
name|retryPolicy
operator|=
name|TRY_ONCE_THEN_FAIL
expr_stmt|;
block|}
else|else
block|{
name|retryPolicy
operator|=
name|createRetryPolicy
argument_list|(
name|conf
argument_list|,
name|YarnServiceConf
operator|.
name|CLIENT_AM_RETRY_MAX_WAIT_MS
argument_list|,
name|YarnServiceConf
operator|.
name|DEFAULT_CLIENT_AM_RETRY_MAX_WAIT_MS
argument_list|,
name|YarnServiceConf
operator|.
name|CLIENT_AM_RETRY_MAX_INTERVAL_MS
argument_list|,
name|YarnServiceConf
operator|.
name|DEFAULT_CLIENT_AM_RETRY_MAX_INTERVAL_MS
argument_list|)
expr_stmt|;
block|}
return|return
name|createRetriableProxy
argument_list|(
name|confClone
argument_list|,
name|protocol
argument_list|,
name|ugi
argument_list|,
name|rpc
argument_list|,
name|serverAddress
argument_list|,
name|retryPolicy
argument_list|)
return|;
block|}
block|}
end_class

end_unit

