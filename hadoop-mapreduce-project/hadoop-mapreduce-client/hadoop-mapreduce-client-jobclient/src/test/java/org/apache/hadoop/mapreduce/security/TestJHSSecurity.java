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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|org
operator|.
name|junit
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
name|protocolrecords
operator|.
name|CancelDelegationTokenRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|RenewDelegationTokenRequest
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
name|HistoryClientService
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
name|HistoryServerStateStoreService
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
name|JHSDelegationTokenSecretManager
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
name|yarn
operator|.
name|api
operator|.
name|records
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
name|ConverterUtils
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
name|junit
operator|.
name|Test
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

begin_class
DECL|class|TestJHSSecurity
specifier|public
class|class
name|TestJHSSecurity
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestJHSSecurity
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
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
name|long
name|initialInterval
init|=
literal|10000l
decl_stmt|;
specifier|final
name|long
name|maxLifetime
init|=
literal|20000l
decl_stmt|;
specifier|final
name|long
name|renewInterval
init|=
literal|10000l
decl_stmt|;
name|JobHistoryServer
name|jobHistoryServer
init|=
literal|null
decl_stmt|;
name|MRClientProtocol
name|clientUsingDT
init|=
literal|null
decl_stmt|;
name|long
name|tokenFetchTime
decl_stmt|;
try|try
block|{
name|jobHistoryServer
operator|=
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
annotation|@
name|Override
specifier|protected
name|JHSDelegationTokenSecretManager
name|createJHSSecretManager
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|HistoryServerStateStoreService
name|store
parameter_list|)
block|{
return|return
operator|new
name|JHSDelegationTokenSecretManager
argument_list|(
name|initialInterval
argument_list|,
name|maxLifetime
argument_list|,
name|renewInterval
argument_list|,
literal|3600000
argument_list|,
name|store
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|HistoryClientService
name|createHistoryClientService
parameter_list|()
block|{
return|return
operator|new
name|HistoryClientService
argument_list|(
name|historyContext
argument_list|,
name|this
operator|.
name|jhsDTSecretManager
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|initializeWebApp
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// Don't need it, skip.;
block|}
block|}
return|;
block|}
block|}
expr_stmt|;
comment|//      final JobHistoryServer jobHistoryServer = jhServer;
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
specifier|final
name|MRClientProtocol
name|hsService
init|=
name|jobHistoryServer
operator|.
name|getClientService
argument_list|()
operator|.
name|getClientHandler
argument_list|()
decl_stmt|;
comment|// Fake the authentication-method
name|UserGroupInformation
name|loggedInUser
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"testrenewer@APACHE.ORG"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"testrenewer"
argument_list|,
name|loggedInUser
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Default realm is APACHE.ORG
name|loggedInUser
operator|.
name|setAuthenticationMethod
argument_list|(
name|AuthenticationMethod
operator|.
name|KERBEROS
argument_list|)
expr_stmt|;
name|Token
name|token
init|=
name|getDelegationToken
argument_list|(
name|loggedInUser
argument_list|,
name|hsService
argument_list|,
name|loggedInUser
operator|.
name|getShortUserName
argument_list|()
argument_list|)
decl_stmt|;
name|tokenFetchTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got delegation token at: "
operator|+
name|tokenFetchTime
argument_list|)
expr_stmt|;
comment|// Now try talking to JHS using the delegation token
name|clientUsingDT
operator|=
name|getMRClientProtocol
argument_list|(
name|token
argument_list|,
name|jobHistoryServer
operator|.
name|getClientService
argument_list|()
operator|.
name|getBindAddress
argument_list|()
argument_list|,
literal|"TheDarkLord"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
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
name|clientUsingDT
operator|.
name|getJobReport
argument_list|(
name|jobReportRequest
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
comment|// Renew after 50% of token age.
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|tokenFetchTime
operator|+
name|initialInterval
operator|/
literal|2
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500l
argument_list|)
expr_stmt|;
block|}
name|long
name|nextExpTime
init|=
name|renewDelegationToken
argument_list|(
name|loggedInUser
argument_list|,
name|hsService
argument_list|,
name|token
argument_list|)
decl_stmt|;
name|long
name|renewalTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Renewed token at: "
operator|+
name|renewalTime
operator|+
literal|", NextExpiryTime: "
operator|+
name|nextExpTime
argument_list|)
expr_stmt|;
comment|// Wait for first expiry, but before renewed expiry.
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>
name|tokenFetchTime
operator|+
name|initialInterval
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|nextExpTime
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500l
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|50l
argument_list|)
expr_stmt|;
comment|// Valid token because of renewal.
try|try
block|{
name|clientUsingDT
operator|.
name|getJobReport
argument_list|(
name|jobReportRequest
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
comment|// Wait for expiry.
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|renewalTime
operator|+
name|renewInterval
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500l
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|50l
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"At time: "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|", token should be invalid"
argument_list|)
expr_stmt|;
comment|// Token should have expired.
try|try
block|{
name|clientUsingDT
operator|.
name|getJobReport
argument_list|(
name|jobReportRequest
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should not have succeeded with an expired token"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is expired"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Test cancellation
comment|// Stop the existing proxy, start another.
if|if
condition|(
name|clientUsingDT
operator|!=
literal|null
condition|)
block|{
comment|//        RPC.stopProxy(clientUsingDT);
name|clientUsingDT
operator|=
literal|null
expr_stmt|;
block|}
name|token
operator|=
name|getDelegationToken
argument_list|(
name|loggedInUser
argument_list|,
name|hsService
argument_list|,
name|loggedInUser
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
name|tokenFetchTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got delegation token at: "
operator|+
name|tokenFetchTime
argument_list|)
expr_stmt|;
comment|// Now try talking to HSService using the delegation token
name|clientUsingDT
operator|=
name|getMRClientProtocol
argument_list|(
name|token
argument_list|,
name|jobHistoryServer
operator|.
name|getClientService
argument_list|()
operator|.
name|getBindAddress
argument_list|()
argument_list|,
literal|"loginuser2"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
try|try
block|{
name|clientUsingDT
operator|.
name|getJobReport
argument_list|(
name|jobReportRequest
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Unexpected exception"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|cancelDelegationToken
argument_list|(
name|loggedInUser
argument_list|,
name|hsService
argument_list|,
name|token
argument_list|)
expr_stmt|;
comment|// Testing the token with different renewer to cancel the token
name|Token
name|tokenWithDifferentRenewer
init|=
name|getDelegationToken
argument_list|(
name|loggedInUser
argument_list|,
name|hsService
argument_list|,
literal|"yarn"
argument_list|)
decl_stmt|;
name|cancelDelegationToken
argument_list|(
name|loggedInUser
argument_list|,
name|hsService
argument_list|,
name|tokenWithDifferentRenewer
argument_list|)
expr_stmt|;
if|if
condition|(
name|clientUsingDT
operator|!=
literal|null
condition|)
block|{
comment|//        RPC.stopProxy(clientUsingDT);
name|clientUsingDT
operator|=
literal|null
expr_stmt|;
block|}
comment|// Creating a new connection.
name|clientUsingDT
operator|=
name|getMRClientProtocol
argument_list|(
name|token
argument_list|,
name|jobHistoryServer
operator|.
name|getClientService
argument_list|()
operator|.
name|getBindAddress
argument_list|()
argument_list|,
literal|"loginuser2"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Cancelled delegation token at: "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify cancellation worked.
try|try
block|{
name|clientUsingDT
operator|.
name|getJobReport
argument_list|(
name|jobReportRequest
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should not have succeeded with a cancelled delegation token"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{       }
block|}
finally|finally
block|{
name|jobHistoryServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getDelegationToken ( final UserGroupInformation loggedInUser, final MRClientProtocol hsService, final String renewerString)
specifier|private
name|Token
name|getDelegationToken
parameter_list|(
specifier|final
name|UserGroupInformation
name|loggedInUser
parameter_list|,
specifier|final
name|MRClientProtocol
name|hsService
parameter_list|,
specifier|final
name|String
name|renewerString
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// Get the delegation token directly as it is a little difficult to setup
comment|// the kerberos based rpc.
name|Token
name|token
init|=
name|loggedInUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Token
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Token
name|run
parameter_list|()
throws|throws
name|IOException
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
name|renewerString
argument_list|)
expr_stmt|;
return|return
name|hsService
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
return|return
name|token
return|;
block|}
DECL|method|renewDelegationToken (final UserGroupInformation loggedInUser, final MRClientProtocol hsService, final Token dToken)
specifier|private
name|long
name|renewDelegationToken
parameter_list|(
specifier|final
name|UserGroupInformation
name|loggedInUser
parameter_list|,
specifier|final
name|MRClientProtocol
name|hsService
parameter_list|,
specifier|final
name|Token
name|dToken
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|long
name|nextExpTime
init|=
name|loggedInUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|RenewDelegationTokenRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RenewDelegationTokenRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setDelegationToken
argument_list|(
name|dToken
argument_list|)
expr_stmt|;
return|return
name|hsService
operator|.
name|renewDelegationToken
argument_list|(
name|request
argument_list|)
operator|.
name|getNextExpirationTime
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|nextExpTime
return|;
block|}
DECL|method|cancelDelegationToken (final UserGroupInformation loggedInUser, final MRClientProtocol hsService, final Token dToken)
specifier|private
name|void
name|cancelDelegationToken
parameter_list|(
specifier|final
name|UserGroupInformation
name|loggedInUser
parameter_list|,
specifier|final
name|MRClientProtocol
name|hsService
parameter_list|,
specifier|final
name|Token
name|dToken
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|loggedInUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|CancelDelegationTokenRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|CancelDelegationTokenRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setDelegationToken
argument_list|(
name|dToken
argument_list|)
expr_stmt|;
name|hsService
operator|.
name|cancelDelegationToken
argument_list|(
name|request
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|getMRClientProtocol (Token token, final InetSocketAddress hsAddress, String user, final Configuration conf)
specifier|private
name|MRClientProtocol
name|getMRClientProtocol
parameter_list|(
name|Token
name|token
parameter_list|,
specifier|final
name|InetSocketAddress
name|hsAddress
parameter_list|,
name|String
name|user
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|)
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
name|ConverterUtils
operator|.
name|convertFromYarn
argument_list|(
name|token
argument_list|,
name|hsAddress
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
name|hsWithDT
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
name|hsAddress
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|hsWithDT
return|;
block|}
block|}
end_class

end_unit

