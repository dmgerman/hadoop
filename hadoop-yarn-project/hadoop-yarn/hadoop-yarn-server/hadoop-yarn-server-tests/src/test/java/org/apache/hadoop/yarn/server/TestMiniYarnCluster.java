begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server
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

begin_class
DECL|class|TestMiniYarnCluster
specifier|public
class|class
name|TestMiniYarnCluster
block|{
annotation|@
name|Test
DECL|method|testTimelineServiceStartInMiniCluster ()
specifier|public
name|void
name|testTimelineServiceStartInMiniCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|int
name|numNodeManagers
init|=
literal|1
decl_stmt|;
name|int
name|numLocalDirs
init|=
literal|1
decl_stmt|;
name|int
name|numLogDirs
init|=
literal|1
decl_stmt|;
name|boolean
name|enableAHS
decl_stmt|;
comment|/*      * Timeline service should not start if TIMELINE_SERVICE_ENABLED == false      * and enableAHS flag == false      */
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|enableAHS
operator|=
literal|false
expr_stmt|;
name|MiniYARNCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cluster
operator|=
operator|new
name|MiniYARNCluster
argument_list|(
name|TestMiniYarnCluster
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|numNodeManagers
argument_list|,
name|numLocalDirs
argument_list|,
name|numLogDirs
argument_list|,
name|numLogDirs
argument_list|,
name|enableAHS
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|start
argument_list|()
expr_stmt|;
comment|//verify that the timeline service is not started.
name|Assert
operator|.
name|assertNull
argument_list|(
literal|"Timeline Service should not have been started"
argument_list|,
name|cluster
operator|.
name|getApplicationHistoryServer
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/*      * Timeline service should start if TIMELINE_SERVICE_ENABLED == true      * and enableAHS == false      */
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
name|enableAHS
operator|=
literal|false
expr_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|cluster
operator|=
operator|new
name|MiniYARNCluster
argument_list|(
name|TestMiniYarnCluster
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|numNodeManagers
argument_list|,
name|numLocalDirs
argument_list|,
name|numLogDirs
argument_list|,
name|numLogDirs
argument_list|,
name|enableAHS
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|start
argument_list|()
expr_stmt|;
comment|//Timeline service may sometime take a while to get started
name|int
name|wait
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|cluster
operator|.
name|getApplicationHistoryServer
argument_list|()
operator|==
literal|null
operator|&&
name|wait
operator|<
literal|20
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|wait
operator|++
expr_stmt|;
block|}
comment|//verify that the timeline service is started.
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Timeline Service should have been started"
argument_list|,
name|cluster
operator|.
name|getApplicationHistoryServer
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/*      * Timeline service should start if TIMELINE_SERVICE_ENABLED == false      * and enableAHS == true      */
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|enableAHS
operator|=
literal|true
expr_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|cluster
operator|=
operator|new
name|MiniYARNCluster
argument_list|(
name|TestMiniYarnCluster
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|numNodeManagers
argument_list|,
name|numLocalDirs
argument_list|,
name|numLogDirs
argument_list|,
name|numLogDirs
argument_list|,
name|enableAHS
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|start
argument_list|()
expr_stmt|;
comment|//Timeline service may sometime take a while to get started
name|int
name|wait
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|cluster
operator|.
name|getApplicationHistoryServer
argument_list|()
operator|==
literal|null
operator|&&
name|wait
operator|<
literal|20
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|wait
operator|++
expr_stmt|;
block|}
comment|//verify that the timeline service is started.
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Timeline Service should have been started"
argument_list|,
name|cluster
operator|.
name|getApplicationHistoryServer
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

