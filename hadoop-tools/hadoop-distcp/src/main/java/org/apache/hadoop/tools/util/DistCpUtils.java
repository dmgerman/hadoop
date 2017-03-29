begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|util
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|FileChecksum
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
name|XAttr
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
name|AclEntry
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
name|AclUtil
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
name|io
operator|.
name|Text
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
name|tools
operator|.
name|CopyListing
operator|.
name|AclsNotSupportedException
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
name|CopyListing
operator|.
name|XAttrsNotSupportedException
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
name|CopyListingFileStatus
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
name|DistCpOptions
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
name|DistCpOptions
operator|.
name|FileAttribute
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
name|mapred
operator|.
name|UniformSizeInputFormat
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
name|DecimalFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_comment
comment|/**  * Utility functions used in DistCp.  */
end_comment

begin_class
DECL|class|DistCpUtils
specifier|public
class|class
name|DistCpUtils
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
name|DistCpUtils
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Retrieves size of the file at the specified path.    * @param path The path of the file whose size is sought.    * @param configuration Configuration, to retrieve the appropriate FileSystem.    * @return The file-size, in number of bytes.    * @throws IOException    */
DECL|method|getFileSize (Path path, Configuration configuration)
specifier|public
specifier|static
name|long
name|getFileSize
parameter_list|(
name|Path
name|path
parameter_list|,
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Retrieving file size for: "
operator|+
name|path
argument_list|)
expr_stmt|;
return|return
name|path
operator|.
name|getFileSystem
argument_list|(
name|configuration
argument_list|)
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
operator|.
name|getLen
argument_list|()
return|;
block|}
comment|/**    * Utility to publish a value to a configuration.    * @param configuration The Configuration to which the value must be written.    * @param label The label for the value being published.    * @param value The value being published.    * @param<T> The type of the value.    */
DECL|method|publish (Configuration configuration, String label, T value)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|publish
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|String
name|label
parameter_list|,
name|T
name|value
parameter_list|)
block|{
name|configuration
operator|.
name|set
argument_list|(
name|label
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Utility to retrieve a specified key from a Configuration. Throw exception    * if not found.    * @param configuration The Configuration in which the key is sought.    * @param label The key being sought.    * @return Integer value of the key.    */
DECL|method|getInt (Configuration configuration, String label)
specifier|public
specifier|static
name|int
name|getInt
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|String
name|label
parameter_list|)
block|{
name|int
name|value
init|=
name|configuration
operator|.
name|getInt
argument_list|(
name|label
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
assert|assert
name|value
operator|>=
literal|0
operator|:
literal|"Couldn't find "
operator|+
name|label
assert|;
return|return
name|value
return|;
block|}
comment|/**    * Utility to retrieve a specified key from a Configuration. Throw exception    * if not found.    * @param configuration The Configuration in which the key is sought.    * @param label The key being sought.    * @return Long value of the key.    */
DECL|method|getLong (Configuration configuration, String label)
specifier|public
specifier|static
name|long
name|getLong
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|String
name|label
parameter_list|)
block|{
name|long
name|value
init|=
name|configuration
operator|.
name|getLong
argument_list|(
name|label
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
assert|assert
name|value
operator|>=
literal|0
operator|:
literal|"Couldn't find "
operator|+
name|label
assert|;
return|return
name|value
return|;
block|}
comment|/**    * Returns the class that implements a copy strategy. Looks up the implementation for    * a particular strategy from distcp-default.xml    *    * @param conf - Configuration object    * @param options - Handle to input options    * @return Class implementing the strategy specified in options.    */
DECL|method|getStrategy (Configuration conf, DistCpOptions options)
specifier|public
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|InputFormat
argument_list|>
name|getStrategy
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|DistCpOptions
name|options
parameter_list|)
block|{
name|String
name|confLabel
init|=
literal|"distcp."
operator|+
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|options
operator|.
name|getCopyStrategy
argument_list|()
argument_list|)
operator|+
literal|".strategy"
operator|+
literal|".impl"
decl_stmt|;
return|return
name|conf
operator|.
name|getClass
argument_list|(
name|confLabel
argument_list|,
name|UniformSizeInputFormat
operator|.
name|class
argument_list|,
name|InputFormat
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**    * Gets relative path of child path with respect to a root path    * For ex. If childPath = /tmp/abc/xyz/file and    *            sourceRootPath = /tmp/abc    * Relative path would be /xyz/file    *         If childPath = /file and    *            sourceRootPath = /    * Relative path would be /file    * @param sourceRootPath - Source root path    * @param childPath - Path for which relative path is required    * @return - Relative portion of the child path (always prefixed with /    *           unless it is empty    */
DECL|method|getRelativePath (Path sourceRootPath, Path childPath)
specifier|public
specifier|static
name|String
name|getRelativePath
parameter_list|(
name|Path
name|sourceRootPath
parameter_list|,
name|Path
name|childPath
parameter_list|)
block|{
name|String
name|childPathString
init|=
name|childPath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|sourceRootPathString
init|=
name|sourceRootPath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
return|return
name|sourceRootPathString
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|?
name|childPathString
else|:
name|childPathString
operator|.
name|substring
argument_list|(
name|sourceRootPathString
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Pack file preservation attributes into a string, containing    * just the first character of each preservation attribute    * @param attributes - Attribute set to preserve    * @return - String containing first letters of each attribute to preserve    */
DECL|method|packAttributes (EnumSet<FileAttribute> attributes)
specifier|public
specifier|static
name|String
name|packAttributes
parameter_list|(
name|EnumSet
argument_list|<
name|FileAttribute
argument_list|>
name|attributes
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|(
name|FileAttribute
operator|.
name|values
argument_list|()
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FileAttribute
name|attribute
range|:
name|attributes
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|attribute
operator|.
name|name
argument_list|()
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|len
operator|++
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|len
argument_list|)
return|;
block|}
comment|/**    * Unpacks preservation attribute string containing the first character of    * each preservation attribute back to a set of attributes to preserve    * @param attributes - Attribute string    * @return - Attribute set    */
DECL|method|unpackAttributes (String attributes)
specifier|public
specifier|static
name|EnumSet
argument_list|<
name|FileAttribute
argument_list|>
name|unpackAttributes
parameter_list|(
name|String
name|attributes
parameter_list|)
block|{
name|EnumSet
argument_list|<
name|FileAttribute
argument_list|>
name|retValue
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|FileAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|attributes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|attributes
operator|.
name|length
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|retValue
operator|.
name|add
argument_list|(
name|FileAttribute
operator|.
name|getAttribute
argument_list|(
name|attributes
operator|.
name|charAt
argument_list|(
name|index
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|retValue
return|;
block|}
comment|/**    * Preserve attribute on file matching that of the file status being sent    * as argument. Barring the block size, all the other attributes are preserved    * by this function    *    * @param targetFS - File system    * @param path - Path that needs to preserve original file status    * @param srcFileStatus - Original file status    * @param attributes - Attribute set that needs to be preserved    * @param preserveRawXattrs if true, raw.* xattrs should be preserved    * @throws IOException - Exception if any (particularly relating to group/owner    *                       change or any transient error)    */
DECL|method|preserve (FileSystem targetFS, Path path, CopyListingFileStatus srcFileStatus, EnumSet<FileAttribute> attributes, boolean preserveRawXattrs)
specifier|public
specifier|static
name|void
name|preserve
parameter_list|(
name|FileSystem
name|targetFS
parameter_list|,
name|Path
name|path
parameter_list|,
name|CopyListingFileStatus
name|srcFileStatus
parameter_list|,
name|EnumSet
argument_list|<
name|FileAttribute
argument_list|>
name|attributes
parameter_list|,
name|boolean
name|preserveRawXattrs
parameter_list|)
throws|throws
name|IOException
block|{
comment|// If not preserving anything from FileStatus, don't bother fetching it.
name|FileStatus
name|targetFileStatus
init|=
name|attributes
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|targetFS
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|group
init|=
name|targetFileStatus
operator|==
literal|null
condition|?
literal|null
else|:
name|targetFileStatus
operator|.
name|getGroup
argument_list|()
decl_stmt|;
name|String
name|user
init|=
name|targetFileStatus
operator|==
literal|null
condition|?
literal|null
else|:
name|targetFileStatus
operator|.
name|getOwner
argument_list|()
decl_stmt|;
name|boolean
name|chown
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|attributes
operator|.
name|contains
argument_list|(
name|FileAttribute
operator|.
name|ACL
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|AclEntry
argument_list|>
name|srcAcl
init|=
name|srcFileStatus
operator|.
name|getAclEntries
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|targetAcl
init|=
name|getAcl
argument_list|(
name|targetFS
argument_list|,
name|targetFileStatus
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|srcAcl
operator|.
name|equals
argument_list|(
name|targetAcl
argument_list|)
condition|)
block|{
name|targetFS
operator|.
name|setAcl
argument_list|(
name|path
argument_list|,
name|srcAcl
argument_list|)
expr_stmt|;
block|}
comment|// setAcl doesn't preserve sticky bit, so also call setPermission if needed.
if|if
condition|(
name|srcFileStatus
operator|.
name|getPermission
argument_list|()
operator|.
name|getStickyBit
argument_list|()
operator|!=
name|targetFileStatus
operator|.
name|getPermission
argument_list|()
operator|.
name|getStickyBit
argument_list|()
condition|)
block|{
name|targetFS
operator|.
name|setPermission
argument_list|(
name|path
argument_list|,
name|srcFileStatus
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|attributes
operator|.
name|contains
argument_list|(
name|FileAttribute
operator|.
name|PERMISSION
argument_list|)
operator|&&
operator|!
name|srcFileStatus
operator|.
name|getPermission
argument_list|()
operator|.
name|equals
argument_list|(
name|targetFileStatus
operator|.
name|getPermission
argument_list|()
argument_list|)
condition|)
block|{
name|targetFS
operator|.
name|setPermission
argument_list|(
name|path
argument_list|,
name|srcFileStatus
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|boolean
name|preserveXAttrs
init|=
name|attributes
operator|.
name|contains
argument_list|(
name|FileAttribute
operator|.
name|XATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|preserveXAttrs
operator|||
name|preserveRawXattrs
condition|)
block|{
specifier|final
name|String
name|rawNS
init|=
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|XAttr
operator|.
name|NameSpace
operator|.
name|RAW
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|srcXAttrs
init|=
name|srcFileStatus
operator|.
name|getXAttrs
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|targetXAttrs
init|=
name|getXAttrs
argument_list|(
name|targetFS
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcXAttrs
operator|!=
literal|null
operator|&&
operator|!
name|srcXAttrs
operator|.
name|equals
argument_list|(
name|targetXAttrs
argument_list|)
condition|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
range|:
name|srcXAttrs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|xattrName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|xattrName
operator|.
name|startsWith
argument_list|(
name|rawNS
argument_list|)
operator|||
name|preserveXAttrs
condition|)
block|{
name|targetFS
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|xattrName
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// The replication factor can only be preserved for replicated files.
comment|// It is ignored when either the source or target file are erasure coded.
if|if
condition|(
name|attributes
operator|.
name|contains
argument_list|(
name|FileAttribute
operator|.
name|REPLICATION
argument_list|)
operator|&&
operator|!
name|targetFileStatus
operator|.
name|isDirectory
argument_list|()
operator|&&
operator|!
name|targetFileStatus
operator|.
name|isErasureCoded
argument_list|()
operator|&&
operator|!
name|srcFileStatus
operator|.
name|isErasureCoded
argument_list|()
operator|&&
name|srcFileStatus
operator|.
name|getReplication
argument_list|()
operator|!=
name|targetFileStatus
operator|.
name|getReplication
argument_list|()
condition|)
block|{
name|targetFS
operator|.
name|setReplication
argument_list|(
name|path
argument_list|,
name|srcFileStatus
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|attributes
operator|.
name|contains
argument_list|(
name|FileAttribute
operator|.
name|GROUP
argument_list|)
operator|&&
operator|!
name|group
operator|.
name|equals
argument_list|(
name|srcFileStatus
operator|.
name|getGroup
argument_list|()
argument_list|)
condition|)
block|{
name|group
operator|=
name|srcFileStatus
operator|.
name|getGroup
argument_list|()
expr_stmt|;
name|chown
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|attributes
operator|.
name|contains
argument_list|(
name|FileAttribute
operator|.
name|USER
argument_list|)
operator|&&
operator|!
name|user
operator|.
name|equals
argument_list|(
name|srcFileStatus
operator|.
name|getOwner
argument_list|()
argument_list|)
condition|)
block|{
name|user
operator|=
name|srcFileStatus
operator|.
name|getOwner
argument_list|()
expr_stmt|;
name|chown
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|chown
condition|)
block|{
name|targetFS
operator|.
name|setOwner
argument_list|(
name|path
argument_list|,
name|user
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|attributes
operator|.
name|contains
argument_list|(
name|FileAttribute
operator|.
name|TIMES
argument_list|)
condition|)
block|{
name|targetFS
operator|.
name|setTimes
argument_list|(
name|path
argument_list|,
name|srcFileStatus
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|srcFileStatus
operator|.
name|getAccessTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns a file's full logical ACL.    *    * @param fileSystem FileSystem containing the file    * @param fileStatus FileStatus of file    * @return List containing full logical ACL    * @throws IOException if there is an I/O error    */
DECL|method|getAcl (FileSystem fileSystem, FileStatus fileStatus)
specifier|public
specifier|static
name|List
argument_list|<
name|AclEntry
argument_list|>
name|getAcl
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|,
name|FileStatus
name|fileStatus
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|AclEntry
argument_list|>
name|entries
init|=
name|fileSystem
operator|.
name|getAclStatus
argument_list|(
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getEntries
argument_list|()
decl_stmt|;
return|return
name|AclUtil
operator|.
name|getAclFromPermAndEntries
argument_list|(
name|fileStatus
operator|.
name|getPermission
argument_list|()
argument_list|,
name|entries
argument_list|)
return|;
block|}
comment|/**    * Returns a file's all xAttrs.    *     * @param fileSystem FileSystem containing the file    * @param path file path    * @return Map containing all xAttrs    * @throws IOException if there is an I/O error    */
DECL|method|getXAttrs (FileSystem fileSystem, Path path)
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|getXAttrs
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fileSystem
operator|.
name|getXAttrs
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**    * Converts a FileStatus to a CopyListingFileStatus.  If preserving ACLs,    * populates the CopyListingFileStatus with the ACLs. If preserving XAttrs,    * populates the CopyListingFileStatus with the XAttrs.    *    * @param fileSystem FileSystem containing the file    * @param fileStatus FileStatus of file    * @param preserveAcls boolean true if preserving ACLs    * @param preserveXAttrs boolean true if preserving XAttrs    * @param preserveRawXAttrs boolean true if preserving raw.* XAttrs    * @throws IOException if there is an I/O error    */
DECL|method|toCopyListingFileStatus ( FileSystem fileSystem, FileStatus fileStatus, boolean preserveAcls, boolean preserveXAttrs, boolean preserveRawXAttrs)
specifier|public
specifier|static
name|CopyListingFileStatus
name|toCopyListingFileStatus
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|,
name|FileStatus
name|fileStatus
parameter_list|,
name|boolean
name|preserveAcls
parameter_list|,
name|boolean
name|preserveXAttrs
parameter_list|,
name|boolean
name|preserveRawXAttrs
parameter_list|)
throws|throws
name|IOException
block|{
name|CopyListingFileStatus
name|copyListingFileStatus
init|=
operator|new
name|CopyListingFileStatus
argument_list|(
name|fileStatus
argument_list|)
decl_stmt|;
if|if
condition|(
name|preserveAcls
condition|)
block|{
name|FsPermission
name|perm
init|=
name|fileStatus
operator|.
name|getPermission
argument_list|()
decl_stmt|;
if|if
condition|(
name|perm
operator|.
name|getAclBit
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclEntries
init|=
name|fileSystem
operator|.
name|getAclStatus
argument_list|(
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getEntries
argument_list|()
decl_stmt|;
name|copyListingFileStatus
operator|.
name|setAclEntries
argument_list|(
name|aclEntries
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|preserveXAttrs
operator|||
name|preserveRawXAttrs
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|srcXAttrs
init|=
name|fileSystem
operator|.
name|getXAttrs
argument_list|(
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|preserveXAttrs
operator|&&
name|preserveRawXAttrs
condition|)
block|{
name|copyListingFileStatus
operator|.
name|setXAttrs
argument_list|(
name|srcXAttrs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|trgXAttrs
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|final
name|String
name|rawNS
init|=
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|XAttr
operator|.
name|NameSpace
operator|.
name|RAW
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|ent
range|:
name|srcXAttrs
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|String
name|xattrName
init|=
name|ent
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|xattrName
operator|.
name|startsWith
argument_list|(
name|rawNS
argument_list|)
condition|)
block|{
if|if
condition|(
name|preserveRawXAttrs
condition|)
block|{
name|trgXAttrs
operator|.
name|put
argument_list|(
name|xattrName
argument_list|,
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|preserveXAttrs
condition|)
block|{
name|trgXAttrs
operator|.
name|put
argument_list|(
name|xattrName
argument_list|,
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|copyListingFileStatus
operator|.
name|setXAttrs
argument_list|(
name|trgXAttrs
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|copyListingFileStatus
return|;
block|}
comment|/**    * Sort sequence file containing FileStatus and Text as key and value respecitvely    *    * @param fs - File System    * @param conf - Configuration    * @param sourceListing - Source listing file    * @return Path of the sorted file. Is source file with _sorted appended to the name    * @throws IOException - Any exception during sort.    */
DECL|method|sortListing (FileSystem fs, Configuration conf, Path sourceListing)
specifier|public
specifier|static
name|Path
name|sortListing
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|Path
name|sourceListing
parameter_list|)
throws|throws
name|IOException
block|{
name|SequenceFile
operator|.
name|Sorter
name|sorter
init|=
operator|new
name|SequenceFile
operator|.
name|Sorter
argument_list|(
name|fs
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|CopyListingFileStatus
operator|.
name|class
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Path
name|output
init|=
operator|new
name|Path
argument_list|(
name|sourceListing
operator|.
name|toString
argument_list|()
operator|+
literal|"_sorted"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|output
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sorter
operator|.
name|sort
argument_list|(
name|sourceListing
argument_list|,
name|output
argument_list|)
expr_stmt|;
return|return
name|output
return|;
block|}
comment|/**    * Determines if a file system supports ACLs by running a canary getAclStatus    * request on the file system root.  This method is used before distcp job    * submission to fail fast if the user requested preserving ACLs, but the file    * system cannot support ACLs.    *    * @param fs FileSystem to check    * @throws AclsNotSupportedException if fs does not support ACLs    */
DECL|method|checkFileSystemAclSupport (FileSystem fs)
specifier|public
specifier|static
name|void
name|checkFileSystemAclSupport
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|AclsNotSupportedException
block|{
try|try
block|{
name|fs
operator|.
name|getAclStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AclsNotSupportedException
argument_list|(
literal|"ACLs not supported for file system: "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Determines if a file system supports XAttrs by running a getXAttrs request    * on the file system root. This method is used before distcp job submission    * to fail fast if the user requested preserving XAttrs, but the file system    * cannot support XAttrs.    *     * @param fs FileSystem to check    * @throws XAttrsNotSupportedException if fs does not support XAttrs    */
DECL|method|checkFileSystemXAttrSupport (FileSystem fs)
specifier|public
specifier|static
name|void
name|checkFileSystemXAttrSupport
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|XAttrsNotSupportedException
block|{
try|try
block|{
name|fs
operator|.
name|getXAttrs
argument_list|(
operator|new
name|Path
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XAttrsNotSupportedException
argument_list|(
literal|"XAttrs not supported for file system: "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * String utility to convert a number-of-bytes to human readable format.    */
DECL|field|FORMATTER
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|DecimalFormat
argument_list|>
name|FORMATTER
init|=
operator|new
name|ThreadLocal
argument_list|<
name|DecimalFormat
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|DecimalFormat
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|DecimalFormat
argument_list|(
literal|"0.0"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|getFormatter ()
specifier|public
specifier|static
name|DecimalFormat
name|getFormatter
parameter_list|()
block|{
return|return
name|FORMATTER
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getStringDescriptionFor (long nBytes)
specifier|public
specifier|static
name|String
name|getStringDescriptionFor
parameter_list|(
name|long
name|nBytes
parameter_list|)
block|{
name|char
name|units
index|[]
init|=
block|{
literal|'B'
block|,
literal|'K'
block|,
literal|'M'
block|,
literal|'G'
block|,
literal|'T'
block|,
literal|'P'
block|}
decl_stmt|;
name|double
name|current
init|=
name|nBytes
decl_stmt|;
name|double
name|prev
init|=
name|current
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|current
operator|=
name|current
operator|/
literal|1024
operator|)
operator|>=
literal|1
condition|)
block|{
name|prev
operator|=
name|current
expr_stmt|;
operator|++
name|index
expr_stmt|;
block|}
assert|assert
name|index
operator|<
name|units
operator|.
name|length
operator|:
literal|"Too large a number."
assert|;
return|return
name|getFormatter
argument_list|()
operator|.
name|format
argument_list|(
name|prev
argument_list|)
operator|+
name|units
index|[
name|index
index|]
return|;
block|}
comment|/**    * Utility to compare checksums for the paths specified.    *    * If checksums's can't be retrieved, it doesn't fail the test    * Only time the comparison would fail is when checksums are    * available and they don't match    *    * @param sourceFS FileSystem for the source path.    * @param source The source path.    * @param sourceChecksum The checksum of the source file. If it is null we    * still need to retrieve it through sourceFS.    * @param targetFS FileSystem for the target path.    * @param target The target path.    * @return If either checksum couldn't be retrieved, the function returns    * false. If checksums are retrieved, the function returns true if they match,    * and false otherwise.    * @throws IOException if there's an exception while retrieving checksums.    */
DECL|method|checksumsAreEqual (FileSystem sourceFS, Path source, FileChecksum sourceChecksum, FileSystem targetFS, Path target)
specifier|public
specifier|static
name|boolean
name|checksumsAreEqual
parameter_list|(
name|FileSystem
name|sourceFS
parameter_list|,
name|Path
name|source
parameter_list|,
name|FileChecksum
name|sourceChecksum
parameter_list|,
name|FileSystem
name|targetFS
parameter_list|,
name|Path
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|FileChecksum
name|targetChecksum
init|=
literal|null
decl_stmt|;
try|try
block|{
name|sourceChecksum
operator|=
name|sourceChecksum
operator|!=
literal|null
condition|?
name|sourceChecksum
else|:
name|sourceFS
operator|.
name|getFileChecksum
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|targetChecksum
operator|=
name|targetFS
operator|.
name|getFileChecksum
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to retrieve checksum for "
operator|+
name|source
operator|+
literal|" or "
operator|+
name|target
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|sourceChecksum
operator|==
literal|null
operator|||
name|targetChecksum
operator|==
literal|null
operator|||
name|sourceChecksum
operator|.
name|equals
argument_list|(
name|targetChecksum
argument_list|)
operator|)
return|;
block|}
block|}
end_class

end_unit

