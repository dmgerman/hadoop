begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|security
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyInt
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyLong
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyObject
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doReturn
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
name|security
operator|.
name|PrivilegedExceptionAction
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
name|*
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
name|impl
operator|.
name|Log4JLogger
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
name|io
operator|.
name|Text
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
name|Client
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
name|ipc
operator|.
name|Server
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
name|ProtocolSignature
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
name|mapred
operator|.
name|TaskUmbilicalProtocol
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
name|mapreduce
operator|.
name|security
operator|.
name|token
operator|.
name|JobTokenIdentifier
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
name|mapreduce
operator|.
name|security
operator|.
name|token
operator|.
name|JobTokenSecretManager
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
name|token
operator|.
name|Token
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
name|SaslInputStream
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
name|SaslRpcClient
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
name|SaslRpcServer
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
name|SecurityUtil
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
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/** Unit tests for using Job Token over RPC. */
end_comment

begin_class
annotation|@
name|Ignore
DECL|class|TestUmbilicalProtocolWithJobToken
specifier|public
class|class
name|TestUmbilicalProtocolWithJobToken
block|{
DECL|field|ADDRESS
specifier|private
specifier|static
specifier|final
name|String
name|ADDRESS
init|=
literal|"0.0.0.0"
decl_stmt|;
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
name|TestUmbilicalProtocolWithJobToken
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
static|static
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
static|static
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|Client
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|Server
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|SaslRpcClient
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|SaslRpcServer
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|SaslInputStream
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJobTokenRpc ()
specifier|public
name|void
name|testJobTokenRpc
parameter_list|()
throws|throws
name|Exception
block|{
name|TaskUmbilicalProtocol
name|mockTT
init|=
name|mock
argument_list|(
name|TaskUmbilicalProtocol
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|TaskUmbilicalProtocol
operator|.
name|versionID
argument_list|)
operator|.
name|when
argument_list|(
name|mockTT
argument_list|)
operator|.
name|getProtocolVersion
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
name|ProtocolSignature
operator|.
name|getProtocolSignature
argument_list|(
name|mockTT
argument_list|,
name|TaskUmbilicalProtocol
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|TaskUmbilicalProtocol
operator|.
name|versionID
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|mockTT
argument_list|)
operator|.
name|getProtocolSignature
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
name|JobTokenSecretManager
name|sm
init|=
operator|new
name|JobTokenSecretManager
argument_list|()
decl_stmt|;
specifier|final
name|Server
name|server
init|=
name|RPC
operator|.
name|getServer
argument_list|(
name|TaskUmbilicalProtocol
operator|.
name|class
argument_list|,
name|mockTT
argument_list|,
name|ADDRESS
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|,
literal|true
argument_list|,
name|conf
argument_list|,
name|sm
argument_list|)
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|UserGroupInformation
name|current
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
specifier|final
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|String
name|jobId
init|=
name|current
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|JobTokenIdentifier
name|tokenId
init|=
operator|new
name|JobTokenIdentifier
argument_list|(
operator|new
name|Text
argument_list|(
name|jobId
argument_list|)
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|JobTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|JobTokenIdentifier
argument_list|>
argument_list|(
name|tokenId
argument_list|,
name|sm
argument_list|)
decl_stmt|;
name|sm
operator|.
name|addTokenForJob
argument_list|(
name|jobId
argument_list|,
name|token
argument_list|)
expr_stmt|;
name|SecurityUtil
operator|.
name|setTokenService
argument_list|(
name|token
argument_list|,
name|addr
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Service address for token is "
operator|+
name|token
operator|.
name|getService
argument_list|()
argument_list|)
expr_stmt|;
name|current
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|current
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|TaskUmbilicalProtocol
name|proxy
init|=
literal|null
decl_stmt|;
try|try
block|{
name|proxy
operator|=
operator|(
name|TaskUmbilicalProtocol
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|TaskUmbilicalProtocol
operator|.
name|class
argument_list|,
name|TaskUmbilicalProtocol
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|ping
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|proxy
operator|!=
literal|null
condition|)
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
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

