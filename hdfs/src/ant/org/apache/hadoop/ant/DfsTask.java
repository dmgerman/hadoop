begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ant
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ant
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|fs
operator|.
name|FsShell
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|AntClassLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|BuildException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Task
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
name|ToolRunner
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
name|hdfs
operator|.
name|HdfsConfiguration
import|;
end_import

begin_comment
comment|/**  * {@link org.apache.hadoop.fs.FsShell FsShell} wrapper for ant Task.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DfsTask
specifier|public
class|class
name|DfsTask
extends|extends
name|Task
block|{
comment|/**    * Default sink for {@link java.lang.System.out System.out}    * and {@link java.lang.System.err System.err}.    */
DECL|field|nullOut
specifier|private
specifier|static
specifier|final
name|OutputStream
name|nullOut
init|=
operator|new
name|OutputStream
argument_list|()
block|{
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
block|{
comment|/* ignore */
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
block|}
decl_stmt|;
DECL|field|shell
specifier|private
specifier|static
specifier|final
name|FsShell
name|shell
init|=
operator|new
name|FsShell
argument_list|()
decl_stmt|;
DECL|field|confloader
specifier|protected
name|AntClassLoader
name|confloader
decl_stmt|;
DECL|field|out
specifier|protected
name|OutputStream
name|out
init|=
name|nullOut
decl_stmt|;
DECL|field|err
specifier|protected
name|OutputStream
name|err
init|=
name|nullOut
decl_stmt|;
comment|// set by ant
DECL|field|cmd
specifier|protected
name|String
name|cmd
decl_stmt|;
DECL|field|argv
specifier|protected
specifier|final
name|LinkedList
argument_list|<
name|String
argument_list|>
name|argv
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|outprop
specifier|protected
name|String
name|outprop
decl_stmt|;
DECL|field|errprop
specifier|protected
name|String
name|errprop
decl_stmt|;
DECL|field|failonerror
specifier|protected
name|boolean
name|failonerror
init|=
literal|true
decl_stmt|;
comment|// saved ant context
DECL|field|antOut
specifier|private
name|PrintStream
name|antOut
decl_stmt|;
DECL|field|antErr
specifier|private
name|PrintStream
name|antErr
decl_stmt|;
comment|/**    * Sets the command to run in {@link org.apache.hadoop.fs.FsShell FsShell}.    * @param cmd A valid command to FsShell, sans&quot;-&quot;.    */
DECL|method|setCmd (String cmd)
specifier|public
name|void
name|setCmd
parameter_list|(
name|String
name|cmd
parameter_list|)
block|{
name|this
operator|.
name|cmd
operator|=
literal|"-"
operator|+
name|cmd
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
comment|/**    * Sets the argument list from a String of comma-separated values.    * @param args A String of comma-separated arguments to FsShell.    */
DECL|method|setArgs (String args)
specifier|public
name|void
name|setArgs
parameter_list|(
name|String
name|args
parameter_list|)
block|{
for|for
control|(
name|String
name|s
range|:
name|args
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\s*,\\s*"
argument_list|)
control|)
name|argv
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the property into which System.out will be written.    * @param outprop The name of the property into which System.out is written.    * If the property is defined before this task is executed, it will not be updated.    */
DECL|method|setOut (String outprop)
specifier|public
name|void
name|setOut
parameter_list|(
name|String
name|outprop
parameter_list|)
block|{
name|this
operator|.
name|outprop
operator|=
name|outprop
expr_stmt|;
name|out
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
if|if
condition|(
name|outprop
operator|.
name|equals
argument_list|(
name|errprop
argument_list|)
condition|)
name|err
operator|=
name|out
expr_stmt|;
block|}
comment|/**    * Sets the property into which System.err will be written. If this property    * has the same name as the property for System.out, the two will be interlaced.    * @param errprop The name of the property into which System.err is written.    * If the property is defined before this task is executed, it will not be updated.    */
DECL|method|setErr (String errprop)
specifier|public
name|void
name|setErr
parameter_list|(
name|String
name|errprop
parameter_list|)
block|{
name|this
operator|.
name|errprop
operator|=
name|errprop
expr_stmt|;
name|err
operator|=
operator|(
name|errprop
operator|.
name|equals
argument_list|(
name|outprop
argument_list|)
operator|)
condition|?
name|err
operator|=
name|out
else|:
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
block|}
comment|/**    * Sets the path for the parent-last ClassLoader, intended to be used for    * {@link org.apache.hadoop.conf.Configuration Configuration}.    * @param confpath The path to search for resources, classes, etc. before    * parent ClassLoaders.    */
DECL|method|setConf (String confpath)
specifier|public
name|void
name|setConf
parameter_list|(
name|String
name|confpath
parameter_list|)
block|{
name|confloader
operator|=
operator|new
name|AntClassLoader
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|confloader
operator|.
name|setProject
argument_list|(
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|confpath
condition|)
name|confloader
operator|.
name|addPathElement
argument_list|(
name|confpath
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets a property controlling whether or not a    * {@link org.apache.tools.ant.BuildException BuildException} will be thrown    * if the command returns a value less than zero or throws an exception.    * @param failonerror If true, throw a BuildException on error.    */
DECL|method|setFailonerror (boolean failonerror)
specifier|public
name|void
name|setFailonerror
parameter_list|(
name|boolean
name|failonerror
parameter_list|)
block|{
name|this
operator|.
name|failonerror
operator|=
name|failonerror
expr_stmt|;
block|}
comment|/**    * Save the current values of System.out, System.err and configure output    * streams for FsShell.    */
DECL|method|pushContext ()
specifier|protected
name|void
name|pushContext
parameter_list|()
block|{
name|antOut
operator|=
name|System
operator|.
name|out
expr_stmt|;
name|antErr
operator|=
name|System
operator|.
name|err
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
name|out
operator|==
name|err
condition|?
name|System
operator|.
name|out
else|:
operator|new
name|PrintStream
argument_list|(
name|err
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create the appropriate output properties with their respective output,    * restore System.out, System.err and release any resources from created    * ClassLoaders to aid garbage collection.    */
DECL|method|popContext ()
specifier|protected
name|void
name|popContext
parameter_list|()
block|{
comment|// write output to property, if applicable
if|if
condition|(
name|outprop
operator|!=
literal|null
operator|&&
operator|!
name|System
operator|.
name|out
operator|.
name|checkError
argument_list|()
condition|)
name|getProject
argument_list|()
operator|.
name|setNewProperty
argument_list|(
name|outprop
argument_list|,
name|out
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|!=
name|err
operator|&&
name|errprop
operator|!=
literal|null
operator|&&
operator|!
name|System
operator|.
name|err
operator|.
name|checkError
argument_list|()
condition|)
name|getProject
argument_list|()
operator|.
name|setNewProperty
argument_list|(
name|errprop
argument_list|,
name|err
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
name|antErr
argument_list|)
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|antOut
argument_list|)
expr_stmt|;
name|confloader
operator|.
name|cleanup
argument_list|()
expr_stmt|;
name|confloader
operator|.
name|setParent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// in case DfsTask is overridden
DECL|method|postCmd (int exit_code)
specifier|protected
name|int
name|postCmd
parameter_list|(
name|int
name|exit_code
parameter_list|)
block|{
if|if
condition|(
literal|"-test"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
operator|&&
name|exit_code
operator|!=
literal|0
condition|)
name|outprop
operator|=
literal|null
expr_stmt|;
return|return
name|exit_code
return|;
block|}
comment|/**    * Invoke {@link org.apache.hadoop.fs.FsShell#doMain FsShell.doMain} after a    * few cursory checks of the configuration.    */
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|BuildException
block|{
if|if
condition|(
literal|null
operator|==
name|cmd
condition|)
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"Missing command (cmd) argument"
argument_list|)
throw|;
name|argv
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|cmd
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|confloader
condition|)
block|{
name|setConf
argument_list|(
name|getProject
argument_list|()
operator|.
name|getProperty
argument_list|(
literal|"hadoop.conf.dir"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|exit_code
init|=
literal|0
decl_stmt|;
try|try
block|{
name|pushContext
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setClassLoader
argument_list|(
name|confloader
argument_list|)
expr_stmt|;
name|exit_code
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|conf
argument_list|,
name|shell
argument_list|,
name|argv
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|argv
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|exit_code
operator|=
name|postCmd
argument_list|(
name|exit_code
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|>
name|exit_code
condition|)
block|{
name|StringBuilder
name|msg
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|argv
control|)
name|msg
operator|.
name|append
argument_list|(
name|s
operator|+
literal|" "
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|"failed: "
operator|+
name|exit_code
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|Exception
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|failonerror
condition|)
throw|throw
operator|new
name|BuildException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|popContext
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

