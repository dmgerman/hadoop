begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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

begin_comment
comment|/**  * The public API for ReplicaAccessor objects.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|ReplicaAccessor
specifier|public
specifier|abstract
class|class
name|ReplicaAccessor
block|{
comment|/**    * Read bytes from the replica.    *    * @param pos    The position in the replica to start reading at.    *                 Must not be negative.    * @param buf    The byte array to read into.    * @param off    The offset within buf to start reading into.    * @param len    The maximum length to read.    *    * @return       The number of bytes read.  If the read extends past the end    *                  of the replica, a short read count will be returned.  We    *                  will should return -1 if EOF is reached and no bytes    *                  can be returned.  We will never return a short read    *                  count unless EOF is reached.    */
DECL|method|read (long pos, byte[] buf, int off, int len)
specifier|public
specifier|abstract
name|int
name|read
parameter_list|(
name|long
name|pos
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read bytes from the replica.    *    * @param pos    The position in the replica to start reading at.    *                 Must not be negative.    * @param buf    The byte buffer to read into.  The amount to read will be    *                 dictated by the remaining bytes between the current    *                 position and the limit.  The ByteBuffer may or may not be    *                 direct.    *    * @return       The number of bytes read.  If the read extends past the end    *                 of the replica, a short read count will be returned.  We    *                 should return -1 if EOF is reached and no bytes can be    *                 returned.  We will never return a short read count unless    *                 EOF is reached.    */
DECL|method|read (long pos, ByteBuffer buf)
specifier|public
specifier|abstract
name|int
name|read
parameter_list|(
name|long
name|pos
parameter_list|,
name|ByteBuffer
name|buf
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Release the resources associated with the ReplicaAccessor.    *    * It is recommended that implementations never throw an IOException.  The    * method is declared as throwing IOException in order to remain compatible    * with java.io.Closeable.  If an exception is thrown, the ReplicaAccessor    * must still be closed when the function returns in order to prevent a    * resource leak.    */
DECL|method|close ()
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Return true if bytes read via this accessor should count towards the    * local byte count statistics.    */
DECL|method|isLocal ()
specifier|public
specifier|abstract
name|boolean
name|isLocal
parameter_list|()
function_decl|;
comment|/**    * Return true if bytes read via this accessor should count towards the    * short-circuit byte count statistics.    */
DECL|method|isShortCircuit ()
specifier|public
specifier|abstract
name|boolean
name|isShortCircuit
parameter_list|()
function_decl|;
comment|/**    * Return the network distance between local machine and the remote machine.    */
DECL|method|getNetworkDistance ()
specifier|public
name|int
name|getNetworkDistance
parameter_list|()
block|{
return|return
name|isLocal
argument_list|()
condition|?
literal|0
else|:
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
block|}
end_class

end_unit

