begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset
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
name|datanode
operator|.
name|fsdataset
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
name|OutputStream
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
name|util
operator|.
name|DataChecksum
import|;
end_import

begin_comment
comment|/**  * Contains the output streams for the data and checksum of a replica.  */
end_comment

begin_class
DECL|class|ReplicaOutputStreams
specifier|public
class|class
name|ReplicaOutputStreams
implements|implements
name|Closeable
block|{
DECL|field|dataOut
specifier|private
specifier|final
name|OutputStream
name|dataOut
decl_stmt|;
DECL|field|checksumOut
specifier|private
specifier|final
name|OutputStream
name|checksumOut
decl_stmt|;
DECL|field|checksum
specifier|private
specifier|final
name|DataChecksum
name|checksum
decl_stmt|;
comment|/**    * Create an object with a data output stream, a checksum output stream    * and a checksum.    */
DECL|method|ReplicaOutputStreams (OutputStream dataOut, OutputStream checksumOut, DataChecksum checksum)
specifier|public
name|ReplicaOutputStreams
parameter_list|(
name|OutputStream
name|dataOut
parameter_list|,
name|OutputStream
name|checksumOut
parameter_list|,
name|DataChecksum
name|checksum
parameter_list|)
block|{
name|this
operator|.
name|dataOut
operator|=
name|dataOut
expr_stmt|;
name|this
operator|.
name|checksumOut
operator|=
name|checksumOut
expr_stmt|;
name|this
operator|.
name|checksum
operator|=
name|checksum
expr_stmt|;
block|}
comment|/** @return the data output stream. */
DECL|method|getDataOut ()
specifier|public
name|OutputStream
name|getDataOut
parameter_list|()
block|{
return|return
name|dataOut
return|;
block|}
comment|/** @return the checksum output stream. */
DECL|method|getChecksumOut ()
specifier|public
name|OutputStream
name|getChecksumOut
parameter_list|()
block|{
return|return
name|checksumOut
return|;
block|}
comment|/** @return the checksum. */
DECL|method|getChecksum ()
specifier|public
name|DataChecksum
name|getChecksum
parameter_list|()
block|{
return|return
name|checksum
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|checksumOut
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

