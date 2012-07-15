begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

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

begin_comment
comment|/**  * Facilitates hooking process termination for tests and debugging.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|ExitUtil
specifier|public
specifier|final
class|class
name|ExitUtil
block|{
DECL|field|LOG
specifier|private
specifier|final
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ExitUtil
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|systemExitDisabled
specifier|private
specifier|static
specifier|volatile
name|boolean
name|systemExitDisabled
init|=
literal|false
decl_stmt|;
DECL|field|firstExitException
specifier|private
specifier|static
specifier|volatile
name|ExitException
name|firstExitException
decl_stmt|;
DECL|class|ExitException
specifier|public
specifier|static
class|class
name|ExitException
extends|extends
name|RuntimeException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|status
specifier|public
specifier|final
name|int
name|status
decl_stmt|;
DECL|method|ExitException (int status, String msg)
specifier|public
name|ExitException
parameter_list|(
name|int
name|status
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
block|}
block|}
comment|/**    * Disable the use of System.exit for testing.    */
DECL|method|disableSystemExit ()
specifier|public
specifier|static
name|void
name|disableSystemExit
parameter_list|()
block|{
name|systemExitDisabled
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * @return true if terminate has been called    */
DECL|method|terminateCalled ()
specifier|public
specifier|static
name|boolean
name|terminateCalled
parameter_list|()
block|{
comment|// Either we set this member or we actually called System#exit
return|return
name|firstExitException
operator|!=
literal|null
return|;
block|}
comment|/**    * @return the first ExitException thrown, null if none thrown yet    */
DECL|method|getFirstExitException ()
specifier|public
specifier|static
name|ExitException
name|getFirstExitException
parameter_list|()
block|{
return|return
name|firstExitException
return|;
block|}
comment|/**    * Terminate the current process. Note that terminate is the *only* method    * that should be used to terminate the daemon processes.    * @param status exit code    * @param msg message used to create the ExitException    * @throws ExitException if System.exit is disabled for test purposes    */
DECL|method|terminate (int status, String msg)
specifier|public
specifier|static
name|void
name|terminate
parameter_list|(
name|int
name|status
parameter_list|,
name|String
name|msg
parameter_list|)
throws|throws
name|ExitException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Exiting with status "
operator|+
name|status
argument_list|)
expr_stmt|;
if|if
condition|(
name|systemExitDisabled
condition|)
block|{
name|ExitException
name|ee
init|=
operator|new
name|ExitException
argument_list|(
name|status
argument_list|,
name|msg
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Terminate called"
argument_list|,
name|ee
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|firstExitException
condition|)
block|{
name|firstExitException
operator|=
name|ee
expr_stmt|;
block|}
throw|throw
name|ee
throw|;
block|}
name|System
operator|.
name|exit
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
comment|/**    * Like {@link terminate(int, String)} without a message.    * @param status    * @throws ExitException    */
DECL|method|terminate (int status)
specifier|public
specifier|static
name|void
name|terminate
parameter_list|(
name|int
name|status
parameter_list|)
throws|throws
name|ExitException
block|{
name|terminate
argument_list|(
name|status
argument_list|,
literal|"ExitException"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

