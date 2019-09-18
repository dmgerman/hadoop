begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.genesis
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|genesis
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
name|conf
operator|.
name|StorageUnit
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|scm
operator|.
name|ScmConfigKeys
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
name|scm
operator|.
name|pipeline
operator|.
name|Pipeline
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
name|scm
operator|.
name|pipeline
operator|.
name|PipelineID
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
name|scm
operator|.
name|server
operator|.
name|SCMConfigurator
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
name|scm
operator|.
name|server
operator|.
name|SCMStorageConfig
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
name|scm
operator|.
name|server
operator|.
name|StorageContainerManager
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
name|server
operator|.
name|ServerUtils
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
name|ozone
operator|.
name|common
operator|.
name|Storage
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
name|om
operator|.
name|OMConfigKeys
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
name|om
operator|.
name|OMStorage
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
name|om
operator|.
name|OzoneManager
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
name|authentication
operator|.
name|client
operator|.
name|AuthenticationException
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
name|utils
operator|.
name|MetadataStore
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
name|utils
operator|.
name|MetadataStoreBuilder
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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|Random
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
name|OZONE_SCM_DB_CACHE_SIZE_DEFAULT
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
name|OZONE_SCM_DB_CACHE_SIZE_MB
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
name|OzoneConsts
operator|.
name|SCM_PIPELINE_DB
import|;
end_import

begin_comment
comment|/**  * Utility class for benchmark test cases.  */
end_comment

