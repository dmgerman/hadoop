begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.protocol
package|package
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
name|protocol
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

begin_comment
comment|/**  * Protocol to talk to OM HA. These methods are needed only called from  * OmRequestHandler.  */
end_comment

begin_interface
DECL|interface|OzoneManagerHAProtocol
specifier|public
interface|interface
name|OzoneManagerHAProtocol
block|{
comment|/**    * Store the snapshot index i.e. the raft log index, corresponding to the    * last transaction applied to the OM RocksDB, in OM metadata dir on disk.    * @param flush flush the OM DB to disk if true    * @return the snapshot index    * @throws IOException    */
DECL|method|saveRatisSnapshot (boolean flush)
name|long
name|saveRatisSnapshot
parameter_list|(
name|boolean
name|flush
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

