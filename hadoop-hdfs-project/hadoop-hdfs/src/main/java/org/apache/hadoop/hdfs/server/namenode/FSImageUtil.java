begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
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
name|conf
operator|.
name|Configuration
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
name|hdfs
operator|.
name|protocol
operator|.
name|LayoutVersion
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
name|hdfs
operator|.
name|protocol
operator|.
name|LayoutVersion
operator|.
name|Feature
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FSImageFormatProtobuf
operator|.
name|Loader
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FsImageProto
operator|.
name|FileSummary
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
name|io
operator|.
name|compress
operator|.
name|CompressionCodec
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FSImageUtil
specifier|public
specifier|final
class|class
name|FSImageUtil
block|{
DECL|field|MAGIC_HEADER
specifier|public
specifier|static
specifier|final
name|byte
index|[]
name|MAGIC_HEADER
init|=
literal|"HDFSIMG1"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
DECL|field|FILE_VERSION
specifier|public
specifier|static
specifier|final
name|int
name|FILE_VERSION
init|=
literal|1
decl_stmt|;
DECL|method|checkFileFormat (RandomAccessFile file)
specifier|public
specifier|static
name|boolean
name|checkFileFormat
parameter_list|(
name|RandomAccessFile
name|file
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|file
operator|.
name|length
argument_list|()
operator|<
name|Loader
operator|.
name|MINIMUM_FILE_LENGTH
condition|)
return|return
literal|false
return|;
name|byte
index|[]
name|magic
init|=
operator|new
name|byte
index|[
name|MAGIC_HEADER
operator|.
name|length
index|]
decl_stmt|;
name|file
operator|.
name|readFully
argument_list|(
name|magic
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|MAGIC_HEADER
argument_list|,
name|magic
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
DECL|method|loadSummary (RandomAccessFile file)
specifier|public
specifier|static
name|FileSummary
name|loadSummary
parameter_list|(
name|RandomAccessFile
name|file
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|FILE_LENGTH_FIELD_SIZE
init|=
literal|4
decl_stmt|;
name|long
name|fileLength
init|=
name|file
operator|.
name|length
argument_list|()
decl_stmt|;
name|file
operator|.
name|seek
argument_list|(
name|fileLength
operator|-
name|FILE_LENGTH_FIELD_SIZE
argument_list|)
expr_stmt|;
name|int
name|summaryLength
init|=
name|file
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|summaryLength
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Negative length of the file"
argument_list|)
throw|;
block|}
name|file
operator|.
name|seek
argument_list|(
name|fileLength
operator|-
name|FILE_LENGTH_FIELD_SIZE
operator|-
name|summaryLength
argument_list|)
expr_stmt|;
name|byte
index|[]
name|summaryBytes
init|=
operator|new
name|byte
index|[
name|summaryLength
index|]
decl_stmt|;
name|file
operator|.
name|readFully
argument_list|(
name|summaryBytes
argument_list|)
expr_stmt|;
name|FileSummary
name|summary
init|=
name|FileSummary
operator|.
name|parseDelimitedFrom
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|summaryBytes
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|summary
operator|.
name|getOndiskVersion
argument_list|()
operator|!=
name|FILE_VERSION
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unsupported file version "
operator|+
name|summary
operator|.
name|getOndiskVersion
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|NameNodeLayoutVersion
operator|.
name|supports
argument_list|(
name|Feature
operator|.
name|PROTOBUF_FORMAT
argument_list|,
name|summary
operator|.
name|getLayoutVersion
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unsupported layout version "
operator|+
name|summary
operator|.
name|getLayoutVersion
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|summary
return|;
block|}
DECL|method|wrapInputStreamForCompression ( Configuration conf, String codec, InputStream in)
specifier|public
specifier|static
name|InputStream
name|wrapInputStreamForCompression
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|codec
parameter_list|,
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|codec
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|in
return|;
name|FSImageCompression
name|compression
init|=
name|FSImageCompression
operator|.
name|createCompression
argument_list|(
name|conf
argument_list|,
name|codec
argument_list|)
decl_stmt|;
name|CompressionCodec
name|imageCodec
init|=
name|compression
operator|.
name|getImageCodec
argument_list|()
decl_stmt|;
return|return
name|imageCodec
operator|.
name|createInputStream
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
end_class

end_unit

