begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers.agent.application.metadata
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|agent
operator|.
name|application
operator|.
name|metadata
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|DockerContainerInputFile
specifier|public
class|class
name|DockerContainerInputFile
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DockerContainerInputFile
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|containerPath
specifier|private
name|String
name|containerPath
decl_stmt|;
DECL|field|fileLocalPath
specifier|private
name|String
name|fileLocalPath
decl_stmt|;
DECL|method|DockerContainerInputFile ()
specifier|public
name|DockerContainerInputFile
parameter_list|()
block|{   }
DECL|method|getContainerMount ()
specifier|public
name|String
name|getContainerMount
parameter_list|()
block|{
return|return
name|containerPath
return|;
block|}
DECL|method|setContainerMount (String containerMount)
specifier|public
name|void
name|setContainerMount
parameter_list|(
name|String
name|containerMount
parameter_list|)
block|{
name|this
operator|.
name|containerPath
operator|=
name|containerMount
expr_stmt|;
block|}
DECL|method|getFileLocalPath ()
specifier|public
name|String
name|getFileLocalPath
parameter_list|()
block|{
return|return
name|fileLocalPath
return|;
block|}
DECL|method|setFileLocalPath (String fileLocalPath)
specifier|public
name|void
name|setFileLocalPath
parameter_list|(
name|String
name|fileLocalPath
parameter_list|)
block|{
name|this
operator|.
name|fileLocalPath
operator|=
name|fileLocalPath
expr_stmt|;
block|}
block|}
end_class

end_unit

