begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.hdds.scm.container.metrics
package|package
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
name|container
operator|.
name|metrics
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
name|RandomUtils
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
name|hdds
operator|.
name|scm
operator|.
name|XceiverClientManager
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
name|container
operator|.
name|ContainerID
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
name|container
operator|.
name|ContainerInfo
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
name|container
operator|.
name|ContainerManager
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
name|metrics2
operator|.
name|MetricsRecordBuilder
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
name|junit
operator|.
name|After
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
name|Before
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
name|io
operator|.
name|IOException
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|getLongCounter
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|getMetrics
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Class used to test {@link SCMContainerManagerMetrics}.  */
end_comment

begin_class
DECL|class|TestSCMContainerManagerMetrics
specifier|public
class|class
name|TestSCMContainerManagerMetrics
block|{
DECL|field|cluster
specifier|private
name|MiniOzoneCluster
name|cluster
decl_stmt|;
DECL|field|scm
specifier|private
name|StorageContainerManager
name|scm
decl_stmt|;
DECL|field|xceiverClientManager
specifier|private
name|XceiverClientManager
name|xceiverClientManager
decl_stmt|;
DECL|field|containerOwner
specifier|private
name|String
name|containerOwner
init|=
literal|"OZONE"
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
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
literal|1
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
name|scm
operator|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
expr_stmt|;
name|xceiverClientManager
operator|=
operator|new
name|XceiverClientManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainerOpsMetrics ()
specifier|public
name|void
name|testContainerOpsMetrics
parameter_list|()
throws|throws
name|IOException
block|{
name|MetricsRecordBuilder
name|metrics
decl_stmt|;
name|ContainerManager
name|containerManager
init|=
name|scm
operator|.
name|getContainerManager
argument_list|()
decl_stmt|;
name|metrics
operator|=
name|getMetrics
argument_list|(
name|SCMContainerManagerMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|numSuccessfulCreateContainers
init|=
name|getLongCounter
argument_list|(
literal|"NumSuccessfulCreateContainers"
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|ContainerInfo
name|containerInfo
init|=
name|containerManager
operator|.
name|allocateContainer
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
name|metrics
operator|=
name|getMetrics
argument_list|(
name|SCMContainerManagerMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|getLongCounter
argument_list|(
literal|"NumSuccessfulCreateContainers"
argument_list|,
name|metrics
argument_list|)
argument_list|,
operator|++
name|numSuccessfulCreateContainers
argument_list|)
expr_stmt|;
try|try
block|{
name|containerManager
operator|.
name|allocateContainer
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
argument_list|,
name|containerOwner
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testContainerOpsMetrics failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// Here it should fail, so it should have the old metric value.
name|metrics
operator|=
name|getMetrics
argument_list|(
name|SCMContainerManagerMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|getLongCounter
argument_list|(
literal|"NumSuccessfulCreateContainers"
argument_list|,
name|metrics
argument_list|)
argument_list|,
name|numSuccessfulCreateContainers
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|getLongCounter
argument_list|(
literal|"NumFailureCreateContainers"
argument_list|,
name|metrics
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|metrics
operator|=
name|getMetrics
argument_list|(
name|SCMContainerManagerMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|numSuccessfulDeleteContainers
init|=
name|getLongCounter
argument_list|(
literal|"NumSuccessfulDeleteContainers"
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|containerManager
operator|.
name|deleteContainer
argument_list|(
operator|new
name|ContainerID
argument_list|(
name|containerInfo
operator|.
name|getContainerID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|=
name|getMetrics
argument_list|(
name|SCMContainerManagerMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|getLongCounter
argument_list|(
literal|"NumSuccessfulDeleteContainers"
argument_list|,
name|metrics
argument_list|)
argument_list|,
name|numSuccessfulDeleteContainers
operator|+
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Give random container to delete.
name|containerManager
operator|.
name|deleteContainer
argument_list|(
operator|new
name|ContainerID
argument_list|(
name|RandomUtils
operator|.
name|nextLong
argument_list|(
literal|10000
argument_list|,
literal|20000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testContainerOpsMetrics failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// Here it should fail, so it should have the old metric value.
name|metrics
operator|=
name|getMetrics
argument_list|(
name|SCMContainerManagerMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|getLongCounter
argument_list|(
literal|"NumSuccessfulDeleteContainers"
argument_list|,
name|metrics
argument_list|)
argument_list|,
name|numSuccessfulCreateContainers
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|getLongCounter
argument_list|(
literal|"NumFailureDeleteContainers"
argument_list|,
name|metrics
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|containerManager
operator|.
name|listContainer
argument_list|(
operator|new
name|ContainerID
argument_list|(
name|containerInfo
operator|.
name|getContainerID
argument_list|()
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|metrics
operator|=
name|getMetrics
argument_list|(
name|SCMContainerManagerMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|getLongCounter
argument_list|(
literal|"NumListContainerOps"
argument_list|,
name|metrics
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

