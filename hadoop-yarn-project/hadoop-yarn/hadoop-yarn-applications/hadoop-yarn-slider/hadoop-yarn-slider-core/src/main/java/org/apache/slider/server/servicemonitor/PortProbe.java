begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.servicemonitor
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|servicemonitor
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
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|RoleInstance
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Probe for a port being open.  */
end_comment

begin_class
DECL|class|PortProbe
specifier|public
class|class
name|PortProbe
extends|extends
name|Probe
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PortProbe
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|port
specifier|private
specifier|final
name|int
name|port
decl_stmt|;
DECL|field|timeout
specifier|private
specifier|final
name|int
name|timeout
decl_stmt|;
DECL|method|PortProbe (int port, int timeout)
specifier|public
name|PortProbe
parameter_list|(
name|int
name|port
parameter_list|,
name|int
name|timeout
parameter_list|)
block|{
name|super
argument_list|(
literal|"Port probe of "
operator|+
name|port
operator|+
literal|" for "
operator|+
name|timeout
operator|+
literal|"ms"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
block|}
DECL|method|create (Map<String, String> props)
specifier|public
specifier|static
name|PortProbe
name|create
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|port
init|=
name|getPropertyInt
argument_list|(
name|props
argument_list|,
name|PORT_PROBE_PORT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|port
operator|>=
literal|65536
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|PORT_PROBE_PORT
operator|+
literal|" "
operator|+
name|port
operator|+
literal|" is out of "
operator|+
literal|"range"
argument_list|)
throw|;
block|}
name|int
name|timeout
init|=
name|getPropertyInt
argument_list|(
name|props
argument_list|,
name|PORT_PROBE_CONNECT_TIMEOUT
argument_list|,
name|PORT_PROBE_CONNECT_TIMEOUT_DEFAULT
argument_list|)
decl_stmt|;
return|return
operator|new
name|PortProbe
argument_list|(
name|port
argument_list|,
name|timeout
argument_list|)
return|;
block|}
comment|/**    * Try to connect to the (host,port); a failure to connect within    * the specified timeout is a failure.    * @param roleInstance role instance    * @return the outcome    */
annotation|@
name|Override
DECL|method|ping (RoleInstance roleInstance)
specifier|public
name|ProbeStatus
name|ping
parameter_list|(
name|RoleInstance
name|roleInstance
parameter_list|)
block|{
name|ProbeStatus
name|status
init|=
operator|new
name|ProbeStatus
argument_list|()
decl_stmt|;
name|String
name|ip
init|=
name|roleInstance
operator|.
name|ip
decl_stmt|;
if|if
condition|(
name|ip
operator|==
literal|null
condition|)
block|{
name|status
operator|.
name|fail
argument_list|(
name|this
argument_list|,
operator|new
name|IOException
argument_list|(
literal|"IP is not available yet"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|status
return|;
block|}
name|InetSocketAddress
name|sockAddr
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|ip
argument_list|,
name|port
argument_list|)
decl_stmt|;
name|Socket
name|socket
init|=
operator|new
name|Socket
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Connecting to "
operator|+
name|sockAddr
operator|.
name|toString
argument_list|()
operator|+
literal|"timeout="
operator|+
name|MonitorUtils
operator|.
name|millisToHumanTime
argument_list|(
name|timeout
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|socket
operator|.
name|connect
argument_list|(
name|sockAddr
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
name|status
operator|.
name|succeed
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|String
name|error
init|=
literal|"Probe "
operator|+
name|sockAddr
operator|+
literal|" failed: "
operator|+
name|e
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|error
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|status
operator|.
name|fail
argument_list|(
name|this
argument_list|,
operator|new
name|IOException
argument_list|(
name|error
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeSocket
argument_list|(
name|socket
argument_list|)
expr_stmt|;
block|}
return|return
name|status
return|;
block|}
block|}
end_class

end_unit

