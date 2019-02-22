begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.gpu
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|resourceplugin
operator|.
name|gpu
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|webapp
operator|.
name|dao
operator|.
name|gpu
operator|.
name|GpuDeviceInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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
name|ExpectedException
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
name|assertNull
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

begin_class
DECL|class|TestGpuDiscoverer
specifier|public
class|class
name|TestGpuDiscoverer
block|{
annotation|@
name|Rule
DECL|field|exception
specifier|public
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
DECL|method|getTestParentFolder ()
specifier|private
name|String
name|getTestParentFolder
parameter_list|()
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
literal|"target/temp/"
operator|+
name|TestGpuDiscoverer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|f
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
DECL|method|touchFile (File f)
specifier|private
name|void
name|touchFile
parameter_list|(
name|File
name|f
parameter_list|)
throws|throws
name|IOException
block|{
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|before ()
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|folder
init|=
name|getTestParentFolder
argument_list|()
decl_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|folder
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|f
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
DECL|method|createConfigWithAllowedDevices (String s)
specifier|private
name|Configuration
name|createConfigWithAllowedDevices
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_GPU_ALLOWED_DEVICES
argument_list|,
name|s
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Test
DECL|method|testLinuxGpuResourceDiscoverPluginConfig ()
specifier|public
name|void
name|testLinuxGpuResourceDiscoverPluginConfig
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Only run this on demand.
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"RunLinuxGpuResourceDiscoverPluginConfigTest"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test case 1, check default setting.
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|GpuDiscoverer
name|plugin
init|=
operator|new
name|GpuDiscoverer
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GpuDiscoverer
operator|.
name|DEFAULT_BINARY_NAME
argument_list|,
name|plugin
operator|.
name|getPathOfGpuBinary
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|plugin
operator|.
name|getEnvironmentToRunCommand
argument_list|()
operator|.
name|get
argument_list|(
literal|"PATH"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|plugin
operator|.
name|getEnvironmentToRunCommand
argument_list|()
operator|.
name|get
argument_list|(
literal|"PATH"
argument_list|)
operator|.
name|contains
argument_list|(
literal|"nvidia"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test case 2, check mandatory set path.
name|File
name|fakeBinary
init|=
operator|new
name|File
argument_list|(
name|getTestParentFolder
argument_list|()
argument_list|,
name|GpuDiscoverer
operator|.
name|DEFAULT_BINARY_NAME
argument_list|)
decl_stmt|;
name|touchFile
argument_list|(
name|fakeBinary
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_GPU_PATH_TO_EXEC
argument_list|,
name|getTestParentFolder
argument_list|()
argument_list|)
expr_stmt|;
name|plugin
operator|=
operator|new
name|GpuDiscoverer
argument_list|()
expr_stmt|;
name|plugin
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fakeBinary
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|plugin
operator|.
name|getPathOfGpuBinary
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|plugin
operator|.
name|getEnvironmentToRunCommand
argument_list|()
operator|.
name|get
argument_list|(
literal|"PATH"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test case 3, check mandatory set path, but binary doesn't exist so default
comment|// path will be used.
name|fakeBinary
operator|.
name|delete
argument_list|()
expr_stmt|;
name|plugin
operator|=
operator|new
name|GpuDiscoverer
argument_list|()
expr_stmt|;
name|plugin
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GpuDiscoverer
operator|.
name|DEFAULT_BINARY_NAME
argument_list|,
name|plugin
operator|.
name|getPathOfGpuBinary
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|plugin
operator|.
name|getEnvironmentToRunCommand
argument_list|()
operator|.
name|get
argument_list|(
literal|"PATH"
argument_list|)
operator|.
name|contains
argument_list|(
literal|"nvidia"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGpuDiscover ()
specifier|public
name|void
name|testGpuDiscover
parameter_list|()
throws|throws
name|YarnException
block|{
comment|// Since this is more of a performance unit test, only run if
comment|// RunUserLimitThroughput is set (-DRunUserLimitThroughput=true)
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"runGpuDiscoverUnitTest"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|GpuDiscoverer
name|plugin
init|=
operator|new
name|GpuDiscoverer
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|GpuDeviceInformation
name|info
init|=
name|plugin
operator|.
name|getGpuDeviceInformation
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|getGpus
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|plugin
operator|.
name|getGpusUsableByYarn
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|info
operator|.
name|getGpus
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNumberOfUsableGpusFromConfigSingleDevice ()
specifier|public
name|void
name|testGetNumberOfUsableGpusFromConfigSingleDevice
parameter_list|()
throws|throws
name|YarnException
block|{
name|Configuration
name|conf
init|=
name|createConfigWithAllowedDevices
argument_list|(
literal|"1:2"
argument_list|)
decl_stmt|;
name|GpuDiscoverer
name|plugin
init|=
operator|new
name|GpuDiscoverer
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|GpuDevice
argument_list|>
name|usableGpuDevices
init|=
name|plugin
operator|.
name|getGpusUsableByYarn
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|usableGpuDevices
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|usableGpuDevices
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|usableGpuDevices
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMinorNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNumberOfUsableGpusFromConfigIllegalFormat ()
specifier|public
name|void
name|testGetNumberOfUsableGpusFromConfigIllegalFormat
parameter_list|()
throws|throws
name|YarnException
block|{
name|Configuration
name|conf
init|=
name|createConfigWithAllowedDevices
argument_list|(
literal|"0:0,1:1,2:2,3"
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|GpuDeviceSpecificationException
operator|.
name|class
argument_list|)
expr_stmt|;
name|GpuDiscoverer
name|plugin
init|=
operator|new
name|GpuDiscoverer
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|getGpusUsableByYarn
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNumberOfUsableGpusFromConfig ()
specifier|public
name|void
name|testGetNumberOfUsableGpusFromConfig
parameter_list|()
throws|throws
name|YarnException
block|{
name|Configuration
name|conf
init|=
name|createConfigWithAllowedDevices
argument_list|(
literal|"0:0,1:1,2:2,3:4"
argument_list|)
decl_stmt|;
name|GpuDiscoverer
name|plugin
init|=
operator|new
name|GpuDiscoverer
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|GpuDevice
argument_list|>
name|usableGpuDevices
init|=
name|plugin
operator|.
name|getGpusUsableByYarn
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|usableGpuDevices
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|usableGpuDevices
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|usableGpuDevices
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMinorNumber
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|usableGpuDevices
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|usableGpuDevices
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getMinorNumber
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|usableGpuDevices
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|usableGpuDevices
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getMinorNumber
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|usableGpuDevices
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|usableGpuDevices
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getMinorNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNumberOfUsableGpusFromConfigDuplicateValues ()
specifier|public
name|void
name|testGetNumberOfUsableGpusFromConfigDuplicateValues
parameter_list|()
throws|throws
name|YarnException
block|{
name|Configuration
name|conf
init|=
name|createConfigWithAllowedDevices
argument_list|(
literal|"0:0,1:1,2:2,1:1"
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|GpuDeviceSpecificationException
operator|.
name|class
argument_list|)
expr_stmt|;
name|GpuDiscoverer
name|plugin
init|=
operator|new
name|GpuDiscoverer
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|getGpusUsableByYarn
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNumberOfUsableGpusFromConfigDuplicateValues2 ()
specifier|public
name|void
name|testGetNumberOfUsableGpusFromConfigDuplicateValues2
parameter_list|()
throws|throws
name|YarnException
block|{
name|Configuration
name|conf
init|=
name|createConfigWithAllowedDevices
argument_list|(
literal|"0:0,1:1,2:2,1:1,2:2"
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|GpuDeviceSpecificationException
operator|.
name|class
argument_list|)
expr_stmt|;
name|GpuDiscoverer
name|plugin
init|=
operator|new
name|GpuDiscoverer
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|getGpusUsableByYarn
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNumberOfUsableGpusFromConfigIncludingSpaces ()
specifier|public
name|void
name|testGetNumberOfUsableGpusFromConfigIncludingSpaces
parameter_list|()
throws|throws
name|YarnException
block|{
name|Configuration
name|conf
init|=
name|createConfigWithAllowedDevices
argument_list|(
literal|"0 : 0,1 : 1"
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|GpuDeviceSpecificationException
operator|.
name|class
argument_list|)
expr_stmt|;
name|GpuDiscoverer
name|plugin
init|=
operator|new
name|GpuDiscoverer
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|getGpusUsableByYarn
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNumberOfUsableGpusFromConfigIncludingGibberish ()
specifier|public
name|void
name|testGetNumberOfUsableGpusFromConfigIncludingGibberish
parameter_list|()
throws|throws
name|YarnException
block|{
name|Configuration
name|conf
init|=
name|createConfigWithAllowedDevices
argument_list|(
literal|"0:@$1,1:1"
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|GpuDeviceSpecificationException
operator|.
name|class
argument_list|)
expr_stmt|;
name|GpuDiscoverer
name|plugin
init|=
operator|new
name|GpuDiscoverer
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|getGpusUsableByYarn
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNumberOfUsableGpusFromConfigIncludingLetters ()
specifier|public
name|void
name|testGetNumberOfUsableGpusFromConfigIncludingLetters
parameter_list|()
throws|throws
name|YarnException
block|{
name|Configuration
name|conf
init|=
name|createConfigWithAllowedDevices
argument_list|(
literal|"x:0, 1:y"
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|GpuDeviceSpecificationException
operator|.
name|class
argument_list|)
expr_stmt|;
name|GpuDiscoverer
name|plugin
init|=
operator|new
name|GpuDiscoverer
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|getGpusUsableByYarn
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNumberOfUsableGpusFromConfigWithoutIndexNumber ()
specifier|public
name|void
name|testGetNumberOfUsableGpusFromConfigWithoutIndexNumber
parameter_list|()
throws|throws
name|YarnException
block|{
name|Configuration
name|conf
init|=
name|createConfigWithAllowedDevices
argument_list|(
literal|":0, :1"
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|GpuDeviceSpecificationException
operator|.
name|class
argument_list|)
expr_stmt|;
name|GpuDiscoverer
name|plugin
init|=
operator|new
name|GpuDiscoverer
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|getGpusUsableByYarn
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNumberOfUsableGpusFromConfigEmptyString ()
specifier|public
name|void
name|testGetNumberOfUsableGpusFromConfigEmptyString
parameter_list|()
throws|throws
name|YarnException
block|{
name|Configuration
name|conf
init|=
name|createConfigWithAllowedDevices
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|GpuDeviceSpecificationException
operator|.
name|class
argument_list|)
expr_stmt|;
name|GpuDiscoverer
name|plugin
init|=
operator|new
name|GpuDiscoverer
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|getGpusUsableByYarn
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNumberOfUsableGpusFromConfigValueWithoutComma ()
specifier|public
name|void
name|testGetNumberOfUsableGpusFromConfigValueWithoutComma
parameter_list|()
throws|throws
name|YarnException
block|{
name|Configuration
name|conf
init|=
name|createConfigWithAllowedDevices
argument_list|(
literal|"0:0 0:1"
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|GpuDeviceSpecificationException
operator|.
name|class
argument_list|)
expr_stmt|;
name|GpuDiscoverer
name|plugin
init|=
operator|new
name|GpuDiscoverer
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|getGpusUsableByYarn
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNumberOfUsableGpusFromConfigValueWithoutComma2 ()
specifier|public
name|void
name|testGetNumberOfUsableGpusFromConfigValueWithoutComma2
parameter_list|()
throws|throws
name|YarnException
block|{
name|Configuration
name|conf
init|=
name|createConfigWithAllowedDevices
argument_list|(
literal|"0.1 0.2"
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|GpuDeviceSpecificationException
operator|.
name|class
argument_list|)
expr_stmt|;
name|GpuDiscoverer
name|plugin
init|=
operator|new
name|GpuDiscoverer
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|getGpusUsableByYarn
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNumberOfUsableGpusFromConfigValueWithoutColonSeparator ()
specifier|public
name|void
name|testGetNumberOfUsableGpusFromConfigValueWithoutColonSeparator
parameter_list|()
throws|throws
name|YarnException
block|{
name|Configuration
name|conf
init|=
name|createConfigWithAllowedDevices
argument_list|(
literal|"0.1,0.2"
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|GpuDeviceSpecificationException
operator|.
name|class
argument_list|)
expr_stmt|;
name|GpuDiscoverer
name|plugin
init|=
operator|new
name|GpuDiscoverer
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|getGpusUsableByYarn
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

