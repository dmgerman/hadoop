begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock.kubernetes
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
operator|.
name|kubernetes
package|;
end_package

begin_import
import|import
name|io
operator|.
name|kubernetes
operator|.
name|client
operator|.
name|JSON
import|;
end_import

begin_import
import|import
name|io
operator|.
name|kubernetes
operator|.
name|client
operator|.
name|models
operator|.
name|V1PersistentVolume
import|;
end_import

begin_import
import|import
name|io
operator|.
name|kubernetes
operator|.
name|client
operator|.
name|models
operator|.
name|V1PersistentVolumeClaim
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
name|DFS_CBLOCK_ISCSI_ADVERTISED_IP
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
name|Test
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
name|Files
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

begin_comment
comment|/**  * Test the resource generation of Dynamic Provisioner.  */
end_comment

begin_class
DECL|class|TestDynamicProvisioner
specifier|public
class|class
name|TestDynamicProvisioner
block|{
annotation|@
name|Test
DECL|method|persitenceVolumeBuilder ()
specifier|public
name|void
name|persitenceVolumeBuilder
parameter_list|()
throws|throws
name|Exception
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
name|setStrings
argument_list|(
name|DFS_CBLOCK_ISCSI_ADVERTISED_IP
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
name|DynamicProvisioner
name|provisioner
init|=
operator|new
name|DynamicProvisioner
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|pvc
init|=
operator|new
name|String
argument_list|(
name|Files
operator|.
name|readAllBytes
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"/dynamicprovisioner/input1-pvc.json"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|pv
init|=
operator|new
name|String
argument_list|(
name|Files
operator|.
name|readAllBytes
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"/dynamicprovisioner/expected1-pv.json"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|JSON
name|json
init|=
operator|new
name|io
operator|.
name|kubernetes
operator|.
name|client
operator|.
name|JSON
argument_list|()
decl_stmt|;
name|V1PersistentVolumeClaim
name|claim
init|=
name|json
operator|.
name|getGson
argument_list|()
operator|.
name|fromJson
argument_list|(
name|pvc
argument_list|,
name|V1PersistentVolumeClaim
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|volumeName
init|=
name|provisioner
operator|.
name|createVolumeName
argument_list|(
name|claim
argument_list|)
decl_stmt|;
name|V1PersistentVolume
name|volume
init|=
name|provisioner
operator|.
name|persitenceVolumeBuilder
argument_list|(
name|claim
argument_list|,
name|volumeName
argument_list|)
decl_stmt|;
comment|//remove the data which should not been compared
name|V1PersistentVolume
name|expectedVolume
init|=
name|json
operator|.
name|getGson
argument_list|()
operator|.
name|fromJson
argument_list|(
name|pv
argument_list|,
name|V1PersistentVolume
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedVolume
argument_list|,
name|volume
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

