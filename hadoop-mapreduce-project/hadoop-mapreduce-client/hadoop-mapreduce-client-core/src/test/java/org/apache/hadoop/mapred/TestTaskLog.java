begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertThat
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
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
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
name|InputStream
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
name|io
operator|.
name|FileUtils
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
name|mapred
operator|.
name|TaskLog
operator|.
name|LogName
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
name|conf
operator|.
name|YarnConfiguration
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
name|Test
import|;
end_import

begin_comment
comment|/**  * TestCounters checks the sanity and recoverability of Queue  */
end_comment

begin_class
DECL|class|TestTaskLog
specifier|public
class|class
name|TestTaskLog
block|{
DECL|field|testDirName
specifier|private
specifier|static
specifier|final
name|String
name|testDirName
init|=
name|TestTaskLog
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
DECL|field|testDir
specifier|private
specifier|static
specifier|final
name|String
name|testDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"target"
operator|+
name|File
operator|.
name|separatorChar
operator|+
literal|"test-dir"
argument_list|)
operator|+
name|File
operator|.
name|separatorChar
operator|+
name|testDirName
decl_stmt|;
annotation|@
name|AfterClass
DECL|method|cleanup ()
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|testDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * test TaskAttemptID    *     * @throws IOException    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|50000
argument_list|)
DECL|method|testTaskLog ()
specifier|public
name|void
name|testTaskLog
parameter_list|()
throws|throws
name|IOException
block|{
comment|// test TaskLog
name|System
operator|.
name|setProperty
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_APP_CONTAINER_LOG_DIR
argument_list|,
literal|"testString"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|TaskLog
operator|.
name|getMRv2LogDir
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"testString"
argument_list|)
expr_stmt|;
name|TaskAttemptID
name|taid
init|=
name|mock
argument_list|(
name|TaskAttemptID
operator|.
name|class
argument_list|)
decl_stmt|;
name|JobID
name|jid
init|=
operator|new
name|JobID
argument_list|(
literal|"job"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|taid
operator|.
name|getJobID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|jid
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|taid
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"JobId"
argument_list|)
expr_stmt|;
name|File
name|f
init|=
name|TaskLog
operator|.
name|getTaskLogFile
argument_list|(
name|taid
argument_list|,
literal|true
argument_list|,
name|LogName
operator|.
name|STDOUT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"testString"
operator|+
name|File
operator|.
name|separatorChar
operator|+
literal|"stdout"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test getRealTaskLogFileLocation
name|File
name|indexFile
init|=
name|TaskLog
operator|.
name|getIndexFile
argument_list|(
name|taid
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|indexFile
operator|.
name|getParentFile
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
name|indexFile
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
name|indexFile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|indexFile
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|TaskLog
operator|.
name|syncLogs
argument_list|(
name|testDir
argument_list|,
name|taid
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|indexFile
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"userlogs"
operator|+
name|File
operator|.
name|separatorChar
operator|+
literal|"job_job_0001"
operator|+
name|File
operator|.
name|separatorChar
operator|+
literal|"JobId.cleanup"
operator|+
name|File
operator|.
name|separatorChar
operator|+
literal|"log.index"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|=
name|TaskLog
operator|.
name|getRealTaskLogFileLocation
argument_list|(
name|taid
argument_list|,
literal|true
argument_list|,
name|LogName
operator|.
name|DEBUGOUT
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|f
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|endsWith
argument_list|(
name|testDirName
operator|+
name|File
operator|.
name|separatorChar
operator|+
literal|"debugout"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|indexFile
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
comment|// test obtainLogDirOwner
name|assertTrue
argument_list|(
name|TaskLog
operator|.
name|obtainLogDirOwner
argument_list|(
name|taid
argument_list|)
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// test TaskLog.Reader
name|assertTrue
argument_list|(
name|readTaskLog
argument_list|(
name|TaskLog
operator|.
name|LogName
operator|.
name|DEBUGOUT
argument_list|,
name|taid
argument_list|,
literal|true
argument_list|)
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|readTaskLog (TaskLog.LogName filter, org.apache.hadoop.mapred.TaskAttemptID taskId, boolean isCleanup)
specifier|private
name|String
name|readTaskLog
parameter_list|(
name|TaskLog
operator|.
name|LogName
name|filter
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|TaskAttemptID
name|taskId
parameter_list|,
name|boolean
name|isCleanup
parameter_list|)
throws|throws
name|IOException
block|{
comment|// string buffer to store task log
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|res
decl_stmt|;
comment|// reads the whole tasklog into inputstream
name|InputStream
name|taskLogReader
init|=
operator|new
name|TaskLog
operator|.
name|Reader
argument_list|(
name|taskId
argument_list|,
name|filter
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
name|isCleanup
argument_list|)
decl_stmt|;
comment|// construct string log from inputstream.
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|65536
index|]
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|res
operator|=
name|taskLogReader
operator|.
name|read
argument_list|(
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|res
operator|>
literal|0
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
operator|new
name|String
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
name|taskLogReader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// trim the string and return it
name|String
name|str
init|=
name|result
operator|.
name|toString
argument_list|()
decl_stmt|;
name|str
operator|=
name|str
operator|.
name|trim
argument_list|()
expr_stmt|;
return|return
name|str
return|;
block|}
comment|/**    * test without TASK_LOG_DIR    *     * @throws IOException    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|50000
argument_list|)
DECL|method|testTaskLogWithoutTaskLogDir ()
specifier|public
name|void
name|testTaskLogWithoutTaskLogDir
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TaskLog tasklog= new TaskLog();
name|System
operator|.
name|clearProperty
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_APP_CONTAINER_LOG_DIR
argument_list|)
expr_stmt|;
comment|// test TaskLog
name|assertThat
argument_list|(
name|TaskLog
operator|.
name|getMRv2LogDir
argument_list|()
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|TaskAttemptID
name|taid
init|=
name|mock
argument_list|(
name|TaskAttemptID
operator|.
name|class
argument_list|)
decl_stmt|;
name|JobID
name|jid
init|=
operator|new
name|JobID
argument_list|(
literal|"job"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|taid
operator|.
name|getJobID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|jid
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|taid
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"JobId"
argument_list|)
expr_stmt|;
name|File
name|f
init|=
name|TaskLog
operator|.
name|getTaskLogFile
argument_list|(
name|taid
argument_list|,
literal|true
argument_list|,
name|LogName
operator|.
name|STDOUT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"stdout"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

