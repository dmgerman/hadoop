begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|hdfs
operator|.
name|DFSTestUtil
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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|server
operator|.
name|namenode
operator|.
name|NameNodeAdapter
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
name|security
operator|.
name|UserGroupInformation
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|AbstractDelegationTokenIdentifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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

begin_comment
comment|/**  * Tests whether FileContext can resolve an hdfs path that has a symlink to  * local file system. Also tests getDelegationTokens API in file context with  * underlying file system as Hdfs.  */
end_comment

begin_class
DECL|class|TestResolveHdfsSymlink
specifier|public
class|class
name|TestResolveHdfsSymlink
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|NameNodeAdapter
operator|.
name|getDtSecretManager
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
argument_list|)
operator|.
name|startThreads
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Tests resolution of an hdfs symlink to the local file system.    * @throws IOException    * @throws InterruptedException    */
annotation|@
name|Test
DECL|method|testFcResolveAfs ()
specifier|public
name|void
name|testFcResolveAfs
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FileContext
name|fcLocal
init|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
decl_stmt|;
name|FileContext
name|fcHdfs
init|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|alphaLocalPath
init|=
operator|new
name|Path
argument_list|(
name|fcLocal
operator|.
name|getDefaultFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"/tmp/alpha"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
argument_list|,
name|alphaLocalPath
argument_list|,
literal|16
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Path
name|linkTarget
init|=
operator|new
name|Path
argument_list|(
name|fcLocal
operator|.
name|getDefaultFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"/tmp"
argument_list|)
decl_stmt|;
name|Path
name|hdfsLink
init|=
operator|new
name|Path
argument_list|(
name|fcHdfs
operator|.
name|getDefaultFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"/tmp/link"
argument_list|)
decl_stmt|;
name|fcHdfs
operator|.
name|createSymlink
argument_list|(
name|linkTarget
argument_list|,
name|hdfsLink
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Path
name|alphaHdfsPathViaLink
init|=
operator|new
name|Path
argument_list|(
name|fcHdfs
operator|.
name|getDefaultFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/tmp/link/alpha"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|AbstractFileSystem
argument_list|>
name|afsList
init|=
name|fcHdfs
operator|.
name|resolveAbstractFileSystems
argument_list|(
name|alphaHdfsPathViaLink
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|afsList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AbstractFileSystem
name|afs
range|:
name|afsList
control|)
block|{
if|if
condition|(
operator|(
operator|!
name|afs
operator|.
name|equals
argument_list|(
name|fcHdfs
operator|.
name|getDefaultFileSystem
argument_list|()
argument_list|)
operator|)
operator|&&
operator|(
operator|!
name|afs
operator|.
name|equals
argument_list|(
name|fcLocal
operator|.
name|getDefaultFileSystem
argument_list|()
argument_list|)
operator|)
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Failed to resolve AFS correctly"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Tests delegation token APIs in FileContext for Hdfs; and renew and cancel    * APIs in Hdfs.    *     * @throws UnsupportedFileSystemException    * @throws IOException    * @throws InterruptedException    */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"deprecation"
block|}
argument_list|)
annotation|@
name|Test
DECL|method|testFcDelegationToken ()
specifier|public
name|void
name|testFcDelegationToken
parameter_list|()
throws|throws
name|UnsupportedFileSystemException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|FileContext
name|fcHdfs
init|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|AbstractFileSystem
name|afs
init|=
name|fcHdfs
operator|.
name|getDefaultFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Token
argument_list|<
name|?
argument_list|>
argument_list|>
name|tokenList
init|=
name|afs
operator|.
name|getDelegationTokens
argument_list|(
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
decl_stmt|;
operator|(
operator|(
name|Hdfs
operator|)
name|afs
operator|)
operator|.
name|renewDelegationToken
argument_list|(
operator|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
operator|)
name|tokenList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Hdfs
operator|)
name|afs
operator|)
operator|.
name|cancelDelegationToken
argument_list|(
operator|(
name|Token
argument_list|<
name|?
extends|extends
name|AbstractDelegationTokenIdentifier
argument_list|>
operator|)
name|tokenList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

