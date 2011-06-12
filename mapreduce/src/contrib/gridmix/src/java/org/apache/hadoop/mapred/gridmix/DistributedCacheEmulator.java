begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
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
name|fs
operator|.
name|permission
operator|.
name|FsAction
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
name|permission
operator|.
name|FsPermission
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
name|BytesWritable
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
name|LongWritable
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
name|MD5Hash
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
name|SequenceFile
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
name|mapreduce
operator|.
name|MRJobConfig
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
name|rumen
operator|.
name|JobStory
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
name|rumen
operator|.
name|JobStoryProducer
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
name|rumen
operator|.
name|Pre21JobHistoryConstants
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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Emulation of Distributed Cache Usage in gridmix.  *<br> Emulation of Distributed Cache Load in gridmix will put load on  * TaskTrackers and affects execution time of tasks because of localization of  * distributed cache files by TaskTrackers.  *<br> Gridmix creates distributed cache files for simulated jobs by launching  * a MapReduce job {@link GenerateDistCacheData} in advance i.e. before  * launching simulated jobs.  *<br> The distributed cache file paths used in the original cluster are mapped  * to unique file names in the simulated cluster.  *<br> All HDFS-based distributed cache files generated by gridmix are  * public distributed cache files. But Gridmix makes sure that load incurred due  * to localization of private distributed cache files on the original cluster  * is also faithfully simulated. Gridmix emulates the load due to private  * distributed cache files by mapping private distributed cache files of  * different users in the original cluster to different public distributed cache  * files in the simulated cluster.  *  *<br> The configuration properties like  * {@link MRJobConfig#CACHE_FILES}, {@link MRJobConfig#CACHE_FILE_VISIBILITIES},  * {@link MRJobConfig#CACHE_FILES_SIZES} and  * {@link MRJobConfig#CACHE_FILE_TIMESTAMPS} obtained from trace are used to  *  decide  *<li> file size of each distributed cache file to be generated  *<li> whether a distributed cache file is already seen in this trace file  *<li> whether a distributed cache file was considered public or private.  *<br>  *<br> Gridmix configures these generated files as distributed cache files for  * the simulated jobs.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DistributedCacheEmulator
class|class
name|DistributedCacheEmulator
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
name|DistributedCacheEmulator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|AVG_BYTES_PER_MAP
specifier|static
specifier|final
name|long
name|AVG_BYTES_PER_MAP
init|=
literal|128
operator|*
literal|1024
operator|*
literal|1024L
decl_stmt|;
comment|// 128MB
comment|// If at least 1 distributed cache file is missing in the expected
comment|// distributed cache dir, Gridmix cannot proceed with emulation of
comment|// distributed cache load.
DECL|field|MISSING_DIST_CACHE_FILES_ERROR
name|int
name|MISSING_DIST_CACHE_FILES_ERROR
init|=
literal|1
decl_stmt|;
DECL|field|distCachePath
specifier|private
name|Path
name|distCachePath
decl_stmt|;
comment|/**    * Map between simulated cluster's distributed cache file paths and their    * file sizes. Unique distributed cache files are entered into this map.    * 2 distributed cache files are considered same if and only if their    * file paths, visibilities and timestamps are same.    */
DECL|field|distCacheFiles
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|distCacheFiles
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Configuration property for whether gridmix should emulate    * distributed cache usage or not. Default value is true.    */
DECL|field|GRIDMIX_EMULATE_DISTRIBUTEDCACHE
specifier|static
specifier|final
name|String
name|GRIDMIX_EMULATE_DISTRIBUTEDCACHE
init|=
literal|"gridmix.distributed-cache-emulation.enable"
decl_stmt|;
comment|// Whether to emulate distributed cache usage or not
DECL|field|emulateDistributedCache
name|boolean
name|emulateDistributedCache
init|=
literal|true
decl_stmt|;
comment|// Whether to generate distributed cache data or not
DECL|field|generateDistCacheData
name|boolean
name|generateDistCacheData
init|=
literal|false
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
comment|// gridmix configuration
comment|// Pseudo local file system where local FS based distributed cache files are
comment|// created by gridmix.
DECL|field|pseudoLocalFs
name|FileSystem
name|pseudoLocalFs
init|=
literal|null
decl_stmt|;
block|{
comment|// Need to handle deprecation of these MapReduce-internal configuration
comment|// properties as MapReduce doesn't handle their deprecation.
name|Configuration
operator|.
name|addDeprecation
argument_list|(
literal|"mapred.cache.files.filesizes"
argument_list|,
operator|new
name|String
index|[]
block|{
name|MRJobConfig
operator|.
name|CACHE_FILES_SIZES
block|}
argument_list|)
expr_stmt|;
name|Configuration
operator|.
name|addDeprecation
argument_list|(
literal|"mapred.cache.files.visibilities"
argument_list|,
operator|new
name|String
index|[]
block|{
name|MRJobConfig
operator|.
name|CACHE_FILE_VISIBILITIES
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param conf gridmix configuration    * @param ioPath&lt;ioPath&gt;/distributedCache/ is the gridmix Distributed    *               Cache directory    */
DECL|method|DistributedCacheEmulator (Configuration conf, Path ioPath)
specifier|public
name|DistributedCacheEmulator
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Path
name|ioPath
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|distCachePath
operator|=
operator|new
name|Path
argument_list|(
name|ioPath
argument_list|,
literal|"distributedCache"
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|.
name|setClass
argument_list|(
literal|"fs.pseudo.impl"
argument_list|,
name|PseudoLocalFs
operator|.
name|class
argument_list|,
name|FileSystem
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * This is to be called before any other method of DistributedCacheEmulator.    *<br> Checks if emulation of distributed cache load is needed and is feasible.    *  Sets the flags generateDistCacheData and emulateDistributedCache to the    *  appropriate values.    *<br> Gridmix does not emulate distributed cache load if    *<ol><li> the specific gridmix job type doesn't need emulation of    * distributed cache load OR    *<li> the trace is coming from a stream instead of file OR    *<li> the distributed cache dir where distributed cache data is to be    * generated by gridmix is on local file system OR    *<li> execute permission is not there for any of the ascendant directories    * of&lt;ioPath&gt; till root. This is because for emulation of distributed    * cache load, distributed cache files created under    *&lt;ioPath/distributedCache/public/&gt; should be considered by hadoop    * as public distributed cache files.    *<li> creation of pseudo local file system fails.</ol>    *<br> For (2), (3), (4) and (5), generation of distributed cache data    * is also disabled.    *     * @param traceIn trace file path. If this is '-', then trace comes from the    *                stream stdin.    * @param jobCreator job creator of gridmix jobs of a specific type    * @param generate  true if -generate option was specified    * @throws IOException    */
DECL|method|init (String traceIn, JobCreator jobCreator, boolean generate)
name|void
name|init
parameter_list|(
name|String
name|traceIn
parameter_list|,
name|JobCreator
name|jobCreator
parameter_list|,
name|boolean
name|generate
parameter_list|)
throws|throws
name|IOException
block|{
name|emulateDistributedCache
operator|=
name|jobCreator
operator|.
name|canEmulateDistCacheLoad
argument_list|()
operator|&&
name|conf
operator|.
name|getBoolean
argument_list|(
name|GRIDMIX_EMULATE_DISTRIBUTEDCACHE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|generateDistCacheData
operator|=
name|generate
expr_stmt|;
if|if
condition|(
name|generateDistCacheData
operator|||
name|emulateDistributedCache
condition|)
block|{
if|if
condition|(
literal|"-"
operator|.
name|equals
argument_list|(
name|traceIn
argument_list|)
condition|)
block|{
comment|// trace is from stdin
name|LOG
operator|.
name|warn
argument_list|(
literal|"Gridmix will not emulate Distributed Cache load because "
operator|+
literal|"the input trace source is a stream instead of file."
argument_list|)
expr_stmt|;
name|emulateDistributedCache
operator|=
name|generateDistCacheData
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
operator|.
name|getUri
argument_list|()
operator|.
name|getScheme
argument_list|()
operator|.
name|equals
argument_list|(
name|distCachePath
operator|.
name|toUri
argument_list|()
operator|.
name|getScheme
argument_list|()
argument_list|)
condition|)
block|{
comment|// local FS
name|LOG
operator|.
name|warn
argument_list|(
literal|"Gridmix will not emulate Distributed Cache load because "
operator|+
literal|"<iopath> provided is on local file system."
argument_list|)
expr_stmt|;
name|emulateDistributedCache
operator|=
name|generateDistCacheData
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
comment|// Check if execute permission is there for all the ascendant
comment|// directories of distCachePath till root.
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|cur
init|=
name|distCachePath
operator|.
name|getParent
argument_list|()
decl_stmt|;
while|while
condition|(
name|cur
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|cur
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|FsPermission
name|perm
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|cur
argument_list|)
operator|.
name|getPermission
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|perm
operator|.
name|getOtherAction
argument_list|()
operator|.
name|and
argument_list|(
name|FsAction
operator|.
name|EXECUTE
argument_list|)
operator|.
name|equals
argument_list|(
name|FsAction
operator|.
name|EXECUTE
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Gridmix will not emulate Distributed Cache load "
operator|+
literal|"because the ascendant directory (of distributed cache "
operator|+
literal|"directory) "
operator|+
name|cur
operator|+
literal|" doesn't have execute permission "
operator|+
literal|"for others."
argument_list|)
expr_stmt|;
name|emulateDistributedCache
operator|=
name|generateDistCacheData
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
name|cur
operator|=
name|cur
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Check if pseudo local file system can be created
try|try
block|{
name|pseudoLocalFs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
literal|"pseudo:///"
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Gridmix will not emulate Distributed Cache load because "
operator|+
literal|"creation of pseudo local file system failed."
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|emulateDistributedCache
operator|=
name|generateDistCacheData
operator|=
literal|false
expr_stmt|;
return|return;
block|}
block|}
comment|/**    * @return true if gridmix should emulate distributed cache load    */
DECL|method|shouldEmulateDistCacheLoad ()
name|boolean
name|shouldEmulateDistCacheLoad
parameter_list|()
block|{
return|return
name|emulateDistributedCache
return|;
block|}
comment|/**    * @return true if gridmix should generate distributed cache data    */
DECL|method|shouldGenerateDistCacheData ()
name|boolean
name|shouldGenerateDistCacheData
parameter_list|()
block|{
return|return
name|generateDistCacheData
return|;
block|}
comment|/**    * @return the distributed cache directory path    */
DECL|method|getDistributedCacheDir ()
name|Path
name|getDistributedCacheDir
parameter_list|()
block|{
return|return
name|distCachePath
return|;
block|}
comment|/**    * Create distributed cache directories.    * Also create a file that contains the list of distributed cache files    * that will be used as distributed cache files for all the simulated jobs.    * @param jsp job story producer for the trace    * @return exit code    * @throws IOException    */
DECL|method|setupGenerateDistCacheData (JobStoryProducer jsp)
name|int
name|setupGenerateDistCacheData
parameter_list|(
name|JobStoryProducer
name|jsp
parameter_list|)
throws|throws
name|IOException
block|{
name|createDistCacheDirectory
argument_list|()
expr_stmt|;
return|return
name|buildDistCacheFilesList
argument_list|(
name|jsp
argument_list|)
return|;
block|}
comment|/**    * Create distributed cache directory where distributed cache files will be    * created by the MapReduce job {@link GenerateDistCacheData#JOB_NAME}.    * @throws IOException    */
DECL|method|createDistCacheDirectory ()
specifier|private
name|void
name|createDistCacheDirectory
parameter_list|()
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileSystem
operator|.
name|mkdirs
argument_list|(
name|fs
argument_list|,
name|distCachePath
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create the list of unique distributed cache files needed for all the    * simulated jobs and write the list to a special file.    * @param jsp job story producer for the trace    * @return exit code    * @throws IOException    */
DECL|method|buildDistCacheFilesList (JobStoryProducer jsp)
specifier|private
name|int
name|buildDistCacheFilesList
parameter_list|(
name|JobStoryProducer
name|jsp
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Read all the jobs from the trace file and build the list of unique
comment|// distributed cache files.
name|JobStory
name|jobStory
decl_stmt|;
while|while
condition|(
operator|(
name|jobStory
operator|=
name|jsp
operator|.
name|getNextJob
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|jobStory
operator|.
name|getOutcome
argument_list|()
operator|==
name|Pre21JobHistoryConstants
operator|.
name|Values
operator|.
name|SUCCESS
operator|&&
name|jobStory
operator|.
name|getSubmissionTime
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|updateHDFSDistCacheFilesList
argument_list|(
name|jobStory
argument_list|)
expr_stmt|;
block|}
block|}
name|jsp
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|writeDistCacheFilesList
argument_list|()
return|;
block|}
comment|/**    * For the job to be simulated, identify the needed distributed cache files by    * mapping original cluster's distributed cache file paths to the simulated cluster's    * paths and add these paths in the map {@code distCacheFiles}.    *<br>    * JobStory should contain distributed cache related properties like    *<li> {@link MRJobConfig#CACHE_FILES}    *<li> {@link MRJobConfig#CACHE_FILE_VISIBILITIES}    *<li> {@link MRJobConfig#CACHE_FILES_SIZES}    *<li> {@link MRJobConfig#CACHE_FILE_TIMESTAMPS}    *<li> {@link MRJobConfig#CLASSPATH_FILES}    *    *<li> {@link MRJobConfig#CACHE_ARCHIVES}    *<li> {@link MRJobConfig#CACHE_ARCHIVES_VISIBILITIES}    *<li> {@link MRJobConfig#CACHE_ARCHIVES_SIZES}    *<li> {@link MRJobConfig#CACHE_ARCHIVES_TIMESTAMPS}    *<li> {@link MRJobConfig#CLASSPATH_ARCHIVES}    *    *<li> {@link MRJobConfig#CACHE_SYMLINK}    *    * @param jobdesc JobStory of original job obtained from trace    * @throws IOException    */
DECL|method|updateHDFSDistCacheFilesList (JobStory jobdesc)
name|void
name|updateHDFSDistCacheFilesList
parameter_list|(
name|JobStory
name|jobdesc
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Map original job's distributed cache file paths to simulated cluster's
comment|// paths, to be used by this simulated job.
name|JobConf
name|jobConf
init|=
name|jobdesc
operator|.
name|getJobConf
argument_list|()
decl_stmt|;
name|String
index|[]
name|files
init|=
name|jobConf
operator|.
name|getStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES
argument_list|)
decl_stmt|;
if|if
condition|(
name|files
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|fileSizes
init|=
name|jobConf
operator|.
name|getStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES_SIZES
argument_list|)
decl_stmt|;
name|String
index|[]
name|visibilities
init|=
name|jobConf
operator|.
name|getStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_VISIBILITIES
argument_list|)
decl_stmt|;
name|String
index|[]
name|timeStamps
init|=
name|jobConf
operator|.
name|getStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_TIMESTAMPS
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|user
init|=
name|jobConf
operator|.
name|getUser
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// Check if visibilities are available because older hadoop versions
comment|// didn't have public, private Distributed Caches separately.
name|boolean
name|visibility
init|=
operator|(
name|visibilities
operator|==
literal|null
operator|)
condition|?
literal|true
else|:
name|Boolean
operator|.
name|valueOf
argument_list|(
name|visibilities
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|isLocalDistCacheFile
argument_list|(
name|files
index|[
name|i
index|]
argument_list|,
name|user
argument_list|,
name|visibility
argument_list|)
condition|)
block|{
comment|// local FS based distributed cache file.
comment|// Create this file on the pseudo local FS on the fly (i.e. when the
comment|// simulated job is submitted).
continue|continue;
block|}
comment|// distributed cache file on hdfs
name|String
name|mappedPath
init|=
name|mapDistCacheFilePath
argument_list|(
name|files
index|[
name|i
index|]
argument_list|,
name|timeStamps
index|[
name|i
index|]
argument_list|,
name|visibility
argument_list|,
name|user
argument_list|)
decl_stmt|;
comment|// No need to add a distributed cache file path to the list if
comment|// (1) the mapped path is already there in the list OR
comment|// (2) the file with the mapped path already exists.
comment|// In any of the above 2 cases, file paths, timestamps, file sizes and
comment|// visibilities match. File sizes should match if file paths and
comment|// timestamps match because single file path with single timestamp
comment|// should correspond to a single file size.
if|if
condition|(
name|distCacheFiles
operator|.
name|containsKey
argument_list|(
name|mappedPath
argument_list|)
operator|||
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|mappedPath
argument_list|)
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|distCacheFiles
operator|.
name|put
argument_list|(
name|mappedPath
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|fileSizes
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Check if the file path provided was constructed by MapReduce for a    * distributed cache file on local file system.    * @param filePath path of the distributed cache file    * @param user job submitter of the job for which&lt;filePath&gt; is a    *             distributed cache file    * @param visibility<code>true</code> for public distributed cache file    * @return true if the path provided is of a local file system based    *              distributed cache file    */
DECL|method|isLocalDistCacheFile (String filePath, String user, boolean visibility)
specifier|static
name|boolean
name|isLocalDistCacheFile
parameter_list|(
name|String
name|filePath
parameter_list|,
name|String
name|user
parameter_list|,
name|boolean
name|visibility
parameter_list|)
block|{
return|return
operator|(
operator|!
name|visibility
operator|&&
name|filePath
operator|.
name|contains
argument_list|(
name|user
operator|+
literal|"/.staging"
argument_list|)
operator|)
return|;
block|}
comment|/**    * Map the HDFS based distributed cache file path from original cluster to    * a unique file name on the simulated cluster.    *<br> Unique  distributed file names on simulated cluster are generated    * using original cluster's<li>file path,<li>timestamp and<li> the    * job-submitter for private distributed cache file.    *<br> This implies that if on original cluster, a single HDFS file    * considered as two private distributed cache files for two jobs of    * different users, then the corresponding simulated jobs will have two    * different files of the same size in public distributed cache, one for each    * user. Both these simulated jobs will not share these distributed cache    * files, thus leading to the same load as seen in the original cluster.    * @param file distributed cache file path    * @param timeStamp time stamp of dist cachce file    * @param isPublic true if this distributed cache file is a public    *                 distributed cache file    * @param user job submitter on original cluster    * @return the mapped path on simulated cluster    */
DECL|method|mapDistCacheFilePath (String file, String timeStamp, boolean isPublic, String user)
specifier|private
name|String
name|mapDistCacheFilePath
parameter_list|(
name|String
name|file
parameter_list|,
name|String
name|timeStamp
parameter_list|,
name|boolean
name|isPublic
parameter_list|,
name|String
name|user
parameter_list|)
block|{
name|String
name|id
init|=
name|file
operator|+
name|timeStamp
decl_stmt|;
if|if
condition|(
operator|!
name|isPublic
condition|)
block|{
comment|// consider job-submitter for private distributed cache file
name|id
operator|=
name|id
operator|.
name|concat
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Path
argument_list|(
name|distCachePath
argument_list|,
name|MD5Hash
operator|.
name|digest
argument_list|(
name|id
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
return|;
block|}
comment|/**    * Write the list of distributed cache files in the decreasing order of    * file sizes into the sequence file. This file will be input to the job    * {@link GenerateDistCacheData}.    * Also validates if -generate option is missing and distributed cache files    * are missing.    * @return exit code    * @throws IOException    */
DECL|method|writeDistCacheFilesList ()
specifier|private
name|int
name|writeDistCacheFilesList
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Sort the distributed cache files in the decreasing order of file sizes.
name|List
name|dcFiles
init|=
operator|new
name|ArrayList
argument_list|(
name|distCacheFiles
operator|.
name|entrySet
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|dcFiles
argument_list|,
operator|new
name|Comparator
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|dc1
parameter_list|,
name|Object
name|dc2
parameter_list|)
block|{
return|return
operator|(
call|(
name|Comparable
call|)
argument_list|(
call|(
name|Map
operator|.
name|Entry
call|)
argument_list|(
name|dc2
argument_list|)
argument_list|)
operator|.
name|getValue
argument_list|()
operator|)
operator|.
name|compareTo
argument_list|(
operator|(
call|(
name|Map
operator|.
name|Entry
call|)
argument_list|(
name|dc1
argument_list|)
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// write the sorted distributed cache files to the sequence file
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|distCacheFilesList
init|=
operator|new
name|Path
argument_list|(
name|distCachePath
argument_list|,
literal|"_distCacheFiles.txt"
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|GenerateDistCacheData
operator|.
name|GRIDMIX_DISTCACHE_FILE_LIST
argument_list|,
name|distCacheFilesList
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|SequenceFile
operator|.
name|Writer
name|src_writer
init|=
name|SequenceFile
operator|.
name|createWriter
argument_list|(
name|fs
argument_list|,
name|conf
argument_list|,
name|distCacheFilesList
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|BytesWritable
operator|.
name|class
argument_list|,
name|SequenceFile
operator|.
name|CompressionType
operator|.
name|NONE
argument_list|)
decl_stmt|;
comment|// Total number of unique distributed cache files
name|int
name|fileCount
init|=
name|dcFiles
operator|.
name|size
argument_list|()
decl_stmt|;
name|long
name|byteCount
init|=
literal|0
decl_stmt|;
comment|// Total size of all distributed cache files
name|long
name|bytesSync
init|=
literal|0
decl_stmt|;
comment|// Bytes after previous sync;used to add sync marker
for|for
control|(
name|Iterator
name|it
init|=
name|dcFiles
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|LongWritable
name|fileSize
init|=
operator|new
name|LongWritable
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|BytesWritable
name|filePath
init|=
operator|new
name|BytesWritable
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|byteCount
operator|+=
name|fileSize
operator|.
name|get
argument_list|()
expr_stmt|;
name|bytesSync
operator|+=
name|fileSize
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|bytesSync
operator|>
name|AVG_BYTES_PER_MAP
condition|)
block|{
name|src_writer
operator|.
name|sync
argument_list|()
expr_stmt|;
name|bytesSync
operator|=
name|fileSize
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|src_writer
operator|.
name|append
argument_list|(
name|fileSize
argument_list|,
name|filePath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|src_writer
operator|!=
literal|null
condition|)
block|{
name|src_writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Set delete on exit for 'dist cache files list' as it is not needed later.
name|fs
operator|.
name|deleteOnExit
argument_list|(
name|distCacheFilesList
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|GenerateDistCacheData
operator|.
name|GRIDMIX_DISTCACHE_FILE_COUNT
argument_list|,
name|fileCount
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|GenerateDistCacheData
operator|.
name|GRIDMIX_DISTCACHE_BYTE_COUNT
argument_list|,
name|byteCount
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Number of HDFS based distributed cache files to be generated is "
operator|+
name|fileCount
operator|+
literal|". Total size of HDFS based distributed cache files "
operator|+
literal|"to be generated is "
operator|+
name|byteCount
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|shouldGenerateDistCacheData
argument_list|()
operator|&&
name|fileCount
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Missing "
operator|+
name|fileCount
operator|+
literal|" distributed cache files under the "
operator|+
literal|" directory\n"
operator|+
name|distCachePath
operator|+
literal|"\nthat are needed for gridmix"
operator|+
literal|" to emulate distributed cache load. Either use -generate\noption"
operator|+
literal|" to generate distributed cache data along with input data OR "
operator|+
literal|"disable\ndistributed cache emulation by configuring '"
operator|+
name|DistributedCacheEmulator
operator|.
name|GRIDMIX_EMULATE_DISTRIBUTEDCACHE
operator|+
literal|"' to false."
argument_list|)
expr_stmt|;
return|return
name|MISSING_DIST_CACHE_FILES_ERROR
return|;
block|}
return|return
literal|0
return|;
block|}
comment|/**    * If gridmix needs to emulate distributed cache load, then configure    * distributed cache files of a simulated job by mapping the original    * cluster's distributed cache file paths to the simulated cluster's paths and    * setting these mapped paths in the job configuration of the simulated job.    *<br>    * Configure local FS based distributed cache files through the property    * "tmpfiles" and hdfs based distributed cache files through the property    * {@link MRJobConfig#CACHE_FILES}.    * @param conf configuration for the simulated job to be run    * @param jobConf job configuration of original cluster's job, obtained from    *                trace    * @throws IOException    */
DECL|method|configureDistCacheFiles (Configuration conf, JobConf jobConf)
name|void
name|configureDistCacheFiles
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|JobConf
name|jobConf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|shouldEmulateDistCacheLoad
argument_list|()
condition|)
block|{
name|String
index|[]
name|files
init|=
name|jobConf
operator|.
name|getStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES
argument_list|)
decl_stmt|;
if|if
condition|(
name|files
operator|!=
literal|null
condition|)
block|{
comment|// hdfs based distributed cache files to be configured for simulated job
name|List
argument_list|<
name|String
argument_list|>
name|cacheFiles
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// local FS based distributed cache files to be configured for
comment|// simulated job
name|List
argument_list|<
name|String
argument_list|>
name|localCacheFiles
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
index|[]
name|visibilities
init|=
name|jobConf
operator|.
name|getStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_VISIBILITIES
argument_list|)
decl_stmt|;
name|String
index|[]
name|timeStamps
init|=
name|jobConf
operator|.
name|getStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_TIMESTAMPS
argument_list|)
decl_stmt|;
name|String
index|[]
name|fileSizes
init|=
name|jobConf
operator|.
name|getStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES_SIZES
argument_list|)
decl_stmt|;
name|String
name|user
init|=
name|jobConf
operator|.
name|getUser
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// Check if visibilities are available because older hadoop versions
comment|// didn't have public, private Distributed Caches separately.
name|boolean
name|visibility
init|=
operator|(
name|visibilities
operator|==
literal|null
operator|)
condition|?
literal|true
else|:
name|Boolean
operator|.
name|valueOf
argument_list|(
name|visibilities
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|isLocalDistCacheFile
argument_list|(
name|files
index|[
name|i
index|]
argument_list|,
name|user
argument_list|,
name|visibility
argument_list|)
condition|)
block|{
comment|// local FS based distributed cache file.
comment|// Create this file on the pseudo local FS.
name|String
name|fileId
init|=
name|MD5Hash
operator|.
name|digest
argument_list|(
name|files
index|[
name|i
index|]
operator|+
name|timeStamps
index|[
name|i
index|]
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|long
name|fileSize
init|=
name|Long
operator|.
name|valueOf
argument_list|(
name|fileSizes
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|Path
name|mappedLocalFilePath
init|=
name|PseudoLocalFs
operator|.
name|generateFilePath
argument_list|(
name|fileId
argument_list|,
name|fileSize
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|pseudoLocalFs
operator|.
name|getUri
argument_list|()
argument_list|,
name|pseudoLocalFs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|pseudoLocalFs
operator|.
name|create
argument_list|(
name|mappedLocalFilePath
argument_list|)
expr_stmt|;
name|localCacheFiles
operator|.
name|add
argument_list|(
name|mappedLocalFilePath
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// hdfs based distributed cache file.
comment|// Get the mapped HDFS path on simulated cluster
name|String
name|mappedPath
init|=
name|mapDistCacheFilePath
argument_list|(
name|files
index|[
name|i
index|]
argument_list|,
name|timeStamps
index|[
name|i
index|]
argument_list|,
name|visibility
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|cacheFiles
operator|.
name|add
argument_list|(
name|mappedPath
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|cacheFiles
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// configure hdfs based distributed cache files for simulated job
name|conf
operator|.
name|setStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES
argument_list|,
name|cacheFiles
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|cacheFiles
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|localCacheFiles
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// configure local FS based distributed cache files for simulated job
name|conf
operator|.
name|setStrings
argument_list|(
literal|"tmpfiles"
argument_list|,
name|localCacheFiles
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|localCacheFiles
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

