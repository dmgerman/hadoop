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
name|FileDescriptor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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

begin_comment
comment|/**  * Contains the input streams for the data and checksum of a replica.  */
end_comment

begin_class
DECL|class|ReplicaInputStreams
specifier|public
class|class
name|ReplicaInputStreams
implements|implements
name|Closeable
block|{
DECL|field|dataIn
specifier|private
specifier|final
name|InputStream
name|dataIn
decl_stmt|;
DECL|field|checksumIn
specifier|private
specifier|final
name|InputStream
name|checksumIn
decl_stmt|;
comment|/** Create an object with a data input stream and a checksum input stream. */
DECL|method|ReplicaInputStreams (FileDescriptor dataFd, FileDescriptor checksumFd)
specifier|public
name|ReplicaInputStreams
parameter_list|(
name|FileDescriptor
name|dataFd
parameter_list|,
name|FileDescriptor
name|checksumFd
parameter_list|)
block|{
name|this
operator|.
name|dataIn
operator|=
operator|new
name|FileInputStream
argument_list|(
name|dataFd
argument_list|)
expr_stmt|;
name|this
operator|.
name|checksumIn
operator|=
operator|new
name|FileInputStream
argument_list|(
name|checksumFd
argument_list|)
expr_stmt|;
block|}
comment|/** @return the data input stream. */
DECL|method|getDataIn ()
specifier|public
name|InputStream
name|getDataIn
parameter_list|()
block|{
return|return
name|dataIn
return|;
block|}
comment|/** @return the checksum input stream. */
DECL|method|getChecksumIn ()
specifier|public
name|InputStream
name|getChecksumIn
parameter_list|()
block|{
return|return
name|checksumIn
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
name|dataIn
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|checksumIn
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

