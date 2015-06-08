begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.recovery
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Optional
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
name|LinkedHashSet
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|SecretKey
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|curator
operator|.
name|test
operator|.
name|TestingServer
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
name|util
operator|.
name|StringUtils
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
name|util
operator|.
name|Tool
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
name|util
operator|.
name|ToolRunner
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
name|ApplicationAttemptId
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
name|ApplicationId
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
name|ContainerId
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
name|AMRMTokenIdentifier
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
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
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
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|records
operator|.
name|ApplicationStateData
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
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMApp
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
name|server
operator|.
name|resourcemanager
operator|.
name|security
operator|.
name|AMRMTokenSecretManager
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
name|server
operator|.
name|resourcemanager
operator|.
name|security
operator|.
name|ClientToAMTokenSecretManagerInRM
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
name|Log
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
name|LogFactory
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
name|After
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

begin_class
DECL|class|TestZKRMStateStorePerf
specifier|public
class|class
name|TestZKRMStateStorePerf
extends|extends
name|RMStateStoreTestBase
implements|implements
name|Tool
block|{
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
name|TestZKRMStateStore
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|version
specifier|final
name|String
name|version
init|=
literal|"0.1"
decl_stmt|;
comment|// Configurable variables for performance test
DECL|field|ZK_PERF_NUM_APP_DEFAULT
specifier|private
name|int
name|ZK_PERF_NUM_APP_DEFAULT
init|=
literal|1000
decl_stmt|;
DECL|field|ZK_PERF_NUM_APPATTEMPT_PER_APP
specifier|private
name|int
name|ZK_PERF_NUM_APPATTEMPT_PER_APP
init|=
literal|10
decl_stmt|;
DECL|field|clusterTimeStamp
specifier|private
specifier|final
name|long
name|clusterTimeStamp
init|=
literal|1352994193343L
decl_stmt|;
DECL|field|USAGE
specifier|private
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"Usage: "
operator|+
name|TestZKRMStateStorePerf
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" -appSize numberOfApplications"
operator|+
literal|" -appAttemptSize numberOfApplicationAttempts"
operator|+
literal|" [-hostPort Host:Port]"
operator|+
literal|" [-workingZnode rootZnodeForTesting]\n"
decl_stmt|;
DECL|field|conf
specifier|private
name|YarnConfiguration
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|workingZnode
specifier|private
name|String
name|workingZnode
init|=
literal|"/Test"
decl_stmt|;
DECL|field|store
specifier|private
name|ZKRMStateStore
name|store
decl_stmt|;
DECL|field|appTokenMgr
specifier|private
name|AMRMTokenSecretManager
name|appTokenMgr
decl_stmt|;
DECL|field|clientToAMTokenMgr
specifier|private
name|ClientToAMTokenSecretManagerInRM
name|clientToAMTokenMgr
decl_stmt|;
DECL|field|curatorTestingServer
specifier|private
name|TestingServer
name|curatorTestingServer
decl_stmt|;
annotation|@
name|Before
DECL|method|setUpZKServer ()
specifier|public
name|void
name|setUpZKServer
parameter_list|()
throws|throws
name|Exception
block|{
name|curatorTestingServer
operator|=
operator|new
name|TestingServer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|appTokenMgr
operator|!=
literal|null
condition|)
block|{
name|appTokenMgr
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|curatorTestingServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|initStore (String hostPort)
specifier|private
name|void
name|initStore
parameter_list|(
name|String
name|hostPort
parameter_list|)
block|{
name|Optional
argument_list|<
name|String
argument_list|>
name|optHostPort
init|=
name|Optional
operator|.
name|fromNullable
argument_list|(
name|hostPort
argument_list|)
decl_stmt|;
name|RMContext
name|rmContext
init|=
name|mock
argument_list|(
name|RMContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_ADDRESS
argument_list|,
name|optHostPort
operator|.
name|or
argument_list|(
name|curatorTestingServer
operator|.
name|getConnectString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|ZK_RM_STATE_STORE_PARENT_PATH
argument_list|,
name|workingZnode
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|ZKRMStateStore
argument_list|()
expr_stmt|;
name|store
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
name|when
argument_list|(
name|rmContext
operator|.
name|getStateStore
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|appTokenMgr
operator|=
operator|new
name|AMRMTokenSecretManager
argument_list|(
name|conf
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
name|appTokenMgr
operator|.
name|start
argument_list|()
expr_stmt|;
name|clientToAMTokenMgr
operator|=
operator|new
name|ClientToAMTokenSecretManagerInRM
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting ZKRMStateStorePerf ver."
operator|+
name|version
argument_list|)
expr_stmt|;
name|int
name|numApp
init|=
name|ZK_PERF_NUM_APP_DEFAULT
decl_stmt|;
name|int
name|numAppAttemptPerApp
init|=
name|ZK_PERF_NUM_APPATTEMPT_PER_APP
decl_stmt|;
name|String
name|hostPort
init|=
literal|null
decl_stmt|;
name|boolean
name|launchLocalZK
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Missing arguments."
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// parse command line
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"-appsize"
argument_list|)
condition|)
block|{
name|numApp
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"-appattemptsize"
argument_list|)
condition|)
block|{
name|numAppAttemptPerApp
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"-hostPort"
argument_list|)
condition|)
block|{
name|hostPort
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
name|launchLocalZK
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"-workingZnode"
argument_list|)
condition|)
block|{
name|workingZnode
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Illegal argument: "
operator|+
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
if|if
condition|(
name|launchLocalZK
condition|)
block|{
try|try
block|{
name|setUpZKServer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"failed to setup. : "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
name|initStore
argument_list|(
name|hostPort
argument_list|)
expr_stmt|;
name|long
name|submitTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|1234
decl_stmt|;
name|ArrayList
argument_list|<
name|ApplicationId
argument_list|>
name|applicationIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|RMApp
argument_list|>
name|rmApps
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|ApplicationAttemptId
argument_list|>
name|attemptIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|Set
argument_list|<
name|ApplicationAttemptId
argument_list|>
argument_list|>
name|appIdsToAttemptId
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|TestDispatcher
name|dispatcher
init|=
operator|new
name|TestDispatcher
argument_list|()
decl_stmt|;
name|store
operator|.
name|setRMDispatcher
argument_list|(
name|dispatcher
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
name|numApp
condition|;
name|i
operator|++
control|)
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|clusterTimeStamp
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|applicationIds
operator|.
name|add
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|ApplicationAttemptId
argument_list|>
name|attemptIdsForThisApp
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numAppAttemptPerApp
condition|;
name|j
operator|++
control|)
block|{
name|ApplicationAttemptId
name|attemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
name|j
argument_list|)
decl_stmt|;
name|attemptIdsForThisApp
operator|.
name|add
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
block|}
name|appIdsToAttemptId
operator|.
name|put
argument_list|(
name|appId
argument_list|,
operator|new
name|LinkedHashSet
argument_list|(
name|attemptIdsForThisApp
argument_list|)
argument_list|)
expr_stmt|;
name|attemptIds
operator|.
name|addAll
argument_list|(
name|attemptIdsForThisApp
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ApplicationId
name|appId
range|:
name|applicationIds
control|)
block|{
name|RMApp
name|app
init|=
literal|null
decl_stmt|;
try|try
block|{
name|app
operator|=
name|storeApp
argument_list|(
name|store
argument_list|,
name|appId
argument_list|,
name|submitTime
argument_list|,
name|startTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"failed to create Application Znode. : "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|waitNotify
argument_list|(
name|dispatcher
argument_list|)
expr_stmt|;
name|rmApps
operator|.
name|add
argument_list|(
name|app
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ApplicationAttemptId
name|attemptId
range|:
name|attemptIds
control|)
block|{
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|tokenId
init|=
name|generateAMRMToken
argument_list|(
name|attemptId
argument_list|,
name|appTokenMgr
argument_list|)
decl_stmt|;
name|SecretKey
name|clientTokenKey
init|=
name|clientToAMTokenMgr
operator|.
name|createMasterKey
argument_list|(
name|attemptId
argument_list|)
decl_stmt|;
try|try
block|{
name|storeAttempt
argument_list|(
name|store
argument_list|,
name|attemptId
argument_list|,
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|attemptId
argument_list|,
literal|0L
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|tokenId
argument_list|,
name|clientTokenKey
argument_list|,
name|dispatcher
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"failed to create AppAttempt Znode. : "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
name|long
name|storeStart
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
name|store
operator|.
name|loadState
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"failed to locaState from ZKRMStateStore. : "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|long
name|storeEnd
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|loadTime
init|=
name|storeEnd
operator|-
name|storeStart
decl_stmt|;
name|String
name|resultMsg
init|=
literal|"ZKRMStateStore takes "
operator|+
name|loadTime
operator|+
literal|" msec to loadState."
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|resultMsg
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|resultMsg
argument_list|)
expr_stmt|;
comment|// cleanup
try|try
block|{
for|for
control|(
name|RMApp
name|app
range|:
name|rmApps
control|)
block|{
name|ApplicationStateData
name|appState
init|=
name|ApplicationStateData
operator|.
name|newInstance
argument_list|(
name|app
operator|.
name|getSubmitTime
argument_list|()
argument_list|,
name|app
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|app
operator|.
name|getApplicationSubmissionContext
argument_list|()
argument_list|,
name|app
operator|.
name|getUser
argument_list|()
argument_list|)
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|app
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|Map
name|m
init|=
name|mock
argument_list|(
name|Map
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|m
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appIdsToAttemptId
operator|.
name|get
argument_list|(
name|appId
argument_list|)
argument_list|)
expr_stmt|;
name|appState
operator|.
name|attempts
operator|=
name|m
expr_stmt|;
name|store
operator|.
name|removeApplicationStateInternal
argument_list|(
name|appState
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"failed to cleanup. : "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// currently this function is just ignored
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Test
DECL|method|perfZKRMStateStore ()
specifier|public
name|void
name|perfZKRMStateStore
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"-appSize"
block|,
name|String
operator|.
name|valueOf
argument_list|(
name|ZK_PERF_NUM_APP_DEFAULT
argument_list|)
block|,
literal|"-appAttemptSize"
block|,
name|String
operator|.
name|valueOf
argument_list|(
name|ZK_PERF_NUM_APPATTEMPT_PER_APP
argument_list|)
block|}
decl_stmt|;
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|static
specifier|public
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|TestZKRMStateStorePerf
name|perf
init|=
operator|new
name|TestZKRMStateStorePerf
argument_list|()
decl_stmt|;
name|int
name|res
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|res
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|perf
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|res
operator|=
operator|-
literal|2
expr_stmt|;
block|}
if|if
condition|(
name|res
operator|==
operator|-
literal|1
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
name|USAGE
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

