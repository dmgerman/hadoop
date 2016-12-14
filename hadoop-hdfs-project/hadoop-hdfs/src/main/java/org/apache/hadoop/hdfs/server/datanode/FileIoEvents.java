begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|FileIoProvider
operator|.
name|OPERATION
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
name|datanode
operator|.
name|fsdataset
operator|.
name|FsVolumeSpi
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * The following hooks can be implemented for instrumentation/fault  * injection.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|FileIoEvents
specifier|public
interface|interface
name|FileIoEvents
block|{
comment|/**    * Invoked before a filesystem metadata operation.    *    * @param volume  target volume for the operation. Null if unavailable.    * @param op  type of operation.    * @return  timestamp at which the operation was started. 0 if    *          unavailable.    */
DECL|method|beforeMetadataOp (@ullable FsVolumeSpi volume, OPERATION op)
name|long
name|beforeMetadataOp
parameter_list|(
annotation|@
name|Nullable
name|FsVolumeSpi
name|volume
parameter_list|,
name|OPERATION
name|op
parameter_list|)
function_decl|;
comment|/**    * Invoked after a filesystem metadata operation has completed.    *    * @param volume  target volume for the operation.  Null if unavailable.    * @param op  type of operation.    * @param begin  timestamp at which the operation was started. 0    *               if unavailable.    */
DECL|method|afterMetadataOp (@ullable FsVolumeSpi volume, OPERATION op, long begin)
name|void
name|afterMetadataOp
parameter_list|(
annotation|@
name|Nullable
name|FsVolumeSpi
name|volume
parameter_list|,
name|OPERATION
name|op
parameter_list|,
name|long
name|begin
parameter_list|)
function_decl|;
comment|/**    * Invoked before a read/write/flush/channel transfer operation.    *    * @param volume  target volume for the operation. Null if unavailable.    * @param op  type of operation.    * @param len  length of the file IO. 0 for flush.    * @return  timestamp at which the operation was started. 0 if    *          unavailable.    */
DECL|method|beforeFileIo (@ullable FsVolumeSpi volume, OPERATION op, long len)
name|long
name|beforeFileIo
parameter_list|(
annotation|@
name|Nullable
name|FsVolumeSpi
name|volume
parameter_list|,
name|OPERATION
name|op
parameter_list|,
name|long
name|len
parameter_list|)
function_decl|;
comment|/**    * Invoked after a read/write/flush/channel transfer operation    * has completed.    *    * @param volume  target volume for the operation. Null if unavailable.    * @param op  type of operation.    * @param len   of the file IO. 0 for flush.    * @return  timestamp at which the operation was started. 0 if    *          unavailable.    */
DECL|method|afterFileIo (@ullable FsVolumeSpi volume, OPERATION op, long begin, long len)
name|void
name|afterFileIo
parameter_list|(
annotation|@
name|Nullable
name|FsVolumeSpi
name|volume
parameter_list|,
name|OPERATION
name|op
parameter_list|,
name|long
name|begin
parameter_list|,
name|long
name|len
parameter_list|)
function_decl|;
comment|/**    * Invoked if an operation fails with an exception.    *  @param volume  target volume for the operation. Null if unavailable.    * @param op  type of operation.    * @param e  Exception encountered during the operation.    * @param begin  time at which the operation was started.    */
DECL|method|onFailure ( @ullable FsVolumeSpi volume, OPERATION op, Exception e, long begin)
name|void
name|onFailure
parameter_list|(
annotation|@
name|Nullable
name|FsVolumeSpi
name|volume
parameter_list|,
name|OPERATION
name|op
parameter_list|,
name|Exception
name|e
parameter_list|,
name|long
name|begin
parameter_list|)
function_decl|;
comment|/**    * Return statistics as a JSON string.    * @return    */
DECL|method|getStatistics ()
annotation|@
name|Nullable
name|String
name|getStatistics
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

