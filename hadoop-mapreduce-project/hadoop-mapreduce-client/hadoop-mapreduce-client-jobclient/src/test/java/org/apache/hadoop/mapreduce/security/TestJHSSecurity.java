begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|security
operator|.
name|PrivilegedAction
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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|mapred
operator|.
name|JobConf
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
name|v2
operator|.
name|api
operator|.
name|HSClientProtocol
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
name|v2
operator|.
name|api
operator|.
name|MRClientProtocol
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
name|v2
operator|.
name|api
operator|.
name|MRDelegationTokenIdentifier
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
name|v2
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|GetDelegationTokenRequest
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
name|v2
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|GetJobReportRequest
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
name|v2
operator|.
name|hs
operator|.
name|JobHistoryServer
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
name|v2
operator|.
name|jobhistory
operator|.
name|JHAdminConfig
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
name|v2
operator|.
name|util
operator|.
name|MRBuilderUtils
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
name|UserGroupInformation
operator|.
name|AuthenticationMethod
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|DelegationToken
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
name|conf
operator|.
name|YarnConfiguration
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
name|exceptions
operator|.
name|YarnRemoteException
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
name|util
operator|.
name|Records
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
name|apache
operator|.
name|log4j
operator|.
name|LogManager
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
name|Logger
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

begin_class
DECL|class|TestJHSSecurity
specifier|public
class|class
name|TestJHSSecurity
block|{
annotation|@
name|Test
DECL|method|testDelegationToken ()
specifier|public
name|void
name|testDelegationToken
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Logger
name|rootLogger
init|=
name|LogManager
operator|.
name|getRootLogger
argument_list|()
decl_stmt|;
name|rootLogger
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
specifier|final
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|(
operator|new
name|JobConf
argument_list|()
argument_list|)
decl_stmt|;
comment|// Just a random principle
name|conf
operator|.
name|set
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_PRINCIPAL
argument_list|,
literal|"RandomOrc/localhost@apache.org"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
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
specifier|final
name|JobHistoryServer
name|jobHistoryServer
init|=
operator|new
name|JobHistoryServer
argument_list|()
block|{
specifier|protected
name|void
name|doSecureLogin
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// no keytab based login
block|}
empty_stmt|;
block|}
decl_stmt|;
name|jobHistoryServer
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|jobHistoryServer
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Fake the authentication-method
name|UserGroupInformation
name|loggedInUser
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|loggedInUser
operator|.
name|setAuthenticationMethod
argument_list|(
name|AuthenticationMethod
operator|.
name|KERBEROS
argument_list|)
expr_stmt|;
comment|// Get the delegation token directly as it is a little difficult to setup
comment|// the kerberos based rpc.
name|DelegationToken
name|token
init|=
name|loggedInUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|DelegationToken
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DelegationToken
name|run
parameter_list|()
throws|throws
name|YarnRemoteException
block|{
name|GetDelegationTokenRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetDelegationTokenRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setRenewer
argument_list|(
literal|"OneRenewerToRuleThemAll"
argument_list|)
expr_stmt|;
return|return
name|jobHistoryServer
operator|.
name|getClientService
argument_list|()
operator|.
name|getClientHandler
argument_list|()
operator|.
name|getDelegationToken
argument_list|(
name|request
argument_list|)
operator|.
name|getDelegationToken
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// Now try talking to JHS using the delegation token
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"TheDarkLord"
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
operator|new
name|Token
argument_list|<
name|MRDelegationTokenIdentifier
argument_list|>
argument_list|(
name|token
operator|.
name|getIdentifier
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
name|token
operator|.
name|getPassword
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|(
name|token
operator|.
name|getKind
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|token
operator|.
name|getService
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|MRClientProtocol
name|userUsingDT
init|=
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|MRClientProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|MRClientProtocol
name|run
parameter_list|()
block|{
return|return
operator|(
name|MRClientProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|HSClientProtocol
operator|.
name|class
argument_list|,
name|jobHistoryServer
operator|.
name|getClientService
argument_list|()
operator|.
name|getBindAddress
argument_list|()
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|GetJobReportRequest
name|jobReportRequest
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetJobReportRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|jobReportRequest
operator|.
name|setJobId
argument_list|(
name|MRBuilderUtils
operator|.
name|newJobId
argument_list|(
literal|123456
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|userUsingDT
operator|.
name|getJobReport
argument_list|(
name|jobReportRequest
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Unknown job job_123456_0001"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

