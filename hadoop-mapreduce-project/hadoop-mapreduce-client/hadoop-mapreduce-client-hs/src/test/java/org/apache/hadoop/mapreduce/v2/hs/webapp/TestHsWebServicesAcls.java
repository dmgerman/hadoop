begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs.webapp
package|package
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
name|webapp
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
name|assertEquals
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
name|when
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
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|WebApplicationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
operator|.
name|Status
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
name|CommonConfigurationKeys
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
name|Path
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
name|JobACLsManager
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
name|mapred
operator|.
name|TaskCompletionEvent
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
name|Counters
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
name|JobACL
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
name|MRConfig
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
name|records
operator|.
name|AMInfo
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
name|records
operator|.
name|JobId
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
name|records
operator|.
name|JobReport
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
name|records
operator|.
name|JobState
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
name|records
operator|.
name|TaskAttemptCompletionEvent
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
name|records
operator|.
name|TaskId
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
name|records
operator|.
name|TaskType
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
name|app
operator|.
name|job
operator|.
name|Job
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
name|app
operator|.
name|job
operator|.
name|Task
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
name|HistoryContext
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
name|MockHistoryContext
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
name|GroupMappingServiceProvider
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
name|Groups
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
name|authorize
operator|.
name|AccessControlList
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
name|webapp
operator|.
name|WebApp
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

