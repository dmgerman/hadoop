begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.input
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
name|input
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
name|ArrayList
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
name|fs
operator|.
name|BlockLocation
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
name|mapreduce
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
name|Mapper
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
name|util
operator|.
name|ReflectionUtils
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
name|StringUtils
import|;
end_import

begin_comment
comment|/**   * A base class for file-based {@link InputFormat}s.  *   *<p><code>FileInputFormat</code> is the base class for all file-based   *<code>InputFormat</code>s. This provides a generic implementation of  * {@link #getSplits(JobContext)}.  * Subclasses of<code>FileInputFormat</code> can also override the   * {@link #isSplitable(JobContext, Path)} method to ensure input-files are  * not split-up and are processed as a whole by {@link Mapper}s.  */
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
DECL|class|FileInputFormat
specifier|public
specifier|abstract
class|class
name|FileInputFormat
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|InputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|INPUT_DIR
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_DIR
init|=
literal|"mapreduce.input.fileinputformat.inputdir"
decl_stmt|;
DECL|field|SPLIT_MAXSIZE
specifier|public
specifier|static
specifier|final
name|String
name|SPLIT_MAXSIZE
init|=
literal|"mapreduce.input.fileinputformat.split.maxsize"
decl_stmt|;
DECL|field|SPLIT_MINSIZE
specifier|public
specifier|static
specifier|final
name|String
name|SPLIT_MINSIZE
init|=
literal|"mapreduce.input.fileinputformat.split.minsize"
decl_stmt|;
DECL|field|PATHFILTER_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|PATHFILTER_CLASS
init|=
literal|"mapreduce.input.pathFilter.class"
decl_stmt|;
DECL|field|NUM_INPUT_FILES
specifier|public
specifier|static
specifier|final
name|String
name|NUM_INPUT_FILES
init|=
literal|"mapreduce.input.fileinputformat.numinputfiles"
decl_stmt|;
DECL|field|INPUT_DIR_RECURSIVE
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_DIR_RECURSIVE
init|=
literal|"mapreduce.input.fileinputformat.input.dir.recursive"
decl_stmt|;
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
name|FileInputFormat
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SPLIT_SLOP
specifier|private
specifier|static
specifier|final
name|double
name|SPLIT_SLOP
init|=
literal|1.1
decl_stmt|;
comment|// 10% slop
annotation|@
name|Deprecated
DECL|enum|Counter
specifier|public
specifier|static
enum|enum
name|Counter
block|{
DECL|enumConstant|BYTES_READ
name|BYTES_READ
block|}
DECL|field|hiddenFileFilter
specifier|private
specifier|static
specifier|final
name|PathFilter
name|hiddenFileFilter
init|=
operator|new
name|PathFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|p
parameter_list|)
block|{
name|String
name|name
init|=
name|p
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
operator|&&
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|"."
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Proxy PathFilter that accepts a path only if all filters given in the    * constructor do. Used by the listPaths() to apply the built-in    * hiddenFileFilter together with a user provided one (if any).    */
DECL|class|MultiPathFilter
specifier|private
specifier|static
class|class
name|MultiPathFilter
implements|implements
name|PathFilter
block|{
DECL|field|filters
specifier|private
name|List
argument_list|<
name|PathFilter
argument_list|>
name|filters
decl_stmt|;
DECL|method|MultiPathFilter (List<PathFilter> filters)
specifier|public
name|MultiPathFilter
parameter_list|(
name|List
argument_list|<
name|PathFilter
argument_list|>
name|filters
parameter_list|)
block|{
name|this
operator|.
name|filters
operator|=
name|filters
expr_stmt|;
block|}
DECL|method|accept (Path path)
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
for|for
control|(
name|PathFilter
name|filter
range|:
name|filters
control|)
block|{
if|if
condition|(
operator|!
name|filter
operator|.
name|accept
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
comment|/**    * @param job    *          the job to modify    * @param inputDirRecursive    */
DECL|method|setInputDirRecursive (Job job, boolean inputDirRecursive)
specifier|public
specifier|static
name|void
name|setInputDirRecursive
parameter_list|(
name|Job
name|job
parameter_list|,
name|boolean
name|inputDirRecursive
parameter_list|)
block|{
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setBoolean
argument_list|(
name|INPUT_DIR_RECURSIVE
argument_list|,
name|inputDirRecursive
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param job    *          the job to look at.    * @return should the files to be read recursively?    */
DECL|method|getInputDirRecursive (JobContext job)
specifier|public
specifier|static
name|boolean
name|getInputDirRecursive
parameter_list|(
name|JobContext
name|job
parameter_list|)
block|{
return|return
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|INPUT_DIR_RECURSIVE
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Get the lower bound on split size imposed by the format.    * @return the number of bytes of the minimal split for this format    */
DECL|method|getFormatMinSplitSize ()
specifier|protected
name|long
name|getFormatMinSplitSize
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
comment|/**    * Is the given filename splitable? Usually, true, but if the file is    * stream compressed, it will not be.    *     *<code>FileInputFormat</code> implementations can override this and return    *<code>false</code> to ensure that individual input files are never split-up    * so that {@link Mapper}s process entire files.    *     * @param context the job context    * @param filename the file name to check    * @return is this file splitable?    */
DECL|method|isSplitable (JobContext context, Path filename)
specifier|protected
name|boolean
name|isSplitable
parameter_list|(
name|JobContext
name|context
parameter_list|,
name|Path
name|filename
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Set a PathFilter to be applied to the input paths for the map-reduce job.    * @param job the job to modify    * @param filter the PathFilter class use for filtering the input paths.    */
DECL|method|setInputPathFilter (Job job, Class<? extends PathFilter> filter)
specifier|public
specifier|static
name|void
name|setInputPathFilter
parameter_list|(
name|Job
name|job
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|PathFilter
argument_list|>
name|filter
parameter_list|)
block|{
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setClass
argument_list|(
name|PATHFILTER_CLASS
argument_list|,
name|filter
argument_list|,
name|PathFilter
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the minimum input split size    * @param job the job to modify    * @param size the minimum size    */
DECL|method|setMinInputSplitSize (Job job, long size)
specifier|public
specifier|static
name|void
name|setMinInputSplitSize
parameter_list|(
name|Job
name|job
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setLong
argument_list|(
name|SPLIT_MINSIZE
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the minimum split size    * @param job the job    * @return the minimum number of bytes that can be in a split    */
DECL|method|getMinSplitSize (JobContext job)
specifier|public
specifier|static
name|long
name|getMinSplitSize
parameter_list|(
name|JobContext
name|job
parameter_list|)
block|{
return|return
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getLong
argument_list|(
name|SPLIT_MINSIZE
argument_list|,
literal|1L
argument_list|)
return|;
block|}
comment|/**    * Set the maximum split size    * @param job the job to modify    * @param size the maximum split size    */
DECL|method|setMaxInputSplitSize (Job job, long size)
specifier|public
specifier|static
name|void
name|setMaxInputSplitSize
parameter_list|(
name|Job
name|job
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setLong
argument_list|(
name|SPLIT_MAXSIZE
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the maximum split size.    * @param context the job to look at.    * @return the maximum number of bytes a split can include    */
DECL|method|getMaxSplitSize (JobContext context)
specifier|public
specifier|static
name|long
name|getMaxSplitSize
parameter_list|(
name|JobContext
name|context
parameter_list|)
block|{
return|return
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getLong
argument_list|(
name|SPLIT_MAXSIZE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
comment|/**    * Get a PathFilter instance of the filter set for the input paths.    *    * @return the PathFilter instance set for the job, NULL if none has been set.    */
DECL|method|getInputPathFilter (JobContext context)
specifier|public
specifier|static
name|PathFilter
name|getInputPathFilter
parameter_list|(
name|JobContext
name|context
parameter_list|)
block|{
name|Configuration
name|conf
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|filterClass
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|PATHFILTER_CLASS
argument_list|,
literal|null
argument_list|,
name|PathFilter
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
operator|(
name|filterClass
operator|!=
literal|null
operator|)
condition|?
operator|(
name|PathFilter
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|filterClass
argument_list|,
name|conf
argument_list|)
else|:
literal|null
return|;
block|}
comment|/** List input directories.    * Subclasses may override to, e.g., select only files matching a regular    * expression.     *     * @param job the job to list input paths for    * @return array of FileStatus objects    * @throws IOException if zero items.    */
DECL|method|listStatus (JobContext job )
specifier|protected
name|List
argument_list|<
name|FileStatus
argument_list|>
name|listStatus
parameter_list|(
name|JobContext
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
operator|new
name|ArrayList
argument_list|<
name|FileStatus
argument_list|>
argument_list|()
decl_stmt|;
name|Path
index|[]
name|dirs
init|=
name|getInputPaths
argument_list|(
name|job
argument_list|)
decl_stmt|;
if|if
condition|(
name|dirs
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No input paths specified in job"
argument_list|)
throw|;
block|}
comment|// get tokens for all the required FileSystems..
name|TokenCache
operator|.
name|obtainTokensForNamenodes
argument_list|(
name|job
operator|.
name|getCredentials
argument_list|()
argument_list|,
name|dirs
argument_list|,
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
comment|// Whether we need to recursive look into the directory structure
name|boolean
name|recursive
init|=
name|getInputDirRecursive
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|IOException
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<
name|IOException
argument_list|>
argument_list|()
decl_stmt|;
comment|// creates a MultiPathFilter with the hiddenFileFilter and the
comment|// user provided one (if any).
name|List
argument_list|<
name|PathFilter
argument_list|>
name|filters
init|=
operator|new
name|ArrayList
argument_list|<
name|PathFilter
argument_list|>
argument_list|()
decl_stmt|;
name|filters
operator|.
name|add
argument_list|(
name|hiddenFileFilter
argument_list|)
expr_stmt|;
name|PathFilter
name|jobFilter
init|=
name|getInputPathFilter
argument_list|(
name|job
argument_list|)
decl_stmt|;
if|if
condition|(
name|jobFilter
operator|!=
literal|null
condition|)
block|{
name|filters
operator|.
name|add
argument_list|(
name|jobFilter
argument_list|)
expr_stmt|;
block|}
name|PathFilter
name|inputFilter
init|=
operator|new
name|MultiPathFilter
argument_list|(
name|filters
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dirs
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|Path
name|p
init|=
name|dirs
index|[
name|i
index|]
decl_stmt|;
name|FileSystem
name|fs
init|=
name|p
operator|.
name|getFileSystem
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|FileStatus
index|[]
name|matches
init|=
name|fs
operator|.
name|globStatus
argument_list|(
name|p
argument_list|,
name|inputFilter
argument_list|)
decl_stmt|;
if|if
condition|(
name|matches
operator|==
literal|null
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Input path does not exist: "
operator|+
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|matches
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Input Pattern "
operator|+
name|p
operator|+
literal|" matches 0 files"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|FileStatus
name|globStat
range|:
name|matches
control|)
block|{
if|if
condition|(
name|globStat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
for|for
control|(
name|FileStatus
name|stat
range|:
name|fs
operator|.
name|listStatus
argument_list|(
name|globStat
operator|.
name|getPath
argument_list|()
argument_list|,
name|inputFilter
argument_list|)
control|)
block|{
if|if
condition|(
name|recursive
operator|&&
name|stat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|addInputPathRecursively
argument_list|(
name|result
argument_list|,
name|fs
argument_list|,
name|stat
operator|.
name|getPath
argument_list|()
argument_list|,
name|inputFilter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|add
argument_list|(
name|stat
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|result
operator|.
name|add
argument_list|(
name|globStat
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
operator|!
name|errors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidInputException
argument_list|(
name|errors
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Total input paths to process : "
operator|+
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**    * Add files in the input path recursively into the results.    * @param result    *          The List to store all files.    * @param fs    *          The FileSystem.    * @param path    *          The input path.    * @param inputFilter    *          The input filter that can be used to filter files/dirs.     * @throws IOException    */
DECL|method|addInputPathRecursively (List<FileStatus> result, FileSystem fs, Path path, PathFilter inputFilter)
specifier|protected
name|void
name|addInputPathRecursively
parameter_list|(
name|List
argument_list|<
name|FileStatus
argument_list|>
name|result
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|PathFilter
name|inputFilter
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|FileStatus
name|stat
range|:
name|fs
operator|.
name|listStatus
argument_list|(
name|path
argument_list|,
name|inputFilter
argument_list|)
control|)
block|{
if|if
condition|(
name|stat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|addInputPathRecursively
argument_list|(
name|result
argument_list|,
name|fs
argument_list|,
name|stat
operator|.
name|getPath
argument_list|()
argument_list|,
name|inputFilter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|add
argument_list|(
name|stat
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * A factory that makes the split for this class. It can be overridden    * by sub-classes to make sub-types    */
DECL|method|makeSplit (Path file, long start, long length, String[] hosts)
specifier|protected
name|FileSplit
name|makeSplit
parameter_list|(
name|Path
name|file
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|length
parameter_list|,
name|String
index|[]
name|hosts
parameter_list|)
block|{
return|return
operator|new
name|FileSplit
argument_list|(
name|file
argument_list|,
name|start
argument_list|,
name|length
argument_list|,
name|hosts
argument_list|)
return|;
block|}
comment|/**     * Generate the list of files and make them into FileSplits.    * @param job the job context    * @throws IOException    */
DECL|method|getSplits (JobContext job)
specifier|public
name|List
argument_list|<
name|InputSplit
argument_list|>
name|getSplits
parameter_list|(
name|JobContext
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|minSize
init|=
name|Math
operator|.
name|max
argument_list|(
name|getFormatMinSplitSize
argument_list|()
argument_list|,
name|getMinSplitSize
argument_list|(
name|job
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|maxSize
init|=
name|getMaxSplitSize
argument_list|(
name|job
argument_list|)
decl_stmt|;
comment|// generate splits
name|List
argument_list|<
name|InputSplit
argument_list|>
name|splits
init|=
operator|new
name|ArrayList
argument_list|<
name|InputSplit
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FileStatus
argument_list|>
name|files
init|=
name|listStatus
argument_list|(
name|job
argument_list|)
decl_stmt|;
for|for
control|(
name|FileStatus
name|file
range|:
name|files
control|)
block|{
name|Path
name|path
init|=
name|file
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|long
name|length
init|=
name|file
operator|.
name|getLen
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|!=
literal|0
condition|)
block|{
name|FileSystem
name|fs
init|=
name|path
operator|.
name|getFileSystem
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|BlockLocation
index|[]
name|blkLocations
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|file
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|isSplitable
argument_list|(
name|job
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|long
name|blockSize
init|=
name|file
operator|.
name|getBlockSize
argument_list|()
decl_stmt|;
name|long
name|splitSize
init|=
name|computeSplitSize
argument_list|(
name|blockSize
argument_list|,
name|minSize
argument_list|,
name|maxSize
argument_list|)
decl_stmt|;
name|long
name|bytesRemaining
init|=
name|length
decl_stmt|;
while|while
condition|(
operator|(
operator|(
name|double
operator|)
name|bytesRemaining
operator|)
operator|/
name|splitSize
operator|>
name|SPLIT_SLOP
condition|)
block|{
name|int
name|blkIndex
init|=
name|getBlockIndex
argument_list|(
name|blkLocations
argument_list|,
name|length
operator|-
name|bytesRemaining
argument_list|)
decl_stmt|;
name|splits
operator|.
name|add
argument_list|(
name|makeSplit
argument_list|(
name|path
argument_list|,
name|length
operator|-
name|bytesRemaining
argument_list|,
name|splitSize
argument_list|,
name|blkLocations
index|[
name|blkIndex
index|]
operator|.
name|getHosts
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|bytesRemaining
operator|-=
name|splitSize
expr_stmt|;
block|}
if|if
condition|(
name|bytesRemaining
operator|!=
literal|0
condition|)
block|{
name|int
name|blkIndex
init|=
name|getBlockIndex
argument_list|(
name|blkLocations
argument_list|,
name|length
operator|-
name|bytesRemaining
argument_list|)
decl_stmt|;
name|splits
operator|.
name|add
argument_list|(
name|makeSplit
argument_list|(
name|path
argument_list|,
name|length
operator|-
name|bytesRemaining
argument_list|,
name|bytesRemaining
argument_list|,
name|blkLocations
index|[
name|blkIndex
index|]
operator|.
name|getHosts
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// not splitable
name|splits
operator|.
name|add
argument_list|(
name|makeSplit
argument_list|(
name|path
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
name|blkLocations
index|[
literal|0
index|]
operator|.
name|getHosts
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//Create empty hosts array for zero length files
name|splits
operator|.
name|add
argument_list|(
name|makeSplit
argument_list|(
name|path
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Save the number of input files for metrics/loadgen
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setLong
argument_list|(
name|NUM_INPUT_FILES
argument_list|,
name|files
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Total # of splits: "
operator|+
name|splits
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|splits
return|;
block|}
DECL|method|computeSplitSize (long blockSize, long minSize, long maxSize)
specifier|protected
name|long
name|computeSplitSize
parameter_list|(
name|long
name|blockSize
parameter_list|,
name|long
name|minSize
parameter_list|,
name|long
name|maxSize
parameter_list|)
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|minSize
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|maxSize
argument_list|,
name|blockSize
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getBlockIndex (BlockLocation[] blkLocations, long offset)
specifier|protected
name|int
name|getBlockIndex
parameter_list|(
name|BlockLocation
index|[]
name|blkLocations
parameter_list|,
name|long
name|offset
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blkLocations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// is the offset inside this block?
if|if
condition|(
operator|(
name|blkLocations
index|[
name|i
index|]
operator|.
name|getOffset
argument_list|()
operator|<=
name|offset
operator|)
operator|&&
operator|(
name|offset
operator|<
name|blkLocations
index|[
name|i
index|]
operator|.
name|getOffset
argument_list|()
operator|+
name|blkLocations
index|[
name|i
index|]
operator|.
name|getLength
argument_list|()
operator|)
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
name|BlockLocation
name|last
init|=
name|blkLocations
index|[
name|blkLocations
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|long
name|fileLength
init|=
name|last
operator|.
name|getOffset
argument_list|()
operator|+
name|last
operator|.
name|getLength
argument_list|()
operator|-
literal|1
decl_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Offset "
operator|+
name|offset
operator|+
literal|" is outside of file (0.."
operator|+
name|fileLength
operator|+
literal|")"
argument_list|)
throw|;
block|}
comment|/**    * Sets the given comma separated paths as the list of inputs     * for the map-reduce job.    *     * @param job the job    * @param commaSeparatedPaths Comma separated paths to be set as     *        the list of inputs for the map-reduce job.    */
DECL|method|setInputPaths (Job job, String commaSeparatedPaths )
specifier|public
specifier|static
name|void
name|setInputPaths
parameter_list|(
name|Job
name|job
parameter_list|,
name|String
name|commaSeparatedPaths
parameter_list|)
throws|throws
name|IOException
block|{
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|StringUtils
operator|.
name|stringToPath
argument_list|(
name|getPathStrings
argument_list|(
name|commaSeparatedPaths
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add the given comma separated paths to the list of inputs for    *  the map-reduce job.    *     * @param job The job to modify    * @param commaSeparatedPaths Comma separated paths to be added to    *        the list of inputs for the map-reduce job.    */
DECL|method|addInputPaths (Job job, String commaSeparatedPaths )
specifier|public
specifier|static
name|void
name|addInputPaths
parameter_list|(
name|Job
name|job
parameter_list|,
name|String
name|commaSeparatedPaths
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|str
range|:
name|getPathStrings
argument_list|(
name|commaSeparatedPaths
argument_list|)
control|)
block|{
name|addInputPath
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set the array of {@link Path}s as the list of inputs    * for the map-reduce job.    *     * @param job The job to modify     * @param inputPaths the {@link Path}s of the input directories/files     * for the map-reduce job.    */
DECL|method|setInputPaths (Job job, Path... inputPaths)
specifier|public
specifier|static
name|void
name|setInputPaths
parameter_list|(
name|Job
name|job
parameter_list|,
name|Path
modifier|...
name|inputPaths
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|job
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
name|inputPaths
index|[
literal|0
index|]
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|inputPaths
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|StringBuffer
name|str
init|=
operator|new
name|StringBuffer
argument_list|(
name|StringUtils
operator|.
name|escapeString
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|inputPaths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|str
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|COMMA_STR
argument_list|)
expr_stmt|;
name|path
operator|=
name|inputPaths
index|[
name|i
index|]
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|inputPaths
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|str
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|escapeString
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|INPUT_DIR
argument_list|,
name|str
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a {@link Path} to the list of inputs for the map-reduce job.    *     * @param job The {@link Job} to modify    * @param path {@link Path} to be added to the list of inputs for     *            the map-reduce job.    */
DECL|method|addInputPath (Job job, Path path)
specifier|public
specifier|static
name|void
name|addInputPath
parameter_list|(
name|Job
name|job
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|job
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|path
operator|=
name|path
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|String
name|dirStr
init|=
name|StringUtils
operator|.
name|escapeString
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|dirs
init|=
name|conf
operator|.
name|get
argument_list|(
name|INPUT_DIR
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|INPUT_DIR
argument_list|,
name|dirs
operator|==
literal|null
condition|?
name|dirStr
else|:
name|dirs
operator|+
literal|","
operator|+
name|dirStr
argument_list|)
expr_stmt|;
block|}
comment|// This method escapes commas in the glob pattern of the given paths.
DECL|method|getPathStrings (String commaSeparatedPaths)
specifier|private
specifier|static
name|String
index|[]
name|getPathStrings
parameter_list|(
name|String
name|commaSeparatedPaths
parameter_list|)
block|{
name|int
name|length
init|=
name|commaSeparatedPaths
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|curlyOpen
init|=
literal|0
decl_stmt|;
name|int
name|pathStart
init|=
literal|0
decl_stmt|;
name|boolean
name|globPattern
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|pathStrings
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|commaSeparatedPaths
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'{'
case|:
block|{
name|curlyOpen
operator|++
expr_stmt|;
if|if
condition|(
operator|!
name|globPattern
condition|)
block|{
name|globPattern
operator|=
literal|true
expr_stmt|;
block|}
break|break;
block|}
case|case
literal|'}'
case|:
block|{
name|curlyOpen
operator|--
expr_stmt|;
if|if
condition|(
name|curlyOpen
operator|==
literal|0
operator|&&
name|globPattern
condition|)
block|{
name|globPattern
operator|=
literal|false
expr_stmt|;
block|}
break|break;
block|}
case|case
literal|','
case|:
block|{
if|if
condition|(
operator|!
name|globPattern
condition|)
block|{
name|pathStrings
operator|.
name|add
argument_list|(
name|commaSeparatedPaths
operator|.
name|substring
argument_list|(
name|pathStart
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|pathStart
operator|=
name|i
operator|+
literal|1
expr_stmt|;
block|}
break|break;
block|}
default|default:
continue|continue;
comment|// nothing special to do for this character
block|}
block|}
name|pathStrings
operator|.
name|add
argument_list|(
name|commaSeparatedPaths
operator|.
name|substring
argument_list|(
name|pathStart
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|pathStrings
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/**    * Get the list of input {@link Path}s for the map-reduce job.    *     * @param context The job    * @return the list of input {@link Path}s for the map-reduce job.    */
DECL|method|getInputPaths (JobContext context)
specifier|public
specifier|static
name|Path
index|[]
name|getInputPaths
parameter_list|(
name|JobContext
name|context
parameter_list|)
block|{
name|String
name|dirs
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|get
argument_list|(
name|INPUT_DIR
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|String
index|[]
name|list
init|=
name|StringUtils
operator|.
name|split
argument_list|(
name|dirs
argument_list|)
decl_stmt|;
name|Path
index|[]
name|result
init|=
operator|new
name|Path
index|[
name|list
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
operator|new
name|Path
argument_list|(
name|StringUtils
operator|.
name|unEscapeString
argument_list|(
name|list
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

