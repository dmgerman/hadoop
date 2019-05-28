begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.impl
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
name|impl
package|;
end_package

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
name|AtomicLong
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|AmazonServiceException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|SdkBaseException
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
name|CopyObjectRequest
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
name|GetObjectMetadataRequest
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
name|GetObjectRequest
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
name|ObjectMetadata
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
name|S3Object
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
name|transfer
operator|.
name|model
operator|.
name|CopyResult
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
name|annotations
operator|.
name|VisibleForTesting
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
name|commons
operator|.
name|lang3
operator|.
name|tuple
operator|.
name|ImmutablePair
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
name|PathIOException
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
name|NoVersionAttributeException
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
name|RemoteFileChangedException
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
name|S3ObjectAttributes
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * Change tracking for input streams: the version ID or etag of the object is  * tracked and compared on open/re-open.  An initial version ID or etag may or  * may not be available, depending on usage (e.g. if S3Guard is utilized).  *  * Self-contained for testing and use in different streams.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|ChangeTracker
specifier|public
class|class
name|ChangeTracker
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
name|ChangeTracker
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** {@code 412 Precondition Failed} (HTTP/1.1 - RFC 2616) */
DECL|field|SC_PRECONDITION_FAILED
specifier|public
specifier|static
specifier|final
name|int
name|SC_PRECONDITION_FAILED
init|=
literal|412
decl_stmt|;
DECL|field|CHANGE_REPORTED_BY_S3
specifier|public
specifier|static
specifier|final
name|String
name|CHANGE_REPORTED_BY_S3
init|=
literal|"Change reported by S3"
decl_stmt|;
comment|/** Policy to use. */
DECL|field|policy
specifier|private
specifier|final
name|ChangeDetectionPolicy
name|policy
decl_stmt|;
comment|/**    * URI of file being read.    */
DECL|field|uri
specifier|private
specifier|final
name|String
name|uri
decl_stmt|;
comment|/**    * Mismatch counter; expected to be wired up to StreamStatistics except    * during testing.    */
DECL|field|versionMismatches
specifier|private
specifier|final
name|AtomicLong
name|versionMismatches
decl_stmt|;
comment|/**    * Revision identifier (e.g. eTag or versionId, depending on change    * detection policy).    */
DECL|field|revisionId
specifier|private
name|String
name|revisionId
decl_stmt|;
comment|/**    * Create a change tracker.    * @param uri URI of object being tracked    * @param policy policy to track.    * @param versionMismatches reference to the version mismatch counter    * @param s3ObjectAttributes attributes of the object, potentially including    * an eTag or versionId to match depending on {@code policy}    */
DECL|method|ChangeTracker (final String uri, final ChangeDetectionPolicy policy, final AtomicLong versionMismatches, final S3ObjectAttributes s3ObjectAttributes)
specifier|public
name|ChangeTracker
parameter_list|(
specifier|final
name|String
name|uri
parameter_list|,
specifier|final
name|ChangeDetectionPolicy
name|policy
parameter_list|,
specifier|final
name|AtomicLong
name|versionMismatches
parameter_list|,
specifier|final
name|S3ObjectAttributes
name|s3ObjectAttributes
parameter_list|)
block|{
name|this
operator|.
name|policy
operator|=
name|checkNotNull
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
name|this
operator|.
name|versionMismatches
operator|=
name|versionMismatches
expr_stmt|;
name|this
operator|.
name|revisionId
operator|=
name|policy
operator|.
name|getRevisionId
argument_list|(
name|s3ObjectAttributes
argument_list|)
expr_stmt|;
if|if
condition|(
name|revisionId
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Revision ID for object at {}: {}"
argument_list|,
name|uri
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getRevisionId ()
specifier|public
name|String
name|getRevisionId
parameter_list|()
block|{
return|return
name|revisionId
return|;
block|}
DECL|method|getSource ()
specifier|public
name|ChangeDetectionPolicy
operator|.
name|Source
name|getSource
parameter_list|()
block|{
return|return
name|policy
operator|.
name|getSource
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getVersionMismatches ()
specifier|public
name|AtomicLong
name|getVersionMismatches
parameter_list|()
block|{
return|return
name|versionMismatches
return|;
block|}
comment|/**    * Apply any revision control set by the policy if it is to be    * enforced on the server.    * @param request request to modify    * @return true iff a constraint was added.    */
DECL|method|maybeApplyConstraint ( final GetObjectRequest request)
specifier|public
name|boolean
name|maybeApplyConstraint
parameter_list|(
specifier|final
name|GetObjectRequest
name|request
parameter_list|)
block|{
if|if
condition|(
name|policy
operator|.
name|getMode
argument_list|()
operator|==
name|ChangeDetectionPolicy
operator|.
name|Mode
operator|.
name|Server
operator|&&
name|revisionId
operator|!=
literal|null
condition|)
block|{
name|policy
operator|.
name|applyRevisionConstraint
argument_list|(
name|request
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Apply any revision control set by the policy if it is to be    * enforced on the server.    * @param request request to modify    * @return true iff a constraint was added.    */
DECL|method|maybeApplyConstraint ( final CopyObjectRequest request)
specifier|public
name|boolean
name|maybeApplyConstraint
parameter_list|(
specifier|final
name|CopyObjectRequest
name|request
parameter_list|)
block|{
if|if
condition|(
name|policy
operator|.
name|getMode
argument_list|()
operator|==
name|ChangeDetectionPolicy
operator|.
name|Mode
operator|.
name|Server
operator|&&
name|revisionId
operator|!=
literal|null
condition|)
block|{
name|policy
operator|.
name|applyRevisionConstraint
argument_list|(
name|request
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|maybeApplyConstraint ( final GetObjectMetadataRequest request)
specifier|public
name|boolean
name|maybeApplyConstraint
parameter_list|(
specifier|final
name|GetObjectMetadataRequest
name|request
parameter_list|)
block|{
if|if
condition|(
name|policy
operator|.
name|getMode
argument_list|()
operator|==
name|ChangeDetectionPolicy
operator|.
name|Mode
operator|.
name|Server
operator|&&
name|revisionId
operator|!=
literal|null
condition|)
block|{
name|policy
operator|.
name|applyRevisionConstraint
argument_list|(
name|request
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Process the response from the server for validation against the    * change policy.    * @param object object returned; may be null.    * @param operation operation in progress.    * @param pos offset of read    * @throws PathIOException raised on failure    * @throws RemoteFileChangedException if the remote file has changed.    */
DECL|method|processResponse (final S3Object object, final String operation, final long pos)
specifier|public
name|void
name|processResponse
parameter_list|(
specifier|final
name|S3Object
name|object
parameter_list|,
specifier|final
name|String
name|operation
parameter_list|,
specifier|final
name|long
name|pos
parameter_list|)
throws|throws
name|PathIOException
block|{
if|if
condition|(
name|object
operator|==
literal|null
condition|)
block|{
comment|// no object returned. Either mismatch or something odd.
if|if
condition|(
name|revisionId
operator|!=
literal|null
condition|)
block|{
comment|// the requirements of the change detection policy wasn't met: the
comment|// object was not returned.
name|versionMismatches
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RemoteFileChangedException
argument_list|(
name|uri
argument_list|,
name|operation
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|CHANGE_REPORTED_BY_S3
operator|+
literal|" during %s"
operator|+
literal|" at position %s."
operator|+
literal|" %s %s was unavailable"
argument_list|,
name|operation
argument_list|,
name|pos
argument_list|,
name|getSource
argument_list|()
argument_list|,
name|getRevisionId
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|PathIOException
argument_list|(
name|uri
argument_list|,
literal|"No data returned from GET request"
argument_list|)
throw|;
block|}
block|}
name|processMetadata
argument_list|(
name|object
operator|.
name|getObjectMetadata
argument_list|()
argument_list|,
name|operation
argument_list|)
expr_stmt|;
block|}
comment|/**    * Process the response from the server for validation against the    * change policy.    * @param copyResult result of a copy operation    * @throws PathIOException raised on failure    * @throws RemoteFileChangedException if the remote file has changed.    */
DECL|method|processResponse (final CopyResult copyResult)
specifier|public
name|void
name|processResponse
parameter_list|(
specifier|final
name|CopyResult
name|copyResult
parameter_list|)
throws|throws
name|PathIOException
block|{
comment|// ETag (sometimes, depending on encryption and/or multipart) is not the
comment|// same on the copied object as the original.  Version Id seems to never
comment|// be the same on the copy.  As such, there isn't really anything that
comment|// can be verified on the response, except that a revision ID is present
comment|// if required.
name|String
name|newRevisionId
init|=
name|policy
operator|.
name|getRevisionId
argument_list|(
name|copyResult
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Copy result {}: {}"
argument_list|,
name|policy
operator|.
name|getSource
argument_list|()
argument_list|,
name|newRevisionId
argument_list|)
expr_stmt|;
if|if
condition|(
name|newRevisionId
operator|==
literal|null
operator|&&
name|policy
operator|.
name|isRequireVersion
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoVersionAttributeException
argument_list|(
name|uri
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"Change detection policy requires %s"
argument_list|,
name|policy
operator|.
name|getSource
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**    * Process an exception generated against the change policy.    * If the exception indicates the file has changed, this method throws    * {@code RemoteFileChangedException} with the original exception as the    * cause.    * @param e the exception    * @param operation the operation performed when the exception was    * generated (e.g. "copy", "read", "select").    * @throws RemoteFileChangedException if the remote file has changed.    */
DECL|method|processException (SdkBaseException e, String operation)
specifier|public
name|void
name|processException
parameter_list|(
name|SdkBaseException
name|e
parameter_list|,
name|String
name|operation
parameter_list|)
throws|throws
name|RemoteFileChangedException
block|{
if|if
condition|(
name|e
operator|instanceof
name|AmazonServiceException
condition|)
block|{
name|AmazonServiceException
name|serviceException
init|=
operator|(
name|AmazonServiceException
operator|)
name|e
decl_stmt|;
comment|// This isn't really going to be hit due to
comment|// https://github.com/aws/aws-sdk-java/issues/1644
if|if
condition|(
name|serviceException
operator|.
name|getStatusCode
argument_list|()
operator|==
name|SC_PRECONDITION_FAILED
condition|)
block|{
name|versionMismatches
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RemoteFileChangedException
argument_list|(
name|uri
argument_list|,
name|operation
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|RemoteFileChangedException
operator|.
name|PRECONDITIONS_FAILED
operator|+
literal|" on %s."
operator|+
literal|" Version %s was unavailable"
argument_list|,
name|getSource
argument_list|()
argument_list|,
name|getRevisionId
argument_list|()
argument_list|)
argument_list|,
name|serviceException
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Process metadata response from server for validation against the change    * policy.    * @param metadata metadata returned from server    * @param operation operation in progress    * @throws PathIOException raised on failure    * @throws RemoteFileChangedException if the remote file has changed.    */
DECL|method|processMetadata (final ObjectMetadata metadata, final String operation)
specifier|public
name|void
name|processMetadata
parameter_list|(
specifier|final
name|ObjectMetadata
name|metadata
parameter_list|,
specifier|final
name|String
name|operation
parameter_list|)
throws|throws
name|PathIOException
block|{
specifier|final
name|String
name|newRevisionId
init|=
name|policy
operator|.
name|getRevisionId
argument_list|(
name|metadata
argument_list|,
name|uri
argument_list|)
decl_stmt|;
name|processNewRevision
argument_list|(
name|newRevisionId
argument_list|,
name|operation
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validate a revision from the server against our expectations.    * @param newRevisionId new revision.    * @param operation operation in progress    * @param pos offset in the file; -1 for "none"    * @throws PathIOException raised on failure    * @throws RemoteFileChangedException if the remote file has changed.    */
DECL|method|processNewRevision (final String newRevisionId, final String operation, final long pos)
specifier|private
name|void
name|processNewRevision
parameter_list|(
specifier|final
name|String
name|newRevisionId
parameter_list|,
specifier|final
name|String
name|operation
parameter_list|,
specifier|final
name|long
name|pos
parameter_list|)
throws|throws
name|PathIOException
block|{
if|if
condition|(
name|newRevisionId
operator|==
literal|null
operator|&&
name|policy
operator|.
name|isRequireVersion
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoVersionAttributeException
argument_list|(
name|uri
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"Change detection policy requires %s"
argument_list|,
name|policy
operator|.
name|getSource
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|revisionId
operator|==
literal|null
condition|)
block|{
comment|// revisionId may be null on first (re)open. Pin it so change can be
comment|// detected if object has been updated
name|LOG
operator|.
name|debug
argument_list|(
literal|"Setting revision ID for object at {}: {}"
argument_list|,
name|uri
argument_list|,
name|newRevisionId
argument_list|)
expr_stmt|;
name|revisionId
operator|=
name|newRevisionId
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|revisionId
operator|.
name|equals
argument_list|(
name|newRevisionId
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Revision ID changed from {} to {}"
argument_list|,
name|revisionId
argument_list|,
name|newRevisionId
argument_list|)
expr_stmt|;
name|ImmutablePair
argument_list|<
name|Boolean
argument_list|,
name|RemoteFileChangedException
argument_list|>
name|pair
init|=
name|policy
operator|.
name|onChangeDetected
argument_list|(
name|revisionId
argument_list|,
name|newRevisionId
argument_list|,
name|uri
argument_list|,
name|pos
argument_list|,
name|operation
argument_list|,
name|versionMismatches
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|pair
operator|.
name|left
condition|)
block|{
comment|// an mismatch has occurred: note it.
name|versionMismatches
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|pair
operator|.
name|right
operator|!=
literal|null
condition|)
block|{
comment|// there's an exception to raise: do it
throw|throw
name|pair
operator|.
name|right
throw|;
block|}
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
literal|"ChangeTracker{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"changeDetectionPolicy="
argument_list|)
operator|.
name|append
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", revisionId='"
argument_list|)
operator|.
name|append
argument_list|(
name|revisionId
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
block|}
end_class

end_unit

