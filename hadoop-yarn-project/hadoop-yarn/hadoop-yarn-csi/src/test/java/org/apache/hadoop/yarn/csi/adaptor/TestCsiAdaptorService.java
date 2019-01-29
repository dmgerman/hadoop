begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.csi.adaptor
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|csi
operator|.
name|adaptor
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|csi
operator|.
name|v0
operator|.
name|Csi
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
name|net
operator|.
name|NetUtils
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
name|service
operator|.
name|ServiceStateException
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
name|yarn
operator|.
name|api
operator|.
name|CsiAdaptorProtocol
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
name|api
operator|.
name|CsiAdaptorPlugin
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
name|api
operator|.
name|impl
operator|.
name|pb
operator|.
name|client
operator|.
name|CsiAdaptorProtocolPBClientImpl
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetPluginInfoRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetPluginInfoResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|NodePublishVolumeRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|NodePublishVolumeResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|NodeUnpublishVolumeRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|NodeUnpublishVolumeResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|ValidateVolumeCapabilitiesRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|ValidateVolumeCapabilitiesResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|ValidateVolumeCapabilitiesRequestPBImpl
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
name|client
operator|.
name|NMProxy
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
name|ipc
operator|.
name|YarnRPC
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|ValidateVolumeCapabilitiesRequest
operator|.
name|AccessMode
operator|.
name|MULTI_NODE_MULTI_WRITER
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|ValidateVolumeCapabilitiesRequest
operator|.
name|VolumeType
operator|.
name|FILE_SYSTEM
import|;
end_import

begin_comment
comment|/**  * UT for {@link CsiAdaptorProtocolService}.  */
end_comment

