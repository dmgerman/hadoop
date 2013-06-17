begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.nfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|nfs
package|;
end_package

begin_comment
comment|/**  * Class encapsulates different types of files  */
end_comment

begin_enum
DECL|enum|NfsFileType
specifier|public
enum|enum
name|NfsFileType
block|{
DECL|enumConstant|NFSREG
name|NFSREG
argument_list|(
literal|1
argument_list|)
block|,
comment|// a regular file
DECL|enumConstant|NFSDIR
name|NFSDIR
argument_list|(
literal|2
argument_list|)
block|,
comment|// a directory
DECL|enumConstant|NFSBLK
name|NFSBLK
argument_list|(
literal|3
argument_list|)
block|,
comment|// a block special device file
DECL|enumConstant|NFSCHR
name|NFSCHR
argument_list|(
literal|4
argument_list|)
block|,
comment|// a character special device
DECL|enumConstant|NFSLNK
name|NFSLNK
argument_list|(
literal|5
argument_list|)
block|,
comment|// a symbolic link
DECL|enumConstant|NFSSOCK
name|NFSSOCK
argument_list|(
literal|6
argument_list|)
block|,
comment|// a socket
DECL|enumConstant|NFSFIFO
name|NFSFIFO
argument_list|(
literal|7
argument_list|)
block|;
comment|// a named pipe
DECL|field|value
specifier|private
specifier|final
name|int
name|value
decl_stmt|;
DECL|method|NfsFileType (int val)
name|NfsFileType
parameter_list|(
name|int
name|val
parameter_list|)
block|{
name|value
operator|=
name|val
expr_stmt|;
block|}
DECL|method|toValue ()
specifier|public
name|int
name|toValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
end_enum

end_unit

