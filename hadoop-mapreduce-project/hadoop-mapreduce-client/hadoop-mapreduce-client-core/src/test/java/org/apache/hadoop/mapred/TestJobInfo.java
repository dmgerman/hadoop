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
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|io
operator|.
name|Text
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
name|mapreduce
operator|.
name|JobID
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
name|mapreduce
operator|.
name|TaskID
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
name|mapreduce
operator|.
name|TaskType
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
name|mapreduce
operator|.
name|JobStatus
operator|.
name|State
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
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * test class JobInfo  *   *   */
end_comment

begin_class
DECL|class|TestJobInfo
specifier|public
class|class
name|TestJobInfo
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testJobInfo ()
specifier|public
name|void
name|testJobInfo
parameter_list|()
throws|throws
name|IOException
block|{
name|JobID
name|jid
init|=
operator|new
name|JobID
argument_list|(
literal|"001"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Text
name|user
init|=
operator|new
name|Text
argument_list|(
literal|"User"
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/test"
argument_list|)
decl_stmt|;
name|JobInfo
name|info
init|=
operator|new
name|JobInfo
argument_list|(
name|jid
argument_list|,
name|user
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|info
operator|.
name|write
argument_list|(
operator|new
name|DataOutputStream
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|JobInfo
name|copyinfo
init|=
operator|new
name|JobInfo
argument_list|()
decl_stmt|;
name|copyinfo
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|info
operator|.
name|getJobID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|copyinfo
operator|.
name|getJobID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|info
operator|.
name|getJobSubmitDir
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|copyinfo
operator|.
name|getJobSubmitDir
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|info
operator|.
name|getUser
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|copyinfo
operator|.
name|getUser
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testTaskID ()
specifier|public
name|void
name|testTaskID
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|JobID
name|jobid
init|=
operator|new
name|JobID
argument_list|(
literal|"1014873536921"
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|TaskID
name|tid
init|=
operator|new
name|TaskID
argument_list|(
name|jobid
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|TaskID
name|tid1
init|=
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|TaskID
operator|.
name|downgrade
argument_list|(
name|tid
argument_list|)
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|TaskReport
name|treport
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|TaskReport
argument_list|(
name|tid1
argument_list|,
literal|0.0f
argument_list|,
name|State
operator|.
name|FAILED
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|,
name|TIPStatus
operator|.
name|FAILED
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|,
operator|new
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|Counters
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|treport
operator|.
name|getTaskId
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"task_1014873536921_0006_m_000000"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|treport
operator|.
name|getTaskID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"task_1014873536921_0006_m_000000"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

