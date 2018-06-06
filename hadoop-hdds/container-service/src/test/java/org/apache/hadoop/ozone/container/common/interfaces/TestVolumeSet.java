begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.interfaces
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
name|interfaces
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
name|impl
operator|.
name|VolumeInfo
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
name|impl
operator|.
name|VolumeSet
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
specifier|protected
name|VolumeSet
name|volumeSet
decl_stmt|;
DECL|field|baseDir
specifier|protected
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
specifier|protected
specifier|final
name|String
name|volume1
init|=
name|baseDir
operator|+
literal|"disk1"
decl_stmt|;
DECL|field|volume2
specifier|protected
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
name|VolumeInfo
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
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|VolumeInfo
argument_list|>
name|volumesList
init|=
name|volumeSet
operator|.
name|getVolumesList
argument_list|()
decl_stmt|;
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
name|volumeSet
operator|.
name|addVolume
argument_list|(
name|volume3
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
name|volume1
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
name|getRootDir
argument_list|()
operator|.
name|toString
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
name|List
argument_list|<
name|VolumeInfo
argument_list|>
name|volumesList
init|=
name|volumeSet
operator|.
name|getVolumesList
argument_list|()
decl_stmt|;
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
name|volume1
operator|+
literal|" does not exist in "
operator|+
literal|"VolumeSet"
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
name|VolumeInfo
name|volumeInfo
range|:
name|volumeSet
operator|.
name|getVolumesList
argument_list|()
control|)
block|{
if|if
condition|(
name|volumeInfo
operator|.
name|getRootDir
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|volume
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
block|}
end_class

end_unit

