begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|MethodRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|FrameworkMethod
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|Statement
import|;
end_import

begin_class
DECL|class|TestDirHelper
specifier|public
class|class
name|TestDirHelper
implements|implements
name|MethodRule
block|{
annotation|@
name|Test
DECL|method|dummy ()
specifier|public
name|void
name|dummy
parameter_list|()
block|{   }
static|static
block|{
name|SysPropsForTestsLoader
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
DECL|field|TEST_DIR_PROP
specifier|public
specifier|static
specifier|final
name|String
name|TEST_DIR_PROP
init|=
literal|"test.dir"
decl_stmt|;
DECL|field|TEST_DIR_ROOT
specifier|static
name|String
name|TEST_DIR_ROOT
decl_stmt|;
DECL|method|delete (File file)
specifier|private
specifier|static
name|void
name|delete
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|file
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|length
argument_list|()
operator|<
literal|5
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Path [{0}] is too short, not deleting"
argument_list|,
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|File
index|[]
name|children
init|=
name|file
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|child
range|:
name|children
control|)
block|{
name|delete
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
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
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Could not delete path [{0}]"
argument_list|,
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
static|static
block|{
try|try
block|{
name|TEST_DIR_ROOT
operator|=
name|System
operator|.
name|getProperty
argument_list|(
name|TEST_DIR_PROP
argument_list|,
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|TEST_DIR_ROOT
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"System property [{0}]=[{1}] must be set to an absolute path"
argument_list|,
name|TEST_DIR_PROP
argument_list|,
name|TEST_DIR_ROOT
argument_list|)
argument_list|)
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
elseif|else
if|if
condition|(
name|TEST_DIR_ROOT
operator|.
name|length
argument_list|()
operator|<
literal|4
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"System property [{0}]=[{1}] must be at least 4 chars"
argument_list|,
name|TEST_DIR_PROP
argument_list|,
name|TEST_DIR_ROOT
argument_list|)
argument_list|)
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
name|TEST_DIR_ROOT
operator|=
operator|new
name|File
argument_list|(
name|TEST_DIR_ROOT
argument_list|,
literal|"testdir"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|TEST_DIR_PROP
argument_list|,
name|TEST_DIR_ROOT
argument_list|)
expr_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR_ROOT
argument_list|)
decl_stmt|;
name|delete
argument_list|(
name|dir
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Could not create test dir [{0}]"
argument_list|,
name|TEST_DIR_ROOT
argument_list|)
argument_list|)
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"test.circus"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|">>> "
operator|+
name|TEST_DIR_PROP
operator|+
literal|"        : "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
name|TEST_DIR_PROP
argument_list|)
argument_list|)
expr_stmt|;
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
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|field|TEST_DIR_TL
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|File
argument_list|>
name|TEST_DIR_TL
init|=
operator|new
name|InheritableThreadLocal
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|apply (final Statement statement, final FrameworkMethod frameworkMethod, final Object o)
specifier|public
name|Statement
name|apply
parameter_list|(
specifier|final
name|Statement
name|statement
parameter_list|,
specifier|final
name|FrameworkMethod
name|frameworkMethod
parameter_list|,
specifier|final
name|Object
name|o
parameter_list|)
block|{
return|return
operator|new
name|Statement
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|evaluate
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|testDir
init|=
literal|null
decl_stmt|;
name|TestDir
name|testDirAnnotation
init|=
name|frameworkMethod
operator|.
name|getAnnotation
argument_list|(
name|TestDir
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|testDirAnnotation
operator|!=
literal|null
condition|)
block|{
name|testDir
operator|=
name|resetTestCaseDir
argument_list|(
name|frameworkMethod
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|TEST_DIR_TL
operator|.
name|set
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
name|statement
operator|.
name|evaluate
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|TEST_DIR_TL
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
comment|/**    * Returns the local test directory for the current test, only available when the    * test method has been annotated with {@link TestDir}.    *    * @return the test directory for the current test. It is an full/absolute    *<code>File</code>.    */
DECL|method|getTestDir ()
specifier|public
specifier|static
name|File
name|getTestDir
parameter_list|()
block|{
name|File
name|testDir
init|=
name|TEST_DIR_TL
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|testDir
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"This test does not use @TestDir"
argument_list|)
throw|;
block|}
return|return
name|testDir
return|;
block|}
DECL|field|counter
specifier|private
specifier|static
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|method|resetTestCaseDir (String testName)
specifier|private
specifier|static
name|File
name|resetTestCaseDir
parameter_list|(
name|String
name|testName
parameter_list|)
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR_ROOT
argument_list|)
decl_stmt|;
name|dir
operator|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|testName
operator|+
literal|"-"
operator|+
name|counter
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|=
name|dir
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
try|try
block|{
name|delete
argument_list|(
name|dir
argument_list|)
expr_stmt|;
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
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Could not delete test dir[{0}], {1}"
argument_list|,
name|dir
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|,
name|ex
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|dir
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Could not create test dir[{0}]"
argument_list|,
name|dir
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|dir
return|;
block|}
block|}
end_class

end_unit

