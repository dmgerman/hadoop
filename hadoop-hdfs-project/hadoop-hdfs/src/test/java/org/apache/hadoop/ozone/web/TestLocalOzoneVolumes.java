begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|datanode
operator|.
name|DataNode
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
name|ozone
operator|.
name|MiniOzoneCluster
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|ozone
operator|.
name|OzoneConfiguration
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
name|ozone
operator|.
name|OzoneConsts
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
name|apache
operator|.
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|Timeout
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

begin_comment
comment|/**  * Test ozone volume in the local storage handler scenario.  */
end_comment

begin_class
DECL|class|TestLocalOzoneVolumes
specifier|public
class|class
name|TestLocalOzoneVolumes
extends|extends
name|TestOzoneHelper
block|{
comment|/**    * Set the timeout for every test.    */
annotation|@
name|Rule
DECL|field|testTimeout
specifier|public
name|Timeout
name|testTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|port
specifier|private
specifier|static
name|int
name|port
init|=
literal|0
decl_stmt|;
comment|/**    * Create a MiniDFSCluster for testing.    *<p>    * Ozone is made active by setting OZONE_ENABLED = true and    * OZONE_HANDLER_TYPE_KEY = "local" , which uses a local directory to    * emulate Ozone backend.    *    * @throws IOException    */
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
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestLocalOzoneVolumes
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|path
operator|+=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT_DEFAULT
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|Logger
operator|.
name|getLogger
argument_list|(
literal|"log4j.logger.org.apache.http"
argument_list|)
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniOzoneCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_LOCAL
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|DataNode
name|dataNode
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|port
operator|=
name|dataNode
operator|.
name|getInfoPort
argument_list|()
expr_stmt|;
block|}
comment|/**    * Shutdown MiniDFSCluster.    */
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
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
comment|/**    * Creates Volumes on Ozone Store.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testCreateVolumes ()
specifier|public
name|void
name|testCreateVolumes
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testCreateVolumes
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create Volumes with Quota.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testCreateVolumesWithQuota ()
specifier|public
name|void
name|testCreateVolumesWithQuota
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testCreateVolumesWithQuota
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create Volumes with Invalid Quota.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testCreateVolumesWithInvalidQuota ()
specifier|public
name|void
name|testCreateVolumesWithInvalidQuota
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testCreateVolumesWithInvalidQuota
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
comment|/**    * To create a volume a user name must be specified using OZONE_USER header.    * This test verifies that we get an error in case we call without a OZONE    * user name.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testCreateVolumesWithInvalidUser ()
specifier|public
name|void
name|testCreateVolumesWithInvalidUser
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testCreateVolumesWithInvalidUser
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
comment|/**    * Only Admins can create volumes in Ozone. This test uses simple userauth as    * backend and hdfs and root are admin users in the simple backend.    *<p>    * This test tries to create a volume as user bilbo.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testCreateVolumesWithOutAdminRights ()
specifier|public
name|void
name|testCreateVolumesWithOutAdminRights
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testCreateVolumesWithOutAdminRights
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a bunch of volumes in a loop.    *    * @throws IOException    */
comment|//@Test
DECL|method|testCreateVolumesInLoop ()
specifier|public
name|void
name|testCreateVolumesInLoop
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testCreateVolumesInLoop
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get volumes owned by the user.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testGetVolumesByUser ()
specifier|public
name|void
name|testGetVolumesByUser
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testGetVolumesByUser
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
comment|/**    * Admins can read volumes belonging to other users.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testGetVolumesOfAnotherUser ()
specifier|public
name|void
name|testGetVolumesOfAnotherUser
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testGetVolumesOfAnotherUser
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
comment|/**    * if you try to read volumes belonging to another user,    * then server always ignores it.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testGetVolumesOfAnotherUserShouldFail ()
specifier|public
name|void
name|testGetVolumesOfAnotherUserShouldFail
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testGetVolumesOfAnotherUserShouldFail
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testListKeyOnEmptyBucket ()
specifier|public
name|void
name|testListKeyOnEmptyBucket
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|testListKeyOnEmptyBucket
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

