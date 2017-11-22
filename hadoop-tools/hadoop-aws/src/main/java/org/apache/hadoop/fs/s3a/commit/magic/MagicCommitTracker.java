begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit.magic
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
name|magic
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|util
operator|.
name|List
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
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|PutObjectRequest
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
name|lang
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
name|WriteOperationHelper
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
name|PutTracker
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
name|files
operator|.
name|SinglePendingCommit
import|;
end_import

begin_comment
comment|/**  * Put tracker for Magic commits.  *<p>Important</p>: must not directly or indirectly import a class which  * uses any datatype in hadoop-mapreduce.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|MagicCommitTracker
specifier|public
class|class
name|MagicCommitTracker
extends|extends
name|PutTracker
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MagicCommitTracker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|originalDestKey
specifier|private
specifier|final
name|String
name|originalDestKey
decl_stmt|;
DECL|field|pendingPartKey
specifier|private
specifier|final
name|String
name|pendingPartKey
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|WriteOperationHelper
name|writer
decl_stmt|;
DECL|field|bucket
specifier|private
specifier|final
name|String
name|bucket
decl_stmt|;
DECL|field|EMPTY
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|EMPTY
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
comment|/**    * Magic commit tracker.    * @param path path nominally being written to    * @param bucket dest bucket    * @param originalDestKey the original key, in the magic directory.    * @param destKey key for the destination    * @param pendingsetKey key of the pendingset file    * @param writer writer instance to use for operations    */
DECL|method|MagicCommitTracker (Path path, String bucket, String originalDestKey, String destKey, String pendingsetKey, WriteOperationHelper writer)
specifier|public
name|MagicCommitTracker
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
name|bucket
parameter_list|,
name|String
name|originalDestKey
parameter_list|,
name|String
name|destKey
parameter_list|,
name|String
name|pendingsetKey
parameter_list|,
name|WriteOperationHelper
name|writer
parameter_list|)
block|{
name|super
argument_list|(
name|destKey
argument_list|)
expr_stmt|;
name|this
operator|.
name|bucket
operator|=
name|bucket
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|originalDestKey
operator|=
name|originalDestKey
expr_stmt|;
name|this
operator|.
name|pendingPartKey
operator|=
name|pendingsetKey
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"File {} is written as magic file to path {}"
argument_list|,
name|path
argument_list|,
name|destKey
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initialize the tracker.    * @return true, indicating that the multipart commit must start.    * @throws IOException any IO problem.    */
annotation|@
name|Override
DECL|method|initialize ()
specifier|public
name|boolean
name|initialize
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Flag to indicate that output is not visible after the stream    * is closed.    * @return true    */
annotation|@
name|Override
DECL|method|outputImmediatelyVisible ()
specifier|public
name|boolean
name|outputImmediatelyVisible
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Complete operation: generate the final commit data, put it.    * @param uploadId Upload ID    * @param parts list of parts    * @param bytesWritten bytes written    * @return false, indicating that the commit must fail.    * @throws IOException any IO problem.    * @throws IllegalArgumentException bad argument    */
annotation|@
name|Override
DECL|method|aboutToComplete (String uploadId, List<PartETag> parts, long bytesWritten)
specifier|public
name|boolean
name|aboutToComplete
parameter_list|(
name|String
name|uploadId
parameter_list|,
name|List
argument_list|<
name|PartETag
argument_list|>
name|parts
parameter_list|,
name|long
name|bytesWritten
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|uploadId
argument_list|)
argument_list|,
literal|"empty/null upload ID: "
operator|+
name|uploadId
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|parts
operator|!=
literal|null
argument_list|,
literal|"No uploaded parts list"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|parts
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"No uploaded parts to save"
argument_list|)
expr_stmt|;
name|SinglePendingCommit
name|commitData
init|=
operator|new
name|SinglePendingCommit
argument_list|()
decl_stmt|;
name|commitData
operator|.
name|touch
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|commitData
operator|.
name|setDestinationKey
argument_list|(
name|getDestKey
argument_list|()
argument_list|)
expr_stmt|;
name|commitData
operator|.
name|setBucket
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
name|commitData
operator|.
name|setUri
argument_list|(
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|commitData
operator|.
name|setUploadId
argument_list|(
name|uploadId
argument_list|)
expr_stmt|;
name|commitData
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|commitData
operator|.
name|setLength
argument_list|(
name|bytesWritten
argument_list|)
expr_stmt|;
name|commitData
operator|.
name|bindCommitData
argument_list|(
name|parts
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|commitData
operator|.
name|toBytes
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Uncommitted data pending to file {};"
operator|+
literal|" commit metadata for {} parts in {}. sixe: {} byte(s)"
argument_list|,
name|path
operator|.
name|toUri
argument_list|()
argument_list|,
name|parts
operator|.
name|size
argument_list|()
argument_list|,
name|pendingPartKey
argument_list|,
name|bytesWritten
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Closed MPU to {}, saved commit information to {}; data=:\n{}"
argument_list|,
name|path
argument_list|,
name|pendingPartKey
argument_list|,
name|commitData
argument_list|)
expr_stmt|;
name|PutObjectRequest
name|put
init|=
name|writer
operator|.
name|createPutObjectRequest
argument_list|(
name|pendingPartKey
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
decl_stmt|;
name|writer
operator|.
name|uploadObject
argument_list|(
name|put
argument_list|)
expr_stmt|;
comment|// now put a 0-byte file with the name of the original under-magic path
name|PutObjectRequest
name|originalDestPut
init|=
name|writer
operator|.
name|createPutObjectRequest
argument_list|(
name|originalDestKey
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|EMPTY
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|writer
operator|.
name|uploadObject
argument_list|(
name|originalDestPut
argument_list|)
expr_stmt|;
return|return
literal|false
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
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"MagicCommitTracker{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", destKey="
argument_list|)
operator|.
name|append
argument_list|(
name|getDestKey
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", pendingPartKey='"
argument_list|)
operator|.
name|append
argument_list|(
name|pendingPartKey
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
literal|", path="
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", writer="
argument_list|)
operator|.
name|append
argument_list|(
name|writer
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

