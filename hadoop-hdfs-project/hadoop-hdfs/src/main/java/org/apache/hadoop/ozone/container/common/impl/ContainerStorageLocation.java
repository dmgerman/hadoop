begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
package|;
end_package

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
name|CachingGetSpaceUsed
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
name|DF
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
name|GetSpaceUsed
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|StorageLocation
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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|DatanodeStorage
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
name|IOUtils
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
name|ozone
operator|.
name|OzoneConsts
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
name|ShutdownHookManager
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
name|Time
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Scanner
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|RunJar
operator|.
name|SHUTDOWN_HOOK_PRIORITY
import|;
end_import

begin_comment
comment|/**  * Class that wraps the space usage of the Datanode Container Storage Location  * by SCM containers.  */
end_comment

begin_class
DECL|class|ContainerStorageLocation
specifier|public
class|class
name|ContainerStorageLocation
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ContainerStorageLocation
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DU_CACHE_FILE
specifier|private
specifier|static
specifier|final
name|String
name|DU_CACHE_FILE
init|=
literal|"scmUsed"
decl_stmt|;
DECL|field|scmUsedSaved
specifier|private
specifier|volatile
name|boolean
name|scmUsedSaved
init|=
literal|false
decl_stmt|;
DECL|field|dataLocation
specifier|private
specifier|final
name|StorageLocation
name|dataLocation
decl_stmt|;
DECL|field|storageUuId
specifier|private
specifier|final
name|String
name|storageUuId
decl_stmt|;
DECL|field|usage
specifier|private
specifier|final
name|DF
name|usage
decl_stmt|;
DECL|field|scmUsage
specifier|private
specifier|final
name|GetSpaceUsed
name|scmUsage
decl_stmt|;
DECL|field|scmUsedFile
specifier|private
specifier|final
name|File
name|scmUsedFile
decl_stmt|;
DECL|method|ContainerStorageLocation (StorageLocation dataLoc, Configuration conf)
specifier|public
name|ContainerStorageLocation
parameter_list|(
name|StorageLocation
name|dataLoc
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|dataLocation
operator|=
name|dataLoc
expr_stmt|;
name|this
operator|.
name|storageUuId
operator|=
name|DatanodeStorage
operator|.
name|generateUuid
argument_list|()
expr_stmt|;
name|File
name|dataDir
init|=
name|Paths
operator|.
name|get
argument_list|(
name|dataLoc
operator|.
name|getNormalizedUri
argument_list|()
argument_list|)
operator|.
name|resolve
argument_list|(
name|OzoneConsts
operator|.
name|CONTAINER_PREFIX
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
comment|// Initialize container data root if it does not exist as required by DF/DU
if|if
condition|(
operator|!
name|dataDir
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|dataDir
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to create the container storage location at : {}"
argument_list|,
name|dataDir
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to create the container"
operator|+
literal|" storage location at : "
operator|+
name|dataDir
argument_list|)
throw|;
block|}
block|}
name|scmUsedFile
operator|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
name|DU_CACHE_FILE
argument_list|)
expr_stmt|;
comment|// get overall disk usage
name|this
operator|.
name|usage
operator|=
operator|new
name|DF
argument_list|(
name|dataDir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// get SCM specific usage
name|this
operator|.
name|scmUsage
operator|=
operator|new
name|CachingGetSpaceUsed
operator|.
name|Builder
argument_list|()
operator|.
name|setPath
argument_list|(
name|dataDir
argument_list|)
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
operator|.
name|setInitialUsed
argument_list|(
name|loadScmUsed
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// Ensure scm usage is saved during shutdown.
name|ShutdownHookManager
operator|.
name|get
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
operator|!
name|scmUsedSaved
condition|)
block|{
name|saveScmUsed
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|,
name|SHUTDOWN_HOOK_PRIORITY
argument_list|)
expr_stmt|;
block|}
DECL|method|getNormalizedUri ()
specifier|public
name|URI
name|getNormalizedUri
parameter_list|()
block|{
return|return
name|dataLocation
operator|.
name|getNormalizedUri
argument_list|()
return|;
block|}
DECL|method|getStorageUuId ()
specifier|public
name|String
name|getStorageUuId
parameter_list|()
block|{
return|return
name|storageUuId
return|;
block|}
DECL|method|getCapacity ()
specifier|public
name|long
name|getCapacity
parameter_list|()
block|{
name|long
name|capacity
init|=
name|usage
operator|.
name|getCapacity
argument_list|()
decl_stmt|;
return|return
operator|(
name|capacity
operator|>
literal|0
operator|)
condition|?
name|capacity
else|:
literal|0
return|;
block|}
DECL|method|getAvailable ()
specifier|public
name|long
name|getAvailable
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|remaining
init|=
name|getCapacity
argument_list|()
operator|-
name|getScmUsed
argument_list|()
decl_stmt|;
name|long
name|available
init|=
name|usage
operator|.
name|getAvailable
argument_list|()
decl_stmt|;
if|if
condition|(
name|remaining
operator|>
name|available
condition|)
block|{
name|remaining
operator|=
name|available
expr_stmt|;
block|}
return|return
operator|(
name|remaining
operator|>
literal|0
operator|)
condition|?
name|remaining
else|:
literal|0
return|;
block|}
DECL|method|getScmUsed ()
specifier|public
name|long
name|getScmUsed
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|scmUsage
operator|.
name|getUsed
argument_list|()
return|;
block|}
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|saveScmUsed
argument_list|()
expr_stmt|;
name|scmUsedSaved
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|scmUsage
operator|instanceof
name|CachingGetSpaceUsed
condition|)
block|{
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
literal|null
argument_list|,
operator|(
operator|(
name|CachingGetSpaceUsed
operator|)
name|scmUsage
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Read in the cached DU value and return it if it is less than 600 seconds    * old (DU update interval). Slight imprecision of scmUsed is not critical    * and skipping DU can significantly shorten the startup time.    * If the cached value is not available or too old, -1 is returned.    */
DECL|method|loadScmUsed ()
name|long
name|loadScmUsed
parameter_list|()
block|{
name|long
name|cachedScmUsed
decl_stmt|;
name|long
name|mtime
decl_stmt|;
name|Scanner
name|sc
decl_stmt|;
try|try
block|{
name|sc
operator|=
operator|new
name|Scanner
argument_list|(
name|scmUsedFile
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
try|try
block|{
comment|// Get the recorded scmUsed from the file.
if|if
condition|(
name|sc
operator|.
name|hasNextLong
argument_list|()
condition|)
block|{
name|cachedScmUsed
operator|=
name|sc
operator|.
name|nextLong
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|// Get the recorded mtime from the file.
if|if
condition|(
name|sc
operator|.
name|hasNextLong
argument_list|()
condition|)
block|{
name|mtime
operator|=
name|sc
operator|.
name|nextLong
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|// Return the cached value if mtime is okay.
if|if
condition|(
name|mtime
operator|>
literal|0
operator|&&
operator|(
name|Time
operator|.
name|now
argument_list|()
operator|-
name|mtime
operator|<
literal|600000L
operator|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Cached ScmUsed found for {} : {} "
argument_list|,
name|dataLocation
argument_list|,
name|cachedScmUsed
argument_list|)
expr_stmt|;
return|return
name|cachedScmUsed
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
finally|finally
block|{
name|sc
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Write the current scmUsed to the cache file.    */
DECL|method|saveScmUsed ()
name|void
name|saveScmUsed
parameter_list|()
block|{
if|if
condition|(
name|scmUsedFile
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|scmUsedFile
operator|.
name|delete
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to delete old scmUsed file in {}."
argument_list|,
name|dataLocation
argument_list|)
expr_stmt|;
block|}
name|OutputStreamWriter
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|long
name|used
init|=
name|getScmUsed
argument_list|()
decl_stmt|;
if|if
condition|(
name|used
operator|>
literal|0
condition|)
block|{
name|out
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|scmUsedFile
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
comment|// mtime is written last, so that truncated writes won't be valid.
name|out
operator|.
name|write
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|used
argument_list|)
operator|+
literal|" "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// If write failed, the volume might be bad. Since the cache file is
comment|// not critical, log the error and continue.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to write scmUsed to "
operator|+
name|scmUsedFile
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
literal|null
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

