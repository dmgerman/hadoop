begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|rules
operator|.
name|Timeout
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
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|OzoneConfigKeys
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
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
name|assertThat
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

begin_comment
comment|/**  * This test class verifies the parsing of SCM endpoint config settings.  * The parsing logic is in {@link OzoneClientUtils}.  */
end_comment

begin_class
DECL|class|TestOzoneClientUtils
specifier|public
class|class
name|TestOzoneClientUtils
block|{
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|thrown
specifier|public
name|ExpectedException
name|thrown
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
comment|/**    * Verify client endpoint lookup failure if it is not configured.    */
annotation|@
name|Test
DECL|method|testMissingScmClientAddress ()
specifier|public
name|void
name|testMissingScmClientAddress
parameter_list|()
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|thrown
operator|.
name|expect
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
name|OzoneClientUtils
operator|.
name|getScmAddressForClients
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that the client endpoint can be correctly parsed from    * configuration.    */
annotation|@
name|Test
DECL|method|testGetScmClientAddress ()
specifier|public
name|void
name|testGetScmClientAddress
parameter_list|()
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
comment|// First try a client address with just a host name. Verify it falls
comment|// back to the default port.
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
name|OzoneClientUtils
operator|.
name|getScmAddressForClients
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getHostString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"1.2.3.4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|is
argument_list|(
name|OZONE_SCM_CLIENT_PORT_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
comment|// Next try a client address with a host name and port. Verify both
comment|// are used correctly.
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4:100"
argument_list|)
expr_stmt|;
name|addr
operator|=
name|OzoneClientUtils
operator|.
name|getScmAddressForClients
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getHostString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"1.2.3.4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|is
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify DataNode endpoint lookup failure if neither the client nor    * datanode endpoint are configured.    */
annotation|@
name|Test
DECL|method|testMissingScmDataNodeAddress ()
specifier|public
name|void
name|testMissingScmDataNodeAddress
parameter_list|()
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|thrown
operator|.
name|expect
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
name|OzoneClientUtils
operator|.
name|getScmAddressForDataNodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that the datanode endpoint is parsed correctly.    * This tests the logic used by the DataNodes to determine which address    * to connect to.    */
annotation|@
name|Test
DECL|method|testGetScmDataNodeAddress ()
specifier|public
name|void
name|testGetScmDataNodeAddress
parameter_list|()
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
comment|// First try a client address with just a host name. Verify it falls
comment|// back to the default port.
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
name|OzoneClientUtils
operator|.
name|getScmAddressForDataNodes
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getHostString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"1.2.3.4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|is
argument_list|(
name|OZONE_SCM_DATANODE_PORT_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
comment|// Next try a client address with just a host name and port. Verify the port
comment|// is ignored and the default DataNode port is used.
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4:100"
argument_list|)
expr_stmt|;
name|addr
operator|=
name|OzoneClientUtils
operator|.
name|getScmAddressForDataNodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getHostString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"1.2.3.4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|is
argument_list|(
name|OZONE_SCM_DATANODE_PORT_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set both OZONE_SCM_CLIENT_ADDRESS_KEY and OZONE_SCM_DATANODE_ADDRESS_KEY.
comment|// Verify that the latter overrides and the port number is still the default.
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4:100"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_DATANODE_ADDRESS_KEY
argument_list|,
literal|"5.6.7.8"
argument_list|)
expr_stmt|;
name|addr
operator|=
name|OzoneClientUtils
operator|.
name|getScmAddressForDataNodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getHostString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"5.6.7.8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|is
argument_list|(
name|OZONE_SCM_DATANODE_PORT_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set both OZONE_SCM_CLIENT_ADDRESS_KEY and OZONE_SCM_DATANODE_ADDRESS_KEY.
comment|// Verify that the latter overrides and the port number from the latter is
comment|// used.
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4:100"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_DATANODE_ADDRESS_KEY
argument_list|,
literal|"5.6.7.8:200"
argument_list|)
expr_stmt|;
name|addr
operator|=
name|OzoneClientUtils
operator|.
name|getScmAddressForDataNodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getHostString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"5.6.7.8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|is
argument_list|(
literal|200
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that the client endpoint bind address is computed correctly.    * This tests the logic used by the SCM to determine its own bind address.    */
annotation|@
name|Test
DECL|method|testScmClientBindHostDefault ()
specifier|public
name|void
name|testScmClientBindHostDefault
parameter_list|()
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
comment|// The bind host should be 0.0.0.0 unless OZONE_SCM_CLIENT_BIND_HOST_KEY
comment|// is set differently.
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
name|OzoneClientUtils
operator|.
name|getScmClientBindAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getHostString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"0.0.0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|is
argument_list|(
name|OZONE_SCM_CLIENT_PORT_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
comment|// The bind host should be 0.0.0.0 unless OZONE_SCM_CLIENT_BIND_HOST_KEY
comment|// is set differently. The port number from OZONE_SCM_CLIENT_ADDRESS_KEY
comment|// should be respected.
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4:100"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_DATANODE_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4:200"
argument_list|)
expr_stmt|;
name|addr
operator|=
name|OzoneClientUtils
operator|.
name|getScmClientBindAddress
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getHostString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"0.0.0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|is
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
comment|// OZONE_SCM_CLIENT_BIND_HOST_KEY should be respected.
comment|// Port number should be default if none is specified via
comment|// OZONE_SCM_DATANODE_ADDRESS_KEY.
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_DATANODE_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CLIENT_BIND_HOST_KEY
argument_list|,
literal|"5.6.7.8"
argument_list|)
expr_stmt|;
name|addr
operator|=
name|OzoneClientUtils
operator|.
name|getScmClientBindAddress
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getHostString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"5.6.7.8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|is
argument_list|(
name|OZONE_SCM_CLIENT_PORT_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
comment|// OZONE_SCM_CLIENT_BIND_HOST_KEY should be respected.
comment|// Port number from OZONE_SCM_CLIENT_ADDRESS_KEY should be
comment|// respected.
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4:100"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_DATANODE_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4:200"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CLIENT_BIND_HOST_KEY
argument_list|,
literal|"5.6.7.8"
argument_list|)
expr_stmt|;
name|addr
operator|=
name|OzoneClientUtils
operator|.
name|getScmClientBindAddress
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getHostString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"5.6.7.8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|is
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that the DataNode endpoint bind address is computed correctly.    * This tests the logic used by the SCM to determine its own bind address.    */
annotation|@
name|Test
DECL|method|testScmDataNodeBindHostDefault ()
specifier|public
name|void
name|testScmDataNodeBindHostDefault
parameter_list|()
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
comment|// The bind host should be 0.0.0.0 unless OZONE_SCM_DATANODE_BIND_HOST_KEY
comment|// is set differently.
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
name|OzoneClientUtils
operator|.
name|getScmDataNodeBindAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getHostString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"0.0.0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|is
argument_list|(
name|OZONE_SCM_DATANODE_PORT_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
comment|// The bind host should be 0.0.0.0 unless OZONE_SCM_DATANODE_BIND_HOST_KEY
comment|// is set differently. The port number from OZONE_SCM_DATANODE_ADDRESS_KEY
comment|// should be respected.
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4:100"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_DATANODE_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4:200"
argument_list|)
expr_stmt|;
name|addr
operator|=
name|OzoneClientUtils
operator|.
name|getScmDataNodeBindAddress
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getHostString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"0.0.0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|is
argument_list|(
literal|200
argument_list|)
argument_list|)
expr_stmt|;
comment|// OZONE_SCM_DATANODE_BIND_HOST_KEY should be respected.
comment|// Port number should be default if none is specified via
comment|// OZONE_SCM_DATANODE_ADDRESS_KEY.
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4:100"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_DATANODE_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_DATANODE_BIND_HOST_KEY
argument_list|,
literal|"5.6.7.8"
argument_list|)
expr_stmt|;
name|addr
operator|=
name|OzoneClientUtils
operator|.
name|getScmDataNodeBindAddress
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getHostString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"5.6.7.8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|is
argument_list|(
name|OZONE_SCM_DATANODE_PORT_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
comment|// OZONE_SCM_DATANODE_BIND_HOST_KEY should be respected.
comment|// Port number from OZONE_SCM_DATANODE_ADDRESS_KEY should be
comment|// respected.
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4:100"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_DATANODE_ADDRESS_KEY
argument_list|,
literal|"1.2.3.4:200"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_DATANODE_BIND_HOST_KEY
argument_list|,
literal|"5.6.7.8"
argument_list|)
expr_stmt|;
name|addr
operator|=
name|OzoneClientUtils
operator|.
name|getScmDataNodeBindAddress
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getHostString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"5.6.7.8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|is
argument_list|(
literal|200
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetSCMAddresses ()
specifier|public
name|void
name|testGetSCMAddresses
parameter_list|()
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|InetSocketAddress
argument_list|>
name|addresses
init|=
literal|null
decl_stmt|;
name|InetSocketAddress
name|addr
init|=
literal|null
decl_stmt|;
name|Iterator
argument_list|<
name|InetSocketAddress
argument_list|>
name|it
init|=
literal|null
decl_stmt|;
comment|// Verify valid IP address setup
name|conf
operator|.
name|setStrings
argument_list|(
name|OZONE_SCM_NAMES
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
name|addresses
operator|=
name|OzoneClientUtils
operator|.
name|getSCMAddresses
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addresses
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|addr
operator|=
name|addresses
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getHostName
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"1.2.3.4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|is
argument_list|(
name|OZONE_SCM_DEFAULT_PORT
argument_list|)
argument_list|)
expr_stmt|;
comment|// Verify valid hostname setup
name|conf
operator|.
name|setStrings
argument_list|(
name|OZONE_SCM_NAMES
argument_list|,
literal|"scm1"
argument_list|)
expr_stmt|;
name|addresses
operator|=
name|OzoneClientUtils
operator|.
name|getSCMAddresses
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addresses
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|addr
operator|=
name|addresses
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getHostName
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"scm1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|is
argument_list|(
name|OZONE_SCM_DEFAULT_PORT
argument_list|)
argument_list|)
expr_stmt|;
comment|// Verify valid hostname and port
name|conf
operator|.
name|setStrings
argument_list|(
name|OZONE_SCM_NAMES
argument_list|,
literal|"scm1:1234"
argument_list|)
expr_stmt|;
name|addresses
operator|=
name|OzoneClientUtils
operator|.
name|getSCMAddresses
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addresses
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|addr
operator|=
name|addresses
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getHostName
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"scm1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1234
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|hostsAndPorts
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|hostsAndPorts
operator|.
name|put
argument_list|(
literal|"scm1"
argument_list|,
literal|1234
argument_list|)
expr_stmt|;
name|hostsAndPorts
operator|.
name|put
argument_list|(
literal|"scm2"
argument_list|,
literal|2345
argument_list|)
expr_stmt|;
name|hostsAndPorts
operator|.
name|put
argument_list|(
literal|"scm3"
argument_list|,
literal|3456
argument_list|)
expr_stmt|;
comment|// Verify multiple hosts and port
name|conf
operator|.
name|setStrings
argument_list|(
name|OZONE_SCM_NAMES
argument_list|,
literal|"scm1:1234,scm2:2345,scm3:3456"
argument_list|)
expr_stmt|;
name|addresses
operator|=
name|OzoneClientUtils
operator|.
name|getSCMAddresses
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addresses
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|it
operator|=
name|addresses
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|expected1
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|hostsAndPorts
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|InetSocketAddress
name|current
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|expected1
operator|.
name|remove
argument_list|(
name|current
operator|.
name|getHostName
argument_list|()
argument_list|,
name|current
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|expected1
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify names with spaces
name|conf
operator|.
name|setStrings
argument_list|(
name|OZONE_SCM_NAMES
argument_list|,
literal|" scm1:1234, scm2:2345 , scm3:3456 "
argument_list|)
expr_stmt|;
name|addresses
operator|=
name|OzoneClientUtils
operator|.
name|getSCMAddresses
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|addresses
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|it
operator|=
name|addresses
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|expected2
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|hostsAndPorts
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|InetSocketAddress
name|current
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|expected2
operator|.
name|remove
argument_list|(
name|current
operator|.
name|getHostName
argument_list|()
argument_list|,
name|current
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|expected2
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify empty value
name|conf
operator|.
name|setStrings
argument_list|(
name|OZONE_SCM_NAMES
argument_list|,
literal|""
argument_list|)
expr_stmt|;
try|try
block|{
name|addresses
operator|=
name|OzoneClientUtils
operator|.
name|getSCMAddresses
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Empty value should cause an IllegalArgumentException"
argument_list|)
expr_stmt|;
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
operator|instanceof
name|IllegalArgumentException
argument_list|)
expr_stmt|;
block|}
comment|// Verify invalid hostname
name|conf
operator|.
name|setStrings
argument_list|(
name|OZONE_SCM_NAMES
argument_list|,
literal|"s..x..:1234"
argument_list|)
expr_stmt|;
try|try
block|{
name|addresses
operator|=
name|OzoneClientUtils
operator|.
name|getSCMAddresses
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"An invalid hostname should cause an IllegalArgumentException"
argument_list|)
expr_stmt|;
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
operator|instanceof
name|IllegalArgumentException
argument_list|)
expr_stmt|;
block|}
comment|// Verify invalid port
name|conf
operator|.
name|setStrings
argument_list|(
name|OZONE_SCM_NAMES
argument_list|,
literal|"scm:xyz"
argument_list|)
expr_stmt|;
try|try
block|{
name|addresses
operator|=
name|OzoneClientUtils
operator|.
name|getSCMAddresses
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"An invalid port should cause an IllegalArgumentException"
argument_list|)
expr_stmt|;
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
operator|instanceof
name|IllegalArgumentException
argument_list|)
expr_stmt|;
block|}
comment|// Verify a mixed case (valid and invalid value both appears)
name|conf
operator|.
name|setStrings
argument_list|(
name|OZONE_SCM_NAMES
argument_list|,
literal|"scm1:1234, scm:xyz"
argument_list|)
expr_stmt|;
try|try
block|{
name|addresses
operator|=
name|OzoneClientUtils
operator|.
name|getSCMAddresses
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"An invalid value should cause an IllegalArgumentException"
argument_list|)
expr_stmt|;
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
operator|instanceof
name|IllegalArgumentException
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

