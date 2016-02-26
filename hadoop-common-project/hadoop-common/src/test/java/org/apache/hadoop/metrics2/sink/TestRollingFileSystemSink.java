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
comment|/**  * Test the {@link RollingFileSystemSink} class in the context of the local file  * system.  */
end_comment

begin_class
DECL|class|TestRollingFileSystemSink
specifier|public
class|class
name|TestRollingFileSystemSink
extends|extends
name|RollingFileSystemSinkTestBase
block|{
comment|/**    * Test writing logs to the local file system.    * @throws Exception when things break    */
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
name|methodDir
operator|.
name|getAbsolutePath
argument_list|()
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
comment|/**    * Test writing logs to the local file system with the sink set to ignore    * errors.    * @throws Exception when things break    */
annotation|@
name|Test
DECL|method|testSilentWrite ()
specifier|public
name|void
name|testSilentWrite
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
name|methodDir
operator|.
name|getAbsolutePath
argument_list|()
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
comment|/**    * Test writing logs to HDFS when the log file already exists.    *    * @throws Exception when things break    */
annotation|@
name|Test
DECL|method|testExistingWrite ()
specifier|public
name|void
name|testExistingWrite
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
name|methodDir
operator|.
name|getAbsolutePath
argument_list|()
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
comment|/**    * Test writing logs to HDFS when the log file and the .1 log file already    * exist.    *    * @throws Exception when things break    */
annotation|@
name|Test
DECL|method|testExistingWrite2 ()
specifier|public
name|void
name|testExistingWrite2
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
name|methodDir
operator|.
name|getAbsolutePath
argument_list|()
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
name|preCreateLogFile
argument_list|(
name|path
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertMetricsContents
argument_list|(
name|doWriteTest
argument_list|(
name|ms
argument_list|,
name|path
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test writing logs to HDFS with ignore errors enabled when    * the log file already exists.    *    * @throws Exception when things break    */
annotation|@
name|Test
DECL|method|testSilentExistingWrite ()
specifier|public
name|void
name|testSilentExistingWrite
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
name|methodDir
operator|.
name|getAbsolutePath
argument_list|()
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
comment|/**    * Test that writing fails when the directory isn't writable.    */
annotation|@
name|Test
DECL|method|testFailedWrite ()
specifier|public
name|void
name|testFailedWrite
parameter_list|()
block|{
name|String
name|path
init|=
name|methodDir
operator|.
name|getAbsolutePath
argument_list|()
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
name|methodDir
operator|.
name|setWritable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|MockSink
operator|.
name|errored
operator|=
literal|false
expr_stmt|;
try|try
block|{
comment|// publish the metrics
name|ms
operator|.
name|publishMetricsNow
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"No exception was generated while writing metrics "
operator|+
literal|"even though the target directory was not writable"
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
finally|finally
block|{
comment|// Make sure the dir is writable again so we can delete it at the end
name|methodDir
operator|.
name|setWritable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that writing fails silently when the directory is not writable.    */
annotation|@
name|Test
DECL|method|testSilentFailedWrite ()
specifier|public
name|void
name|testSilentFailedWrite
parameter_list|()
block|{
name|String
name|path
init|=
name|methodDir
operator|.
name|getAbsolutePath
argument_list|()
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
name|methodDir
operator|.
name|setWritable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|MockSink
operator|.
name|errored
operator|=
literal|false
expr_stmt|;
try|try
block|{
comment|// publish the metrics
name|ms
operator|.
name|publishMetricsNow
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"An exception was generated while writing metrics "
operator|+
literal|"when the target directory was not writable, even though the "
operator|+
literal|"sink is set to ignore errors"
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
finally|finally
block|{
comment|// Make sure the dir is writable again so we can delete it at the end
name|methodDir
operator|.
name|setWritable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

