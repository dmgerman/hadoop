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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|spy
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
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
name|io
operator|.
name|InputStream
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
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileUtil
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
name|test
operator|.
name|GenericTestUtils
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

begin_class
DECL|class|TestRunJar
specifier|public
class|class
name|TestRunJar
block|{
DECL|field|FOOBAR_TXT
specifier|private
specifier|static
specifier|final
name|String
name|FOOBAR_TXT
init|=
literal|"foobar.txt"
decl_stmt|;
DECL|field|FOOBAZ_TXT
specifier|private
specifier|static
specifier|final
name|String
name|FOOBAZ_TXT
init|=
literal|"foobaz.txt"
decl_stmt|;
DECL|field|BUFF_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BUFF_SIZE
init|=
literal|2048
decl_stmt|;
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
DECL|field|TEST_JAR_2_NAME
specifier|private
specifier|static
specifier|final
name|String
name|TEST_JAR_2_NAME
init|=
literal|"test-runjar2.jar"
decl_stmt|;
DECL|field|MOCKED_NOW
specifier|private
specifier|static
specifier|final
name|long
name|MOCKED_NOW
init|=
literal|1_460_389_972_000L
decl_stmt|;
DECL|field|MOCKED_NOW_PLUS_TWO_SEC
specifier|private
specifier|static
specifier|final
name|long
name|MOCKED_NOW_PLUS_TWO_SEC
init|=
name|MOCKED_NOW
operator|+
literal|2_000
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|TEST_ROOT_DIR
operator|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
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
specifier|public
name|void
name|tearDown
parameter_list|()
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
name|ZipEntry
name|zipEntry1
init|=
operator|new
name|ZipEntry
argument_list|(
name|FOOBAR_TXT
argument_list|)
decl_stmt|;
name|zipEntry1
operator|.
name|setTime
argument_list|(
name|MOCKED_NOW
argument_list|)
expr_stmt|;
name|jstream
operator|.
name|putNextEntry
argument_list|(
name|zipEntry1
argument_list|)
expr_stmt|;
name|jstream
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
name|ZipEntry
name|zipEntry2
init|=
operator|new
name|ZipEntry
argument_list|(
name|FOOBAZ_TXT
argument_list|)
decl_stmt|;
name|zipEntry2
operator|.
name|setTime
argument_list|(
name|MOCKED_NOW_PLUS_TWO_SEC
argument_list|)
expr_stmt|;
name|jstream
operator|.
name|putNextEntry
argument_list|(
name|zipEntry2
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
name|getUnjarDir
argument_list|(
literal|"unjar-all"
argument_list|)
decl_stmt|;
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
name|TestRunJar
operator|.
name|FOOBAR_TXT
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
name|FOOBAZ_TXT
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test unjarring a specific regex    */
annotation|@
name|Test
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
name|getUnjarDir
argument_list|(
literal|"unjar-pattern"
argument_list|)
decl_stmt|;
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
name|TestRunJar
operator|.
name|FOOBAR_TXT
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
name|FOOBAZ_TXT
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnJarDoesNotLooseLastModify ()
specifier|public
name|void
name|testUnJarDoesNotLooseLastModify
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|unjarDir
init|=
name|getUnjarDir
argument_list|(
literal|"unjar-lastmod"
argument_list|)
decl_stmt|;
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
name|String
name|failureMessage
init|=
literal|"Last modify time was lost during unJar"
decl_stmt|;
name|assertEquals
argument_list|(
name|failureMessage
argument_list|,
name|MOCKED_NOW
argument_list|,
operator|new
name|File
argument_list|(
name|unjarDir
argument_list|,
name|TestRunJar
operator|.
name|FOOBAR_TXT
argument_list|)
operator|.
name|lastModified
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|failureMessage
argument_list|,
name|MOCKED_NOW_PLUS_TWO_SEC
argument_list|,
operator|new
name|File
argument_list|(
name|unjarDir
argument_list|,
name|FOOBAZ_TXT
argument_list|)
operator|.
name|lastModified
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getUnjarDir (String dirName)
specifier|private
name|File
name|getUnjarDir
parameter_list|(
name|String
name|dirName
parameter_list|)
block|{
name|File
name|unjarDir
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
name|dirName
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
name|TestRunJar
operator|.
name|FOOBAR_TXT
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|unjarDir
return|;
block|}
comment|/**    * Tests the client classloader to verify the main class and its dependent    * class are loaded correctly by the application classloader, and others are    * loaded by the system classloader.    */
annotation|@
name|Test
DECL|method|testClientClassLoader ()
specifier|public
name|void
name|testClientClassLoader
parameter_list|()
throws|throws
name|Throwable
block|{
name|RunJar
name|runJar
init|=
name|spy
argument_list|(
operator|new
name|RunJar
argument_list|()
argument_list|)
decl_stmt|;
comment|// enable the client classloader
name|when
argument_list|(
name|runJar
operator|.
name|useClientClassLoader
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// set the system classes and blacklist the test main class and the test
comment|// third class so they can be loaded by the application classloader
name|String
name|mainCls
init|=
name|ClassLoaderCheckMain
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|thirdCls
init|=
name|ClassLoaderCheckThird
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|systemClasses
init|=
literal|"-"
operator|+
name|mainCls
operator|+
literal|","
operator|+
literal|"-"
operator|+
name|thirdCls
operator|+
literal|","
operator|+
name|ApplicationClassLoader
operator|.
name|SYSTEM_CLASSES_DEFAULT
decl_stmt|;
name|when
argument_list|(
name|runJar
operator|.
name|getSystemClasses
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|systemClasses
argument_list|)
expr_stmt|;
comment|// create the test jar
name|File
name|testJar
init|=
name|makeClassLoaderTestJar
argument_list|(
name|mainCls
argument_list|,
name|thirdCls
argument_list|)
decl_stmt|;
comment|// form the args
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[
literal|3
index|]
decl_stmt|;
name|args
index|[
literal|0
index|]
operator|=
name|testJar
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|args
index|[
literal|1
index|]
operator|=
name|mainCls
expr_stmt|;
comment|// run RunJar
name|runJar
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
comment|// it should not throw an exception
block|}
DECL|method|makeClassLoaderTestJar (String... clsNames)
specifier|private
name|File
name|makeClassLoaderTestJar
parameter_list|(
name|String
modifier|...
name|clsNames
parameter_list|)
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
name|TEST_JAR_2_NAME
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
for|for
control|(
name|String
name|clsName
range|:
name|clsNames
control|)
block|{
name|String
name|name
init|=
name|clsName
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
operator|+
literal|".class"
decl_stmt|;
name|InputStream
name|entryInputStream
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/"
operator|+
name|name
argument_list|)
decl_stmt|;
name|ZipEntry
name|entry
init|=
operator|new
name|ZipEntry
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|jstream
operator|.
name|putNextEntry
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|BufferedInputStream
name|bufInputStream
init|=
operator|new
name|BufferedInputStream
argument_list|(
name|entryInputStream
argument_list|,
name|BUFF_SIZE
argument_list|)
decl_stmt|;
name|int
name|count
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|BUFF_SIZE
index|]
decl_stmt|;
while|while
condition|(
operator|(
name|count
operator|=
name|bufInputStream
operator|.
name|read
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|BUFF_SIZE
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|jstream
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
name|jstream
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
name|jstream
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|jarFile
return|;
block|}
block|}
end_class

end_unit