begin_class
DECL|class|TestCsiAdaptorService
specifier|public
class|class
name|TestCsiAdaptorService
block|{
DECL|field|testRoot
specifier|private
specifier|static
name|File
name|testRoot
init|=
literal|null
decl_stmt|;
DECL|field|domainSocket
specifier|private
specifier|static
name|String
name|domainSocket
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
name|testRoot
operator|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
literal|"csi-test"
argument_list|)
expr_stmt|;
name|File
name|socketPath
init|=
operator|new
name|File
argument_list|(
name|testRoot
argument_list|,
literal|"csi.sock"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|forceMkdirParent
argument_list|(
name|socketPath
argument_list|)
expr_stmt|;
name|domainSocket
operator|=
literal|"unix://"
operator|+
name|socketPath
operator|.
name|getAbsolutePath
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
throws|throws
name|IOException
block|{
if|if
condition|(
name|testRoot
operator|!=
literal|null
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|testRoot
argument_list|)
expr_stmt|;
block|}
block|}
DECL|interface|FakeCsiAdaptor
specifier|private
interface|interface
name|FakeCsiAdaptor
extends|extends
name|CsiAdaptorPlugin
block|{
DECL|method|init (String driverName, Configuration conf)
specifier|default
name|void
name|init
parameter_list|(
name|String
name|driverName
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|YarnException
block|{
return|return;
block|}
DECL|method|getDriverName ()
specifier|default
name|String
name|getDriverName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|getPluginInfo (GetPluginInfoRequest request)
specifier|default
name|GetPluginInfoResponse
name|getPluginInfo
parameter_list|(
name|GetPluginInfoRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
DECL|method|validateVolumeCapacity ( ValidateVolumeCapabilitiesRequest request)
specifier|default
name|ValidateVolumeCapabilitiesResponse
name|validateVolumeCapacity
parameter_list|(
name|ValidateVolumeCapabilitiesRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
DECL|method|nodePublishVolume ( NodePublishVolumeRequest request)
specifier|default
name|NodePublishVolumeResponse
name|nodePublishVolume
parameter_list|(
name|NodePublishVolumeRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
DECL|method|nodeUnpublishVolume ( NodeUnpublishVolumeRequest request)
specifier|default
name|NodeUnpublishVolumeResponse
name|nodeUnpublishVolume
parameter_list|(
name|NodeUnpublishVolumeRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testValidateVolume ()
specifier|public
name|void
name|testValidateVolume
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|ServerSocket
name|ss
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ss
operator|.
name|close
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|address
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|ss
operator|.
name|getLocalPort
argument_list|()
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSI_ADAPTOR_PREFIX
operator|+
literal|"test-driver.address"
argument_list|,
name|address
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSI_DRIVER_PREFIX
operator|+
literal|"test-driver.endpoint"
argument_list|,
literal|"unix:///tmp/test-driver.sock"
argument_list|)
expr_stmt|;
comment|// inject a fake CSI adaptor
comment|// this client validates if the ValidateVolumeCapabilitiesRequest
comment|// is integrity, and then reply a fake response
name|CsiAdaptorPlugin
name|plugin
init|=
operator|new
name|FakeCsiAdaptor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getDriverName
parameter_list|()
block|{
return|return
literal|"test-driver"
return|;
block|}
annotation|@
name|Override
specifier|public
name|GetPluginInfoResponse
name|getPluginInfo
parameter_list|(
name|GetPluginInfoRequest
name|request
parameter_list|)
block|{
return|return
name|GetPluginInfoResponse
operator|.
name|newInstance
argument_list|(
literal|"test-plugin"
argument_list|,
literal|"0.1"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ValidateVolumeCapabilitiesResponse
name|validateVolumeCapacity
parameter_list|(
name|ValidateVolumeCapabilitiesRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
comment|// validate we get all info from the request
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"volume-id-0000123"
argument_list|,
name|request
operator|.
name|getVolumeId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|request
operator|.
name|getVolumeCapabilities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Csi
operator|.
name|VolumeCapability
operator|.
name|AccessMode
operator|.
name|newBuilder
argument_list|()
operator|.
name|setModeValue
argument_list|(
literal|5
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|getMode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|request
operator|.
name|getVolumeCapabilities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAccessMode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|request
operator|.
name|getVolumeCapabilities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMountFlags
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getVolumeCapabilities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMountFlags
argument_list|()
operator|.
name|contains
argument_list|(
literal|"mountFlag1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getVolumeCapabilities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMountFlags
argument_list|()
operator|.
name|contains
argument_list|(
literal|"mountFlag2"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|request
operator|.
name|getVolumeAttributes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"v1"
argument_list|,
name|request
operator|.
name|getVolumeAttributes
argument_list|()
operator|.
name|get
argument_list|(
literal|"k1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"v2"
argument_list|,
name|request
operator|.
name|getVolumeAttributes
argument_list|()
operator|.
name|get
argument_list|(
literal|"k2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// return a fake result
return|return
name|ValidateVolumeCapabilitiesResponse
operator|.
name|newInstance
argument_list|(
literal|false
argument_list|,
literal|"this is a test"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|CsiAdaptorProtocolService
name|service
init|=
operator|new
name|CsiAdaptorProtocolService
argument_list|(
name|plugin
argument_list|)
decl_stmt|;
name|service
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
init|(
name|CsiAdaptorProtocolPBClientImpl
name|client
init|=
operator|new
name|CsiAdaptorProtocolPBClientImpl
argument_list|(
literal|1L
argument_list|,
name|address
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
init|)
block|{
name|ValidateVolumeCapabilitiesRequest
name|request
init|=
name|ValidateVolumeCapabilitiesRequestPBImpl
operator|.
name|newInstance
argument_list|(
literal|"volume-id-0000123"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|ValidateVolumeCapabilitiesRequest
operator|.
name|VolumeCapability
argument_list|(
name|MULTI_NODE_MULTI_WRITER
argument_list|,
name|FILE_SYSTEM
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"mountFlag1"
argument_list|,
literal|"mountFlag2"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"k1"
argument_list|,
literal|"v1"
argument_list|,
literal|"k2"
argument_list|,
literal|"v2"
argument_list|)
argument_list|)
decl_stmt|;
name|ValidateVolumeCapabilitiesResponse
name|response
init|=
name|client
operator|.
name|validateVolumeCapacity
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|response
operator|.
name|isSupported
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"this is a test"
argument_list|,
name|response
operator|.
name|getResponseMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testValidateVolumeWithNMProxy ()
specifier|public
name|void
name|testValidateVolumeWithNMProxy
parameter_list|()
throws|throws
name|Exception
block|{
name|ServerSocket
name|ss
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ss
operator|.
name|close
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|address
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|ss
operator|.
name|getLocalPort
argument_list|()
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSI_ADAPTOR_PREFIX
operator|+
literal|"test-driver.address"
argument_list|,
name|address
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSI_DRIVER_PREFIX
operator|+
literal|"test-driver.endpoint"
argument_list|,
literal|"unix:///tmp/test-driver.sock"
argument_list|)
expr_stmt|;
comment|// inject a fake CSI adaptor
comment|// this client validates if the ValidateVolumeCapabilitiesRequest
comment|// is integrity, and then reply a fake response
name|FakeCsiAdaptor
name|plugin
init|=
operator|new
name|FakeCsiAdaptor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getDriverName
parameter_list|()
block|{
return|return
literal|"test-driver"
return|;
block|}
annotation|@
name|Override
specifier|public
name|GetPluginInfoResponse
name|getPluginInfo
parameter_list|(
name|GetPluginInfoRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|GetPluginInfoResponse
operator|.
name|newInstance
argument_list|(
literal|"test-plugin"
argument_list|,
literal|"0.1"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ValidateVolumeCapabilitiesResponse
name|validateVolumeCapacity
parameter_list|(
name|ValidateVolumeCapabilitiesRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
comment|// validate we get all info from the request
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"volume-id-0000123"
argument_list|,
name|request
operator|.
name|getVolumeId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|request
operator|.
name|getVolumeCapabilities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Csi
operator|.
name|VolumeCapability
operator|.
name|AccessMode
operator|.
name|newBuilder
argument_list|()
operator|.
name|setModeValue
argument_list|(
literal|5
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|getMode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|request
operator|.
name|getVolumeCapabilities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAccessMode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|request
operator|.
name|getVolumeCapabilities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMountFlags
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getVolumeCapabilities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMountFlags
argument_list|()
operator|.
name|contains
argument_list|(
literal|"mountFlag1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getVolumeCapabilities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMountFlags
argument_list|()
operator|.
name|contains
argument_list|(
literal|"mountFlag2"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|request
operator|.
name|getVolumeAttributes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"v1"
argument_list|,
name|request
operator|.
name|getVolumeAttributes
argument_list|()
operator|.
name|get
argument_list|(
literal|"k1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"v2"
argument_list|,
name|request
operator|.
name|getVolumeAttributes
argument_list|()
operator|.
name|get
argument_list|(
literal|"k2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// return a fake result
return|return
name|ValidateVolumeCapabilitiesResponse
operator|.
name|newInstance
argument_list|(
literal|false
argument_list|,
literal|"this is a test"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|CsiAdaptorProtocolService
name|service
init|=
operator|new
name|CsiAdaptorProtocolService
argument_list|(
name|plugin
argument_list|)
decl_stmt|;
name|service
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|currentUser
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|CsiAdaptorProtocol
name|adaptorClient
init|=
name|NMProxy
operator|.
name|createNMProxy
argument_list|(
name|conf
argument_list|,
name|CsiAdaptorProtocol
operator|.
name|class
argument_list|,
name|currentUser
argument_list|,
name|rpc
argument_list|,
name|NetUtils
operator|.
name|createSocketAddrForHost
argument_list|(
literal|"localhost"
argument_list|,
name|ss
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ValidateVolumeCapabilitiesRequest
name|request
init|=
name|ValidateVolumeCapabilitiesRequestPBImpl
operator|.
name|newInstance
argument_list|(
literal|"volume-id-0000123"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|ValidateVolumeCapabilitiesRequest
operator|.
name|VolumeCapability
argument_list|(
name|MULTI_NODE_MULTI_WRITER
argument_list|,
name|FILE_SYSTEM
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"mountFlag1"
argument_list|,
literal|"mountFlag2"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"k1"
argument_list|,
literal|"v1"
argument_list|,
literal|"k2"
argument_list|,
literal|"v2"
argument_list|)
argument_list|)
decl_stmt|;
name|ValidateVolumeCapabilitiesResponse
name|response
init|=
name|adaptorClient
operator|.
name|validateVolumeCapacity
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|response
operator|.
name|isSupported
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"this is a test"
argument_list|,
name|response
operator|.
name|getResponseMessage
argument_list|()
argument_list|)
expr_stmt|;
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ServiceStateException
operator|.
name|class
argument_list|)
DECL|method|testMissingConfiguration ()
specifier|public
name|void
name|testMissingConfiguration
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|CsiAdaptorProtocolService
name|service
init|=
operator|new
name|CsiAdaptorProtocolService
argument_list|(
operator|new
name|FakeCsiAdaptor
argument_list|()
block|{}
argument_list|)
decl_stmt|;
name|service
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ServiceStateException
operator|.
name|class
argument_list|)
DECL|method|testInvalidServicePort ()
specifier|public
name|void
name|testInvalidServicePort
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSI_ADAPTOR_PREFIX
operator|+
literal|"test-driver-0001.address"
argument_list|,
literal|"0.0.0.0:-100"
argument_list|)
expr_stmt|;
comment|// this is an invalid address
name|CsiAdaptorProtocolService
name|service
init|=
operator|new
name|CsiAdaptorProtocolService
argument_list|(
operator|new
name|FakeCsiAdaptor
argument_list|()
block|{}
argument_list|)
decl_stmt|;
name|service
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ServiceStateException
operator|.
name|class
argument_list|)
DECL|method|testInvalidHost ()
specifier|public
name|void
name|testInvalidHost
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSI_ADAPTOR_PREFIX
operator|+
literal|"test-driver-0001.address"
argument_list|,
literal|"192.0.1:8999"
argument_list|)
expr_stmt|;
comment|// this is an invalid ip address
name|CsiAdaptorProtocolService
name|service
init|=
operator|new
name|CsiAdaptorProtocolService
argument_list|(
operator|new
name|FakeCsiAdaptor
argument_list|()
block|{}
argument_list|)
decl_stmt|;
name|service
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCustomizedAdaptor ()
specifier|public
name|void
name|testCustomizedAdaptor
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|ServerSocket
name|ss
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ss
operator|.
name|close
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|address
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|ss
operator|.
name|getLocalPort
argument_list|()
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSI_DRIVER_NAMES
argument_list|,
literal|"customized-driver"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSI_ADAPTOR_PREFIX
operator|+
literal|"customized-driver.address"
argument_list|,
name|address
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSI_ADAPTOR_PREFIX
operator|+
literal|"customized-driver.class"
argument_list|,
literal|"org.apache.hadoop.yarn.csi.adaptor.MockCsiAdaptor"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSI_DRIVER_PREFIX
operator|+
literal|"customized-driver.endpoint"
argument_list|,
literal|"unix:///tmp/customized-driver.sock"
argument_list|)
expr_stmt|;
name|CsiAdaptorServices
name|services
init|=
operator|new
name|CsiAdaptorServices
argument_list|()
decl_stmt|;
name|services
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|services
operator|.
name|start
argument_list|()
expr_stmt|;
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|currentUser
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|CsiAdaptorProtocol
name|adaptorClient
init|=
name|NMProxy
operator|.
name|createNMProxy
argument_list|(
name|conf
argument_list|,
name|CsiAdaptorProtocol
operator|.
name|class
argument_list|,
name|currentUser
argument_list|,
name|rpc
argument_list|,
name|NetUtils
operator|.
name|createSocketAddrForHost
argument_list|(
literal|"localhost"
argument_list|,
name|ss
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// Test getPluginInfo
name|GetPluginInfoResponse
name|pluginInfo
init|=
name|adaptorClient
operator|.
name|getPluginInfo
argument_list|(
name|GetPluginInfoRequest
operator|.
name|newInstance
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pluginInfo
operator|.
name|getDriverName
argument_list|()
argument_list|,
literal|"customized-driver"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pluginInfo
operator|.
name|getVersion
argument_list|()
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
comment|// Test validateVolumeCapacity
name|ValidateVolumeCapabilitiesRequest
name|request
init|=
name|ValidateVolumeCapabilitiesRequestPBImpl
operator|.
name|newInstance
argument_list|(
literal|"volume-id-0000123"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|ValidateVolumeCapabilitiesRequest
operator|.
name|VolumeCapability
argument_list|(
name|MULTI_NODE_MULTI_WRITER
argument_list|,
name|FILE_SYSTEM
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"mountFlag1"
argument_list|,
literal|"mountFlag2"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"k1"
argument_list|,
literal|"v1"
argument_list|,
literal|"k2"
argument_list|,
literal|"v2"
argument_list|)
argument_list|)
decl_stmt|;
name|ValidateVolumeCapabilitiesResponse
name|response
init|=
name|adaptorClient
operator|.
name|validateVolumeCapacity
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|response
operator|.
name|isSupported
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"verified via MockCsiAdaptor"
argument_list|,
name|response
operator|.
name|getResponseMessage
argument_list|()
argument_list|)
expr_stmt|;
name|services
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleCsiAdaptors ()
specifier|public
name|void
name|testMultipleCsiAdaptors
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|ServerSocket
name|driver1Addr
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ServerSocket
name|driver2Addr
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|address1
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|driver1Addr
operator|.
name|getLocalPort
argument_list|()
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|address2
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|driver2Addr
operator|.
name|getLocalPort
argument_list|()
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// Two csi-drivers configured
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSI_DRIVER_NAMES
argument_list|,
literal|"customized-driver-1,customized-driver-2"
argument_list|)
expr_stmt|;
comment|// customized-driver-1
name|conf
operator|.
name|setSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSI_ADAPTOR_PREFIX
operator|+
literal|"customized-driver-1.address"
argument_list|,
name|address1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSI_ADAPTOR_PREFIX
operator|+
literal|"customized-driver-1.class"
argument_list|,
literal|"org.apache.hadoop.yarn.csi.adaptor.MockCsiAdaptor"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSI_DRIVER_PREFIX
operator|+
literal|"customized-driver-1.endpoint"
argument_list|,
literal|"unix:///tmp/customized-driver-1.sock"
argument_list|)
expr_stmt|;
comment|// customized-driver-2
name|conf
operator|.
name|setSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSI_ADAPTOR_PREFIX
operator|+
literal|"customized-driver-2.address"
argument_list|,
name|address2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSI_ADAPTOR_PREFIX
operator|+
literal|"customized-driver-2.class"
argument_list|,
literal|"org.apache.hadoop.yarn.csi.adaptor.MockCsiAdaptor"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CSI_DRIVER_PREFIX
operator|+
literal|"customized-driver-2.endpoint"
argument_list|,
literal|"unix:///tmp/customized-driver-2.sock"
argument_list|)
expr_stmt|;
name|driver1Addr
operator|.
name|close
argument_list|()
expr_stmt|;
name|driver2Addr
operator|.
name|close
argument_list|()
expr_stmt|;
name|CsiAdaptorServices
name|services
init|=
operator|new
name|CsiAdaptorServices
argument_list|()
decl_stmt|;
name|services
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|services
operator|.
name|start
argument_list|()
expr_stmt|;
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|currentUser
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|CsiAdaptorProtocol
name|client1
init|=
name|NMProxy
operator|.
name|createNMProxy
argument_list|(
name|conf
argument_list|,
name|CsiAdaptorProtocol
operator|.
name|class
argument_list|,
name|currentUser
argument_list|,
name|rpc
argument_list|,
name|NetUtils
operator|.
name|createSocketAddrForHost
argument_list|(
literal|"localhost"
argument_list|,
name|driver1Addr
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// ***************************************************
comment|// Verify talking with customized-driver-1
comment|// ***************************************************
comment|// Test getPluginInfo
name|GetPluginInfoResponse
name|pluginInfo
init|=
name|client1
operator|.
name|getPluginInfo
argument_list|(
name|GetPluginInfoRequest
operator|.
name|newInstance
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pluginInfo
operator|.
name|getDriverName
argument_list|()
argument_list|,
literal|"customized-driver-1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pluginInfo
operator|.
name|getVersion
argument_list|()
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
comment|// Test validateVolumeCapacity
name|ValidateVolumeCapabilitiesRequest
name|request
init|=
name|ValidateVolumeCapabilitiesRequestPBImpl
operator|.
name|newInstance
argument_list|(
literal|"driver-1-volume-00001"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|ValidateVolumeCapabilitiesRequest
operator|.
name|VolumeCapability
argument_list|(
name|MULTI_NODE_MULTI_WRITER
argument_list|,
name|FILE_SYSTEM
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|ValidateVolumeCapabilitiesResponse
name|response
init|=
name|client1
operator|.
name|validateVolumeCapacity
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|response
operator|.
name|isSupported
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"verified via MockCsiAdaptor"
argument_list|,
name|response
operator|.
name|getResponseMessage
argument_list|()
argument_list|)
expr_stmt|;
comment|// ***************************************************
comment|// Verify talking with customized-driver-2
comment|// ***************************************************
name|CsiAdaptorProtocol
name|client2
init|=
name|NMProxy
operator|.
name|createNMProxy
argument_list|(
name|conf
argument_list|,
name|CsiAdaptorProtocol
operator|.
name|class
argument_list|,
name|currentUser
argument_list|,
name|rpc
argument_list|,
name|NetUtils
operator|.
name|createSocketAddrForHost
argument_list|(
literal|"localhost"
argument_list|,
name|driver2Addr
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|GetPluginInfoResponse
name|pluginInfo2
init|=
name|client2
operator|.
name|getPluginInfo
argument_list|(
name|GetPluginInfoRequest
operator|.
name|newInstance
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pluginInfo2
operator|.
name|getDriverName
argument_list|()
argument_list|,
literal|"customized-driver-2"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pluginInfo2
operator|.
name|getVersion
argument_list|()
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|services
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

