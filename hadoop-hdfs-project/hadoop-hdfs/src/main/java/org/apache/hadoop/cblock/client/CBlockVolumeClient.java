begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
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
name|cblock
operator|.
name|meta
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
name|io
operator|.
name|retry
operator|.
name|RetryPolicies
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
name|security
operator|.
name|UserGroupInformation
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
name|concurrent
operator|.
name|TimeUnit
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
name|DFS_CBLOCK_SERVICERPC_HOSTNAME_DEFAULT
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
name|DFS_CBLOCK_SERVICERPC_HOSTNAME_KEY
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
name|DFS_CBLOCK_SERVICERPC_PORT_DEFAULT
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
name|DFS_CBLOCK_SERVICERPC_PORT_KEY
import|;
end_import

begin_comment
comment|/**  * Implementation of client used by CBlock command line tool.  */
end_comment

begin_class
DECL|class|CBlockVolumeClient
specifier|public
class|class
name|CBlockVolumeClient
block|{
DECL|field|cblockClient
specifier|private
specifier|final
name|CBlockServiceProtocolClientSideTranslatorPB
name|cblockClient
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|method|CBlockVolumeClient (OzoneConfiguration conf)
specifier|public
name|CBlockVolumeClient
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
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
name|String
name|serverAddress
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFS_CBLOCK_SERVICERPC_HOSTNAME_KEY
argument_list|,
name|DFS_CBLOCK_SERVICERPC_HOSTNAME_DEFAULT
argument_list|)
decl_stmt|;
name|int
name|serverPort
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFS_CBLOCK_SERVICERPC_PORT_KEY
argument_list|,
name|DFS_CBLOCK_SERVICERPC_PORT_DEFAULT
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|address
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|serverAddress
argument_list|,
name|serverPort
argument_list|)
decl_stmt|;
comment|// currently the largest supported volume is about 8TB, which might take
comment|//> 20 seconds to finish creating containers. thus set timeout to 30 sec.
name|cblockClient
operator|=
operator|new
name|CBlockServiceProtocolClientSideTranslatorPB
argument_list|(
name|RPC
operator|.
name|getProtocolProxy
argument_list|(
name|CBlockServiceProtocolPB
operator|.
name|class
argument_list|,
name|version
argument_list|,
name|address
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
name|conf
argument_list|,
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|conf
argument_list|)
argument_list|,
literal|30000
argument_list|,
name|RetryPolicies
operator|.
name|retryUpToMaximumCountWithFixedSleep
argument_list|(
literal|300
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
operator|.
name|getProxy
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|CBlockVolumeClient (OzoneConfiguration conf, InetSocketAddress serverAddress)
specifier|public
name|CBlockVolumeClient
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|InetSocketAddress
name|serverAddress
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
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
name|cblockClient
operator|=
operator|new
name|CBlockServiceProtocolClientSideTranslatorPB
argument_list|(
name|RPC
operator|.
name|getProtocolProxy
argument_list|(
name|CBlockServiceProtocolPB
operator|.
name|class
argument_list|,
name|version
argument_list|,
name|serverAddress
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
name|conf
argument_list|,
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|conf
argument_list|)
argument_list|,
literal|30000
argument_list|,
name|RetryPolicies
operator|.
name|retryUpToMaximumCountWithFixedSleep
argument_list|(
literal|300
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
operator|.
name|getProxy
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createVolume (String userName, String volumeName, long volumeSize, int blockSize)
specifier|public
name|void
name|createVolume
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|volumeName
parameter_list|,
name|long
name|volumeSize
parameter_list|,
name|int
name|blockSize
parameter_list|)
throws|throws
name|IOException
block|{
name|cblockClient
operator|.
name|createVolume
argument_list|(
name|userName
argument_list|,
name|volumeName
argument_list|,
name|volumeSize
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteVolume (String userName, String volumeName, boolean force)
specifier|public
name|void
name|deleteVolume
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|volumeName
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
block|{
name|cblockClient
operator|.
name|deleteVolume
argument_list|(
name|userName
argument_list|,
name|volumeName
argument_list|,
name|force
argument_list|)
expr_stmt|;
block|}
DECL|method|infoVolume (String userName, String volumeName)
specifier|public
name|VolumeInfo
name|infoVolume
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|volumeName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|cblockClient
operator|.
name|infoVolume
argument_list|(
name|userName
argument_list|,
name|volumeName
argument_list|)
return|;
block|}
DECL|method|listVolume (String userName)
specifier|public
name|List
argument_list|<
name|VolumeInfo
argument_list|>
name|listVolume
parameter_list|(
name|String
name|userName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|cblockClient
operator|.
name|listVolume
argument_list|(
name|userName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

