begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock.jscsiHelper
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
operator|.
name|jscsiHelper
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
name|cblock
operator|.
name|protocolPB
operator|.
name|CBlockClientServerProtocolPB
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
name|cblock
operator|.
name|protocolPB
operator|.
name|CBlockServiceProtocolPB
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
name|ipc
operator|.
name|ProtobufRpcEngine
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
name|ipc
operator|.
name|RPC
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
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
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
name|scm
operator|.
name|client
operator|.
name|ContainerOperationClient
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
name|jscsi
operator|.
name|target
operator|.
name|Configuration
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_CONTAINER_SIZE_GB_DEFAULT
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_CONTAINER_SIZE_GB_KEY
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_JSCSI_CBLOCK_SERVER_ADDRESS_DEFAULT
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_JSCSI_CBLOCK_SERVER_ADDRESS_KEY
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_JSCSI_PORT_DEFAULT
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_JSCSI_PORT_KEY
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_JSCSI_SERVER_ADDRESS_DEFAULT
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_JSCSI_SERVER_ADDRESS_KEY
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_ADDRESS_KEY
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_BIND_HOST_DEFAULT
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_BIND_HOST_KEY
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_PORT_DEFAULT
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_PORT_KEY
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DATANODE_ADDRESS_KEY
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DATANODE_PORT_DEFAULT
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DATANODE_PORT_KEY
import|;
end_import

begin_comment
comment|/**  * This class runs the target server process.  */
end_comment

begin_class
DECL|class|SCSITargetDaemon
specifier|public
specifier|final
class|class
name|SCSITargetDaemon
block|{
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|OzoneConfiguration
name|ozoneConf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|ozoneConf
argument_list|,
name|CBlockClientServerProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|long
name|containerSizeGB
init|=
name|ozoneConf
operator|.
name|getInt
argument_list|(
name|DFS_CBLOCK_CONTAINER_SIZE_GB_KEY
argument_list|,
name|DFS_CBLOCK_CONTAINER_SIZE_GB_DEFAULT
argument_list|)
decl_stmt|;
name|ContainerOperationClient
operator|.
name|setContainerSizeB
argument_list|(
name|containerSizeGB
operator|*
name|OzoneConsts
operator|.
name|GB
argument_list|)
expr_stmt|;
name|String
name|jscsiServerAddress
init|=
name|ozoneConf
operator|.
name|get
argument_list|(
name|DFS_CBLOCK_JSCSI_SERVER_ADDRESS_KEY
argument_list|,
name|DFS_CBLOCK_JSCSI_SERVER_ADDRESS_DEFAULT
argument_list|)
decl_stmt|;
name|String
name|cbmIPAddress
init|=
name|ozoneConf
operator|.
name|get
argument_list|(
name|DFS_CBLOCK_JSCSI_CBLOCK_SERVER_ADDRESS_KEY
argument_list|,
name|DFS_CBLOCK_JSCSI_CBLOCK_SERVER_ADDRESS_DEFAULT
argument_list|)
decl_stmt|;
name|int
name|cbmPort
init|=
name|ozoneConf
operator|.
name|getInt
argument_list|(
name|DFS_CBLOCK_JSCSI_PORT_KEY
argument_list|,
name|DFS_CBLOCK_JSCSI_PORT_DEFAULT
argument_list|)
decl_stmt|;
name|String
name|scmAddress
init|=
name|ozoneConf
operator|.
name|get
argument_list|(
name|OZONE_SCM_CLIENT_BIND_HOST_KEY
argument_list|,
name|OZONE_SCM_CLIENT_BIND_HOST_DEFAULT
argument_list|)
decl_stmt|;
name|int
name|scmClientPort
init|=
name|ozoneConf
operator|.
name|getInt
argument_list|(
name|OZONE_SCM_CLIENT_PORT_KEY
argument_list|,
name|OZONE_SCM_CLIENT_PORT_DEFAULT
argument_list|)
decl_stmt|;
name|int
name|scmDatanodePort
init|=
name|ozoneConf
operator|.
name|getInt
argument_list|(
name|OZONE_SCM_DATANODE_PORT_KEY
argument_list|,
name|OZONE_SCM_DATANODE_PORT_DEFAULT
argument_list|)
decl_stmt|;
name|String
name|scmClientAddress
init|=
name|scmAddress
operator|+
literal|":"
operator|+
name|scmClientPort
decl_stmt|;
name|String
name|scmDataodeAddress
init|=
name|scmAddress
operator|+
literal|":"
operator|+
name|scmDatanodePort
decl_stmt|;
name|ozoneConf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|,
name|scmClientAddress
argument_list|)
expr_stmt|;
name|ozoneConf
operator|.
name|set
argument_list|(
name|OZONE_SCM_DATANODE_ADDRESS_KEY
argument_list|,
name|scmDataodeAddress
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|cbmAddress
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|cbmIPAddress
argument_list|,
name|cbmPort
argument_list|)
decl_stmt|;
name|long
name|version
init|=
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|CBlockServiceProtocolPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|CBlockClientProtocolClientSideTranslatorPB
name|cbmClient
init|=
operator|new
name|CBlockClientProtocolClientSideTranslatorPB
argument_list|(
name|RPC
operator|.
name|getProxy
argument_list|(
name|CBlockClientServerProtocolPB
operator|.
name|class
argument_list|,
name|version
argument_list|,
name|cbmAddress
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
name|ozoneConf
argument_list|,
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|ozoneConf
argument_list|)
argument_list|,
literal|5000
argument_list|)
argument_list|)
decl_stmt|;
name|CBlockManagerHandler
name|cbmHandler
init|=
operator|new
name|CBlockManagerHandler
argument_list|(
name|cbmClient
argument_list|)
decl_stmt|;
name|Configuration
name|jscsiConfig
init|=
operator|new
name|Configuration
argument_list|(
name|jscsiServerAddress
argument_list|)
decl_stmt|;
name|DefaultMetricsSystem
operator|.
name|initialize
argument_list|(
literal|"CBlockMetrics"
argument_list|)
expr_stmt|;
name|CBlockTargetMetrics
name|metrics
init|=
name|CBlockTargetMetrics
operator|.
name|create
argument_list|()
decl_stmt|;
name|CBlockTargetServer
name|targetServer
init|=
operator|new
name|CBlockTargetServer
argument_list|(
name|ozoneConf
argument_list|,
name|jscsiConfig
argument_list|,
name|cbmHandler
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|targetServer
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
DECL|method|SCSITargetDaemon ()
specifier|private
name|SCSITargetDaemon
parameter_list|()
block|{    }
block|}
end_class

end_unit

