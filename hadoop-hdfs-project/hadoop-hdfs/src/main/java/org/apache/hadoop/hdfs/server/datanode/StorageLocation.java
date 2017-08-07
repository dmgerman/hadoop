begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
package|package
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|regex
operator|.
name|Matcher
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
name|LocalFileSystem
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
name|fs
operator|.
name|StorageType
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
name|DFSConfigKeys
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
name|HdfsConfiguration
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
name|common
operator|.
name|Storage
operator|.
name|StorageDirectory
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
name|common
operator|.
name|Storage
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
name|checker
operator|.
name|Checkable
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
name|checker
operator|.
name|VolumeCheckResult
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
comment|/**  * Encapsulates the URI and storage medium that together describe a  * storage directory.  * The default storage medium is assumed to be DISK, if none is specified.  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StorageLocation
specifier|public
class|class
name|StorageLocation
implements|implements
name|Checkable
argument_list|<
name|StorageLocation
operator|.
name|CheckContext
argument_list|,
name|VolumeCheckResult
argument_list|>
implements|,
name|Comparable
argument_list|<
name|StorageLocation
argument_list|>
block|{
DECL|field|storageType
specifier|private
specifier|final
name|StorageType
name|storageType
decl_stmt|;
DECL|field|baseURI
specifier|private
specifier|final
name|URI
name|baseURI
decl_stmt|;
comment|/** Regular expression that describes a storage uri with a storage type.    *  e.g. [Disk]/storages/storage1/    */
DECL|field|regex
specifier|private
specifier|static
specifier|final
name|Pattern
name|regex
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\[(\\w*)\\](.+)$"
argument_list|)
decl_stmt|;
DECL|method|StorageLocation (StorageType storageType, URI uri)
specifier|private
name|StorageLocation
parameter_list|(
name|StorageType
name|storageType
parameter_list|,
name|URI
name|uri
parameter_list|)
block|{
name|this
operator|.
name|storageType
operator|=
name|storageType
expr_stmt|;
if|if
condition|(
name|uri
operator|.
name|getScheme
argument_list|()
operator|==
literal|null
operator|||
name|uri
operator|.
name|getScheme
argument_list|()
operator|.
name|equals
argument_list|(
literal|"file"
argument_list|)
condition|)
block|{
comment|// make sure all URIs that point to a file have the same scheme
name|uri
operator|=
name|normalizeFileURI
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
name|baseURI
operator|=
name|uri
expr_stmt|;
block|}
DECL|method|normalizeFileURI (URI uri)
specifier|public
specifier|static
name|URI
name|normalizeFileURI
parameter_list|(
name|URI
name|uri
parameter_list|)
block|{
try|try
block|{
name|File
name|uriFile
init|=
operator|new
name|File
argument_list|(
name|uri
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|uriStr
init|=
name|uriFile
operator|.
name|toURI
argument_list|()
operator|.
name|normalize
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|uriStr
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|uriStr
operator|=
name|uriStr
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|uriStr
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|URI
argument_list|(
name|uriStr
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"URI: "
operator|+
name|uri
operator|+
literal|" is not in the expected format"
argument_list|)
throw|;
block|}
block|}
DECL|method|getStorageType ()
specifier|public
name|StorageType
name|getStorageType
parameter_list|()
block|{
return|return
name|this
operator|.
name|storageType
return|;
block|}
DECL|method|getUri ()
specifier|public
name|URI
name|getUri
parameter_list|()
block|{
return|return
name|baseURI
return|;
block|}
DECL|method|getNormalizedUri ()
specifier|public
name|URI
name|getNormalizedUri
parameter_list|()
block|{
return|return
name|baseURI
operator|.
name|normalize
argument_list|()
return|;
block|}
DECL|method|matchesStorageDirectory (StorageDirectory sd)
specifier|public
name|boolean
name|matchesStorageDirectory
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|equals
argument_list|(
name|sd
operator|.
name|getStorageLocation
argument_list|()
argument_list|)
return|;
block|}
DECL|method|matchesStorageDirectory (StorageDirectory sd, String bpid)
specifier|public
name|boolean
name|matchesStorageDirectory
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|,
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|sd
operator|.
name|getStorageLocation
argument_list|()
operator|.
name|getStorageType
argument_list|()
operator|==
name|StorageType
operator|.
name|PROVIDED
operator|&&
name|storageType
operator|==
name|StorageType
operator|.
name|PROVIDED
condition|)
block|{
return|return
name|matchesStorageDirectory
argument_list|(
name|sd
argument_list|)
return|;
block|}
if|if
condition|(
name|sd
operator|.
name|getStorageLocation
argument_list|()
operator|.
name|getStorageType
argument_list|()
operator|==
name|StorageType
operator|.
name|PROVIDED
operator|||
name|storageType
operator|==
name|StorageType
operator|.
name|PROVIDED
condition|)
block|{
comment|//only one of these is PROVIDED; so it cannot be a match!
return|return
literal|false
return|;
block|}
comment|//both storage directories are local
return|return
name|this
operator|.
name|getBpURI
argument_list|(
name|bpid
argument_list|,
name|Storage
operator|.
name|STORAGE_DIR_CURRENT
argument_list|)
operator|.
name|normalize
argument_list|()
operator|.
name|equals
argument_list|(
name|sd
operator|.
name|getRoot
argument_list|()
operator|.
name|toURI
argument_list|()
operator|.
name|normalize
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Attempt to parse a storage uri with storage class and URI. The storage    * class component of the uri is case-insensitive.    *    * @param rawLocation Location string of the format [type]uri, where [type] is    *                    optional.    * @return A StorageLocation object if successfully parsed, null otherwise.    *         Does not throw any exceptions.    */
DECL|method|parse (String rawLocation)
specifier|public
specifier|static
name|StorageLocation
name|parse
parameter_list|(
name|String
name|rawLocation
parameter_list|)
throws|throws
name|IOException
throws|,
name|SecurityException
block|{
name|Matcher
name|matcher
init|=
name|regex
operator|.
name|matcher
argument_list|(
name|rawLocation
argument_list|)
decl_stmt|;
name|StorageType
name|storageType
init|=
name|StorageType
operator|.
name|DEFAULT
decl_stmt|;
name|String
name|location
init|=
name|rawLocation
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|String
name|classString
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|location
operator|=
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|classString
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|storageType
operator|=
name|StorageType
operator|.
name|valueOf
argument_list|(
name|StringUtils
operator|.
name|toUpperCase
argument_list|(
name|classString
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|//do Path.toURI instead of new URI(location) as this ensures that
comment|//"/a/b" and "/a/b/" are represented in a consistent manner
return|return
operator|new
name|StorageLocation
argument_list|(
name|storageType
argument_list|,
operator|new
name|Path
argument_list|(
name|location
argument_list|)
operator|.
name|toUri
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|storageType
operator|+
literal|"]"
operator|+
name|baseURI
operator|.
name|normalize
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
operator|!
operator|(
name|obj
operator|instanceof
name|StorageLocation
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|comp
init|=
name|compareTo
argument_list|(
operator|(
name|StorageLocation
operator|)
name|obj
argument_list|)
decl_stmt|;
return|return
name|comp
operator|==
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|toString
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (StorageLocation obj)
specifier|public
name|int
name|compareTo
parameter_list|(
name|StorageLocation
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
name|this
condition|)
block|{
return|return
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|StorageLocation
name|otherStorage
init|=
operator|(
name|StorageLocation
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|getNormalizedUri
argument_list|()
operator|!=
literal|null
operator|&&
name|otherStorage
operator|.
name|getNormalizedUri
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|getNormalizedUri
argument_list|()
operator|.
name|compareTo
argument_list|(
name|otherStorage
operator|.
name|getNormalizedUri
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|getNormalizedUri
argument_list|()
operator|==
literal|null
operator|&&
name|otherStorage
operator|.
name|getNormalizedUri
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|storageType
operator|.
name|compareTo
argument_list|(
name|otherStorage
operator|.
name|getStorageType
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|getNormalizedUri
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|1
return|;
block|}
block|}
DECL|method|getBpURI (String bpid, String currentStorageDir)
specifier|public
name|URI
name|getBpURI
parameter_list|(
name|String
name|bpid
parameter_list|,
name|String
name|currentStorageDir
parameter_list|)
block|{
try|try
block|{
name|File
name|localFile
init|=
operator|new
name|File
argument_list|(
name|getUri
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|localFile
argument_list|,
name|currentStorageDir
argument_list|)
argument_list|,
name|bpid
argument_list|)
operator|.
name|toURI
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Create physical directory for block pools on the data node.    *    * @param blockPoolID    *          the block pool id    * @param conf    *          Configuration instance to use.    * @throws IOException on errors    */
DECL|method|makeBlockPoolDir (String blockPoolID, Configuration conf)
specifier|public
name|void
name|makeBlockPoolDir
parameter_list|(
name|String
name|blockPoolID
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|storageType
operator|==
name|StorageType
operator|.
name|PROVIDED
condition|)
block|{
comment|//skip creation if the storage type is PROVIDED
return|return;
block|}
name|LocalFileSystem
name|localFS
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FsPermission
name|permission
init|=
operator|new
name|FsPermission
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_PERMISSION_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_PERMISSION_DEFAULT
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|data
init|=
operator|new
name|File
argument_list|(
name|getBpURI
argument_list|(
name|blockPoolID
argument_list|,
name|Storage
operator|.
name|STORAGE_DIR_CURRENT
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|DiskChecker
operator|.
name|checkDir
argument_list|(
name|localFS
argument_list|,
operator|new
name|Path
argument_list|(
name|data
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|,
name|permission
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|DataStorage
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid directory in: "
operator|+
name|data
operator|.
name|getCanonicalPath
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
comment|// Checkable
DECL|method|check (CheckContext context)
specifier|public
name|VolumeCheckResult
name|check
parameter_list|(
name|CheckContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|//we assume provided storage locations are always healthy,
comment|//and check only for local storages.
if|if
condition|(
name|storageType
operator|!=
name|StorageType
operator|.
name|PROVIDED
condition|)
block|{
name|DiskChecker
operator|.
name|checkDir
argument_list|(
name|context
operator|.
name|localFileSystem
argument_list|,
operator|new
name|Path
argument_list|(
name|baseURI
argument_list|)
argument_list|,
name|context
operator|.
name|expectedPermission
argument_list|)
expr_stmt|;
block|}
return|return
name|VolumeCheckResult
operator|.
name|HEALTHY
return|;
block|}
comment|/**    * Class to hold the parameters for running a {@link #check}.    */
DECL|class|CheckContext
specifier|public
specifier|static
specifier|final
class|class
name|CheckContext
block|{
DECL|field|localFileSystem
specifier|private
specifier|final
name|LocalFileSystem
name|localFileSystem
decl_stmt|;
DECL|field|expectedPermission
specifier|private
specifier|final
name|FsPermission
name|expectedPermission
decl_stmt|;
DECL|method|CheckContext (LocalFileSystem localFileSystem, FsPermission expectedPermission)
specifier|public
name|CheckContext
parameter_list|(
name|LocalFileSystem
name|localFileSystem
parameter_list|,
name|FsPermission
name|expectedPermission
parameter_list|)
block|{
name|this
operator|.
name|localFileSystem
operator|=
name|localFileSystem
expr_stmt|;
name|this
operator|.
name|expectedPermission
operator|=
name|expectedPermission
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

