begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.output
package|package
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|JobStatus
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

begin_comment
comment|/**  * This is a special committer which creates the factory for the committer and  * runs off that. Why does it exist? So that you can explicitly instantiate  * a committer by classname and yet still have the actual implementation  * driven dynamically by the factory options and destination filesystem.  * This simplifies integration  * with existing code which takes the classname of a committer.  * There's no factory for this, as that would lead to a loop.  *  * All commit protocol methods and accessors are delegated to the  * wrapped committer.  *  * How to use:  *  *<ol>  *<li>  *     In applications which take a classname of committer in  *     a configuration option, set it to the canonical name of this class  *     (see {@link #NAME}). When this class is instantiated, it will  *     use the factory mechanism to locate the configured committer for the  *     destination.  *</li>  *<li>  *     In code, explicitly create an instance of this committer through  *     its constructor, then invoke commit lifecycle operations on it.  *     The dynamically configured committer will be created in the constructor  *     and have the lifecycle operations relayed to it.  *</li>  *</ol>  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|BindingPathOutputCommitter
specifier|public
class|class
name|BindingPathOutputCommitter
extends|extends
name|PathOutputCommitter
block|{
comment|/**    * The classname for use in configurations.    */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|BindingPathOutputCommitter
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
decl_stmt|;
comment|/**    * The bound committer.    */
DECL|field|committer
specifier|private
specifier|final
name|PathOutputCommitter
name|committer
decl_stmt|;
comment|/**    * Instantiate.    * @param outputPath output path (may be null)    * @param context task context    * @throws IOException on any failure.    */
DECL|method|BindingPathOutputCommitter (Path outputPath, TaskAttemptContext context)
specifier|public
name|BindingPathOutputCommitter
parameter_list|(
name|Path
name|outputPath
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|outputPath
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|committer
operator|=
name|PathOutputCommitterFactory
operator|.
name|getCommitterFactory
argument_list|(
name|outputPath
argument_list|,
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
operator|.
name|createOutputCommitter
argument_list|(
name|outputPath
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOutputPath ()
specifier|public
name|Path
name|getOutputPath
parameter_list|()
block|{
return|return
name|committer
operator|.
name|getOutputPath
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getWorkPath ()
specifier|public
name|Path
name|getWorkPath
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|committer
operator|.
name|getWorkPath
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setupJob (JobContext jobContext)
specifier|public
name|void
name|setupJob
parameter_list|(
name|JobContext
name|jobContext
parameter_list|)
throws|throws
name|IOException
block|{
name|committer
operator|.
name|setupJob
argument_list|(
name|jobContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setupTask (TaskAttemptContext taskContext)
specifier|public
name|void
name|setupTask
parameter_list|(
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
block|{
name|committer
operator|.
name|setupTask
argument_list|(
name|taskContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsTaskCommit (TaskAttemptContext taskContext)
specifier|public
name|boolean
name|needsTaskCommit
parameter_list|(
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|committer
operator|.
name|needsTaskCommit
argument_list|(
name|taskContext
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|commitTask (TaskAttemptContext taskContext)
specifier|public
name|void
name|commitTask
parameter_list|(
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
block|{
name|committer
operator|.
name|commitTask
argument_list|(
name|taskContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abortTask (TaskAttemptContext taskContext)
specifier|public
name|void
name|abortTask
parameter_list|(
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
block|{
name|committer
operator|.
name|abortTask
argument_list|(
name|taskContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|cleanupJob (JobContext jobContext)
specifier|public
name|void
name|cleanupJob
parameter_list|(
name|JobContext
name|jobContext
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|cleanupJob
argument_list|(
name|jobContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|commitJob (JobContext jobContext)
specifier|public
name|void
name|commitJob
parameter_list|(
name|JobContext
name|jobContext
parameter_list|)
throws|throws
name|IOException
block|{
name|committer
operator|.
name|commitJob
argument_list|(
name|jobContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abortJob (JobContext jobContext, JobStatus.State state)
specifier|public
name|void
name|abortJob
parameter_list|(
name|JobContext
name|jobContext
parameter_list|,
name|JobStatus
operator|.
name|State
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|committer
operator|.
name|abortJob
argument_list|(
name|jobContext
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
DECL|method|isRecoverySupported ()
specifier|public
name|boolean
name|isRecoverySupported
parameter_list|()
block|{
return|return
name|committer
operator|.
name|isRecoverySupported
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isCommitJobRepeatable (JobContext jobContext)
specifier|public
name|boolean
name|isCommitJobRepeatable
parameter_list|(
name|JobContext
name|jobContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|committer
operator|.
name|isCommitJobRepeatable
argument_list|(
name|jobContext
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isRecoverySupported (JobContext jobContext)
specifier|public
name|boolean
name|isRecoverySupported
parameter_list|(
name|JobContext
name|jobContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|committer
operator|.
name|isRecoverySupported
argument_list|(
name|jobContext
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|recoverTask (TaskAttemptContext taskContext)
specifier|public
name|void
name|recoverTask
parameter_list|(
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
block|{
name|committer
operator|.
name|recoverTask
argument_list|(
name|taskContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasOutputPath ()
specifier|public
name|boolean
name|hasOutputPath
parameter_list|()
block|{
return|return
name|committer
operator|.
name|hasOutputPath
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"BindingPathOutputCommitter{"
operator|+
literal|"committer="
operator|+
name|committer
operator|+
literal|'}'
return|;
block|}
comment|/**    * Get the inner committer.    * @return the bonded committer.    */
DECL|method|getCommitter ()
specifier|public
name|PathOutputCommitter
name|getCommitter
parameter_list|()
block|{
return|return
name|committer
return|;
block|}
block|}
end_class

end_unit

