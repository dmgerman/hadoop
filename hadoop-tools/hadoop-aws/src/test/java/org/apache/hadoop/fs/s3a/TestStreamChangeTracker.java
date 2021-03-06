begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
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
name|Headers
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
name|org
operator|.
name|junit
operator|.
name|Test
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
name|impl
operator|.
name|ChangeDetectionPolicy
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
name|impl
operator|.
name|ChangeTracker
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
name|test
operator|.
name|HadoopTestBase
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
name|impl
operator|.
name|ChangeDetectionPolicy
operator|.
name|CHANGE_DETECTED
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
name|impl
operator|.
name|ChangeDetectionPolicy
operator|.
name|createPolicy
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
name|impl
operator|.
name|ChangeTracker
operator|.
name|CHANGE_REPORTED_BY_S3
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
name|test
operator|.
name|LambdaTestUtils
operator|.
name|intercept
import|;
end_import

begin_comment
comment|/**  * Test {@link ChangeTracker}.  */
end_comment

begin_class
DECL|class|TestStreamChangeTracker
specifier|public
class|class
name|TestStreamChangeTracker
extends|extends
name|HadoopTestBase
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
name|TestStreamChangeTracker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BUCKET
specifier|public
specifier|static
specifier|final
name|String
name|BUCKET
init|=
literal|"bucket"
decl_stmt|;
DECL|field|OBJECT
specifier|public
specifier|static
specifier|final
name|String
name|OBJECT
init|=
literal|"object"
decl_stmt|;
DECL|field|DEST_OBJECT
specifier|public
specifier|static
specifier|final
name|String
name|DEST_OBJECT
init|=
literal|"new_object"
decl_stmt|;
DECL|field|URI
specifier|public
specifier|static
specifier|final
name|String
name|URI
init|=
literal|"s3a://"
operator|+
name|BUCKET
operator|+
literal|"/"
operator|+
name|OBJECT
decl_stmt|;
DECL|field|PATH
specifier|public
specifier|static
specifier|final
name|Path
name|PATH
init|=
operator|new
name|Path
argument_list|(
name|URI
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testVersionCheckingHandlingNoVersions ()
specifier|public
name|void
name|testVersionCheckingHandlingNoVersions
parameter_list|()
throws|throws
name|Throwable
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"If an endpoint doesn't return versions, that's OK"
argument_list|)
expr_stmt|;
name|ChangeTracker
name|tracker
init|=
name|newTracker
argument_list|(
name|ChangeDetectionPolicy
operator|.
name|Mode
operator|.
name|Client
argument_list|,
name|ChangeDetectionPolicy
operator|.
name|Source
operator|.
name|VersionId
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Tracker should not have applied contraints "
operator|+
name|tracker
argument_list|,
name|tracker
operator|.
name|maybeApplyConstraint
argument_list|(
name|newGetObjectRequest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|processResponse
argument_list|(
name|newResponse
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrackerMismatchCount
argument_list|(
name|tracker
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVersionCheckingHandlingNoVersionsVersionRequired ()
specifier|public
name|void
name|testVersionCheckingHandlingNoVersionsVersionRequired
parameter_list|()
throws|throws
name|Throwable
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"If an endpoint doesn't return versions but we are configured to"
operator|+
literal|"require them"
argument_list|)
expr_stmt|;
name|ChangeTracker
name|tracker
init|=
name|newTracker
argument_list|(
name|ChangeDetectionPolicy
operator|.
name|Mode
operator|.
name|Client
argument_list|,
name|ChangeDetectionPolicy
operator|.
name|Source
operator|.
name|VersionId
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|expectNoVersionAttributeException
argument_list|(
name|tracker
argument_list|,
name|newResponse
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"policy requires VersionId"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEtagCheckingWarn ()
specifier|public
name|void
name|testEtagCheckingWarn
parameter_list|()
throws|throws
name|Throwable
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"If an endpoint doesn't return errors, that's OK"
argument_list|)
expr_stmt|;
name|ChangeTracker
name|tracker
init|=
name|newTracker
argument_list|(
name|ChangeDetectionPolicy
operator|.
name|Mode
operator|.
name|Warn
argument_list|,
name|ChangeDetectionPolicy
operator|.
name|Source
operator|.
name|ETag
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Tracker should not have applied constraints "
operator|+
name|tracker
argument_list|,
name|tracker
operator|.
name|maybeApplyConstraint
argument_list|(
name|newGetObjectRequest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|processResponse
argument_list|(
name|newResponse
argument_list|(
literal|"e1"
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|processResponse
argument_list|(
name|newResponse
argument_list|(
literal|"e1"
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|processResponse
argument_list|(
name|newResponse
argument_list|(
literal|"e2"
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrackerMismatchCount
argument_list|(
name|tracker
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// subsequent error triggers doesn't trigger another warning
name|tracker
operator|.
name|processResponse
argument_list|(
name|newResponse
argument_list|(
literal|"e2"
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrackerMismatchCount
argument_list|(
name|tracker
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVersionCheckingOnClient ()
specifier|public
name|void
name|testVersionCheckingOnClient
parameter_list|()
throws|throws
name|Throwable
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Verify the client-side version checker raises exceptions"
argument_list|)
expr_stmt|;
name|ChangeTracker
name|tracker
init|=
name|newTracker
argument_list|(
name|ChangeDetectionPolicy
operator|.
name|Mode
operator|.
name|Client
argument_list|,
name|ChangeDetectionPolicy
operator|.
name|Source
operator|.
name|VersionId
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Tracker should not have applied constraints "
operator|+
name|tracker
argument_list|,
name|tracker
operator|.
name|maybeApplyConstraint
argument_list|(
name|newGetObjectRequest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|processResponse
argument_list|(
name|newResponse
argument_list|(
literal|null
argument_list|,
literal|"rev1"
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrackerMismatchCount
argument_list|(
name|tracker
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertRevisionId
argument_list|(
name|tracker
argument_list|,
literal|"rev1"
argument_list|)
expr_stmt|;
name|GetObjectRequest
name|request
init|=
name|newGetObjectRequest
argument_list|()
decl_stmt|;
name|expectChangeException
argument_list|(
name|tracker
argument_list|,
name|newResponse
argument_list|(
literal|null
argument_list|,
literal|"rev2"
argument_list|)
argument_list|,
literal|"change detected"
argument_list|)
expr_stmt|;
comment|// mismatch was noted (so gets to FS stats)
name|assertTrackerMismatchCount
argument_list|(
name|tracker
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// another read causes another exception
name|expectChangeException
argument_list|(
name|tracker
argument_list|,
name|newResponse
argument_list|(
literal|null
argument_list|,
literal|"rev2"
argument_list|)
argument_list|,
literal|"change detected"
argument_list|)
expr_stmt|;
comment|// mismatch was noted again
name|assertTrackerMismatchCount
argument_list|(
name|tracker
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVersionCheckingOnServer ()
specifier|public
name|void
name|testVersionCheckingOnServer
parameter_list|()
throws|throws
name|Throwable
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Verify the client-side version checker handles null-ness"
argument_list|)
expr_stmt|;
name|ChangeTracker
name|tracker
init|=
name|newTracker
argument_list|(
name|ChangeDetectionPolicy
operator|.
name|Mode
operator|.
name|Server
argument_list|,
name|ChangeDetectionPolicy
operator|.
name|Source
operator|.
name|VersionId
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Tracker should not have applied contraints "
operator|+
name|tracker
argument_list|,
name|tracker
operator|.
name|maybeApplyConstraint
argument_list|(
name|newGetObjectRequest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|processResponse
argument_list|(
name|newResponse
argument_list|(
literal|null
argument_list|,
literal|"rev1"
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrackerMismatchCount
argument_list|(
name|tracker
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertRevisionId
argument_list|(
name|tracker
argument_list|,
literal|"rev1"
argument_list|)
expr_stmt|;
name|GetObjectRequest
name|request
init|=
name|newGetObjectRequest
argument_list|()
decl_stmt|;
name|assertConstraintApplied
argument_list|(
name|tracker
argument_list|,
name|request
argument_list|)
expr_stmt|;
comment|// now, the tracker expects a null response
name|expectChangeException
argument_list|(
name|tracker
argument_list|,
literal|null
argument_list|,
name|CHANGE_REPORTED_BY_S3
argument_list|)
expr_stmt|;
name|assertTrackerMismatchCount
argument_list|(
name|tracker
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// now, imagine the server doesn't trigger a failure due to some
comment|// bug in its logic
comment|// we should still react to the reported value
name|expectChangeException
argument_list|(
name|tracker
argument_list|,
name|newResponse
argument_list|(
literal|null
argument_list|,
literal|"rev2"
argument_list|)
argument_list|,
name|CHANGE_DETECTED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVersionCheckingUpfrontETag ()
specifier|public
name|void
name|testVersionCheckingUpfrontETag
parameter_list|()
throws|throws
name|Throwable
block|{
name|ChangeTracker
name|tracker
init|=
name|newTracker
argument_list|(
name|ChangeDetectionPolicy
operator|.
name|Mode
operator|.
name|Server
argument_list|,
name|ChangeDetectionPolicy
operator|.
name|Source
operator|.
name|ETag
argument_list|,
literal|false
argument_list|,
name|objectAttributes
argument_list|(
literal|"etag1"
argument_list|,
literal|"versionid1"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"etag1"
argument_list|,
name|tracker
operator|.
name|getRevisionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVersionCheckingUpfrontVersionId ()
specifier|public
name|void
name|testVersionCheckingUpfrontVersionId
parameter_list|()
throws|throws
name|Throwable
block|{
name|ChangeTracker
name|tracker
init|=
name|newTracker
argument_list|(
name|ChangeDetectionPolicy
operator|.
name|Mode
operator|.
name|Server
argument_list|,
name|ChangeDetectionPolicy
operator|.
name|Source
operator|.
name|VersionId
argument_list|,
literal|false
argument_list|,
name|objectAttributes
argument_list|(
literal|"etag1"
argument_list|,
literal|"versionid1"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"versionid1"
argument_list|,
name|tracker
operator|.
name|getRevisionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVersionCheckingETagCopyServer ()
specifier|public
name|void
name|testVersionCheckingETagCopyServer
parameter_list|()
throws|throws
name|Throwable
block|{
name|ChangeTracker
name|tracker
init|=
name|newTracker
argument_list|(
name|ChangeDetectionPolicy
operator|.
name|Mode
operator|.
name|Server
argument_list|,
name|ChangeDetectionPolicy
operator|.
name|Source
operator|.
name|VersionId
argument_list|,
literal|false
argument_list|,
name|objectAttributes
argument_list|(
literal|"etag1"
argument_list|,
literal|"versionid1"
argument_list|)
argument_list|)
decl_stmt|;
name|assertConstraintApplied
argument_list|(
name|tracker
argument_list|,
name|newCopyObjectRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVersionCheckingETagCopyClient ()
specifier|public
name|void
name|testVersionCheckingETagCopyClient
parameter_list|()
throws|throws
name|Throwable
block|{
name|ChangeTracker
name|tracker
init|=
name|newTracker
argument_list|(
name|ChangeDetectionPolicy
operator|.
name|Mode
operator|.
name|Client
argument_list|,
name|ChangeDetectionPolicy
operator|.
name|Source
operator|.
name|VersionId
argument_list|,
literal|false
argument_list|,
name|objectAttributes
argument_list|(
literal|"etag1"
argument_list|,
literal|"versionid1"
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Tracker should not have applied contraints "
operator|+
name|tracker
argument_list|,
name|tracker
operator|.
name|maybeApplyConstraint
argument_list|(
name|newCopyObjectRequest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCopyVersionIdRequired ()
specifier|public
name|void
name|testCopyVersionIdRequired
parameter_list|()
throws|throws
name|Throwable
block|{
name|ChangeTracker
name|tracker
init|=
name|newTracker
argument_list|(
name|ChangeDetectionPolicy
operator|.
name|Mode
operator|.
name|Client
argument_list|,
name|ChangeDetectionPolicy
operator|.
name|Source
operator|.
name|VersionId
argument_list|,
literal|true
argument_list|,
name|objectAttributes
argument_list|(
literal|"etag1"
argument_list|,
literal|"versionId"
argument_list|)
argument_list|)
decl_stmt|;
name|expectNoVersionAttributeException
argument_list|(
name|tracker
argument_list|,
name|newCopyResult
argument_list|(
literal|"etag1"
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"policy requires VersionId"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCopyETagRequired ()
specifier|public
name|void
name|testCopyETagRequired
parameter_list|()
throws|throws
name|Throwable
block|{
name|ChangeTracker
name|tracker
init|=
name|newTracker
argument_list|(
name|ChangeDetectionPolicy
operator|.
name|Mode
operator|.
name|Client
argument_list|,
name|ChangeDetectionPolicy
operator|.
name|Source
operator|.
name|ETag
argument_list|,
literal|true
argument_list|,
name|objectAttributes
argument_list|(
literal|"etag1"
argument_list|,
literal|"versionId"
argument_list|)
argument_list|)
decl_stmt|;
name|expectNoVersionAttributeException
argument_list|(
name|tracker
argument_list|,
name|newCopyResult
argument_list|(
literal|null
argument_list|,
literal|"versionId"
argument_list|)
argument_list|,
literal|"policy requires ETag"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCopyVersionMismatch ()
specifier|public
name|void
name|testCopyVersionMismatch
parameter_list|()
throws|throws
name|Throwable
block|{
name|ChangeTracker
name|tracker
init|=
name|newTracker
argument_list|(
name|ChangeDetectionPolicy
operator|.
name|Mode
operator|.
name|Server
argument_list|,
name|ChangeDetectionPolicy
operator|.
name|Source
operator|.
name|ETag
argument_list|,
literal|true
argument_list|,
name|objectAttributes
argument_list|(
literal|"etag"
argument_list|,
literal|"versionId"
argument_list|)
argument_list|)
decl_stmt|;
comment|// 412 is translated to RemoteFileChangedException
comment|// note: this scenario is never currently hit due to
comment|// https://github.com/aws/aws-sdk-java/issues/1644
name|AmazonServiceException
name|awsException
init|=
operator|new
name|AmazonServiceException
argument_list|(
literal|"aws exception"
argument_list|)
decl_stmt|;
name|awsException
operator|.
name|setStatusCode
argument_list|(
name|ChangeTracker
operator|.
name|SC_PRECONDITION_FAILED
argument_list|)
expr_stmt|;
name|expectChangeException
argument_list|(
name|tracker
argument_list|,
name|awsException
argument_list|,
literal|"copy"
argument_list|,
name|RemoteFileChangedException
operator|.
name|PRECONDITIONS_FAILED
argument_list|)
expr_stmt|;
comment|// processing another type of exception does nothing
name|tracker
operator|.
name|processException
argument_list|(
operator|new
name|SdkBaseException
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"copy"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertConstraintApplied (final ChangeTracker tracker, final GetObjectRequest request)
specifier|protected
name|void
name|assertConstraintApplied
parameter_list|(
specifier|final
name|ChangeTracker
name|tracker
parameter_list|,
specifier|final
name|GetObjectRequest
name|request
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Tracker should have applied contraints "
operator|+
name|tracker
argument_list|,
name|tracker
operator|.
name|maybeApplyConstraint
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertConstraintApplied (final ChangeTracker tracker, final CopyObjectRequest request)
specifier|protected
name|void
name|assertConstraintApplied
parameter_list|(
specifier|final
name|ChangeTracker
name|tracker
parameter_list|,
specifier|final
name|CopyObjectRequest
name|request
parameter_list|)
throws|throws
name|PathIOException
block|{
name|assertTrue
argument_list|(
literal|"Tracker should have applied contraints "
operator|+
name|tracker
argument_list|,
name|tracker
operator|.
name|maybeApplyConstraint
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|expectChangeException ( final ChangeTracker tracker, final S3Object response, final String message)
specifier|protected
name|RemoteFileChangedException
name|expectChangeException
parameter_list|(
specifier|final
name|ChangeTracker
name|tracker
parameter_list|,
specifier|final
name|S3Object
name|response
parameter_list|,
specifier|final
name|String
name|message
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|expectException
argument_list|(
name|tracker
argument_list|,
name|response
argument_list|,
name|message
argument_list|,
name|RemoteFileChangedException
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|expectChangeException ( final ChangeTracker tracker, final SdkBaseException exception, final String operation, final String message)
specifier|protected
name|RemoteFileChangedException
name|expectChangeException
parameter_list|(
specifier|final
name|ChangeTracker
name|tracker
parameter_list|,
specifier|final
name|SdkBaseException
name|exception
parameter_list|,
specifier|final
name|String
name|operation
parameter_list|,
specifier|final
name|String
name|message
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|expectException
argument_list|(
name|tracker
argument_list|,
name|exception
argument_list|,
name|operation
argument_list|,
name|message
argument_list|,
name|RemoteFileChangedException
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|expectNoVersionAttributeException ( final ChangeTracker tracker, final S3Object response, final String message)
specifier|protected
name|PathIOException
name|expectNoVersionAttributeException
parameter_list|(
specifier|final
name|ChangeTracker
name|tracker
parameter_list|,
specifier|final
name|S3Object
name|response
parameter_list|,
specifier|final
name|String
name|message
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|expectException
argument_list|(
name|tracker
argument_list|,
name|response
argument_list|,
name|message
argument_list|,
name|NoVersionAttributeException
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|expectNoVersionAttributeException ( final ChangeTracker tracker, final CopyResult response, final String message)
specifier|protected
name|PathIOException
name|expectNoVersionAttributeException
parameter_list|(
specifier|final
name|ChangeTracker
name|tracker
parameter_list|,
specifier|final
name|CopyResult
name|response
parameter_list|,
specifier|final
name|String
name|message
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|expectException
argument_list|(
name|tracker
argument_list|,
name|response
argument_list|,
name|message
argument_list|,
name|NoVersionAttributeException
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|expectException ( final ChangeTracker tracker, final S3Object response, final String message, final Class<T> clazz)
specifier|protected
parameter_list|<
name|T
extends|extends
name|Exception
parameter_list|>
name|T
name|expectException
parameter_list|(
specifier|final
name|ChangeTracker
name|tracker
parameter_list|,
specifier|final
name|S3Object
name|response
parameter_list|,
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|intercept
argument_list|(
name|clazz
argument_list|,
name|message
argument_list|,
parameter_list|()
lambda|->
block|{
name|tracker
operator|.
name|processResponse
argument_list|(
name|response
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|tracker
return|;
block|}
argument_list|)
return|;
block|}
DECL|method|expectException ( final ChangeTracker tracker, final CopyResult response, final String message, final Class<T> clazz)
specifier|protected
parameter_list|<
name|T
extends|extends
name|Exception
parameter_list|>
name|T
name|expectException
parameter_list|(
specifier|final
name|ChangeTracker
name|tracker
parameter_list|,
specifier|final
name|CopyResult
name|response
parameter_list|,
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|intercept
argument_list|(
name|clazz
argument_list|,
name|message
argument_list|,
parameter_list|()
lambda|->
block|{
name|tracker
operator|.
name|processResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
return|return
name|tracker
return|;
block|}
argument_list|)
return|;
block|}
DECL|method|expectException ( final ChangeTracker tracker, final SdkBaseException exception, final String operation, final String message, final Class<T> clazz)
specifier|protected
parameter_list|<
name|T
extends|extends
name|Exception
parameter_list|>
name|T
name|expectException
parameter_list|(
specifier|final
name|ChangeTracker
name|tracker
parameter_list|,
specifier|final
name|SdkBaseException
name|exception
parameter_list|,
specifier|final
name|String
name|operation
parameter_list|,
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|intercept
argument_list|(
name|clazz
argument_list|,
name|message
argument_list|,
parameter_list|()
lambda|->
block|{
name|tracker
operator|.
name|processException
argument_list|(
name|exception
argument_list|,
name|operation
argument_list|)
expr_stmt|;
return|return
name|tracker
return|;
block|}
argument_list|)
return|;
block|}
DECL|method|assertRevisionId (final ChangeTracker tracker, final String revId)
specifier|protected
name|void
name|assertRevisionId
parameter_list|(
specifier|final
name|ChangeTracker
name|tracker
parameter_list|,
specifier|final
name|String
name|revId
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Wrong revision ID in "
operator|+
name|tracker
argument_list|,
name|revId
argument_list|,
name|tracker
operator|.
name|getRevisionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertTrackerMismatchCount ( final ChangeTracker tracker, final int expectedCount)
specifier|protected
name|void
name|assertTrackerMismatchCount
parameter_list|(
specifier|final
name|ChangeTracker
name|tracker
parameter_list|,
specifier|final
name|int
name|expectedCount
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"counter in tracker "
operator|+
name|tracker
argument_list|,
name|expectedCount
argument_list|,
name|tracker
operator|.
name|getVersionMismatches
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create tracker.    * Contains standard assertions(s).    * @return the tracker.    */
DECL|method|newTracker (final ChangeDetectionPolicy.Mode mode, final ChangeDetectionPolicy.Source source, boolean requireVersion)
specifier|protected
name|ChangeTracker
name|newTracker
parameter_list|(
specifier|final
name|ChangeDetectionPolicy
operator|.
name|Mode
name|mode
parameter_list|,
specifier|final
name|ChangeDetectionPolicy
operator|.
name|Source
name|source
parameter_list|,
name|boolean
name|requireVersion
parameter_list|)
block|{
return|return
name|newTracker
argument_list|(
name|mode
argument_list|,
name|source
argument_list|,
name|requireVersion
argument_list|,
name|objectAttributes
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Create tracker.    * Contains standard assertions(s).    * @return the tracker.    */
DECL|method|newTracker (final ChangeDetectionPolicy.Mode mode, final ChangeDetectionPolicy.Source source, boolean requireVersion, S3ObjectAttributes objectAttributes)
specifier|protected
name|ChangeTracker
name|newTracker
parameter_list|(
specifier|final
name|ChangeDetectionPolicy
operator|.
name|Mode
name|mode
parameter_list|,
specifier|final
name|ChangeDetectionPolicy
operator|.
name|Source
name|source
parameter_list|,
name|boolean
name|requireVersion
parameter_list|,
name|S3ObjectAttributes
name|objectAttributes
parameter_list|)
block|{
name|ChangeDetectionPolicy
name|policy
init|=
name|createPolicy
argument_list|(
name|mode
argument_list|,
name|source
argument_list|,
name|requireVersion
argument_list|)
decl_stmt|;
name|ChangeTracker
name|tracker
init|=
operator|new
name|ChangeTracker
argument_list|(
name|URI
argument_list|,
name|policy
argument_list|,
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
argument_list|,
name|objectAttributes
argument_list|)
decl_stmt|;
if|if
condition|(
name|objectAttributes
operator|.
name|getVersionId
argument_list|()
operator|==
literal|null
operator|&&
name|objectAttributes
operator|.
name|getETag
argument_list|()
operator|==
literal|null
condition|)
block|{
name|assertFalse
argument_list|(
literal|"Tracker should not have applied constraints "
operator|+
name|tracker
argument_list|,
name|tracker
operator|.
name|maybeApplyConstraint
argument_list|(
name|newGetObjectRequest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|tracker
return|;
block|}
DECL|method|newGetObjectRequest ()
specifier|private
name|GetObjectRequest
name|newGetObjectRequest
parameter_list|()
block|{
return|return
operator|new
name|GetObjectRequest
argument_list|(
name|BUCKET
argument_list|,
name|OBJECT
argument_list|)
return|;
block|}
DECL|method|newCopyObjectRequest ()
specifier|private
name|CopyObjectRequest
name|newCopyObjectRequest
parameter_list|()
block|{
return|return
operator|new
name|CopyObjectRequest
argument_list|(
name|BUCKET
argument_list|,
name|OBJECT
argument_list|,
name|BUCKET
argument_list|,
name|DEST_OBJECT
argument_list|)
return|;
block|}
DECL|method|newCopyResult (String eTag, String versionId)
specifier|private
name|CopyResult
name|newCopyResult
parameter_list|(
name|String
name|eTag
parameter_list|,
name|String
name|versionId
parameter_list|)
block|{
name|CopyResult
name|copyResult
init|=
operator|new
name|CopyResult
argument_list|()
decl_stmt|;
name|copyResult
operator|.
name|setSourceBucketName
argument_list|(
name|BUCKET
argument_list|)
expr_stmt|;
name|copyResult
operator|.
name|setSourceKey
argument_list|(
name|OBJECT
argument_list|)
expr_stmt|;
name|copyResult
operator|.
name|setDestinationBucketName
argument_list|(
name|BUCKET
argument_list|)
expr_stmt|;
name|copyResult
operator|.
name|setDestinationKey
argument_list|(
name|DEST_OBJECT
argument_list|)
expr_stmt|;
name|copyResult
operator|.
name|setETag
argument_list|(
name|eTag
argument_list|)
expr_stmt|;
name|copyResult
operator|.
name|setVersionId
argument_list|(
name|versionId
argument_list|)
expr_stmt|;
return|return
name|copyResult
return|;
block|}
DECL|method|newResponse (String etag, String versionId)
specifier|private
name|S3Object
name|newResponse
parameter_list|(
name|String
name|etag
parameter_list|,
name|String
name|versionId
parameter_list|)
block|{
name|ObjectMetadata
name|md
init|=
operator|new
name|ObjectMetadata
argument_list|()
decl_stmt|;
if|if
condition|(
name|etag
operator|!=
literal|null
condition|)
block|{
name|md
operator|.
name|setHeader
argument_list|(
name|Headers
operator|.
name|ETAG
argument_list|,
name|etag
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|versionId
operator|!=
literal|null
condition|)
block|{
name|md
operator|.
name|setHeader
argument_list|(
name|Headers
operator|.
name|S3_VERSION_ID
argument_list|,
name|versionId
argument_list|)
expr_stmt|;
block|}
name|S3Object
name|response
init|=
name|emptyResponse
argument_list|()
decl_stmt|;
name|response
operator|.
name|setObjectMetadata
argument_list|(
name|md
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
DECL|method|emptyResponse ()
specifier|private
name|S3Object
name|emptyResponse
parameter_list|()
block|{
name|S3Object
name|response
init|=
operator|new
name|S3Object
argument_list|()
decl_stmt|;
name|response
operator|.
name|setBucketName
argument_list|(
name|BUCKET
argument_list|)
expr_stmt|;
name|response
operator|.
name|setKey
argument_list|(
name|OBJECT
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
DECL|method|objectAttributes ( String etag, String versionId)
specifier|private
name|S3ObjectAttributes
name|objectAttributes
parameter_list|(
name|String
name|etag
parameter_list|,
name|String
name|versionId
parameter_list|)
block|{
return|return
operator|new
name|S3ObjectAttributes
argument_list|(
name|BUCKET
argument_list|,
name|PATH
argument_list|,
name|OBJECT
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|etag
argument_list|,
name|versionId
argument_list|,
literal|0
argument_list|)
return|;
block|}
block|}
end_class

end_unit

