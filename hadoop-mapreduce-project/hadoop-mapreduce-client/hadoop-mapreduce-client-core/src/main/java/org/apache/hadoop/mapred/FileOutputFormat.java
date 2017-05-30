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
name|InterfaceAudience
operator|.
name|Private
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
name|Progressable
import|;
end_import

begin_comment
comment|/** A base class for {@link OutputFormat}. */
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
implements|implements
name|OutputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
annotation|@
name|Deprecated
DECL|enum|Counter
specifier|public
enum|enum
name|Counter
block|{
DECL|enumConstant|BYTES_WRITTEN
name|BYTES_WRITTEN
block|}
comment|/**    * Set whether the output of the job is compressed.    * @param conf the {@link JobConf} to modify    * @param compress should the output of the job be compressed?    */
DECL|method|setCompressOutput (JobConf conf, boolean compress)
specifier|public
specifier|static
name|void
name|setCompressOutput
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|boolean
name|compress
parameter_list|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
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
name|FileOutputFormat
operator|.
name|COMPRESS
argument_list|,
name|compress
argument_list|)
expr_stmt|;
block|}
comment|/**    * Is the job output compressed?    * @param conf the {@link JobConf} to look in    * @return<code>true</code> if the job output should be compressed,    *<code>false</code> otherwise    */
DECL|method|getCompressOutput (JobConf conf)
specifier|public
specifier|static
name|boolean
name|getCompressOutput
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getBoolean
argument_list|(
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
name|FileOutputFormat
operator|.
name|COMPRESS
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Set the {@link CompressionCodec} to be used to compress job outputs.    * @param conf the {@link JobConf} to modify    * @param codecClass the {@link CompressionCodec} to be used to    *                   compress the job outputs    */
specifier|public
specifier|static
name|void
DECL|method|setOutputCompressorClass (JobConf conf, Class<? extends CompressionCodec> codecClass)
name|setOutputCompressorClass
parameter_list|(
name|JobConf
name|conf
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
name|conf
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
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
comment|/**    * Get the {@link CompressionCodec} for compressing the job outputs.    * @param conf the {@link JobConf} to look in    * @param defaultValue the {@link CompressionCodec} to return if not set    * @return the {@link CompressionCodec} to be used to compress the     *         job outputs    * @throws IllegalArgumentException if the class was specified, but not found    */
specifier|public
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|CompressionCodec
argument_list|>
DECL|method|getOutputCompressorClass (JobConf conf, Class<? extends CompressionCodec> defaultValue)
name|getOutputCompressorClass
parameter_list|(
name|JobConf
name|conf
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
name|String
name|name
init|=
name|conf
operator|.
name|get
argument_list|(
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
DECL|method|getRecordWriter (FileSystem ignored, JobConf job, String name, Progressable progress)
specifier|public
specifier|abstract
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
DECL|method|checkOutputSpecs (FileSystem ignored, JobConf job)
specifier|public
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
name|FileAlreadyExistsException
throws|,
name|InvalidJobConfException
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
operator|&&
name|job
operator|.
name|getNumReduceTasks
argument_list|()
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|InvalidJobConfException
argument_list|(
literal|"Output directory not set in JobConf."
argument_list|)
throw|;
block|}
if|if
condition|(
name|outDir
operator|!=
literal|null
condition|)
block|{
name|FileSystem
name|fs
init|=
name|outDir
operator|.
name|getFileSystem
argument_list|(
name|job
argument_list|)
decl_stmt|;
comment|// normalize the output directory
name|outDir
operator|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|outDir
argument_list|)
expr_stmt|;
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
comment|// get delegation token for the outDir's file system
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
argument_list|)
expr_stmt|;
comment|// check its existence
if|if
condition|(
name|fs
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
block|}
comment|/**    * Set the {@link Path} of the output directory for the map-reduce job.    *    * @param conf The configuration of the job.    * @param outputDir the {@link Path} of the output directory for     * the map-reduce job.    */
DECL|method|setOutputPath (JobConf conf, Path outputDir)
specifier|public
specifier|static
name|void
name|setOutputPath
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|Path
name|outputDir
parameter_list|)
block|{
name|outputDir
operator|=
operator|new
name|Path
argument_list|(
name|conf
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|,
name|outputDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
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
comment|/**    * Set the {@link Path} of the task's temporary output directory     * for the map-reduce job.    *     *<p><i>Note</i>: Task output path is set by the framework.    *</p>    * @param conf The configuration of the job.    * @param outputDir the {@link Path} of the output directory     * for the map-reduce job.    */
annotation|@
name|Private
DECL|method|setWorkOutputPath (JobConf conf, Path outputDir)
specifier|public
specifier|static
name|void
name|setWorkOutputPath
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|Path
name|outputDir
parameter_list|)
block|{
name|outputDir
operator|=
operator|new
name|Path
argument_list|(
name|conf
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|,
name|outputDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|TASK_OUTPUT_DIR
argument_list|,
name|outputDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the {@link Path} to the output directory for the map-reduce job.    *     * @return the {@link Path} to the output directory for the map-reduce job.    * @see FileOutputFormat#getWorkOutputPath(JobConf)    */
DECL|method|getOutputPath (JobConf conf)
specifier|public
specifier|static
name|Path
name|getOutputPath
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
name|String
name|name
init|=
name|conf
operator|.
name|get
argument_list|(
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
comment|/**    *  Get the {@link Path} to the task's temporary output directory     *  for the map-reduce job    *      *<b id="SideEffectFiles">Tasks' Side-Effect Files</b>    *     *<p><i>Note:</i> The following is valid only if the {@link OutputCommitter}    *  is {@link FileOutputCommitter}. If<code>OutputCommitter</code> is not     *  a<code>FileOutputCommitter</code>, the task's temporary output    *  directory is same as {@link #getOutputPath(JobConf)} i.e.    *<tt>${mapreduce.output.fileoutputformat.outputdir}$</tt></p>    *      *<p>Some applications need to create/write-to side-files, which differ from    * the actual job-outputs.    *     *<p>In such cases there could be issues with 2 instances of the same TIP     * (running simultaneously e.g. speculative tasks) trying to open/write-to the    * same file (path) on HDFS. Hence the application-writer will have to pick     * unique names per task-attempt (e.g. using the attemptid, say     *<tt>attempt_200709221812_0001_m_000000_0</tt>), not just per TIP.</p>     *     *<p>To get around this the Map-Reduce framework helps the application-writer     * out by maintaining a special     *<tt>${mapreduce.output.fileoutputformat.outputdir}/_temporary/_${taskid}</tt>     * sub-directory for each task-attempt on HDFS where the output of the     * task-attempt goes. On successful completion of the task-attempt the files     * in the<tt>${mapreduce.output.fileoutputformat.outputdir}/_temporary/_${taskid}</tt> (only)     * are<i>promoted</i> to<tt>${mapreduce.output.fileoutputformat.outputdir}</tt>. Of course, the     * framework discards the sub-directory of unsuccessful task-attempts. This     * is completely transparent to the application.</p>    *     *<p>The application-writer can take advantage of this by creating any     * side-files required in<tt>${mapreduce.task.output.dir}</tt> during execution     * of his reduce-task i.e. via {@link #getWorkOutputPath(JobConf)}, and the     * framework will move them out similarly - thus she doesn't have to pick     * unique paths per task-attempt.</p>    *     *<p><i>Note</i>: the value of<tt>${mapreduce.task.output.dir}</tt> during     * execution of a particular task-attempt is actually     *<tt>${mapreduce.output.fileoutputformat.outputdir}/_temporary/_{$taskid}</tt>, and this value is     * set by the map-reduce framework. So, just create any side-files in the     * path  returned by {@link #getWorkOutputPath(JobConf)} from map/reduce     * task to take advantage of this feature.</p>    *     *<p>The entire discussion holds true for maps of jobs with     * reducer=NONE (i.e. 0 reduces) since output of the map, in that case,     * goes directly to HDFS.</p>     *     * @return the {@link Path} to the task's temporary output directory     * for the map-reduce job.    */
DECL|method|getWorkOutputPath (JobConf conf)
specifier|public
specifier|static
name|Path
name|getWorkOutputPath
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
name|String
name|name
init|=
name|conf
operator|.
name|get
argument_list|(
name|JobContext
operator|.
name|TASK_OUTPUT_DIR
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
comment|/**    * Helper function to create the task's temporary output directory and     * return the path to the task's output file.    *     * @param conf job-configuration    * @param name temporary task-output filename    * @return path to the task's temporary output file    * @throws IOException    */
DECL|method|getTaskOutputPath (JobConf conf, String name)
specifier|public
specifier|static
name|Path
name|getTaskOutputPath
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
comment|// ${mapred.out.dir}
name|Path
name|outputPath
init|=
name|getOutputPath
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|outputPath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Undefined job output-path"
argument_list|)
throw|;
block|}
name|OutputCommitter
name|committer
init|=
name|conf
operator|.
name|getOutputCommitter
argument_list|()
decl_stmt|;
name|Path
name|workPath
init|=
name|outputPath
decl_stmt|;
name|TaskAttemptContext
name|context
init|=
operator|new
name|TaskAttemptContextImpl
argument_list|(
name|conf
argument_list|,
name|TaskAttemptID
operator|.
name|forName
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|JobContext
operator|.
name|TASK_ATTEMPT_ID
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|committer
operator|instanceof
name|FileOutputCommitter
condition|)
block|{
name|workPath
operator|=
operator|(
operator|(
name|FileOutputCommitter
operator|)
name|committer
operator|)
operator|.
name|getWorkPath
argument_list|(
name|context
argument_list|,
name|outputPath
argument_list|)
expr_stmt|;
block|}
comment|// ${mapred.out.dir}/_temporary/_${taskid}/${name}
return|return
operator|new
name|Path
argument_list|(
name|workPath
argument_list|,
name|name
argument_list|)
return|;
block|}
comment|/**    * Helper function to generate a name that is unique for the task.    *    *<p>The generated name can be used to create custom files from within the    * different tasks for the job, the names for different tasks will not collide    * with each other.</p>    *    *<p>The given name is postfixed with the task type, 'm' for maps, 'r' for    * reduces and the task partition number. For example, give a name 'test'    * running on the first map o the job the generated name will be    * 'test-m-00000'.</p>    *    * @param conf the configuration for the job.    * @param name the name to make unique.    * @return a unique name accross all tasks of the job.    */
DECL|method|getUniqueName (JobConf conf, String name)
specifier|public
specifier|static
name|String
name|getUniqueName
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|int
name|partition
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|JobContext
operator|.
name|TASK_PARTITION
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|partition
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"This method can only be called from within a Job"
argument_list|)
throw|;
block|}
name|String
name|taskType
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|JobContext
operator|.
name|TASK_ISMAP
argument_list|,
name|JobContext
operator|.
name|DEFAULT_TASK_ISMAP
argument_list|)
condition|?
literal|"m"
else|:
literal|"r"
decl_stmt|;
name|NumberFormat
name|numberFormat
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|numberFormat
operator|.
name|setMinimumIntegerDigits
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|numberFormat
operator|.
name|setGroupingUsed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|name
operator|+
literal|"-"
operator|+
name|taskType
operator|+
literal|"-"
operator|+
name|numberFormat
operator|.
name|format
argument_list|(
name|partition
argument_list|)
return|;
block|}
comment|/**    * Helper function to generate a {@link Path} for a file that is unique for    * the task within the job output directory.    *    *<p>The path can be used to create custom files from within the map and    * reduce tasks. The path name will be unique for each task. The path parent    * will be the job output directory.</p>ls    *    *<p>This method uses the {@link #getUniqueName} method to make the file name    * unique for the task.</p>    *    * @param conf the configuration for the job.    * @param name the name for the file.    * @return a unique path accross all tasks of the job.    */
DECL|method|getPathForCustomFile (JobConf conf, String name)
specifier|public
specifier|static
name|Path
name|getPathForCustomFile
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|getWorkOutputPath
argument_list|(
name|conf
argument_list|)
argument_list|,
name|getUniqueName
argument_list|(
name|conf
argument_list|,
name|name
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

