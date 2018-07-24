begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.volume
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|volume
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|utils
operator|.
name|HddsVolumeUtil
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
name|test
operator|.
name|GenericTestUtils
operator|.
name|LogCapturer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|HDDS_DATANODE_DIR_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|volume
operator|.
name|HddsVolume
operator|.
name|HDDS_VOLUME_DIR
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
name|assertTrue
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
name|fail
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
name|Assert
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
name|File
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  * Tests {@link VolumeSet} operations.  */
end_comment

begin_class
DECL|class|TestVolumeSet
specifier|public
class|class
name|TestVolumeSet
block|{
DECL|field|conf
specifier|private
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|volumeSet
specifier|private
name|VolumeSet
name|volumeSet
decl_stmt|;
DECL|field|baseDir
specifier|private
specifier|final
name|String
name|baseDir
init|=
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
decl_stmt|;
DECL|field|volume1
specifier|private
specifier|final
name|String
name|volume1
init|=
name|baseDir
operator|+
literal|"disk1"
decl_stmt|;
DECL|field|volume2
specifier|private
specifier|final
name|String
name|volume2
init|=
name|baseDir
operator|+
literal|"disk2"
decl_stmt|;
DECL|field|volumes
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|volumes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|DUMMY_IP_ADDR
specifier|private
specifier|static
specifier|final
name|String
name|DUMMY_IP_ADDR
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|method|initializeVolumeSet ()
specifier|private
name|void
name|initializeVolumeSet
parameter_list|()
throws|throws
name|Exception
block|{
name|volumeSet
operator|=
operator|new
name|VolumeSet
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
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
literal|300_000
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|String
name|dataDirKey
init|=
name|volume1
operator|+
literal|","
operator|+
name|volume2
decl_stmt|;
name|volumes
operator|.
name|add
argument_list|(
name|volume1
argument_list|)
expr_stmt|;
name|volumes
operator|.
name|add
argument_list|(
name|volume2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|dataDirKey
argument_list|)
expr_stmt|;
name|initializeVolumeSet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Delete the hdds volume root dir
name|List
argument_list|<
name|HddsVolume
argument_list|>
name|hddsVolumes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|hddsVolumes
operator|.
name|addAll
argument_list|(
name|volumeSet
operator|.
name|getVolumesList
argument_list|()
argument_list|)
expr_stmt|;
name|hddsVolumes
operator|.
name|addAll
argument_list|(
name|volumeSet
operator|.
name|getFailedVolumesList
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|HddsVolume
name|volume
range|:
name|hddsVolumes
control|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|volume
operator|.
name|getHddsRootDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|volumeSet
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|baseDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checkVolumeExistsInVolumeSet (String volume)
specifier|private
name|boolean
name|checkVolumeExistsInVolumeSet
parameter_list|(
name|String
name|volume
parameter_list|)
block|{
for|for
control|(
name|HddsVolume
name|hddsVolume
range|:
name|volumeSet
operator|.
name|getVolumesList
argument_list|()
control|)
block|{
if|if
condition|(
name|hddsVolume
operator|.
name|getHddsRootDir
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|HddsVolumeUtil
operator|.
name|getHddsRoot
argument_list|(
name|volume
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Test
DECL|method|testVolumeSetInitialization ()
specifier|public
name|void
name|testVolumeSetInitialization
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|HddsVolume
argument_list|>
name|volumesList
init|=
name|volumeSet
operator|.
name|getVolumesList
argument_list|()
decl_stmt|;
comment|// VolumeSet initialization should add volume1 and volume2 to VolumeSet
name|assertEquals
argument_list|(
literal|"VolumeSet intialization is incorrect"
argument_list|,
name|volumesList
operator|.
name|size
argument_list|()
argument_list|,
name|volumes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"VolumeSet not initailized correctly"
argument_list|,
name|checkVolumeExistsInVolumeSet
argument_list|(
name|volume1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"VolumeSet not initailized correctly"
argument_list|,
name|checkVolumeExistsInVolumeSet
argument_list|(
name|volume2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddVolume ()
specifier|public
name|void
name|testAddVolume
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|volumeSet
operator|.
name|getVolumesList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add a volume to VolumeSet
name|String
name|volume3
init|=
name|baseDir
operator|+
literal|"disk3"
decl_stmt|;
name|boolean
name|success
init|=
name|volumeSet
operator|.
name|addVolume
argument_list|(
name|volume3
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|success
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|volumeSet
operator|.
name|getVolumesList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"AddVolume did not add requested volume to VolumeSet"
argument_list|,
name|checkVolumeExistsInVolumeSet
argument_list|(
name|volume3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailVolume ()
specifier|public
name|void
name|testFailVolume
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Fail a volume
name|volumeSet
operator|.
name|failVolume
argument_list|(
name|volume1
argument_list|)
expr_stmt|;
comment|// Failed volume should not show up in the volumeList
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|volumeSet
operator|.
name|getVolumesList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Failed volume should be added to FailedVolumeList
name|assertEquals
argument_list|(
literal|"Failed volume not present in FailedVolumeMap"
argument_list|,
literal|1
argument_list|,
name|volumeSet
operator|.
name|getFailedVolumesList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Failed Volume list did not match"
argument_list|,
name|HddsVolumeUtil
operator|.
name|getHddsRoot
argument_list|(
name|volume1
argument_list|)
argument_list|,
name|volumeSet
operator|.
name|getFailedVolumesList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getHddsRootDir
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|volumeSet
operator|.
name|getFailedVolumesList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isFailed
argument_list|()
argument_list|)
expr_stmt|;
comment|// Failed volume should not exist in VolumeMap
name|Path
name|volume1Path
init|=
operator|new
name|Path
argument_list|(
name|volume1
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|volumeSet
operator|.
name|getVolumeMap
argument_list|()
operator|.
name|containsKey
argument_list|(
name|volume1Path
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveVolume ()
specifier|public
name|void
name|testRemoveVolume
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|volumeSet
operator|.
name|getVolumesList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Remove a volume from VolumeSet
name|volumeSet
operator|.
name|removeVolume
argument_list|(
name|volume1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|volumeSet
operator|.
name|getVolumesList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Attempting to remove a volume which does not exist in VolumeSet should
comment|// log a warning.
name|LogCapturer
name|logs
init|=
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|LogFactory
operator|.
name|getLog
argument_list|(
name|VolumeSet
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|volumeSet
operator|.
name|removeVolume
argument_list|(
name|volume1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|volumeSet
operator|.
name|getVolumesList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|expectedLogMessage
init|=
literal|"Volume : "
operator|+
name|HddsVolumeUtil
operator|.
name|getHddsRoot
argument_list|(
name|volume1
argument_list|)
operator|+
literal|" does not exist in VolumeSet"
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Log output does not contain expected log message: "
operator|+
name|expectedLogMessage
argument_list|,
name|logs
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
name|expectedLogMessage
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVolumeInInconsistentState ()
specifier|public
name|void
name|testVolumeInInconsistentState
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|volumeSet
operator|.
name|getVolumesList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add a volume to VolumeSet
name|String
name|volume3
init|=
name|baseDir
operator|+
literal|"disk3"
decl_stmt|;
comment|// Create the root volume dir and create a sub-directory within it.
name|File
name|newVolume
init|=
operator|new
name|File
argument_list|(
name|volume3
argument_list|,
name|HDDS_VOLUME_DIR
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"new volume root: "
operator|+
name|newVolume
argument_list|)
expr_stmt|;
name|newVolume
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to create new volume root"
argument_list|,
name|newVolume
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|newVolume
argument_list|,
literal|"chunks"
argument_list|)
decl_stmt|;
name|dataDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|dataDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// The new volume is in an inconsistent state as the root dir is
comment|// non-empty but the version file does not exist. Add Volume should
comment|// return false.
name|boolean
name|success
init|=
name|volumeSet
operator|.
name|addVolume
argument_list|(
name|volume3
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|success
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|volumeSet
operator|.
name|getVolumesList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"AddVolume should fail for an inconsistent volume"
argument_list|,
operator|!
name|checkVolumeExistsInVolumeSet
argument_list|(
name|volume3
argument_list|)
argument_list|)
expr_stmt|;
comment|// Delete volume3
name|File
name|volume
init|=
operator|new
name|File
argument_list|(
name|volume3
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|volume
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShutdown ()
specifier|public
name|void
name|testShutdown
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|HddsVolume
argument_list|>
name|volumesList
init|=
name|volumeSet
operator|.
name|getVolumesList
argument_list|()
decl_stmt|;
name|volumeSet
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// Verify that the volumes are shutdown and the volumeUsage is set to null.
for|for
control|(
name|HddsVolume
name|volume
range|:
name|volumesList
control|)
block|{
name|Assert
operator|.
name|assertNull
argument_list|(
name|volume
operator|.
name|getVolumeInfo
argument_list|()
operator|.
name|getUsageForTesting
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
comment|// getAvailable() should throw null pointer exception as usage is null.
name|volume
operator|.
name|getAvailable
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Volume shutdown failed."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|ex
parameter_list|)
block|{
comment|// Do Nothing. Exception is expected.
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testFailVolumes ()
specifier|public
name|void
name|testFailVolumes
parameter_list|()
throws|throws
name|Exception
block|{
name|VolumeSet
name|volSet
init|=
literal|null
decl_stmt|;
name|File
name|readOnlyVolumePath
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|)
decl_stmt|;
comment|//Set to readonly, so that this volume will be failed
name|readOnlyVolumePath
operator|.
name|setReadOnly
argument_list|()
expr_stmt|;
name|File
name|volumePath
init|=
name|GenericTestUtils
operator|.
name|getRandomizedTestDir
argument_list|()
decl_stmt|;
name|OzoneConfiguration
name|ozoneConfig
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|ozoneConfig
operator|.
name|set
argument_list|(
name|HDDS_DATANODE_DIR_KEY
argument_list|,
name|readOnlyVolumePath
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|","
operator|+
name|volumePath
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|volSet
operator|=
operator|new
name|VolumeSet
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|ozoneConfig
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|volSet
operator|.
name|getFailedVolumesList
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|readOnlyVolumePath
argument_list|,
name|volSet
operator|.
name|getFailedVolumesList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getHddsRootDir
argument_list|()
argument_list|)
expr_stmt|;
comment|//Set back to writable
try|try
block|{
name|readOnlyVolumePath
operator|.
name|setWritable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|volumePath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

