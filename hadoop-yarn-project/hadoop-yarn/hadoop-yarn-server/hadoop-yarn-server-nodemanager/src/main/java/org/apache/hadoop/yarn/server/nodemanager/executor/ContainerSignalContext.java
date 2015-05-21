begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.executor
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
name|executor
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|ContainerExecutor
operator|.
name|Signal
import|;
end_import

begin_comment
comment|/**  * Encapsulates information required for container signaling.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|ContainerSignalContext
specifier|public
specifier|final
class|class
name|ContainerSignalContext
block|{
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|pid
specifier|private
specifier|final
name|String
name|pid
decl_stmt|;
DECL|field|signal
specifier|private
specifier|final
name|Signal
name|signal
decl_stmt|;
DECL|class|Builder
specifier|public
specifier|static
specifier|final
class|class
name|Builder
block|{
DECL|field|user
specifier|private
name|String
name|user
decl_stmt|;
DECL|field|pid
specifier|private
name|String
name|pid
decl_stmt|;
DECL|field|signal
specifier|private
name|Signal
name|signal
decl_stmt|;
DECL|method|Builder ()
specifier|public
name|Builder
parameter_list|()
block|{     }
DECL|method|setUser (String user)
specifier|public
name|Builder
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setPid (String pid)
specifier|public
name|Builder
name|setPid
parameter_list|(
name|String
name|pid
parameter_list|)
block|{
name|this
operator|.
name|pid
operator|=
name|pid
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSignal (Signal signal)
specifier|public
name|Builder
name|setSignal
parameter_list|(
name|Signal
name|signal
parameter_list|)
block|{
name|this
operator|.
name|signal
operator|=
name|signal
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|ContainerSignalContext
name|build
parameter_list|()
block|{
return|return
operator|new
name|ContainerSignalContext
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
DECL|method|ContainerSignalContext (Builder builder)
specifier|private
name|ContainerSignalContext
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|builder
operator|.
name|user
expr_stmt|;
name|this
operator|.
name|pid
operator|=
name|builder
operator|.
name|pid
expr_stmt|;
name|this
operator|.
name|signal
operator|=
name|builder
operator|.
name|signal
expr_stmt|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|this
operator|.
name|user
return|;
block|}
DECL|method|getPid ()
specifier|public
name|String
name|getPid
parameter_list|()
block|{
return|return
name|this
operator|.
name|pid
return|;
block|}
DECL|method|getSignal ()
specifier|public
name|Signal
name|getSignal
parameter_list|()
block|{
return|return
name|this
operator|.
name|signal
return|;
block|}
block|}
end_class

end_unit