begin_class
DECL|class|GenesisUtil
specifier|public
specifier|final
class|class
name|GenesisUtil
block|{
DECL|method|GenesisUtil ()
specifier|private
name|GenesisUtil
parameter_list|()
block|{
comment|// private constructor.
block|}
DECL|field|DEFAULT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_TYPE
init|=
literal|"default"
decl_stmt|;
DECL|field|CACHE_10MB_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_10MB_TYPE
init|=
literal|"Cache10MB"
decl_stmt|;
DECL|field|CACHE_1GB_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_1GB_TYPE
init|=
literal|"Cache1GB"
decl_stmt|;
DECL|field|CLOSED_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CLOSED_TYPE
init|=
literal|"ClosedContainer"
decl_stmt|;
DECL|field|DB_FILE_LEN
specifier|private
specifier|static
specifier|final
name|int
name|DB_FILE_LEN
init|=
literal|7
decl_stmt|;
DECL|field|TMP_DIR
specifier|private
specifier|static
specifier|final
name|String
name|TMP_DIR
init|=
literal|"java.io.tmpdir"
decl_stmt|;
DECL|method|getTempPath ()
specifier|public
specifier|static
name|Path
name|getTempPath
parameter_list|()
block|{
return|return
name|Paths
operator|.
name|get
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
name|TMP_DIR
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getMetadataStore (String dbType)
specifier|public
specifier|static
name|MetadataStore
name|getMetadataStore
parameter_list|(
name|String
name|dbType
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|MetadataStoreBuilder
name|builder
init|=
name|MetadataStoreBuilder
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setCreateIfMissing
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setDbFile
argument_list|(
name|getTempPath
argument_list|()
operator|.
name|resolve
argument_list|(
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
name|DB_FILE_LEN
argument_list|)
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|dbType
condition|)
block|{
case|case
name|DEFAULT_TYPE
case|:
break|break;
case|case
name|CLOSED_TYPE
case|:
break|break;
case|case
name|CACHE_10MB_TYPE
case|:
name|builder
operator|.
name|setCacheSize
argument_list|(
operator|(
name|long
operator|)
name|StorageUnit
operator|.
name|MB
operator|.
name|toBytes
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|CACHE_1GB_TYPE
case|:
name|builder
operator|.
name|setCacheSize
argument_list|(
operator|(
name|long
operator|)
name|StorageUnit
operator|.
name|GB
operator|.
name|toBytes
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unknown type: "
operator|+
name|dbType
argument_list|)
throw|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|createDatanodeDetails (String uuid)
specifier|public
specifier|static
name|DatanodeDetails
name|createDatanodeDetails
parameter_list|(
name|String
name|uuid
parameter_list|)
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|String
name|ipAddress
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
operator|+
literal|"."
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
operator|+
literal|"."
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
operator|+
literal|"."
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
decl_stmt|;
name|DatanodeDetails
operator|.
name|Port
name|containerPort
init|=
name|DatanodeDetails
operator|.
name|newPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|STANDALONE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DatanodeDetails
operator|.
name|Port
name|ratisPort
init|=
name|DatanodeDetails
operator|.
name|newPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|RATIS
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DatanodeDetails
operator|.
name|Port
name|restPort
init|=
name|DatanodeDetails
operator|.
name|newPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|REST
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DatanodeDetails
operator|.
name|Builder
name|builder
init|=
name|DatanodeDetails
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setUuid
argument_list|(
name|uuid
argument_list|)
operator|.
name|setHostName
argument_list|(
literal|"localhost"
argument_list|)
operator|.
name|setIpAddress
argument_list|(
name|ipAddress
argument_list|)
operator|.
name|addPort
argument_list|(
name|containerPort
argument_list|)
operator|.
name|addPort
argument_list|(
name|ratisPort
argument_list|)
operator|.
name|addPort
argument_list|(
name|restPort
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getScm (OzoneConfiguration conf, SCMConfigurator configurator)
specifier|static
name|StorageContainerManager
name|getScm
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|SCMConfigurator
name|configurator
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
name|SCMStorageConfig
name|scmStore
init|=
operator|new
name|SCMStorageConfig
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|scmStore
operator|.
name|getState
argument_list|()
operator|!=
name|Storage
operator|.
name|StorageState
operator|.
name|INITIALIZED
condition|)
block|{
name|String
name|clusterId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|scmId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|scmStore
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
expr_stmt|;
name|scmStore
operator|.
name|setScmId
argument_list|(
name|scmId
argument_list|)
expr_stmt|;
comment|// writes the version file properties
name|scmStore
operator|.
name|initialize
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|StorageContainerManager
argument_list|(
name|conf
argument_list|,
name|configurator
argument_list|)
return|;
block|}
DECL|method|configureSCM (Configuration conf, int numHandlers)
specifier|static
name|void
name|configureSCM
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|numHandlers
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DATANODE_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HTTP_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HANDLER_COUNT_KEY
argument_list|,
name|numHandlers
argument_list|)
expr_stmt|;
block|}
DECL|method|addPipelines (HddsProtos.ReplicationFactor factor, int numPipelines, Configuration conf)
specifier|static
name|void
name|addPipelines
parameter_list|(
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
parameter_list|,
name|int
name|numPipelines
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|File
name|metaDir
init|=
name|ServerUtils
operator|.
name|getScmDbDir
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|File
name|pipelineDBPath
init|=
operator|new
name|File
argument_list|(
name|metaDir
argument_list|,
name|SCM_PIPELINE_DB
argument_list|)
decl_stmt|;
name|int
name|cacheSize
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_SCM_DB_CACHE_SIZE_MB
argument_list|,
name|OZONE_SCM_DB_CACHE_SIZE_DEFAULT
argument_list|)
decl_stmt|;
name|MetadataStore
name|pipelineStore
init|=
name|MetadataStoreBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCreateIfMissing
argument_list|(
literal|true
argument_list|)
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
operator|.
name|setDbFile
argument_list|(
name|pipelineDBPath
argument_list|)
operator|.
name|setCacheSize
argument_list|(
name|cacheSize
operator|*
name|OzoneConsts
operator|.
name|MB
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|factor
operator|.
name|getNumber
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|GenesisUtil
operator|.
name|createDatanodeDetails
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numPipelines
condition|;
name|i
operator|++
control|)
block|{
name|Pipeline
name|pipeline
init|=
name|Pipeline
operator|.
name|newBuilder
argument_list|()
operator|.
name|setState
argument_list|(
name|Pipeline
operator|.
name|PipelineState
operator|.
name|OPEN
argument_list|)
operator|.
name|setId
argument_list|(
name|PipelineID
operator|.
name|randomId
argument_list|()
argument_list|)
operator|.
name|setType
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|)
operator|.
name|setFactor
argument_list|(
name|factor
argument_list|)
operator|.
name|setNodes
argument_list|(
name|nodes
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|pipelineStore
operator|.
name|put
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
operator|.
name|getProtobuf
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|pipeline
operator|.
name|getProtobufMessage
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|pipelineStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getOm (OzoneConfiguration conf)
specifier|static
name|OzoneManager
name|getOm
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
name|OMStorage
name|omStorage
init|=
operator|new
name|OMStorage
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|SCMStorageConfig
name|scmStore
init|=
operator|new
name|SCMStorageConfig
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|omStorage
operator|.
name|getState
argument_list|()
operator|!=
name|Storage
operator|.
name|StorageState
operator|.
name|INITIALIZED
condition|)
block|{
name|omStorage
operator|.
name|setClusterId
argument_list|(
name|scmStore
operator|.
name|getClusterID
argument_list|()
argument_list|)
expr_stmt|;
name|omStorage
operator|.
name|setScmId
argument_list|(
name|scmStore
operator|.
name|getScmId
argument_list|()
argument_list|)
expr_stmt|;
name|omStorage
operator|.
name|setOmId
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|omStorage
operator|.
name|initialize
argument_list|()
expr_stmt|;
block|}
return|return
name|OzoneManager
operator|.
name|createOm
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|configureOM (Configuration conf, int numHandlers)
specifier|static
name|void
name|configureOM
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|numHandlers
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_HTTP_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_HANDLER_COUNT_KEY
argument_list|,
name|numHandlers
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

