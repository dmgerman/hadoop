begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|assertGauge
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
name|io
operator|.
name|PipedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PipedOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServerConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|FSDataOutputStream
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
name|hdfs
operator|.
name|DFSTestUtil
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
name|tools
operator|.
name|JMXGet
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
comment|/**  * Startup and checkpoint tests  *   */
end_comment

begin_class
DECL|class|TestJMXGet
specifier|public
class|class
name|TestJMXGet
block|{
DECL|field|config
specifier|private
name|Configuration
name|config
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|seed
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0xAAAAEEFL
decl_stmt|;
DECL|field|blockSize
specifier|static
specifier|final
name|int
name|blockSize
init|=
literal|4096
decl_stmt|;
DECL|field|fileSize
specifier|static
specifier|final
name|int
name|fileSize
init|=
literal|8192
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
name|config
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
block|}
comment|/**    * clean up    */
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
if|if
condition|(
name|cluster
operator|.
name|isClusterUp
argument_list|()
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|File
name|data_dir
init|=
operator|new
name|File
argument_list|(
name|cluster
operator|.
name|getDataDirectory
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|data_dir
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|data_dir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not delete hdfs directory in tearDown '"
operator|+
name|data_dir
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * test JMX connection to NameNode..    * @throws Exception     */
annotation|@
name|Test
DECL|method|testNameNode ()
specifier|public
name|void
name|testNameNode
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numDatanodes
init|=
literal|2
decl_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|config
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numDatanodes
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
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/test1"
argument_list|)
argument_list|,
name|fileSize
argument_list|,
name|fileSize
argument_list|,
name|blockSize
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|JMXGet
name|jmx
init|=
operator|new
name|JMXGet
argument_list|()
decl_stmt|;
name|String
name|serviceName
init|=
literal|"NameNode"
decl_stmt|;
name|jmx
operator|.
name|setService
argument_list|(
name|serviceName
argument_list|)
expr_stmt|;
name|jmx
operator|.
name|init
argument_list|()
expr_stmt|;
comment|// default lists namenode mbeans only
name|assertTrue
argument_list|(
literal|"error printAllValues"
argument_list|,
name|checkPrintAllValues
argument_list|(
name|jmx
argument_list|)
argument_list|)
expr_stmt|;
comment|//get some data from different source
name|DFSTestUtil
operator|.
name|waitForMetric
argument_list|(
name|jmx
argument_list|,
literal|"NumLiveDataNodes"
argument_list|,
name|numDatanodes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDatanodes
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|jmx
operator|.
name|getValue
argument_list|(
literal|"NumLiveDataNodes"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"CorruptBlocks"
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|jmx
operator|.
name|getValue
argument_list|(
literal|"CorruptBlocks"
argument_list|)
argument_list|)
argument_list|,
name|getMetrics
argument_list|(
literal|"FSNamesystem"
argument_list|)
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitForMetric
argument_list|(
name|jmx
argument_list|,
literal|"NumOpenConnections"
argument_list|,
name|numDatanodes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDatanodes
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|jmx
operator|.
name|getValue
argument_list|(
literal|"NumOpenConnections"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|MBeanServerConnection
name|mbsc
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|ObjectName
name|query
init|=
operator|new
name|ObjectName
argument_list|(
literal|"Hadoop:service="
operator|+
name|serviceName
operator|+
literal|",*"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|names
init|=
name|mbsc
operator|.
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"No beans should be registered for "
operator|+
name|serviceName
argument_list|,
name|names
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkPrintAllValues (JMXGet jmx)
specifier|private
specifier|static
name|boolean
name|checkPrintAllValues
parameter_list|(
name|JMXGet
name|jmx
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|size
init|=
literal|0
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
literal|null
decl_stmt|;
name|String
name|pattern
init|=
literal|"List of all the available keys:"
decl_stmt|;
name|PipedOutputStream
name|pipeOut
init|=
operator|new
name|PipedOutputStream
argument_list|()
decl_stmt|;
name|PipedInputStream
name|pipeIn
init|=
operator|new
name|PipedInputStream
argument_list|(
name|pipeOut
argument_list|)
decl_stmt|;
name|PrintStream
name|oldErr
init|=
name|System
operator|.
name|err
decl_stmt|;
name|System
operator|.
name|setErr
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|pipeOut
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|jmx
operator|.
name|printAllValues
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|size
operator|=
name|pipeIn
operator|.
name|available
argument_list|()
operator|)
operator|!=
literal|0
condition|)
block|{
name|bytes
operator|=
operator|new
name|byte
index|[
name|size
index|]
expr_stmt|;
name|pipeIn
operator|.
name|read
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|pipeOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|pipeIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setErr
argument_list|(
name|oldErr
argument_list|)
expr_stmt|;
block|}
return|return
name|bytes
operator|!=
literal|null
condition|?
operator|new
name|String
argument_list|(
name|bytes
argument_list|)
operator|.
name|contains
argument_list|(
name|pattern
argument_list|)
else|:
literal|false
return|;
block|}
comment|/**    * test JMX connection to DataNode..    * @throws Exception     */
annotation|@
name|Test
DECL|method|testDataNode ()
specifier|public
name|void
name|testDataNode
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numDatanodes
init|=
literal|2
decl_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|config
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numDatanodes
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
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
argument_list|,
name|fileSize
argument_list|,
name|fileSize
argument_list|,
name|blockSize
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|JMXGet
name|jmx
init|=
operator|new
name|JMXGet
argument_list|()
decl_stmt|;
name|String
name|serviceName
init|=
literal|"DataNode"
decl_stmt|;
name|jmx
operator|.
name|setService
argument_list|(
name|serviceName
argument_list|)
expr_stmt|;
name|jmx
operator|.
name|init
argument_list|()
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitForMetric
argument_list|(
name|jmx
argument_list|,
literal|"BytesWritten"
argument_list|,
name|fileSize
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fileSize
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|jmx
operator|.
name|getValue
argument_list|(
literal|"BytesWritten"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|MBeanServerConnection
name|mbsc
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|ObjectName
name|query
init|=
operator|new
name|ObjectName
argument_list|(
literal|"Hadoop:service="
operator|+
name|serviceName
operator|+
literal|",*"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|names
init|=
name|mbsc
operator|.
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"No beans should be registered for "
operator|+
name|serviceName
argument_list|,
name|names
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

