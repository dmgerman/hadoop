begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|blob
operator|.
name|BlobRequestOptions
import|;
end_import

begin_comment
comment|/**  * Constants and helper methods for ASV's custom data format in page blobs.  */
end_comment

begin_class
DECL|class|PageBlobFormatHelpers
specifier|final
class|class
name|PageBlobFormatHelpers
block|{
DECL|field|PAGE_SIZE
specifier|public
specifier|static
specifier|final
name|short
name|PAGE_SIZE
init|=
literal|512
decl_stmt|;
DECL|field|PAGE_HEADER_SIZE
specifier|public
specifier|static
specifier|final
name|short
name|PAGE_HEADER_SIZE
init|=
literal|2
decl_stmt|;
DECL|field|PAGE_DATA_SIZE
specifier|public
specifier|static
specifier|final
name|short
name|PAGE_DATA_SIZE
init|=
name|PAGE_SIZE
operator|-
name|PAGE_HEADER_SIZE
decl_stmt|;
comment|// Hide constructor for utility class.
DECL|method|PageBlobFormatHelpers ()
specifier|private
name|PageBlobFormatHelpers
parameter_list|()
block|{        }
comment|/**    * Stores the given short as a two-byte array.    */
DECL|method|fromShort (short s)
specifier|public
specifier|static
name|byte
index|[]
name|fromShort
parameter_list|(
name|short
name|s
parameter_list|)
block|{
return|return
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|2
argument_list|)
operator|.
name|putShort
argument_list|(
name|s
argument_list|)
operator|.
name|array
argument_list|()
return|;
block|}
comment|/**    * Retrieves a short from the given two bytes.    */
DECL|method|toShort (byte firstByte, byte secondByte)
specifier|public
specifier|static
name|short
name|toShort
parameter_list|(
name|byte
name|firstByte
parameter_list|,
name|byte
name|secondByte
parameter_list|)
block|{
return|return
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
name|firstByte
block|,
name|secondByte
block|}
argument_list|)
operator|.
name|getShort
argument_list|()
return|;
block|}
DECL|method|withMD5Checking ()
specifier|public
specifier|static
name|BlobRequestOptions
name|withMD5Checking
parameter_list|()
block|{
name|BlobRequestOptions
name|options
init|=
operator|new
name|BlobRequestOptions
argument_list|()
decl_stmt|;
name|options
operator|.
name|setUseTransactionalContentMD5
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|options
return|;
block|}
block|}
end_class

end_unit

