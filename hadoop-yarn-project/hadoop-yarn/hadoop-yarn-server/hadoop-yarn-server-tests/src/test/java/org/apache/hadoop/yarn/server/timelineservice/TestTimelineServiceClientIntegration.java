begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice
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
name|timelineservice
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
name|timelineservice
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
name|server
operator|.
name|api
operator|.
name|CollectorNodemanagerProtocol
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
name|timelineservice
operator|.
name|collector
operator|.
name|PerNodeTimelineCollectorsAuxService
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
name|timelineservice
operator|.
name|collector
operator|.
name|TimelineCollectorManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
DECL|class|TestTimelineServiceClientIntegration
specifier|public
class|class
name|TestTimelineServiceClientIntegration
block|{
DECL|field|collectorManager
specifier|private
specifier|static
name|TimelineCollectorManager
name|collectorManager
decl_stmt|;
DECL|field|auxService
specifier|private
specifier|static
name|PerNodeTimelineCollectorsAuxService
name|auxService
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupClass ()
specifier|public
specifier|static
name|void
name|setupClass
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|collectorManager
operator|=
operator|new
name|MyTimelineCollectorManager
argument_list|()
expr_stmt|;
name|auxService
operator|=
name|PerNodeTimelineCollectorsAuxService
operator|.
name|launchServer
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
name|collectorManager
argument_list|)
expr_stmt|;
name|auxService
operator|.
name|addApplication
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
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
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|tearDownClass ()
specifier|public
specifier|static
name|void
name|tearDownClass
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|auxService
operator|!=
literal|null
condition|)
block|{
name|auxService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPutEntities ()
specifier|public
name|void
name|testPutEntities
parameter_list|()
throws|throws
name|Exception
block|{
name|TimelineClient
name|client
init|=
name|TimelineClient
operator|.
name|createTimelineClient
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
comment|// set the timeline service address manually
name|client
operator|.
name|setTimelineServiceAddress
argument_list|(
name|collectorManager
operator|.
name|getRestServerBindAddress
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|init
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
name|TimelineEntity
name|entity
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|entity
operator|.
name|setType
argument_list|(
literal|"test entity type"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setId
argument_list|(
literal|"test entity id"
argument_list|)
expr_stmt|;
name|client
operator|.
name|putEntities
argument_list|(
name|entity
argument_list|)
expr_stmt|;
name|client
operator|.
name|putEntitiesAsync
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|MyTimelineCollectorManager
specifier|private
specifier|static
class|class
name|MyTimelineCollectorManager
extends|extends
name|TimelineCollectorManager
block|{
DECL|method|MyTimelineCollectorManager ()
specifier|public
name|MyTimelineCollectorManager
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNMCollectorService ()
specifier|protected
name|CollectorNodemanagerProtocol
name|getNMCollectorService
parameter_list|()
block|{
return|return
name|mock
argument_list|(
name|CollectorNodemanagerProtocol
operator|.
name|class
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

