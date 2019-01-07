begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
operator|.
name|io
operator|.
name|OzoneOutputStream
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmMultipartCommitUploadPartInfo
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_comment
comment|/**  * OzoneOutputStream stub for testing.  */
end_comment

begin_class
DECL|class|OzoneOutputStreamStub
specifier|public
class|class
name|OzoneOutputStreamStub
extends|extends
name|OzoneOutputStream
block|{
DECL|field|partName
specifier|private
specifier|final
name|String
name|partName
decl_stmt|;
comment|/**    * Constructs OzoneOutputStreamStub with outputStream and partName.    *    * @param outputStream    * @param name - partName    */
DECL|method|OzoneOutputStreamStub (OutputStream outputStream, String name)
specifier|public
name|OzoneOutputStreamStub
parameter_list|(
name|OutputStream
name|outputStream
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|outputStream
argument_list|)
expr_stmt|;
name|this
operator|.
name|partName
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (int b)
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|getOutputStream
argument_list|()
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (byte[] b, int off, int len)
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|getOutputStream
argument_list|()
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
specifier|synchronized
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|getOutputStream
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|//commitKey can be done here, if needed.
name|getOutputStream
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCommitUploadPartInfo ()
specifier|public
name|OmMultipartCommitUploadPartInfo
name|getCommitUploadPartInfo
parameter_list|()
block|{
return|return
operator|new
name|OmMultipartCommitUploadPartInfo
argument_list|(
name|partName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

