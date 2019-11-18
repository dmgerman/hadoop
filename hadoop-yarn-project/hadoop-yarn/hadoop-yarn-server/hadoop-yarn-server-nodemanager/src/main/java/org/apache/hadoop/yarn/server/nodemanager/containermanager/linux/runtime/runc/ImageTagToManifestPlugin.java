begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.runc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|linux
operator|.
name|runtime
operator|.
name|runc
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
name|FSDataInputStream
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
name|service
operator|.
name|AbstractService
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
name|concurrent
operator|.
name|HadoopExecutors
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|FileInputStream
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
name|InputStreamReader
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ScheduledExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
operator|.
name|DEFAULT_NM_RUNC_CACHE_REFRESH_INTERVAL
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
operator|.
name|DEFAULT_NM_RUNC_IMAGE_TOPLEVEL_DIR
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
operator|.
name|DEFAULT_NUM_MANIFESTS_TO_CACHE
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
operator|.
name|NM_HDFS_RUNC_IMAGE_TAG_TO_HASH_FILE
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
operator|.
name|NM_LOCAL_RUNC_IMAGE_TAG_TO_HASH_FILE
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
operator|.
name|NM_RUNC_CACHE_REFRESH_INTERVAL
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
operator|.
name|NM_RUNC_IMAGE_TOPLEVEL_DIR
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
operator|.
name|NM_RUNC_NUM_MANIFESTS_TO_CACHE
import|;
end_import

begin_comment
comment|/**  * This class is a plugin for the  * {@link org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.RuncContainerRuntime}  * to convert image tags into runC image manifests.  */
end_comment

