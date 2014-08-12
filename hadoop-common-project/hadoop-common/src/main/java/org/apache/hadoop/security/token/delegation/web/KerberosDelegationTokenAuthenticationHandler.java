begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.token.delegation.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|web
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
name|InterfaceStability
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
name|authentication
operator|.
name|server
operator|.
name|AuthenticationHandler
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
name|authentication
operator|.
name|server
operator|.
name|KerberosAuthenticationHandler
import|;
end_import

begin_comment
comment|/**  * An {@link AuthenticationHandler} that implements Kerberos SPNEGO mechanism  * for HTTP and supports Delegation Token functionality.  *<p/>  * In addition to the {@link KerberosAuthenticationHandler} configuration  * properties, this handler supports:  *<ul>  *<li>kerberos.delegation-token.token-kind: the token kind for generated tokens  * (no default, required property).</li>  *<li>kerberos.delegation-token.update-interval.sec: secret manager master key  * update interval in seconds (default 1 day).</li>  *<li>kerberos.delegation-token.max-lifetime.sec: maximum life of a delegation  * token in seconds (default 7 days).</li>  *<li>kerberos.delegation-token.renewal-interval.sec: renewal interval for  * delegation tokens in seconds (default 1 day).</li>  *<li>kerberos.delegation-token.removal-scan-interval.sec: delegation tokens  * removal scan interval in seconds (default 1 hour).</li>  *</ul>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|KerberosDelegationTokenAuthenticationHandler
specifier|public
class|class
name|KerberosDelegationTokenAuthenticationHandler
extends|extends
name|DelegationTokenAuthenticationHandler
block|{
DECL|method|KerberosDelegationTokenAuthenticationHandler ()
specifier|public
name|KerberosDelegationTokenAuthenticationHandler
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|KerberosAuthenticationHandler
argument_list|(
name|KerberosAuthenticationHandler
operator|.
name|TYPE
operator|+
name|TYPE_POSTFIX
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

