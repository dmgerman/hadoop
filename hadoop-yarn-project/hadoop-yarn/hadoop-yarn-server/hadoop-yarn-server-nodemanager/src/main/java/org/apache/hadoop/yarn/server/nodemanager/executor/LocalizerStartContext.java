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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|LocalDirsHandlerService
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

begin_comment
comment|/**  * Encapsulates information required for starting a localizer.  */
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
DECL|class|LocalizerStartContext
specifier|public
specifier|final
class|class
name|LocalizerStartContext
block|{
DECL|field|nmPrivateContainerTokens
specifier|private
specifier|final
name|Path
name|nmPrivateContainerTokens
decl_stmt|;
DECL|field|nmAddr
specifier|private
specifier|final
name|InetSocketAddress
name|nmAddr
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|appId
specifier|private
specifier|final
name|String
name|appId
decl_stmt|;
DECL|field|locId
specifier|private
specifier|final
name|String
name|locId
decl_stmt|;
DECL|field|dirsHandler
specifier|private
specifier|final
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
DECL|class|Builder
specifier|public
specifier|static
specifier|final
class|class
name|Builder
block|{
DECL|field|nmPrivateContainerTokens
specifier|private
name|Path
name|nmPrivateContainerTokens
decl_stmt|;
DECL|field|nmAddr
specifier|private
name|InetSocketAddress
name|nmAddr
decl_stmt|;
DECL|field|user
specifier|private
name|String
name|user
decl_stmt|;
DECL|field|appId
specifier|private
name|String
name|appId
decl_stmt|;
DECL|field|locId
specifier|private
name|String
name|locId
decl_stmt|;
DECL|field|dirsHandler
specifier|private
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
DECL|method|Builder ()
specifier|public
name|Builder
parameter_list|()
block|{     }
DECL|method|setNmPrivateContainerTokens (Path nmPrivateContainerTokens)
specifier|public
name|Builder
name|setNmPrivateContainerTokens
parameter_list|(
name|Path
name|nmPrivateContainerTokens
parameter_list|)
block|{
name|this
operator|.
name|nmPrivateContainerTokens
operator|=
name|nmPrivateContainerTokens
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setNmAddr (InetSocketAddress nmAddr)
specifier|public
name|Builder
name|setNmAddr
parameter_list|(
name|InetSocketAddress
name|nmAddr
parameter_list|)
block|{
name|this
operator|.
name|nmAddr
operator|=
name|nmAddr
expr_stmt|;
return|return
name|this
return|;
block|}
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
DECL|method|setAppId (String appId)
specifier|public
name|Builder
name|setAppId
parameter_list|(
name|String
name|appId
parameter_list|)
block|{
name|this
operator|.
name|appId
operator|=
name|appId
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setLocId (String locId)
specifier|public
name|Builder
name|setLocId
parameter_list|(
name|String
name|locId
parameter_list|)
block|{
name|this
operator|.
name|locId
operator|=
name|locId
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setDirsHandler (LocalDirsHandlerService dirsHandler)
specifier|public
name|Builder
name|setDirsHandler
parameter_list|(
name|LocalDirsHandlerService
name|dirsHandler
parameter_list|)
block|{
name|this
operator|.
name|dirsHandler
operator|=
name|dirsHandler
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|LocalizerStartContext
name|build
parameter_list|()
block|{
return|return
operator|new
name|LocalizerStartContext
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
DECL|method|LocalizerStartContext (Builder builder)
specifier|private
name|LocalizerStartContext
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|nmPrivateContainerTokens
operator|=
name|builder
operator|.
name|nmPrivateContainerTokens
expr_stmt|;
name|this
operator|.
name|nmAddr
operator|=
name|builder
operator|.
name|nmAddr
expr_stmt|;
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
name|appId
operator|=
name|builder
operator|.
name|appId
expr_stmt|;
name|this
operator|.
name|locId
operator|=
name|builder
operator|.
name|locId
expr_stmt|;
name|this
operator|.
name|dirsHandler
operator|=
name|builder
operator|.
name|dirsHandler
expr_stmt|;
block|}
DECL|method|getNmPrivateContainerTokens ()
specifier|public
name|Path
name|getNmPrivateContainerTokens
parameter_list|()
block|{
return|return
name|this
operator|.
name|nmPrivateContainerTokens
return|;
block|}
DECL|method|getNmAddr ()
specifier|public
name|InetSocketAddress
name|getNmAddr
parameter_list|()
block|{
return|return
name|this
operator|.
name|nmAddr
return|;
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
DECL|method|getAppId ()
specifier|public
name|String
name|getAppId
parameter_list|()
block|{
return|return
name|this
operator|.
name|appId
return|;
block|}
DECL|method|getLocId ()
specifier|public
name|String
name|getLocId
parameter_list|()
block|{
return|return
name|this
operator|.
name|locId
return|;
block|}
DECL|method|getDirsHandler ()
specifier|public
name|LocalDirsHandlerService
name|getDirsHandler
parameter_list|()
block|{
return|return
name|this
operator|.
name|dirsHandler
return|;
block|}
block|}
end_class

end_unit

