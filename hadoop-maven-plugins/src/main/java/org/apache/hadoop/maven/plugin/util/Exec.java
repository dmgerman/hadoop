begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.maven.plugin.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|maven
operator|.
name|plugin
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
name|maven
operator|.
name|plugin
operator|.
name|Mojo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Exec is a helper class for executing an external process from a mojo.  */
end_comment

begin_class
DECL|class|Exec
specifier|public
class|class
name|Exec
block|{
DECL|field|mojo
specifier|private
name|Mojo
name|mojo
decl_stmt|;
comment|/**    * Creates a new Exec instance for executing an external process from the given    * mojo.    *     * @param mojo Mojo executing external process    */
DECL|method|Exec (Mojo mojo)
specifier|public
name|Exec
parameter_list|(
name|Mojo
name|mojo
parameter_list|)
block|{
name|this
operator|.
name|mojo
operator|=
name|mojo
expr_stmt|;
block|}
comment|/**    * Runs the specified command and saves each line of the command's output to    * the given list.    *    * @param command List containing command and all arguments    * @param output List in/out parameter to receive command output    * @return int exit code of command    */
DECL|method|run (List<String> command, List<String> output)
specifier|public
name|int
name|run
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|command
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|output
parameter_list|)
block|{
return|return
name|this
operator|.
name|run
argument_list|(
name|command
argument_list|,
name|output
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Runs the specified command and saves each line of the command's output to    * the given list and each line of the command's stderr to the other list.    *    * @param command List containing command and all arguments    * @param output List in/out parameter to receive command output    * @param errors List in/out parameter to receive command stderr    * @return int exit code of command    */
DECL|method|run (List<String> command, List<String> output, List<String> errors)
specifier|public
name|int
name|run
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|command
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|output
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|errors
parameter_list|)
block|{
name|int
name|retCode
init|=
literal|1
decl_stmt|;
name|ProcessBuilder
name|pb
init|=
operator|new
name|ProcessBuilder
argument_list|(
name|command
argument_list|)
decl_stmt|;
try|try
block|{
name|Process
name|p
init|=
name|pb
operator|.
name|start
argument_list|()
decl_stmt|;
name|OutputBufferThread
name|stdOut
init|=
operator|new
name|OutputBufferThread
argument_list|(
name|p
operator|.
name|getInputStream
argument_list|()
argument_list|)
decl_stmt|;
name|OutputBufferThread
name|stdErr
init|=
operator|new
name|OutputBufferThread
argument_list|(
name|p
operator|.
name|getErrorStream
argument_list|()
argument_list|)
decl_stmt|;
name|stdOut
operator|.
name|start
argument_list|()
expr_stmt|;
name|stdErr
operator|.
name|start
argument_list|()
expr_stmt|;
name|retCode
operator|=
name|p
operator|.
name|waitFor
argument_list|()
expr_stmt|;
if|if
condition|(
name|retCode
operator|!=
literal|0
condition|)
block|{
name|mojo
operator|.
name|getLog
argument_list|()
operator|.
name|warn
argument_list|(
name|command
operator|+
literal|" failed with error code "
operator|+
name|retCode
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|stdErr
operator|.
name|getOutput
argument_list|()
control|)
block|{
name|mojo
operator|.
name|getLog
argument_list|()
operator|.
name|debug
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|stdOut
operator|.
name|join
argument_list|()
expr_stmt|;
name|stdErr
operator|.
name|join
argument_list|()
expr_stmt|;
name|output
operator|.
name|addAll
argument_list|(
name|stdOut
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|errors
operator|!=
literal|null
condition|)
block|{
name|errors
operator|.
name|addAll
argument_list|(
name|stdErr
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|mojo
operator|.
name|getLog
argument_list|()
operator|.
name|warn
argument_list|(
name|command
operator|+
literal|" failed: "
operator|+
name|ex
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|retCode
return|;
block|}
comment|/**    * OutputBufferThread is a background thread for consuming and storing output    * of the external process.    */
DECL|class|OutputBufferThread
specifier|public
specifier|static
class|class
name|OutputBufferThread
extends|extends
name|Thread
block|{
DECL|field|output
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|output
decl_stmt|;
DECL|field|reader
specifier|private
name|BufferedReader
name|reader
decl_stmt|;
comment|/**      * Creates a new OutputBufferThread to consume the given InputStream.      *       * @param is InputStream to consume      */
DECL|method|OutputBufferThread (InputStream is)
specifier|public
name|OutputBufferThread
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
name|this
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|output
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unsupported encoding "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
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
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|output
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"make failed with error code "
operator|+
name|ex
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns every line consumed from the input.      *       * @return List<String> every line consumed from the input      */
DECL|method|getOutput ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getOutput
parameter_list|()
block|{
return|return
name|output
return|;
block|}
block|}
comment|/**    * Add environment variables to a ProcessBuilder.    */
DECL|method|addEnvironment (ProcessBuilder pb, Map<String, String> env)
specifier|public
specifier|static
name|void
name|addEnvironment
parameter_list|(
name|ProcessBuilder
name|pb
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|)
block|{
if|if
condition|(
name|env
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|processEnv
init|=
name|pb
operator|.
name|environment
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|env
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|val
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|val
operator|=
literal|""
expr_stmt|;
block|}
name|processEnv
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Pretty-print the environment to a StringBuilder.    */
DECL|method|envToString (Map<String, String> env)
specifier|public
specifier|static
name|String
name|envToString
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|)
block|{
name|StringBuilder
name|bld
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|bld
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
if|if
condition|(
name|env
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|env
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|val
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|val
operator|=
literal|""
expr_stmt|;
block|}
name|bld
operator|.
name|append
argument_list|(
literal|"\n  "
argument_list|)
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" = '"
argument_list|)
operator|.
name|append
argument_list|(
name|val
argument_list|)
operator|.
name|append
argument_list|(
literal|"'\n"
argument_list|)
expr_stmt|;
block|}
block|}
name|bld
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|bld
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

