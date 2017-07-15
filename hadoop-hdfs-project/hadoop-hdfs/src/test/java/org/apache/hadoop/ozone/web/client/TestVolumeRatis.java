begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.client
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
operator|.
name|client
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
name|ozone
operator|.
name|RatisTestHelper
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
name|web
operator|.
name|exceptions
operator|.
name|OzoneException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|*
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
comment|/** The same as {@link TestVolume} except that this test is Ratis enabled. */
end_comment

begin_class
DECL|class|TestVolumeRatis
specifier|public
class|class
name|TestVolumeRatis
block|{
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
DECL|field|suite
specifier|private
specifier|static
name|RatisTestHelper
operator|.
name|RatisTestSuite
name|suite
decl_stmt|;
DECL|field|ozoneClient
specifier|private
specifier|static
name|OzoneRestClient
name|ozoneClient
decl_stmt|;
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
name|suite
operator|=
operator|new
name|RatisTestHelper
operator|.
name|RatisTestSuite
argument_list|(
name|TestVolumeRatis
operator|.
name|class
argument_list|)
expr_stmt|;
name|ozoneClient
operator|=
name|suite
operator|.
name|newOzoneRestClient
argument_list|()
expr_stmt|;
block|}
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
name|suite
operator|!=
literal|null
condition|)
block|{
name|suite
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCreateVolume ()
specifier|public
name|void
name|testCreateVolume
parameter_list|()
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
name|TestVolume
operator|.
name|runTestCreateVolume
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateDuplicateVolume ()
specifier|public
name|void
name|testCreateDuplicateVolume
parameter_list|()
throws|throws
name|OzoneException
block|{
name|TestVolume
operator|.
name|runTestCreateDuplicateVolume
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteVolume ()
specifier|public
name|void
name|testDeleteVolume
parameter_list|()
throws|throws
name|OzoneException
block|{
name|TestVolume
operator|.
name|runTestDeleteVolume
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChangeOwnerOnVolume ()
specifier|public
name|void
name|testChangeOwnerOnVolume
parameter_list|()
throws|throws
name|OzoneException
block|{
name|TestVolume
operator|.
name|runTestChangeOwnerOnVolume
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChangeQuotaOnVolume ()
specifier|public
name|void
name|testChangeQuotaOnVolume
parameter_list|()
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
name|TestVolume
operator|.
name|runTestChangeQuotaOnVolume
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
block|}
comment|// TODO: remove @Ignore below once the problem has been resolved.
annotation|@
name|Ignore
argument_list|(
literal|"listVolumes not implemented in DistributedStorageHandler"
argument_list|)
annotation|@
name|Test
DECL|method|testListVolume ()
specifier|public
name|void
name|testListVolume
parameter_list|()
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
name|TestVolume
operator|.
name|runTestListVolume
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
block|}
comment|// TODO: remove @Ignore below once the problem has been resolved.
annotation|@
name|Ignore
argument_list|(
literal|"See TestVolume.testListVolumePagination()"
argument_list|)
annotation|@
name|Test
DECL|method|testListVolumePagination ()
specifier|public
name|void
name|testListVolumePagination
parameter_list|()
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
name|TestVolume
operator|.
name|runTestListVolumePagination
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
block|}
comment|// TODO: remove @Ignore below once the problem has been resolved.
annotation|@
name|Ignore
argument_list|(
literal|"See TestVolume.testListAllVolumes()"
argument_list|)
annotation|@
name|Test
DECL|method|testListAllVolumes ()
specifier|public
name|void
name|testListAllVolumes
parameter_list|()
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
name|TestVolume
operator|.
name|runTestListAllVolumes
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testListVolumes ()
specifier|public
name|void
name|testListVolumes
parameter_list|()
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
name|TestVolume
operator|.
name|runTestListVolumes
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

