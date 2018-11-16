begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|container
operator|.
name|common
operator|.
name|transport
operator|.
name|server
operator|.
name|XceiverServerSpi
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
name|transport
operator|.
name|server
operator|.
name|ratis
operator|.
name|XceiverServerRatis
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftGroupId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|server
operator|.
name|impl
operator|.
name|RaftServerImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|server
operator|.
name|impl
operator|.
name|RaftServerProxy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|server
operator|.
name|protocol
operator|.
name|TermIndex
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|statemachine
operator|.
name|StateMachine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|statemachine
operator|.
name|impl
operator|.
name|SimpleStateMachineStorage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|statemachine
operator|.
name|impl
operator|.
name|SingleFileSnapshotInfo
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
comment|/**  * Tests Freon with Datanode restarts without waiting for pipeline to close.  */
end_comment

begin_class
DECL|class|TestFreonWithDatanodeFastRestart
specifier|public
class|class
name|TestFreonWithDatanodeFastRestart
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
name|cluster
operator|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|setHbProcessorInterval
argument_list|(
literal|1000
argument_list|)
operator|.
name|setHbInterval
argument_list|(
literal|1000
argument_list|)
operator|.
name|setNumDatanodes
argument_list|(
literal|3
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
DECL|method|testRestart ()
specifier|public
name|void
name|testRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|startFreon
argument_list|()
expr_stmt|;
name|StateMachine
name|sm
init|=
name|getStateMachine
argument_list|()
decl_stmt|;
name|TermIndex
name|termIndexBeforeRestart
init|=
name|sm
operator|.
name|getLastAppliedTermIndex
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|restartHddsDatanode
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sm
operator|=
name|getStateMachine
argument_list|()
expr_stmt|;
name|SimpleStateMachineStorage
name|storage
init|=
operator|(
name|SimpleStateMachineStorage
operator|)
name|sm
operator|.
name|getStateMachineStorage
argument_list|()
decl_stmt|;
name|SingleFileSnapshotInfo
name|snapshotInfo
init|=
name|storage
operator|.
name|getLatestSnapshot
argument_list|()
decl_stmt|;
name|TermIndex
name|termInSnapshot
init|=
name|snapshotInfo
operator|.
name|getTermIndex
argument_list|()
decl_stmt|;
name|String
name|expectedSnapFile
init|=
name|storage
operator|.
name|getSnapshotFile
argument_list|(
name|termIndexBeforeRestart
operator|.
name|getTerm
argument_list|()
argument_list|,
name|termIndexBeforeRestart
operator|.
name|getIndex
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedSnapFile
argument_list|,
name|snapshotInfo
operator|.
name|getFile
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|termInSnapshot
argument_list|,
name|termIndexBeforeRestart
argument_list|)
expr_stmt|;
comment|// After restart the term index might have progressed to apply pending
comment|// transactions.
name|TermIndex
name|termIndexAfterRestart
init|=
name|sm
operator|.
name|getLastAppliedTermIndex
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|termIndexAfterRestart
operator|.
name|getIndex
argument_list|()
operator|>=
name|termIndexBeforeRestart
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO: fix me
comment|// Give some time for the datanode to register again with SCM.
comment|// If we try to use the pipeline before the datanode registers with SCM
comment|// we end up in "NullPointerException: scmId cannot be null" in
comment|// datanode statemachine and datanode crashes.
comment|// This has to be fixed. Check HDDS-830.
comment|// Until then this sleep should help us!
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|startFreon
argument_list|()
expr_stmt|;
block|}
DECL|method|startFreon ()
specifier|private
name|void
name|startFreon
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
name|setType
argument_list|(
name|ReplicationType
operator|.
name|RATIS
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
name|setKeySize
argument_list|(
literal|20971520
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
literal|0
argument_list|,
name|randomKeyGenerator
operator|.
name|getUnsuccessfulValidationCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getStateMachine ()
specifier|private
name|StateMachine
name|getStateMachine
parameter_list|()
throws|throws
name|Exception
block|{
name|XceiverServerSpi
name|server
init|=
name|cluster
operator|.
name|getHddsDatanodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDatanodeStateMachine
argument_list|()
operator|.
name|getContainer
argument_list|()
operator|.
name|getServer
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|)
decl_stmt|;
name|RaftServerProxy
name|proxy
init|=
call|(
name|RaftServerProxy
call|)
argument_list|(
operator|(
operator|(
name|XceiverServerRatis
operator|)
name|server
operator|)
operator|.
name|getServer
argument_list|()
argument_list|)
decl_stmt|;
name|RaftGroupId
name|groupId
init|=
name|proxy
operator|.
name|getGroupIds
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|RaftServerImpl
name|impl
init|=
name|proxy
operator|.
name|getImpl
argument_list|(
name|groupId
argument_list|)
decl_stmt|;
return|return
name|impl
operator|.
name|getStateMachine
argument_list|()
return|;
block|}
block|}
end_class

end_unit

