begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
package|;
end_package

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
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|core
operator|.
name|ResourceConfig
operator|.
name|PROPERTY_CONTAINER_REQUEST_FILTERS
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|core
operator|.
name|ResourceConfig
operator|.
name|FEATURE_TRACE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|container
operator|.
name|ContainerFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|core
operator|.
name|ApplicationAdapter
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
name|IOUtils
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
name|ksm
operator|.
name|protocolPB
operator|.
name|KeySpaceManagerProtocolClientSideTranslatorPB
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
name|ksm
operator|.
name|protocolPB
operator|.
name|KeySpaceManagerProtocolPB
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
name|OzoneClientUtils
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
name|protocolPB
operator|.
name|ScmBlockLocationProtocolClientSideTranslatorPB
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
name|protocolPB
operator|.
name|ScmBlockLocationProtocolPB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|ipc
operator|.
name|Client
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
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
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
name|protocolPB
operator|.
name|StorageContainerLocationProtocolPB
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
name|handlers
operator|.
name|ServiceFilter
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
name|interfaces
operator|.
name|StorageHandler
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
name|ObjectStoreApplication
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
name|netty
operator|.
name|ObjectStoreJerseyContainer
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
name|storage
operator|.
name|DistributedStorageHandler
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
name|localstorage
operator|.
name|LocalStorageHandler
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

begin_comment
comment|/**  * Implements object store handling within the DataNode process.  This class is  * responsible for initializing and maintaining the RPC clients and servers and  * the web application required for the object store implementation.  */
end_comment