begin_class
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|ImageTagToManifestPlugin
specifier|public
class|class
name|ImageTagToManifestPlugin
extends|extends
name|AbstractService
implements|implements
name|RuncImageTagToManifestPlugin
block|{
DECL|field|manifestCache
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ImageManifest
argument_list|>
name|manifestCache
decl_stmt|;
DECL|field|objMapper
specifier|private
name|ObjectMapper
name|objMapper
decl_stmt|;
DECL|field|localImageToHashCache
specifier|private
name|AtomicReference
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|localImageToHashCache
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|hdfsImageToHashCache
specifier|private
name|AtomicReference
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|hdfsImageToHashCache
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|exec
specifier|private
name|ScheduledExecutorService
name|exec
decl_stmt|;
DECL|field|hdfsModTime
specifier|private
name|long
name|hdfsModTime
decl_stmt|;
DECL|field|localModTime
specifier|private
name|long
name|localModTime
decl_stmt|;
DECL|field|hdfsImageToHashFile
specifier|private
name|String
name|hdfsImageToHashFile
decl_stmt|;
DECL|field|manifestDir
specifier|private
name|String
name|manifestDir
decl_stmt|;
DECL|field|localImageTagToHashFile
specifier|private
name|String
name|localImageTagToHashFile
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
name|ImageTagToManifestPlugin
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SHA256_HASH_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|SHA256_HASH_LENGTH
init|=
literal|64
decl_stmt|;
DECL|field|ALPHA_NUMERIC
specifier|private
specifier|static
specifier|final
name|String
name|ALPHA_NUMERIC
init|=
literal|"[a-zA-Z0-9]+"
decl_stmt|;
DECL|method|ImageTagToManifestPlugin ()
specifier|public
name|ImageTagToManifestPlugin
parameter_list|()
block|{
name|super
argument_list|(
literal|"ImageTagToManifestPluginService"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getManifestFromImageTag (String imageTag)
specifier|public
name|ImageManifest
name|getManifestFromImageTag
parameter_list|(
name|String
name|imageTag
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|hash
init|=
name|getHashFromImageTag
argument_list|(
name|imageTag
argument_list|)
decl_stmt|;
name|ImageManifest
name|manifest
init|=
name|manifestCache
operator|.
name|get
argument_list|(
name|hash
argument_list|)
decl_stmt|;
if|if
condition|(
name|manifest
operator|!=
literal|null
condition|)
block|{
return|return
name|manifest
return|;
block|}
name|Path
name|manifestPath
init|=
operator|new
name|Path
argument_list|(
name|manifestDir
operator|+
name|hash
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|manifestPath
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FSDataInputStream
name|input
decl_stmt|;
try|try
block|{
name|input
operator|=
name|fs
operator|.
name|open
argument_list|(
name|manifestPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Manifest file is not a valid HDFS file: "
operator|+
name|manifestPath
operator|.
name|toString
argument_list|()
argument_list|,
name|iae
argument_list|)
throw|;
block|}
name|byte
index|[]
name|bytes
init|=
name|IOUtils
operator|.
name|toByteArray
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|manifest
operator|=
name|objMapper
operator|.
name|readValue
argument_list|(
name|bytes
argument_list|,
name|ImageManifest
operator|.
name|class
argument_list|)
expr_stmt|;
name|manifestCache
operator|.
name|put
argument_list|(
name|hash
argument_list|,
name|manifest
argument_list|)
expr_stmt|;
return|return
name|manifest
return|;
block|}
annotation|@
name|Override
DECL|method|getHashFromImageTag (String imageTag)
specifier|public
name|String
name|getHashFromImageTag
parameter_list|(
name|String
name|imageTag
parameter_list|)
block|{
name|String
name|hash
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|localImageToHashCacheMap
init|=
name|localImageToHashCache
operator|.
name|get
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|hdfsImageToHashCacheMap
init|=
name|hdfsImageToHashCache
operator|.
name|get
argument_list|()
decl_stmt|;
comment|// 1) Go to local file
comment|// 2) Go to HDFS
comment|// 3) Use tag as is/Assume tag is the hash
name|hash
operator|=
name|localImageToHashCacheMap
operator|.
name|get
argument_list|(
name|imageTag
argument_list|)
expr_stmt|;
if|if
condition|(
name|hash
operator|==
literal|null
condition|)
block|{
name|hash
operator|=
name|hdfsImageToHashCacheMap
operator|.
name|get
argument_list|(
name|imageTag
argument_list|)
expr_stmt|;
if|if
condition|(
name|hash
operator|==
literal|null
condition|)
block|{
name|hash
operator|=
name|imageTag
expr_stmt|;
block|}
block|}
return|return
name|hash
return|;
block|}
DECL|method|getLocalImageToHashReader ()
specifier|protected
name|BufferedReader
name|getLocalImageToHashReader
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|localImageTagToHashFile
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Did not load local image to hash file, "
operator|+
literal|"file is null"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|File
name|imageTagToHashFile
init|=
operator|new
name|File
argument_list|(
name|localImageTagToHashFile
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|imageTagToHashFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Did not load local image to hash file, "
operator|+
literal|"file doesn't exist"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|long
name|newLocalModTime
init|=
name|imageTagToHashFile
operator|.
name|lastModified
argument_list|()
decl_stmt|;
if|if
condition|(
name|newLocalModTime
operator|==
name|localModTime
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Did not load local image to hash file, "
operator|+
literal|"file is unmodified"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|localModTime
operator|=
name|newLocalModTime
expr_stmt|;
return|return
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|imageTagToHashFile
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getHdfsImageToHashReader ()
specifier|protected
name|BufferedReader
name|getHdfsImageToHashReader
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|hdfsImageToHashFile
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Did not load hdfs image to hash file, "
operator|+
literal|"file is null"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|Path
name|imageToHash
init|=
operator|new
name|Path
argument_list|(
name|hdfsImageToHashFile
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|imageToHash
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|imageToHash
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Did not load hdfs image to hash file, "
operator|+
literal|"file doesn't exist"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|long
name|newHdfsModTime
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|imageToHash
argument_list|)
operator|.
name|getModificationTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|newHdfsModTime
operator|==
name|hdfsModTime
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Did not load hdfs image to hash file, "
operator|+
literal|"file is unmodified"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|hdfsModTime
operator|=
name|newHdfsModTime
expr_stmt|;
return|return
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|imageToHash
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
return|;
block|}
comment|/** You may specify multiple tags per hash all on the same line.    * Comments are allowed using #. Anything after this character will not    * be read    * Example file:    * foo/bar:current,fizz/gig:latest:123456789    * #this/line:wont,be:parsed:2378590895     * This will map both foo/bar:current and fizz/gig:latest to 123456789    */
DECL|method|readImageToHashFile ( BufferedReader br)
specifier|protected
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|readImageToHashFile
parameter_list|(
name|BufferedReader
name|br
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|br
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|line
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|imageToHashCache
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|int
name|index
decl_stmt|;
name|index
operator|=
name|line
operator|.
name|indexOf
argument_list|(
literal|"#"
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
elseif|else
if|if
condition|(
name|index
operator|!=
operator|-
literal|1
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
name|index
operator|=
name|line
operator|.
name|lastIndexOf
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|==
operator|-
literal|1
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Malformed imageTagToManifest entry: "
operator|+
name|line
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|String
name|imageTags
init|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
decl_stmt|;
name|String
index|[]
name|imageTagArray
init|=
name|imageTags
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|String
name|hash
init|=
name|line
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|hash
operator|.
name|matches
argument_list|(
name|ALPHA_NUMERIC
argument_list|)
operator|||
name|hash
operator|.
name|length
argument_list|()
operator|!=
name|SHA256_HASH_LENGTH
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Malformed image hash: "
operator|+
name|hash
argument_list|)
expr_stmt|;
continue|continue;
block|}
for|for
control|(
name|String
name|imageTag
range|:
name|imageTagArray
control|)
block|{
name|imageToHashCache
operator|.
name|put
argument_list|(
name|imageTag
argument_list|,
name|hash
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|imageToHashCache
return|;
block|}
DECL|method|loadImageToHashFiles ()
specifier|public
name|boolean
name|loadImageToHashFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|ret
init|=
literal|false
decl_stmt|;
try|try
init|(
name|BufferedReader
name|localBr
init|=
name|getLocalImageToHashReader
argument_list|()
init|;
name|BufferedReader
name|hdfsBr
operator|=
name|getHdfsImageToHashReader
argument_list|()
init|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|localImageToHash
init|=
name|readImageToHashFile
argument_list|(
name|localBr
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|hdfsImageToHash
init|=
name|readImageToHashFile
argument_list|(
name|hdfsBr
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tmpLocalImageToHash
init|=
name|localImageToHashCache
operator|.
name|get
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tmpHdfsImageToHash
init|=
name|hdfsImageToHashCache
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|localImageToHash
operator|!=
literal|null
operator|&&
operator|!
name|localImageToHash
operator|.
name|equals
argument_list|(
name|tmpLocalImageToHash
argument_list|)
condition|)
block|{
name|localImageToHashCache
operator|.
name|set
argument_list|(
name|localImageToHash
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Reloaded local image tag to hash cache"
argument_list|)
expr_stmt|;
name|ret
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|hdfsImageToHash
operator|!=
literal|null
operator|&&
operator|!
name|hdfsImageToHash
operator|.
name|equals
argument_list|(
name|tmpHdfsImageToHash
argument_list|)
condition|)
block|{
name|hdfsImageToHashCache
operator|.
name|set
argument_list|(
name|hdfsImageToHash
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Reloaded hdfs image tag to hash cache"
argument_list|)
expr_stmt|;
name|ret
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration configuration)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceInit
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|configuration
expr_stmt|;
name|localImageTagToHashFile
operator|=
name|conf
operator|.
name|get
argument_list|(
name|NM_LOCAL_RUNC_IMAGE_TAG_TO_HASH_FILE
argument_list|)
expr_stmt|;
if|if
condition|(
name|localImageTagToHashFile
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failed to load local runC image to hash file. "
operator|+
literal|"Config not set"
argument_list|)
expr_stmt|;
block|}
name|hdfsImageToHashFile
operator|=
name|conf
operator|.
name|get
argument_list|(
name|NM_HDFS_RUNC_IMAGE_TAG_TO_HASH_FILE
argument_list|)
expr_stmt|;
if|if
condition|(
name|hdfsImageToHashFile
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failed to load HDFS runC image to hash file. Config not set"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hdfsImageToHashFile
operator|==
literal|null
operator|&&
name|localImageTagToHashFile
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No valid image-tag-to-hash files"
argument_list|)
expr_stmt|;
block|}
name|manifestDir
operator|=
name|conf
operator|.
name|get
argument_list|(
name|NM_RUNC_IMAGE_TOPLEVEL_DIR
argument_list|,
name|DEFAULT_NM_RUNC_IMAGE_TOPLEVEL_DIR
argument_list|)
operator|+
literal|"/manifests/"
expr_stmt|;
name|int
name|numManifestsToCache
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|NM_RUNC_NUM_MANIFESTS_TO_CACHE
argument_list|,
name|DEFAULT_NUM_MANIFESTS_TO_CACHE
argument_list|)
decl_stmt|;
name|this
operator|.
name|objMapper
operator|=
operator|new
name|ObjectMapper
argument_list|()
expr_stmt|;
name|this
operator|.
name|manifestCache
operator|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|LRUCache
argument_list|(
name|numManifestsToCache
argument_list|,
literal|0.75f
argument_list|)
argument_list|)
expr_stmt|;
name|exec
operator|=
name|HadoopExecutors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|loadImageToHashFiles
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Couldn't load any image-tag-to-hash-files"
argument_list|)
expr_stmt|;
block|}
name|int
name|runcCacheRefreshInterval
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|NM_RUNC_CACHE_REFRESH_INTERVAL
argument_list|,
name|DEFAULT_NM_RUNC_CACHE_REFRESH_INTERVAL
argument_list|)
decl_stmt|;
name|exec
operator|=
name|HadoopExecutors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|exec
operator|.
name|scheduleWithFixedDelay
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
try|try
block|{
name|loadImageToHashFiles
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"runC cache refresh thread caught an exception: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|,
name|runcCacheRefreshInterval
argument_list|,
name|runcCacheRefreshInterval
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
name|exec
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
DECL|class|LRUCache
specifier|private
specifier|static
class|class
name|LRUCache
extends|extends
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|ImageManifest
argument_list|>
block|{
DECL|field|cacheSize
specifier|private
name|int
name|cacheSize
decl_stmt|;
DECL|method|LRUCache (int initialCapacity, float loadFactor)
name|LRUCache
parameter_list|(
name|int
name|initialCapacity
parameter_list|,
name|float
name|loadFactor
parameter_list|)
block|{
name|super
argument_list|(
name|initialCapacity
argument_list|,
name|loadFactor
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|cacheSize
operator|=
name|initialCapacity
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeEldestEntry ( Map.Entry<String, ImageManifest> eldest)
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ImageManifest
argument_list|>
name|eldest
parameter_list|)
block|{
return|return
name|this
operator|.
name|size
argument_list|()
operator|>
name|cacheSize
return|;
block|}
block|}
block|}
end_class

end_unit

