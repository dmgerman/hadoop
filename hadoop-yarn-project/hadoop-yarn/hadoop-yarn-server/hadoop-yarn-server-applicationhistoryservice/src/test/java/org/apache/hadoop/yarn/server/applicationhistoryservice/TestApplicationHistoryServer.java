begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice
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
name|applicationhistoryservice
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
name|assertNotNull
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
name|http
operator|.
name|lib
operator|.
name|StaticUserWebFilter
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
name|AuthenticationFilterInitializer
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
name|service
operator|.
name|Service
operator|.
name|STATE
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
name|ExitUtil
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
name|server
operator|.
name|timeline
operator|.
name|MemoryTimelineStore
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
name|timeline
operator|.
name|TimelineStore
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
name|timeline
operator|.
name|recovery
operator|.
name|MemoryTimelineStateStore
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
name|timeline
operator|.
name|recovery
operator|.
name|TimelineStateStore
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
name|timeline
operator|.
name|security
operator|.
name|TimelineAuthenticationFilterInitializer
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
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Map
import|;
end_import

begin_class
DECL|class|TestApplicationHistoryServer
specifier|public
class|class
name|TestApplicationHistoryServer
block|{
comment|// simple test init/start/stop ApplicationHistoryServer. Status should change.
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testStartStopServer ()
specifier|public
name|void
name|testStartStopServer
parameter_list|()
throws|throws
name|Exception
block|{
name|ApplicationHistoryServer
name|historyServer
init|=
operator|new
name|ApplicationHistoryServer
argument_list|()
decl_stmt|;
name|Configuration
name|config
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|config
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_STORE
argument_list|,
name|MemoryTimelineStore
operator|.
name|class
argument_list|,
name|TimelineStore
operator|.
name|class
argument_list|)
expr_stmt|;
name|config
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_STATE_STORE_CLASS
argument_list|,
name|MemoryTimelineStateStore
operator|.
name|class
argument_list|,
name|TimelineStateStore
operator|.
name|class
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_WEBAPP_ADDRESS
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
try|try
block|{
try|try
block|{
name|historyServer
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|config
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_HANDLER_THREAD_COUNT
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|historyServer
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|TIMELINE_SERVICE_HANDLER_THREAD_COUNT
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_HANDLER_THREAD_COUNT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_CLIENT_THREAD_COUNT
argument_list|)
expr_stmt|;
name|historyServer
operator|=
operator|new
name|ApplicationHistoryServer
argument_list|()
expr_stmt|;
name|historyServer
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|INITED
argument_list|,
name|historyServer
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|ApplicationHistoryClientService
name|historyService
init|=
name|historyServer
operator|.
name|getClientService
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|historyServer
operator|.
name|getClientService
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|INITED
argument_list|,
name|historyService
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|historyServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|,
name|historyServer
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|,
name|historyService
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|historyServer
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|STOPPED
argument_list|,
name|historyServer
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|historyServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|// test launch method
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testLaunch ()
specifier|public
name|void
name|testLaunch
parameter_list|()
throws|throws
name|Exception
block|{
name|ExitUtil
operator|.
name|disableSystemExit
argument_list|()
expr_stmt|;
name|ApplicationHistoryServer
name|historyServer
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Not able to modify the config of this test case,
comment|// but others have been customized to avoid conflicts
name|historyServer
operator|=
name|ApplicationHistoryServer
operator|.
name|launchAppHistoryServer
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExitUtil
operator|.
name|ExitException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|e
operator|.
name|status
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|resetFirstExitException
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|historyServer
operator|!=
literal|null
condition|)
block|{
name|historyServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|//test launch method with -D arguments
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testLaunchWithArguments ()
specifier|public
name|void
name|testLaunchWithArguments
parameter_list|()
throws|throws
name|Exception
block|{
name|ExitUtil
operator|.
name|disableSystemExit
argument_list|()
expr_stmt|;
name|ApplicationHistoryServer
name|historyServer
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Not able to modify the config of this test case,
comment|// but others have been customized to avoid conflicts
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[
literal|2
index|]
decl_stmt|;
name|args
index|[
literal|0
index|]
operator|=
literal|"-D"
operator|+
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_LEVELDB_TTL_INTERVAL_MS
operator|+
literal|"=4000"
expr_stmt|;
name|args
index|[
literal|1
index|]
operator|=
literal|"-D"
operator|+
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_TTL_MS
operator|+
literal|"=200"
expr_stmt|;
name|historyServer
operator|=
name|ApplicationHistoryServer
operator|.
name|launchAppHistoryServer
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
name|historyServer
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"4000"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_LEVELDB_TTL_INTERVAL_MS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"200"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_TTL_MS
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExitUtil
operator|.
name|ExitException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|e
operator|.
name|status
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|resetFirstExitException
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|historyServer
operator|!=
literal|null
condition|)
block|{
name|historyServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|240000
argument_list|)
DECL|method|testFilterOverrides ()
specifier|public
name|void
name|testFilterOverrides
parameter_list|()
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|driver
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|driver
operator|.
name|put
argument_list|(
literal|""
argument_list|,
name|TimelineAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|driver
operator|.
name|put
argument_list|(
name|StaticUserWebFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|StaticUserWebFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|","
operator|+
name|TimelineAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|driver
operator|.
name|put
argument_list|(
name|AuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|TimelineAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|driver
operator|.
name|put
argument_list|(
name|TimelineAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|TimelineAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|driver
operator|.
name|put
argument_list|(
name|AuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|","
operator|+
name|TimelineAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|TimelineAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|driver
operator|.
name|put
argument_list|(
name|AuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|", "
operator|+
name|TimelineAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|TimelineAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|driver
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|filterInitializer
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|expectedValue
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|ApplicationHistoryServer
name|historyServer
init|=
operator|new
name|ApplicationHistoryServer
argument_list|()
decl_stmt|;
name|Configuration
name|config
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|config
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_STORE
argument_list|,
name|MemoryTimelineStore
operator|.
name|class
argument_list|,
name|TimelineStore
operator|.
name|class
argument_list|)
expr_stmt|;
name|config
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_STATE_STORE_CLASS
argument_list|,
name|MemoryTimelineStateStore
operator|.
name|class
argument_list|,
name|TimelineStateStore
operator|.
name|class
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_WEBAPP_ADDRESS
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
try|try
block|{
name|config
operator|.
name|set
argument_list|(
literal|"hadoop.http.filter.initializers"
argument_list|,
name|filterInitializer
argument_list|)
expr_stmt|;
name|historyServer
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|historyServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|Configuration
name|tmp
init|=
name|historyServer
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedValue
argument_list|,
name|tmp
operator|.
name|get
argument_list|(
literal|"hadoop.http.filter.initializers"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|historyServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|240000
argument_list|)
DECL|method|testHostedUIs ()
specifier|public
name|void
name|testHostedUIs
parameter_list|()
throws|throws
name|Exception
block|{
name|ApplicationHistoryServer
name|historyServer
init|=
operator|new
name|ApplicationHistoryServer
argument_list|()
decl_stmt|;
name|Configuration
name|config
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|config
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_STORE
argument_list|,
name|MemoryTimelineStore
operator|.
name|class
argument_list|,
name|TimelineStore
operator|.
name|class
argument_list|)
expr_stmt|;
name|config
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_STATE_STORE_CLASS
argument_list|,
name|MemoryTimelineStateStore
operator|.
name|class
argument_list|,
name|TimelineStateStore
operator|.
name|class
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_WEBAPP_ADDRESS
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|UI1
init|=
literal|"UI1"
decl_stmt|;
name|String
name|connFileStr
init|=
literal|""
decl_stmt|;
name|File
name|diskFile
init|=
operator|new
name|File
argument_list|(
literal|"./pom.xml"
argument_list|)
decl_stmt|;
name|String
name|diskFileStr
init|=
name|readInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|diskFile
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|config
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_UI_NAMES
argument_list|,
name|UI1
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_UI_WEB_PATH_PREFIX
operator|+
name|UI1
argument_list|,
literal|"/"
operator|+
name|UI1
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_UI_ON_DISK_PATH_PREFIX
operator|+
name|UI1
argument_list|,
literal|"./"
argument_list|)
expr_stmt|;
name|historyServer
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|historyServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|historyServer
operator|.
name|getPort
argument_list|()
operator|+
literal|"/"
operator|+
name|UI1
operator|+
literal|"/pom.xml"
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|connFileStr
operator|=
name|readInputStream
argument_list|(
name|conn
operator|.
name|getInputStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|historyServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Web file contents should be the same as on disk contents"
argument_list|,
name|diskFileStr
argument_list|,
name|connFileStr
argument_list|)
expr_stmt|;
block|}
DECL|method|readInputStream (InputStream input)
specifier|private
name|String
name|readInputStream
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|data
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|512
index|]
decl_stmt|;
name|int
name|read
decl_stmt|;
while|while
condition|(
operator|(
name|read
operator|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|data
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|data
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

