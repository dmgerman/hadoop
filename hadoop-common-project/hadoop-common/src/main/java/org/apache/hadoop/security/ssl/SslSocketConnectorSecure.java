begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.ssl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|ssl
package|;
end_package

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|security
operator|.
name|SslSocketConnector
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLServerSocket
import|;
end_import

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
name|java
operator|.
name|net
operator|.
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * This subclass of the Jetty SslSocketConnector exists solely to control  * the TLS protocol versions allowed.  This is fallout from the POODLE  * vulnerability (CVE-2014-3566), which requires that SSLv3 be disabled.  * Only TLS 1.0 and later protocols are allowed.  */
end_comment

begin_class
DECL|class|SslSocketConnectorSecure
specifier|public
class|class
name|SslSocketConnectorSecure
extends|extends
name|SslSocketConnector
block|{
DECL|method|SslSocketConnectorSecure ()
specifier|public
name|SslSocketConnectorSecure
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create a new ServerSocket that will not accept SSLv3 connections,    * but will accept TLSv1.x connections.    */
DECL|method|newServerSocket (String host, int port,int backlog)
specifier|protected
name|ServerSocket
name|newServerSocket
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|int
name|backlog
parameter_list|)
throws|throws
name|IOException
block|{
name|SSLServerSocket
name|socket
init|=
operator|(
name|SSLServerSocket
operator|)
name|super
operator|.
name|newServerSocket
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|backlog
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|nonSSLProtocols
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|socket
operator|.
name|getEnabledProtocols
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|p
operator|.
name|contains
argument_list|(
literal|"SSLv3"
argument_list|)
condition|)
block|{
name|nonSSLProtocols
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
name|socket
operator|.
name|setEnabledProtocols
argument_list|(
name|nonSSLProtocols
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|nonSSLProtocols
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|socket
return|;
block|}
block|}
end_class

end_unit

