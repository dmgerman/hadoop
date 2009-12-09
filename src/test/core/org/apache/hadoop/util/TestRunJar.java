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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

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
name|util
operator|.
name|jar
operator|.
name|JarOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileUtil
import|;
end_import

begin_class
DECL|class|TestRunJar
specifier|public
class|class
name|TestRunJar
extends|extends
name|TestCase
block|{
DECL|field|TEST_ROOT_DIR
specifier|private
name|File
name|TEST_ROOT_DIR
decl_stmt|;
DECL|field|TEST_JAR_NAME
specifier|private
specifier|static
specifier|final
name|String
name|TEST_JAR_NAME
init|=
literal|"test-runjar.jar"
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|TEST_ROOT_DIR
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|TEST_ROOT_DIR
operator|.
name|exists
argument_list|()
condition|)
block|{
name|TEST_ROOT_DIR
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
name|makeTestJar
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a jar with two files in it in our    * test dir.    */
DECL|method|makeTestJar ()
specifier|private
name|void
name|makeTestJar
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|jarFile
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
name|TEST_JAR_NAME
argument_list|)
decl_stmt|;
name|JarOutputStream
name|jstream
init|=
operator|new
name|JarOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|jarFile
argument_list|)
argument_list|)
decl_stmt|;
name|jstream
operator|.
name|putNextEntry
argument_list|(
operator|new
name|ZipEntry
argument_list|(
literal|"foobar.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|jstream
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
name|jstream
operator|.
name|putNextEntry
argument_list|(
operator|new
name|ZipEntry
argument_list|(
literal|"foobaz.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|jstream
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
name|jstream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test default unjarring behavior - unpack everything    */
annotation|@
name|Test
DECL|method|testUnJar ()
specifier|public
name|void
name|testUnJar
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|unjarDir
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"unjar-all"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"unjar dir shouldn't exist at test start"
argument_list|,
operator|new
name|File
argument_list|(
name|unjarDir
argument_list|,
literal|"foobar.txt"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// Unjar everything
name|RunJar
operator|.
name|unJar
argument_list|(
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
name|TEST_JAR_NAME
argument_list|)
argument_list|,
name|unjarDir
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"foobar unpacked"
argument_list|,
operator|new
name|File
argument_list|(
name|unjarDir
argument_list|,
literal|"foobar.txt"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"foobaz unpacked"
argument_list|,
operator|new
name|File
argument_list|(
name|unjarDir
argument_list|,
literal|"foobaz.txt"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test unjarring a specific regex    */
DECL|method|testUnJarWithPattern ()
specifier|public
name|void
name|testUnJarWithPattern
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|unjarDir
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"unjar-pattern"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"unjar dir shouldn't exist at test start"
argument_list|,
operator|new
name|File
argument_list|(
name|unjarDir
argument_list|,
literal|"foobar.txt"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// Unjar only a regex
name|RunJar
operator|.
name|unJar
argument_list|(
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
name|TEST_JAR_NAME
argument_list|)
argument_list|,
name|unjarDir
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*baz.*"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"foobar not unpacked"
argument_list|,
operator|new
name|File
argument_list|(
name|unjarDir
argument_list|,
literal|"foobar.txt"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"foobaz unpacked"
argument_list|,
operator|new
name|File
argument_list|(
name|unjarDir
argument_list|,
literal|"foobaz.txt"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

