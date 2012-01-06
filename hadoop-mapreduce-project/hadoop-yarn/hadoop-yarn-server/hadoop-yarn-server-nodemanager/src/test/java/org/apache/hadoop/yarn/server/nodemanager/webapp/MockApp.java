begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.webapp
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
name|nodemanager
operator|.
name|webapp
package|;
end_package

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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
operator|.
name|Application
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
operator|.
name|ApplicationEvent
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
operator|.
name|ApplicationState
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
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
name|BuilderUtils
import|;
end_import

begin_class
DECL|class|MockApp
specifier|public
class|class
name|MockApp
implements|implements
name|Application
block|{
DECL|field|user
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|appId
specifier|final
name|ApplicationId
name|appId
decl_stmt|;
DECL|field|containers
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|Container
argument_list|>
name|containers
init|=
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|Container
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|appState
name|ApplicationState
name|appState
decl_stmt|;
DECL|field|app
name|Application
name|app
decl_stmt|;
DECL|method|MockApp (int uniqId)
specifier|public
name|MockApp
parameter_list|(
name|int
name|uniqId
parameter_list|)
block|{
name|this
argument_list|(
literal|"mockUser"
argument_list|,
literal|1234
argument_list|,
name|uniqId
argument_list|)
expr_stmt|;
block|}
DECL|method|MockApp (String user, long clusterTimeStamp, int uniqId)
specifier|public
name|MockApp
parameter_list|(
name|String
name|user
parameter_list|,
name|long
name|clusterTimeStamp
parameter_list|,
name|int
name|uniqId
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
comment|// Add an application and the corresponding containers
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|appId
operator|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
name|recordFactory
argument_list|,
name|clusterTimeStamp
argument_list|,
name|uniqId
argument_list|)
expr_stmt|;
name|appState
operator|=
name|ApplicationState
operator|.
name|NEW
expr_stmt|;
block|}
DECL|method|setState (ApplicationState state)
specifier|public
name|void
name|setState
parameter_list|(
name|ApplicationState
name|state
parameter_list|)
block|{
name|this
operator|.
name|appState
operator|=
name|state
expr_stmt|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
DECL|method|getContainers ()
specifier|public
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|Container
argument_list|>
name|getContainers
parameter_list|()
block|{
return|return
name|containers
return|;
block|}
DECL|method|getAppId ()
specifier|public
name|ApplicationId
name|getAppId
parameter_list|()
block|{
return|return
name|appId
return|;
block|}
DECL|method|getApplicationState ()
specifier|public
name|ApplicationState
name|getApplicationState
parameter_list|()
block|{
return|return
name|appState
return|;
block|}
DECL|method|handle (ApplicationEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|ApplicationEvent
name|event
parameter_list|)
block|{}
block|}
end_class

end_unit

