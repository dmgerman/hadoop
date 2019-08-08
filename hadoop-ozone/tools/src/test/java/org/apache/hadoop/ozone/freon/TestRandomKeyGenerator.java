begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.freon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|freon
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
name|client
operator|.
name|ReplicationFactor
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
name|client
operator|.
name|ReplicationType
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
name|ozone
operator|.
name|MiniOzoneCluster
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
name|junit
operator|.
name|AfterClass
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
name|BeforeClass
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
comment|/**  * Tests Freon, with MiniOzoneCluster.  */
end_comment

begin_class
DECL|class|TestRandomKeyGenerator
specifier|public
class|class
name|TestRandomKeyGenerator
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
decl_stmt|;
comment|/**    * Create a MiniDFSCluster for testing.    *<p>    * Ozone is made active by setting OZONE_ENABLED = true    *    */
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
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
name|OzoneConfigKeys
operator|.
name|OZONE_CLIENT_WATCH_REQUEST_TIMEOUT
argument_list|,
literal|"5000ms"
argument_list|)
expr_stmt|;
name|cluster
operator|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|setNumDatanodes
argument_list|(
literal|5
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
block|}
comment|/**    * Shutdown MiniDFSCluster.    */
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
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
block|}
block|}
annotation|@
name|Test
DECL|method|defaultTest ()
specifier|public
name|void
name|defaultTest
parameter_list|()
throws|throws
name|Exception
block|{
name|RandomKeyGenerator
name|randomKeyGenerator
init|=
operator|new
name|RandomKeyGenerator
argument_list|(
operator|(
name|OzoneConfiguration
operator|)
name|cluster
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfVolumes
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfBuckets
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfKeys
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setFactor
argument_list|(
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setType
argument_list|(
name|ReplicationType
operator|.
name|RATIS
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|call
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|randomKeyGenerator
operator|.
name|getNumberOfVolumesCreated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|randomKeyGenerator
operator|.
name|getNumberOfBucketsCreated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|randomKeyGenerator
operator|.
name|getNumberOfKeysAdded
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|multiThread ()
specifier|public
name|void
name|multiThread
parameter_list|()
throws|throws
name|Exception
block|{
name|RandomKeyGenerator
name|randomKeyGenerator
init|=
operator|new
name|RandomKeyGenerator
argument_list|(
operator|(
name|OzoneConfiguration
operator|)
name|cluster
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfVolumes
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfBuckets
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfKeys
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfThreads
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setKeySize
argument_list|(
literal|10240
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setFactor
argument_list|(
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setType
argument_list|(
name|ReplicationType
operator|.
name|RATIS
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|call
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|randomKeyGenerator
operator|.
name|getNumberOfVolumesCreated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|randomKeyGenerator
operator|.
name|getNumberOfBucketsCreated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|randomKeyGenerator
operator|.
name|getNumberOfKeysAdded
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|ratisTest3 ()
specifier|public
name|void
name|ratisTest3
parameter_list|()
throws|throws
name|Exception
block|{
name|RandomKeyGenerator
name|randomKeyGenerator
init|=
operator|new
name|RandomKeyGenerator
argument_list|(
operator|(
name|OzoneConfiguration
operator|)
name|cluster
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfVolumes
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfBuckets
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfKeys
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfThreads
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setKeySize
argument_list|(
literal|10240
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setFactor
argument_list|(
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setType
argument_list|(
name|ReplicationType
operator|.
name|RATIS
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|call
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|randomKeyGenerator
operator|.
name|getNumberOfVolumesCreated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|randomKeyGenerator
operator|.
name|getNumberOfBucketsCreated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|randomKeyGenerator
operator|.
name|getNumberOfKeysAdded
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|bigFileThan2GB ()
specifier|public
name|void
name|bigFileThan2GB
parameter_list|()
throws|throws
name|Exception
block|{
name|RandomKeyGenerator
name|randomKeyGenerator
init|=
operator|new
name|RandomKeyGenerator
argument_list|(
operator|(
name|OzoneConfiguration
operator|)
name|cluster
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfVolumes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfBuckets
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfKeys
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfThreads
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setKeySize
argument_list|(
literal|10L
operator|+
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setFactor
argument_list|(
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setType
argument_list|(
name|ReplicationType
operator|.
name|RATIS
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setValidateWrites
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|call
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|randomKeyGenerator
operator|.
name|getNumberOfVolumesCreated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|randomKeyGenerator
operator|.
name|getNumberOfBucketsCreated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|randomKeyGenerator
operator|.
name|getNumberOfKeysAdded
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|randomKeyGenerator
operator|.
name|getSuccessfulValidationCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|fileWithSizeZero ()
specifier|public
name|void
name|fileWithSizeZero
parameter_list|()
throws|throws
name|Exception
block|{
name|RandomKeyGenerator
name|randomKeyGenerator
init|=
operator|new
name|RandomKeyGenerator
argument_list|(
operator|(
name|OzoneConfiguration
operator|)
name|cluster
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfVolumes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfBuckets
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfKeys
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfThreads
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setKeySize
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setFactor
argument_list|(
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setType
argument_list|(
name|ReplicationType
operator|.
name|RATIS
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setValidateWrites
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|call
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|randomKeyGenerator
operator|.
name|getNumberOfVolumesCreated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|randomKeyGenerator
operator|.
name|getNumberOfBucketsCreated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|randomKeyGenerator
operator|.
name|getNumberOfKeysAdded
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|randomKeyGenerator
operator|.
name|getSuccessfulValidationCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testThreadPoolSize ()
specifier|public
name|void
name|testThreadPoolSize
parameter_list|()
throws|throws
name|Exception
block|{
name|RandomKeyGenerator
name|randomKeyGenerator
init|=
operator|new
name|RandomKeyGenerator
argument_list|(
operator|(
name|OzoneConfiguration
operator|)
name|cluster
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfVolumes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfBuckets
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfKeys
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setFactor
argument_list|(
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setType
argument_list|(
name|ReplicationType
operator|.
name|RATIS
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|setNumOfThreads
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|randomKeyGenerator
operator|.
name|call
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|randomKeyGenerator
operator|.
name|getThreadPoolSize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|randomKeyGenerator
operator|.
name|getNumberOfKeysAdded
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

