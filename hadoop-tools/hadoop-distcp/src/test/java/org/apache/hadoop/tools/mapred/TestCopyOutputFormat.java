begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|mapred
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
name|mapreduce
operator|.
name|*
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
name|task
operator|.
name|TaskAttemptContextImpl
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
name|task
operator|.
name|JobContextImpl
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
name|tools
operator|.
name|DistCpConstants
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
name|org
operator|.
name|junit
operator|.
name|Assert
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

begin_class
DECL|class|TestCopyOutputFormat
specifier|public
class|class
name|TestCopyOutputFormat
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
name|TestCopyOutputFormat
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testSetCommitDirectory ()
specifier|public
name|void
name|testSetCommitDirectory
parameter_list|()
block|{
try|try
block|{
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|CopyOutputFormat
operator|.
name|getCommitDirectory
argument_list|(
name|job
argument_list|)
argument_list|)
expr_stmt|;
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|set
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_TARGET_FINAL_PATH
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|CopyOutputFormat
operator|.
name|getCommitDirectory
argument_list|(
name|job
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|directory
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/test"
argument_list|)
decl_stmt|;
name|CopyOutputFormat
operator|.
name|setCommitDirectory
argument_list|(
name|job
argument_list|,
name|directory
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|directory
argument_list|,
name|CopyOutputFormat
operator|.
name|getCommitDirectory
argument_list|(
name|job
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|directory
operator|.
name|toString
argument_list|()
argument_list|,
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|get
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_TARGET_FINAL_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception encountered while running test"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Failed while testing for set Commit Directory"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSetWorkingDirectory ()
specifier|public
name|void
name|testSetWorkingDirectory
parameter_list|()
block|{
try|try
block|{
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|CopyOutputFormat
operator|.
name|getWorkingDirectory
argument_list|(
name|job
argument_list|)
argument_list|)
expr_stmt|;
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|set
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_TARGET_WORK_PATH
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|CopyOutputFormat
operator|.
name|getWorkingDirectory
argument_list|(
name|job
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|directory
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/test"
argument_list|)
decl_stmt|;
name|CopyOutputFormat
operator|.
name|setWorkingDirectory
argument_list|(
name|job
argument_list|,
name|directory
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|directory
argument_list|,
name|CopyOutputFormat
operator|.
name|getWorkingDirectory
argument_list|(
name|job
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|directory
operator|.
name|toString
argument_list|()
argument_list|,
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|get
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_TARGET_WORK_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception encountered while running test"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Failed while testing for set Working Directory"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetOutputCommitter ()
specifier|public
name|void
name|testGetOutputCommitter
parameter_list|()
block|{
try|try
block|{
name|TaskAttemptContext
name|context
init|=
operator|new
name|TaskAttemptContextImpl
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
operator|new
name|TaskAttemptID
argument_list|(
literal|"200707121733"
argument_list|,
literal|1
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|set
argument_list|(
literal|"mapred.output.dir"
argument_list|,
literal|"/out"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|new
name|CopyOutputFormat
argument_list|()
operator|.
name|getOutputCommitter
argument_list|(
name|context
argument_list|)
operator|instanceof
name|CopyCommitter
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception encountered "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Unable to get output committer"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCheckOutputSpecs ()
specifier|public
name|void
name|testCheckOutputSpecs
parameter_list|()
block|{
try|try
block|{
name|OutputFormat
name|outputFormat
init|=
operator|new
name|CopyOutputFormat
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|JobID
name|jobID
init|=
operator|new
name|JobID
argument_list|(
literal|"200707121733"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
name|JobContext
name|context
init|=
operator|new
name|JobContextImpl
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|jobID
argument_list|)
decl_stmt|;
name|outputFormat
operator|.
name|checkOutputSpecs
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"No checking for invalid work/commit path"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ignore
parameter_list|)
block|{ }
name|CopyOutputFormat
operator|.
name|setWorkingDirectory
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/tmp/work"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|JobContext
name|context
init|=
operator|new
name|JobContextImpl
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|jobID
argument_list|)
decl_stmt|;
name|outputFormat
operator|.
name|checkOutputSpecs
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"No checking for invalid commit path"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ignore
parameter_list|)
block|{ }
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|set
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_TARGET_WORK_PATH
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|CopyOutputFormat
operator|.
name|setCommitDirectory
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/tmp/commit"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|JobContext
name|context
init|=
operator|new
name|JobContextImpl
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|jobID
argument_list|)
decl_stmt|;
name|outputFormat
operator|.
name|checkOutputSpecs
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"No checking for invalid work path"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ignore
parameter_list|)
block|{ }
name|CopyOutputFormat
operator|.
name|setWorkingDirectory
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/tmp/work"
argument_list|)
argument_list|)
expr_stmt|;
name|CopyOutputFormat
operator|.
name|setCommitDirectory
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/tmp/commit"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|JobContext
name|context
init|=
operator|new
name|JobContextImpl
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|jobID
argument_list|)
decl_stmt|;
name|outputFormat
operator|.
name|checkOutputSpecs
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ignore
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Output spec check failed."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception encountered while testing checkoutput specs"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Checkoutput Spec failure"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception encountered while testing checkoutput specs"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Checkoutput Spec failure"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

