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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilterOutputStream
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
name|util
operator|.
name|DataChecksum
import|;
end_import

begin_comment
comment|/**  * A Checksum output stream.  * Checksum for the contents of the file is calculated and  * appended to the end of the file on close of the stream.  * Used for IFiles  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|IFileOutputStream
specifier|public
class|class
name|IFileOutputStream
extends|extends
name|FilterOutputStream
block|{
comment|/**    * The output stream to be checksummed.     */
DECL|field|sum
specifier|private
specifier|final
name|DataChecksum
name|sum
decl_stmt|;
DECL|field|barray
specifier|private
name|byte
index|[]
name|barray
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|field|finished
specifier|private
name|boolean
name|finished
init|=
literal|false
decl_stmt|;
comment|/**    * Create a checksum output stream that writes    * the bytes to the given stream.    * @param out    */
DECL|method|IFileOutputStream (OutputStream out)
specifier|public
name|IFileOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|)
block|{
name|super
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|sum
operator|=
name|DataChecksum
operator|.
name|newDataChecksum
argument_list|(
name|DataChecksum
operator|.
name|CHECKSUM_CRC32
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|barray
operator|=
operator|new
name|byte
index|[
name|sum
operator|.
name|getChecksumSize
argument_list|()
index|]
expr_stmt|;
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
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return;
block|}
name|closed
operator|=
literal|true
expr_stmt|;
name|finish
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Finishes writing data to the output stream, by writing    * the checksum bytes to the end. The underlying stream is not closed.    * @throws IOException    */
DECL|method|finish ()
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|finished
condition|)
block|{
return|return;
block|}
name|finished
operator|=
literal|true
expr_stmt|;
name|sum
operator|.
name|writeValue
argument_list|(
name|barray
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|barray
argument_list|,
literal|0
argument_list|,
name|sum
operator|.
name|getChecksumSize
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**    * Write bytes to the stream.    */
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
name|sum
operator|.
name|update
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|out
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
name|barray
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|b
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|barray
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

