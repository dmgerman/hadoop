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
name|io
operator|.
name|IOException
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
name|exceptions
operator|.
name|YarnException
import|;
end_import

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
DECL|class|TestDSFailedAppMaster
specifier|public
class|class
name|TestDSFailedAppMaster
extends|extends
name|ApplicationMaster
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestDSFailedAppMaster
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|super
operator|.
name|run
argument_list|()
expr_stmt|;
comment|// for the 2nd attempt.
if|if
condition|(
name|appAttemptID
operator|.
name|getAttemptId
argument_list|()
operator|==
literal|2
condition|)
block|{
comment|// should reuse the earlier running container, so numAllocatedContainers
comment|// should be set to 1. And should ask no more containers, so
comment|// numRequestedContainers should be the same as numTotalContainers.
comment|// The only container is the container requested by the AM in the first
comment|// attempt.
if|if
condition|(
name|numAllocatedContainers
operator|.
name|get
argument_list|()
operator|!=
literal|1
operator|||
name|numRequestedContainers
operator|.
name|get
argument_list|()
operator|!=
name|numTotalContainers
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"NumAllocatedContainers is "
operator|+
name|numAllocatedContainers
operator|.
name|get
argument_list|()
operator|+
literal|" and NumRequestedContainers is "
operator|+
name|numAllocatedContainers
operator|.
name|get
argument_list|()
operator|+
literal|".Application Master failed. exiting"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
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
name|TestDSFailedAppMaster
name|appMaster
init|=
operator|new
name|TestDSFailedAppMaster
argument_list|()
decl_stmt|;
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
if|if
condition|(
name|appMaster
operator|.
name|appAttemptID
operator|.
name|getAttemptId
argument_list|()
operator|==
literal|1
condition|)
block|{
try|try
block|{
comment|// sleep some time, wait for the AM to launch a container.
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
comment|// fail the first am.
name|System
operator|.
name|exit
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
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

