begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
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

begin_class
DECL|class|FilterService
specifier|public
class|class
name|FilterService
implements|implements
name|Service
block|{
DECL|field|service
specifier|private
specifier|final
name|Service
name|service
decl_stmt|;
DECL|field|startTime
specifier|private
specifier|final
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|method|FilterService (Service service)
specifier|public
name|FilterService
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
name|this
operator|.
name|service
operator|=
name|service
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration config)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|config
parameter_list|)
block|{
name|service
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|service
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|registerServiceListener (ServiceStateChangeListener listener)
specifier|public
name|void
name|registerServiceListener
parameter_list|(
name|ServiceStateChangeListener
name|listener
parameter_list|)
block|{
name|service
operator|.
name|registerServiceListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|unregisterServiceListener (ServiceStateChangeListener listener)
specifier|public
name|void
name|unregisterServiceListener
parameter_list|(
name|ServiceStateChangeListener
name|listener
parameter_list|)
block|{
name|service
operator|.
name|unregisterServiceListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
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
name|service
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getConfig ()
specifier|public
name|Configuration
name|getConfig
parameter_list|()
block|{
return|return
name|service
operator|.
name|getConfig
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getServiceState ()
specifier|public
name|STATE
name|getServiceState
parameter_list|()
block|{
return|return
name|service
operator|.
name|getServiceState
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
annotation|@
name|Override
DECL|method|isInState (STATE state)
specifier|public
name|boolean
name|isInState
parameter_list|(
name|STATE
name|state
parameter_list|)
block|{
return|return
name|service
operator|.
name|isInState
argument_list|(
name|state
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFailureCause ()
specifier|public
name|Throwable
name|getFailureCause
parameter_list|()
block|{
return|return
name|service
operator|.
name|getFailureCause
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFailureState ()
specifier|public
name|STATE
name|getFailureState
parameter_list|()
block|{
return|return
name|service
operator|.
name|getFailureState
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|waitForServiceToStop (long timeout)
specifier|public
name|boolean
name|waitForServiceToStop
parameter_list|(
name|long
name|timeout
parameter_list|)
block|{
return|return
name|service
operator|.
name|waitForServiceToStop
argument_list|(
name|timeout
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getLifecycleHistory ()
specifier|public
name|List
argument_list|<
name|LifecycleEvent
argument_list|>
name|getLifecycleHistory
parameter_list|()
block|{
return|return
name|service
operator|.
name|getLifecycleHistory
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getBlockers ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getBlockers
parameter_list|()
block|{
return|return
name|service
operator|.
name|getBlockers
argument_list|()
return|;
block|}
block|}
end_class

end_unit

