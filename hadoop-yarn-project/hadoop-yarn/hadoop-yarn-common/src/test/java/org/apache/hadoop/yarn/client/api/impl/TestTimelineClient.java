begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api.impl
package|package
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
name|api
operator|.
name|impl
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doThrow
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
name|spy
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
name|when
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ConnectException
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
name|token
operator|.
name|delegation
operator|.
name|AbstractDelegationTokenSecretManager
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
name|timeline
operator|.
name|TimelineDomain
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
name|timeline
operator|.
name|TimelineEntities
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
name|timeline
operator|.
name|TimelineEntity
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
name|timeline
operator|.
name|TimelineEvent
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
name|timeline
operator|.
name|TimelinePutResponse
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
name|api
operator|.
name|TimelineClient
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
name|YarnException
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
name|security
operator|.
name|client
operator|.
name|TimelineDelegationTokenIdentifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|junit
operator|.
name|Before
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
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|ClientHandlerException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|ClientResponse
import|;
end_import

begin_class
DECL|class|TestTimelineClient
specifier|public
class|class
name|TestTimelineClient
block|{
DECL|field|client
specifier|private
name|TimelineClientImpl
name|client
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|client
operator|=
name|createTimelineClient
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPostEntities ()
specifier|public
name|void
name|testPostEntities
parameter_list|()
throws|throws
name|Exception
block|{
name|mockEntityClientResponse
argument_list|(
name|client
argument_list|,
name|ClientResponse
operator|.
name|Status
operator|.
name|OK
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|TimelinePutResponse
name|response
init|=
name|client
operator|.
name|putEntities
argument_list|(
name|generateEntity
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is not expected"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPostEntitiesWithError ()
specifier|public
name|void
name|testPostEntitiesWithError
parameter_list|()
throws|throws
name|Exception
block|{
name|mockEntityClientResponse
argument_list|(
name|client
argument_list|,
name|ClientResponse
operator|.
name|Status
operator|.
name|OK
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|TimelinePutResponse
name|response
init|=
name|client
operator|.
name|putEntities
argument_list|(
name|generateEntity
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|response
operator|.
name|getErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test entity id"
argument_list|,
name|response
operator|.
name|getErrors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getEntityId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test entity type"
argument_list|,
name|response
operator|.
name|getErrors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getEntityType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TimelinePutResponse
operator|.
name|TimelinePutError
operator|.
name|IO_EXCEPTION
argument_list|,
name|response
operator|.
name|getErrors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getErrorCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is not expected"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPostEntitiesNoResponse ()
specifier|public
name|void
name|testPostEntitiesNoResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|mockEntityClientResponse
argument_list|(
name|client
argument_list|,
name|ClientResponse
operator|.
name|Status
operator|.
name|INTERNAL_SERVER_ERROR
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|putEntities
argument_list|(
name|generateEntity
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Failed to get the response from the timeline server."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPostEntitiesConnectionRefused ()
specifier|public
name|void
name|testPostEntitiesConnectionRefused
parameter_list|()
throws|throws
name|Exception
block|{
name|mockEntityClientResponse
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|putEntities
argument_list|(
name|generateEntity
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"RuntimeException is expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|re
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|re
operator|instanceof
name|ClientHandlerException
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPutDomain ()
specifier|public
name|void
name|testPutDomain
parameter_list|()
throws|throws
name|Exception
block|{
name|mockDomainClientResponse
argument_list|(
name|client
argument_list|,
name|ClientResponse
operator|.
name|Status
operator|.
name|OK
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|putDomain
argument_list|(
name|generateDomain
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is not expected"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPutDomainNoResponse ()
specifier|public
name|void
name|testPutDomainNoResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|mockDomainClientResponse
argument_list|(
name|client
argument_list|,
name|ClientResponse
operator|.
name|Status
operator|.
name|FORBIDDEN
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|putDomain
argument_list|(
name|generateDomain
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Failed to get the response from the timeline server."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPutDomainConnectionRefused ()
specifier|public
name|void
name|testPutDomainConnectionRefused
parameter_list|()
throws|throws
name|Exception
block|{
name|mockDomainClientResponse
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|putDomain
argument_list|(
name|generateDomain
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"RuntimeException is expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|re
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|re
operator|instanceof
name|ClientHandlerException
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCheckRetryCount ()
specifier|public
name|void
name|testCheckRetryCount
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_CLIENT_MAX_RETRIES
argument_list|,
operator|-
literal|2
argument_list|)
expr_stmt|;
name|createTimelineClient
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_CLIENT_MAX_RETRIES
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|createTimelineClient
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|newMaxRetries
init|=
literal|5
decl_stmt|;
name|long
name|newIntervalMs
init|=
literal|500
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_CLIENT_MAX_RETRIES
argument_list|,
name|newMaxRetries
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS
argument_list|,
name|newIntervalMs
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|TimelineClientImpl
name|client
init|=
name|createTimelineClient
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
comment|// This call should fail because there is no timeline server
name|client
operator|.
name|putEntities
argument_list|(
name|generateEntity
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception expected! "
operator|+
literal|"Timeline server should be off to run this test. "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|ce
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Handler exception for reason other than retry: "
operator|+
name|ce
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ce
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Connection retries limit exceeded"
argument_list|)
argument_list|)
expr_stmt|;
comment|// we would expect this exception here, check if the client has retried
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Retry filter didn't perform any retries! "
argument_list|,
name|client
operator|.
name|connectionRetry
operator|.
name|getRetired
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDelegationTokenOperationsRetry ()
specifier|public
name|void
name|testDelegationTokenOperationsRetry
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|newMaxRetries
init|=
literal|5
decl_stmt|;
name|long
name|newIntervalMs
init|=
literal|500
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_CLIENT_MAX_RETRIES
argument_list|,
name|newMaxRetries
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS
argument_list|,
name|newIntervalMs
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// use kerberos to bypass the issue in HADOOP-11215
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
name|TimelineClientImpl
name|client
init|=
name|createTimelineClient
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|TestTimlineDelegationTokenSecretManager
name|dtManager
init|=
operator|new
name|TestTimlineDelegationTokenSecretManager
argument_list|()
decl_stmt|;
try|try
block|{
name|dtManager
operator|.
name|startThreads
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
try|try
block|{
comment|// try getting a delegation token
name|client
operator|.
name|getDelegationToken
argument_list|(
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
name|assertFail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|ce
parameter_list|)
block|{
name|assertException
argument_list|(
name|client
argument_list|,
name|ce
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// try renew a delegation token
name|TimelineDelegationTokenIdentifier
name|timelineDT
init|=
operator|new
name|TimelineDelegationTokenIdentifier
argument_list|(
operator|new
name|Text
argument_list|(
literal|"tester"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"tester"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"tester"
argument_list|)
argument_list|)
decl_stmt|;
name|client
operator|.
name|renewDelegationToken
argument_list|(
operator|new
name|Token
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
argument_list|(
name|timelineDT
argument_list|,
name|dtManager
argument_list|)
argument_list|)
expr_stmt|;
name|assertFail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|ce
parameter_list|)
block|{
name|assertException
argument_list|(
name|client
argument_list|,
name|ce
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// try cancel a delegation token
name|TimelineDelegationTokenIdentifier
name|timelineDT
init|=
operator|new
name|TimelineDelegationTokenIdentifier
argument_list|(
operator|new
name|Text
argument_list|(
literal|"tester"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"tester"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"tester"
argument_list|)
argument_list|)
decl_stmt|;
name|client
operator|.
name|cancelDelegationToken
argument_list|(
operator|new
name|Token
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
argument_list|(
name|timelineDT
argument_list|,
name|dtManager
argument_list|)
argument_list|)
expr_stmt|;
name|assertFail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|ce
parameter_list|)
block|{
name|assertException
argument_list|(
name|client
argument_list|,
name|ce
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
name|dtManager
operator|.
name|stopThreads
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|assertFail ()
specifier|private
specifier|static
name|void
name|assertFail
parameter_list|()
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception expected! "
operator|+
literal|"Timeline server should be off to run this test."
argument_list|)
expr_stmt|;
block|}
DECL|method|assertException (TimelineClientImpl client, RuntimeException ce)
specifier|private
name|void
name|assertException
parameter_list|(
name|TimelineClientImpl
name|client
parameter_list|,
name|RuntimeException
name|ce
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Handler exception for reason other than retry: "
operator|+
name|ce
operator|.
name|toString
argument_list|()
argument_list|,
name|ce
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Connection retries limit exceeded"
argument_list|)
argument_list|)
expr_stmt|;
comment|// we would expect this exception here, check if the client has retried
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Retry filter didn't perform any retries! "
argument_list|,
name|client
operator|.
name|connectionRetry
operator|.
name|getRetired
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|mockEntityClientResponse ( TimelineClientImpl client, ClientResponse.Status status, boolean hasError, boolean hasRuntimeError)
specifier|private
specifier|static
name|ClientResponse
name|mockEntityClientResponse
parameter_list|(
name|TimelineClientImpl
name|client
parameter_list|,
name|ClientResponse
operator|.
name|Status
name|status
parameter_list|,
name|boolean
name|hasError
parameter_list|,
name|boolean
name|hasRuntimeError
parameter_list|)
block|{
name|ClientResponse
name|response
init|=
name|mock
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|hasRuntimeError
condition|)
block|{
name|doThrow
argument_list|(
operator|new
name|ClientHandlerException
argument_list|(
operator|new
name|ConnectException
argument_list|()
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|client
argument_list|)
operator|.
name|doPostingObject
argument_list|(
name|any
argument_list|(
name|TimelineEntities
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
name|doReturn
argument_list|(
name|response
argument_list|)
operator|.
name|when
argument_list|(
name|client
argument_list|)
operator|.
name|doPostingObject
argument_list|(
name|any
argument_list|(
name|TimelineEntities
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|response
operator|.
name|getClientResponseStatus
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|TimelinePutResponse
operator|.
name|TimelinePutError
name|error
init|=
operator|new
name|TimelinePutResponse
operator|.
name|TimelinePutError
argument_list|()
decl_stmt|;
name|error
operator|.
name|setEntityId
argument_list|(
literal|"test entity id"
argument_list|)
expr_stmt|;
name|error
operator|.
name|setEntityType
argument_list|(
literal|"test entity type"
argument_list|)
expr_stmt|;
name|error
operator|.
name|setErrorCode
argument_list|(
name|TimelinePutResponse
operator|.
name|TimelinePutError
operator|.
name|IO_EXCEPTION
argument_list|)
expr_stmt|;
name|TimelinePutResponse
name|putResponse
init|=
operator|new
name|TimelinePutResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasError
condition|)
block|{
name|putResponse
operator|.
name|addError
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
name|when
argument_list|(
name|response
operator|.
name|getEntity
argument_list|(
name|TimelinePutResponse
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|putResponse
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
DECL|method|mockDomainClientResponse ( TimelineClientImpl client, ClientResponse.Status status, boolean hasRuntimeError)
specifier|private
specifier|static
name|ClientResponse
name|mockDomainClientResponse
parameter_list|(
name|TimelineClientImpl
name|client
parameter_list|,
name|ClientResponse
operator|.
name|Status
name|status
parameter_list|,
name|boolean
name|hasRuntimeError
parameter_list|)
block|{
name|ClientResponse
name|response
init|=
name|mock
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|hasRuntimeError
condition|)
block|{
name|doThrow
argument_list|(
operator|new
name|ClientHandlerException
argument_list|(
operator|new
name|ConnectException
argument_list|()
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|client
argument_list|)
operator|.
name|doPostingObject
argument_list|(
name|any
argument_list|(
name|TimelineDomain
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
name|doReturn
argument_list|(
name|response
argument_list|)
operator|.
name|when
argument_list|(
name|client
argument_list|)
operator|.
name|doPostingObject
argument_list|(
name|any
argument_list|(
name|TimelineDomain
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|response
operator|.
name|getClientResponseStatus
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|status
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
DECL|method|generateEntity ()
specifier|private
specifier|static
name|TimelineEntity
name|generateEntity
parameter_list|()
block|{
name|TimelineEntity
name|entity
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|entity
operator|.
name|setEntityId
argument_list|(
literal|"entity id"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setEntityType
argument_list|(
literal|"entity type"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setStartTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
operator|++
name|i
control|)
block|{
name|TimelineEvent
name|event
init|=
operator|new
name|TimelineEvent
argument_list|()
decl_stmt|;
name|event
operator|.
name|setTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|event
operator|.
name|setEventType
argument_list|(
literal|"test event type "
operator|+
name|i
argument_list|)
expr_stmt|;
name|event
operator|.
name|addEventInfo
argument_list|(
literal|"key1"
argument_list|,
literal|"val1"
argument_list|)
expr_stmt|;
name|event
operator|.
name|addEventInfo
argument_list|(
literal|"key2"
argument_list|,
literal|"val2"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
name|entity
operator|.
name|addRelatedEntity
argument_list|(
literal|"test ref type 1"
argument_list|,
literal|"test ref id 1"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addRelatedEntity
argument_list|(
literal|"test ref type 2"
argument_list|,
literal|"test ref id 2"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addPrimaryFilter
argument_list|(
literal|"pkey1"
argument_list|,
literal|"pval1"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addPrimaryFilter
argument_list|(
literal|"pkey2"
argument_list|,
literal|"pval2"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addOtherInfo
argument_list|(
literal|"okey1"
argument_list|,
literal|"oval1"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addOtherInfo
argument_list|(
literal|"okey2"
argument_list|,
literal|"oval2"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setDomainId
argument_list|(
literal|"domain id 1"
argument_list|)
expr_stmt|;
return|return
name|entity
return|;
block|}
DECL|method|generateDomain ()
specifier|public
specifier|static
name|TimelineDomain
name|generateDomain
parameter_list|()
block|{
name|TimelineDomain
name|domain
init|=
operator|new
name|TimelineDomain
argument_list|()
decl_stmt|;
name|domain
operator|.
name|setId
argument_list|(
literal|"namesapce id"
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setDescription
argument_list|(
literal|"domain description"
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setOwner
argument_list|(
literal|"domain owner"
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setReaders
argument_list|(
literal|"domain_reader"
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setWriters
argument_list|(
literal|"domain_writer"
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setCreatedTime
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setModifiedTime
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
return|return
name|domain
return|;
block|}
DECL|method|createTimelineClient ( YarnConfiguration conf)
specifier|private
specifier|static
name|TimelineClientImpl
name|createTimelineClient
parameter_list|(
name|YarnConfiguration
name|conf
parameter_list|)
block|{
name|TimelineClientImpl
name|client
init|=
name|spy
argument_list|(
operator|(
name|TimelineClientImpl
operator|)
name|TimelineClient
operator|.
name|createTimelineClient
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|client
return|;
block|}
DECL|class|TestTimlineDelegationTokenSecretManager
specifier|private
specifier|static
class|class
name|TestTimlineDelegationTokenSecretManager
extends|extends
name|AbstractDelegationTokenSecretManager
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
block|{
DECL|method|TestTimlineDelegationTokenSecretManager ()
specifier|public
name|TestTimlineDelegationTokenSecretManager
parameter_list|()
block|{
name|super
argument_list|(
literal|100000
argument_list|,
literal|100000
argument_list|,
literal|100000
argument_list|,
literal|100000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createIdentifier ()
specifier|public
name|TimelineDelegationTokenIdentifier
name|createIdentifier
parameter_list|()
block|{
return|return
operator|new
name|TimelineDelegationTokenIdentifier
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

