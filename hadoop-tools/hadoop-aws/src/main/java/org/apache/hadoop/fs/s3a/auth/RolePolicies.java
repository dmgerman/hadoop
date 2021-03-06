begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.auth
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
name|auth
package|;
end_package

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
name|Arrays
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
name|List
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
name|Lists
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
name|auth
operator|.
name|RoleModel
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Operations, statements and policies covering the operations  * needed to work with S3 and S3Guard.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
literal|"Tests"
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|RolePolicies
specifier|public
specifier|final
class|class
name|RolePolicies
block|{
DECL|method|RolePolicies ()
specifier|private
name|RolePolicies
parameter_list|()
block|{   }
comment|/** All KMS operations: {@value}.*/
DECL|field|KMS_ALL_OPERATIONS
specifier|public
specifier|static
specifier|final
name|String
name|KMS_ALL_OPERATIONS
init|=
literal|"kms:*"
decl_stmt|;
comment|/** KMS encryption. This is<i>Not</i> used by SSE-KMS: {@value}. */
DECL|field|KMS_ENCRYPT
specifier|public
specifier|static
specifier|final
name|String
name|KMS_ENCRYPT
init|=
literal|"kms:Encrypt"
decl_stmt|;
comment|/**    * Decrypt data encrypted with SSE-KMS: {@value}.    */
DECL|field|KMS_DECRYPT
specifier|public
specifier|static
specifier|final
name|String
name|KMS_DECRYPT
init|=
literal|"kms:Decrypt"
decl_stmt|;
comment|/**    * Arn for all KMS keys: {@value}.    */
DECL|field|KMS_ALL_KEYS
specifier|public
specifier|static
specifier|final
name|String
name|KMS_ALL_KEYS
init|=
literal|"*"
decl_stmt|;
comment|/**    * This is used by S3 to generate a per-object encryption key and    * the encrypted value of this, the latter being what it tags    * the object with for later decryption: {@value}.    */
DECL|field|KMS_GENERATE_DATA_KEY
specifier|public
specifier|static
specifier|final
name|String
name|KMS_GENERATE_DATA_KEY
init|=
literal|"kms:GenerateDataKey"
decl_stmt|;
comment|/**    * Actions needed to read and write SSE-KMS data.    */
DECL|field|KMS_KEY_RW
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|KMS_KEY_RW
init|=
operator|new
name|String
index|[]
block|{
name|KMS_DECRYPT
block|,
name|KMS_GENERATE_DATA_KEY
block|,
name|KMS_ENCRYPT
block|}
decl_stmt|;
comment|/**    * Actions needed to read SSE-KMS data.    */
DECL|field|KMS_KEY_READ
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|KMS_KEY_READ
init|=
operator|new
name|String
index|[]
block|{
name|KMS_DECRYPT
block|}
decl_stmt|;
comment|/**    * Statement to allow KMS R/W access access, so full use of    * SSE-KMS.    */
DECL|field|STATEMENT_ALLOW_SSE_KMS_RW
specifier|public
specifier|static
specifier|final
name|Statement
name|STATEMENT_ALLOW_SSE_KMS_RW
init|=
name|statement
argument_list|(
literal|true
argument_list|,
name|KMS_ALL_KEYS
argument_list|,
name|KMS_ALL_OPERATIONS
argument_list|)
decl_stmt|;
comment|/**    * Statement to allow read access to KMS keys, so the ability    * to read SSE-KMS data,, but not decrypt it.    */
DECL|field|STATEMENT_ALLOW_SSE_KMS_READ
specifier|public
specifier|static
specifier|final
name|Statement
name|STATEMENT_ALLOW_SSE_KMS_READ
init|=
name|statement
argument_list|(
literal|true
argument_list|,
name|KMS_ALL_KEYS
argument_list|,
name|KMS_KEY_READ
argument_list|)
decl_stmt|;
comment|/**    * All S3 operations: {@value}.    */
DECL|field|S3_ALL_OPERATIONS
specifier|public
specifier|static
specifier|final
name|String
name|S3_ALL_OPERATIONS
init|=
literal|"s3:*"
decl_stmt|;
comment|/**    * All S3 buckets: {@value}.    */
DECL|field|S3_ALL_BUCKETS
specifier|public
specifier|static
specifier|final
name|String
name|S3_ALL_BUCKETS
init|=
literal|"arn:aws:s3:::*"
decl_stmt|;
comment|/**    * All bucket list operations, including    * {@link #S3_BUCKET_LIST_BUCKET} and    * {@link #S3_BUCKET_LIST_MULTIPART_UPLOADS}.    */
DECL|field|S3_BUCKET_ALL_LIST
specifier|public
specifier|static
specifier|final
name|String
name|S3_BUCKET_ALL_LIST
init|=
literal|"s3:ListBucket*"
decl_stmt|;
comment|/**    * List the contents of a bucket.    * It applies to a bucket, not to a path in a bucket.    */
DECL|field|S3_BUCKET_LIST_BUCKET
specifier|public
specifier|static
specifier|final
name|String
name|S3_BUCKET_LIST_BUCKET
init|=
literal|"s3:ListBucket"
decl_stmt|;
comment|/**    * This is used by the abort operation in S3A commit work.    * It applies to a bucket, not to a path in a bucket.    */
DECL|field|S3_BUCKET_LIST_MULTIPART_UPLOADS
specifier|public
specifier|static
specifier|final
name|String
name|S3_BUCKET_LIST_MULTIPART_UPLOADS
init|=
literal|"s3:ListBucketMultipartUploads"
decl_stmt|;
comment|/**    * List multipart upload is needed for the S3A Commit protocols.    * It applies to a path in a bucket.    */
DECL|field|S3_LIST_MULTIPART_UPLOAD_PARTS
specifier|public
specifier|static
specifier|final
name|String
name|S3_LIST_MULTIPART_UPLOAD_PARTS
init|=
literal|"s3:ListMultipartUploadParts"
decl_stmt|;
comment|/**    * Abort multipart upload is needed for the S3A Commit protocols.    * It applies to a path in a bucket.    */
DECL|field|S3_ABORT_MULTIPART_UPLOAD
specifier|public
specifier|static
specifier|final
name|String
name|S3_ABORT_MULTIPART_UPLOAD
init|=
literal|"s3:AbortMultipartUpload"
decl_stmt|;
comment|/**    * All s3:Delete* operations.    */
DECL|field|S3_ALL_DELETE
specifier|public
specifier|static
specifier|final
name|String
name|S3_ALL_DELETE
init|=
literal|"s3:Delete*"
decl_stmt|;
DECL|field|S3_DELETE_OBJECT
specifier|public
specifier|static
specifier|final
name|String
name|S3_DELETE_OBJECT
init|=
literal|"s3:DeleteObject"
decl_stmt|;
DECL|field|S3_DELETE_OBJECT_TAGGING
specifier|public
specifier|static
specifier|final
name|String
name|S3_DELETE_OBJECT_TAGGING
init|=
literal|"s3:DeleteObjectTagging"
decl_stmt|;
DECL|field|S3_DELETE_OBJECT_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|S3_DELETE_OBJECT_VERSION
init|=
literal|"s3:DeleteObjectVersion"
decl_stmt|;
DECL|field|S3_DELETE_OBJECT_VERSION_TAGGING
specifier|public
specifier|static
specifier|final
name|String
name|S3_DELETE_OBJECT_VERSION_TAGGING
init|=
literal|"s3:DeleteObjectVersionTagging"
decl_stmt|;
comment|/**    * All s3:Get* operations.    */
DECL|field|S3_ALL_GET
specifier|public
specifier|static
specifier|final
name|String
name|S3_ALL_GET
init|=
literal|"s3:Get*"
decl_stmt|;
DECL|field|S3_GET_OBJECT
specifier|public
specifier|static
specifier|final
name|String
name|S3_GET_OBJECT
init|=
literal|"s3:GetObject"
decl_stmt|;
DECL|field|S3_GET_OBJECT_ACL
specifier|public
specifier|static
specifier|final
name|String
name|S3_GET_OBJECT_ACL
init|=
literal|"s3:GetObjectAcl"
decl_stmt|;
DECL|field|S3_GET_OBJECT_TAGGING
specifier|public
specifier|static
specifier|final
name|String
name|S3_GET_OBJECT_TAGGING
init|=
literal|"s3:GetObjectTagging"
decl_stmt|;
DECL|field|S3_GET_OBJECT_TORRENT
specifier|public
specifier|static
specifier|final
name|String
name|S3_GET_OBJECT_TORRENT
init|=
literal|"s3:GetObjectTorrent"
decl_stmt|;
DECL|field|S3_GET_OBJECT_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|S3_GET_OBJECT_VERSION
init|=
literal|"s3:GetObjectVersion"
decl_stmt|;
DECL|field|S3_GET_BUCKET_LOCATION
specifier|public
specifier|static
specifier|final
name|String
name|S3_GET_BUCKET_LOCATION
init|=
literal|"s3:GetBucketLocation"
decl_stmt|;
DECL|field|S3_GET_OBJECT_VERSION_ACL
specifier|public
specifier|static
specifier|final
name|String
name|S3_GET_OBJECT_VERSION_ACL
init|=
literal|"s3:GetObjectVersionAcl"
decl_stmt|;
DECL|field|S3_GET_OBJECT_VERSION_TAGGING
specifier|public
specifier|static
specifier|final
name|String
name|S3_GET_OBJECT_VERSION_TAGGING
init|=
literal|"s3:GetObjectVersionTagging"
decl_stmt|;
DECL|field|S3_GET_OBJECT_VERSION_TORRENT
specifier|public
specifier|static
specifier|final
name|String
name|S3_GET_OBJECT_VERSION_TORRENT
init|=
literal|"s3:GetObjectVersionTorrent"
decl_stmt|;
comment|/**    * S3 Put*.    * This covers single an multipart uploads, but not list/abort of the latter.    */
DECL|field|S3_ALL_PUT
specifier|public
specifier|static
specifier|final
name|String
name|S3_ALL_PUT
init|=
literal|"s3:Put*"
decl_stmt|;
DECL|field|S3_PUT_OBJECT
specifier|public
specifier|static
specifier|final
name|String
name|S3_PUT_OBJECT
init|=
literal|"s3:PutObject"
decl_stmt|;
DECL|field|S3_PUT_OBJECT_ACL
specifier|public
specifier|static
specifier|final
name|String
name|S3_PUT_OBJECT_ACL
init|=
literal|"s3:PutObjectAcl"
decl_stmt|;
DECL|field|S3_PUT_OBJECT_TAGGING
specifier|public
specifier|static
specifier|final
name|String
name|S3_PUT_OBJECT_TAGGING
init|=
literal|"s3:PutObjectTagging"
decl_stmt|;
DECL|field|S3_PUT_OBJECT_VERSION_ACL
specifier|public
specifier|static
specifier|final
name|String
name|S3_PUT_OBJECT_VERSION_ACL
init|=
literal|"s3:PutObjectVersionAcl"
decl_stmt|;
DECL|field|S3_PUT_OBJECT_VERSION_TAGGING
specifier|public
specifier|static
specifier|final
name|String
name|S3_PUT_OBJECT_VERSION_TAGGING
init|=
literal|"s3:PutObjectVersionTagging"
decl_stmt|;
DECL|field|S3_RESTORE_OBJECT
specifier|public
specifier|static
specifier|final
name|String
name|S3_RESTORE_OBJECT
init|=
literal|"s3:RestoreObject"
decl_stmt|;
comment|/**    * Actions needed to read a file in S3 through S3A, excluding    * S3Guard and SSE-KMS.    */
DECL|field|S3_PATH_READ_OPERATIONS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|S3_PATH_READ_OPERATIONS
init|=
operator|new
name|String
index|[]
block|{
name|S3_GET_OBJECT
block|,       }
decl_stmt|;
comment|/**    * Base actions needed to read data from S3 through S3A,    * excluding:    *<ol>    *<li>bucket-level operations</li>    *<li>SSE-KMS key operations</li>    *<li>DynamoDB operations for S3Guard.</li>    *</ol>    * As this excludes the bucket list operations, it is not sufficient    * to read from a bucket on its own.    */
DECL|field|S3_ROOT_READ_OPERATIONS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|S3_ROOT_READ_OPERATIONS
init|=
operator|new
name|String
index|[]
block|{
name|S3_ALL_GET
block|,       }
decl_stmt|;
DECL|field|S3_ROOT_READ_OPERATIONS_LIST
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|S3_ROOT_READ_OPERATIONS_LIST
init|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|S3_ALL_GET
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * Policies which can be applied to bucket resources for read operations.    *<ol>    *<li>SSE-KMS key operations</li>    *<li>DynamoDB operations for S3Guard.</li>    *</ol>    */
DECL|field|S3_BUCKET_READ_OPERATIONS
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|S3_BUCKET_READ_OPERATIONS
init|=
operator|new
name|String
index|[]
block|{
name|S3_ALL_GET
block|,
name|S3_BUCKET_ALL_LIST
block|,       }
decl_stmt|;
comment|/**    * Actions needed to write data to an S3A Path.    * This includes the appropriate read operations, but    * not SSE-KMS or S3Guard support.    */
DECL|field|S3_PATH_RW_OPERATIONS
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|S3_PATH_RW_OPERATIONS
init|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
name|S3_ALL_GET
block|,
name|S3_PUT_OBJECT
block|,
name|S3_DELETE_OBJECT
block|,
name|S3_ABORT_MULTIPART_UPLOAD
block|,       }
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * Actions needed to write data to an S3A Path.    * This is purely the extra operations needed for writing atop    * of the read operation set.    * Deny these and a path is still readable, but not writeable.    * Excludes: bucket-ARN, SSE-KMS and S3Guard permissions.    */
DECL|field|S3_PATH_WRITE_OPERATIONS
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|S3_PATH_WRITE_OPERATIONS
init|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
name|S3_PUT_OBJECT
block|,
name|S3_DELETE_OBJECT
block|,
name|S3_ABORT_MULTIPART_UPLOAD
block|}
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * Actions needed for R/W IO from the root of a bucket.    * Excludes: bucket-ARN, SSE-KMS and S3Guard permissions.    */
DECL|field|S3_ROOT_RW_OPERATIONS
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|S3_ROOT_RW_OPERATIONS
init|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
name|S3_ALL_GET
block|,
name|S3_PUT_OBJECT
block|,
name|S3_DELETE_OBJECT
block|,
name|S3_ABORT_MULTIPART_UPLOAD
block|,       }
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * All DynamoDB operations: {@value}.    */
DECL|field|DDB_ALL_OPERATIONS
specifier|public
specifier|static
specifier|final
name|String
name|DDB_ALL_OPERATIONS
init|=
literal|"dynamodb:*"
decl_stmt|;
comment|/**    * Operations needed for DDB/S3Guard Admin.    * For now: make this {@link #DDB_ALL_OPERATIONS}.    */
DECL|field|DDB_ADMIN
specifier|public
specifier|static
specifier|final
name|String
name|DDB_ADMIN
init|=
name|DDB_ALL_OPERATIONS
decl_stmt|;
comment|/**    * Permission for DDB describeTable() operation: {@value}.    * This is used during initialization.    */
DECL|field|DDB_DESCRIBE_TABLE
specifier|public
specifier|static
specifier|final
name|String
name|DDB_DESCRIBE_TABLE
init|=
literal|"dynamodb:DescribeTable"
decl_stmt|;
comment|/**    * Permission to query the DDB table: {@value}.    */
DECL|field|DDB_QUERY
specifier|public
specifier|static
specifier|final
name|String
name|DDB_QUERY
init|=
literal|"dynamodb:Query"
decl_stmt|;
comment|/**    * Permission for DDB operation to get a record: {@value}.    */
DECL|field|DDB_GET_ITEM
specifier|public
specifier|static
specifier|final
name|String
name|DDB_GET_ITEM
init|=
literal|"dynamodb:GetItem"
decl_stmt|;
comment|/**    * Permission for DDB write record operation: {@value}.    */
DECL|field|DDB_PUT_ITEM
specifier|public
specifier|static
specifier|final
name|String
name|DDB_PUT_ITEM
init|=
literal|"dynamodb:PutItem"
decl_stmt|;
comment|/**    * Permission for DDB update single item operation: {@value}.    */
DECL|field|DDB_UPDATE_ITEM
specifier|public
specifier|static
specifier|final
name|String
name|DDB_UPDATE_ITEM
init|=
literal|"dynamodb:UpdateItem"
decl_stmt|;
comment|/**    * Permission for DDB delete operation: {@value}.    */
DECL|field|DDB_DELETE_ITEM
specifier|public
specifier|static
specifier|final
name|String
name|DDB_DELETE_ITEM
init|=
literal|"dynamodb:DeleteItem"
decl_stmt|;
comment|/**    * Permission for DDB operation: {@value}.    */
DECL|field|DDB_BATCH_GET_ITEM
specifier|public
specifier|static
specifier|final
name|String
name|DDB_BATCH_GET_ITEM
init|=
literal|"dynamodb:BatchGetItem"
decl_stmt|;
comment|/**    * Batch write permission for DDB: {@value}.    */
DECL|field|DDB_BATCH_WRITE_ITEM
specifier|public
specifier|static
specifier|final
name|String
name|DDB_BATCH_WRITE_ITEM
init|=
literal|"dynamodb:BatchWriteItem"
decl_stmt|;
comment|/**    * All DynamoDB tables: {@value}.    */
DECL|field|ALL_DDB_TABLES
specifier|public
specifier|static
specifier|final
name|String
name|ALL_DDB_TABLES
init|=
literal|"arn:aws:dynamodb:*"
decl_stmt|;
comment|/**    * Statement to allow all DDB access.    */
DECL|field|STATEMENT_ALL_DDB
specifier|public
specifier|static
specifier|final
name|Statement
name|STATEMENT_ALL_DDB
init|=
name|allowAllDynamoDBOperations
argument_list|(
name|ALL_DDB_TABLES
argument_list|)
decl_stmt|;
comment|/**    * Statement to allow all client operations needed for S3Guard,    * but none of the admin operations.    */
DECL|field|STATEMENT_S3GUARD_CLIENT
specifier|public
specifier|static
specifier|final
name|Statement
name|STATEMENT_S3GUARD_CLIENT
init|=
name|allowS3GuardClientOperations
argument_list|(
name|ALL_DDB_TABLES
argument_list|)
decl_stmt|;
comment|/**    * Allow all S3 Operations.    * This does not cover DDB or S3-KMS    */
DECL|field|STATEMENT_ALL_S3
specifier|public
specifier|static
specifier|final
name|Statement
name|STATEMENT_ALL_S3
init|=
name|statement
argument_list|(
literal|true
argument_list|,
name|S3_ALL_BUCKETS
argument_list|,
name|S3_ALL_OPERATIONS
argument_list|)
decl_stmt|;
comment|/**    * The s3:GetBucketLocation permission is for all buckets, not for    * any named bucket, which complicates permissions.    */
DECL|field|STATEMENT_ALL_S3_GET_BUCKET_LOCATION
specifier|public
specifier|static
specifier|final
name|Statement
name|STATEMENT_ALL_S3_GET_BUCKET_LOCATION
init|=
name|statement
argument_list|(
literal|true
argument_list|,
name|S3_ALL_BUCKETS
argument_list|,
name|S3_GET_BUCKET_LOCATION
argument_list|)
decl_stmt|;
comment|/**    * Policy for all S3 and S3Guard operations, and SSE-KMS.    */
DECL|field|ALLOW_S3_AND_SGUARD
specifier|public
specifier|static
specifier|final
name|Policy
name|ALLOW_S3_AND_SGUARD
init|=
name|policy
argument_list|(
name|STATEMENT_ALL_S3
argument_list|,
name|STATEMENT_ALL_DDB
argument_list|,
name|STATEMENT_ALLOW_SSE_KMS_RW
argument_list|,
name|STATEMENT_ALL_S3_GET_BUCKET_LOCATION
argument_list|)
decl_stmt|;
DECL|method|allowS3GuardClientOperations (String tableArn)
specifier|public
specifier|static
name|Statement
name|allowS3GuardClientOperations
parameter_list|(
name|String
name|tableArn
parameter_list|)
block|{
return|return
name|statement
argument_list|(
literal|true
argument_list|,
name|tableArn
argument_list|,
name|DDB_BATCH_GET_ITEM
argument_list|,
name|DDB_BATCH_WRITE_ITEM
argument_list|,
name|DDB_DELETE_ITEM
argument_list|,
name|DDB_DESCRIBE_TABLE
argument_list|,
name|DDB_GET_ITEM
argument_list|,
name|DDB_PUT_ITEM
argument_list|,
name|DDB_QUERY
argument_list|,
name|DDB_UPDATE_ITEM
argument_list|)
return|;
block|}
DECL|method|allowAllDynamoDBOperations (String tableArn)
specifier|public
specifier|static
name|Statement
name|allowAllDynamoDBOperations
parameter_list|(
name|String
name|tableArn
parameter_list|)
block|{
return|return
name|statement
argument_list|(
literal|true
argument_list|,
name|tableArn
argument_list|,
name|DDB_ALL_OPERATIONS
argument_list|)
return|;
block|}
comment|/**    * From an S3 bucket name, build an ARN to refer to it.    * @param bucket bucket name.    * @param write are write permissions required    * @return return statement granting access.    */
DECL|method|allowS3Operations (String bucket, boolean write)
specifier|public
specifier|static
name|List
argument_list|<
name|Statement
argument_list|>
name|allowS3Operations
parameter_list|(
name|String
name|bucket
parameter_list|,
name|boolean
name|write
parameter_list|)
block|{
comment|// add the bucket operations for the specific bucket ARN
name|ArrayList
argument_list|<
name|Statement
argument_list|>
name|statements
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|statement
argument_list|(
literal|true
argument_list|,
name|bucketToArn
argument_list|(
name|bucket
argument_list|)
argument_list|,
name|S3_GET_BUCKET_LOCATION
argument_list|,
name|S3_BUCKET_ALL_LIST
argument_list|)
argument_list|)
decl_stmt|;
comment|// then add the statements for objects in the buckets
if|if
condition|(
name|write
condition|)
block|{
name|statements
operator|.
name|add
argument_list|(
name|statement
argument_list|(
literal|true
argument_list|,
name|bucketObjectsToArn
argument_list|(
name|bucket
argument_list|)
argument_list|,
name|S3_ROOT_RW_OPERATIONS
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|statements
operator|.
name|add
argument_list|(
name|statement
argument_list|(
literal|true
argument_list|,
name|bucketObjectsToArn
argument_list|(
name|bucket
argument_list|)
argument_list|,
name|S3_ROOT_READ_OPERATIONS_LIST
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|statements
return|;
block|}
comment|/**    * From an S3 bucket name, build an ARN to refer to all objects in    * it.    * @param bucket bucket name.    * @return return the ARN to use in statements.    */
DECL|method|bucketObjectsToArn (String bucket)
specifier|public
specifier|static
name|String
name|bucketObjectsToArn
parameter_list|(
name|String
name|bucket
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"arn:aws:s3:::%s/*"
argument_list|,
name|bucket
argument_list|)
return|;
block|}
comment|/**    * From an S3 bucket name, build an ARN to refer to it.    * @param bucket bucket name.    * @return return the ARN to use in statements.    */
DECL|method|bucketToArn (String bucket)
specifier|public
specifier|static
name|String
name|bucketToArn
parameter_list|(
name|String
name|bucket
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"arn:aws:s3:::%s"
argument_list|,
name|bucket
argument_list|)
return|;
block|}
block|}
end_class

end_unit

