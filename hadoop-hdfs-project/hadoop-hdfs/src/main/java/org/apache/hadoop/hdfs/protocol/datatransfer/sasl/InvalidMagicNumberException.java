begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol.datatransfer.sasl
package|package
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
name|datatransfer
operator|.
name|sasl
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
name|hdfs
operator|.
name|protocol
operator|.
name|datatransfer
operator|.
name|sasl
operator|.
name|DataTransferSaslUtil
operator|.
name|SASL_TRANSFER_MAGIC_NUMBER
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

begin_comment
comment|/**  * Indicates that SASL protocol negotiation expected to read a pre-defined magic  * number, but the expected value was not seen.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|InvalidMagicNumberException
specifier|public
class|class
name|InvalidMagicNumberException
extends|extends
name|IOException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|handshake4Encryption
specifier|private
specifier|final
name|boolean
name|handshake4Encryption
decl_stmt|;
comment|/**    * Creates a new InvalidMagicNumberException.    *    * @param magicNumber expected value    */
DECL|method|InvalidMagicNumberException (final int magicNumber, final boolean handshake4Encryption)
specifier|public
name|InvalidMagicNumberException
parameter_list|(
specifier|final
name|int
name|magicNumber
parameter_list|,
specifier|final
name|boolean
name|handshake4Encryption
parameter_list|)
block|{
name|super
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Received %x instead of %x from client."
argument_list|,
name|magicNumber
argument_list|,
name|SASL_TRANSFER_MAGIC_NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|handshake4Encryption
operator|=
name|handshake4Encryption
expr_stmt|;
block|}
comment|/**    * Return true if it's handshake for encryption    *     * @return boolean true if it's handshake for encryption    */
DECL|method|isHandshake4Encryption ()
specifier|public
name|boolean
name|isHandshake4Encryption
parameter_list|()
block|{
return|return
name|handshake4Encryption
return|;
block|}
block|}
end_class

end_unit

