begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.cli
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
operator|.
name|cli
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
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
name|InterfaceAudience
operator|.
name|Private
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
operator|.
name|Unstable
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
name|Configured
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
name|util
operator|.
name|Tool
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
name|client
operator|.
name|api
operator|.
name|YarnClient
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
name|conf
operator|.
name|YarnConfiguration
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|YarnCLI
specifier|public
specifier|abstract
class|class
name|YarnCLI
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|STATUS_CMD
specifier|public
specifier|static
specifier|final
name|String
name|STATUS_CMD
init|=
literal|"status"
decl_stmt|;
DECL|field|LIST_CMD
specifier|public
specifier|static
specifier|final
name|String
name|LIST_CMD
init|=
literal|"list"
decl_stmt|;
DECL|field|KILL_CMD
specifier|public
specifier|static
specifier|final
name|String
name|KILL_CMD
init|=
literal|"kill"
decl_stmt|;
DECL|field|FAIL_CMD
specifier|public
specifier|static
specifier|final
name|String
name|FAIL_CMD
init|=
literal|"fail"
decl_stmt|;
DECL|field|MOVE_TO_QUEUE_CMD
specifier|public
specifier|static
specifier|final
name|String
name|MOVE_TO_QUEUE_CMD
init|=
literal|"movetoqueue"
decl_stmt|;
DECL|field|HELP_CMD
specifier|public
specifier|static
specifier|final
name|String
name|HELP_CMD
init|=
literal|"help"
decl_stmt|;
DECL|field|SIGNAL_CMD
specifier|public
specifier|static
specifier|final
name|String
name|SIGNAL_CMD
init|=
literal|"signal"
decl_stmt|;
DECL|field|sysout
specifier|protected
name|PrintStream
name|sysout
decl_stmt|;
DECL|field|syserr
specifier|protected
name|PrintStream
name|syserr
decl_stmt|;
DECL|field|client
specifier|protected
name|YarnClient
name|client
decl_stmt|;
DECL|method|YarnCLI ()
specifier|public
name|YarnCLI
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createYarnClient ()
specifier|protected
name|YarnClient
name|createYarnClient
parameter_list|()
block|{
return|return
name|YarnClient
operator|.
name|createYarnClient
argument_list|()
return|;
block|}
DECL|method|createAndStartYarnClient ()
specifier|protected
name|void
name|createAndStartYarnClient
parameter_list|()
block|{
name|client
operator|=
name|createYarnClient
argument_list|()
expr_stmt|;
name|client
operator|.
name|init
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|setSysOutPrintStream (PrintStream sysout)
specifier|public
name|void
name|setSysOutPrintStream
parameter_list|(
name|PrintStream
name|sysout
parameter_list|)
block|{
name|this
operator|.
name|sysout
operator|=
name|sysout
expr_stmt|;
block|}
DECL|method|setSysErrPrintStream (PrintStream syserr)
specifier|public
name|void
name|setSysErrPrintStream
parameter_list|(
name|PrintStream
name|syserr
parameter_list|)
block|{
name|this
operator|.
name|syserr
operator|=
name|syserr
expr_stmt|;
block|}
DECL|method|getClient ()
specifier|public
name|YarnClient
name|getClient
parameter_list|()
block|{
return|return
name|client
return|;
block|}
DECL|method|setClient (YarnClient client)
specifier|public
name|void
name|setClient
parameter_list|(
name|YarnClient
name|client
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
comment|// this.client may be null when it is called before
comment|// invoking `createAndStartYarnClient`
if|if
condition|(
name|this
operator|.
name|client
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

