begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_comment
comment|/**  * Standard strings to use in exception messages in filesystems  * HDFS is used as the reference source of the strings  */
end_comment

begin_class
DECL|class|FSExceptionMessages
specifier|public
class|class
name|FSExceptionMessages
block|{
comment|/**    * The operation failed because the stream is closed: {@value}    */
DECL|field|STREAM_IS_CLOSED
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_IS_CLOSED
init|=
literal|"Stream is closed!"
decl_stmt|;
comment|/**    * Negative offset seek forbidden : {@value}    */
DECL|field|NEGATIVE_SEEK
specifier|public
specifier|static
specifier|final
name|String
name|NEGATIVE_SEEK
init|=
literal|"Cannot seek to a negative offset"
decl_stmt|;
comment|/**    * Seeks : {@value}    */
DECL|field|CANNOT_SEEK_PAST_EOF
specifier|public
specifier|static
specifier|final
name|String
name|CANNOT_SEEK_PAST_EOF
init|=
literal|"Attempted to seek or read past the end of the file"
decl_stmt|;
DECL|field|EOF_IN_READ_FULLY
specifier|public
specifier|static
specifier|final
name|String
name|EOF_IN_READ_FULLY
init|=
literal|"End of file reached before reading fully."
decl_stmt|;
DECL|field|TOO_MANY_BYTES_FOR_DEST_BUFFER
specifier|public
specifier|static
specifier|final
name|String
name|TOO_MANY_BYTES_FOR_DEST_BUFFER
init|=
literal|"Requested more bytes than destination buffer size"
decl_stmt|;
DECL|field|PERMISSION_DENIED
specifier|public
specifier|static
specifier|final
name|String
name|PERMISSION_DENIED
init|=
literal|"Permission denied"
decl_stmt|;
DECL|field|PERMISSION_DENIED_BY_STICKY_BIT
specifier|public
specifier|static
specifier|final
name|String
name|PERMISSION_DENIED_BY_STICKY_BIT
init|=
literal|"Permission denied by sticky bit"
decl_stmt|;
block|}
end_class

end_unit