begin_class
DECL|class|ObjectStoreHandler
specifier|public
specifier|final
class|class
name|ObjectStoreHandler
implements|implements
name|Closeable
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ObjectStoreJerseyContainer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|objectStoreJerseyContainer
specifier|private
specifier|final
name|ObjectStoreJerseyContainer
name|objectStoreJerseyContainer
decl_stmt|;
specifier|private
specifier|final
name|KeySpaceManagerProtocolClientSideTranslatorPB
DECL|field|keySpaceManagerClient
name|keySpaceManagerClient
decl_stmt|;
specifier|private
specifier|final
name|StorageContainerLocationProtocolClientSideTranslatorPB
DECL|field|storageContainerLocationClient
name|storageContainerLocationClient
decl_stmt|;
specifier|private
specifier|final
name|ScmBlockLocationProtocolClientSideTranslatorPB
DECL|field|scmBlockLocationClient
name|scmBlockLocationClient
decl_stmt|;
DECL|field|storageHandler
specifier|private
specifier|final
name|StorageHandler
name|storageHandler
decl_stmt|;
comment|/**    * Creates a new ObjectStoreHandler.    *    * @param conf configuration    * @throws IOException if there is an I/O error    */
DECL|method|ObjectStoreHandler (Configuration conf)
specifier|public
name|ObjectStoreHandler
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|shType
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|OZONE_HANDLER_TYPE_KEY
argument_list|,
name|OZONE_HANDLER_TYPE_DEFAULT
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"ObjectStoreHandler initializing with {}: {}"
argument_list|,
name|OZONE_HANDLER_TYPE_KEY
argument_list|,
name|shType
argument_list|)
expr_stmt|;
name|boolean
name|ozoneTrace
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|OZONE_TRACE_ENABLED_KEY
argument_list|,
name|OZONE_TRACE_ENABLED_DEFAULT
argument_list|)
decl_stmt|;
comment|// Initialize Jersey container for object store web application.
if|if
condition|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
operator|.
name|equalsIgnoreCase
argument_list|(
name|shType
argument_list|)
condition|)
block|{
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|StorageContainerLocationProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|long
name|scmVersion
init|=
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|StorageContainerLocationProtocolPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|scmAddress
init|=
name|OzoneClientUtils
operator|.
name|getScmAddressForClients
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|this
operator|.
name|storageContainerLocationClient
operator|=
operator|new
name|StorageContainerLocationProtocolClientSideTranslatorPB
argument_list|(
name|RPC
operator|.
name|getProxy
argument_list|(
name|StorageContainerLocationProtocolPB
operator|.
name|class
argument_list|,
name|scmVersion
argument_list|,
name|scmAddress
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
name|Client
operator|.
name|getRpcTimeout
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|scmBlockAddress
init|=
name|OzoneClientUtils
operator|.
name|getScmAddressForBlockClients
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|this
operator|.
name|scmBlockLocationClient
operator|=
operator|new
name|ScmBlockLocationProtocolClientSideTranslatorPB
argument_list|(
name|RPC
operator|.
name|getProxy
argument_list|(
name|ScmBlockLocationProtocolPB
operator|.
name|class
argument_list|,
name|scmVersion
argument_list|,
name|scmBlockAddress
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
name|Client
operator|.
name|getRpcTimeout
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|KeySpaceManagerProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|long
name|ksmVersion
init|=
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|KeySpaceManagerProtocolPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|ksmAddress
init|=
name|OzoneClientUtils
operator|.
name|getKsmAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|this
operator|.
name|keySpaceManagerClient
operator|=
operator|new
name|KeySpaceManagerProtocolClientSideTranslatorPB
argument_list|(
name|RPC
operator|.
name|getProxy
argument_list|(
name|KeySpaceManagerProtocolPB
operator|.
name|class
argument_list|,
name|ksmVersion
argument_list|,
name|ksmAddress
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
name|Client
operator|.
name|getRpcTimeout
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|storageHandler
operator|=
operator|new
name|DistributedStorageHandler
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|(
name|conf
argument_list|)
argument_list|,
name|this
operator|.
name|storageContainerLocationClient
argument_list|,
name|this
operator|.
name|keySpaceManagerClient
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_LOCAL
operator|.
name|equalsIgnoreCase
argument_list|(
name|shType
argument_list|)
condition|)
block|{
name|storageHandler
operator|=
operator|new
name|LocalStorageHandler
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|storageContainerLocationClient
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|scmBlockLocationClient
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|keySpaceManagerClient
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unrecognized value for %s: %s,"
operator|+
literal|" Allowed values are %s,%s"
argument_list|,
name|OZONE_HANDLER_TYPE_KEY
argument_list|,
name|shType
argument_list|,
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|,
name|OzoneConsts
operator|.
name|OZONE_HANDLER_LOCAL
argument_list|)
argument_list|)
throw|;
block|}
block|}
name|ApplicationAdapter
name|aa
init|=
operator|new
name|ApplicationAdapter
argument_list|(
operator|new
name|ObjectStoreApplication
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|settingsMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|settingsMap
operator|.
name|put
argument_list|(
name|PROPERTY_CONTAINER_REQUEST_FILTERS
argument_list|,
name|ServiceFilter
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
name|settingsMap
operator|.
name|put
argument_list|(
name|FEATURE_TRACE
argument_list|,
name|ozoneTrace
argument_list|)
expr_stmt|;
name|aa
operator|.
name|setPropertiesAndFeatures
argument_list|(
name|settingsMap
argument_list|)
expr_stmt|;
name|this
operator|.
name|objectStoreJerseyContainer
operator|=
name|ContainerFactory
operator|.
name|createContainer
argument_list|(
name|ObjectStoreJerseyContainer
operator|.
name|class
argument_list|,
name|aa
argument_list|)
expr_stmt|;
name|this
operator|.
name|objectStoreJerseyContainer
operator|.
name|setStorageHandler
argument_list|(
name|storageHandler
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the initialized web application container.    *    * @return initialized web application container    */
DECL|method|getObjectStoreJerseyContainer ()
specifier|public
name|ObjectStoreJerseyContainer
name|getObjectStoreJerseyContainer
parameter_list|()
block|{
return|return
name|this
operator|.
name|objectStoreJerseyContainer
return|;
block|}
comment|/**    * Returns the storage handler.    *    * @return returns the storage handler    */
DECL|method|getStorageHandler ()
specifier|public
name|StorageHandler
name|getStorageHandler
parameter_list|()
block|{
return|return
name|this
operator|.
name|storageHandler
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Closing ObjectStoreHandler."
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|storageContainerLocationClient
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|scmBlockLocationClient
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|keySpaceManagerClient
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

