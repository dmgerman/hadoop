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

begin_comment
comment|/**  * Statistic which are collected in S3A.  * These statistics are available at a low level in {@link S3AStorageStatistics}  * and as metrics in {@link S3AInstrumentation}  */
end_comment

begin_enum
DECL|enum|Statistic
specifier|public
enum|enum
name|Statistic
block|{
DECL|enumConstant|DIRECTORIES_CREATED
name|DIRECTORIES_CREATED
argument_list|(
literal|"directories_created"
argument_list|,
literal|"Total number of directories created through the object store."
argument_list|)
block|,
DECL|enumConstant|DIRECTORIES_DELETED
name|DIRECTORIES_DELETED
argument_list|(
literal|"directories_deleted"
argument_list|,
literal|"Total number of directories deleted through the object store."
argument_list|)
block|,
DECL|enumConstant|FILES_COPIED
name|FILES_COPIED
argument_list|(
literal|"files_copied"
argument_list|,
literal|"Total number of files copied within the object store."
argument_list|)
block|,
DECL|enumConstant|FILES_COPIED_BYTES
name|FILES_COPIED_BYTES
argument_list|(
literal|"files_copied_bytes"
argument_list|,
literal|"Total number of bytes copied within the object store."
argument_list|)
block|,
DECL|enumConstant|FILES_CREATED
name|FILES_CREATED
argument_list|(
literal|"files_created"
argument_list|,
literal|"Total number of files created through the object store."
argument_list|)
block|,
DECL|enumConstant|FILES_DELETED
name|FILES_DELETED
argument_list|(
literal|"files_deleted"
argument_list|,
literal|"Total number of files deleted from the object store."
argument_list|)
block|,
DECL|enumConstant|IGNORED_ERRORS
name|IGNORED_ERRORS
argument_list|(
literal|"ignored_errors"
argument_list|,
literal|"Errors caught and ignored"
argument_list|)
block|,
DECL|enumConstant|INVOCATION_COPY_FROM_LOCAL_FILE
name|INVOCATION_COPY_FROM_LOCAL_FILE
argument_list|(
literal|"invocations_copyfromlocalfile"
argument_list|,
literal|"Calls of copyFromLocalFile()"
argument_list|)
block|,
DECL|enumConstant|INVOCATION_EXISTS
name|INVOCATION_EXISTS
argument_list|(
literal|"invocations_exists"
argument_list|,
literal|"Calls of exists()"
argument_list|)
block|,
DECL|enumConstant|INVOCATION_GET_FILE_STATUS
name|INVOCATION_GET_FILE_STATUS
argument_list|(
literal|"invocations_getfilestatus"
argument_list|,
literal|"Calls of getFileStatus()"
argument_list|)
block|,
DECL|enumConstant|INVOCATION_GLOB_STATUS
name|INVOCATION_GLOB_STATUS
argument_list|(
literal|"invocations_globstatus"
argument_list|,
literal|"Calls of globStatus()"
argument_list|)
block|,
DECL|enumConstant|INVOCATION_IS_DIRECTORY
name|INVOCATION_IS_DIRECTORY
argument_list|(
literal|"invocations_is_directory"
argument_list|,
literal|"Calls of isDirectory()"
argument_list|)
block|,
DECL|enumConstant|INVOCATION_IS_FILE
name|INVOCATION_IS_FILE
argument_list|(
literal|"invocations_is_file"
argument_list|,
literal|"Calls of isFile()"
argument_list|)
block|,
DECL|enumConstant|INVOCATION_LIST_FILES
name|INVOCATION_LIST_FILES
argument_list|(
literal|"invocations_listfiles"
argument_list|,
literal|"Calls of listFiles()"
argument_list|)
block|,
DECL|enumConstant|INVOCATION_LIST_LOCATED_STATUS
name|INVOCATION_LIST_LOCATED_STATUS
argument_list|(
literal|"invocations_listlocatedstatus"
argument_list|,
literal|"Calls of listLocatedStatus()"
argument_list|)
block|,
DECL|enumConstant|INVOCATION_LIST_STATUS
name|INVOCATION_LIST_STATUS
argument_list|(
literal|"invocations_liststatus"
argument_list|,
literal|"Calls of listStatus()"
argument_list|)
block|,
DECL|enumConstant|INVOCATION_MKDIRS
name|INVOCATION_MKDIRS
argument_list|(
literal|"invocations_mdkirs"
argument_list|,
literal|"Calls of mkdirs()"
argument_list|)
block|,
DECL|enumConstant|INVOCATION_RENAME
name|INVOCATION_RENAME
argument_list|(
literal|"invocations_rename"
argument_list|,
literal|"Calls of rename()"
argument_list|)
block|,
DECL|enumConstant|OBJECT_COPY_REQUESTS
name|OBJECT_COPY_REQUESTS
argument_list|(
literal|"object_copy_requests"
argument_list|,
literal|"Object copy requests"
argument_list|)
block|,
DECL|enumConstant|OBJECT_DELETE_REQUESTS
name|OBJECT_DELETE_REQUESTS
argument_list|(
literal|"object_delete_requests"
argument_list|,
literal|"Object delete requests"
argument_list|)
block|,
DECL|enumConstant|OBJECT_LIST_REQUESTS
name|OBJECT_LIST_REQUESTS
argument_list|(
literal|"object_list_requests"
argument_list|,
literal|"Number of object listings made"
argument_list|)
block|,
DECL|enumConstant|OBJECT_METADATA_REQUESTS
name|OBJECT_METADATA_REQUESTS
argument_list|(
literal|"object_metadata_requests"
argument_list|,
literal|"Number of requests for object metadata"
argument_list|)
block|,
DECL|enumConstant|OBJECT_MULTIPART_UPLOAD_ABORTED
name|OBJECT_MULTIPART_UPLOAD_ABORTED
argument_list|(
literal|"object_multipart_aborted"
argument_list|,
literal|"Object multipart upload aborted"
argument_list|)
block|,
DECL|enumConstant|OBJECT_PUT_REQUESTS
name|OBJECT_PUT_REQUESTS
argument_list|(
literal|"object_put_requests"
argument_list|,
literal|"Object put/multipart upload count"
argument_list|)
block|,
DECL|enumConstant|OBJECT_PUT_BYTES
name|OBJECT_PUT_BYTES
argument_list|(
literal|"object_put_bytes"
argument_list|,
literal|"number of bytes uploaded"
argument_list|)
block|,
DECL|enumConstant|STREAM_ABORTED
name|STREAM_ABORTED
argument_list|(
literal|"stream_aborted"
argument_list|,
literal|"Count of times the TCP stream was aborted"
argument_list|)
block|,
DECL|enumConstant|STREAM_BACKWARD_SEEK_OPERATIONS
name|STREAM_BACKWARD_SEEK_OPERATIONS
argument_list|(
literal|"stream_backward_seek_pperations"
argument_list|,
literal|"Number of executed seek operations which went backwards in a stream"
argument_list|)
block|,
DECL|enumConstant|STREAM_CLOSED
name|STREAM_CLOSED
argument_list|(
literal|"streamClosed"
argument_list|,
literal|"Count of times the TCP stream was closed"
argument_list|)
block|,
DECL|enumConstant|STREAM_CLOSE_OPERATIONS
name|STREAM_CLOSE_OPERATIONS
argument_list|(
literal|"stream_close_operations"
argument_list|,
literal|"Total count of times an attempt to close a data stream was made"
argument_list|)
block|,
DECL|enumConstant|STREAM_FORWARD_SEEK_OPERATIONS
name|STREAM_FORWARD_SEEK_OPERATIONS
argument_list|(
literal|"stream_forward_seek_operations"
argument_list|,
literal|"Number of executed seek operations which went forward in a stream"
argument_list|)
block|,
DECL|enumConstant|STREAM_OPENED
name|STREAM_OPENED
argument_list|(
literal|"streamOpened"
argument_list|,
literal|"Total count of times an input stream to object store was opened"
argument_list|)
block|,
DECL|enumConstant|STREAM_READ_EXCEPTIONS
name|STREAM_READ_EXCEPTIONS
argument_list|(
literal|"stream_read_exceptions"
argument_list|,
literal|"Number of seek operations invoked on input streams"
argument_list|)
block|,
DECL|enumConstant|STREAM_READ_FULLY_OPERATIONS
name|STREAM_READ_FULLY_OPERATIONS
argument_list|(
literal|"stream_read_fully_operations"
argument_list|,
literal|"Count of readFully() operations in streams"
argument_list|)
block|,
DECL|enumConstant|STREAM_READ_OPERATIONS
name|STREAM_READ_OPERATIONS
argument_list|(
literal|"stream_read_operations"
argument_list|,
literal|"Count of read() operations in streams"
argument_list|)
block|,
DECL|enumConstant|STREAM_READ_OPERATIONS_INCOMPLETE
name|STREAM_READ_OPERATIONS_INCOMPLETE
argument_list|(
literal|"stream_read_operations_incomplete"
argument_list|,
literal|"Count of incomplete read() operations in streams"
argument_list|)
block|,
DECL|enumConstant|STREAM_SEEK_BYTES_BACKWARDS
name|STREAM_SEEK_BYTES_BACKWARDS
argument_list|(
literal|"stream_bytes_backwards_on_seek"
argument_list|,
literal|"Count of bytes moved backwards during seek operations"
argument_list|)
block|,
DECL|enumConstant|STREAM_SEEK_BYTES_READ
name|STREAM_SEEK_BYTES_READ
argument_list|(
literal|"stream_bytes_read"
argument_list|,
literal|"Count of bytes read during seek() in stream operations"
argument_list|)
block|,
DECL|enumConstant|STREAM_SEEK_BYTES_SKIPPED
name|STREAM_SEEK_BYTES_SKIPPED
argument_list|(
literal|"stream_bytes_skipped_on_seek"
argument_list|,
literal|"Count of bytes skipped during forward seek operation"
argument_list|)
block|,
DECL|enumConstant|STREAM_SEEK_OPERATIONS
name|STREAM_SEEK_OPERATIONS
argument_list|(
literal|"stream_seek_operations"
argument_list|,
literal|"Number of seek operations during stream IO."
argument_list|)
block|,
DECL|enumConstant|STREAM_CLOSE_BYTES_READ
name|STREAM_CLOSE_BYTES_READ
argument_list|(
literal|"stream_bytes_read_in_close"
argument_list|,
literal|"Count of bytes read when closing streams during seek operations."
argument_list|)
block|,
DECL|enumConstant|STREAM_ABORT_BYTES_DISCARDED
name|STREAM_ABORT_BYTES_DISCARDED
argument_list|(
literal|"stream_bytes_discarded_in_abort"
argument_list|,
literal|"Count of bytes discarded by aborting the stream"
argument_list|)
block|;
DECL|method|Statistic (String symbol, String description)
name|Statistic
parameter_list|(
name|String
name|symbol
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|symbol
operator|=
name|symbol
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
DECL|field|symbol
specifier|private
specifier|final
name|String
name|symbol
decl_stmt|;
DECL|field|description
specifier|private
specifier|final
name|String
name|description
decl_stmt|;
DECL|method|getSymbol ()
specifier|public
name|String
name|getSymbol
parameter_list|()
block|{
return|return
name|symbol
return|;
block|}
comment|/**    * Get a statistic from a symbol.    * @param symbol statistic to look up    * @return the value or null.    */
DECL|method|fromSymbol (String symbol)
specifier|public
specifier|static
name|Statistic
name|fromSymbol
parameter_list|(
name|String
name|symbol
parameter_list|)
block|{
if|if
condition|(
name|symbol
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Statistic
name|opType
range|:
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|opType
operator|.
name|getSymbol
argument_list|()
operator|.
name|equals
argument_list|(
name|symbol
argument_list|)
condition|)
block|{
return|return
name|opType
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
comment|/**    * The string value is simply the symbol.    * This makes this operation very low cost.    * @return the symbol of this statistic.    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|symbol
return|;
block|}
block|}
end_enum

end_unit

