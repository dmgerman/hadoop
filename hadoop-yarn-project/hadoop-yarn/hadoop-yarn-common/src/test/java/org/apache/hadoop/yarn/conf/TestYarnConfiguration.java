begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|conf
package|;
end_package

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
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|util
operator|.
name|WebAppUtils
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
name|SocketAddress
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

begin_class
DECL|class|TestYarnConfiguration
specifier|public
class|class
name|TestYarnConfiguration
block|{
annotation|@
name|Test
DECL|method|testDefaultRMWebUrl ()
specifier|public
name|void
name|testDefaultRMWebUrl
parameter_list|()
throws|throws
name|Exception
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|String
name|rmWebUrl
init|=
name|WebAppUtils
operator|.
name|getRMWebAppURLWithScheme
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// shouldn't have a "/" on the end of the url as all the other uri routinnes
comment|// specifically add slashes and Jetty doesn't handle double slashes.
name|Assert
operator|.
name|assertNotSame
argument_list|(
literal|"RM Web Url is not correct"
argument_list|,
literal|"http://0.0.0.0:8088"
argument_list|,
name|rmWebUrl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRMWebUrlSpecified ()
specifier|public
name|void
name|testRMWebUrlSpecified
parameter_list|()
throws|throws
name|Exception
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
comment|// seems a bit odd but right now we are forcing webapp for RM to be
comment|// RM_ADDRESS
comment|// for host and use the port from the RM_WEBAPP_ADDRESS
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
argument_list|,
literal|"fortesting:24543"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ADDRESS
argument_list|,
literal|"rmtesting:9999"
argument_list|)
expr_stmt|;
name|String
name|rmWebUrl
init|=
name|WebAppUtils
operator|.
name|getRMWebAppURLWithScheme
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
index|[]
name|parts
init|=
name|rmWebUrl
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"RM Web URL Port is incrrect"
argument_list|,
literal|24543
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|parts
index|[
name|parts
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotSame
argument_list|(
literal|"RM Web Url not resolved correctly. Should not be rmtesting"
argument_list|,
literal|"http://rmtesting:24543"
argument_list|,
name|rmWebUrl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetSocketAddressForNMWithHA ()
specifier|public
name|void
name|testGetSocketAddressForNMWithHA
parameter_list|()
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
comment|// Set NM address
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_ADDRESS
argument_list|,
literal|"0.0.0.0:1234"
argument_list|)
expr_stmt|;
comment|// Set HA
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ID
argument_list|,
literal|"rm1"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|HAUtil
operator|.
name|isHAEnabled
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|NM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_PORT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1234
argument_list|,
name|addr
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetSocketAddr ()
specifier|public
name|void
name|testGetSocketAddr
parameter_list|()
throws|throws
name|Exception
block|{
name|YarnConfiguration
name|conf
decl_stmt|;
name|InetSocketAddress
name|resourceTrackerAddress
decl_stmt|;
comment|//all default
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|resourceTrackerAddress
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_BIND_HOST
argument_list|,
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_PORT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
index|[
literal|0
index|]
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_PORT
argument_list|)
argument_list|,
name|resourceTrackerAddress
argument_list|)
expr_stmt|;
comment|//with address
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
literal|"10.0.0.1"
argument_list|)
expr_stmt|;
name|resourceTrackerAddress
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_BIND_HOST
argument_list|,
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_PORT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"10.0.0.1"
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_PORT
argument_list|)
argument_list|,
name|resourceTrackerAddress
argument_list|)
expr_stmt|;
comment|//address and socket
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
literal|"10.0.0.2:5001"
argument_list|)
expr_stmt|;
name|resourceTrackerAddress
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_BIND_HOST
argument_list|,
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_PORT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"10.0.0.2"
argument_list|,
literal|5001
argument_list|)
argument_list|,
name|resourceTrackerAddress
argument_list|)
expr_stmt|;
comment|//bind host only
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_BIND_HOST
argument_list|,
literal|"10.0.0.3"
argument_list|)
expr_stmt|;
name|resourceTrackerAddress
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_BIND_HOST
argument_list|,
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_PORT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"10.0.0.3"
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_PORT
argument_list|)
argument_list|,
name|resourceTrackerAddress
argument_list|)
expr_stmt|;
comment|//bind host and address no port
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_BIND_HOST
argument_list|,
literal|"0.0.0.0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
literal|"10.0.0.2"
argument_list|)
expr_stmt|;
name|resourceTrackerAddress
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_BIND_HOST
argument_list|,
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_PORT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"0.0.0.0"
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_PORT
argument_list|)
argument_list|,
name|resourceTrackerAddress
argument_list|)
expr_stmt|;
comment|//bind host and address with port
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_BIND_HOST
argument_list|,
literal|"0.0.0.0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
literal|"10.0.0.2:5003"
argument_list|)
expr_stmt|;
name|resourceTrackerAddress
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_BIND_HOST
argument_list|,
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_PORT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"0.0.0.0"
argument_list|,
literal|5003
argument_list|)
argument_list|,
name|resourceTrackerAddress
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpdateConnectAddr ()
specifier|public
name|void
name|testUpdateConnectAddr
parameter_list|()
throws|throws
name|Exception
block|{
name|YarnConfiguration
name|conf
decl_stmt|;
name|InetSocketAddress
name|resourceTrackerConnectAddress
decl_stmt|;
name|InetSocketAddress
name|serverAddress
decl_stmt|;
comment|//no override, old behavior.  Won't work on a host named "yo.yo.yo"
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
literal|"yo.yo.yo"
argument_list|)
expr_stmt|;
name|serverAddress
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
index|[
literal|0
index|]
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|resourceTrackerConnectAddress
operator|=
name|conf
operator|.
name|updateConnectAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_BIND_HOST
argument_list|,
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|serverAddress
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|resourceTrackerConnectAddress
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"yo.yo.yo"
argument_list|)
argument_list|)
expr_stmt|;
comment|//cause override with address
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
literal|"yo.yo.yo"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_BIND_HOST
argument_list|,
literal|"0.0.0.0"
argument_list|)
expr_stmt|;
name|serverAddress
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
index|[
literal|0
index|]
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|resourceTrackerConnectAddress
operator|=
name|conf
operator|.
name|updateConnectAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_BIND_HOST
argument_list|,
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|serverAddress
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|resourceTrackerConnectAddress
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"yo.yo.yo"
argument_list|)
argument_list|)
expr_stmt|;
comment|//tests updateConnectAddr won't add suffix to NM service address configurations
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCALIZER_ADDRESS
argument_list|,
literal|"yo.yo.yo"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_BIND_HOST
argument_list|,
literal|"0.0.0.0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ID
argument_list|,
literal|"rm1"
argument_list|)
expr_stmt|;
name|serverAddress
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LOCALIZER_ADDRESS
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
index|[
literal|0
index|]
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LOCALIZER_ADDRESS
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|localizerAddress
init|=
name|conf
operator|.
name|updateConnectAddr
argument_list|(
name|YarnConfiguration
operator|.
name|NM_BIND_HOST
argument_list|,
name|YarnConfiguration
operator|.
name|NM_LOCALIZER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LOCALIZER_ADDRESS
argument_list|,
name|serverAddress
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|localizerAddress
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"yo.yo.yo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|HAUtil
operator|.
name|addSuffix
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCALIZER_ADDRESS
argument_list|,
literal|"rm1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

