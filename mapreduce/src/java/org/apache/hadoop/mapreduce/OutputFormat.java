begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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
name|FileSystem
import|;
end_import

begin_comment
comment|/**   *<code>OutputFormat</code> describes the output-specification for a   * Map-Reduce job.  *  *<p>The Map-Reduce framework relies on the<code>OutputFormat</code> of the  * job to:<p>  *<ol>  *<li>  *   Validate the output-specification of the job. For e.g. check that the   *   output directory doesn't already exist.   *<li>  *   Provide the {@link RecordWriter} implementation to be used to write out  *   the output files of the job. Output files are stored in a   *   {@link FileSystem}.  *</li>  *</ol>  *   * @see RecordWriter  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|OutputFormat
specifier|public
specifier|abstract
class|class
name|OutputFormat
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
comment|/**     * Get the {@link RecordWriter} for the given task.    *    * @param context the information about the current task.    * @return a {@link RecordWriter} to write the output for the job.    * @throws IOException    */
specifier|public
specifier|abstract
name|RecordWriter
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
DECL|method|getRecordWriter (TaskAttemptContext context )
name|getRecordWriter
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**     * Check for validity of the output-specification for the job.    *      *<p>This is to validate the output specification for the job when it is    * a job is submitted.  Typically checks that it does not already exist,    * throwing an exception when it already exists, so that output is not    * overwritten.</p>    *    * @param context information about the job    * @throws IOException when output should not be attempted    */
DECL|method|checkOutputSpecs (JobContext context )
specifier|public
specifier|abstract
name|void
name|checkOutputSpecs
parameter_list|(
name|JobContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Get the output committer for this output format. This is responsible    * for ensuring the output is committed correctly.    * @param context the task context    * @return an output committer    * @throws IOException    * @throws InterruptedException    */
specifier|public
specifier|abstract
DECL|method|getOutputCommitter (TaskAttemptContext context )
name|OutputCommitter
name|getOutputCommitter
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
block|}
end_class

end_unit

