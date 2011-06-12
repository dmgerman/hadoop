begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.streaming
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|streaming
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|Iterator
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
name|util
operator|.
name|Shell
operator|.
name|ShellCommandExecutor
import|;
end_import

begin_class
DECL|class|UtilTest
class|class
name|UtilTest
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|UtilTest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Utility routine to recurisvely delete a directory.    * On normal return, the file does not exist.    *    * @param file File or directory to delete.    *    * @throws RuntimeException if the file, or some file within    * it, could not be deleted.    */
DECL|method|recursiveDelete (File file)
specifier|static
name|void
name|recursiveDelete
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|file
operator|=
name|file
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
return|return;
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
for|for
control|(
name|File
name|child
range|:
name|file
operator|.
name|listFiles
argument_list|()
control|)
block|{
name|recursiveDelete
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|file
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to delete "
operator|+
name|file
argument_list|)
throw|;
block|}
block|}
DECL|method|UtilTest (String testName)
specifier|public
name|UtilTest
parameter_list|(
name|String
name|testName
parameter_list|)
block|{
name|testName_
operator|=
name|testName
expr_stmt|;
name|userDir_
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
expr_stmt|;
name|antTestDir_
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
name|userDir_
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"test.build.data-or-user.dir="
operator|+
name|antTestDir_
argument_list|)
expr_stmt|;
block|}
DECL|method|checkUserDir ()
name|void
name|checkUserDir
parameter_list|()
block|{
comment|// trunk/src/contrib/streaming --> trunk/build/contrib/streaming/test/data
if|if
condition|(
operator|!
name|userDir_
operator|.
name|equals
argument_list|(
name|antTestDir_
argument_list|)
condition|)
block|{
comment|// because changes to user.dir are ignored by File static methods.
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"user.dir != test.build.data. The junit Ant task must be forked."
argument_list|)
throw|;
block|}
block|}
DECL|method|redirectIfAntJunit ()
name|void
name|redirectIfAntJunit
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|fromAntJunit
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|)
operator|!=
literal|null
decl_stmt|;
if|if
condition|(
name|fromAntJunit
condition|)
block|{
operator|new
name|File
argument_list|(
name|antTestDir_
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|outFile
init|=
operator|new
name|File
argument_list|(
name|antTestDir_
argument_list|,
name|testName_
operator|+
literal|".log"
argument_list|)
decl_stmt|;
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|outFile
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|collate (List<String> args, String sep)
specifier|public
specifier|static
name|String
name|collate
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|sep
parameter_list|)
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|args
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|makeJavaCommand (Class<?> main, String[] argv)
specifier|public
specifier|static
name|String
name|makeJavaCommand
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|main
parameter_list|,
name|String
index|[]
name|argv
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|vargs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|File
name|javaHomeBin
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.home"
argument_list|)
argument_list|,
literal|"bin"
argument_list|)
decl_stmt|;
name|File
name|jvm
init|=
operator|new
name|File
argument_list|(
name|javaHomeBin
argument_list|,
literal|"java"
argument_list|)
decl_stmt|;
name|vargs
operator|.
name|add
argument_list|(
name|jvm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// copy parent classpath
name|vargs
operator|.
name|add
argument_list|(
literal|"-classpath"
argument_list|)
expr_stmt|;
name|vargs
operator|.
name|add
argument_list|(
literal|"\""
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.class.path"
argument_list|)
operator|+
literal|"\""
argument_list|)
expr_stmt|;
comment|// add heap-size limit
name|vargs
operator|.
name|add
argument_list|(
literal|"-Xmx"
operator|+
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|maxMemory
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add main class and its arguments
name|vargs
operator|.
name|add
argument_list|(
name|main
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|argv
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|vargs
operator|.
name|add
argument_list|(
name|argv
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|collate
argument_list|(
name|vargs
argument_list|,
literal|" "
argument_list|)
return|;
block|}
DECL|method|isCygwin ()
specifier|public
specifier|static
name|boolean
name|isCygwin
parameter_list|()
block|{
name|String
name|OS
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
decl_stmt|;
return|return
operator|(
name|OS
operator|.
name|indexOf
argument_list|(
literal|"Windows"
argument_list|)
operator|>
operator|-
literal|1
operator|)
return|;
block|}
comment|/**    * Is perl supported on this machine ?    * @return true if perl is available and is working as expected    */
DECL|method|hasPerlSupport ()
specifier|public
specifier|static
name|boolean
name|hasPerlSupport
parameter_list|()
block|{
name|boolean
name|hasPerl
init|=
literal|false
decl_stmt|;
name|ShellCommandExecutor
name|shexec
init|=
operator|new
name|ShellCommandExecutor
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"perl"
block|,
literal|"-e"
block|,
literal|"print 42"
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|shexec
operator|.
name|execute
argument_list|()
expr_stmt|;
if|if
condition|(
name|shexec
operator|.
name|getOutput
argument_list|()
operator|.
name|equals
argument_list|(
literal|"42"
argument_list|)
condition|)
block|{
name|hasPerl
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Perl is installed, but isn't behaving as expected."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not run perl: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|hasPerl
return|;
block|}
DECL|field|userDir_
specifier|private
name|String
name|userDir_
decl_stmt|;
DECL|field|antTestDir_
specifier|private
name|String
name|antTestDir_
decl_stmt|;
DECL|field|testName_
specifier|private
name|String
name|testName_
decl_stmt|;
block|}
end_class

end_unit

