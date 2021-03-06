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
name|mapreduce
operator|.
name|Job
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
name|JobContext
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
name|OutputCommitter
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
name|TaskAttemptContext
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
name|lib
operator|.
name|output
operator|.
name|TextOutputFormat
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
name|security
operator|.
name|TokenCache
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * The CopyOutputFormat is the Hadoop OutputFormat used in DistCp.  * It sets up the Job's Configuration (in the Job-Context) with the settings  * for the work-directory, final commit-directory, etc. It also sets the right  * output-committer.  * @param<K>  * @param<V>  */
end_comment

begin_class
DECL|class|CopyOutputFormat
specifier|public
class|class
name|CopyOutputFormat
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|TextOutputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
comment|/**    * Setter for the working directory for DistCp (where files will be copied    * before they are moved to the final commit-directory.)    * @param job The Job on whose configuration the working-directory is to be set.    * @param workingDirectory The path to use as the working directory.    */
DECL|method|setWorkingDirectory (Job job, Path workingDirectory)
specifier|public
specifier|static
name|void
name|setWorkingDirectory
parameter_list|(
name|Job
name|job
parameter_list|,
name|Path
name|workingDirectory
parameter_list|)
block|{
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
name|workingDirectory
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Setter for the final directory for DistCp (where files copied will be    * moved, atomically.)    * @param job The Job on whose configuration the working-directory is to be set.    * @param commitDirectory The path to use for final commit.    */
DECL|method|setCommitDirectory (Job job, Path commitDirectory)
specifier|public
specifier|static
name|void
name|setCommitDirectory
parameter_list|(
name|Job
name|job
parameter_list|,
name|Path
name|commitDirectory
parameter_list|)
block|{
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
name|commitDirectory
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Getter for the working directory.    * @param job The Job from whose configuration the working-directory is to    * be retrieved.    * @return The working-directory Path.    */
DECL|method|getWorkingDirectory (Job job)
specifier|public
specifier|static
name|Path
name|getWorkingDirectory
parameter_list|(
name|Job
name|job
parameter_list|)
block|{
return|return
name|getWorkingDirectory
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getWorkingDirectory (Configuration conf)
specifier|private
specifier|static
name|Path
name|getWorkingDirectory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|workingDirectory
init|=
name|conf
operator|.
name|get
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_TARGET_WORK_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|workingDirectory
operator|==
literal|null
operator|||
name|workingDirectory
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|Path
argument_list|(
name|workingDirectory
argument_list|)
return|;
block|}
block|}
comment|/**    * Getter for the final commit-directory.    * @param job The Job from whose configuration the commit-directory is to be    * retrieved.    * @return The commit-directory Path.    */
DECL|method|getCommitDirectory (Job job)
specifier|public
specifier|static
name|Path
name|getCommitDirectory
parameter_list|(
name|Job
name|job
parameter_list|)
block|{
return|return
name|getCommitDirectory
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getCommitDirectory (Configuration conf)
specifier|private
specifier|static
name|Path
name|getCommitDirectory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|commitDirectory
init|=
name|conf
operator|.
name|get
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_TARGET_FINAL_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|commitDirectory
operator|==
literal|null
operator|||
name|commitDirectory
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|Path
argument_list|(
name|commitDirectory
argument_list|)
return|;
block|}
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getOutputCommitter (TaskAttemptContext context)
specifier|public
name|OutputCommitter
name|getOutputCommitter
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CopyCommitter
argument_list|(
name|getOutputPath
argument_list|(
name|context
argument_list|)
argument_list|,
name|context
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|checkOutputSpecs (JobContext context)
specifier|public
name|void
name|checkOutputSpecs
parameter_list|(
name|JobContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
if|if
condition|(
name|getCommitDirectory
argument_list|(
name|conf
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Commit directory not configured"
argument_list|)
throw|;
block|}
name|Path
name|workingPath
init|=
name|getWorkingDirectory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|workingPath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Working directory not configured"
argument_list|)
throw|;
block|}
comment|// get delegation token for outDir's file system
name|TokenCache
operator|.
name|obtainTokensForNamenodes
argument_list|(
name|context
operator|.
name|getCredentials
argument_list|()
argument_list|,
operator|new
name|Path
index|[]
block|{
name|workingPath
block|}
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

