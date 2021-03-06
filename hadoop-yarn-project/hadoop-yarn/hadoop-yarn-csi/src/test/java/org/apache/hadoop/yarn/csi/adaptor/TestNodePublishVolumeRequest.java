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
name|NodePublishVolumeRequestPBImpl
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
name|proto
operator|.
name|CsiAdaptorProtos
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
name|proto
operator|.
name|CsiAdaptorProtos
operator|.
name|VolumeCapability
operator|.
name|AccessMode
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
name|proto
operator|.
name|CsiAdaptorProtos
operator|.
name|VolumeCapability
operator|.
name|VolumeType
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

begin_comment
comment|/**  * UT for NodePublishVolumeRequest.  */
end_comment

begin_class
DECL|class|TestNodePublishVolumeRequest
specifier|public
class|class
name|TestNodePublishVolumeRequest
block|{
annotation|@
name|Test
DECL|method|testPBRecord ()
specifier|public
name|void
name|testPBRecord
parameter_list|()
block|{
name|CsiAdaptorProtos
operator|.
name|VolumeCapability
name|capability
init|=
name|CsiAdaptorProtos
operator|.
name|VolumeCapability
operator|.
name|newBuilder
argument_list|()
operator|.
name|setAccessMode
argument_list|(
name|AccessMode
operator|.
name|MULTI_NODE_READER_ONLY
argument_list|)
operator|.
name|setVolumeType
argument_list|(
name|VolumeType
operator|.
name|FILE_SYSTEM
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|CsiAdaptorProtos
operator|.
name|NodePublishVolumeRequest
name|proto
init|=
name|CsiAdaptorProtos
operator|.
name|NodePublishVolumeRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|setReadonly
argument_list|(
literal|false
argument_list|)
operator|.
name|setVolumeId
argument_list|(
literal|"test-vol-000001"
argument_list|)
operator|.
name|setTargetPath
argument_list|(
literal|"/mnt/data"
argument_list|)
operator|.
name|setStagingTargetPath
argument_list|(
literal|"/mnt/staging"
argument_list|)
operator|.
name|setVolumeCapability
argument_list|(
name|capability
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NodePublishVolumeRequestPBImpl
name|pbImpl
init|=
operator|new
name|NodePublishVolumeRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test-vol-000001"
argument_list|,
name|pbImpl
operator|.
name|getVolumeId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/mnt/data"
argument_list|,
name|pbImpl
operator|.
name|getTargetPath
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/mnt/staging"
argument_list|,
name|pbImpl
operator|.
name|getStagingPath
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|pbImpl
operator|.
name|getReadOnly
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

