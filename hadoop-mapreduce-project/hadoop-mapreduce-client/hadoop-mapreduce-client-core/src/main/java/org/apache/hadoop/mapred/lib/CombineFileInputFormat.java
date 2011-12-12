begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|lib
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
name|util
operator|.
name|List
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
name|fs
operator|.
name|PathFilter
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
name|FileInputFormat
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
name|InputFormat
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
name|InputSplit
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
name|JobConf
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
name|Reporter
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
name|RecordReader
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
name|TaskAttemptContext
import|;
end_import

begin_comment
comment|/**  * An abstract {@link org.apache.hadoop.mapred.InputFormat} that returns {@link CombineFileSplit}'s  * in {@link org.apache.hadoop.mapred.InputFormat#getSplits(JobConf, int)} method.   * Splits are constructed from the files under the input paths.   * A split cannot have files from different pools.  * Each split returned may contain blocks from different files.  * If a maxSplitSize is specified, then blocks on the same node are  * combined to form a single split. Blocks that are left over are  * then combined with other blocks in the same rack.   * If maxSplitSize is not specified, then blocks from the same rack  * are combined in a single split; no attempt is made to create  * node-local splits.  * If the maxSplitSize is equal to the block size, then this class  * is similar to the default spliting behaviour in Hadoop: each  * block is a locally processed split.  * Subclasses implement {@link org.apache.hadoop.mapred.InputFormat#getRecordReader(InputSplit, JobConf, Reporter)}  * to construct<code>RecordReader</code>'s for<code>CombineFileSplit</code>'s.  * @see CombineFileSplit  * @deprecated Use   * {@link org.apache.hadoop.mapreduce.lib.input.CombineFileInputFormat}  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|CombineFileInputFormat
specifier|public
specifier|abstract
class|class
name|CombineFileInputFormat
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
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
name|input
operator|.
name|CombineFileInputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
implements|implements
name|InputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
comment|/**    * default constructor    */
DECL|method|CombineFileInputFormat ()
specifier|public
name|CombineFileInputFormat
parameter_list|()
block|{   }
DECL|method|getSplits (JobConf job, int numSplits)
specifier|public
name|InputSplit
index|[]
name|getSplits
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|int
name|numSplits
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|InputSplit
argument_list|>
name|newStyleSplits
init|=
name|super
operator|.
name|getSplits
argument_list|(
operator|new
name|Job
argument_list|(
name|job
argument_list|)
argument_list|)
decl_stmt|;
name|InputSplit
index|[]
name|ret
init|=
operator|new
name|InputSplit
index|[
name|newStyleSplits
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|newStyleSplits
operator|.
name|size
argument_list|()
condition|;
operator|++
name|pos
control|)
block|{
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
name|input
operator|.
name|CombineFileSplit
name|newStyleSplit
init|=
operator|(
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
name|input
operator|.
name|CombineFileSplit
operator|)
name|newStyleSplits
operator|.
name|get
argument_list|(
name|pos
argument_list|)
decl_stmt|;
name|ret
index|[
name|pos
index|]
operator|=
operator|new
name|CombineFileSplit
argument_list|(
name|job
argument_list|,
name|newStyleSplit
operator|.
name|getPaths
argument_list|()
argument_list|,
name|newStyleSplit
operator|.
name|getStartOffsets
argument_list|()
argument_list|,
name|newStyleSplit
operator|.
name|getLengths
argument_list|()
argument_list|,
name|newStyleSplit
operator|.
name|getLocations
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Create a new pool and add the filters to it.    * A split cannot have files from different pools.    * @deprecated Use {@link #createPool(List)}.    */
annotation|@
name|Deprecated
DECL|method|createPool (JobConf conf, List<PathFilter> filters)
specifier|protected
name|void
name|createPool
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|List
argument_list|<
name|PathFilter
argument_list|>
name|filters
parameter_list|)
block|{
name|createPool
argument_list|(
name|filters
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new pool and add the filters to it.     * A pathname can satisfy any one of the specified filters.    * A split cannot have files from different pools.    * @deprecated Use {@link #createPool(PathFilter...)}.    */
annotation|@
name|Deprecated
DECL|method|createPool (JobConf conf, PathFilter... filters)
specifier|protected
name|void
name|createPool
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|PathFilter
modifier|...
name|filters
parameter_list|)
block|{
name|createPool
argument_list|(
name|filters
argument_list|)
expr_stmt|;
block|}
comment|/**    * This is not implemented yet.     */
DECL|method|getRecordReader (InputSplit split, JobConf job, Reporter reporter)
specifier|public
specifier|abstract
name|RecordReader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|getRecordReader
parameter_list|(
name|InputSplit
name|split
parameter_list|,
name|JobConf
name|job
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|// abstract method from super class implemented to return null
DECL|method|createRecordReader ( org.apache.hadoop.mapreduce.InputSplit split, TaskAttemptContext context)
specifier|public
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|RecordReader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|createRecordReader
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|InputSplit
name|split
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
comment|/** List input directories.    * Subclasses may override to, e.g., select only files matching a regular    * expression.     *     * @param job the job to list input paths for    * @return array of FileStatus objects    * @throws IOException if zero items.    */
DECL|method|listStatus (JobConf job)
specifier|protected
name|FileStatus
index|[]
name|listStatus
parameter_list|(
name|JobConf
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|FileStatus
argument_list|>
name|result
init|=
name|super
operator|.
name|listStatus
argument_list|(
operator|new
name|Job
argument_list|(
name|job
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|FileStatus
index|[
name|result
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

