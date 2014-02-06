begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.applications.distributedshell
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|applications
operator|.
name|distributedshell
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
import|;
end_import

begin_class
DECL|class|ContainerLaunchFailAppMaster
specifier|public
class|class
name|ContainerLaunchFailAppMaster
extends|extends
name|ApplicationMaster
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ContainerLaunchFailAppMaster
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|ContainerLaunchFailAppMaster ()
specifier|public
name|ContainerLaunchFailAppMaster
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createNMCallbackHandler ()
name|NMCallbackHandler
name|createNMCallbackHandler
parameter_list|()
block|{
return|return
operator|new
name|FailContainerLaunchNMCallbackHandler
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|class|FailContainerLaunchNMCallbackHandler
class|class
name|FailContainerLaunchNMCallbackHandler
extends|extends
name|ApplicationMaster
operator|.
name|NMCallbackHandler
block|{
DECL|method|FailContainerLaunchNMCallbackHandler ( ApplicationMaster applicationMaster)
specifier|public
name|FailContainerLaunchNMCallbackHandler
parameter_list|(
name|ApplicationMaster
name|applicationMaster
parameter_list|)
block|{
name|super
argument_list|(
name|applicationMaster
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onContainerStarted (ContainerId containerId, Map<String, ByteBuffer> allServiceResponse)
specifier|public
name|void
name|onContainerStarted
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|allServiceResponse
parameter_list|)
block|{
name|super
operator|.
name|onStartContainerError
argument_list|(
name|containerId
argument_list|,
operator|new
name|RuntimeException
argument_list|(
literal|"Inject Container Launch failure"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
try|try
block|{
name|ContainerLaunchFailAppMaster
name|appMaster
init|=
operator|new
name|ContainerLaunchFailAppMaster
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing ApplicationMaster"
argument_list|)
expr_stmt|;
name|boolean
name|doRun
init|=
name|appMaster
operator|.
name|init
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|doRun
condition|)
block|{
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|appMaster
operator|.
name|run
argument_list|()
expr_stmt|;
name|result
operator|=
name|appMaster
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Error running ApplicationMaster"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Application Master completed successfully. exiting"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Application Master failed. exiting"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

