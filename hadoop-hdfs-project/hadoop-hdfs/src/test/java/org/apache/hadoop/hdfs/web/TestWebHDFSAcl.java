begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FSAclBaseTest
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
name|Ignore
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
comment|/**  * Tests ACL APIs via WebHDFS.  */
end_comment

begin_class
DECL|class|TestWebHDFSAcl
specifier|public
class|class
name|TestWebHDFSAcl
extends|extends
name|FSAclBaseTest
block|{
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
name|WebHdfsTestUtil
operator|.
name|createConf
argument_list|()
expr_stmt|;
name|startCluster
argument_list|()
expr_stmt|;
block|}
comment|/**    * We need to skip this test on WebHDFS, because WebHDFS currently cannot    * resolve symlinks.    */
annotation|@
name|Override
annotation|@
name|Test
annotation|@
name|Ignore
DECL|method|testDefaultAclNewSymlinkIntermediate ()
specifier|public
name|void
name|testDefaultAclNewSymlinkIntermediate
parameter_list|()
block|{   }
comment|/**    * Overridden to provide a WebHdfsFileSystem wrapper for the super-user.    *    * @return WebHdfsFileSystem for super-user    * @throws Exception if creation fails    */
annotation|@
name|Override
DECL|method|createFileSystem ()
specifier|protected
name|WebHdfsFileSystem
name|createFileSystem
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|WebHdfsTestUtil
operator|.
name|getWebHdfsFileSystem
argument_list|(
name|conf
argument_list|,
name|WebHdfsConstants
operator|.
name|WEBHDFS_SCHEME
argument_list|)
return|;
block|}
comment|/**    * Overridden to provide a WebHdfsFileSystem wrapper for a specific user.    *    * @param user UserGroupInformation specific user    * @return WebHdfsFileSystem for specific user    * @throws Exception if creation fails    */
annotation|@
name|Override
DECL|method|createFileSystem (UserGroupInformation user)
specifier|protected
name|WebHdfsFileSystem
name|createFileSystem
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|WebHdfsTestUtil
operator|.
name|getWebHdfsFileSystemAs
argument_list|(
name|user
argument_list|,
name|conf
argument_list|,
name|WebHdfsConstants
operator|.
name|WEBHDFS_SCHEME
argument_list|)
return|;
block|}
block|}
end_class

end_unit

