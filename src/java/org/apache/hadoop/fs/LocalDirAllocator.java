begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|commons
operator|.
name|logging
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
name|util
operator|.
name|DiskChecker
operator|.
name|DiskErrorException
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

begin_comment
comment|/** An implementation of a round-robin scheme for disk allocation for creating  * files. The way it works is that it is kept track what disk was last  * allocated for a file write. For the current request, the next disk from  * the set of disks would be allocated if the free space on the disk is   * sufficient enough to accommodate the file that is being considered for  * creation. If the space requirements cannot be met, the next disk in order  * would be tried and so on till a disk is found with sufficient capacity.  * Once a disk with sufficient space is identified, a check is done to make  * sure that the disk is writable. Also, there is an API provided that doesn't  * take the space requirements into consideration but just checks whether the  * disk under consideration is writable (this should be used for cases where  * the file size is not known apriori). An API is provided to read a path that  * was created earlier. That API works by doing a scan of all the disks for the  * input pathname.  * This implementation also provides the functionality of having multiple   * allocators per JVM (one for each unique functionality or context, like   * mapred, dfs-client, etc.). It ensures that there is only one instance of  * an allocator per context per JVM.  * Note:  * 1. The contexts referred above are actually the configuration items defined  * in the Configuration class like "mapred.local.dir" (for which we want to   * control the dir allocations). The context-strings are exactly those   * configuration items.  * 2. This implementation does not take into consideration cases where  * a disk becomes read-only or goes out of space while a file is being written  * to (disks are shared between multiple processes, and so the latter situation  * is probable).  * 3. In the class implementation, "Disk" is referred to as "Dir", which  * actually points to the configured directory on the Disk which will be the  * parent for all file write/read allocations.  */
end_comment

