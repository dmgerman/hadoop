begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|javax
operator|.
name|net
operator|.
name|SocketFactory
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
name|security
operator|.
name|token
operator|.
name|SecretManager
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
name|token
operator|.
name|TokenIdentifier
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

begin_comment
comment|/** An RPC implementation. */
end_comment

begin_interface
DECL|interface|RpcEngine
interface|interface
name|RpcEngine
block|{
comment|/** Construct a client-side proxy object. */
DECL|method|getProxy (Class protocol, long clientVersion, InetSocketAddress addr, UserGroupInformation ticket, Configuration conf, SocketFactory factory)
name|Object
name|getProxy
parameter_list|(
name|Class
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|UserGroupInformation
name|ticket
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|SocketFactory
name|factory
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Stop this proxy. */
DECL|method|stopProxy (Object proxy)
name|void
name|stopProxy
parameter_list|(
name|Object
name|proxy
parameter_list|)
function_decl|;
comment|/** Expert: Make multiple, parallel calls to a set of servers. */
DECL|method|call (Method method, Object[][] params, InetSocketAddress[] addrs, UserGroupInformation ticket, Configuration conf)
name|Object
index|[]
name|call
parameter_list|(
name|Method
name|method
parameter_list|,
name|Object
index|[]
index|[]
name|params
parameter_list|,
name|InetSocketAddress
index|[]
name|addrs
parameter_list|,
name|UserGroupInformation
name|ticket
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/** Construct a server for a protocol implementation instance. */
DECL|method|getServer (Class protocol, Object instance, String bindAddress, int port, int numHandlers, boolean verbose, Configuration conf, SecretManager<? extends TokenIdentifier> secretManager )
name|RPC
operator|.
name|Server
name|getServer
parameter_list|(
name|Class
name|protocol
parameter_list|,
name|Object
name|instance
parameter_list|,
name|String
name|bindAddress
parameter_list|,
name|int
name|port
parameter_list|,
name|int
name|numHandlers
parameter_list|,
name|boolean
name|verbose
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|SecretManager
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|secretManager
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

