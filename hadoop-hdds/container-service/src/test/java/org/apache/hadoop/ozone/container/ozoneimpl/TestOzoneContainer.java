begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.ozoneimpl
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
name|ozoneimpl
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|ContainerSet
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
name|volume
operator|.
name|RoundRobinVolumeChoosingPolicy
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
name|volume
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
name|ozone
operator|.
name|container
operator|.
name|keyvalue
operator|.
name|KeyValueContainer
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
name|keyvalue
operator|.
name|KeyValueContainerData
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
name|TemporaryFolder
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
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * This class is used to test OzoneContainer.  */
end_comment

begin_class
DECL|class|TestOzoneContainer
specifier|public
class|class
name|TestOzoneContainer
block|{
annotation|@
name|Rule
DECL|field|folder
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
DECL|field|conf
specifier|private
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|scmId
specifier|private
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
DECL|field|volumeSet
specifier|private
name|VolumeSet
name|volumeSet
decl_stmt|;
DECL|field|volumeChoosingPolicy
specifier|private
name|RoundRobinVolumeChoosingPolicy
name|volumeChoosingPolicy
decl_stmt|;
DECL|field|keyValueContainerData
specifier|private
name|KeyValueContainerData
name|keyValueContainerData
decl_stmt|;
DECL|field|keyValueContainer
specifier|private
name|KeyValueContainer
name|keyValueContainer
decl_stmt|;
DECL|field|datanodeDetails
specifier|private
specifier|final
name|DatanodeDetails
name|datanodeDetails
init|=
name|createDatanodeDetails
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
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
name|conf
operator|.
name|set
argument_list|(
name|ScmConfigKeys
operator|.
name|HDDS_DATANODE_DIR_KEY
argument_list|,
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|","
operator|+
name|folder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|folder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|volumeSet
operator|=
operator|new
name|VolumeSet
argument_list|(
name|datanodeDetails
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|volumeChoosingPolicy
operator|=
operator|new
name|RoundRobinVolumeChoosingPolicy
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|keyValueContainerData
operator|=
operator|new
name|KeyValueContainerData
argument_list|(
name|i
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|=
operator|new
name|KeyValueContainer
argument_list|(
name|keyValueContainerData
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|.
name|create
argument_list|(
name|volumeSet
argument_list|,
name|volumeChoosingPolicy
argument_list|,
name|scmId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBuildContainerMap ()
specifier|public
name|void
name|testBuildContainerMap
parameter_list|()
throws|throws
name|Exception
block|{
name|OzoneContainer
name|ozoneContainer
init|=
operator|new
name|OzoneContainer
argument_list|(
name|datanodeDetails
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|ContainerSet
name|containerset
init|=
name|ozoneContainer
operator|.
name|getContainerSet
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|containerset
operator|.
name|containerCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createDatanodeDetails ()
specifier|private
name|DatanodeDetails
name|createDatanodeDetails
parameter_list|()
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
name|String
name|uuid
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
name|hostName
init|=
name|uuid
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
block|}
end_class

end_unit

