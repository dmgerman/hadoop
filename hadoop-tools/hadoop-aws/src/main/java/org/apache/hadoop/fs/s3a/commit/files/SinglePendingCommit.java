begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit.files
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
name|commit
operator|.
name|files
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
name|io
operator|.
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|Date
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

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|PartETag
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
name|base
operator|.
name|Preconditions
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
name|lang3
operator|.
name|StringUtils
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
name|s3a
operator|.
name|commit
operator|.
name|ValidationFailure
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
name|JsonSerialization
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
name|fs
operator|.
name|s3a
operator|.
name|commit
operator|.
name|CommitUtils
operator|.
name|validateCollectionClass
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
name|fs
operator|.
name|s3a
operator|.
name|commit
operator|.
name|ValidationFailure
operator|.
name|verify
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
name|StringUtils
operator|.
name|join
import|;
end_import

begin_comment
comment|/**  * This is the serialization format for uploads yet to be committerd.  *  * It's marked as {@link Serializable} so that it can be passed in RPC  * calls; for this to work it relies on the fact that java.io ArrayList  * and LinkedList are serializable. If any other list type is used for etags,  * it must also be serialized. Jackson expects lists, and it is used  * to persist to disk.  *  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|SinglePendingCommit
specifier|public
class|class
name|SinglePendingCommit
extends|extends
name|PersistentCommitData
implements|implements
name|Iterable
argument_list|<
name|String
argument_list|>
block|{
comment|/**    * Serialization ID: {@value}.    */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0x10000
operator|+
name|VERSION
decl_stmt|;
comment|/** Version marker. */
DECL|field|version
specifier|private
name|int
name|version
init|=
name|VERSION
decl_stmt|;
comment|/**    * This is the filename of the pending file itself.    * Used during processing; it's persistent value, if any, is ignored.    */
DECL|field|filename
specifier|private
name|String
name|filename
decl_stmt|;
comment|/** Path URI of the destination. */
DECL|field|uri
specifier|private
name|String
name|uri
init|=
literal|""
decl_stmt|;
comment|/** ID of the upload. */
DECL|field|uploadId
specifier|private
name|String
name|uploadId
decl_stmt|;
comment|/** Destination bucket. */
DECL|field|bucket
specifier|private
name|String
name|bucket
decl_stmt|;
comment|/** Destination key in the bucket. */
DECL|field|destinationKey
specifier|private
name|String
name|destinationKey
decl_stmt|;
comment|/** When was the upload created? */
DECL|field|created
specifier|private
name|long
name|created
decl_stmt|;
comment|/** When was the upload saved? */
DECL|field|saved
specifier|private
name|long
name|saved
decl_stmt|;
comment|/** timestamp as date; no expectation of parseability. */
DECL|field|date
specifier|private
name|String
name|date
decl_stmt|;
comment|/** Job ID, if known. */
DECL|field|jobId
specifier|private
name|String
name|jobId
init|=
literal|""
decl_stmt|;
comment|/** Task ID, if known. */
DECL|field|taskId
specifier|private
name|String
name|taskId
init|=
literal|""
decl_stmt|;
comment|/** Arbitrary notes. */
DECL|field|text
specifier|private
name|String
name|text
init|=
literal|""
decl_stmt|;
comment|/** Ordered list of etags. */
DECL|field|etags
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|etags
decl_stmt|;
comment|/**    * Any custom extra data committer subclasses may choose to add.    */
DECL|field|extraData
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|extraData
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/** Destination file size. */
DECL|field|length
specifier|private
name|long
name|length
decl_stmt|;
DECL|method|SinglePendingCommit ()
specifier|public
name|SinglePendingCommit
parameter_list|()
block|{   }
comment|/**    * Get a JSON serializer for this class.    * @return a serializer.    */
DECL|method|serializer ()
specifier|public
specifier|static
name|JsonSerialization
argument_list|<
name|SinglePendingCommit
argument_list|>
name|serializer
parameter_list|()
block|{
return|return
operator|new
name|JsonSerialization
argument_list|<>
argument_list|(
name|SinglePendingCommit
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Load an instance from a file, then validate it.    * @param fs filesystem    * @param path path    * @return the loaded instance    * @throws IOException IO failure    * @throws ValidationFailure if the data is invalid    */
DECL|method|load (FileSystem fs, Path path)
specifier|public
specifier|static
name|SinglePendingCommit
name|load
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|SinglePendingCommit
name|instance
init|=
name|serializer
argument_list|()
operator|.
name|load
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|instance
operator|.
name|filename
operator|=
name|path
operator|.
name|toString
argument_list|()
expr_stmt|;
name|instance
operator|.
name|validate
argument_list|()
expr_stmt|;
return|return
name|instance
return|;
block|}
comment|/**    * Deserialize via java Serialization API: deserialize the instance    * and then call {@link #validate()} to verify that the deserialized    * data is valid.    * @param inStream input stream    * @throws IOException IO problem    * @throws ClassNotFoundException reflection problems    * @throws ValidationFailure validation failure    */
DECL|method|readObject (ObjectInputStream inStream)
specifier|private
name|void
name|readObject
parameter_list|(
name|ObjectInputStream
name|inStream
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|inStream
operator|.
name|defaultReadObject
argument_list|()
expr_stmt|;
name|validate
argument_list|()
expr_stmt|;
block|}
comment|/**    * Set the various timestamp fields to the supplied value.    * @param millis time in milliseconds    */
DECL|method|touch (long millis)
specifier|public
name|void
name|touch
parameter_list|(
name|long
name|millis
parameter_list|)
block|{
name|created
operator|=
name|millis
expr_stmt|;
name|saved
operator|=
name|millis
expr_stmt|;
name|date
operator|=
operator|new
name|Date
argument_list|(
name|millis
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
comment|/**    * Set the commit data.    * @param parts ordered list of etags.    * @throws ValidationFailure if the data is invalid    */
DECL|method|bindCommitData (List<PartETag> parts)
specifier|public
name|void
name|bindCommitData
parameter_list|(
name|List
argument_list|<
name|PartETag
argument_list|>
name|parts
parameter_list|)
throws|throws
name|ValidationFailure
block|{
name|etags
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|parts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|counter
init|=
literal|1
decl_stmt|;
for|for
control|(
name|PartETag
name|part
range|:
name|parts
control|)
block|{
name|verify
argument_list|(
name|part
operator|.
name|getPartNumber
argument_list|()
operator|==
name|counter
argument_list|,
literal|"Expected part number %s but got %s"
argument_list|,
name|counter
argument_list|,
name|part
operator|.
name|getPartNumber
argument_list|()
argument_list|)
expr_stmt|;
name|etags
operator|.
name|add
argument_list|(
name|part
operator|.
name|getETag
argument_list|()
argument_list|)
expr_stmt|;
name|counter
operator|++
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|validate ()
specifier|public
name|void
name|validate
parameter_list|()
throws|throws
name|ValidationFailure
block|{
name|verify
argument_list|(
name|version
operator|==
name|VERSION
argument_list|,
literal|"Wrong version: %s"
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|bucket
argument_list|)
argument_list|,
literal|"Empty bucket"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|destinationKey
argument_list|)
argument_list|,
literal|"Empty destination"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|uploadId
argument_list|)
argument_list|,
literal|"Empty uploadId"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|length
operator|>=
literal|0
argument_list|,
literal|"Invalid length: "
operator|+
name|length
argument_list|)
expr_stmt|;
name|destinationPath
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|etags
operator|!=
literal|null
argument_list|,
literal|"No etag list"
argument_list|)
expr_stmt|;
name|validateCollectionClass
argument_list|(
name|etags
argument_list|,
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|etag
range|:
name|etags
control|)
block|{
name|verify
argument_list|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|etag
argument_list|)
argument_list|,
literal|"Empty etag"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|extraData
operator|!=
literal|null
condition|)
block|{
name|validateCollectionClass
argument_list|(
name|extraData
operator|.
name|keySet
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|validateCollectionClass
argument_list|(
name|extraData
operator|.
name|values
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"DelayedCompleteData{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"version="
argument_list|)
operator|.
name|append
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", uri='"
argument_list|)
operator|.
name|append
argument_list|(
name|uri
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", destination='"
argument_list|)
operator|.
name|append
argument_list|(
name|destinationKey
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", uploadId='"
argument_list|)
operator|.
name|append
argument_list|(
name|uploadId
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", created="
argument_list|)
operator|.
name|append
argument_list|(
name|created
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", saved="
argument_list|)
operator|.
name|append
argument_list|(
name|saved
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", size="
argument_list|)
operator|.
name|append
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", date='"
argument_list|)
operator|.
name|append
argument_list|(
name|date
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", jobId='"
argument_list|)
operator|.
name|append
argument_list|(
name|jobId
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", taskId='"
argument_list|)
operator|.
name|append
argument_list|(
name|taskId
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", notes='"
argument_list|)
operator|.
name|append
argument_list|(
name|text
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
if|if
condition|(
name|etags
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", etags=["
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|join
argument_list|(
literal|","
argument_list|,
name|etags
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", etags=null"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toBytes ()
specifier|public
name|byte
index|[]
name|toBytes
parameter_list|()
throws|throws
name|IOException
block|{
name|validate
argument_list|()
expr_stmt|;
return|return
name|serializer
argument_list|()
operator|.
name|toBytes
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|save (FileSystem fs, Path path, boolean overwrite)
specifier|public
name|void
name|save
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
throws|throws
name|IOException
block|{
name|serializer
argument_list|()
operator|.
name|save
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|this
argument_list|,
name|overwrite
argument_list|)
expr_stmt|;
block|}
comment|/**    * Build the destination path of the object.    * @return the path    * @throws IllegalStateException if the URI is invalid    */
DECL|method|destinationPath ()
specifier|public
name|Path
name|destinationPath
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|uri
argument_list|)
argument_list|,
literal|"Empty uri"
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|new
name|Path
argument_list|(
operator|new
name|URI
argument_list|(
name|uri
argument_list|)
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
name|IllegalStateException
argument_list|(
literal|"Cannot parse URI "
operator|+
name|uri
argument_list|)
throw|;
block|}
block|}
comment|/**    * Get the number of etags.    * @return the size of the etag list.    */
DECL|method|getPartCount ()
specifier|public
name|int
name|getPartCount
parameter_list|()
block|{
return|return
name|etags
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Iterate over the etags.    * @return an iterator.    */
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|etags
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/** @return version marker. */
DECL|method|getVersion ()
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
DECL|method|setVersion (int version)
specifier|public
name|void
name|setVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
comment|/**    * This is the filename of the pending file itself.    * Used during processing; it's persistent value, if any, is ignored.    * @return filename    */
DECL|method|getFilename ()
specifier|public
name|String
name|getFilename
parameter_list|()
block|{
return|return
name|filename
return|;
block|}
DECL|method|setFilename (String filename)
specifier|public
name|void
name|setFilename
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
name|this
operator|.
name|filename
operator|=
name|filename
expr_stmt|;
block|}
comment|/** @return path URI of the destination. */
DECL|method|getUri ()
specifier|public
name|String
name|getUri
parameter_list|()
block|{
return|return
name|uri
return|;
block|}
DECL|method|setUri (String uri)
specifier|public
name|void
name|setUri
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
block|}
comment|/** @return ID of the upload. */
DECL|method|getUploadId ()
specifier|public
name|String
name|getUploadId
parameter_list|()
block|{
return|return
name|uploadId
return|;
block|}
DECL|method|setUploadId (String uploadId)
specifier|public
name|void
name|setUploadId
parameter_list|(
name|String
name|uploadId
parameter_list|)
block|{
name|this
operator|.
name|uploadId
operator|=
name|uploadId
expr_stmt|;
block|}
comment|/** @return destination bucket. */
DECL|method|getBucket ()
specifier|public
name|String
name|getBucket
parameter_list|()
block|{
return|return
name|bucket
return|;
block|}
DECL|method|setBucket (String bucket)
specifier|public
name|void
name|setBucket
parameter_list|(
name|String
name|bucket
parameter_list|)
block|{
name|this
operator|.
name|bucket
operator|=
name|bucket
expr_stmt|;
block|}
comment|/** @return destination key in the bucket. */
DECL|method|getDestinationKey ()
specifier|public
name|String
name|getDestinationKey
parameter_list|()
block|{
return|return
name|destinationKey
return|;
block|}
DECL|method|setDestinationKey (String destinationKey)
specifier|public
name|void
name|setDestinationKey
parameter_list|(
name|String
name|destinationKey
parameter_list|)
block|{
name|this
operator|.
name|destinationKey
operator|=
name|destinationKey
expr_stmt|;
block|}
comment|/**    * When was the upload created?    * @return timestamp    */
DECL|method|getCreated ()
specifier|public
name|long
name|getCreated
parameter_list|()
block|{
return|return
name|created
return|;
block|}
DECL|method|setCreated (long created)
specifier|public
name|void
name|setCreated
parameter_list|(
name|long
name|created
parameter_list|)
block|{
name|this
operator|.
name|created
operator|=
name|created
expr_stmt|;
block|}
comment|/**    * When was the upload saved?    * @return timestamp    */
DECL|method|getSaved ()
specifier|public
name|long
name|getSaved
parameter_list|()
block|{
return|return
name|saved
return|;
block|}
DECL|method|setSaved (long saved)
specifier|public
name|void
name|setSaved
parameter_list|(
name|long
name|saved
parameter_list|)
block|{
name|this
operator|.
name|saved
operator|=
name|saved
expr_stmt|;
block|}
comment|/**    * Timestamp as date; no expectation of parseability.    * @return date string    */
DECL|method|getDate ()
specifier|public
name|String
name|getDate
parameter_list|()
block|{
return|return
name|date
return|;
block|}
DECL|method|setDate (String date)
specifier|public
name|void
name|setDate
parameter_list|(
name|String
name|date
parameter_list|)
block|{
name|this
operator|.
name|date
operator|=
name|date
expr_stmt|;
block|}
comment|/** @return Job ID, if known. */
DECL|method|getJobId ()
specifier|public
name|String
name|getJobId
parameter_list|()
block|{
return|return
name|jobId
return|;
block|}
DECL|method|setJobId (String jobId)
specifier|public
name|void
name|setJobId
parameter_list|(
name|String
name|jobId
parameter_list|)
block|{
name|this
operator|.
name|jobId
operator|=
name|jobId
expr_stmt|;
block|}
comment|/** @return Task ID, if known. */
DECL|method|getTaskId ()
specifier|public
name|String
name|getTaskId
parameter_list|()
block|{
return|return
name|taskId
return|;
block|}
DECL|method|setTaskId (String taskId)
specifier|public
name|void
name|setTaskId
parameter_list|(
name|String
name|taskId
parameter_list|)
block|{
name|this
operator|.
name|taskId
operator|=
name|taskId
expr_stmt|;
block|}
comment|/**    * Arbitrary notes.    * @return any notes    */
DECL|method|getText ()
specifier|public
name|String
name|getText
parameter_list|()
block|{
return|return
name|text
return|;
block|}
DECL|method|setText (String text)
specifier|public
name|void
name|setText
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
comment|/** @return ordered list of etags. */
DECL|method|getEtags ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getEtags
parameter_list|()
block|{
return|return
name|etags
return|;
block|}
DECL|method|setEtags (List<String> etags)
specifier|public
name|void
name|setEtags
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|etags
parameter_list|)
block|{
name|this
operator|.
name|etags
operator|=
name|etags
expr_stmt|;
block|}
comment|/**    * Any custom extra data committer subclasses may choose to add.    * @return custom data    */
DECL|method|getExtraData ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getExtraData
parameter_list|()
block|{
return|return
name|extraData
return|;
block|}
DECL|method|setExtraData (Map<String, String> extraData)
specifier|public
name|void
name|setExtraData
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|extraData
parameter_list|)
block|{
name|this
operator|.
name|extraData
operator|=
name|extraData
expr_stmt|;
block|}
comment|/**    * Destination file size.    * @return size of destination object    */
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
DECL|method|setLength (long length)
specifier|public
name|void
name|setLength
parameter_list|(
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
block|}
end_class

end_unit