begin_class
DECL|class|TestHsWebServicesAcls
specifier|public
class|class
name|TestHsWebServicesAcls
block|{
DECL|field|FRIENDLY_USER
specifier|private
specifier|static
name|String
name|FRIENDLY_USER
init|=
literal|"friendly"
decl_stmt|;
DECL|field|ENEMY_USER
specifier|private
specifier|static
name|String
name|ENEMY_USER
init|=
literal|"enemy"
decl_stmt|;
DECL|field|conf
specifier|private
name|JobConf
name|conf
decl_stmt|;
DECL|field|ctx
specifier|private
name|HistoryContext
name|ctx
decl_stmt|;
DECL|field|jobIdStr
specifier|private
name|String
name|jobIdStr
decl_stmt|;
DECL|field|taskIdStr
specifier|private
name|String
name|taskIdStr
decl_stmt|;
DECL|field|taskAttemptIdStr
specifier|private
name|String
name|taskAttemptIdStr
decl_stmt|;
DECL|field|hsWebServices
specifier|private
name|HsWebServices
name|hsWebServices
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
operator|new
name|JobConf
argument_list|()
expr_stmt|;
name|this
operator|.
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_GROUP_MAPPING
argument_list|,
name|NullGroupsProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|.
name|setBoolean
argument_list|(
name|MRConfig
operator|.
name|MR_ACLS_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Groups
operator|.
name|getUserToGroupsMappingService
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|buildHistoryContext
argument_list|(
name|this
operator|.
name|conf
argument_list|)
expr_stmt|;
name|WebApp
name|webApp
init|=
name|mock
argument_list|(
name|HsWebApp
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|webApp
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"hsmockwebapp"
argument_list|)
expr_stmt|;
name|this
operator|.
name|hsWebServices
operator|=
operator|new
name|HsWebServices
argument_list|(
name|ctx
argument_list|,
name|conf
argument_list|,
name|webApp
argument_list|)
expr_stmt|;
name|this
operator|.
name|hsWebServices
operator|.
name|setResponse
argument_list|(
name|mock
argument_list|(
name|HttpServletResponse
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|ctx
operator|.
name|getAllJobs
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|this
operator|.
name|jobIdStr
operator|=
name|job
operator|.
name|getID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|Task
name|task
init|=
name|job
operator|.
name|getTasks
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|this
operator|.
name|taskIdStr
operator|=
name|task
operator|.
name|getID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|taskAttemptIdStr
operator|=
name|task
operator|.
name|getAttempts
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetJobAcls ()
specifier|public
name|void
name|testGetJobAcls
parameter_list|()
block|{
name|HttpServletRequest
name|hsr
init|=
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ENEMY_USER
argument_list|)
expr_stmt|;
try|try
block|{
name|hsWebServices
operator|.
name|getJob
argument_list|(
name|hsr
argument_list|,
name|jobIdStr
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"enemy can access job"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|WebApplicationException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|Status
operator|.
name|UNAUTHORIZED
argument_list|,
name|Status
operator|.
name|fromStatusCode
argument_list|(
name|e
operator|.
name|getResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|FRIENDLY_USER
argument_list|)
expr_stmt|;
name|hsWebServices
operator|.
name|getJob
argument_list|(
name|hsr
argument_list|,
name|jobIdStr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetJobCountersAcls ()
specifier|public
name|void
name|testGetJobCountersAcls
parameter_list|()
block|{
name|HttpServletRequest
name|hsr
init|=
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ENEMY_USER
argument_list|)
expr_stmt|;
try|try
block|{
name|hsWebServices
operator|.
name|getJobCounters
argument_list|(
name|hsr
argument_list|,
name|jobIdStr
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"enemy can access job"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|WebApplicationException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|Status
operator|.
name|UNAUTHORIZED
argument_list|,
name|Status
operator|.
name|fromStatusCode
argument_list|(
name|e
operator|.
name|getResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|FRIENDLY_USER
argument_list|)
expr_stmt|;
name|hsWebServices
operator|.
name|getJobCounters
argument_list|(
name|hsr
argument_list|,
name|jobIdStr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetJobConfAcls ()
specifier|public
name|void
name|testGetJobConfAcls
parameter_list|()
block|{
name|HttpServletRequest
name|hsr
init|=
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ENEMY_USER
argument_list|)
expr_stmt|;
try|try
block|{
name|hsWebServices
operator|.
name|getJobConf
argument_list|(
name|hsr
argument_list|,
name|jobIdStr
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"enemy can access job"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|WebApplicationException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|Status
operator|.
name|UNAUTHORIZED
argument_list|,
name|Status
operator|.
name|fromStatusCode
argument_list|(
name|e
operator|.
name|getResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|FRIENDLY_USER
argument_list|)
expr_stmt|;
name|hsWebServices
operator|.
name|getJobConf
argument_list|(
name|hsr
argument_list|,
name|jobIdStr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetJobTasksAcls ()
specifier|public
name|void
name|testGetJobTasksAcls
parameter_list|()
block|{
name|HttpServletRequest
name|hsr
init|=
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ENEMY_USER
argument_list|)
expr_stmt|;
try|try
block|{
name|hsWebServices
operator|.
name|getJobTasks
argument_list|(
name|hsr
argument_list|,
name|jobIdStr
argument_list|,
literal|"m"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"enemy can access job"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|WebApplicationException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|Status
operator|.
name|UNAUTHORIZED
argument_list|,
name|Status
operator|.
name|fromStatusCode
argument_list|(
name|e
operator|.
name|getResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|FRIENDLY_USER
argument_list|)
expr_stmt|;
name|hsWebServices
operator|.
name|getJobTasks
argument_list|(
name|hsr
argument_list|,
name|jobIdStr
argument_list|,
literal|"m"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetJobTaskAcls ()
specifier|public
name|void
name|testGetJobTaskAcls
parameter_list|()
block|{
name|HttpServletRequest
name|hsr
init|=
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ENEMY_USER
argument_list|)
expr_stmt|;
try|try
block|{
name|hsWebServices
operator|.
name|getJobTask
argument_list|(
name|hsr
argument_list|,
name|jobIdStr
argument_list|,
name|this
operator|.
name|taskIdStr
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"enemy can access job"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|WebApplicationException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|Status
operator|.
name|UNAUTHORIZED
argument_list|,
name|Status
operator|.
name|fromStatusCode
argument_list|(
name|e
operator|.
name|getResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|FRIENDLY_USER
argument_list|)
expr_stmt|;
name|hsWebServices
operator|.
name|getJobTask
argument_list|(
name|hsr
argument_list|,
name|this
operator|.
name|jobIdStr
argument_list|,
name|this
operator|.
name|taskIdStr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetSingleTaskCountersAcls ()
specifier|public
name|void
name|testGetSingleTaskCountersAcls
parameter_list|()
block|{
name|HttpServletRequest
name|hsr
init|=
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ENEMY_USER
argument_list|)
expr_stmt|;
try|try
block|{
name|hsWebServices
operator|.
name|getSingleTaskCounters
argument_list|(
name|hsr
argument_list|,
name|this
operator|.
name|jobIdStr
argument_list|,
name|this
operator|.
name|taskIdStr
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"enemy can access job"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|WebApplicationException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|Status
operator|.
name|UNAUTHORIZED
argument_list|,
name|Status
operator|.
name|fromStatusCode
argument_list|(
name|e
operator|.
name|getResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|FRIENDLY_USER
argument_list|)
expr_stmt|;
name|hsWebServices
operator|.
name|getSingleTaskCounters
argument_list|(
name|hsr
argument_list|,
name|this
operator|.
name|jobIdStr
argument_list|,
name|this
operator|.
name|taskIdStr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetJobTaskAttemptsAcls ()
specifier|public
name|void
name|testGetJobTaskAttemptsAcls
parameter_list|()
block|{
name|HttpServletRequest
name|hsr
init|=
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ENEMY_USER
argument_list|)
expr_stmt|;
try|try
block|{
name|hsWebServices
operator|.
name|getJobTaskAttempts
argument_list|(
name|hsr
argument_list|,
name|this
operator|.
name|jobIdStr
argument_list|,
name|this
operator|.
name|taskIdStr
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"enemy can access job"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|WebApplicationException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|Status
operator|.
name|UNAUTHORIZED
argument_list|,
name|Status
operator|.
name|fromStatusCode
argument_list|(
name|e
operator|.
name|getResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|FRIENDLY_USER
argument_list|)
expr_stmt|;
name|hsWebServices
operator|.
name|getJobTaskAttempts
argument_list|(
name|hsr
argument_list|,
name|this
operator|.
name|jobIdStr
argument_list|,
name|this
operator|.
name|taskIdStr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetJobTaskAttemptIdAcls ()
specifier|public
name|void
name|testGetJobTaskAttemptIdAcls
parameter_list|()
block|{
name|HttpServletRequest
name|hsr
init|=
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ENEMY_USER
argument_list|)
expr_stmt|;
try|try
block|{
name|hsWebServices
operator|.
name|getJobTaskAttemptId
argument_list|(
name|hsr
argument_list|,
name|this
operator|.
name|jobIdStr
argument_list|,
name|this
operator|.
name|taskIdStr
argument_list|,
name|this
operator|.
name|taskAttemptIdStr
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"enemy can access job"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|WebApplicationException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|Status
operator|.
name|UNAUTHORIZED
argument_list|,
name|Status
operator|.
name|fromStatusCode
argument_list|(
name|e
operator|.
name|getResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|FRIENDLY_USER
argument_list|)
expr_stmt|;
name|hsWebServices
operator|.
name|getJobTaskAttemptId
argument_list|(
name|hsr
argument_list|,
name|this
operator|.
name|jobIdStr
argument_list|,
name|this
operator|.
name|taskIdStr
argument_list|,
name|this
operator|.
name|taskAttemptIdStr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetJobTaskAttemptIdCountersAcls ()
specifier|public
name|void
name|testGetJobTaskAttemptIdCountersAcls
parameter_list|()
block|{
name|HttpServletRequest
name|hsr
init|=
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ENEMY_USER
argument_list|)
expr_stmt|;
try|try
block|{
name|hsWebServices
operator|.
name|getJobTaskAttemptIdCounters
argument_list|(
name|hsr
argument_list|,
name|this
operator|.
name|jobIdStr
argument_list|,
name|this
operator|.
name|taskIdStr
argument_list|,
name|this
operator|.
name|taskAttemptIdStr
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"enemy can access job"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|WebApplicationException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|Status
operator|.
name|UNAUTHORIZED
argument_list|,
name|Status
operator|.
name|fromStatusCode
argument_list|(
name|e
operator|.
name|getResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|when
argument_list|(
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|FRIENDLY_USER
argument_list|)
expr_stmt|;
name|hsWebServices
operator|.
name|getJobTaskAttemptIdCounters
argument_list|(
name|hsr
argument_list|,
name|this
operator|.
name|jobIdStr
argument_list|,
name|this
operator|.
name|taskIdStr
argument_list|,
name|this
operator|.
name|taskAttemptIdStr
argument_list|)
expr_stmt|;
block|}
DECL|method|buildHistoryContext (final Configuration conf)
specifier|private
specifier|static
name|HistoryContext
name|buildHistoryContext
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|HistoryContext
name|ctx
init|=
operator|new
name|MockHistoryContext
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|jobs
init|=
name|ctx
operator|.
name|getAllJobs
argument_list|()
decl_stmt|;
name|JobId
name|jobId
init|=
name|jobs
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Job
name|mockJob
init|=
operator|new
name|MockJobForAcls
argument_list|(
name|jobs
operator|.
name|get
argument_list|(
name|jobId
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|jobs
operator|.
name|put
argument_list|(
name|jobId
argument_list|,
name|mockJob
argument_list|)
expr_stmt|;
return|return
name|ctx
return|;
block|}
DECL|class|NullGroupsProvider
specifier|private
specifier|static
class|class
name|NullGroupsProvider
implements|implements
name|GroupMappingServiceProvider
block|{
annotation|@
name|Override
DECL|method|getGroups (String user)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getGroups
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|cacheGroupsRefresh ()
specifier|public
name|void
name|cacheGroupsRefresh
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|cacheGroupsAdd (List<String> groups)
specifier|public
name|void
name|cacheGroupsAdd
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|groups
parameter_list|)
throws|throws
name|IOException
block|{     }
block|}
DECL|class|MockJobForAcls
specifier|private
specifier|static
class|class
name|MockJobForAcls
implements|implements
name|Job
block|{
DECL|field|mockJob
specifier|private
name|Job
name|mockJob
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|jobAcls
specifier|private
name|Map
argument_list|<
name|JobACL
argument_list|,
name|AccessControlList
argument_list|>
name|jobAcls
decl_stmt|;
DECL|field|aclsMgr
specifier|private
name|JobACLsManager
name|aclsMgr
decl_stmt|;
DECL|method|MockJobForAcls (Job mockJob, Configuration conf)
specifier|public
name|MockJobForAcls
parameter_list|(
name|Job
name|mockJob
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|mockJob
operator|=
name|mockJob
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|AccessControlList
name|viewAcl
init|=
operator|new
name|AccessControlList
argument_list|(
name|FRIENDLY_USER
argument_list|)
decl_stmt|;
name|this
operator|.
name|jobAcls
operator|=
operator|new
name|HashMap
argument_list|<
name|JobACL
argument_list|,
name|AccessControlList
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|jobAcls
operator|.
name|put
argument_list|(
name|JobACL
operator|.
name|VIEW_JOB
argument_list|,
name|viewAcl
argument_list|)
expr_stmt|;
name|this
operator|.
name|aclsMgr
operator|=
operator|new
name|JobACLsManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getID ()
specifier|public
name|JobId
name|getID
parameter_list|()
block|{
return|return
name|mockJob
operator|.
name|getID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|mockJob
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getState ()
specifier|public
name|JobState
name|getState
parameter_list|()
block|{
return|return
name|mockJob
operator|.
name|getState
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getReport ()
specifier|public
name|JobReport
name|getReport
parameter_list|()
block|{
return|return
name|mockJob
operator|.
name|getReport
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getAllCounters ()
specifier|public
name|Counters
name|getAllCounters
parameter_list|()
block|{
return|return
name|mockJob
operator|.
name|getAllCounters
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTasks ()
specifier|public
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|getTasks
parameter_list|()
block|{
return|return
name|mockJob
operator|.
name|getTasks
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTasks (TaskType taskType)
specifier|public
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|getTasks
parameter_list|(
name|TaskType
name|taskType
parameter_list|)
block|{
return|return
name|mockJob
operator|.
name|getTasks
argument_list|(
name|taskType
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTask (TaskId taskID)
specifier|public
name|Task
name|getTask
parameter_list|(
name|TaskId
name|taskID
parameter_list|)
block|{
return|return
name|mockJob
operator|.
name|getTask
argument_list|(
name|taskID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDiagnostics ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getDiagnostics
parameter_list|()
block|{
return|return
name|mockJob
operator|.
name|getDiagnostics
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTotalMaps ()
specifier|public
name|int
name|getTotalMaps
parameter_list|()
block|{
return|return
name|mockJob
operator|.
name|getTotalMaps
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTotalReduces ()
specifier|public
name|int
name|getTotalReduces
parameter_list|()
block|{
return|return
name|mockJob
operator|.
name|getTotalReduces
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCompletedMaps ()
specifier|public
name|int
name|getCompletedMaps
parameter_list|()
block|{
return|return
name|mockJob
operator|.
name|getCompletedMaps
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCompletedReduces ()
specifier|public
name|int
name|getCompletedReduces
parameter_list|()
block|{
return|return
name|mockJob
operator|.
name|getCompletedReduces
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
return|return
name|mockJob
operator|.
name|getProgress
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isUber ()
specifier|public
name|boolean
name|isUber
parameter_list|()
block|{
return|return
name|mockJob
operator|.
name|isUber
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getUserName ()
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|mockJob
operator|.
name|getUserName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getQueueName ()
specifier|public
name|String
name|getQueueName
parameter_list|()
block|{
return|return
name|mockJob
operator|.
name|getQueueName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getConfFile ()
specifier|public
name|Path
name|getConfFile
parameter_list|()
block|{
return|return
operator|new
name|Path
argument_list|(
literal|"/some/path/to/conf"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|loadConfFile ()
specifier|public
name|Configuration
name|loadConfFile
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|getJobACLs ()
specifier|public
name|Map
argument_list|<
name|JobACL
argument_list|,
name|AccessControlList
argument_list|>
name|getJobACLs
parameter_list|()
block|{
return|return
name|jobAcls
return|;
block|}
annotation|@
name|Override
DECL|method|getTaskAttemptCompletionEvents ( int fromEventId, int maxEvents)
specifier|public
name|TaskAttemptCompletionEvent
index|[]
name|getTaskAttemptCompletionEvents
parameter_list|(
name|int
name|fromEventId
parameter_list|,
name|int
name|maxEvents
parameter_list|)
block|{
return|return
name|mockJob
operator|.
name|getTaskAttemptCompletionEvents
argument_list|(
name|fromEventId
argument_list|,
name|maxEvents
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMapAttemptCompletionEvents ( int startIndex, int maxEvents)
specifier|public
name|TaskCompletionEvent
index|[]
name|getMapAttemptCompletionEvents
parameter_list|(
name|int
name|startIndex
parameter_list|,
name|int
name|maxEvents
parameter_list|)
block|{
return|return
name|mockJob
operator|.
name|getMapAttemptCompletionEvents
argument_list|(
name|startIndex
argument_list|,
name|maxEvents
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAMInfos ()
specifier|public
name|List
argument_list|<
name|AMInfo
argument_list|>
name|getAMInfos
parameter_list|()
block|{
return|return
name|mockJob
operator|.
name|getAMInfos
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|checkAccess (UserGroupInformation callerUGI, JobACL jobOperation)
specifier|public
name|boolean
name|checkAccess
parameter_list|(
name|UserGroupInformation
name|callerUGI
parameter_list|,
name|JobACL
name|jobOperation
parameter_list|)
block|{
return|return
name|aclsMgr
operator|.
name|checkAccess
argument_list|(
name|callerUGI
argument_list|,
name|jobOperation
argument_list|,
name|this
operator|.
name|getUserName
argument_list|()
argument_list|,
name|jobAcls
operator|.
name|get
argument_list|(
name|jobOperation
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

