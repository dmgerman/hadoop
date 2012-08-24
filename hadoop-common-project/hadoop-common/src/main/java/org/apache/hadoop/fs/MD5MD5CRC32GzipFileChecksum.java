begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|MD5Hash
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
name|util
operator|.
name|DataChecksum
import|;
end_import

begin_comment
comment|/** For CRC32 with the Gzip polynomial */
end_comment

begin_class
DECL|class|MD5MD5CRC32GzipFileChecksum
specifier|public
class|class
name|MD5MD5CRC32GzipFileChecksum
extends|extends
name|MD5MD5CRC32FileChecksum
block|{
comment|/** Same as this(0, 0, null) */
DECL|method|MD5MD5CRC32GzipFileChecksum ()
specifier|public
name|MD5MD5CRC32GzipFileChecksum
parameter_list|()
block|{
name|this
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Create a MD5FileChecksum */
DECL|method|MD5MD5CRC32GzipFileChecksum (int bytesPerCRC, long crcPerBlock, MD5Hash md5)
specifier|public
name|MD5MD5CRC32GzipFileChecksum
parameter_list|(
name|int
name|bytesPerCRC
parameter_list|,
name|long
name|crcPerBlock
parameter_list|,
name|MD5Hash
name|md5
parameter_list|)
block|{
name|super
argument_list|(
name|bytesPerCRC
argument_list|,
name|crcPerBlock
argument_list|,
name|md5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCrcType ()
specifier|public
name|DataChecksum
operator|.
name|Type
name|getCrcType
parameter_list|()
block|{
comment|// default to the one that is understood by all releases.
return|return
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32
return|;
block|}
block|}
end_class

end_unit

