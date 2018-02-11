begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.ozone.contract
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|ozone
operator|.
name|contract
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
name|lang
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|contract
operator|.
name|AbstractFSContract
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
name|ozone
operator|.
name|Constants
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
name|server
operator|.
name|datanode
operator|.
name|DataNode
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
name|server
operator|.
name|datanode
operator|.
name|ObjectStoreHandler
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
name|MiniOzoneClassicCluster
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
name|client
operator|.
name|rest
operator|.
name|OzoneException
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
name|BucketArgs
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
name|UserArgs
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
name|VolumeArgs
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
name|utils
operator|.
name|OzoneUtils
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
name|ksm
operator|.
name|KSMConfigKeys
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
name|ScmConfigKeys
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * The contract of Ozone: only enabled if the test bucket is provided.  */
end_comment

begin_class
DECL|class|OzoneContract
class|class
name|OzoneContract
extends|extends
name|AbstractFSContract
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneClassicCluster
name|cluster
decl_stmt|;
DECL|field|storageHandler
specifier|private
specifier|static
name|StorageHandler
name|storageHandler
decl_stmt|;
DECL|field|CONTRACT_XML
specifier|private
specifier|static
specifier|final
name|String
name|CONTRACT_XML
init|=
literal|"contract/ozone.xml"
decl_stmt|;
DECL|method|OzoneContract (Configuration conf)
name|OzoneContract
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//insert the base features
name|addConfResource
argument_list|(
name|CONTRACT_XML
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getScheme ()
specifier|public
name|String
name|getScheme
parameter_list|()
block|{
return|return
name|Constants
operator|.
name|OZONE_URI_SCHEME
return|;
block|}
annotation|@
name|Override
DECL|method|getTestPath ()
specifier|public
name|Path
name|getTestPath
parameter_list|()
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
return|return
name|path
return|;
block|}
DECL|method|createCluster ()
specifier|public
specifier|static
name|void
name|createCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|addResource
argument_list|(
name|CONTRACT_XML
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniOzoneClassicCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|5
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
name|storageHandler
operator|=
operator|new
name|ObjectStoreHandler
argument_list|(
name|conf
argument_list|)
operator|.
name|getStorageHandler
argument_list|()
expr_stmt|;
block|}
DECL|method|copyClusterConfigs (String configKey)
specifier|private
name|void
name|copyClusterConfigs
parameter_list|(
name|String
name|configKey
parameter_list|)
block|{
name|getConf
argument_list|()
operator|.
name|set
argument_list|(
name|configKey
argument_list|,
name|cluster
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|configKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTestFileSystem ()
specifier|public
name|FileSystem
name|getTestFileSystem
parameter_list|()
throws|throws
name|IOException
block|{
comment|//assumes cluster is not null
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"cluster not created"
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
name|String
name|userName
init|=
literal|"user"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|adminName
init|=
literal|"admin"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|volumeName
init|=
literal|"volume"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|bucketName
init|=
literal|"bucket"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|UserArgs
name|userArgs
init|=
operator|new
name|UserArgs
argument_list|(
literal|null
argument_list|,
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|VolumeArgs
name|volumeArgs
init|=
operator|new
name|VolumeArgs
argument_list|(
name|volumeName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|volumeArgs
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|volumeArgs
operator|.
name|setAdminName
argument_list|(
name|adminName
argument_list|)
expr_stmt|;
name|BucketArgs
name|bucketArgs
init|=
operator|new
name|BucketArgs
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
try|try
block|{
name|storageHandler
operator|.
name|createVolume
argument_list|(
name|volumeArgs
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|createBucket
argument_list|(
name|bucketArgs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OzoneException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|DataNode
name|dataNode
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|int
name|port
init|=
name|dataNode
operator|.
name|getInfoPort
argument_list|()
decl_stmt|;
name|String
name|uri
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s://localhost:%d/%s/%s"
argument_list|,
name|Constants
operator|.
name|OZONE_URI_SCHEME
argument_list|,
name|port
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
name|getConf
argument_list|()
operator|.
name|set
argument_list|(
literal|"fs.defaultFS"
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|copyClusterConfigs
argument_list|(
name|KSMConfigKeys
operator|.
name|OZONE_KSM_ADDRESS_KEY
argument_list|)
expr_stmt|;
name|copyClusterConfigs
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|)
expr_stmt|;
return|return
name|FileSystem
operator|.
name|get
argument_list|(
name|getConf
argument_list|()
argument_list|)
return|;
block|}
DECL|method|destroyCluster ()
specifier|public
specifier|static
name|void
name|destroyCluster
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

