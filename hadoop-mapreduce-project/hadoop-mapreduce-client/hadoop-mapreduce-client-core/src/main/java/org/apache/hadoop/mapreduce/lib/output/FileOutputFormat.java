begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|text
operator|.
name|NumberFormat
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
name|io
operator|.
name|compress
operator|.
name|CompressionCodec
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
name|FileAlreadyExistsException
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
name|InvalidJobConfException
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
name|OutputFormat
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
name|RecordWriter
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
name|TaskInputOutputContext
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

begin_comment
comment|/** A base class for {@link OutputFormat}s that read from {@link FileSystem}s.*/
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
DECL|class|FileOutputFormat
specifier|public
specifier|abstract
class|class
name|FileOutputFormat
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|OutputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
comment|/** Construct output file names so that, when an output directory listing is    * sorted lexicographically, positions correspond to output partitions.*/
DECL|field|NUMBER_FORMAT
specifier|private
specifier|static
specifier|final
name|NumberFormat
name|NUMBER_FORMAT
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|()
decl_stmt|;
DECL|field|BASE_OUTPUT_NAME
specifier|protected
specifier|static
specifier|final
name|String
name|BASE_OUTPUT_NAME
init|=
literal|"mapreduce.output.basename"
decl_stmt|;
DECL|field|PART
specifier|protected
specifier|static
specifier|final
name|String
name|PART
init|=
literal|"part"
decl_stmt|;
static|static
block|{
name|NUMBER_FORMAT
operator|.
name|setMinimumIntegerDigits
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|NUMBER_FORMAT
operator|.
name|setGroupingUsed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|field|committer
specifier|private
name|FileOutputCommitter
name|committer
init|=
literal|null
decl_stmt|;
DECL|field|COMPRESS
specifier|public
specifier|static
specifier|final
name|String
name|COMPRESS
init|=
literal|"mapreduce.output.fileoutputformat.compress"
decl_stmt|;
DECL|field|COMPRESS_CODEC
specifier|public
specifier|static
specifier|final
name|String
name|COMPRESS_CODEC
init|=
literal|"mapreduce.output.fileoutputformat.compress.codec"
decl_stmt|;
DECL|field|COMPRESS_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|COMPRESS_TYPE
init|=
literal|"mapreduce.output.fileoutputformat.compress.type"
decl_stmt|;
DECL|field|OUTDIR
specifier|public
specifier|static
specifier|final
name|String
name|OUTDIR
init|=
literal|"mapreduce.output.fileoutputformat.outputdir"
decl_stmt|;
annotation|@
name|Deprecated
DECL|enum|Counter
specifier|public
specifier|static
enum|enum
name|Counter
block|{
DECL|enumConstant|BYTES_WRITTEN
name|BYTES_WRITTEN
block|}
comment|/**    * Set whether the output of the job is compressed.    * @param job the job to modify    * @param compress should the output of the job be compressed?    */
DECL|method|setCompressOutput (Job job, boolean compress)
specifier|public
specifier|static
name|void
name|setCompressOutput
parameter_list|(
name|Job
name|job
parameter_list|,
name|boolean
name|compress
parameter_list|)
block|{
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setBoolean
argument_list|(
name|FileOutputFormat
operator|.
name|COMPRESS
argument_list|,
name|compress
argument_list|)
expr_stmt|;
block|}
comment|/**    * Is the job output compressed?    * @param job the Job to look in    * @return<code>true</code> if the job output should be compressed,    *<code>false</code> otherwise    */
DECL|method|getCompressOutput (JobContext job)
specifier|public
specifier|static
name|boolean
name|getCompressOutput
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
name|FileOutputFormat
operator|.
name|COMPRESS
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Set the {@link CompressionCodec} to be used to compress job outputs.    * @param job the job to modify    * @param codecClass the {@link CompressionCodec} to be used to    *                   compress the job outputs    */
specifier|public
specifier|static
name|void
DECL|method|setOutputCompressorClass (Job job, Class<? extends CompressionCodec> codecClass)
name|setOutputCompressorClass
parameter_list|(
name|Job
name|job
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|CompressionCodec
argument_list|>
name|codecClass
parameter_list|)
block|{
name|setCompressOutput
argument_list|(
name|job
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setClass
argument_list|(
name|FileOutputFormat
operator|.
name|COMPRESS_CODEC
argument_list|,
name|codecClass
argument_list|,
name|CompressionCodec
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the {@link CompressionCodec} for compressing the job outputs.    * @param job the {@link Job} to look in    * @param defaultValue the {@link CompressionCodec} to return if not set    * @return the {@link CompressionCodec} to be used to compress the     *         job outputs    * @throws IllegalArgumentException if the class was specified, but not found    */
specifier|public
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|CompressionCodec
argument_list|>
DECL|method|getOutputCompressorClass (JobContext job, Class<? extends CompressionCodec> defaultValue)
name|getOutputCompressorClass
parameter_list|(
name|JobContext
name|job
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|CompressionCodec
argument_list|>
name|defaultValue
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|CompressionCodec
argument_list|>
name|codecClass
init|=
name|defaultValue
decl_stmt|;
name|Configuration
name|conf
init|=
name|job
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|conf
operator|.
name|get
argument_list|(
name|FileOutputFormat
operator|.
name|COMPRESS_CODEC
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|codecClass
operator|=
name|conf
operator|.
name|getClassByName
argument_list|(
name|name
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|CompressionCodec
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Compression codec "
operator|+
name|name
operator|+
literal|" was not found."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|codecClass
return|;
block|}
specifier|public
specifier|abstract
name|RecordWriter
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
DECL|method|getRecordWriter (TaskAttemptContext job )
name|getRecordWriter
parameter_list|(
name|TaskAttemptContext
name|job
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
DECL|method|checkOutputSpecs (JobContext job )
specifier|public
name|void
name|checkOutputSpecs
parameter_list|(
name|JobContext
name|job
parameter_list|)
throws|throws
name|FileAlreadyExistsException
throws|,
name|IOException
block|{
comment|// Ensure that the output directory is set and not already there
name|Path
name|outDir
init|=
name|getOutputPath
argument_list|(
name|job
argument_list|)
decl_stmt|;
if|if
condition|(
name|outDir
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidJobConfException
argument_list|(
literal|"Output directory not set."
argument_list|)
throw|;
block|}
comment|// get delegation token for outDir's file system
name|TokenCache
operator|.
name|obtainTokensForNamenodes
argument_list|(
name|job
operator|.
name|getCredentials
argument_list|()
argument_list|,
operator|new
name|Path
index|[]
block|{
name|outDir
block|}
argument_list|,
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|outDir
operator|.
name|getFileSystem
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
operator|.
name|exists
argument_list|(
name|outDir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|FileAlreadyExistsException
argument_list|(
literal|"Output directory "
operator|+
name|outDir
operator|+
literal|" already exists"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Set the {@link Path} of the output directory for the map-reduce job.    *    * @param job The job to modify    * @param outputDir the {@link Path} of the output directory for     * the map-reduce job.    */
DECL|method|setOutputPath (Job job, Path outputDir)
specifier|public
specifier|static
name|void
name|setOutputPath
parameter_list|(
name|Job
name|job
parameter_list|,
name|Path
name|outputDir
parameter_list|)
block|{
try|try
block|{
name|outputDir
operator|=
name|outputDir
operator|.
name|getFileSystem
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|outputDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Throw the IOException as a RuntimeException to be compatible with MR1
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|set
argument_list|(
name|FileOutputFormat
operator|.
name|OUTDIR
argument_list|,
name|outputDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the {@link Path} to the output directory for the map-reduce job.    *     * @return the {@link Path} to the output directory for the map-reduce job.    * @see FileOutputFormat#getWorkOutputPath(TaskInputOutputContext)    */
DECL|method|getOutputPath (JobContext job)
specifier|public
specifier|static
name|Path
name|getOutputPath
parameter_list|(
name|JobContext
name|job
parameter_list|)
block|{
name|String
name|name
init|=
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|get
argument_list|(
name|FileOutputFormat
operator|.
name|OUTDIR
argument_list|)
decl_stmt|;
return|return
name|name
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|Path
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    *  Get the {@link Path} to the task's temporary output directory     *  for the map-reduce job    *      *<h4 id="SideEffectFiles">Tasks' Side-Effect Files</h4>    *     *<p>Some applications need to create/write-to side-files, which differ from    * the actual job-outputs.    *     *<p>In such cases there could be issues with 2 instances of the same TIP     * (running simultaneously e.g. speculative tasks) trying to open/write-to the    * same file (path) on HDFS. Hence the application-writer will have to pick     * unique names per task-attempt (e.g. using the attemptid, say     *<tt>attempt_200709221812_0001_m_000000_0</tt>), not just per TIP.</p>     *     *<p>To get around this the Map-Reduce framework helps the application-writer     * out by maintaining a special     *<tt>${mapreduce.output.fileoutputformat.outputdir}/_temporary/_${taskid}</tt>     * sub-directory for each task-attempt on HDFS where the output of the     * task-attempt goes. On successful completion of the task-attempt the files     * in the<tt>${mapreduce.output.fileoutputformat.outputdir}/_temporary/_${taskid}</tt> (only)     * are<i>promoted</i> to<tt>${mapreduce.output.fileoutputformat.outputdir}</tt>. Of course, the     * framework discards the sub-directory of unsuccessful task-attempts. This     * is completely transparent to the application.</p>    *     *<p>The application-writer can take advantage of this by creating any     * side-files required in a work directory during execution     * of his task i.e. via     * {@link #getWorkOutputPath(TaskInputOutputContext)}, and    * the framework will move them out similarly - thus she doesn't have to pick     * unique paths per task-attempt.</p>    *     *<p>The entire discussion holds true for maps of jobs with     * reducer=NONE (i.e. 0 reduces) since output of the map, in that case,     * goes directly to HDFS.</p>     *     * @return the {@link Path} to the task's temporary output directory     * for the map-reduce job.    */
DECL|method|getWorkOutputPath (TaskInputOutputContext<?,?,?,?> context )
specifier|public
specifier|static
name|Path
name|getWorkOutputPath
parameter_list|(
name|TaskInputOutputContext
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|FileOutputCommitter
name|committer
init|=
operator|(
name|FileOutputCommitter
operator|)
name|context
operator|.
name|getOutputCommitter
argument_list|()
decl_stmt|;
return|return
name|committer
operator|.
name|getWorkPath
argument_list|()
return|;
block|}
comment|/**    * Helper function to generate a {@link Path} for a file that is unique for    * the task within the job output directory.    *    *<p>The path can be used to create custom files from within the map and    * reduce tasks. The path name will be unique for each task. The path parent    * will be the job output directory.</p>ls    *    *<p>This method uses the {@link #getUniqueFile} method to make the file name    * unique for the task.</p>    *    * @param context the context for the task.    * @param name the name for the file.    * @param extension the extension for the file    * @return a unique path accross all tasks of the job.    */
specifier|public
DECL|method|getPathForWorkFile (TaskInputOutputContext<?,?,?,?> context, String name, String extension )
specifier|static
name|Path
name|getPathForWorkFile
parameter_list|(
name|TaskInputOutputContext
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
name|context
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|extension
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
operator|new
name|Path
argument_list|(
name|getWorkOutputPath
argument_list|(
name|context
argument_list|)
argument_list|,
name|getUniqueFile
argument_list|(
name|context
argument_list|,
name|name
argument_list|,
name|extension
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Generate a unique filename, based on the task id, name, and extension    * @param context the task that is calling this    * @param name the base filename    * @param extension the filename extension    * @return a string like $name-[mrsct]-$id$extension    */
DECL|method|getUniqueFile (TaskAttemptContext context, String name, String extension)
specifier|public
specifier|synchronized
specifier|static
name|String
name|getUniqueFile
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|extension
parameter_list|)
block|{
name|TaskID
name|taskId
init|=
name|context
operator|.
name|getTaskAttemptID
argument_list|()
operator|.
name|getTaskID
argument_list|()
decl_stmt|;
name|int
name|partition
init|=
name|taskId
operator|.
name|getId
argument_list|()
decl_stmt|;
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|TaskID
operator|.
name|getRepresentingCharacter
argument_list|(
name|taskId
operator|.
name|getTaskType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|NUMBER_FORMAT
operator|.
name|format
argument_list|(
name|partition
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|extension
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Get the default path and filename for the output format.    * @param context the task context    * @param extension an extension to add to the filename    * @return a full path $output/_temporary/$taskid/part-[mr]-$id    * @throws IOException    */
DECL|method|getDefaultWorkFile (TaskAttemptContext context, String extension)
specifier|public
name|Path
name|getDefaultWorkFile
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|,
name|String
name|extension
parameter_list|)
throws|throws
name|IOException
block|{
name|FileOutputCommitter
name|committer
init|=
operator|(
name|FileOutputCommitter
operator|)
name|getOutputCommitter
argument_list|(
name|context
argument_list|)
decl_stmt|;
return|return
operator|new
name|Path
argument_list|(
name|committer
operator|.
name|getWorkPath
argument_list|()
argument_list|,
name|getUniqueFile
argument_list|(
name|context
argument_list|,
name|getOutputName
argument_list|(
name|context
argument_list|)
argument_list|,
name|extension
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get the base output name for the output file.    */
DECL|method|getOutputName (JobContext job)
specifier|protected
specifier|static
name|String
name|getOutputName
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
name|get
argument_list|(
name|BASE_OUTPUT_NAME
argument_list|,
name|PART
argument_list|)
return|;
block|}
comment|/**    * Set the base output name for output file to be created.    */
DECL|method|setOutputName (JobContext job, String name)
specifier|protected
specifier|static
name|void
name|setOutputName
parameter_list|(
name|JobContext
name|job
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|set
argument_list|(
name|BASE_OUTPUT_NAME
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
DECL|method|getOutputCommitter (TaskAttemptContext context )
name|OutputCommitter
name|getOutputCommitter
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|committer
operator|==
literal|null
condition|)
block|{
name|Path
name|output
init|=
name|getOutputPath
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|committer
operator|=
operator|new
name|FileOutputCommitter
argument_list|(
name|output
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
return|return
name|committer
return|;
block|}
block|}
end_class

end_unit

