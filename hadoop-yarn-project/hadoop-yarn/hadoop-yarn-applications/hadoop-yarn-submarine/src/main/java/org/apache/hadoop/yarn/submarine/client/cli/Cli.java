begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.client.cli
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|submarine
operator|.
name|client
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
name|yarn
operator|.
name|submarine
operator|.
name|common
operator|.
name|ClientContext
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
name|submarine
operator|.
name|common
operator|.
name|fs
operator|.
name|DefaultRemoteDirectoryManager
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
name|submarine
operator|.
name|runtimes
operator|.
name|RuntimeFactory
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
name|Arrays
import|;
end_import

begin_class
DECL|class|Cli
specifier|public
class|class
name|Cli
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
name|Cli
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|printHelp ()
specifier|private
specifier|static
name|void
name|printHelp
parameter_list|()
block|{
name|StringBuilder
name|helpMsg
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|helpMsg
operator|.
name|append
argument_list|(
literal|"\n\nUsage:<object> [<action>] [<args>]\n"
argument_list|)
expr_stmt|;
name|helpMsg
operator|.
name|append
argument_list|(
literal|"  Below are all objects / actions:\n"
argument_list|)
expr_stmt|;
name|helpMsg
operator|.
name|append
argument_list|(
literal|"    job \n"
argument_list|)
expr_stmt|;
name|helpMsg
operator|.
name|append
argument_list|(
literal|"       run : run a job, please see 'job run --help' for usage \n"
argument_list|)
expr_stmt|;
name|helpMsg
operator|.
name|append
argument_list|(
literal|"       show : get status of job, please see 'job show --help' for usage \n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|helpMsg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getClientContext ()
specifier|private
specifier|static
name|ClientContext
name|getClientContext
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|ClientContext
name|clientContext
init|=
operator|new
name|ClientContext
argument_list|()
decl_stmt|;
name|clientContext
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|clientContext
operator|.
name|setRemoteDirectoryManager
argument_list|(
operator|new
name|DefaultRemoteDirectoryManager
argument_list|(
name|clientContext
argument_list|)
argument_list|)
expr_stmt|;
name|RuntimeFactory
name|runtimeFactory
init|=
name|RuntimeFactory
operator|.
name|getRuntimeFactory
argument_list|(
name|clientContext
argument_list|)
decl_stmt|;
name|clientContext
operator|.
name|setRuntimeFactory
argument_list|(
name|runtimeFactory
argument_list|)
expr_stmt|;
return|return
name|clientContext
return|;
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
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"              _                              _              \n"
operator|+
literal|"             | |                            (_)             \n"
operator|+
literal|"  ___  _   _ | |__   _ __ ___    __ _  _ __  _  _ __    ___ \n"
operator|+
literal|" / __|| | | || '_ \\ | '_ ` _ \\  / _` || '__|| || '_ \\  / _ \\\n"
operator|+
literal|" \\__ \\| |_| || |_) || | | | | || (_| || |   | || | | ||  __/\n"
operator|+
literal|" |___/ \\__,_||_.__/ |_| |_| |_| \\__,_||_|   |_||_| |_| \\___|\n"
operator|+
literal|"                                                    \n"
operator|+
literal|"                             ?\n"
operator|+
literal|" ~~~~~~~~~~~~~~~~~~~~~~~~~~~|^\"~~~~~~~~~~~~~~~~~~~~~~~~~o~~~~~~~~~~~\n"
operator|+
literal|"        o                   |                  o      __o\n"
operator|+
literal|"         o                  |                 o     |X__>\n"
operator|+
literal|"       ___o                 |                __o\n"
operator|+
literal|"     (X___>--             __|__            |X__>     o\n"
operator|+
literal|"                         |     \\                   __o\n"
operator|+
literal|"                         |      \\                |X__>\n"
operator|+
literal|"  _______________________|_______\\________________\n"
operator|+
literal|"<                                                \\____________   _\n"
operator|+
literal|"  \\                                                            \\ (_)\n"
operator|+
literal|"   \\    O       O       O>=)\n"
operator|+
literal|"    \\__________________________________________________________/ (_)\n"
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|CliUtils
operator|.
name|argsForHelp
argument_list|(
name|args
argument_list|)
condition|)
block|{
name|printHelp
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|2
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Bad parameters specified."
argument_list|)
expr_stmt|;
name|printHelp
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|moduleArgs
init|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|args
argument_list|,
literal|2
argument_list|,
name|args
operator|.
name|length
argument_list|)
decl_stmt|;
name|ClientContext
name|clientContext
init|=
name|getClientContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"job"
argument_list|)
condition|)
block|{
name|String
name|subCmd
init|=
name|args
index|[
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|subCmd
operator|.
name|equals
argument_list|(
name|CliConstants
operator|.
name|RUN
argument_list|)
condition|)
block|{
operator|new
name|RunJobCli
argument_list|(
name|clientContext
argument_list|)
operator|.
name|run
argument_list|(
name|moduleArgs
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|subCmd
operator|.
name|equals
argument_list|(
name|CliConstants
operator|.
name|SHOW
argument_list|)
condition|)
block|{
operator|new
name|ShowJobCli
argument_list|(
name|clientContext
argument_list|)
operator|.
name|run
argument_list|(
name|moduleArgs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|printHelp
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown option for job"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|printHelp
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bad parameters<TODO>"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

