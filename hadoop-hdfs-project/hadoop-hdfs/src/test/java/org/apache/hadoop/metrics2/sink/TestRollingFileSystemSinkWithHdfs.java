begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.sink
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|sink
package|;
end_package

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
name|URI
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
name|java
operator|.
name|util
operator|.
name|Calendar
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
name|FileStatus
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
name|metrics2
operator|.
name|MetricsSystem
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
name|sink
operator|.
name|RollingFileSystemSinkTestBase
operator|.
name|MyMetrics1
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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

begin_comment
comment|/**  * Test the {@link RollingFileSystemSink} class in the context of HDFS.  */
end_comment

begin_class
DECL|class|TestRollingFileSystemSinkWithHdfs
specifier|public
class|class
name|TestRollingFileSystemSinkWithHdfs
extends|extends
name|RollingFileSystemSinkTestBase
block|{
DECL|field|NUM_DATANODES
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DATANODES
init|=
literal|4
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
comment|/**    * Create a {@link MiniDFSCluster} instance with four nodes.  The    * node count is required to allow append to function. Also clear the    * sink's test flags.    *    * @throws IOException thrown if cluster creation fails    */
annotation|@
name|Before
DECL|method|setupHdfs ()
specifier|public
name|void
name|setupHdfs
parameter_list|()
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
comment|// It appears that since HDFS-265, append is always enabled.
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
name|numDataNodes
argument_list|(
name|NUM_DATANODES
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// Also clear sink flags
name|RollingFileSystemSink
operator|.
name|flushQuickly
operator|=
literal|false
expr_stmt|;
name|RollingFileSystemSink
operator|.
name|hasFlushed
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * Stop the {@link MiniDFSCluster}.    */
annotation|@
name|After
DECL|method|shutdownHdfs ()
specifier|public
name|void
name|shutdownHdfs
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
comment|/**    * Test writing logs to HDFS.    *    * @throws Exception thrown when things break    */
annotation|@
name|Test
DECL|method|testWrite ()
specifier|public
name|void
name|testWrite
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"hdfs://"
operator|+
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getHostAndPort
argument_list|()
operator|+
literal|"/tmp"
decl_stmt|;
name|MetricsSystem
name|ms
init|=
name|initMetricsSystem
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertMetricsContents
argument_list|(
name|doWriteTest
argument_list|(
name|ms
argument_list|,
name|path
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test writing logs to HDFS if append is enabled and the log file already    * exists.    *    * @throws Exception thrown when things break    */
annotation|@
name|Test
DECL|method|testAppend ()
specifier|public
name|void
name|testAppend
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"hdfs://"
operator|+
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getHostAndPort
argument_list|()
operator|+
literal|"/tmp"
decl_stmt|;
name|assertExtraContents
argument_list|(
name|doAppendTest
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test writing logs to HDFS if append is enabled, the log file already    * exists, and the sink is set to ignore errors.    *    * @throws Exception thrown when things break    */
annotation|@
name|Test
DECL|method|testSilentAppend ()
specifier|public
name|void
name|testSilentAppend
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"hdfs://"
operator|+
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getHostAndPort
argument_list|()
operator|+
literal|"/tmp"
decl_stmt|;
name|assertExtraContents
argument_list|(
name|doAppendTest
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test writing logs to HDFS without append enabled, when the log file already    * exists.    *    * @throws Exception thrown when things break    */
annotation|@
name|Test
DECL|method|testNoAppend ()
specifier|public
name|void
name|testNoAppend
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"hdfs://"
operator|+
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getHostAndPort
argument_list|()
operator|+
literal|"/tmp"
decl_stmt|;
name|assertMetricsContents
argument_list|(
name|doAppendTest
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test writing logs to HDFS without append enabled, with ignore errors    * enabled, and when the log file already exists.    *    * @throws Exception thrown when things break    */
annotation|@
name|Test
DECL|method|testSilentOverwrite ()
specifier|public
name|void
name|testSilentOverwrite
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"hdfs://"
operator|+
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getHostAndPort
argument_list|()
operator|+
literal|"/tmp"
decl_stmt|;
name|assertMetricsContents
argument_list|(
name|doAppendTest
argument_list|(
name|path
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that writing to HDFS fails when HDFS is unavailable.    *    * @throws IOException thrown when reading or writing log files    */
annotation|@
name|Test
DECL|method|testFailedWrite ()
specifier|public
name|void
name|testFailedWrite
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|path
init|=
literal|"hdfs://"
operator|+
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getHostAndPort
argument_list|()
operator|+
literal|"/tmp"
decl_stmt|;
name|MetricsSystem
name|ms
init|=
name|initMetricsSystem
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
operator|new
name|MyMetrics1
argument_list|()
operator|.
name|registerWith
argument_list|(
name|ms
argument_list|)
expr_stmt|;
name|shutdownHdfs
argument_list|()
expr_stmt|;
name|MockSink
operator|.
name|errored
operator|=
literal|false
expr_stmt|;
name|ms
operator|.
name|publishMetricsNow
argument_list|()
expr_stmt|;
comment|// publish the metrics
name|assertTrue
argument_list|(
literal|"No exception was generated while writing metrics "
operator|+
literal|"even though HDFS was unavailable"
argument_list|,
name|MockSink
operator|.
name|errored
argument_list|)
expr_stmt|;
name|ms
operator|.
name|stop
argument_list|()
expr_stmt|;
name|ms
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test that closing a file in HDFS fails when HDFS is unavailable.    *    * @throws IOException thrown when reading or writing log files    */
annotation|@
name|Test
DECL|method|testFailedClose ()
specifier|public
name|void
name|testFailedClose
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|path
init|=
literal|"hdfs://"
operator|+
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getHostAndPort
argument_list|()
operator|+
literal|"/tmp"
decl_stmt|;
name|MetricsSystem
name|ms
init|=
name|initMetricsSystem
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
operator|new
name|MyMetrics1
argument_list|()
operator|.
name|registerWith
argument_list|(
name|ms
argument_list|)
expr_stmt|;
name|ms
operator|.
name|publishMetricsNow
argument_list|()
expr_stmt|;
comment|// publish the metrics
name|shutdownHdfs
argument_list|()
expr_stmt|;
name|MockSink
operator|.
name|errored
operator|=
literal|false
expr_stmt|;
name|ms
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"No exception was generated while stopping sink "
operator|+
literal|"even though HDFS was unavailable"
argument_list|,
name|MockSink
operator|.
name|errored
argument_list|)
expr_stmt|;
name|ms
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test that writing to HDFS fails silently when HDFS is unavailable.    *    * @throws IOException thrown when reading or writing log files    * @throws java.lang.InterruptedException thrown if interrupted    */
annotation|@
name|Test
DECL|method|testSilentFailedWrite ()
specifier|public
name|void
name|testSilentFailedWrite
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|String
name|path
init|=
literal|"hdfs://"
operator|+
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getHostAndPort
argument_list|()
operator|+
literal|"/tmp"
decl_stmt|;
name|MetricsSystem
name|ms
init|=
name|initMetricsSystem
argument_list|(
name|path
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
operator|new
name|MyMetrics1
argument_list|()
operator|.
name|registerWith
argument_list|(
name|ms
argument_list|)
expr_stmt|;
name|shutdownHdfs
argument_list|()
expr_stmt|;
name|MockSink
operator|.
name|errored
operator|=
literal|false
expr_stmt|;
name|ms
operator|.
name|publishMetricsNow
argument_list|()
expr_stmt|;
comment|// publish the metrics
name|assertFalse
argument_list|(
literal|"An exception was generated writing metrics "
operator|+
literal|"while HDFS was unavailable, even though the sink is set to "
operator|+
literal|"ignore errors"
argument_list|,
name|MockSink
operator|.
name|errored
argument_list|)
expr_stmt|;
name|ms
operator|.
name|stop
argument_list|()
expr_stmt|;
name|ms
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test that closing a file in HDFS silently fails when HDFS is unavailable.    *    * @throws IOException thrown when reading or writing log files    */
annotation|@
name|Test
DECL|method|testSilentFailedClose ()
specifier|public
name|void
name|testSilentFailedClose
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|path
init|=
literal|"hdfs://"
operator|+
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getHostAndPort
argument_list|()
operator|+
literal|"/tmp"
decl_stmt|;
name|MetricsSystem
name|ms
init|=
name|initMetricsSystem
argument_list|(
name|path
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
operator|new
name|MyMetrics1
argument_list|()
operator|.
name|registerWith
argument_list|(
name|ms
argument_list|)
expr_stmt|;
name|ms
operator|.
name|publishMetricsNow
argument_list|()
expr_stmt|;
comment|// publish the metrics
name|shutdownHdfs
argument_list|()
expr_stmt|;
name|MockSink
operator|.
name|errored
operator|=
literal|false
expr_stmt|;
name|ms
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"An exception was generated stopping sink "
operator|+
literal|"while HDFS was unavailable, even though the sink is set to "
operator|+
literal|"ignore errors"
argument_list|,
name|MockSink
operator|.
name|errored
argument_list|)
expr_stmt|;
name|ms
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * This test specifically checks whether the flusher thread is automatically    * flushing the files.  It unfortunately can only test with the alternative    * flushing schedule (because of test timing), but it's better than nothing.    *    * @throws Exception thrown if something breaks    */
annotation|@
name|Test
DECL|method|testFlushThread ()
specifier|public
name|void
name|testFlushThread
parameter_list|()
throws|throws
name|Exception
block|{
name|RollingFileSystemSink
operator|.
name|flushQuickly
operator|=
literal|true
expr_stmt|;
name|String
name|path
init|=
literal|"hdfs://"
operator|+
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getHostAndPort
argument_list|()
operator|+
literal|"/tmp"
decl_stmt|;
name|MetricsSystem
name|ms
init|=
name|initMetricsSystem
argument_list|(
name|path
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
operator|new
name|MyMetrics1
argument_list|()
operator|.
name|registerWith
argument_list|(
name|ms
argument_list|)
expr_stmt|;
comment|// Publish the metrics
name|ms
operator|.
name|publishMetricsNow
argument_list|()
expr_stmt|;
comment|// Pubish again because the first write seems to get properly flushed
comment|// regardless.
name|ms
operator|.
name|publishMetricsNow
argument_list|()
expr_stmt|;
comment|// Sleep until the flusher has run
while|while
condition|(
operator|!
name|RollingFileSystemSink
operator|.
name|hasFlushed
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|50L
argument_list|)
expr_stmt|;
block|}
name|Calendar
name|now
init|=
name|getNowNotTopOfHour
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|newInstance
argument_list|(
operator|new
name|URI
argument_list|(
name|path
argument_list|)
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|currentDir
init|=
operator|new
name|Path
argument_list|(
name|path
argument_list|,
name|DATE_FORMAT
operator|.
name|format
argument_list|(
name|now
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Path
name|currentFile
init|=
name|findMostRecentLogFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|currentDir
argument_list|,
name|getLogFilename
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|currentFile
argument_list|)
decl_stmt|;
comment|// Each metrics record is 118+ bytes, depending on hostname
name|assertTrue
argument_list|(
literal|"The flusher thread didn't flush the log contents. Expected "
operator|+
literal|"at least 236 bytes in the log file, but got "
operator|+
name|status
operator|.
name|getLen
argument_list|()
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
operator|>=
literal|236
argument_list|)
expr_stmt|;
name|ms
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test that a failure to connect to HDFS does not cause the init() method    * to fail.    */
annotation|@
name|Test
DECL|method|testInitWithNoHDFS ()
specifier|public
name|void
name|testInitWithNoHDFS
parameter_list|()
block|{
name|String
name|path
init|=
literal|"hdfs://"
operator|+
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getHostAndPort
argument_list|()
operator|+
literal|"/tmp"
decl_stmt|;
name|shutdownHdfs
argument_list|()
expr_stmt|;
name|MockSink
operator|.
name|errored
operator|=
literal|false
expr_stmt|;
name|initMetricsSystem
argument_list|(
name|path
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The sink was not initialized as expected"
argument_list|,
name|MockSink
operator|.
name|initialized
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"The sink threw an unexpected error on initialization"
argument_list|,
name|MockSink
operator|.
name|errored
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

