begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_DEFAULT
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|ReconfigurationException
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
name|CommonConfigurationKeys
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
name|FileUtil
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
name|DFSConfigKeys
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
name|HdfsConfiguration
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
name|MiniDFSCluster
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
name|MiniDFSNNTopology
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

begin_comment
comment|/**  * Test to reconfigure some parameters for DataNode without restart  */
end_comment

begin_class
DECL|class|TestDataNodeReconfiguration
specifier|public
class|class
name|TestDataNodeReconfiguration
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestBlockRecovery
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DATA_DIR
specifier|private
specifier|static
specifier|final
name|String
name|DATA_DIR
init|=
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
operator|+
literal|"data"
decl_stmt|;
DECL|field|NN_ADDR
specifier|private
specifier|final
specifier|static
name|InetSocketAddress
name|NN_ADDR
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
literal|5020
argument_list|)
decl_stmt|;
DECL|field|NUM_NAME_NODE
specifier|private
specifier|final
name|int
name|NUM_NAME_NODE
init|=
literal|1
decl_stmt|;
DECL|field|NUM_DATA_NODE
specifier|private
specifier|final
name|int
name|NUM_DATA_NODE
init|=
literal|10
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
annotation|@
name|Before
DECL|method|Setup ()
specifier|public
name|void
name|Setup
parameter_list|()
throws|throws
name|IOException
block|{
name|startDFSCluster
argument_list|(
name|NUM_NAME_NODE
argument_list|,
name|NUM_DATA_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
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
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|DATA_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir
operator|.
name|exists
argument_list|()
condition|)
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Cannot delete data-node dirs"
argument_list|,
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|startDFSCluster (int numNameNodes, int numDataNodes)
specifier|private
name|void
name|startDFSCluster
parameter_list|(
name|int
name|numNameNodes
parameter_list|,
name|int
name|numDataNodes
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
name|MiniDFSNNTopology
name|nnTopology
init|=
name|MiniDFSNNTopology
operator|.
name|simpleFederatedTopology
argument_list|(
name|numNameNodes
argument_list|)
decl_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|nnTopology
argument_list|(
name|nnTopology
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numDataNodes
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
block|}
comment|/**    * Starts an instance of DataNode    *    * @throws IOException    */
DECL|method|createDNsForTest (int numDateNode)
specifier|public
name|DataNode
index|[]
name|createDNsForTest
parameter_list|(
name|int
name|numDateNode
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|DATA_DIR
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_ADDRESS_KEY
argument_list|,
literal|"0.0.0.0:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTP_ADDRESS_KEY
argument_list|,
literal|"0.0.0.0:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_IPC_ADDRESS_KEY
argument_list|,
literal|"0.0.0.0:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DataNode
index|[]
name|result
init|=
operator|new
name|DataNode
index|[
name|numDateNode
index|]
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
name|numDateNode
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|DataNodeTestUtils
operator|.
name|startDNWithMockNN
argument_list|(
name|conf
argument_list|,
name|NN_ADDR
argument_list|,
name|DATA_DIR
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Test
DECL|method|testMaxConcurrentMoversReconfiguration ()
specifier|public
name|void
name|testMaxConcurrentMoversReconfiguration
parameter_list|()
throws|throws
name|ReconfigurationException
throws|,
name|IOException
block|{
name|int
name|maxConcurrentMovers
init|=
literal|10
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
name|NUM_DATA_NODE
condition|;
name|i
operator|++
control|)
block|{
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// try invalid values
try|try
block|{
name|dn
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ReconfigurationException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"expecting NumberFormatException"
argument_list|,
name|expected
operator|.
name|getCause
argument_list|()
operator|instanceof
name|NumberFormatException
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|dn
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ReconfigurationException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"expecting IllegalArgumentException"
argument_list|,
name|expected
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IllegalArgumentException
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|dn
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ReconfigurationException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"expecting IllegalArgumentException"
argument_list|,
name|expected
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IllegalArgumentException
argument_list|)
expr_stmt|;
block|}
comment|// change properties
name|dn
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|maxConcurrentMovers
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify change
name|assertEquals
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s has wrong value"
argument_list|,
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY
argument_list|)
argument_list|,
name|maxConcurrentMovers
argument_list|,
name|dn
operator|.
name|xserver
operator|.
name|balanceThrottler
operator|.
name|getMaxConcurrentMovers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s has wrong value"
argument_list|,
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY
argument_list|)
argument_list|,
name|maxConcurrentMovers
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|dn
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// revert to default
name|dn
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// verify default
name|assertEquals
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s has wrong value"
argument_list|,
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY
argument_list|)
argument_list|,
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_DEFAULT
argument_list|,
name|dn
operator|.
name|xserver
operator|.
name|balanceThrottler
operator|.
name|getMaxConcurrentMovers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"expect %s is not configured"
argument_list|,
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY
argument_list|)
argument_list|,
literal|null
argument_list|,
name|dn
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAcquireWithMaxConcurrentMoversGreaterThanDefault ()
specifier|public
name|void
name|testAcquireWithMaxConcurrentMoversGreaterThanDefault
parameter_list|()
throws|throws
name|IOException
throws|,
name|ReconfigurationException
block|{
name|testAcquireWithMaxConcurrentMoversShared
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAcquireWithMaxConcurrentMoversLessThanDefault ()
specifier|public
name|void
name|testAcquireWithMaxConcurrentMoversLessThanDefault
parameter_list|()
throws|throws
name|IOException
throws|,
name|ReconfigurationException
block|{
name|testAcquireWithMaxConcurrentMoversShared
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|testAcquireWithMaxConcurrentMoversShared ( int maxConcurrentMovers)
specifier|private
name|void
name|testAcquireWithMaxConcurrentMoversShared
parameter_list|(
name|int
name|maxConcurrentMovers
parameter_list|)
throws|throws
name|IOException
throws|,
name|ReconfigurationException
block|{
name|DataNode
index|[]
name|dns
init|=
literal|null
decl_stmt|;
try|try
block|{
name|dns
operator|=
name|createDNsForTest
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|testAcquireOnMaxConcurrentMoversReconfiguration
argument_list|(
name|dns
index|[
literal|0
index|]
argument_list|,
name|maxConcurrentMovers
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
name|ioe
throw|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|re
parameter_list|)
block|{
throw|throw
name|re
throw|;
block|}
finally|finally
block|{
name|shutDownDNs
argument_list|(
name|dns
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|shutDownDNs (DataNode[] dns)
specifier|private
name|void
name|shutDownDNs
parameter_list|(
name|DataNode
index|[]
name|dns
parameter_list|)
block|{
if|if
condition|(
name|dns
operator|==
literal|null
condition|)
block|{
return|return;
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
name|dns
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
if|if
condition|(
name|dns
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|dns
index|[
name|i
index|]
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot close: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testAcquireOnMaxConcurrentMoversReconfiguration ( DataNode dataNode, int maxConcurrentMovers)
specifier|private
name|void
name|testAcquireOnMaxConcurrentMoversReconfiguration
parameter_list|(
name|DataNode
name|dataNode
parameter_list|,
name|int
name|maxConcurrentMovers
parameter_list|)
throws|throws
name|IOException
throws|,
name|ReconfigurationException
block|{
name|int
name|defaultMaxThreads
init|=
name|dataNode
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_DEFAULT
argument_list|)
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
name|defaultMaxThreads
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"should be able to get thread quota"
argument_list|,
literal|true
argument_list|,
name|dataNode
operator|.
name|xserver
operator|.
name|balanceThrottler
operator|.
name|acquire
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"should not be able to get thread quota"
argument_list|,
literal|false
argument_list|,
name|dataNode
operator|.
name|xserver
operator|.
name|balanceThrottler
operator|.
name|acquire
argument_list|()
argument_list|)
expr_stmt|;
comment|// change properties
name|dataNode
operator|.
name|reconfigureProperty
argument_list|(
name|DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|maxConcurrentMovers
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"thread quota is wrong"
argument_list|,
name|maxConcurrentMovers
argument_list|,
name|dataNode
operator|.
name|xserver
operator|.
name|balanceThrottler
operator|.
name|getMaxConcurrentMovers
argument_list|()
argument_list|)
expr_stmt|;
comment|// thread quota
name|int
name|val
init|=
name|Math
operator|.
name|abs
argument_list|(
name|maxConcurrentMovers
operator|-
name|defaultMaxThreads
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultMaxThreads
operator|<
name|maxConcurrentMovers
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|val
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"should be able to get thread quota"
argument_list|,
literal|true
argument_list|,
name|dataNode
operator|.
name|xserver
operator|.
name|balanceThrottler
operator|.
name|acquire
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|defaultMaxThreads
operator|>
name|maxConcurrentMovers
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|val
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"should not be able to get thread quota"
argument_list|,
literal|false
argument_list|,
name|dataNode
operator|.
name|xserver
operator|.
name|balanceThrottler
operator|.
name|acquire
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"should not be able to get thread quota"
argument_list|,
literal|false
argument_list|,
name|dataNode
operator|.
name|xserver
operator|.
name|balanceThrottler
operator|.
name|acquire
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

