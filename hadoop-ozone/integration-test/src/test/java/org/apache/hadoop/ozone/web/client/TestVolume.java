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
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|lang3
operator|.
name|RandomStringUtils
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
name|hdds
operator|.
name|client
operator|.
name|OzoneQuota
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
name|hdds
operator|.
name|conf
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
name|client
operator|.
name|VolumeArgs
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
name|client
operator|.
name|protocol
operator|.
name|ClientProtocol
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
name|client
operator|.
name|rpc
operator|.
name|RpcClient
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
name|client
operator|.
name|rest
operator|.
name|OzoneException
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
name|client
operator|.
name|OzoneVolume
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
name|utils
operator|.
name|OzoneUtils
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
name|hadoop
operator|.
name|util
operator|.
name|Time
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
name|Assert
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
name|Ignore
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|assertNotNull
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

begin_comment
comment|/**  * Test Ozone Volumes Lifecycle.  */
end_comment

begin_class
DECL|class|TestVolume
specifier|public
class|class
name|TestVolume
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|client
specifier|private
specifier|static
name|ClientProtocol
name|client
init|=
literal|null
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
name|TestVolume
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
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
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
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
name|client
operator|=
operator|new
name|RpcClient
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * shutdown MiniDFSCluster.    */
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
annotation|@
name|Test
DECL|method|testCreateVolume ()
specifier|public
name|void
name|testCreateVolume
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestCreateVolume
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestCreateVolume (ClientProtocol client)
specifier|static
name|void
name|runTestCreateVolume
parameter_list|(
name|ClientProtocol
name|client
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
throws|,
name|ParseException
block|{
name|String
name|volumeName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|long
name|currentTime
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|VolumeArgs
name|volumeArgs
init|=
name|VolumeArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setOwner
argument_list|(
literal|"bilbo"
argument_list|)
operator|.
name|setQuota
argument_list|(
literal|"100TB"
argument_list|)
operator|.
name|setAdmin
argument_list|(
literal|"hdfs"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|client
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|,
name|volumeArgs
argument_list|)
expr_stmt|;
name|OzoneVolume
name|vol
init|=
name|client
operator|.
name|getVolumeDetails
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|vol
operator|.
name|getName
argument_list|()
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|vol
operator|.
name|getAdmin
argument_list|()
argument_list|,
literal|"hdfs"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|vol
operator|.
name|getOwner
argument_list|()
argument_list|,
literal|"bilbo"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|vol
operator|.
name|getQuota
argument_list|()
argument_list|,
name|OzoneQuota
operator|.
name|parseQuota
argument_list|(
literal|"100TB"
argument_list|)
operator|.
name|sizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify the key creation time
name|assertTrue
argument_list|(
operator|(
name|vol
operator|.
name|getCreationTime
argument_list|()
operator|/
literal|1000
operator|)
operator|>=
operator|(
name|currentTime
operator|/
literal|1000
operator|)
argument_list|)
expr_stmt|;
comment|// Test create a volume with invalid volume name,
comment|// not use Rule here because the test method is static.
try|try
block|{
name|String
name|invalidVolumeName
init|=
literal|"#"
operator|+
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|client
operator|.
name|createVolume
argument_list|(
name|invalidVolumeName
argument_list|)
expr_stmt|;
comment|/*       //TODO: RestClient and RpcClient should use HddsClientUtils to verify name       fail("Except the volume creation be failed because the"           + " volume name starts with an invalid char #");*/
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Bucket or Volume name"
operator|+
literal|" has an unsupported character : #"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
throws|,
name|IOException
block|{
name|runTestCreateDuplicateVolume
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestCreateDuplicateVolume (ClientProtocol client)
specifier|static
name|void
name|runTestCreateDuplicateVolume
parameter_list|(
name|ClientProtocol
name|client
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
try|try
block|{
name|client
operator|.
name|createVolume
argument_list|(
literal|"testvol"
argument_list|)
expr_stmt|;
name|client
operator|.
name|createVolume
argument_list|(
literal|"testvol"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Volume creation failed, error:VOLUME_ALREADY_EXISTS"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
throws|,
name|IOException
block|{
name|runTestDeleteVolume
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestDeleteVolume (ClientProtocol client)
specifier|static
name|void
name|runTestDeleteVolume
parameter_list|(
name|ClientProtocol
name|client
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
name|String
name|volumeName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|client
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|client
operator|.
name|deleteVolume
argument_list|(
name|volumeName
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
name|Exception
block|{
name|runTestChangeOwnerOnVolume
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestChangeOwnerOnVolume (ClientProtocol client)
specifier|static
name|void
name|runTestChangeOwnerOnVolume
parameter_list|(
name|ClientProtocol
name|client
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|ParseException
throws|,
name|IOException
block|{
name|String
name|volumeName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|client
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|client
operator|.
name|getVolumeDetails
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|client
operator|.
name|setVolumeOwner
argument_list|(
name|volumeName
argument_list|,
literal|"frodo"
argument_list|)
expr_stmt|;
name|OzoneVolume
name|newVol
init|=
name|client
operator|.
name|getVolumeDetails
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|newVol
operator|.
name|getOwner
argument_list|()
argument_list|,
literal|"frodo"
argument_list|)
expr_stmt|;
comment|// verify if the creation time is missing after setting owner operation
name|assertTrue
argument_list|(
name|newVol
operator|.
name|getCreationTime
argument_list|()
operator|>
literal|0
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
name|Exception
block|{
name|runTestChangeQuotaOnVolume
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestChangeQuotaOnVolume (ClientProtocol client)
specifier|static
name|void
name|runTestChangeQuotaOnVolume
parameter_list|(
name|ClientProtocol
name|client
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
throws|,
name|ParseException
block|{
name|String
name|volumeName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|client
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|client
operator|.
name|setVolumeQuota
argument_list|(
name|volumeName
argument_list|,
name|OzoneQuota
operator|.
name|parseQuota
argument_list|(
literal|"1000MB"
argument_list|)
argument_list|)
expr_stmt|;
name|OzoneVolume
name|newVol
init|=
name|client
operator|.
name|getVolumeDetails
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|newVol
operator|.
name|getQuota
argument_list|()
argument_list|,
name|OzoneQuota
operator|.
name|parseQuota
argument_list|(
literal|"1000MB"
argument_list|)
operator|.
name|sizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify if the creation time is missing after setting quota operation
name|assertTrue
argument_list|(
name|newVol
operator|.
name|getCreationTime
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
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
name|runTestListVolume
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestListVolume (ClientProtocol client)
specifier|static
name|void
name|runTestListVolume
parameter_list|(
name|ClientProtocol
name|client
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|10
condition|;
name|x
operator|++
control|)
block|{
name|String
name|volumeName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|client
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|OzoneVolume
argument_list|>
name|ovols
init|=
name|client
operator|.
name|listVolumes
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ovols
operator|.
name|size
argument_list|()
operator|>=
literal|10
argument_list|)
expr_stmt|;
block|}
comment|// TODO: remove @Ignore below once the problem has been resolved.
annotation|@
name|Ignore
argument_list|(
literal|"Takes 3m to run, disable for now."
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
name|runTestListVolumePagination
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestListVolumePagination (ClientProtocol client)
specifier|static
name|void
name|runTestListVolumePagination
parameter_list|(
name|ClientProtocol
name|client
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
specifier|final
name|int
name|volCount
init|=
literal|2000
decl_stmt|;
specifier|final
name|int
name|step
init|=
literal|100
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|volCount
condition|;
name|x
operator|++
control|)
block|{
name|String
name|volumeName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|client
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
block|}
name|String
name|prevKey
init|=
literal|null
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|int
name|pagecount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|count
operator|<
name|volCount
condition|)
block|{
name|List
argument_list|<
name|OzoneVolume
argument_list|>
name|ovols
init|=
name|client
operator|.
name|listVolumes
argument_list|(
literal|null
argument_list|,
name|prevKey
argument_list|,
name|step
argument_list|)
decl_stmt|;
name|count
operator|+=
name|ovols
operator|.
name|size
argument_list|()
expr_stmt|;
name|prevKey
operator|=
name|ovols
operator|.
name|get
argument_list|(
name|ovols
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|getName
argument_list|()
expr_stmt|;
name|pagecount
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|volCount
operator|/
name|step
argument_list|,
name|pagecount
argument_list|)
expr_stmt|;
block|}
comment|// TODO: remove @Ignore below once the problem has been resolved.
annotation|@
name|Ignore
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
name|runTestListAllVolumes
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestListAllVolumes (ClientProtocol client)
specifier|static
name|void
name|runTestListAllVolumes
parameter_list|(
name|ClientProtocol
name|client
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
specifier|final
name|int
name|volCount
init|=
literal|200
decl_stmt|;
specifier|final
name|int
name|step
init|=
literal|10
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|volCount
condition|;
name|x
operator|++
control|)
block|{
name|String
name|userName
init|=
literal|"frodo"
operator|+
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|5
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|String
name|volumeName
init|=
literal|"vol"
operator|+
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|5
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|VolumeArgs
name|volumeArgs
init|=
name|VolumeArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setOwner
argument_list|(
name|userName
argument_list|)
operator|.
name|setQuota
argument_list|(
literal|"100TB"
argument_list|)
operator|.
name|setAdmin
argument_list|(
literal|"hdfs"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|client
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|,
name|volumeArgs
argument_list|)
expr_stmt|;
name|OzoneVolume
name|vol
init|=
name|client
operator|.
name|getVolumeDetails
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|vol
argument_list|)
expr_stmt|;
block|}
name|String
name|prevKey
init|=
literal|null
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|int
name|pagecount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|count
operator|<
name|volCount
condition|)
block|{
name|List
argument_list|<
name|OzoneVolume
argument_list|>
name|ovols
init|=
name|client
operator|.
name|listVolumes
argument_list|(
literal|null
argument_list|,
name|prevKey
argument_list|,
name|step
argument_list|)
decl_stmt|;
name|count
operator|+=
name|ovols
operator|.
name|size
argument_list|()
expr_stmt|;
if|if
condition|(
name|ovols
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|prevKey
operator|=
name|ovols
operator|.
name|get
argument_list|(
name|ovols
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
name|pagecount
operator|++
expr_stmt|;
block|}
comment|// becasue we are querying an existing ozone store, there will
comment|// be volumes created by other tests too. So we should get more page counts.
name|assertEquals
argument_list|(
name|volCount
operator|/
name|step
argument_list|,
name|pagecount
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
name|Exception
block|{
name|runTestListVolumes
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestListVolumes (ClientProtocol client)
specifier|static
name|void
name|runTestListVolumes
parameter_list|(
name|ClientProtocol
name|client
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
throws|,
name|ParseException
block|{
specifier|final
name|int
name|volCount
init|=
literal|20
decl_stmt|;
specifier|final
name|String
name|user1
init|=
literal|"test-user-a"
decl_stmt|;
specifier|final
name|String
name|user2
init|=
literal|"test-user-b"
decl_stmt|;
name|long
name|currentTime
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
comment|// Create 20 volumes, 10 for user1 and another 10 for user2.
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|volCount
condition|;
name|x
operator|++
control|)
block|{
name|String
name|volumeName
decl_stmt|;
name|String
name|userName
decl_stmt|;
if|if
condition|(
name|x
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
comment|// create volume [test-vol0, test-vol2, ..., test-vol18] for user1
name|userName
operator|=
name|user1
expr_stmt|;
name|volumeName
operator|=
literal|"test-vol"
operator|+
name|x
expr_stmt|;
block|}
else|else
block|{
comment|// create volume [test-vol1, test-vol3, ..., test-vol19] for user2
name|userName
operator|=
name|user2
expr_stmt|;
name|volumeName
operator|=
literal|"test-vol"
operator|+
name|x
expr_stmt|;
block|}
name|VolumeArgs
name|volumeArgs
init|=
name|VolumeArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setOwner
argument_list|(
name|userName
argument_list|)
operator|.
name|setQuota
argument_list|(
literal|"100TB"
argument_list|)
operator|.
name|setAdmin
argument_list|(
literal|"hdfs"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|client
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|,
name|volumeArgs
argument_list|)
expr_stmt|;
name|OzoneVolume
name|vol
init|=
name|client
operator|.
name|getVolumeDetails
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|vol
argument_list|)
expr_stmt|;
block|}
comment|// list all the volumes belong to user1
name|List
argument_list|<
name|OzoneVolume
argument_list|>
name|volumeList
init|=
name|client
operator|.
name|listVolumes
argument_list|(
name|user1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|volumeList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify the owner name and creation time of volume
for|for
control|(
name|OzoneVolume
name|vol
range|:
name|volumeList
control|)
block|{
name|assertTrue
argument_list|(
name|vol
operator|.
name|getOwner
argument_list|()
operator|.
name|equals
argument_list|(
name|user1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
name|vol
operator|.
name|getCreationTime
argument_list|()
operator|/
literal|1000
operator|)
operator|>=
operator|(
name|currentTime
operator|/
literal|1000
operator|)
argument_list|)
expr_stmt|;
block|}
comment|// test max key parameter of listing volumes
name|volumeList
operator|=
name|client
operator|.
name|listVolumes
argument_list|(
name|user1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|volumeList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// test prefix parameter of listing volumes
name|volumeList
operator|=
name|client
operator|.
name|listVolumes
argument_list|(
name|user1
argument_list|,
literal|"test-vol10"
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|volumeList
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|volumeList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"test-vol10"
argument_list|)
argument_list|)
expr_stmt|;
name|volumeList
operator|=
name|client
operator|.
name|listVolumes
argument_list|(
name|user1
argument_list|,
literal|"test-vol1"
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|volumeList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// test start key parameter of listing volumes
name|volumeList
operator|=
name|client
operator|.
name|listVolumes
argument_list|(
name|user2
argument_list|,
literal|null
argument_list|,
literal|"test-vol15"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|volumeList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

