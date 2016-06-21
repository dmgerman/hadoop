begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.filecache
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|filecache
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
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
name|conf
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
name|util
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
name|fs
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
name|MRJobConfig
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

begin_comment
comment|/**  * Distribute application-specific large, read-only files efficiently.  *   *<p><code>DistributedCache</code> is a facility provided by the Map-Reduce  * framework to cache files (text, archives, jars etc.) needed by applications.  *</p>  *   *<p>Applications specify the files, via urls (hdfs:// or http://) to be cached   * via the {@link org.apache.hadoop.mapred.JobConf}. The  *<code>DistributedCache</code> assumes that the files specified via urls are  * already present on the {@link FileSystem} at the path specified by the url  * and are accessible by every machine in the cluster.</p>  *   *<p>The framework will copy the necessary files on to the slave node before   * any tasks for the job are executed on that node. Its efficiency stems from   * the fact that the files are only copied once per job and the ability to   * cache archives which are un-archived on the slaves.</p>   *  *<p><code>DistributedCache</code> can be used to distribute simple, read-only  * data/text files and/or more complex types such as archives, jars etc.   * Archives (zip, tar and tgz/tar.gz files) are un-archived at the slave nodes.   * Jars may be optionally added to the classpath of the tasks, a rudimentary   * software distribution mechanism.  Files have execution permissions.  * In older version of Hadoop Map/Reduce users could optionally ask for symlinks  * to be created in the working directory of the child task.  In the current   * version symlinks are always created.  If the URL does not have a fragment   * the name of the file or directory will be used. If multiple files or   * directories map to the same link name, the last one added, will be used.  All  * others will not even be downloaded.</p>  *   *<p><code>DistributedCache</code> tracks modification timestamps of the cache   * files. Clearly the cache files should not be modified by the application   * or externally while the job is executing.</p>  *   *<p>Here is an illustrative example on how to use the   *<code>DistributedCache</code>:</p>  *<p><blockquote><pre>  *     // Setting up the cache for the application  *       *     1. Copy the requisite files to the<code>FileSystem</code>:  *       *     $ bin/hadoop fs -copyFromLocal lookup.dat /myapp/lookup.dat    *     $ bin/hadoop fs -copyFromLocal map.zip /myapp/map.zip    *     $ bin/hadoop fs -copyFromLocal mylib.jar /myapp/mylib.jar  *     $ bin/hadoop fs -copyFromLocal mytar.tar /myapp/mytar.tar  *     $ bin/hadoop fs -copyFromLocal mytgz.tgz /myapp/mytgz.tgz  *     $ bin/hadoop fs -copyFromLocal mytargz.tar.gz /myapp/mytargz.tar.gz  *       *     2. Setup the application's<code>JobConf</code>:  *       *     JobConf job = new JobConf();  *     DistributedCache.addCacheFile(new URI("/myapp/lookup.dat#lookup.dat"),   *                                   job);  *     DistributedCache.addCacheArchive(new URI("/myapp/map.zip", job);  *     DistributedCache.addFileToClassPath(new Path("/myapp/mylib.jar"), job);  *     DistributedCache.addCacheArchive(new URI("/myapp/mytar.tar", job);  *     DistributedCache.addCacheArchive(new URI("/myapp/mytgz.tgz", job);  *     DistributedCache.addCacheArchive(new URI("/myapp/mytargz.tar.gz", job);  *       *     3. Use the cached files in the {@link org.apache.hadoop.mapred.Mapper}  *     or {@link org.apache.hadoop.mapred.Reducer}:  *       *     public static class MapClass extends MapReduceBase    *     implements Mapper&lt;K, V, K, V&gt; {  *       *       private Path[] localArchives;  *       private Path[] localFiles;  *         *       public void configure(JobConf job) {  *         // Get the cached archives/files  *         File f = new File("./map.zip/some/file/in/zip.txt");  *       }  *         *       public void map(K key, V value,   *                       OutputCollector&lt;K, V&gt; output, Reporter reporter)   *       throws IOException {  *         // Use data from the cached archives/files here  *         // ...  *         // ...  *         output.collect(k, v);  *       }  *     }  *       *</pre></blockquote>  *  * It is also very common to use the DistributedCache by using  * {@link org.apache.hadoop.util.GenericOptionsParser}.  *  * This class includes methods that should be used by users  * (specifically those mentioned in the example above, as well  * as {@link DistributedCache#addArchiveToClassPath(Path, Configuration)}),  * as well as methods intended for use by the MapReduce framework  * (e.g., {@link org.apache.hadoop.mapred.JobClient}).  *  * @see org.apache.hadoop.mapreduce.Job  * @see org.apache.hadoop.mapred.JobConf  * @see org.apache.hadoop.mapred.JobClient  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DistributedCache
specifier|public
class|class
name|DistributedCache
block|{
DECL|field|WILDCARD
specifier|public
specifier|static
specifier|final
name|String
name|WILDCARD
init|=
literal|"*"
decl_stmt|;
comment|/**    * Set the configuration with the given set of archives.  Intended    * to be used by user code.    * @param archives The list of archives that need to be localized    * @param conf Configuration which will be changed    * @deprecated Use {@link Job#setCacheArchives(URI[])} instead    * @see Job#setCacheArchives(URI[])    */
annotation|@
name|Deprecated
DECL|method|setCacheArchives (URI[] archives, Configuration conf)
specifier|public
specifier|static
name|void
name|setCacheArchives
parameter_list|(
name|URI
index|[]
name|archives
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|sarchives
init|=
name|StringUtils
operator|.
name|uriToString
argument_list|(
name|archives
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_ARCHIVES
argument_list|,
name|sarchives
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the configuration with the given set of files.  Intended to be    * used by user code.    * @param files The list of files that need to be localized    * @param conf Configuration which will be changed    * @deprecated Use {@link Job#setCacheFiles(URI[])} instead    * @see Job#setCacheFiles(URI[])    */
annotation|@
name|Deprecated
DECL|method|setCacheFiles (URI[] files, Configuration conf)
specifier|public
specifier|static
name|void
name|setCacheFiles
parameter_list|(
name|URI
index|[]
name|files
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|sfiles
init|=
name|StringUtils
operator|.
name|uriToString
argument_list|(
name|files
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES
argument_list|,
name|sfiles
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get cache archives set in the Configuration.  Used by    * internal DistributedCache and MapReduce code.    * @param conf The configuration which contains the archives    * @return A URI array of the caches set in the Configuration    * @throws IOException    * @deprecated Use {@link JobContext#getCacheArchives()} instead    * @see JobContext#getCacheArchives()    */
annotation|@
name|Deprecated
DECL|method|getCacheArchives (Configuration conf)
specifier|public
specifier|static
name|URI
index|[]
name|getCacheArchives
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|StringUtils
operator|.
name|stringToURI
argument_list|(
name|conf
operator|.
name|getStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_ARCHIVES
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get cache files set in the Configuration.  Used by internal    * DistributedCache and MapReduce code.    * @param conf The configuration which contains the files    * @return A URI array of the files set in the Configuration    * @throws IOException    * @deprecated Use {@link JobContext#getCacheFiles()} instead    * @see JobContext#getCacheFiles()    */
annotation|@
name|Deprecated
DECL|method|getCacheFiles (Configuration conf)
specifier|public
specifier|static
name|URI
index|[]
name|getCacheFiles
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|StringUtils
operator|.
name|stringToURI
argument_list|(
name|conf
operator|.
name|getStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Return the path array of the localized caches.  Intended to be used    * by user code.    * @param conf Configuration that contains the localized archives    * @return A path array of localized caches    * @throws IOException    * @deprecated Use {@link JobContext#getLocalCacheArchives()} instead    * @see JobContext#getLocalCacheArchives()    */
annotation|@
name|Deprecated
DECL|method|getLocalCacheArchives (Configuration conf)
specifier|public
specifier|static
name|Path
index|[]
name|getLocalCacheArchives
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|StringUtils
operator|.
name|stringToPath
argument_list|(
name|conf
operator|.
name|getStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_LOCALARCHIVES
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Return the path array of the localized files.  Intended to be used    * by user code.    * @param conf Configuration that contains the localized files    * @return A path array of localized files    * @throws IOException    * @deprecated Use {@link JobContext#getLocalCacheFiles()} instead    * @see JobContext#getLocalCacheFiles()    */
annotation|@
name|Deprecated
DECL|method|getLocalCacheFiles (Configuration conf)
specifier|public
specifier|static
name|Path
index|[]
name|getLocalCacheFiles
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|StringUtils
operator|.
name|stringToPath
argument_list|(
name|conf
operator|.
name|getStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_LOCALFILES
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Parse a list of strings into longs.    * @param strs the list of strings to parse    * @return a list of longs that were parsed. same length as strs.    */
DECL|method|parseTimestamps (String[] strs)
specifier|private
specifier|static
name|long
index|[]
name|parseTimestamps
parameter_list|(
name|String
index|[]
name|strs
parameter_list|)
block|{
if|if
condition|(
name|strs
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
index|[]
name|result
init|=
operator|new
name|long
index|[
name|strs
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
name|strs
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|strs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Get the timestamps of the archives.  Used by internal    * DistributedCache and MapReduce code.    * @param conf The configuration which stored the timestamps    * @return a long array of timestamps    * @deprecated Use {@link JobContext#getArchiveTimestamps()} instead    * @see JobContext#getArchiveTimestamps()    */
annotation|@
name|Deprecated
DECL|method|getArchiveTimestamps (Configuration conf)
specifier|public
specifier|static
name|long
index|[]
name|getArchiveTimestamps
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|parseTimestamps
argument_list|(
name|conf
operator|.
name|getStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_ARCHIVES_TIMESTAMPS
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get the timestamps of the files.  Used by internal    * DistributedCache and MapReduce code.    * @param conf The configuration which stored the timestamps    * @return a long array of timestamps    * @deprecated Use {@link JobContext#getFileTimestamps()} instead    * @see JobContext#getFileTimestamps()    */
annotation|@
name|Deprecated
DECL|method|getFileTimestamps (Configuration conf)
specifier|public
specifier|static
name|long
index|[]
name|getFileTimestamps
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|parseTimestamps
argument_list|(
name|conf
operator|.
name|getStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_TIMESTAMPS
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Add a archives to be localized to the conf.  Intended to    * be used by user code.    * @param uri The uri of the cache to be localized    * @param conf Configuration to add the cache to    * @deprecated Use {@link Job#addCacheArchive(URI)} instead    * @see Job#addCacheArchive(URI)    */
annotation|@
name|Deprecated
DECL|method|addCacheArchive (URI uri, Configuration conf)
specifier|public
specifier|static
name|void
name|addCacheArchive
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|archives
init|=
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_ARCHIVES
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_ARCHIVES
argument_list|,
name|archives
operator|==
literal|null
condition|?
name|uri
operator|.
name|toString
argument_list|()
else|:
name|archives
operator|+
literal|","
operator|+
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a file to be localized to the conf.  The localized file will be    * downloaded to the execution node(s), and a link will created to the    * file from the job's working directory. If the last part of URI's path name    * is "*", then the entire parent directory will be localized and links    * will be created from the job's working directory to each file in the    * parent directory.    *    * The access permissions of the file will determine whether the localized    * file will be shared across jobs.  If the file is not readable by other or    * if any of its parent directories is not executable by other, then the    * file will not be shared.  In the case of a path that ends in "/*",    * sharing of the localized files will be determined solely from the    * access permissions of the parent directories.  The access permissions of    * the individual files will be ignored.    *    * Intended to be used by user code.    *    * @param uri The uri of the cache to be localized    * @param conf Configuration to add the cache to    * @deprecated Use {@link Job#addCacheFile(URI)} instead    * @see Job#addCacheFile(URI)    */
annotation|@
name|Deprecated
DECL|method|addCacheFile (URI uri, Configuration conf)
specifier|public
specifier|static
name|void
name|addCacheFile
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|files
init|=
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES
argument_list|,
name|files
operator|==
literal|null
condition|?
name|uri
operator|.
name|toString
argument_list|()
else|:
name|files
operator|+
literal|","
operator|+
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a file path to the current set of classpath entries.  The file will    * also be added to the cache.  Intended to be used by user code.    *    * @param file Path of the file to be added    * @param conf Configuration that contains the classpath setting    * @deprecated Use {@link Job#addFileToClassPath(Path)} instead    * @see #addCacheFile(URI, Configuration)    * @see Job#addFileToClassPath(Path)    */
annotation|@
name|Deprecated
DECL|method|addFileToClassPath (Path file, Configuration conf)
specifier|public
specifier|static
name|void
name|addFileToClassPath
parameter_list|(
name|Path
name|file
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|addFileToClassPath
argument_list|(
name|file
argument_list|,
name|conf
argument_list|,
name|file
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a file path to the current set of classpath entries. The file will    * also be added to the cache.  Intended to be used by user code.    *    * @param file Path of the file to be added    * @param conf Configuration that contains the classpath setting    * @param fs FileSystem with respect to which {@code archivefile} should    *              be interpreted.    * @see #addCacheFile(URI, Configuration)    */
DECL|method|addFileToClassPath (Path file, Configuration conf, FileSystem fs)
specifier|public
specifier|static
name|void
name|addFileToClassPath
parameter_list|(
name|Path
name|file
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
block|{
name|addFileToClassPath
argument_list|(
name|file
argument_list|,
name|conf
argument_list|,
name|fs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a file path to the current set of classpath entries. The file will    * also be added to the cache if {@code addToCache} is true.  Used by    * internal DistributedCache code.    *    * @param file Path of the file to be added    * @param conf Configuration that contains the classpath setting    * @param fs FileSystem with respect to which {@code archivefile} should    *              be interpreted.    * @param addToCache whether the file should also be added to the cache list    * @see #addCacheFile(URI, Configuration)    */
DECL|method|addFileToClassPath (Path file, Configuration conf, FileSystem fs, boolean addToCache)
specifier|public
specifier|static
name|void
name|addFileToClassPath
parameter_list|(
name|Path
name|file
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|boolean
name|addToCache
parameter_list|)
block|{
name|String
name|classpath
init|=
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CLASSPATH_FILES
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CLASSPATH_FILES
argument_list|,
name|classpath
operator|==
literal|null
condition|?
name|file
operator|.
name|toString
argument_list|()
else|:
name|classpath
operator|+
literal|","
operator|+
name|file
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|addToCache
condition|)
block|{
name|URI
name|uri
init|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|file
argument_list|)
operator|.
name|toUri
argument_list|()
decl_stmt|;
name|addCacheFile
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get the file entries in classpath as an array of Path.    * Used by internal DistributedCache code.    *     * @param conf Configuration that contains the classpath setting    * @deprecated Use {@link JobContext#getFileClassPaths()} instead    * @see JobContext#getFileClassPaths()    */
annotation|@
name|Deprecated
DECL|method|getFileClassPaths (Configuration conf)
specifier|public
specifier|static
name|Path
index|[]
name|getFileClassPaths
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|(
name|ArrayList
argument_list|<
name|String
argument_list|>
operator|)
name|conf
operator|.
name|getStringCollection
argument_list|(
name|MRJobConfig
operator|.
name|CLASSPATH_FILES
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Path
index|[]
name|paths
init|=
operator|new
name|Path
index|[
name|list
operator|.
name|size
argument_list|()
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
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|paths
index|[
name|i
index|]
operator|=
operator|new
name|Path
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|paths
return|;
block|}
comment|/**    * Add an archive path to the current set of classpath entries. It adds the    * archive to cache as well.  Intended to be used by user code.    *     * @param archive Path of the archive to be added    * @param conf Configuration that contains the classpath setting    * @deprecated Use {@link Job#addArchiveToClassPath(Path)} instead    * @see Job#addArchiveToClassPath(Path)    */
annotation|@
name|Deprecated
DECL|method|addArchiveToClassPath (Path archive, Configuration conf)
specifier|public
specifier|static
name|void
name|addArchiveToClassPath
parameter_list|(
name|Path
name|archive
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|addArchiveToClassPath
argument_list|(
name|archive
argument_list|,
name|conf
argument_list|,
name|archive
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add an archive path to the current set of classpath entries. It adds the    * archive to cache as well.  Intended to be used by user code.    *    * @param archive Path of the archive to be added    * @param conf Configuration that contains the classpath setting    * @param fs FileSystem with respect to which {@code archive} should be interpreted.    */
DECL|method|addArchiveToClassPath (Path archive, Configuration conf, FileSystem fs)
specifier|public
specifier|static
name|void
name|addArchiveToClassPath
parameter_list|(
name|Path
name|archive
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|classpath
init|=
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CLASSPATH_ARCHIVES
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CLASSPATH_ARCHIVES
argument_list|,
name|classpath
operator|==
literal|null
condition|?
name|archive
operator|.
name|toString
argument_list|()
else|:
name|classpath
operator|+
literal|","
operator|+
name|archive
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|archive
argument_list|)
operator|.
name|toUri
argument_list|()
decl_stmt|;
name|addCacheArchive
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the archive entries in classpath as an array of Path.    * Used by internal DistributedCache code.    *     * @param conf Configuration that contains the classpath setting    * @deprecated Use {@link JobContext#getArchiveClassPaths()} instead     * @see JobContext#getArchiveClassPaths()    */
annotation|@
name|Deprecated
DECL|method|getArchiveClassPaths (Configuration conf)
specifier|public
specifier|static
name|Path
index|[]
name|getArchiveClassPaths
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|(
name|ArrayList
argument_list|<
name|String
argument_list|>
operator|)
name|conf
operator|.
name|getStringCollection
argument_list|(
name|MRJobConfig
operator|.
name|CLASSPATH_ARCHIVES
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Path
index|[]
name|paths
init|=
operator|new
name|Path
index|[
name|list
operator|.
name|size
argument_list|()
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
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|paths
index|[
name|i
index|]
operator|=
operator|new
name|Path
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|paths
return|;
block|}
comment|/**    * Originally intended to enable symlinks, but currently symlinks cannot be    * disabled. This is a NO-OP.    * @param conf the jobconf    * @deprecated This is a NO-OP.     */
annotation|@
name|Deprecated
DECL|method|createSymlink (Configuration conf)
specifier|public
specifier|static
name|void
name|createSymlink
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|//NOOP
block|}
comment|/**    * Originally intended to check if symlinks should be used, but currently    * symlinks cannot be disabled.    * @param conf the jobconf    * @return true    * @deprecated symlinks are always created.    */
annotation|@
name|Deprecated
DECL|method|getSymlink (Configuration conf)
specifier|public
specifier|static
name|boolean
name|getSymlink
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
DECL|method|parseBooleans (String[] strs)
specifier|private
specifier|static
name|boolean
index|[]
name|parseBooleans
parameter_list|(
name|String
index|[]
name|strs
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|strs
condition|)
block|{
return|return
literal|null
return|;
block|}
name|boolean
index|[]
name|result
init|=
operator|new
name|boolean
index|[
name|strs
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
name|strs
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|strs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Get the booleans on whether the files are public or not.  Used by     * internal DistributedCache and MapReduce code.    * @param conf The configuration which stored the timestamps    * @return a string array of booleans     */
DECL|method|getFileVisibilities (Configuration conf)
specifier|public
specifier|static
name|boolean
index|[]
name|getFileVisibilities
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|parseBooleans
argument_list|(
name|conf
operator|.
name|getStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_VISIBILITIES
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get the booleans on whether the archives are public or not.  Used by     * internal DistributedCache and MapReduce code.    * @param conf The configuration which stored the timestamps    * @return a string array of booleans     */
DECL|method|getArchiveVisibilities (Configuration conf)
specifier|public
specifier|static
name|boolean
index|[]
name|getArchiveVisibilities
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|parseBooleans
argument_list|(
name|conf
operator|.
name|getStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_ARCHIVES_VISIBILITIES
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * This method checks if there is a conflict in the fragment names     * of the uris. Also makes sure that each uri has a fragment. It     * is only to be called if you want to create symlinks for     * the various archives and files.  May be used by user code.    * @param uriFiles The uri array of urifiles    * @param uriArchives the uri array of uri archives    */
DECL|method|checkURIs (URI[] uriFiles, URI[] uriArchives)
specifier|public
specifier|static
name|boolean
name|checkURIs
parameter_list|(
name|URI
index|[]
name|uriFiles
parameter_list|,
name|URI
index|[]
name|uriArchives
parameter_list|)
block|{
if|if
condition|(
operator|(
name|uriFiles
operator|==
literal|null
operator|)
operator|&&
operator|(
name|uriArchives
operator|==
literal|null
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// check if fragment is null for any uri
comment|// also check if there are any conflicts in fragment names
name|Set
argument_list|<
name|String
argument_list|>
name|fragments
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// iterate over file uris
if|if
condition|(
name|uriFiles
operator|!=
literal|null
condition|)
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
name|uriFiles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|fragment
init|=
name|uriFiles
index|[
name|i
index|]
operator|.
name|getFragment
argument_list|()
decl_stmt|;
if|if
condition|(
name|fragment
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|lowerCaseFragment
init|=
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|fragment
argument_list|)
decl_stmt|;
if|if
condition|(
name|fragments
operator|.
name|contains
argument_list|(
name|lowerCaseFragment
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|fragments
operator|.
name|add
argument_list|(
name|lowerCaseFragment
argument_list|)
expr_stmt|;
block|}
block|}
comment|// iterate over archive uris
if|if
condition|(
name|uriArchives
operator|!=
literal|null
condition|)
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
name|uriArchives
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|fragment
init|=
name|uriArchives
index|[
name|i
index|]
operator|.
name|getFragment
argument_list|()
decl_stmt|;
if|if
condition|(
name|fragment
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|lowerCaseFragment
init|=
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|fragment
argument_list|)
decl_stmt|;
if|if
condition|(
name|fragments
operator|.
name|contains
argument_list|(
name|lowerCaseFragment
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|fragments
operator|.
name|add
argument_list|(
name|lowerCaseFragment
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

