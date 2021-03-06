begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.service.launcher.testservices
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|service
operator|.
name|launcher
operator|.
name|testservices
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
name|service
operator|.
name|AbstractService
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
DECL|class|RunningService
specifier|public
class|class
name|RunningService
extends|extends
name|AbstractService
implements|implements
name|Runnable
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
name|RunningService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"org.apache.hadoop.service.launcher.testservices.RunningService"
decl_stmt|;
DECL|field|DELAY
specifier|public
specifier|static
specifier|final
name|int
name|DELAY
init|=
literal|100
decl_stmt|;
comment|/**    * Property on delay times.    */
DECL|field|DELAY_TIME
specifier|public
specifier|static
specifier|final
name|String
name|DELAY_TIME
init|=
literal|"delay.time"
decl_stmt|;
DECL|field|FAIL_IN_RUN
specifier|public
specifier|static
specifier|final
name|String
name|FAIL_IN_RUN
init|=
literal|"fail.runnable"
decl_stmt|;
DECL|field|FAILURE_MESSAGE
specifier|public
specifier|static
specifier|final
name|String
name|FAILURE_MESSAGE
init|=
literal|"FAIL_IN_RUN"
decl_stmt|;
DECL|field|interrupted
specifier|private
name|boolean
name|interrupted
decl_stmt|;
DECL|field|delayTime
specifier|public
name|int
name|delayTime
init|=
name|DELAY
decl_stmt|;
DECL|field|failInRun
specifier|public
name|boolean
name|failInRun
decl_stmt|;
DECL|method|RunningService ()
specifier|public
name|RunningService
parameter_list|()
block|{
name|super
argument_list|(
literal|"RunningService"
argument_list|)
expr_stmt|;
block|}
DECL|method|RunningService (String name)
specifier|public
name|RunningService
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|delayTime
operator|=
name|getConfig
argument_list|()
operator|.
name|getInt
argument_list|(
name|DELAY_TIME
argument_list|,
name|delayTime
argument_list|)
expr_stmt|;
name|failInRun
operator|=
name|getConfig
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|FAIL_IN_RUN
argument_list|,
name|failInRun
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setName
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|delayTime
argument_list|)
expr_stmt|;
if|if
condition|(
name|failInRun
condition|)
block|{
name|noteFailure
argument_list|(
operator|new
name|Exception
argument_list|(
name|FAILURE_MESSAGE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|interrupted
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Interrupted"
argument_list|)
expr_stmt|;
block|}
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|isInterrupted ()
specifier|public
name|boolean
name|isInterrupted
parameter_list|()
block|{
return|return
name|interrupted
return|;
block|}
block|}
end_class

end_unit

