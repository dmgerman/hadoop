begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.cli
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|cli
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
name|cli
operator|.
name|CommandLine
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
name|scm
operator|.
name|client
operator|.
name|ScmClient
import|;
end_import

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
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_comment
comment|/**  * The abstract class of all SCM CLI commands.  */
end_comment

begin_class
DECL|class|OzoneCommandHandler
specifier|public
specifier|abstract
class|class
name|OzoneCommandHandler
block|{
DECL|field|scmClient
specifier|private
name|ScmClient
name|scmClient
decl_stmt|;
DECL|field|out
specifier|protected
name|PrintStream
name|out
init|=
name|System
operator|.
name|out
decl_stmt|;
DECL|field|err
specifier|protected
name|PrintStream
name|err
init|=
name|System
operator|.
name|err
decl_stmt|;
comment|/**    * Constructs a handler object.    */
DECL|method|OzoneCommandHandler (ScmClient scmClient)
specifier|public
name|OzoneCommandHandler
parameter_list|(
name|ScmClient
name|scmClient
parameter_list|)
block|{
name|this
operator|.
name|scmClient
operator|=
name|scmClient
expr_stmt|;
block|}
DECL|method|getScmClient ()
specifier|protected
name|ScmClient
name|getScmClient
parameter_list|()
block|{
return|return
name|scmClient
return|;
block|}
comment|/**    * Sets customized output stream to redirect the stdout to somewhere else.    * @param out    */
DECL|method|setOut (PrintStream out)
specifier|public
name|void
name|setOut
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
comment|/**    * Sets customized error stream to redirect the stderr to somewhere else.    * @param err    */
DECL|method|setErr (PrintStream err)
specifier|public
name|void
name|setErr
parameter_list|(
name|PrintStream
name|err
parameter_list|)
block|{
name|this
operator|.
name|err
operator|=
name|err
expr_stmt|;
block|}
DECL|method|logOut (String msg, String ... variable)
specifier|public
name|void
name|logOut
parameter_list|(
name|String
name|msg
parameter_list|,
name|String
modifier|...
name|variable
parameter_list|)
block|{
name|this
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|msg
argument_list|,
name|variable
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Executes the Client command.    *    * @param cmd - CommandLine.    * @throws IOException throws exception.    */
DECL|method|execute (CommandLine cmd)
specifier|public
specifier|abstract
name|void
name|execute
parameter_list|(
name|CommandLine
name|cmd
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Display a help message describing the options the command takes.    * TODO : currently only prints to standard out, may want to change this.    */
DECL|method|displayHelp ()
specifier|public
specifier|abstract
name|void
name|displayHelp
parameter_list|()
function_decl|;
block|}
end_class

end_unit

