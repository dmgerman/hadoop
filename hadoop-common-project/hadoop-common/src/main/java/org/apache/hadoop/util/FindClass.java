begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|StringUtils
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
name|util
operator|.
name|ToolRunner
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
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|CodeSource
import|;
end_import

begin_comment
comment|/**  * This entry point exists for diagnosing classloader problems:  * is a class or resource present -and if so, where?  *  *<p>  * Actions  *<br>  *<ul>  *<li><pre>load</pre>: load a class but do not attempt to create it</li>  *<li><pre>create</pre>: load and create a class, print its string value</li>  *<li><pre>printresource</pre>: load a resource then print it to stdout</li>  *<li><pre>resource</pre>: load a resource then print the URL of that  *   resource</li>  *</ul>  *  * It returns an error code if a class/resource cannot be loaded/found  * -and optionally a class may be requested as being loaded.  * The latter action will call the class's constructor -it must support an  * empty constructor); any side effects from the  * constructor or static initializers will take place.  *  * All error messages are printed to {@link System#out}; errors  * to {@link System#err}.  *   */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UseOfSystemOutOrSystemErr"
argument_list|)
DECL|class|FindClass
specifier|public
specifier|final
class|class
name|FindClass
extends|extends
name|Configured
implements|implements
name|Tool
block|{
comment|/**    * create command: {@value}    */
DECL|field|A_CREATE
specifier|public
specifier|static
specifier|final
name|String
name|A_CREATE
init|=
literal|"create"
decl_stmt|;
comment|/**    * Load command: {@value}    */
DECL|field|A_LOAD
specifier|public
specifier|static
specifier|final
name|String
name|A_LOAD
init|=
literal|"load"
decl_stmt|;
comment|/**    * Command to locate a resource: {@value}    */
DECL|field|A_RESOURCE
specifier|public
specifier|static
specifier|final
name|String
name|A_RESOURCE
init|=
literal|"locate"
decl_stmt|;
comment|/**    * Command to locate and print a resource: {@value}    */
DECL|field|A_PRINTRESOURCE
specifier|public
specifier|static
specifier|final
name|String
name|A_PRINTRESOURCE
init|=
literal|"print"
decl_stmt|;
comment|/**    * Exit code when the operation succeeded: {@value}    */
DECL|field|SUCCESS
specifier|public
specifier|static
specifier|final
name|int
name|SUCCESS
init|=
literal|0
decl_stmt|;
comment|/**    * generic error {@value}    */
DECL|field|E_GENERIC
specifier|protected
specifier|static
specifier|final
name|int
name|E_GENERIC
init|=
literal|1
decl_stmt|;
comment|/**    * usage error -bad arguments or similar {@value}    */
DECL|field|E_USAGE
specifier|protected
specifier|static
specifier|final
name|int
name|E_USAGE
init|=
literal|2
decl_stmt|;
comment|/**    * class or resource not found {@value}    */
DECL|field|E_NOT_FOUND
specifier|protected
specifier|static
specifier|final
name|int
name|E_NOT_FOUND
init|=
literal|3
decl_stmt|;
comment|/**    * class load failed {@value}    */
DECL|field|E_LOAD_FAILED
specifier|protected
specifier|static
specifier|final
name|int
name|E_LOAD_FAILED
init|=
literal|4
decl_stmt|;
comment|/**    * class creation failed {@value}    */
DECL|field|E_CREATE_FAILED
specifier|protected
specifier|static
specifier|final
name|int
name|E_CREATE_FAILED
init|=
literal|5
decl_stmt|;
comment|/**    * Output stream. Defaults to {@link System#out}    */
DECL|field|stdout
specifier|private
specifier|static
name|PrintStream
name|stdout
init|=
name|System
operator|.
name|out
decl_stmt|;
comment|/**    * Error stream. Defaults to {@link System#err}    */
DECL|field|stderr
specifier|private
specifier|static
name|PrintStream
name|stderr
init|=
name|System
operator|.
name|err
decl_stmt|;
comment|/**    * Empty constructor; passes a new Configuration    * object instance to its superclass's constructor    */
DECL|method|FindClass ()
specifier|public
name|FindClass
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a class with a specified configuration    * @param conf configuration    */
DECL|method|FindClass (Configuration conf)
specifier|public
name|FindClass
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Change the output streams to be something other than the     * System.out and System.err streams    * @param out new stdout stream    * @param err new stderr stream    */
annotation|@
name|VisibleForTesting
DECL|method|setOutputStreams (PrintStream out, PrintStream err)
specifier|public
specifier|static
name|void
name|setOutputStreams
parameter_list|(
name|PrintStream
name|out
parameter_list|,
name|PrintStream
name|err
parameter_list|)
block|{
name|stdout
operator|=
name|out
expr_stmt|;
name|stderr
operator|=
name|err
expr_stmt|;
block|}
comment|/**    * Get a class fromt the configuration    * @param name the class name    * @return the class    * @throws ClassNotFoundException if the class was not found    * @throws Error on other classloading problems    */
DECL|method|getClass (String name)
specifier|private
name|Class
name|getClass
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getClassByName
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Get the resource    * @param name resource name    * @return URL or null for not found    */
DECL|method|getResource (String name)
specifier|private
name|URL
name|getResource
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getResource
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Load a resource    * @param name resource name    * @return the status code    */
DECL|method|loadResource (String name)
specifier|private
name|int
name|loadResource
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|URL
name|url
init|=
name|getResource
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
name|err
argument_list|(
literal|"Resource not found: %s"
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|E_NOT_FOUND
return|;
block|}
name|out
argument_list|(
literal|"%s: %s"
argument_list|,
name|name
argument_list|,
name|url
argument_list|)
expr_stmt|;
return|return
name|SUCCESS
return|;
block|}
comment|/**    * Dump a resource to out    * @param name resource name    * @return the status code    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"NestedAssignment"
argument_list|)
DECL|method|dumpResource (String name)
specifier|private
name|int
name|dumpResource
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|URL
name|url
init|=
name|getResource
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
name|err
argument_list|(
literal|"Resource not found:"
operator|+
name|name
argument_list|)
expr_stmt|;
return|return
name|E_NOT_FOUND
return|;
block|}
try|try
block|{
comment|//open the resource
name|InputStream
name|instream
init|=
name|url
operator|.
name|openStream
argument_list|()
decl_stmt|;
comment|//read it in and print
name|int
name|data
decl_stmt|;
while|while
condition|(
operator|-
literal|1
operator|!=
operator|(
name|data
operator|=
name|instream
operator|.
name|read
argument_list|()
operator|)
condition|)
block|{
name|stdout
operator|.
name|print
argument_list|(
operator|(
name|char
operator|)
name|data
argument_list|)
expr_stmt|;
block|}
comment|//end of file
name|stdout
operator|.
name|print
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
return|return
name|SUCCESS
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|printStack
argument_list|(
name|e
argument_list|,
literal|"Failed to read resource %s at URL %s"
argument_list|,
name|name
argument_list|,
name|url
argument_list|)
expr_stmt|;
return|return
name|E_LOAD_FAILED
return|;
block|}
block|}
comment|/**    * print something to stderr    * @param s string to print    */
DECL|method|err (String s, Object... args)
specifier|private
specifier|static
name|void
name|err
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|stderr
operator|.
name|format
argument_list|(
name|s
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|stderr
operator|.
name|print
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
comment|/**    * print something to stdout    * @param s string to print    */
DECL|method|out (String s, Object... args)
specifier|private
specifier|static
name|void
name|out
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|stdout
operator|.
name|format
argument_list|(
name|s
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|stdout
operator|.
name|print
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
comment|/**    * print a stack trace with text    * @param e the exception to print    * @param text text to print    */
DECL|method|printStack (Throwable e, String text, Object... args)
specifier|private
specifier|static
name|void
name|printStack
parameter_list|(
name|Throwable
name|e
parameter_list|,
name|String
name|text
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|err
argument_list|(
name|text
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|stderr
argument_list|)
expr_stmt|;
block|}
comment|/**    * Loads the class of the given name    * @param name classname    * @return outcome code    */
DECL|method|loadClass (String name)
specifier|private
name|int
name|loadClass
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
name|Class
name|clazz
init|=
name|getClass
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|loadedClass
argument_list|(
name|name
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
return|return
name|SUCCESS
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|printStack
argument_list|(
name|e
argument_list|,
literal|"Class not found "
operator|+
name|name
argument_list|)
expr_stmt|;
return|return
name|E_NOT_FOUND
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|printStack
argument_list|(
name|e
argument_list|,
literal|"Exception while loading class "
operator|+
name|name
argument_list|)
expr_stmt|;
return|return
name|E_LOAD_FAILED
return|;
block|}
catch|catch
parameter_list|(
name|Error
name|e
parameter_list|)
block|{
name|printStack
argument_list|(
name|e
argument_list|,
literal|"Error while loading class "
operator|+
name|name
argument_list|)
expr_stmt|;
return|return
name|E_LOAD_FAILED
return|;
block|}
block|}
comment|/**    * Log that a class has been loaded, and where from.    * @param name classname    * @param clazz class    */
DECL|method|loadedClass (String name, Class clazz)
specifier|private
name|void
name|loadedClass
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
name|out
argument_list|(
literal|"Loaded %s as %s"
argument_list|,
name|name
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
name|CodeSource
name|source
init|=
name|clazz
operator|.
name|getProtectionDomain
argument_list|()
operator|.
name|getCodeSource
argument_list|()
decl_stmt|;
name|URL
name|url
init|=
name|source
operator|.
name|getLocation
argument_list|()
decl_stmt|;
name|out
argument_list|(
literal|"%s: %s"
argument_list|,
name|name
argument_list|,
name|url
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create an instance of a class    * @param name classname    * @return the outcome    */
DECL|method|createClassInstance (String name)
specifier|private
name|int
name|createClassInstance
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
name|Class
name|clazz
init|=
name|getClass
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|loadedClass
argument_list|(
name|name
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
name|Object
name|instance
init|=
name|clazz
operator|.
name|newInstance
argument_list|()
decl_stmt|;
try|try
block|{
comment|//stringify
name|out
argument_list|(
literal|"Created instance "
operator|+
name|instance
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//catch those classes whose toString() method is brittle, but don't fail the probe
name|printStack
argument_list|(
name|e
argument_list|,
literal|"Created class instance but the toString() operator failed"
argument_list|)
expr_stmt|;
block|}
return|return
name|SUCCESS
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|printStack
argument_list|(
name|e
argument_list|,
literal|"Class not found "
operator|+
name|name
argument_list|)
expr_stmt|;
return|return
name|E_NOT_FOUND
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|printStack
argument_list|(
name|e
argument_list|,
literal|"Exception while creating class "
operator|+
name|name
argument_list|)
expr_stmt|;
return|return
name|E_CREATE_FAILED
return|;
block|}
catch|catch
parameter_list|(
name|Error
name|e
parameter_list|)
block|{
name|printStack
argument_list|(
name|e
argument_list|,
literal|"Exception while creating class "
operator|+
name|name
argument_list|)
expr_stmt|;
return|return
name|E_CREATE_FAILED
return|;
block|}
block|}
comment|/**    * Run the class/resource find or load operation    * @param args command specific arguments.    * @return the outcome    * @throws Exception if something went very wrong    */
annotation|@
name|Override
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
return|return
name|usage
argument_list|(
name|args
argument_list|)
return|;
block|}
name|String
name|action
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|String
name|name
init|=
name|args
index|[
literal|1
index|]
decl_stmt|;
name|int
name|result
decl_stmt|;
if|if
condition|(
name|A_LOAD
operator|.
name|equals
argument_list|(
name|action
argument_list|)
condition|)
block|{
name|result
operator|=
name|loadClass
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|A_CREATE
operator|.
name|equals
argument_list|(
name|action
argument_list|)
condition|)
block|{
comment|//first load to separate load errors from create
name|result
operator|=
name|loadClass
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|==
name|SUCCESS
condition|)
block|{
comment|//class loads, so instantiate it
name|result
operator|=
name|createClassInstance
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|A_RESOURCE
operator|.
name|equals
argument_list|(
name|action
argument_list|)
condition|)
block|{
name|result
operator|=
name|loadResource
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|A_PRINTRESOURCE
operator|.
name|equals
argument_list|(
name|action
argument_list|)
condition|)
block|{
name|result
operator|=
name|dumpResource
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|usage
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Print a usage message    * @param args the command line arguments    * @return an exit code    */
DECL|method|usage (String[] args)
specifier|private
name|int
name|usage
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|err
argument_list|(
literal|"Usage : [load | create]<classname>"
argument_list|)
expr_stmt|;
name|err
argument_list|(
literal|"        [locate | print]<resourcename>]"
argument_list|)
expr_stmt|;
name|err
argument_list|(
literal|"The return codes are:"
argument_list|)
expr_stmt|;
name|explainResult
argument_list|(
name|SUCCESS
argument_list|,
literal|"The operation was successful"
argument_list|)
expr_stmt|;
name|explainResult
argument_list|(
name|E_GENERIC
argument_list|,
literal|"Something went wrong"
argument_list|)
expr_stmt|;
name|explainResult
argument_list|(
name|E_USAGE
argument_list|,
literal|"This usage message was printed"
argument_list|)
expr_stmt|;
name|explainResult
argument_list|(
name|E_NOT_FOUND
argument_list|,
literal|"The class or resource was not found"
argument_list|)
expr_stmt|;
name|explainResult
argument_list|(
name|E_LOAD_FAILED
argument_list|,
literal|"The class was found but could not be loaded"
argument_list|)
expr_stmt|;
name|explainResult
argument_list|(
name|E_CREATE_FAILED
argument_list|,
literal|"The class was loaded, but an instance of it could not be created"
argument_list|)
expr_stmt|;
return|return
name|E_USAGE
return|;
block|}
comment|/**    * Explain an error code as part of the usage    * @param errorcode error code returned    * @param text error text    */
DECL|method|explainResult (int errorcode, String text)
specifier|private
name|void
name|explainResult
parameter_list|(
name|int
name|errorcode
parameter_list|,
name|String
name|text
parameter_list|)
block|{
name|err
argument_list|(
literal|" %2d -- %s "
argument_list|,
name|errorcode
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
comment|/**    * Main entry point.     * Runs the class via the {@link ToolRunner}, then    * exits with an appropriate exit code.     * @param args argument list    */
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
try|try
block|{
name|int
name|result
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|FindClass
argument_list|()
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|printStack
argument_list|(
name|e
argument_list|,
literal|"Running FindClass"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|E_GENERIC
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

