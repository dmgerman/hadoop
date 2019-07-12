begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|Closeable
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

begin_comment
comment|/**  * This represents state which may be passed to bulk IO operations  * to enable them to store information about the state of the ongoing  * operation across invocations.  *<p>  * A bulk operation state<i>MUST</i> only be be used for the single store  * from which it was created, and<i>MUST</i>only for the duration of a single  * bulk update operation.  *<p>  * Passing in the state is to allow the stores to maintain state about  * updates they have already made to their store during this single operation:  * a cache of what has happened. It is not a list of operations to be applied.  * If a list of operations to perform is built up (e.g. during rename)  * that is the duty of the caller, not this state.  *<p>  * After the operation has completed, it<i>MUST</i> be closed so  * as to guarantee that all state is released.  */
end_comment

begin_class
DECL|class|BulkOperationState
specifier|public
class|class
name|BulkOperationState
implements|implements
name|Closeable
block|{
DECL|field|operation
specifier|private
specifier|final
name|OperationType
name|operation
decl_stmt|;
comment|/**    * Constructor.    * @param operation the type of the operation.    */
DECL|method|BulkOperationState (final OperationType operation)
specifier|public
name|BulkOperationState
parameter_list|(
specifier|final
name|OperationType
name|operation
parameter_list|)
block|{
name|this
operator|.
name|operation
operator|=
name|operation
expr_stmt|;
block|}
comment|/**    * Get the operation type.    * @return the operation type.    */
DECL|method|getOperation ()
specifier|public
name|OperationType
name|getOperation
parameter_list|()
block|{
return|return
name|operation
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{    }
comment|/**    * Enumeration of operations which can be performed in bulk.    * This can be used by the stores however they want.    * One special aspect: renames are to be done through a {@link RenameTracker}.    * Callers will be blocked from initiating a rename through    * {@code S3Guard#initiateBulkWrite()}    */
DECL|enum|OperationType
specifier|public
enum|enum
name|OperationType
block|{
comment|/** Writing data. */
DECL|enumConstant|Put
name|Put
block|,
comment|/** Rename: add and delete. */
DECL|enumConstant|Rename
name|Rename
block|,
comment|/** Pruning: deleting entries and updating parents. */
DECL|enumConstant|Prune
name|Prune
block|,
comment|/** Commit operation. */
DECL|enumConstant|Commit
name|Commit
block|,
comment|/** Deletion operation. */
DECL|enumConstant|Delete
name|Delete
block|,
comment|/** FSCK operation. */
DECL|enumConstant|Fsck
name|Fsck
block|}
block|}
end_class

end_unit

