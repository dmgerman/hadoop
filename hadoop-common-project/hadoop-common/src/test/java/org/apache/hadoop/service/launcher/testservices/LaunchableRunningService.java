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
name|launcher
operator|.
name|LaunchableService
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
name|launcher
operator|.
name|LauncherExitCodes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
comment|/**  * A service which implements {@link LaunchableService}.  * It  *<ol>  *<li>does nothing in its {@link #serviceStart()}</li>  *<li>does its sleep+ maybe fail operation in its {@link #execute()}  *   method</li>  *<li>gets the failing flag from the argument {@link #ARG_FAILING} first,  *   the config file second.</li>  *<li>returns 0 for a successful execute</li>  *<li>returns a configurable exit code for a failing execute</li>  *<li>generates a new configuration in {@link #bindArgs(Configuration, List)}  *   to verify that these propagate.</li>  *</ol>  */
end_comment

begin_class
DECL|class|LaunchableRunningService
specifier|public
class|class
name|LaunchableRunningService
extends|extends
name|RunningService
implements|implements
name|LaunchableService
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"org.apache.hadoop.service.launcher.testservices.LaunchableRunningService"
decl_stmt|;
DECL|field|ARG_FAILING
specifier|public
specifier|static
specifier|final
name|String
name|ARG_FAILING
init|=
literal|"--failing"
decl_stmt|;
DECL|field|EXIT_CODE_PROP
specifier|public
specifier|static
specifier|final
name|String
name|EXIT_CODE_PROP
init|=
literal|"exit.code"
decl_stmt|;
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
name|LaunchableRunningService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|exitCode
specifier|private
name|int
name|exitCode
init|=
literal|0
decl_stmt|;
DECL|method|LaunchableRunningService ()
specifier|public
name|LaunchableRunningService
parameter_list|()
block|{
name|this
argument_list|(
literal|"LaunchableRunningService"
argument_list|)
expr_stmt|;
block|}
DECL|method|LaunchableRunningService (String name)
specifier|public
name|LaunchableRunningService
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
DECL|method|bindArgs (Configuration config, List<String> args)
specifier|public
name|Configuration
name|bindArgs
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|STATE
operator|.
name|NOTINITED
argument_list|,
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|arg
range|:
name|args
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
name|Configuration
name|newConf
init|=
operator|new
name|Configuration
argument_list|(
name|config
argument_list|)
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|contains
argument_list|(
name|ARG_FAILING
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"CLI contains "
operator|+
name|ARG_FAILING
argument_list|)
expr_stmt|;
name|failInRun
operator|=
literal|true
expr_stmt|;
name|newConf
operator|.
name|setInt
argument_list|(
name|EXIT_CODE_PROP
argument_list|,
name|LauncherExitCodes
operator|.
name|EXIT_OTHER_FAILURE
argument_list|)
expr_stmt|;
block|}
return|return
name|newConf
return|;
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
if|if
condition|(
name|conf
operator|.
name|getBoolean
argument_list|(
name|FAIL_IN_RUN
argument_list|,
literal|false
argument_list|)
condition|)
block|{
comment|//if the conf value says fail, the exit code goes to it too
name|exitCode
operator|=
name|LauncherExitCodes
operator|.
name|EXIT_FAIL
expr_stmt|;
block|}
comment|// the exit code can be read off the property
name|exitCode
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|EXIT_CODE_PROP
argument_list|,
name|exitCode
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
comment|// no-op
block|}
annotation|@
name|Override
DECL|method|execute ()
specifier|public
name|int
name|execute
parameter_list|()
throws|throws
name|Exception
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
return|return
name|exitCode
return|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|getExitCode ()
specifier|public
name|int
name|getExitCode
parameter_list|()
block|{
return|return
name|exitCode
return|;
block|}
DECL|method|setExitCode (int exitCode)
specifier|public
name|void
name|setExitCode
parameter_list|(
name|int
name|exitCode
parameter_list|)
block|{
name|this
operator|.
name|exitCode
operator|=
name|exitCode
expr_stmt|;
block|}
block|}
end_class

end_unit

