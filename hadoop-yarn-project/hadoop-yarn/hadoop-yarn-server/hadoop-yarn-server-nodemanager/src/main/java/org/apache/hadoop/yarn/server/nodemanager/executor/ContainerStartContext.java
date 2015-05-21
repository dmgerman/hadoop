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
name|containermanager
operator|.
name|container
operator|.
name|Container
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

begin_comment
comment|/**  * Encapsulates information required for starting/launching containers.  */
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
DECL|class|ContainerStartContext
specifier|public
specifier|final
class|class
name|ContainerStartContext
block|{
DECL|field|container
specifier|private
specifier|final
name|Container
name|container
decl_stmt|;
DECL|field|nmPrivateContainerScriptPath
specifier|private
specifier|final
name|Path
name|nmPrivateContainerScriptPath
decl_stmt|;
DECL|field|nmPrivateTokensPath
specifier|private
specifier|final
name|Path
name|nmPrivateTokensPath
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
DECL|field|containerWorkDir
specifier|private
specifier|final
name|Path
name|containerWorkDir
decl_stmt|;
DECL|field|localDirs
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|localDirs
decl_stmt|;
DECL|field|logDirs
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|logDirs
decl_stmt|;
DECL|class|Builder
specifier|public
specifier|static
specifier|final
class|class
name|Builder
block|{
DECL|field|container
specifier|private
name|Container
name|container
decl_stmt|;
DECL|field|nmPrivateContainerScriptPath
specifier|private
name|Path
name|nmPrivateContainerScriptPath
decl_stmt|;
DECL|field|nmPrivateTokensPath
specifier|private
name|Path
name|nmPrivateTokensPath
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
DECL|field|containerWorkDir
specifier|private
name|Path
name|containerWorkDir
decl_stmt|;
DECL|field|localDirs
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|localDirs
decl_stmt|;
DECL|field|logDirs
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|logDirs
decl_stmt|;
DECL|method|Builder ()
specifier|public
name|Builder
parameter_list|()
block|{     }
DECL|method|setContainer (Container container)
specifier|public
name|Builder
name|setContainer
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
name|this
operator|.
name|container
operator|=
name|container
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setNmPrivateContainerScriptPath ( Path nmPrivateContainerScriptPath)
specifier|public
name|Builder
name|setNmPrivateContainerScriptPath
parameter_list|(
name|Path
name|nmPrivateContainerScriptPath
parameter_list|)
block|{
name|this
operator|.
name|nmPrivateContainerScriptPath
operator|=
name|nmPrivateContainerScriptPath
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setNmPrivateTokensPath (Path nmPrivateTokensPath)
specifier|public
name|Builder
name|setNmPrivateTokensPath
parameter_list|(
name|Path
name|nmPrivateTokensPath
parameter_list|)
block|{
name|this
operator|.
name|nmPrivateTokensPath
operator|=
name|nmPrivateTokensPath
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
DECL|method|setContainerWorkDir (Path containerWorkDir)
specifier|public
name|Builder
name|setContainerWorkDir
parameter_list|(
name|Path
name|containerWorkDir
parameter_list|)
block|{
name|this
operator|.
name|containerWorkDir
operator|=
name|containerWorkDir
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setLocalDirs (List<String> localDirs)
specifier|public
name|Builder
name|setLocalDirs
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|localDirs
parameter_list|)
block|{
name|this
operator|.
name|localDirs
operator|=
name|localDirs
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setLogDirs (List<String> logDirs)
specifier|public
name|Builder
name|setLogDirs
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|logDirs
parameter_list|)
block|{
name|this
operator|.
name|logDirs
operator|=
name|logDirs
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|ContainerStartContext
name|build
parameter_list|()
block|{
return|return
operator|new
name|ContainerStartContext
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
DECL|method|ContainerStartContext (Builder builder)
specifier|private
name|ContainerStartContext
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|container
operator|=
name|builder
operator|.
name|container
expr_stmt|;
name|this
operator|.
name|nmPrivateContainerScriptPath
operator|=
name|builder
operator|.
name|nmPrivateContainerScriptPath
expr_stmt|;
name|this
operator|.
name|nmPrivateTokensPath
operator|=
name|builder
operator|.
name|nmPrivateTokensPath
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
name|containerWorkDir
operator|=
name|builder
operator|.
name|containerWorkDir
expr_stmt|;
name|this
operator|.
name|localDirs
operator|=
name|builder
operator|.
name|localDirs
expr_stmt|;
name|this
operator|.
name|logDirs
operator|=
name|builder
operator|.
name|logDirs
expr_stmt|;
block|}
DECL|method|getContainer ()
specifier|public
name|Container
name|getContainer
parameter_list|()
block|{
return|return
name|this
operator|.
name|container
return|;
block|}
DECL|method|getNmPrivateContainerScriptPath ()
specifier|public
name|Path
name|getNmPrivateContainerScriptPath
parameter_list|()
block|{
return|return
name|this
operator|.
name|nmPrivateContainerScriptPath
return|;
block|}
DECL|method|getNmPrivateTokensPath ()
specifier|public
name|Path
name|getNmPrivateTokensPath
parameter_list|()
block|{
return|return
name|this
operator|.
name|nmPrivateTokensPath
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
DECL|method|getContainerWorkDir ()
specifier|public
name|Path
name|getContainerWorkDir
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerWorkDir
return|;
block|}
DECL|method|getLocalDirs ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getLocalDirs
parameter_list|()
block|{
return|return
name|this
operator|.
name|localDirs
return|;
block|}
DECL|method|getLogDirs ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getLogDirs
parameter_list|()
block|{
return|return
name|this
operator|.
name|logDirs
return|;
block|}
block|}
end_class

end_unit

