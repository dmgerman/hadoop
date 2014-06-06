begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
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
name|conf
operator|.
name|Configured
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
name|tools
operator|.
name|util
operator|.
name|DistCpUtils
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
name|security
operator|.
name|Credentials
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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
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
name|util
operator|.
name|Set
import|;
end_import

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
name|Sets
import|;
end_import

begin_comment
comment|/**  * The CopyListing abstraction is responsible for how the list of  * sources and targets is constructed, for DistCp's copy function.  * The copy-listing should be a SequenceFile<Text, CopyListingFileStatus>,  * located at the path specified to buildListing(),  * each entry being a pair of (Source relative path, source file status),  * all the paths being fully qualified.  */
end_comment

begin_class
DECL|class|CopyListing
specifier|public
specifier|abstract
class|class
name|CopyListing
extends|extends
name|Configured
block|{
DECL|field|credentials
specifier|private
name|Credentials
name|credentials
decl_stmt|;
comment|/**    * Build listing function creates the input listing that distcp uses to    * perform the copy.    *    * The build listing is a sequence file that has relative path of a file in the key    * and the file status information of the source file in the value    *    * For instance if the source path is /tmp/data and the traversed path is    * /tmp/data/dir1/dir2/file1, then the sequence file would contain    *    * key: /dir1/dir2/file1 and value: FileStatus(/tmp/data/dir1/dir2/file1)    *    * File would also contain directory entries. Meaning, if /tmp/data/dir1/dir2/file1    * is the only file under /tmp/data, the resulting sequence file would contain the    * following entries    *    * key: /dir1 and value: FileStatus(/tmp/data/dir1)    * key: /dir1/dir2 and value: FileStatus(/tmp/data/dir1/dir2)    * key: /dir1/dir2/file1 and value: FileStatus(/tmp/data/dir1/dir2/file1)    *    * Cases requiring special handling:    * If source path is a file (/tmp/file1), contents of the file will be as follows    *    * TARGET DOES NOT EXIST: Key-"", Value-FileStatus(/tmp/file1)    * TARGET IS FILE       : Key-"", Value-FileStatus(/tmp/file1)    * TARGET IS DIR        : Key-"/file1", Value-FileStatus(/tmp/file1)      *    * @param pathToListFile - Output file where the listing would be stored    * @param options - Input options to distcp    * @throws IOException - Exception if any    */
DECL|method|buildListing (Path pathToListFile, DistCpOptions options)
specifier|public
specifier|final
name|void
name|buildListing
parameter_list|(
name|Path
name|pathToListFile
parameter_list|,
name|DistCpOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|validatePaths
argument_list|(
name|options
argument_list|)
expr_stmt|;
name|doBuildListing
argument_list|(
name|pathToListFile
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|Configuration
name|config
init|=
name|getConf
argument_list|()
decl_stmt|;
name|config
operator|.
name|set
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_LISTING_FILE_PATH
argument_list|,
name|pathToListFile
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setLong
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_TOTAL_BYTES_TO_BE_COPIED
argument_list|,
name|getBytesToCopy
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setLong
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_TOTAL_NUMBER_OF_RECORDS
argument_list|,
name|getNumberOfPaths
argument_list|()
argument_list|)
expr_stmt|;
name|validateFinalListing
argument_list|(
name|pathToListFile
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validate input and output paths    *    * @param options - Input options    * @throws InvalidInputException: If inputs are invalid    * @throws IOException: any Exception with FS     */
DECL|method|validatePaths (DistCpOptions options)
specifier|protected
specifier|abstract
name|void
name|validatePaths
parameter_list|(
name|DistCpOptions
name|options
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidInputException
function_decl|;
comment|/**    * The interface to be implemented by sub-classes, to create the source/target file listing.    * @param pathToListFile Path on HDFS where the listing file is written.    * @param options Input Options for DistCp (indicating source/target paths.)    * @throws IOException: Thrown on failure to create the listing file.    */
DECL|method|doBuildListing (Path pathToListFile, DistCpOptions options)
specifier|protected
specifier|abstract
name|void
name|doBuildListing
parameter_list|(
name|Path
name|pathToListFile
parameter_list|,
name|DistCpOptions
name|options
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Return the total bytes that distCp should copy for the source paths    * This doesn't consider whether file is same should be skipped during copy    *    * @return total bytes to copy    */
DECL|method|getBytesToCopy ()
specifier|protected
specifier|abstract
name|long
name|getBytesToCopy
parameter_list|()
function_decl|;
comment|/**    * Return the total number of paths to distcp, includes directories as well    * This doesn't consider whether file/dir is already present and should be skipped during copy    *    * @return Total number of paths to distcp    */
DECL|method|getNumberOfPaths ()
specifier|protected
specifier|abstract
name|long
name|getNumberOfPaths
parameter_list|()
function_decl|;
comment|/**    * Validate the final resulting path listing.  Checks if there are duplicate    * entries.  If preserving ACLs, checks that file system can support ACLs.    * If preserving XAttrs, checks that file system can support XAttrs.    *    * @param pathToListFile - path listing build by doBuildListing    * @param options - Input options to distcp    * @throws IOException - Any issues while checking for duplicates and throws    * @throws DuplicateFileException - if there are duplicates    */
DECL|method|validateFinalListing (Path pathToListFile, DistCpOptions options)
specifier|private
name|void
name|validateFinalListing
parameter_list|(
name|Path
name|pathToListFile
parameter_list|,
name|DistCpOptions
name|options
parameter_list|)
throws|throws
name|DuplicateFileException
throws|,
name|IOException
block|{
name|Configuration
name|config
init|=
name|getConf
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|pathToListFile
operator|.
name|getFileSystem
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|Path
name|sortedList
init|=
name|DistCpUtils
operator|.
name|sortListing
argument_list|(
name|fs
argument_list|,
name|config
argument_list|,
name|pathToListFile
argument_list|)
decl_stmt|;
name|SequenceFile
operator|.
name|Reader
name|reader
init|=
operator|new
name|SequenceFile
operator|.
name|Reader
argument_list|(
name|config
argument_list|,
name|SequenceFile
operator|.
name|Reader
operator|.
name|file
argument_list|(
name|sortedList
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Text
name|lastKey
init|=
operator|new
name|Text
argument_list|(
literal|"*"
argument_list|)
decl_stmt|;
comment|//source relative path can never hold *
name|CopyListingFileStatus
name|lastFileStatus
init|=
operator|new
name|CopyListingFileStatus
argument_list|()
decl_stmt|;
name|Text
name|currentKey
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|URI
argument_list|>
name|aclSupportCheckFsSet
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|URI
argument_list|>
name|xAttrSupportCheckFsSet
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|next
argument_list|(
name|currentKey
argument_list|)
condition|)
block|{
if|if
condition|(
name|currentKey
operator|.
name|equals
argument_list|(
name|lastKey
argument_list|)
condition|)
block|{
name|CopyListingFileStatus
name|currentFileStatus
init|=
operator|new
name|CopyListingFileStatus
argument_list|()
decl_stmt|;
name|reader
operator|.
name|getCurrentValue
argument_list|(
name|currentFileStatus
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DuplicateFileException
argument_list|(
literal|"File "
operator|+
name|lastFileStatus
operator|.
name|getPath
argument_list|()
operator|+
literal|" and "
operator|+
name|currentFileStatus
operator|.
name|getPath
argument_list|()
operator|+
literal|" would cause duplicates. Aborting"
argument_list|)
throw|;
block|}
name|reader
operator|.
name|getCurrentValue
argument_list|(
name|lastFileStatus
argument_list|)
expr_stmt|;
if|if
condition|(
name|options
operator|.
name|shouldPreserve
argument_list|(
name|DistCpOptions
operator|.
name|FileAttribute
operator|.
name|ACL
argument_list|)
condition|)
block|{
name|FileSystem
name|lastFs
init|=
name|lastFileStatus
operator|.
name|getPath
argument_list|()
operator|.
name|getFileSystem
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|URI
name|lastFsUri
init|=
name|lastFs
operator|.
name|getUri
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|aclSupportCheckFsSet
operator|.
name|contains
argument_list|(
name|lastFsUri
argument_list|)
condition|)
block|{
name|DistCpUtils
operator|.
name|checkFileSystemAclSupport
argument_list|(
name|lastFs
argument_list|)
expr_stmt|;
name|aclSupportCheckFsSet
operator|.
name|add
argument_list|(
name|lastFsUri
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|options
operator|.
name|shouldPreserve
argument_list|(
name|DistCpOptions
operator|.
name|FileAttribute
operator|.
name|XATTR
argument_list|)
condition|)
block|{
name|FileSystem
name|lastFs
init|=
name|lastFileStatus
operator|.
name|getPath
argument_list|()
operator|.
name|getFileSystem
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|URI
name|lastFsUri
init|=
name|lastFs
operator|.
name|getUri
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|xAttrSupportCheckFsSet
operator|.
name|contains
argument_list|(
name|lastFsUri
argument_list|)
condition|)
block|{
name|DistCpUtils
operator|.
name|checkFileSystemXAttrSupport
argument_list|(
name|lastFs
argument_list|)
expr_stmt|;
name|xAttrSupportCheckFsSet
operator|.
name|add
argument_list|(
name|lastFsUri
argument_list|)
expr_stmt|;
block|}
block|}
name|lastKey
operator|.
name|set
argument_list|(
name|currentKey
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Protected constructor, to initialize configuration.    * @param configuration The input configuration,    *                        with which the source/target FileSystems may be accessed.    * @param credentials - Credentials object on which the FS delegation tokens are cached.If null    * delegation token caching is skipped    */
DECL|method|CopyListing (Configuration configuration, Credentials credentials)
specifier|protected
name|CopyListing
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|Credentials
name|credentials
parameter_list|)
block|{
name|setConf
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|setCredentials
argument_list|(
name|credentials
argument_list|)
expr_stmt|;
block|}
comment|/**    * set Credentials store, on which FS delegatin token will be cached    * @param credentials - Credentials object    */
DECL|method|setCredentials (Credentials credentials)
specifier|protected
name|void
name|setCredentials
parameter_list|(
name|Credentials
name|credentials
parameter_list|)
block|{
name|this
operator|.
name|credentials
operator|=
name|credentials
expr_stmt|;
block|}
comment|/**    * get credentials to update the delegation tokens for accessed FS objects    * @return Credentials object    */
DECL|method|getCredentials ()
specifier|protected
name|Credentials
name|getCredentials
parameter_list|()
block|{
return|return
name|credentials
return|;
block|}
comment|/**    * Public Factory method with which the appropriate CopyListing implementation may be retrieved.    * @param configuration The input configuration.    * @param credentials Credentials object on which the FS delegation tokens are cached    * @param options The input Options, to help choose the appropriate CopyListing Implementation.    * @return An instance of the appropriate CopyListing implementation.    * @throws java.io.IOException - Exception if any    */
DECL|method|getCopyListing (Configuration configuration, Credentials credentials, DistCpOptions options)
specifier|public
specifier|static
name|CopyListing
name|getCopyListing
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|Credentials
name|credentials
parameter_list|,
name|DistCpOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|copyListingClassName
init|=
name|configuration
operator|.
name|get
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_COPY_LISTING_CLASS
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|CopyListing
argument_list|>
name|copyListingClass
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|copyListingClassName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|copyListingClass
operator|=
name|configuration
operator|.
name|getClass
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_COPY_LISTING_CLASS
argument_list|,
name|GlobbedCopyListing
operator|.
name|class
argument_list|,
name|CopyListing
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|options
operator|.
name|getSourceFileListing
argument_list|()
operator|==
literal|null
condition|)
block|{
name|copyListingClass
operator|=
name|GlobbedCopyListing
operator|.
name|class
expr_stmt|;
block|}
else|else
block|{
name|copyListingClass
operator|=
name|FileBasedCopyListing
operator|.
name|class
expr_stmt|;
block|}
block|}
name|copyListingClassName
operator|=
name|copyListingClass
operator|.
name|getName
argument_list|()
expr_stmt|;
name|Constructor
argument_list|<
name|?
extends|extends
name|CopyListing
argument_list|>
name|constructor
init|=
name|copyListingClass
operator|.
name|getDeclaredConstructor
argument_list|(
name|Configuration
operator|.
name|class
argument_list|,
name|Credentials
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|constructor
operator|.
name|newInstance
argument_list|(
name|configuration
argument_list|,
name|credentials
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to instantiate "
operator|+
name|copyListingClassName
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|class|DuplicateFileException
specifier|static
class|class
name|DuplicateFileException
extends|extends
name|RuntimeException
block|{
DECL|method|DuplicateFileException (String message)
specifier|public
name|DuplicateFileException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|InvalidInputException
specifier|static
class|class
name|InvalidInputException
extends|extends
name|RuntimeException
block|{
DECL|method|InvalidInputException (String message)
specifier|public
name|InvalidInputException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|AclsNotSupportedException
specifier|public
specifier|static
class|class
name|AclsNotSupportedException
extends|extends
name|RuntimeException
block|{
DECL|method|AclsNotSupportedException (String message)
specifier|public
name|AclsNotSupportedException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|XAttrsNotSupportedException
specifier|public
specifier|static
class|class
name|XAttrsNotSupportedException
extends|extends
name|RuntimeException
block|{
DECL|method|XAttrsNotSupportedException (String message)
specifier|public
name|XAttrsNotSupportedException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

