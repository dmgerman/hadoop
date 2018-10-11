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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Progressable
import|;
end_import

begin_comment
comment|/**   *<code>OutputFormat</code> describes the output-specification for a   * Map-Reduce job.  *  *<p>The Map-Reduce framework relies on the<code>OutputFormat</code> of the  * job to:<p>  *<ol>  *<li>  *   Validate the output-specification of the job. For e.g. check that the   *   output directory doesn't already exist.   *<li>  *   Provide the {@link RecordWriter} implementation to be used to write out  *   the output files of the job. Output files are stored in a   *   {@link FileSystem}.  *</li>  *</ol>  *   * @see RecordWriter  * @see JobConf  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|OutputFormat
specifier|public
interface|interface
name|OutputFormat
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
comment|/**     * Get the {@link RecordWriter} for the given job.    *    * @param ignored    * @param job configuration for the job whose output is being written.    * @param name the unique name for this part of the output.    * @param progress mechanism for reporting progress while writing to file.    * @return a {@link RecordWriter} to write the output for the job.    * @throws IOException    */
DECL|method|getRecordWriter (FileSystem ignored, JobConf job, String name, Progressable progress)
name|RecordWriter
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|getRecordWriter
parameter_list|(
name|FileSystem
name|ignored
parameter_list|,
name|JobConf
name|job
parameter_list|,
name|String
name|name
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**     * Check for validity of the output-specification for the job.    *      *<p>This is to validate the output specification for the job when it is    * a job is submitted.  Typically checks that it does not already exist,    * throwing an exception when it already exists, so that output is not    * overwritten.</p>    *    * Implementations which write to filesystems which support delegation    * tokens usually collect the tokens for the destination path(s)    * and attach them to the job configuration.    * @param ignored    * @param job job configuration.    * @throws IOException when output should not be attempted    */
DECL|method|checkOutputSpecs (FileSystem ignored, JobConf job)
name|void
name|checkOutputSpecs
parameter_list|(
name|FileSystem
name|ignored
parameter_list|,
name|JobConf
name|job
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

