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
name|FileSystem
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
name|Path
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
name|permission
operator|.
name|FsPermission
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
name|MiniDFSCluster
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
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_class
DECL|class|TestHdfsHelper
specifier|public
class|class
name|TestHdfsHelper
extends|extends
name|TestDirHelper
block|{
annotation|@
name|Test
DECL|method|dummy ()
specifier|public
name|void
name|dummy
parameter_list|()
block|{   }
DECL|field|HADOOP_MINI_HDFS
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_MINI_HDFS
init|=
literal|"test.hadoop.hdfs"
decl_stmt|;
DECL|field|HDFS_CONF_TL
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|Configuration
argument_list|>
name|HDFS_CONF_TL
init|=
operator|new
name|InheritableThreadLocal
argument_list|<
name|Configuration
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|HDFS_TEST_DIR_TL
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|Path
argument_list|>
name|HDFS_TEST_DIR_TL
init|=
operator|new
name|InheritableThreadLocal
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|apply (Statement statement, FrameworkMethod frameworkMethod, Object o)
specifier|public
name|Statement
name|apply
parameter_list|(
name|Statement
name|statement
parameter_list|,
name|FrameworkMethod
name|frameworkMethod
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
name|TestHdfs
name|testHdfsAnnotation
init|=
name|frameworkMethod
operator|.
name|getAnnotation
argument_list|(
name|TestHdfs
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|testHdfsAnnotation
operator|!=
literal|null
condition|)
block|{
name|statement
operator|=
operator|new
name|HdfsStatement
argument_list|(
name|statement
argument_list|,
name|frameworkMethod
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|apply
argument_list|(
name|statement
argument_list|,
name|frameworkMethod
argument_list|,
name|o
argument_list|)
return|;
block|}
DECL|class|HdfsStatement
specifier|private
specifier|static
class|class
name|HdfsStatement
extends|extends
name|Statement
block|{
DECL|field|statement
specifier|private
name|Statement
name|statement
decl_stmt|;
DECL|field|testName
specifier|private
name|String
name|testName
decl_stmt|;
DECL|method|HdfsStatement (Statement statement, String testName)
specifier|public
name|HdfsStatement
parameter_list|(
name|Statement
name|statement
parameter_list|,
name|String
name|testName
parameter_list|)
block|{
name|this
operator|.
name|statement
operator|=
name|statement
expr_stmt|;
name|this
operator|.
name|testName
operator|=
name|testName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|evaluate ()
specifier|public
name|void
name|evaluate
parameter_list|()
throws|throws
name|Throwable
block|{
name|MiniDFSCluster
name|miniHdfs
init|=
literal|null
decl_stmt|;
name|Configuration
name|conf
init|=
name|HadoopUsersConfTestHelper
operator|.
name|getBaseConf
argument_list|()
decl_stmt|;
if|if
condition|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
name|HADOOP_MINI_HDFS
argument_list|,
literal|"true"
argument_list|)
argument_list|)
condition|)
block|{
name|miniHdfs
operator|=
name|startMiniHdfs
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|=
name|miniHdfs
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|HDFS_CONF_TL
operator|.
name|set
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|HDFS_TEST_DIR_TL
operator|.
name|set
argument_list|(
name|resetHdfsTestDir
argument_list|(
name|conf
argument_list|)
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
name|HDFS_CONF_TL
operator|.
name|remove
argument_list|()
expr_stmt|;
name|HDFS_TEST_DIR_TL
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
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
DECL|method|resetHdfsTestDir (Configuration conf)
specifier|private
name|Path
name|resetHdfsTestDir
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Path
name|testDir
init|=
operator|new
name|Path
argument_list|(
literal|"./"
operator|+
name|TEST_DIR_ROOT
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
decl_stmt|;
try|try
block|{
comment|// currentUser
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|testDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
return|return
name|testDir
return|;
block|}
block|}
comment|/**    * Returns the HDFS test directory for the current test, only available when the    * test method has been annotated with {@link TestHdfs}.    *    * @return the HDFS test directory for the current test. It is an full/absolute    *<code>Path</code>.    */
DECL|method|getHdfsTestDir ()
specifier|public
specifier|static
name|Path
name|getHdfsTestDir
parameter_list|()
block|{
name|Path
name|testDir
init|=
name|HDFS_TEST_DIR_TL
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
literal|"This test does not use @TestHdfs"
argument_list|)
throw|;
block|}
return|return
name|testDir
return|;
block|}
comment|/**    * Returns a FileSystemAccess<code>JobConf</code> preconfigured with the FileSystemAccess cluster    * settings for testing. This configuration is only available when the test    * method has been annotated with {@link TestHdfs}. Refer to {@link HTestCase}    * header for details)    *    * @return the FileSystemAccess<code>JobConf</code> preconfigured with the FileSystemAccess cluster    *         settings for testing    */
DECL|method|getHdfsConf ()
specifier|public
specifier|static
name|Configuration
name|getHdfsConf
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|HDFS_CONF_TL
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"This test does not use @TestHdfs"
argument_list|)
throw|;
block|}
return|return
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|field|MINI_DFS
specifier|private
specifier|static
name|MiniDFSCluster
name|MINI_DFS
init|=
literal|null
decl_stmt|;
DECL|method|startMiniHdfs (Configuration conf)
specifier|private
specifier|static
specifier|synchronized
name|MiniDFSCluster
name|startMiniHdfs
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|MINI_DFS
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"hadoop.log.dir"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"hadoop.log.dir"
argument_list|,
operator|new
name|File
argument_list|(
name|TEST_DIR_ROOT
argument_list|,
literal|"hadoop-log"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"test.build.data"
argument_list|,
operator|new
name|File
argument_list|(
name|TEST_DIR_ROOT
argument_list|,
literal|"hadoop-data"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|HadoopUsersConfTestHelper
operator|.
name|addUserConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"fs.hdfs.impl.disable.cache"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"dfs.block.access.token.enable"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"dfs.permissions"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.security.authentication"
argument_list|,
literal|"simple"
argument_list|)
expr_stmt|;
name|MiniDFSCluster
operator|.
name|Builder
name|builder
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|builder
operator|.
name|numDataNodes
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|miniHdfs
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|FileSystem
name|fileSystem
init|=
name|miniHdfs
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|fileSystem
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp"
argument_list|)
argument_list|)
expr_stmt|;
name|fileSystem
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/user"
argument_list|)
argument_list|)
expr_stmt|;
name|fileSystem
operator|.
name|setPermission
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp"
argument_list|)
argument_list|,
name|FsPermission
operator|.
name|valueOf
argument_list|(
literal|"-rwxrwxrwx"
argument_list|)
argument_list|)
expr_stmt|;
name|fileSystem
operator|.
name|setPermission
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/user"
argument_list|)
argument_list|,
name|FsPermission
operator|.
name|valueOf
argument_list|(
literal|"-rwxrwxrwx"
argument_list|)
argument_list|)
expr_stmt|;
name|MINI_DFS
operator|=
name|miniHdfs
expr_stmt|;
block|}
return|return
name|MINI_DFS
return|;
block|}
block|}
end_class

end_unit

