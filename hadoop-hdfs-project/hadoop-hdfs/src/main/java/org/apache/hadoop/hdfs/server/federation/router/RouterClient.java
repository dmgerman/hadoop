begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|router
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|InterfaceAudience
operator|.
name|Private
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
name|hdfs
operator|.
name|protocolPB
operator|.
name|RouterAdminProtocolPB
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
name|hdfs
operator|.
name|protocolPB
operator|.
name|RouterAdminProtocolTranslatorPB
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|MountTableManager
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
name|ProtobufRpcEngine
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
name|RPC
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
name|net
operator|.
name|NetUtils
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

begin_comment
comment|/**  * Client to connect to the {@link Router} via the admin protocol.  */
end_comment

begin_class
annotation|@
name|Private
DECL|class|RouterClient
specifier|public
class|class
name|RouterClient
implements|implements
name|Closeable
block|{
DECL|field|proxy
specifier|private
specifier|final
name|RouterAdminProtocolTranslatorPB
name|proxy
decl_stmt|;
DECL|field|ugi
specifier|private
specifier|final
name|UserGroupInformation
name|ugi
decl_stmt|;
DECL|method|createRouterProxy ( InetSocketAddress address, Configuration conf, UserGroupInformation ugi)
specifier|private
specifier|static
name|RouterAdminProtocolTranslatorPB
name|createRouterProxy
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|IOException
block|{
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|RouterAdminProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|AtomicBoolean
name|fallbackToSimpleAuth
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|long
name|version
init|=
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|RouterAdminProtocolPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|RouterAdminProtocolPB
name|proxy
init|=
name|RPC
operator|.
name|getProtocolProxy
argument_list|(
name|RouterAdminProtocolPB
operator|.
name|class
argument_list|,
name|version
argument_list|,
name|address
argument_list|,
name|ugi
argument_list|,
name|conf
argument_list|,
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|conf
argument_list|)
argument_list|,
name|RPC
operator|.
name|getRpcTimeout
argument_list|(
name|conf
argument_list|)
argument_list|,
literal|null
argument_list|,
name|fallbackToSimpleAuth
argument_list|)
operator|.
name|getProxy
argument_list|()
decl_stmt|;
return|return
operator|new
name|RouterAdminProtocolTranslatorPB
argument_list|(
name|proxy
argument_list|)
return|;
block|}
DECL|method|RouterClient (InetSocketAddress address, Configuration conf)
specifier|public
name|RouterClient
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
name|this
operator|.
name|proxy
operator|=
name|createRouterProxy
argument_list|(
name|address
argument_list|,
name|conf
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
block|}
DECL|method|getMountTableManager ()
specifier|public
name|MountTableManager
name|getMountTableManager
parameter_list|()
block|{
return|return
name|proxy
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