begin_class
DECL|class|LocalDirAllocator
specifier|public
class|class
name|LocalDirAllocator
block|{
comment|//A Map from the config item names like "mapred.local.dir",
comment|//"dfs.client.buffer.dir" to the instance of the AllocatorPerContext. This
comment|//is a static object to make sure there exists exactly one instance per JVM
DECL|field|contexts
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|AllocatorPerContext
argument_list|>
name|contexts
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|AllocatorPerContext
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|contextCfgItemName
specifier|private
name|String
name|contextCfgItemName
decl_stmt|;
comment|/** Used when size of file to be allocated is unknown. */
DECL|field|SIZE_UNKNOWN
specifier|public
specifier|static
specifier|final
name|int
name|SIZE_UNKNOWN
init|=
operator|-
literal|1
decl_stmt|;
comment|/**Create an allocator object    * @param contextCfgItemName    */
DECL|method|LocalDirAllocator (String contextCfgItemName)
specifier|public
name|LocalDirAllocator
parameter_list|(
name|String
name|contextCfgItemName
parameter_list|)
block|{
name|this
operator|.
name|contextCfgItemName
operator|=
name|contextCfgItemName
expr_stmt|;
block|}
comment|/** This method must be used to obtain the dir allocation context for a     * particular value of the context name. The context name must be an item    * defined in the Configuration object for which we want to control the     * dir allocations (e.g.,<code>mapred.local.dir</code>). The method will    * create a context for that name if it doesn't already exist.    */
DECL|method|obtainContext (String contextCfgItemName)
specifier|private
name|AllocatorPerContext
name|obtainContext
parameter_list|(
name|String
name|contextCfgItemName
parameter_list|)
block|{
synchronized|synchronized
init|(
name|contexts
init|)
block|{
name|AllocatorPerContext
name|l
init|=
name|contexts
operator|.
name|get
argument_list|(
name|contextCfgItemName
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
name|contexts
operator|.
name|put
argument_list|(
name|contextCfgItemName
argument_list|,
operator|(
name|l
operator|=
operator|new
name|AllocatorPerContext
argument_list|(
name|contextCfgItemName
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
name|l
return|;
block|}
block|}
comment|/** Get a path from the local FS. This method should be used if the size of     *  the file is not known apriori. We go round-robin over the set of disks    *  (via the configured dirs) and return the first complete path where    *  we could create the parent directory of the passed path.     *  @param pathStr the requested path (this will be created on the first     *  available disk)    *  @param conf the Configuration object    *  @return the complete path to the file on a local disk    *  @throws IOException    */
DECL|method|getLocalPathForWrite (String pathStr, Configuration conf)
specifier|public
name|Path
name|getLocalPathForWrite
parameter_list|(
name|String
name|pathStr
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getLocalPathForWrite
argument_list|(
name|pathStr
argument_list|,
name|SIZE_UNKNOWN
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/** Get a path from the local FS. Pass size as     *  SIZE_UNKNOWN if not known apriori. We    *  round-robin over the set of disks (via the configured dirs) and return    *  the first complete path which has enough space     *  @param pathStr the requested path (this will be created on the first     *  available disk)    *  @param size the size of the file that is going to be written    *  @param conf the Configuration object    *  @return the complete path to the file on a local disk    *  @throws IOException    */
DECL|method|getLocalPathForWrite (String pathStr, long size, Configuration conf)
specifier|public
name|Path
name|getLocalPathForWrite
parameter_list|(
name|String
name|pathStr
parameter_list|,
name|long
name|size
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|AllocatorPerContext
name|context
init|=
name|obtainContext
argument_list|(
name|contextCfgItemName
argument_list|)
decl_stmt|;
return|return
name|context
operator|.
name|getLocalPathForWrite
argument_list|(
name|pathStr
argument_list|,
name|size
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/** Get a path from the local FS for reading. We search through all the    *  configured dirs for the file's existence and return the complete    *  path to the file when we find one     *  @param pathStr the requested file (this will be searched)    *  @param conf the Configuration object    *  @return the complete path to the file on a local disk    *  @throws IOException    */
DECL|method|getLocalPathToRead (String pathStr, Configuration conf)
specifier|public
name|Path
name|getLocalPathToRead
parameter_list|(
name|String
name|pathStr
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|AllocatorPerContext
name|context
init|=
name|obtainContext
argument_list|(
name|contextCfgItemName
argument_list|)
decl_stmt|;
return|return
name|context
operator|.
name|getLocalPathToRead
argument_list|(
name|pathStr
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/** Creates a temporary file in the local FS. Pass size as -1 if not known     *  apriori. We round-robin over the set of disks (via the configured dirs)     *  and select the first complete path which has enough space. A file is    *  created on this directory. The file is guaranteed to go away when the    *  JVM exits.    *  @param pathStr prefix for the temporary file    *  @param size the size of the file that is going to be written    *  @param conf the Configuration object    *  @return a unique temporary file    *  @throws IOException    */
DECL|method|createTmpFileForWrite (String pathStr, long size, Configuration conf)
specifier|public
name|File
name|createTmpFileForWrite
parameter_list|(
name|String
name|pathStr
parameter_list|,
name|long
name|size
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|AllocatorPerContext
name|context
init|=
name|obtainContext
argument_list|(
name|contextCfgItemName
argument_list|)
decl_stmt|;
return|return
name|context
operator|.
name|createTmpFileForWrite
argument_list|(
name|pathStr
argument_list|,
name|size
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/** Method to check whether a context is valid    * @param contextCfgItemName    * @return true/false    */
DECL|method|isContextValid (String contextCfgItemName)
specifier|public
specifier|static
name|boolean
name|isContextValid
parameter_list|(
name|String
name|contextCfgItemName
parameter_list|)
block|{
synchronized|synchronized
init|(
name|contexts
init|)
block|{
return|return
name|contexts
operator|.
name|containsKey
argument_list|(
name|contextCfgItemName
argument_list|)
return|;
block|}
block|}
comment|/** We search through all the configured dirs for the file's existence    *  and return true when we find      *  @param pathStr the requested file (this will be searched)    *  @param conf the Configuration object    *  @return true if files exist. false otherwise    *  @throws IOException    */
DECL|method|ifExists (String pathStr,Configuration conf)
specifier|public
name|boolean
name|ifExists
parameter_list|(
name|String
name|pathStr
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|AllocatorPerContext
name|context
init|=
name|obtainContext
argument_list|(
name|contextCfgItemName
argument_list|)
decl_stmt|;
return|return
name|context
operator|.
name|ifExists
argument_list|(
name|pathStr
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/**    * Get the current directory index for the given configuration item.    * @return the current directory index for the given configuration item.    */
DECL|method|getCurrentDirectoryIndex ()
name|int
name|getCurrentDirectoryIndex
parameter_list|()
block|{
name|AllocatorPerContext
name|context
init|=
name|obtainContext
argument_list|(
name|contextCfgItemName
argument_list|)
decl_stmt|;
return|return
name|context
operator|.
name|getCurrentDirectoryIndex
argument_list|()
return|;
block|}
DECL|class|AllocatorPerContext
specifier|private
specifier|static
class|class
name|AllocatorPerContext
block|{
DECL|field|LOG
specifier|private
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AllocatorPerContext
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dirNumLastAccessed
specifier|private
name|int
name|dirNumLastAccessed
decl_stmt|;
DECL|field|dirIndexRandomizer
specifier|private
name|Random
name|dirIndexRandomizer
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|localFS
specifier|private
name|FileSystem
name|localFS
decl_stmt|;
DECL|field|dirDF
specifier|private
name|DF
index|[]
name|dirDF
decl_stmt|;
DECL|field|contextCfgItemName
specifier|private
name|String
name|contextCfgItemName
decl_stmt|;
DECL|field|localDirs
specifier|private
name|String
index|[]
name|localDirs
decl_stmt|;
DECL|field|savedLocalDirs
specifier|private
name|String
name|savedLocalDirs
init|=
literal|""
decl_stmt|;
DECL|method|AllocatorPerContext (String contextCfgItemName)
specifier|public
name|AllocatorPerContext
parameter_list|(
name|String
name|contextCfgItemName
parameter_list|)
block|{
name|this
operator|.
name|contextCfgItemName
operator|=
name|contextCfgItemName
expr_stmt|;
block|}
comment|/** This method gets called everytime before any read/write to make sure      * that any change to localDirs is reflected immediately.      */
DECL|method|confChanged (Configuration conf)
specifier|private
name|void
name|confChanged
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|newLocalDirs
init|=
name|conf
operator|.
name|get
argument_list|(
name|contextCfgItemName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|newLocalDirs
operator|.
name|equals
argument_list|(
name|savedLocalDirs
argument_list|)
condition|)
block|{
name|localDirs
operator|=
name|conf
operator|.
name|getStrings
argument_list|(
name|contextCfgItemName
argument_list|)
expr_stmt|;
name|localFS
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|int
name|numDirs
init|=
name|localDirs
operator|.
name|length
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|dirs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|numDirs
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|DF
argument_list|>
name|dfList
init|=
operator|new
name|ArrayList
argument_list|<
name|DF
argument_list|>
argument_list|(
name|numDirs
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
name|numDirs
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
comment|// filter problematic directories
name|Path
name|tmpDir
init|=
operator|new
name|Path
argument_list|(
name|localDirs
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|localFS
operator|.
name|mkdirs
argument_list|(
name|tmpDir
argument_list|)
operator|||
name|localFS
operator|.
name|exists
argument_list|(
name|tmpDir
argument_list|)
condition|)
block|{
try|try
block|{
name|DiskChecker
operator|.
name|checkDir
argument_list|(
operator|new
name|File
argument_list|(
name|localDirs
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|dirs
operator|.
name|add
argument_list|(
name|localDirs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|dfList
operator|.
name|add
argument_list|(
operator|new
name|DF
argument_list|(
operator|new
name|File
argument_list|(
name|localDirs
index|[
name|i
index|]
argument_list|)
argument_list|,
literal|30000
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DiskErrorException
name|de
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|localDirs
index|[
name|i
index|]
operator|+
literal|"is not writable\n"
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|de
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to create "
operator|+
name|localDirs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to create "
operator|+
name|localDirs
index|[
name|i
index|]
operator|+
literal|": "
operator|+
name|ie
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\n"
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ie
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//ignore
block|}
name|localDirs
operator|=
name|dirs
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|dirs
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|dirDF
operator|=
name|dfList
operator|.
name|toArray
argument_list|(
operator|new
name|DF
index|[
name|dirs
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|savedLocalDirs
operator|=
name|newLocalDirs
expr_stmt|;
comment|// randomize the first disk picked in the round-robin selection
name|dirNumLastAccessed
operator|=
name|dirIndexRandomizer
operator|.
name|nextInt
argument_list|(
name|dirs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createPath (String path)
specifier|private
name|Path
name|createPath
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
operator|new
name|Path
argument_list|(
name|localDirs
index|[
name|dirNumLastAccessed
index|]
argument_list|)
argument_list|,
name|path
argument_list|)
decl_stmt|;
comment|//check whether we are able to create a directory here. If the disk
comment|//happens to be RDONLY we will fail
try|try
block|{
name|DiskChecker
operator|.
name|checkDir
argument_list|(
operator|new
name|File
argument_list|(
name|file
operator|.
name|getParent
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|file
return|;
block|}
catch|catch
parameter_list|(
name|DiskErrorException
name|d
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Get the current directory index.      * @return the current directory index.      */
DECL|method|getCurrentDirectoryIndex ()
name|int
name|getCurrentDirectoryIndex
parameter_list|()
block|{
return|return
name|dirNumLastAccessed
return|;
block|}
comment|/** Get a path from the local FS. This method should be used if the size of       *  the file is not known a priori.       *        *  It will use roulette selection, picking directories      *  with probability proportional to their available space.       */
DECL|method|getLocalPathForWrite (String path, Configuration conf)
specifier|public
specifier|synchronized
name|Path
name|getLocalPathForWrite
parameter_list|(
name|String
name|path
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getLocalPathForWrite
argument_list|(
name|path
argument_list|,
name|SIZE_UNKNOWN
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/** Get a path from the local FS. If size is known, we go      *  round-robin over the set of disks (via the configured dirs) and return      *  the first complete path which has enough space.      *        *  If size is not known, use roulette selection -- pick directories      *  with probability proportional to their available space.      */
DECL|method|getLocalPathForWrite (String pathStr, long size, Configuration conf)
specifier|public
specifier|synchronized
name|Path
name|getLocalPathForWrite
parameter_list|(
name|String
name|pathStr
parameter_list|,
name|long
name|size
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|confChanged
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|int
name|numDirs
init|=
name|localDirs
operator|.
name|length
decl_stmt|;
name|int
name|numDirsSearched
init|=
literal|0
decl_stmt|;
comment|//remove the leading slash from the path (to make sure that the uri
comment|//resolution results in a valid path on the dir being checked)
if|if
condition|(
name|pathStr
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|pathStr
operator|=
name|pathStr
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|Path
name|returnPath
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|size
operator|==
name|SIZE_UNKNOWN
condition|)
block|{
comment|//do roulette selection: pick dir with probability
comment|//proportional to available size
name|long
index|[]
name|availableOnDisk
init|=
operator|new
name|long
index|[
name|dirDF
operator|.
name|length
index|]
decl_stmt|;
name|long
name|totalAvailable
init|=
literal|0
decl_stmt|;
comment|//build the "roulette wheel"
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dirDF
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|availableOnDisk
index|[
name|i
index|]
operator|=
name|dirDF
index|[
name|i
index|]
operator|.
name|getAvailable
argument_list|()
expr_stmt|;
name|totalAvailable
operator|+=
name|availableOnDisk
index|[
name|i
index|]
expr_stmt|;
block|}
comment|// Keep rolling the wheel till we get a valid path
name|Random
name|r
init|=
operator|new
name|java
operator|.
name|util
operator|.
name|Random
argument_list|()
decl_stmt|;
while|while
condition|(
name|numDirsSearched
operator|<
name|numDirs
operator|&&
name|returnPath
operator|==
literal|null
condition|)
block|{
name|long
name|randomPosition
init|=
name|Math
operator|.
name|abs
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
operator|%
name|totalAvailable
decl_stmt|;
name|int
name|dir
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|randomPosition
operator|>
name|availableOnDisk
index|[
name|dir
index|]
condition|)
block|{
name|randomPosition
operator|-=
name|availableOnDisk
index|[
name|dir
index|]
expr_stmt|;
name|dir
operator|++
expr_stmt|;
block|}
name|dirNumLastAccessed
operator|=
name|dir
expr_stmt|;
name|returnPath
operator|=
name|createPath
argument_list|(
name|pathStr
argument_list|)
expr_stmt|;
if|if
condition|(
name|returnPath
operator|==
literal|null
condition|)
block|{
name|totalAvailable
operator|-=
name|availableOnDisk
index|[
name|dir
index|]
expr_stmt|;
name|availableOnDisk
index|[
name|dir
index|]
operator|=
literal|0
expr_stmt|;
comment|// skip this disk
name|numDirsSearched
operator|++
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
while|while
condition|(
name|numDirsSearched
operator|<
name|numDirs
operator|&&
name|returnPath
operator|==
literal|null
condition|)
block|{
name|long
name|capacity
init|=
name|dirDF
index|[
name|dirNumLastAccessed
index|]
operator|.
name|getAvailable
argument_list|()
decl_stmt|;
if|if
condition|(
name|capacity
operator|>
name|size
condition|)
block|{
name|returnPath
operator|=
name|createPath
argument_list|(
name|pathStr
argument_list|)
expr_stmt|;
block|}
name|dirNumLastAccessed
operator|++
expr_stmt|;
name|dirNumLastAccessed
operator|=
name|dirNumLastAccessed
operator|%
name|numDirs
expr_stmt|;
name|numDirsSearched
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|returnPath
operator|!=
literal|null
condition|)
block|{
return|return
name|returnPath
return|;
block|}
comment|//no path found
throw|throw
operator|new
name|DiskErrorException
argument_list|(
literal|"Could not find any valid local "
operator|+
literal|"directory for "
operator|+
name|pathStr
argument_list|)
throw|;
block|}
comment|/** Creates a file on the local FS. Pass size as       * {@link LocalDirAllocator.SIZE_UNKNOWN} if not known apriori. We      *  round-robin over the set of disks (via the configured dirs) and return      *  a file on the first path which has enough space. The file is guaranteed      *  to go away when the JVM exits.      */
DECL|method|createTmpFileForWrite (String pathStr, long size, Configuration conf)
specifier|public
name|File
name|createTmpFileForWrite
parameter_list|(
name|String
name|pathStr
parameter_list|,
name|long
name|size
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// find an appropriate directory
name|Path
name|path
init|=
name|getLocalPathForWrite
argument_list|(
name|pathStr
argument_list|,
name|size
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|path
operator|.
name|getParent
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|prefix
init|=
name|path
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// create a temp file on this directory
name|File
name|result
init|=
name|File
operator|.
name|createTempFile
argument_list|(
name|prefix
argument_list|,
literal|null
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|result
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Get a path from the local FS for reading. We search through all the      *  configured dirs for the file's existence and return the complete      *  path to the file when we find one       */
DECL|method|getLocalPathToRead (String pathStr, Configuration conf)
specifier|public
specifier|synchronized
name|Path
name|getLocalPathToRead
parameter_list|(
name|String
name|pathStr
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|confChanged
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|int
name|numDirs
init|=
name|localDirs
operator|.
name|length
decl_stmt|;
name|int
name|numDirsSearched
init|=
literal|0
decl_stmt|;
comment|//remove the leading slash from the path (to make sure that the uri
comment|//resolution results in a valid path on the dir being checked)
if|if
condition|(
name|pathStr
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|pathStr
operator|=
name|pathStr
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|numDirsSearched
operator|<
name|numDirs
condition|)
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|localDirs
index|[
name|numDirsSearched
index|]
argument_list|,
name|pathStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|localFS
operator|.
name|exists
argument_list|(
name|file
argument_list|)
condition|)
block|{
return|return
name|file
return|;
block|}
name|numDirsSearched
operator|++
expr_stmt|;
block|}
comment|//no path found
throw|throw
operator|new
name|DiskErrorException
argument_list|(
literal|"Could not find "
operator|+
name|pathStr
operator|+
literal|" in any of"
operator|+
literal|" the configured local directories"
argument_list|)
throw|;
block|}
comment|/** We search through all the configured dirs for the file's existence      *  and return true when we find one       */
DECL|method|ifExists (String pathStr,Configuration conf)
specifier|public
specifier|synchronized
name|boolean
name|ifExists
parameter_list|(
name|String
name|pathStr
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
try|try
block|{
name|int
name|numDirs
init|=
name|localDirs
operator|.
name|length
decl_stmt|;
name|int
name|numDirsSearched
init|=
literal|0
decl_stmt|;
comment|//remove the leading slash from the path (to make sure that the uri
comment|//resolution results in a valid path on the dir being checked)
if|if
condition|(
name|pathStr
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|pathStr
operator|=
name|pathStr
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|numDirsSearched
operator|<
name|numDirs
condition|)
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|localDirs
index|[
name|numDirsSearched
index|]
argument_list|,
name|pathStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|localFS
operator|.
name|exists
argument_list|(
name|file
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|numDirsSearched
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// IGNORE and try again
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

