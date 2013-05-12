begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|MapTask
operator|.
name|MAP_OUTPUT_INDEX_RECORD_LENGTH
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|LongBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|CheckedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|CheckedOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Checksum
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|ChecksumException
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
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|FileSystem
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
name|io
operator|.
name|IOUtils
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
name|SecureIOUtils
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
name|PureJavaCrc32
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|SpillRecord
specifier|public
class|class
name|SpillRecord
block|{
comment|/** Backing store */
DECL|field|buf
specifier|private
specifier|final
name|ByteBuffer
name|buf
decl_stmt|;
comment|/** View of backing storage as longs */
DECL|field|entries
specifier|private
specifier|final
name|LongBuffer
name|entries
decl_stmt|;
DECL|method|SpillRecord (int numPartitions)
specifier|public
name|SpillRecord
parameter_list|(
name|int
name|numPartitions
parameter_list|)
block|{
name|buf
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|numPartitions
operator|*
name|MapTask
operator|.
name|MAP_OUTPUT_INDEX_RECORD_LENGTH
argument_list|)
expr_stmt|;
name|entries
operator|=
name|buf
operator|.
name|asLongBuffer
argument_list|()
expr_stmt|;
block|}
DECL|method|SpillRecord (Path indexFileName, JobConf job)
specifier|public
name|SpillRecord
parameter_list|(
name|Path
name|indexFileName
parameter_list|,
name|JobConf
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|indexFileName
argument_list|,
name|job
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|SpillRecord (Path indexFileName, JobConf job, String expectedIndexOwner)
specifier|public
name|SpillRecord
parameter_list|(
name|Path
name|indexFileName
parameter_list|,
name|JobConf
name|job
parameter_list|,
name|String
name|expectedIndexOwner
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|indexFileName
argument_list|,
name|job
argument_list|,
operator|new
name|PureJavaCrc32
argument_list|()
argument_list|,
name|expectedIndexOwner
argument_list|)
expr_stmt|;
block|}
DECL|method|SpillRecord (Path indexFileName, JobConf job, Checksum crc, String expectedIndexOwner)
specifier|public
name|SpillRecord
parameter_list|(
name|Path
name|indexFileName
parameter_list|,
name|JobConf
name|job
parameter_list|,
name|Checksum
name|crc
parameter_list|,
name|String
name|expectedIndexOwner
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FileSystem
name|rfs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|job
argument_list|)
operator|.
name|getRaw
argument_list|()
decl_stmt|;
specifier|final
name|FSDataInputStream
name|in
init|=
name|SecureIOUtils
operator|.
name|openFSDataInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|indexFileName
operator|.
name|toUri
argument_list|()
operator|.
name|getRawPath
argument_list|()
argument_list|)
argument_list|,
name|expectedIndexOwner
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|long
name|length
init|=
name|rfs
operator|.
name|getFileStatus
argument_list|(
name|indexFileName
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
specifier|final
name|int
name|partitions
init|=
operator|(
name|int
operator|)
name|length
operator|/
name|MAP_OUTPUT_INDEX_RECORD_LENGTH
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|partitions
operator|*
name|MAP_OUTPUT_INDEX_RECORD_LENGTH
decl_stmt|;
name|buf
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|size
argument_list|)
expr_stmt|;
if|if
condition|(
name|crc
operator|!=
literal|null
condition|)
block|{
name|crc
operator|.
name|reset
argument_list|()
expr_stmt|;
name|CheckedInputStream
name|chk
init|=
operator|new
name|CheckedInputStream
argument_list|(
name|in
argument_list|,
name|crc
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|chk
argument_list|,
name|buf
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
if|if
condition|(
name|chk
operator|.
name|getChecksum
argument_list|()
operator|.
name|getValue
argument_list|()
operator|!=
name|in
operator|.
name|readLong
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ChecksumException
argument_list|(
literal|"Checksum error reading spill index: "
operator|+
name|indexFileName
argument_list|,
operator|-
literal|1
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|buf
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
name|entries
operator|=
name|buf
operator|.
name|asLongBuffer
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Return number of IndexRecord entries in this spill.    */
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|entries
operator|.
name|capacity
argument_list|()
operator|/
operator|(
name|MapTask
operator|.
name|MAP_OUTPUT_INDEX_RECORD_LENGTH
operator|/
literal|8
operator|)
return|;
block|}
comment|/**    * Get spill offsets for given partition.    */
DECL|method|getIndex (int partition)
specifier|public
name|IndexRecord
name|getIndex
parameter_list|(
name|int
name|partition
parameter_list|)
block|{
specifier|final
name|int
name|pos
init|=
name|partition
operator|*
name|MapTask
operator|.
name|MAP_OUTPUT_INDEX_RECORD_LENGTH
operator|/
literal|8
decl_stmt|;
return|return
operator|new
name|IndexRecord
argument_list|(
name|entries
operator|.
name|get
argument_list|(
name|pos
argument_list|)
argument_list|,
name|entries
operator|.
name|get
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
argument_list|,
name|entries
operator|.
name|get
argument_list|(
name|pos
operator|+
literal|2
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Set spill offsets for given partition.    */
DECL|method|putIndex (IndexRecord rec, int partition)
specifier|public
name|void
name|putIndex
parameter_list|(
name|IndexRecord
name|rec
parameter_list|,
name|int
name|partition
parameter_list|)
block|{
specifier|final
name|int
name|pos
init|=
name|partition
operator|*
name|MapTask
operator|.
name|MAP_OUTPUT_INDEX_RECORD_LENGTH
operator|/
literal|8
decl_stmt|;
name|entries
operator|.
name|put
argument_list|(
name|pos
argument_list|,
name|rec
operator|.
name|startOffset
argument_list|)
expr_stmt|;
name|entries
operator|.
name|put
argument_list|(
name|pos
operator|+
literal|1
argument_list|,
name|rec
operator|.
name|rawLength
argument_list|)
expr_stmt|;
name|entries
operator|.
name|put
argument_list|(
name|pos
operator|+
literal|2
argument_list|,
name|rec
operator|.
name|partLength
argument_list|)
expr_stmt|;
block|}
comment|/**    * Write this spill record to the location provided.    */
DECL|method|writeToFile (Path loc, JobConf job)
specifier|public
name|void
name|writeToFile
parameter_list|(
name|Path
name|loc
parameter_list|,
name|JobConf
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|writeToFile
argument_list|(
name|loc
argument_list|,
name|job
argument_list|,
operator|new
name|PureJavaCrc32
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|writeToFile (Path loc, JobConf job, Checksum crc)
specifier|public
name|void
name|writeToFile
parameter_list|(
name|Path
name|loc
parameter_list|,
name|JobConf
name|job
parameter_list|,
name|Checksum
name|crc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FileSystem
name|rfs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|job
argument_list|)
operator|.
name|getRaw
argument_list|()
decl_stmt|;
name|CheckedOutputStream
name|chk
init|=
literal|null
decl_stmt|;
specifier|final
name|FSDataOutputStream
name|out
init|=
name|rfs
operator|.
name|create
argument_list|(
name|loc
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|crc
operator|!=
literal|null
condition|)
block|{
name|crc
operator|.
name|reset
argument_list|()
expr_stmt|;
name|chk
operator|=
operator|new
name|CheckedOutputStream
argument_list|(
name|out
argument_list|,
name|crc
argument_list|)
expr_stmt|;
name|chk
operator|.
name|write
argument_list|(
name|buf
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|chk
operator|.
name|getChecksum
argument_list|()
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|write
argument_list|(
name|buf
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|chk
operator|!=
literal|null
condition|)
block|{
name|chk
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

