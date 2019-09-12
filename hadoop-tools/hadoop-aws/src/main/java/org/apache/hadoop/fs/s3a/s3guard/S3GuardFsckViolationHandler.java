begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.s3guard
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|s3guard
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|s3a
operator|.
name|S3AFileStatus
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
name|s3a
operator|.
name|S3AFileSystem
import|;
end_import

begin_comment
comment|/**  * Violation handler for the S3Guard's fsck.  */
end_comment

begin_class
DECL|class|S3GuardFsckViolationHandler
specifier|public
class|class
name|S3GuardFsckViolationHandler
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
name|S3GuardFsckViolationHandler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// The rawFS and metadataStore are here to prepare when the ViolationHandlers
comment|// will not just log, but fix the violations, so they will have access.
DECL|field|rawFs
specifier|private
specifier|final
name|S3AFileSystem
name|rawFs
decl_stmt|;
DECL|field|metadataStore
specifier|private
specifier|final
name|DynamoDBMetadataStore
name|metadataStore
decl_stmt|;
DECL|field|newLine
specifier|private
specifier|static
name|String
name|newLine
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
DECL|method|S3GuardFsckViolationHandler (S3AFileSystem fs, DynamoDBMetadataStore ddbms)
specifier|public
name|S3GuardFsckViolationHandler
parameter_list|(
name|S3AFileSystem
name|fs
parameter_list|,
name|DynamoDBMetadataStore
name|ddbms
parameter_list|)
block|{
name|this
operator|.
name|metadataStore
operator|=
name|ddbms
expr_stmt|;
name|this
operator|.
name|rawFs
operator|=
name|fs
expr_stmt|;
block|}
DECL|method|handle (S3GuardFsck.ComparePair comparePair)
specifier|public
name|void
name|handle
parameter_list|(
name|S3GuardFsck
operator|.
name|ComparePair
name|comparePair
parameter_list|)
block|{
if|if
condition|(
operator|!
name|comparePair
operator|.
name|containsViolation
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"There is no violation in the compare pair: {}"
argument_list|,
name|comparePair
argument_list|)
expr_stmt|;
return|return;
block|}
name|StringBuilder
name|sB
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sB
operator|.
name|append
argument_list|(
name|newLine
argument_list|)
operator|.
name|append
argument_list|(
literal|"On path: "
argument_list|)
operator|.
name|append
argument_list|(
name|comparePair
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|newLine
argument_list|)
expr_stmt|;
name|handleComparePair
argument_list|(
name|comparePair
argument_list|,
name|sB
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|sB
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new instance of the violation handler for all the violations    * found in the compare pair and use it.    *    * @param comparePair the compare pair with violations    * @param sB StringBuilder to append error strings from violations.    */
DECL|method|handleComparePair (S3GuardFsck.ComparePair comparePair, StringBuilder sB)
specifier|protected
specifier|static
name|void
name|handleComparePair
parameter_list|(
name|S3GuardFsck
operator|.
name|ComparePair
name|comparePair
parameter_list|,
name|StringBuilder
name|sB
parameter_list|)
block|{
for|for
control|(
name|S3GuardFsck
operator|.
name|Violation
name|violation
range|:
name|comparePair
operator|.
name|getViolations
argument_list|()
control|)
block|{
try|try
block|{
name|ViolationHandler
name|handler
init|=
name|violation
operator|.
name|getHandler
argument_list|()
operator|.
name|getDeclaredConstructor
argument_list|(
name|S3GuardFsck
operator|.
name|ComparePair
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|(
name|comparePair
argument_list|)
decl_stmt|;
specifier|final
name|String
name|errorStr
init|=
name|handler
operator|.
name|getError
argument_list|()
decl_stmt|;
name|sB
operator|.
name|append
argument_list|(
name|errorStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can not find declared constructor for handler: {}"
argument_list|,
name|violation
operator|.
name|getHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
decl||
name|InstantiationException
decl||
name|InvocationTargetException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can not instantiate handler: {}"
argument_list|,
name|violation
operator|.
name|getHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sB
operator|.
name|append
argument_list|(
name|newLine
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Violation handler abstract class.    * This class should be extended for violation handlers.    */
DECL|class|ViolationHandler
specifier|public
specifier|static
specifier|abstract
class|class
name|ViolationHandler
block|{
DECL|field|pathMetadata
specifier|private
specifier|final
name|PathMetadata
name|pathMetadata
decl_stmt|;
DECL|field|s3FileStatus
specifier|private
specifier|final
name|S3AFileStatus
name|s3FileStatus
decl_stmt|;
DECL|field|msFileStatus
specifier|private
specifier|final
name|S3AFileStatus
name|msFileStatus
decl_stmt|;
DECL|field|s3DirListing
specifier|private
specifier|final
name|List
argument_list|<
name|FileStatus
argument_list|>
name|s3DirListing
decl_stmt|;
DECL|field|msDirListing
specifier|private
specifier|final
name|DirListingMetadata
name|msDirListing
decl_stmt|;
DECL|method|ViolationHandler (S3GuardFsck.ComparePair comparePair)
specifier|public
name|ViolationHandler
parameter_list|(
name|S3GuardFsck
operator|.
name|ComparePair
name|comparePair
parameter_list|)
block|{
name|pathMetadata
operator|=
name|comparePair
operator|.
name|getMsPathMetadata
argument_list|()
expr_stmt|;
name|s3FileStatus
operator|=
name|comparePair
operator|.
name|getS3FileStatus
argument_list|()
expr_stmt|;
if|if
condition|(
name|pathMetadata
operator|!=
literal|null
condition|)
block|{
name|msFileStatus
operator|=
name|pathMetadata
operator|.
name|getFileStatus
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|msFileStatus
operator|=
literal|null
expr_stmt|;
block|}
name|s3DirListing
operator|=
name|comparePair
operator|.
name|getS3DirListing
argument_list|()
expr_stmt|;
name|msDirListing
operator|=
name|comparePair
operator|.
name|getMsDirListing
argument_list|()
expr_stmt|;
block|}
DECL|method|getError ()
specifier|public
specifier|abstract
name|String
name|getError
parameter_list|()
function_decl|;
DECL|method|getPathMetadata ()
specifier|public
name|PathMetadata
name|getPathMetadata
parameter_list|()
block|{
return|return
name|pathMetadata
return|;
block|}
DECL|method|getS3FileStatus ()
specifier|public
name|S3AFileStatus
name|getS3FileStatus
parameter_list|()
block|{
return|return
name|s3FileStatus
return|;
block|}
DECL|method|getMsFileStatus ()
specifier|public
name|S3AFileStatus
name|getMsFileStatus
parameter_list|()
block|{
return|return
name|msFileStatus
return|;
block|}
DECL|method|getS3DirListing ()
specifier|public
name|List
argument_list|<
name|FileStatus
argument_list|>
name|getS3DirListing
parameter_list|()
block|{
return|return
name|s3DirListing
return|;
block|}
DECL|method|getMsDirListing ()
specifier|public
name|DirListingMetadata
name|getMsDirListing
parameter_list|()
block|{
return|return
name|msDirListing
return|;
block|}
block|}
comment|/**    * The violation handler when there's no matching metadata entry in the MS.    */
DECL|class|NoMetadataEntry
specifier|public
specifier|static
class|class
name|NoMetadataEntry
extends|extends
name|ViolationHandler
block|{
DECL|method|NoMetadataEntry (S3GuardFsck.ComparePair comparePair)
specifier|public
name|NoMetadataEntry
parameter_list|(
name|S3GuardFsck
operator|.
name|ComparePair
name|comparePair
parameter_list|)
block|{
name|super
argument_list|(
name|comparePair
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getError ()
specifier|public
name|String
name|getError
parameter_list|()
block|{
return|return
literal|"No PathMetadata for this path in the MS."
return|;
block|}
block|}
comment|/**    * The violation handler when there's no parent entry.    */
DECL|class|NoParentEntry
specifier|public
specifier|static
class|class
name|NoParentEntry
extends|extends
name|ViolationHandler
block|{
DECL|method|NoParentEntry (S3GuardFsck.ComparePair comparePair)
specifier|public
name|NoParentEntry
parameter_list|(
name|S3GuardFsck
operator|.
name|ComparePair
name|comparePair
parameter_list|)
block|{
name|super
argument_list|(
name|comparePair
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getError ()
specifier|public
name|String
name|getError
parameter_list|()
block|{
return|return
literal|"Entry does not have a parent entry (not root)"
return|;
block|}
block|}
comment|/**    * The violation handler when the parent of an entry is a file.    */
DECL|class|ParentIsAFile
specifier|public
specifier|static
class|class
name|ParentIsAFile
extends|extends
name|ViolationHandler
block|{
DECL|method|ParentIsAFile (S3GuardFsck.ComparePair comparePair)
specifier|public
name|ParentIsAFile
parameter_list|(
name|S3GuardFsck
operator|.
name|ComparePair
name|comparePair
parameter_list|)
block|{
name|super
argument_list|(
name|comparePair
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getError ()
specifier|public
name|String
name|getError
parameter_list|()
block|{
return|return
literal|"The entry's parent in the metastore database is a file."
return|;
block|}
block|}
comment|/**    * The violation handler when the parent of an entry is tombstoned.    */
DECL|class|ParentTombstoned
specifier|public
specifier|static
class|class
name|ParentTombstoned
extends|extends
name|ViolationHandler
block|{
DECL|method|ParentTombstoned (S3GuardFsck.ComparePair comparePair)
specifier|public
name|ParentTombstoned
parameter_list|(
name|S3GuardFsck
operator|.
name|ComparePair
name|comparePair
parameter_list|)
block|{
name|super
argument_list|(
name|comparePair
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getError ()
specifier|public
name|String
name|getError
parameter_list|()
block|{
return|return
literal|"The entry in the metastore database has a parent entry "
operator|+
literal|"which is a tombstone marker"
return|;
block|}
block|}
comment|/**    * The violation handler when there's a directory is a file metadata in MS.    */
DECL|class|DirInS3FileInMs
specifier|public
specifier|static
class|class
name|DirInS3FileInMs
extends|extends
name|ViolationHandler
block|{
DECL|method|DirInS3FileInMs (S3GuardFsck.ComparePair comparePair)
specifier|public
name|DirInS3FileInMs
parameter_list|(
name|S3GuardFsck
operator|.
name|ComparePair
name|comparePair
parameter_list|)
block|{
name|super
argument_list|(
name|comparePair
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getError ()
specifier|public
name|String
name|getError
parameter_list|()
block|{
return|return
literal|"A directory in S3 is a file entry in the MS"
return|;
block|}
block|}
comment|/**    * The violation handler when a file metadata is a directory in MS.    */
DECL|class|FileInS3DirInMs
specifier|public
specifier|static
class|class
name|FileInS3DirInMs
extends|extends
name|ViolationHandler
block|{
DECL|method|FileInS3DirInMs (S3GuardFsck.ComparePair comparePair)
specifier|public
name|FileInS3DirInMs
parameter_list|(
name|S3GuardFsck
operator|.
name|ComparePair
name|comparePair
parameter_list|)
block|{
name|super
argument_list|(
name|comparePair
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getError ()
specifier|public
name|String
name|getError
parameter_list|()
block|{
return|return
literal|"A file in S3 is a directory entry in the MS"
return|;
block|}
block|}
comment|/**    * The violation handler when there's a directory listing content mismatch.    */
DECL|class|AuthDirContentMismatch
specifier|public
specifier|static
class|class
name|AuthDirContentMismatch
extends|extends
name|ViolationHandler
block|{
DECL|method|AuthDirContentMismatch (S3GuardFsck.ComparePair comparePair)
specifier|public
name|AuthDirContentMismatch
parameter_list|(
name|S3GuardFsck
operator|.
name|ComparePair
name|comparePair
parameter_list|)
block|{
name|super
argument_list|(
name|comparePair
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getError ()
specifier|public
name|String
name|getError
parameter_list|()
block|{
specifier|final
name|String
name|str
init|=
name|String
operator|.
name|format
argument_list|(
literal|"The content of an authoritative directory listing does "
operator|+
literal|"not match the content of the S3 listing. S3: %s, MS: %s"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|getS3DirListing
argument_list|()
argument_list|)
argument_list|,
name|getMsDirListing
argument_list|()
operator|.
name|getListing
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|str
return|;
block|}
block|}
comment|/**    * The violation handler when there's a length mismatch.    */
DECL|class|LengthMismatch
specifier|public
specifier|static
class|class
name|LengthMismatch
extends|extends
name|ViolationHandler
block|{
DECL|method|LengthMismatch (S3GuardFsck.ComparePair comparePair)
specifier|public
name|LengthMismatch
parameter_list|(
name|S3GuardFsck
operator|.
name|ComparePair
name|comparePair
parameter_list|)
block|{
name|super
argument_list|(
name|comparePair
argument_list|)
expr_stmt|;
block|}
DECL|method|getError ()
annotation|@
name|Override
specifier|public
name|String
name|getError
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"File length mismatch - S3: %s, MS: %s"
argument_list|,
name|getS3FileStatus
argument_list|()
operator|.
name|getLen
argument_list|()
argument_list|,
name|getMsFileStatus
argument_list|()
operator|.
name|getLen
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * The violation handler when there's a modtime mismatch.    */
DECL|class|ModTimeMismatch
specifier|public
specifier|static
class|class
name|ModTimeMismatch
extends|extends
name|ViolationHandler
block|{
DECL|method|ModTimeMismatch (S3GuardFsck.ComparePair comparePair)
specifier|public
name|ModTimeMismatch
parameter_list|(
name|S3GuardFsck
operator|.
name|ComparePair
name|comparePair
parameter_list|)
block|{
name|super
argument_list|(
name|comparePair
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getError ()
specifier|public
name|String
name|getError
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"File timestamp mismatch - S3: %s, MS: %s"
argument_list|,
name|getS3FileStatus
argument_list|()
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|getMsFileStatus
argument_list|()
operator|.
name|getModificationTime
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * The violation handler when there's a version id mismatch.    */
DECL|class|VersionIdMismatch
specifier|public
specifier|static
class|class
name|VersionIdMismatch
extends|extends
name|ViolationHandler
block|{
DECL|method|VersionIdMismatch (S3GuardFsck.ComparePair comparePair)
specifier|public
name|VersionIdMismatch
parameter_list|(
name|S3GuardFsck
operator|.
name|ComparePair
name|comparePair
parameter_list|)
block|{
name|super
argument_list|(
name|comparePair
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getError ()
specifier|public
name|String
name|getError
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"getVersionId mismatch - S3: %s, MS: %s"
argument_list|,
name|getS3FileStatus
argument_list|()
operator|.
name|getVersionId
argument_list|()
argument_list|,
name|getMsFileStatus
argument_list|()
operator|.
name|getVersionId
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * The violation handler when there's an etag mismatch.    */
DECL|class|EtagMismatch
specifier|public
specifier|static
class|class
name|EtagMismatch
extends|extends
name|ViolationHandler
block|{
DECL|method|EtagMismatch (S3GuardFsck.ComparePair comparePair)
specifier|public
name|EtagMismatch
parameter_list|(
name|S3GuardFsck
operator|.
name|ComparePair
name|comparePair
parameter_list|)
block|{
name|super
argument_list|(
name|comparePair
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getError ()
specifier|public
name|String
name|getError
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"Etag mismatch - S3: %s, MS: %s"
argument_list|,
name|getS3FileStatus
argument_list|()
operator|.
name|getETag
argument_list|()
argument_list|,
name|getMsFileStatus
argument_list|()
operator|.
name|getETag
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * The violation handler when there's no etag.    */
DECL|class|NoEtag
specifier|public
specifier|static
class|class
name|NoEtag
extends|extends
name|ViolationHandler
block|{
DECL|method|NoEtag (S3GuardFsck.ComparePair comparePair)
specifier|public
name|NoEtag
parameter_list|(
name|S3GuardFsck
operator|.
name|ComparePair
name|comparePair
parameter_list|)
block|{
name|super
argument_list|(
name|comparePair
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getError ()
specifier|public
name|String
name|getError
parameter_list|()
block|{
return|return
literal|"No etag."
return|;
block|}
block|}
comment|/**    * The violation handler when there's a tombstoned entry in the ms is    * present, but the object is not deleted in S3.    */
DECL|class|TombstonedInMsNotDeletedInS3
specifier|public
specifier|static
class|class
name|TombstonedInMsNotDeletedInS3
extends|extends
name|ViolationHandler
block|{
DECL|method|TombstonedInMsNotDeletedInS3 (S3GuardFsck.ComparePair comparePair)
specifier|public
name|TombstonedInMsNotDeletedInS3
parameter_list|(
name|S3GuardFsck
operator|.
name|ComparePair
name|comparePair
parameter_list|)
block|{
name|super
argument_list|(
name|comparePair
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getError ()
specifier|public
name|String
name|getError
parameter_list|()
block|{
return|return
literal|"The entry for the path is tombstoned in the MS."
return|;
block|}
block|}
block|}
end_class

end_unit

